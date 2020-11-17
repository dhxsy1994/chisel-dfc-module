
package Dfc

import chisel3.iotesters.{Driver, PeekPokeTester}
import chisel3.testers.BasicTester
import chisel3._
import chisel3.util._
import chisel3.testers._
import org.scalatest.{FlatSpec, Matchers}

//counter部件 testing
//sbt testOnly Dfc.WaveformTester_A_counterPart
class WaveformTester_A_counterPart(dut: A_counterPart) extends PeekPokeTester(dut){
  poke(dut.io.dIn, 4)
  poke(dut.io.load, true)
  poke(dut.io.operationAddr, 2)
  poke(dut.io.countDownEn, false)
  step(1)
  poke(dut.io.dIn, 4)
  poke(dut.io.load, false)
  poke(dut.io.operationAddr, 2)
  poke(dut.io.countDownEn, false)
  step(1)
  poke(dut.io.dIn, 4)
  poke(dut.io.load, false)
  poke(dut.io.operationAddr, 2)
  poke(dut.io.countDownEn, true)
  step(1)
  poke(dut.io.dIn, 4)
  poke(dut.io.load, false)
  poke(dut.io.operationAddr, 2)
  poke(dut.io.countDownEn, false)
  step(1)
  poke(dut.io.dIn, 4)
  poke(dut.io.load, false)
  poke(dut.io.operationAddr, 2)
  poke(dut.io.countDownEn, true)
  step(1)
  poke(dut.io.dIn, 4)
  poke(dut.io.load, false)
  poke(dut.io.operationAddr, 2)
  poke(dut.io.countDownEn, true)
  step(1)
  poke(dut.io.dIn, 4)
  poke(dut.io.load, false)
  poke(dut.io.operationAddr, 2)
  poke(dut.io.countDownEn, true)
  step(1)
  poke(dut.io.dIn, 4)
  poke(dut.io.load, false)
  poke(dut.io.operationAddr, 2)
  poke(dut.io.countDownEn, false)
  step(1)
  poke(dut.io.dIn, 4)
  poke(dut.io.load, false)
  poke(dut.io.operationAddr, 2)
  poke(dut.io.countDownEn, false)
  step(1)
}

class WaveformSpec_A_counterPart extends FlatSpec with Matchers {
  "WaveformSpec-Test_seq" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new A_counterPart){
      c => new WaveformTester_A_counterPart(c)
    } should be (true)
  }
}

//A表部件 testing
//sbt testOnly Dfc.WaveformTester_dfc_A
class WaveformTester_dfc_A (dut: dfc_A) extends PeekPokeTester(dut) {
  val twCount = 3.U(8.W)
  val twinputLink = 8.U(8.W)
  val twpId = 16.U(16.W)

  //test write data
  val twData = Cat(twCount, twinputLink, twpId)
  val twAddr = 3.U(6.W)
  poke(dut.io.wEn, true)
  poke(dut.io.wData, twData)
  poke(dut.io.wAddr, twAddr)
  step(2)
  step(2)
}

class WaveformSpec_dfc_A extends FlatSpec with Matchers {
  "WaveformSpec-dfc_A" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new dfc_A){
      c => new WaveformTester_dfc_A(c)
    } should be (true)
  }
}