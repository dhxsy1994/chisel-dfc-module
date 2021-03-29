// See README.md for license details.

package PassThrough


import chisel3._
import chisel3.util._
import chisel3.internal.firrtl.Width
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class PTgenerator(width: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(width.W))
    val out = Output(UInt(width.W))
  })
  io.out := io.in
}

//算术运算逻辑
class ALU extends Module {
  val io = IO(new Bundle() {
    val a = Input(UInt(16.W))
    val b = Input(UInt(16.W))

    val fn = Input(UInt(2.W))
    val y = Output(UInt(16.W))
  })
  io.y := 0.U
  switch(io.fn){
    is (0.U) {io.y := io.a + io.b}
    is (1.U) {io.y := io.a - io.b}
    is (2.U) {io.y := io.a * io.b}
    is (3.U) {io.y := io.a / io.b}
  }
}

// 普通寄存器，输入端和输出端为对外接口
class Reg extends Module {
  val io = IO(new Bundle() {
    val inVal = Input(UInt(4.W))
    val outVal = Output(UInt(4.W))
  })
  val valReg = RegInit(0.U(4.W))
  valReg := io.inVal
  //valReg := valReg.bitSet(3.asUInt(), true.B)
  io.outVal := valReg

  printf("valReg = %d\n", valReg)
}

// 带有使能信号的重置移位寄存器
// 在使能信号为true的时候，寄存器读入输入data
// 尝试手动覆盖默认的重置信号似乎是成功的。
class Reg_en extends Module{
  val io = IO(new Bundle() {
    val alter_reset = Input(Bool())

    val data = Input(UInt(4.W))
    val enable = Input(Bool())
    val out = Output(UInt(4.W))
  })
  val enableReg = RegInit(0.U(4.W))

  withReset(io.alter_reset){
    when (io.enable) {
      enableReg := io.data
    }
    io.out := enableReg
  }
  printf("%d\n", io.out)
}

// 串行输入，并行输出的4位移位寄存器
class shiftReg extends Module {
  val io = IO(new Bundle() {
    val serIn = Input(UInt(4.W))
    val q = Output(UInt(4.W))
  })
  val outReg = RegInit(0.U(4.W))
  outReg := Cat(io.serIn, outReg(3, 1))
  io.q := outReg
}

// 并行输入，串行输出的4位移位寄存器
class shiftReg_serOut extends Module {
  val io = IO(new Bundle() {
    val d = Input(UInt(4.W))
    val load = Input(Bool())
    val serOut = Output(UInt(4.W))
  })

  val loadReg = RegInit(0.U(4.W))
  when(io.load){
    loadReg := io.d
  }otherwise{
    loadReg := Cat(0.U, loadReg(3,1))
  }
  io.serOut := loadReg(0)
  printf("io.serOut: %d\n", io.serOut)
}

// memory
// 没有定义读中写的行为
class Memory() extends Module {
  val io = IO(new Bundle() {
    val rdAddr = Input(UInt(10.W))
    val rdData = Output(UInt(8.W))
    val wrEna = Input(Bool())
    val wrData = Input(UInt(8.W))
    val wrAddr = Input(UInt(10.W))
  })

  val mem = SyncReadMem(1024, UInt(8.W))

  //val dataMem  = Seq.fill(4)(SeqMem(256, Vec(4, UInt(8.W))))

  io.rdData := mem.read(io.rdAddr)

  when(io.wrEna){
    mem.write(io.wrAddr, io.wrData)
  }
  printf("%d\n", io.rdData)
}

class DeviceUnderTest extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(2.W))
    val b = Input(UInt(2.W))
    val out = Output(UInt(2.W))
    })
  	  io.out := io.a & io.b // 端口输出连接
}

