package PassThrough

import chisel3._
import chisel3.testers.{BasicTester, TesterDriver}
import chisel3.util._
import org.scalatest.FlatSpec

class QueueTester(q: => Queue_ready) extends BasicTester{
  val dut = Module(q)

  def Cat(l: Seq[Bits]): UInt = (l.tail foldLeft l.head.asUInt){(x, y) =>
    assert(x.isLit() && y.isLit())
    (x.litValue() << y.getWidth | y.litValue()).U((x.getWidth + y.getWidth).W)
  }
  def Cat(x: Bits, l: Bits*): UInt = Cat(x :: l.toList)

  def rs1(inst: UInt) = ((inst.litValue() >> 15) & 0x1f).toInt
  def rs2(inst: UInt) = ((inst.litValue() >> 20) & 0x1f).toInt
  def rd (inst: UInt) = ((inst.litValue() >> 7)  & 0x1f).toInt
  def csr(inst: UInt) =  (inst.litValue() >> 20)
  def reg(x: Int) = (x & ((1 << 5) - 1)).U(5.W)
  def imm(x: Int) = (x & ((1 << 20) - 1)).S(21.W)

  def U(op: UInt, rd: Int, i: Int) =
    Cat(imm(i), reg(rd), op)
  val LUI    = BigInt("0110111", 2).U(7.W)

  val tU = U(LUI, 5, 12416)

  println(tU)

  val (cntr, done) = Counter(true.B, 8)

  val rnd = scala.util.Random
  val testIn_bits = Seq.fill(10)((rnd.nextInt(1 << 8)).U(8.W))

  val testIn_valid = Seq(1, 1, 1, 0, 0, 0, 0, 0).map(i => i.asUInt())

  val testOut_ready = Seq(0, 0, 0, 1, 1, 1, 0, 0).map(i => i.asUInt())

  dut.io.in.bits := VecInit(testIn_bits)(cntr)
  dut.io.in.valid := VecInit(testIn_valid)(cntr)
  dut.io.out.ready := VecInit(testOut_ready)(cntr)

  when(done){
    stop();
    stop()
  }
  printf("io.in.ready = %d\n", dut.io.in.ready)
  printf("io.out.valid = %d, io.out.bits = %d\n", dut.io.out.ready, dut.io.out.bits)
  printf("io.in.bits = %d\n", dut.io.in.bits)
}

class QueueTests extends FlatSpec{
  "basic test PTgenerator" should "pass"in{
    assert(TesterDriver.execute(() => new QueueTester(new Queue_ready)))
  }
}