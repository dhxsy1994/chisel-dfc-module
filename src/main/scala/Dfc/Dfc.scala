
package Dfc


import chisel3._
import chisel3.util._
import chisel3.internal.firrtl.Width
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

//A table
class A_Meta extends Bundle {
  val inputLink = UInt(8.W)
  val pId = UInt(16.W)
}

class A_counterPart extends Module {
  //counter 部件

  val io = IO(new Bundle() {
    val countDownEn = Input(Bool())
    val dIn = Input(UInt(8.W))
    val load = Input(Bool())

    val operationAddr = Input(UInt(6.W))
    val interruptSignal = Output(Bool())
  })

  val Meta = Mem(64, UInt(8.W))
  val next = WireInit(0.U)

  when(io.load === true.B){
    Meta(io.operationAddr) := io.dIn
  }.elsewhen(io.load === false.B && io.countDownEn === true.B && Meta(io.operationAddr) > 0.U){
    next := Meta.read(io.operationAddr) - 1.U
    Meta.write(io.operationAddr, next)
  }

  //next := Meta.read(io.operationAddr)
  io.interruptSignal := false.B

  when(Meta(io.operationAddr) === 0.U){
    io.interruptSignal := true.B
  }

  printf("Meta(%d) = %d\n", io.operationAddr, Meta(io.operationAddr))
  printf("interruptSignal = %d\n", io.interruptSignal)
}


//Table A
class dfc_A extends Module {
  val io = IO(new Bundle() {
    val wEn = Input(Bool())
    val wData = Input(UInt(32.W)) //写
    val wAddr = Input(UInt(6.W))

    val rAddr = Input(UInt(6.W)) //读
    val rData = Output(UInt(16.W))

    val counterDownAddr = Input(UInt(6.W)) // 计数器-1信号位置
    val exceptionPost = Output(Bool()) //中断控制 how to imp？
  })

  // 共64行
  // 0 is empty line
  val Metamem = SyncReadMem(64, new A_Meta)

  //import chisel3._
  val counterPart = new A_counterPart

  val valid = RegInit(0.U(64.W))

  val addr_wire = Reg(UInt(6.W))
  val data_wire = Reg(UInt(32.W))
  val countDownAddr_wire = Wire(UInt(6.W))
  //write Meta info
  val wMeta = Wire(new A_Meta)


  //write
  when(io.wEn === true.B && io.wData.orR() === true.B){
    addr_wire := io.wAddr
    data_wire := io.wData

    val addr_lit = addr_wire.litValue()//can not run

    val wCount = data_wire(31,24)
    val winputLink = data_wire(23,16)
    val wpId = data_wire(15,0)

    //Meta input
    wMeta.inputLink := winputLink
    wMeta.pId := wpId
    Metamem.write(addr_wire, wMeta)

    //Counter input


    Counters(addr_lit.intValue()).io.load := true.B
    Counters(addr_lit.intValue()).io.dIn := wCount

    valid(io.wAddr) := 1.U
  }

  //read pId to rData
  when(io.rAddr.orR()){
    io.rData := Metamem(io.rAddr).pId
    // 其他访问方式
    // val data = Metamem.read(io.rAddr).pId
  }

  //recive countDown signal
  when(io.counterDownAddr.orR()){
    countDownAddr_wire := io.counterDownAddr
    val countDownAddr_lit = countDownAddr_wire.litValue()
    Counters(countDownAddr_lit.intValue()).io.countDownEn := true.B
  }

  //interrupt 64 bit
  val interruptBit = 0.U(64.W)
  for( i <- 0 to 64){
    interruptBit(i) := Counters(i).io.interruptSignal
  }

  when(interruptBit.orR()){
    io.exceptionPost := true.B
  }

  for(i <- 1 until 63){
    printf("Metamem.pId = %d\n", Metamem(i).pId)
    printf("Metamem.inputLink = %d\n", Metamem(i).inputLink)
  }

}



