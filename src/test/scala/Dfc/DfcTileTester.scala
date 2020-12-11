package Dfc

import chisel3._
import chisel3.util._
import chisel3.internal.firrtl.Width
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import org.scalatest.{FlatSpec, Matchers}

class WaveformTester_DfcTileTester_twA(dut: DfcTile) extends PeekPokeTester(dut) {
  //Testing write TableA
  val wData_A = 0x03080010

  val wEnA_t =      Seq(0, 1, 0, 0, 0, 0, 0, 0)
  val wEnD_Addr_t = Seq(0, 0, 0, 0, 0, 0, 0, 0)
  val wEnD_Info_t = Seq(0, 0, 0, 0, 0, 0, 0, 0)

  val wData_t =     Seq(0, wData_A, 0, 0, 0, 0, 0, 0)

  val opAddr_A_t =  Seq(0, 8, 8, 8, 0, 0, 0, 0)
  val opAddr_D_t =  Seq(0, 0, 0, 0, 0, 0, 0, 0)

  val listenAddr_t= Seq(0, 0, 0, 0, 0, 0, 0, 0)

  println("Testing singal step length = " + wData_t.length)

  for (i <- wEnA_t.indices) {
    poke(dut.io.wEnA, wEnA_t(i))
    poke(dut.io.wEnD_Addr, wEnD_Addr_t(i))
    poke(dut.io.wEnD_Info, wEnD_Info_t(i))
    poke(dut.io.wData, wData_t(i))
    poke(dut.io.opAddr_A, opAddr_A_t(i))
    poke(dut.io.opAddr_D, opAddr_D_t(i))
    poke(dut.io.listenAddr, listenAddr_t(i))
    step(1)
  }

  step(1)
}

class WaveformSpec_DfcTileTester_twA extends FlatSpec with Matchers{
  "DfcTileTester_Test write TableA" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new DfcTile){
      c => new WaveformTester_DfcTileTester_twA(c)
    } should be (true)
  }
}

class WaveformTester_DfcTileTester_twD(dut: DfcTile) extends PeekPokeTester(dut) {
  /* Testing write TableD
     A have one line
     D have three lines
   */
  val wData_A = 0x03080010
  //write TableD MetaInfo: tableAId = 12, LinkNext =  9, 10, 0

  val wData_D_addr0 = 0x46403377
  val wData_D_addr1 = 0x46403378
  val wData_D_addr2 = 0x46403379

  val wData_D_info0 = 0x0000024C
  val wData_D_info1 = 0x0000028C
  val wData_D_info2 = 0x0000000C

//  val wData_A_1 = 0x02180256
//  val wData_D_1_addr0 = 0x00003377
//  val wData_D_1_addr1 = 0x00003378
//  val wData_D_1_addr2 = 0x00003379


  //TODO: infoMeta LinkNext data type is UInt, can not recognize NULL
  val wEnA_t =      Seq(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
  //write TableD with 2 enable
  val wEnD_Addr_t = Seq(0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0)
  val wEnD_Info_t = Seq(0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0)

  val wData_t =     Seq(0, 0, wData_D_addr0, wData_D_info0, wData_D_addr1,
                        wData_D_info1, wData_D_addr2, wData_D_info2, 0, 0, 0)

  val opAddr_A_t =  Seq(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
  //opAddrD write same addr
  val opAddr_D_t =  Seq(0, 0, 8, 8, 9, 9, 10, 10, 0, 0, 0)

  val listenAddr_t= Seq(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

  println("Testing singal step length = " + wData_t.length)

  for (i <- wEnA_t.indices) {
    poke(dut.io.wEnA, wEnA_t(i))
    poke(dut.io.wEnD_Addr, wEnD_Addr_t(i))
    poke(dut.io.wEnD_Info, wEnD_Info_t(i))
    poke(dut.io.wData, wData_t(i))
    poke(dut.io.opAddr_A, opAddr_A_t(i))
    poke(dut.io.opAddr_D, opAddr_D_t(i))
    poke(dut.io.listenAddr, listenAddr_t(i))
    step(1)
  }
  step(1)
}

class WaveformSpec_DfcTileTester_twD extends FlatSpec with Matchers {
  "DfcTileTester_Test write TableD" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new DfcTile){
      c => new WaveformTester_DfcTileTester_twD(c)
    } should be (true)
  }
}

class  WaveformTester_DfcTileTester(dut: DfcTile) extends PeekPokeTester(dut){
  /* Testing write and listenHit
    with complete write and read
 */
  val wData_A = 0x03080010
  //write TableD MetaInfo: tableAId = 12, LinkNext =  9, 10, 0

  val wData_D_addr0 = 0x46403377
  val wData_D_addr1 = 0x46403378
  val wData_D_addr2 = 0x46403379

  val wData_D_info0 = 0x0000024C
  val wData_D_info1 = 0x0000028C
  val wData_D_info2 = 0x0000000C

  val wEnA_t =      Seq(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
  //write TableD with 2 enable
  val wEnD_Addr_t = Seq(0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
  val wEnD_Info_t = Seq(0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

  val wData_t =     Seq(0, wData_A, 0, wData_D_addr0, wData_D_info0, wData_D_addr1, wData_D_info1, wData_D_addr2, wData_D_info2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

  val opAddr_A_t =  Seq(0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
  //opAddrD write same addr
  val opAddr_D_t =  Seq(0, 0, 0, 8, 8, 9, 9, 10, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

  val listenAddr_t= Seq(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, wData_D_addr0, wData_D_addr1, wData_D_addr2, 0, 0, 0, 0, 0, 0)

  println("Testing singal step length = " + wData_t.length)

  for (i <- wEnA_t.indices) {
    poke(dut.io.wEnA, wEnA_t(i))
    poke(dut.io.wEnD_Addr, wEnD_Addr_t(i))
    poke(dut.io.wEnD_Info, wEnD_Info_t(i))
    poke(dut.io.wData, wData_t(i))
    poke(dut.io.opAddr_A, opAddr_A_t(i))
    poke(dut.io.opAddr_D, opAddr_D_t(i))
    poke(dut.io.listenAddr, listenAddr_t(i))
    step(1)
  }
  step(1)
}

class WaveformSpec_DfcTileTester extends FlatSpec with Matchers{
  "DfcTileTester_Test" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new DfcTile){
      c => new WaveformTester_DfcTileTester(c)
    } should be (true)
  }
}