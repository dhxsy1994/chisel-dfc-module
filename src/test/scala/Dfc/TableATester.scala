
package Dfc

import chisel3.iotesters.{Driver, PeekPokeTester}
import chisel3.testers.BasicTester
import chisel3._
import chisel3.util._
import chisel3.testers._
import org.scalatest.{FlatSpec, Matchers}

//counterPart testing
//sbt testOnly Dfc.WaveformTester_A_counterPart
class WaveformTester_A_counterPart(dut: A_counterPart) extends PeekPokeTester(dut){
  //simplify test
  var load_t = Seq(1, 0, 0, 0, 0, 0, 0, 0)
  var countDown_t = Seq(0, 0, 1, 1, 1, 0, 1, 0)
  var dIn_t = Seq(4, 4, 4, 4, 4, 4, 4, 4)

  println("Testing singal step length = " + load_t.length)

  for(i <- 0 until( countDown_t.length - 1)){
    poke(dut.io.dIn, dIn_t(i))
    poke(dut.io.load, load_t(i))
    poke(dut.io.operationAddr, 2)
    poke(dut.io.countDownEn, countDown_t(i))
    step(1)
  }
  //end cycle
  poke(dut.io.dIn, 0)
  poke(dut.io.load, false)
  poke(dut.io.operationAddr, 2)
  poke(dut.io.countDownEn,false)
  step(1)

}

class WaveformSpec_A_counterPart extends FlatSpec with Matchers {
  "WaveformSpec-Test_seq" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new A_counterPart){
      c => new WaveformTester_A_counterPart(c)
    } should be (true)
  }
}

//DFC_A testing
//sbt testOnly Dfc.WaveformTester_dfc_A
class WaveformTester_dfc_A (dut: dfc_A) extends PeekPokeTester(dut) {

  val twCount = 3.U(8.W)
  val twinputLink = 8.U(8.W)
  val twpId = 16.U(16.W)
  // cat = 0x03080010
  //test write data
  //val twData = Cat(twCount, twinputLink, twpId).intValue() can not use, why？
  val twData = 0x03080010

  //testing poke Seq
  val wEn_t = Seq(1, 0, 0, 0, 0, 0, 0, 0)
  val wData_t = Seq(0x03080010, 0x03080010, 0x03080010, 0x03080010, 0x03080010, 0x03080010, 0x03080010, 0x03080010)
  val wAddr_t = Seq(9, 9, 9, 9, 9, 9, 9, 9)

  val rAddr_t = Seq(9, 9, 9, 9, 9, 9, 9, 9)
  val counterDownEn_t = Seq(0, 0, 1, 1, 0, 1, 0, 0)
  val counterDownAddr_t = Seq(0, 0, 0, 0, 0, 0, 0, 0)

  println("Testing singal step length = " + wEn_t.length)

  for (i <- 0 until( wEn_t.length - 1)){
    poke(dut.io.wEn, wEn_t(i))
    poke(dut.io.wData, wData_t(i))
    poke(dut.io.wAddr, wAddr_t(i))
    poke(dut.io.rAddr, rAddr_t(i))
    poke(dut.io.counterDownAddr, counterDownAddr_t(i))
    poke(dut.io.counterDownEn, counterDownEn_t(i))

    step(1)
  }

  poke(dut.io.wEn, false)
  poke(dut.io.wData.asUInt(), 0)
  poke(dut.io.wAddr, 9)
  step(1)

}

class WaveformSpec_dfc_A extends FlatSpec with Matchers {
  "WaveformSpec-dfc_A" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new dfc_A){
      c => new WaveformTester_dfc_A(c)
    } should be (true)
  }
}