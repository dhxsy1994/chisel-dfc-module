
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
  val load_t = Seq(1, 1, 0, 0, 0, 0, 0, 0)
  val countDown_t = Seq(0, 0, 1, 1, 1, 0, 0, 0)
  val operationAddr_t = Seq(2, 7, 2, 2, 2, 0, 0, 0)
  val dIn_t = Seq(3, 2, 0, 0, 0, 0, 0, 0)

  println("Testing singal step length = " + load_t.length)

  for(i <- countDown_t.indices){
    poke(dut.io.dIn, dIn_t(i))
    poke(dut.io.load, load_t(i))
    poke(dut.io.operationAddr, operationAddr_t(i))
    poke(dut.io.countDownEn, countDown_t(i))
    step(1)
  }
  //end cycle
  step(1)

}

class WaveformSpec_A_counterPart extends FlatSpec with Matchers {
  "WaveformSpecTest_A_counterPart" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new A_counterPart){
      c => new WaveformTester_A_counterPart(c)
    } should be (true)
  }
}

//DFC_A testing
//sbt testOnly Dfc.WaveformTester_dfc_A
class WaveformTester_dfc_A_Loopone (dut: dfc_A) extends PeekPokeTester(dut) {
  /* testing write once
    //Count from 3 -> 0, InterruptSig 0 -> 1
    //Waveform shown correct

    tdData
    val twCount = 3.U(8.W)
    val twinputLink = 8.U(8.W)
    val twpId = 16.U(16.W)
   */

  val twData = 0x03080010

  //testing poke Seq
  val wEn_t = Seq(1, 0, 0, 0, 0, 0, 0, 0)
  val wData_t = Seq(twData, 0, 0, 0, 0, 0, 0, 0)
  val opAddr_t = Seq(9, 9, 0, 0, 0, 0, 0, 0)

  val counterDownEn_t = Seq(0, 0, 1, 1, 0, 1, 0, 0)
  //val counterDownEn_t = Seq(0, 0, 0, 0, 0, 0, 0, 0)
  val counterDownAddr_t = Seq(0, 0, 9, 9, 0, 9, 0, 0)

  println("Testing singal step length = " + wEn_t.length)

  for (i <- wEn_t.indices) {
    poke(dut.io.wEn, wEn_t(i))
    poke(dut.io.wData, wData_t(i))
    poke(dut.io.opAddr, opAddr_t(i))
    //poke(dut.io.rAddr, rAddr_t(i))
    poke(dut.io.counterDownAddr, counterDownAddr_t(i))
    poke(dut.io.counterDownEn, counterDownEn_t(i))
    step(1)
  }

  step(1)
}

class WaveformSpec_dfc_A_Loopone extends FlatSpec with Matchers {
  "WaveformTester_dfc_A_Loopone" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new dfc_A){
      c => new WaveformTester_dfc_A_Loopone(c)
    } should be (true)
  }
}

class WaveformTester_dfc_A_Looptwo(dut: dfc_A) extends PeekPokeTester(dut){
  /* testing write twice
    twData0 -> A(9)
    val twCount = 3.U(8.W)
    val twinputLink = 8.U(8.W)
    val twpId = 16.U(16.W)

    twData1 -> A(7)
    val twCount = 2.U(8.W)
    val twinputLink = 19.U(8.W)
    val twpId = 256.U(16.W)
   */
  val twData0 = 0x03080010

  val tdData1 = 0x02130100

  val wEn_t = Seq(1, 0, 1, 0, 0, 0, 0, 0, 0, 0)
  val wData_t = Seq(twData0, 0, tdData1, 0, 0, 0, 0, 0, 0, 0)
  val opAddr_t = Seq(9, 9, 7, 7, 0, 0, 0, 0, 0, 0)

  val counterDownEn_t = Seq(0, 0, 0, 0, 1, 1, 1, 1, 1, 0)
  //val counterDownEn_t = Seq(0, 0, 0, 0, 0, 0, 0, 0)
  val counterDownAddr_t = Seq(0, 0, 0, 0, 9, 9, 9, 7, 7, 0)

  println("Testing singal step length = " + wEn_t.length)

  for (i <- wEn_t.indices){
    poke(dut.io.wEn, wEn_t(i))
    poke(dut.io.wData, wData_t(i))
    poke(dut.io.opAddr, opAddr_t(i))
    //poke(dut.io.rAddr, rAddr_t(i))
    poke(dut.io.counterDownAddr, counterDownAddr_t(i))
    poke(dut.io.counterDownEn, counterDownEn_t(i))
    step(1)
  }
  poke(dut.io.wEn, 0)
  poke(dut.io.wData, 0)
  poke(dut.io.opAddr, 0)
  //poke(dut.io.rAddr, rAddr_t(i))
  poke(dut.io.counterDownAddr, 0)
  poke(dut.io.counterDownEn, 0)
  step(1)
}

class WaveformSpec_dfc_A_Looptwo extends FlatSpec with Matchers {
  "WaveformTester_dfc_A_Loopone" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new dfc_A){
      c => new WaveformTester_dfc_A_Looptwo(c)
    } should be (true)
  }
}