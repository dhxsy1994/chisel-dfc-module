
package Dfc


import chisel3._
import chisel3.util._
import chisel3.internal.firrtl.Width
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

//Parametes global define

//A table Meta
class A_Meta extends Bundle {
  val inputLink = UInt(8.W)
  val pId = UInt(16.W)
}

class A_counterPart extends Module {
  //counter 部件
  val io = IO(new Bundle() {
    val operationAddr = Input(UInt(6.W))

    val dIn = Input(UInt(8.W))
    val load = Input(Bool())

    val countDownEn = Input(Bool())
    val interruptSignal = Output(Bool())
  })

  //internal Meta
  val counterMeta = Mem(64, UInt(8.W))
  //valid Reg
  val valid = RegInit(0.U(64.W))

  //buffer input regs
  val lastAddr = RegInit(0.U)
  lastAddr := io.operationAddr
  val lastcountDownEn = RegInit(false.B)
  lastcountDownEn := io.countDownEn
  val lastload = RegInit(false.B)
  lastload := io.load
  val lastdIn = RegInit(0.U)
  lastdIn := io.dIn

  //next init
  val next = WireInit(0.U)
  //current Count
  val currentCount = Wire(UInt())

  val stimulate = RegInit(0.U)

  val equalZero = WireInit(false.B)

  //TODO: Verifying interrupt logic!!!!
  when(lastload && !valid(lastAddr)){
    //load condition
    counterMeta(lastAddr) := lastdIn
    valid := valid.bitSet(lastAddr, true.B)
  }.elsewhen(lastload && valid(lastAddr)){
    printf("Failed signal, load = %d, valid = %d\n", io.load, valid(lastAddr))
  }

  when(lastcountDownEn && valid(lastAddr)){
    next := currentCount - 1.U
    counterMeta.write(lastAddr, next)
  }.elsewhen(lastcountDownEn && !valid(lastAddr)){
    printf("Failed signal, countDownEn = %d, valid = %d\n", io.countDownEn, valid(lastAddr))
  }.otherwise {
    next := 0.U
  }

  currentCount := counterMeta.read(lastAddr)

  when(valid(lastAddr)){
    equalZero := currentCount === 0.U
  }.otherwise{
    equalZero := false.B
  }

  when(equalZero) {
    printf("stimulate up\n")
    stimulate := 1.U
  }.otherwise{
    stimulate := 0.U
  }

  //TODO: clean operation not correct
  when(stimulate === 1.U){
    stimulate := 0.U
//    valid.bitSet(io.operationAddr, false.B)
//    counterMeta.write(io.operationAddr, 255.U)
    valid.bitSet(lastAddr, false.B)
    counterMeta.write(lastAddr, 255.U)
  }

  io.interruptSignal := stimulate

  printf("counterMeta(%d) = %d\n", io.operationAddr, counterMeta(lastAddr))
  printf("counterPart.interruptSignal = %d\n", io.interruptSignal)
  //printf("next = %d\n", next)
  printf("valid(%d) = %d\n", lastAddr,valid(lastAddr))
}

/*---------------------------------------------------------------------*/
//Separated dfc_AIO imp

class dfc_AIO extends Bundle {
  val wEn = Input(Bool())
  val wData = Input(UInt(32.W))

  //addr wire merge
  val opAddr = Input(UInt(6.W))
  //val rAddr = Input(UInt(6.W))
  val rData = Output(UInt(16.W))

  val counterDownEn = Input(Bool())
  //specificlly addr?
  val counterDownAddr = Input(UInt(6.W))

  val interruptPost = Output(Bool())
  //TODO: control
}

//Table A
class dfc_A extends Module {

  val io = IO(new dfc_AIO)

  //IO Ports output init, if not, report not fully initialized error
  io.rData := 0.U
  //io.counterDownEn := false.B
  io.interruptPost := false.B

  //64 Meta lines
  val Metamem = Mem(64, new A_Meta)
  val valid = RegInit(0.U(64.W))
  //A counter Part
  val counterPart = Module(new A_counterPart)

  val addr_wire = Wire(UInt(6.W))
  val data_wire = Wire(UInt(32.W))
  val counterPartInterrupt_wire = Wire(UInt())

  //counterPart IO init
  counterPart.io.load := io.wEn //link with io.wEn
  counterPart.io.countDownEn := io.counterDownEn //link with io.counterDownRn
  counterPart.io.dIn := 0.U

  //counterPart opAddr transfer
  counterPart.io.operationAddr := io.opAddr

  //counterPart interruptSignal
  counterPartInterrupt_wire := counterPart.io.interruptSignal

  //wirte addr & data wire
  addr_wire := io.opAddr
  data_wire := io.wData

  val wCount = data_wire(31,24)
  val winputLink = data_wire(23,16)
  val wpId = data_wire(15,0)

  //write Meta info init, if not, report not fully initialzled error
  val wMeta = Wire(new A_Meta)
  wMeta.inputLink := winputLink
  wMeta.pId := wpId

  //write A table
  when(io.wEn === true.B && io.wData.orR() === true.B && valid(io.opAddr) === false.B) {
    //write Meta
    Metamem.write(addr_wire, wMeta)

    //write Counter. need load, dIn, operationAddr
    counterPart.io.load := true.B
    counterPart.io.dIn := wCount
    //counterPart.io.operationAddr := addr_wire

    //valid bit
    valid := valid.bitSet(addr_wire, true.B)
  }

  /*  when count = 0, countDown operation cause interrupt
     TODO: interrupt operation define
   */

  when(io.counterDownEn === true.B){
    counterPart.io.operationAddr := io.counterDownAddr
    counterPart.io.countDownEn := true.B

  }.elsewhen(io.counterDownEn === false.B){
    counterPart.io.countDownEn := false.B
  }

  //sync with counterPart.interruptSignal
  //TODO: interrupt logic verify, Need Dealy a cycle or not?
  when(counterPartInterrupt_wire === true.B && valid(addr_wire) === true.B) {
    printf("exceptionPost\n")
    io.interruptPost := true.B
    valid := valid.bitSet(addr_wire, false.B)
  }

  //read rData, only read Metamem.pId info, read Data without condition
  io.rData := Metamem(io.opAddr).pId

  //print
  printf("Metamem(%d).inputLink = %d\n", io.opAddr, Metamem.read(io.opAddr).inputLink)
  printf("Metamem(%d).pId = %d\n", io.opAddr, Metamem.read(io.opAddr).pId)
  printf("io.rData = %d\n", io.rData)
  printf("io.interruptPost = %d\n", io.interruptPost)

}




