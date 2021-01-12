package PassThrough

import chisel3._
import chisel3.util._
import chisel3.internal.firrtl.Width
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class counterPart extends Module {
  //counter 部件
  val io = IO(new Bundle() {
    val countDownEn = Input(Bool())
    val dIn = Input(UInt(8.W))
    val load = Input(Bool())

    val interruptSignal = Output(Bool())
  })
  val cnt = Reg(UInt(8.W)) //init计数reg
  val equalZero = cnt === 0.U //判0

  val next = WireInit(0.U(8.W))

  when(io.load === true.B){
    next := io.dIn
  }.elsewhen(io.load === false.B && io.countDownEn === false.B){
    next := cnt
  }.elsewhen(io.load === false.B && io.countDownEn === true.B){
    next := cnt - 1.U
  }

  //通路中间值next
  cnt := next
  io.interruptSignal := false.B

  //interrupt
  when(equalZero === true.B){
    io.interruptSignal := true.B
  }

  printf("cnt = %d\n", cnt)
}


class Vec_Module extends Module{
  val io = IO(new Bundle() {
    val inData = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  io.out := 0.U

  val counterParts = VecInit(Seq.fill(64){Module(new counterPart()).io})

  //scala 0 until 64, 63 cannot trigger
  for(i <- 0 to 63){
    if(i == 3){
      counterParts(i).load := true.B
      counterParts(i).dIn := io.inData
      counterParts(i).countDownEn := false.B
      counterParts(i).interruptSignal:= false.B

    }else{
      counterParts(i).countDownEn := false.B
      counterParts(i).load := 0.U
      counterParts(i).dIn := 0.U
      counterParts(i).interruptSignal := false.B
    }
  }
}

class SimpleLink extends Bundle {
  val data = Output(UInt(16.W))
  val valid = Output(Bool())
}

class PLink extends SimpleLink {
  val parity = Output(UInt(5.W))
}

class FilterIO extends Bundle {
  val x = Flipped(new PLink)
  val y = new PLink
}

class Filter extends Module {
  val io = IO(new FilterIO)

  io.y.data := 0.U
  io.y.valid := false.B
  io.y.parity := 1.U

  when(io.x.data === 0.U){
    printf("data = 0\n")
  }

}

class Block extends Module {
  val io = IO(new FilterIO)
  val f1 = Module(new Filter)
  val f2 = Module(new Filter)
  f1.io.x <> io.x
  f1.io.y <> f2.io.x
  f2.io.y <> io.y
}