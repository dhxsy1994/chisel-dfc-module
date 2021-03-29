package PassThrough

import chisel3._
import chisel3.util._
import org.scalatest._
import chisel3.testers._
import chisel3.iotesters._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}


class WaveformTester_Vec_Module(dut: Vec_Module) extends PeekPokeTester(dut) {
  poke(dut.io.inData, 8)
  step(1)
  poke(dut.io.inData, 8)
  step(1)
}


class WaveformSpec_Vec_Module extends FlatSpec with Matchers{
  "Wavefrom-test-PT" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new Vec_Module() ){
      c => new WaveformTester_Vec_Module(c)
    } should be (true)
  }
}

class WaveformTester_Block(dut: Block) extends PeekPokeTester(dut) {
  poke(dut.io.x.data, 0)
  step(1)
  poke(dut.io.x.data, 0)
  step(1)
}

class WaveformSpec_width extends FlatSpec with Matchers{
  "Wavefrom-test-PT" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new Block() ){
      c => new WaveformTester_Block(c)
    } should be (true)
  }
}

class Tester(dut: DeviceUnderTest) extends PeekPokeTester(dut) {
	  poke(dut.io.a, 3.U)
	  poke(dut.io.b, 1.U)
	  step(1)
	  expect(dut.io.out, 1)
	  poke(dut.io.a, 2.U)
	  poke(dut.io.b, 0.U)
	  step(1)
	  expect(dut.io.out, 0)
	  println ( " Result is : " + peek ( dut.io.out ).toString )
}

class WaveformSpec_Tester extends FlatSpec with Matchers{
  "Wavefrom_test" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new DeviceUnderTest() ){
      c => new Tester(c)
    } should be (true)
  }
}

