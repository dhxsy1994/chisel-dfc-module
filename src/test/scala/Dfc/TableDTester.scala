package Dfc

import chisel3.iotesters.{Driver, PeekPokeTester}
import chisel3.testers.BasicTester
import chisel3._
import chisel3.util._
import chisel3.testers._
import org.scalatest.{FlatSpec, Matchers}

class WaveformTester_dfc_D (dut: dfc_D) extends PeekPokeTester(dut){
  val Addr_data_t = 0x46403377
  val HitListenAddr: Int = Addr_data_t
  val Info_data_t = 0x0000024C
  //infoMeta  linkNext = 12 tablaAid = 9

  val wEnA_t = Seq(1, 0, 0, 0, 0, 0, 0, 0)
  val wENI_t = Seq(0, 1, 0, 0, 0, 0, 0, 0)
  val opAddr_t = Seq(8, 8, 8, 8, 0, 0, 0, 0)

  val wData_t = Seq(Addr_data_t, Info_data_t, 0, 0, 0, 0, 0, 0)
  val listenAddr_t = Seq(0, 0, 0, 0, HitListenAddr, 0, 0, 0)


  println("Testing singal step length = " + wEnA_t.length)

  for(i <- wEnA_t.indices){
    poke(dut.io.wEnAddr, wEnA_t(i))
    poke(dut.io.wEnInfo, wENI_t(i))
    poke(dut.io.wData, wData_t(i))
    poke(dut.io.opAddr, opAddr_t(i))
    poke(dut.io.wData, wData_t(i))
    poke(dut.io.listenAddr, listenAddr_t(i))
    poke(dut.io.opAddr, opAddr_t(i))
    step(1)
  }

  step(1)
}

class WaveformSpec_dfc_D extends FlatSpec with Matchers {
  "WaveformSpec_Test_dfc_D" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new dfc_D){
      c => new WaveformTester_dfc_D(c)
    } should be (true)
  }
}

