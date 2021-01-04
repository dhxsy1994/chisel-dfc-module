package PassThrough

import chisel3._
import chisel3.testers.{BasicTester, TesterDriver}
import chisel3.util._
import org.scalatest.FlatSpec

class QueueTester(q: => Queue_ready) extends BasicTester{
  val dut = Module(q)

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
  "basic test QueueTests" should "pass"in{
    assert(TesterDriver.execute(() => new QueueTester(new Queue_ready)))
  }
}