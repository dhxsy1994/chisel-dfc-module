//单元测试类
//单元测试激励信号
package PassThrough

import chisel3._
import chisel3.util._
import org.scalatest._
import chisel3.testers._
import chisel3.iotesters._
import scala.collection.mutable.ArrayBuffer
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}



class WaveformTester_PT(dut: PTgenerator) extends PeekPokeTester(dut){
  poke(dut.io.in, 0)
  //expect(dut.io.out, 0)
  step(1)
  poke(dut.io.in, 1) // Set our input to value 1
  step(1)
  expect(dut.io.out, 1) // Assert that the output correctly has 1
  poke(dut.io.in, 2) // Set our input to value 2
  step(1)
  expect(dut.io.out, 2)
}

class WaveformSpec_PT extends FlatSpec with Matchers{
  "Wavefrom-test-PT" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new PTgenerator(4) ){
      c => new WaveformTester_PT(c)
    } should be (true)
  }
}

object Types {
  val add :: sub :: mul :: div :: Nil
  = Enum(4)
}


// for testing basictester
class basicT_PT(pt: => PTgenerator) extends BasicTester{
  val dut = Module(pt)
  val width = 4
  //cntr is Counter
  val (cntr, done) = Counter(true.B, 12)

  val rnd = scala.util.Random
  val testList = new ArrayBuffer[UInt]

  for ( i <- 0 to 12){
     testList+=((rnd.nextInt(1 << 4)).U(4.W))
  }

  dut.io.in := VecInit(testList)(cntr)

  when(done){
    stop();
    stop()
  }
  assert(dut.io.out === VecInit(testList)(cntr))
  printf("cntr=%d, io.in=%d, io.out=%d\n", cntr, dut.io.in, dut.io.out)

  val write = ((0 until 4 )foldLeft 0.U){
    (data, i) => data | data
  }
  printf("write=%d\n", write)
}

class basicT_run extends FlatSpec{
  "basic test PTgenerator" should "pass"in{
    assert(TesterDriver.execute(() => new basicT_PT(new PTgenerator(4))))
  }
}

class WaveformTester_ALU(dut: ALU) extends PeekPokeTester(dut){

  println("ALU tester")
  poke(dut.io.a, 1)
  poke(dut.io.b, 1)
  poke(dut.io.fn, 0)
  expect(dut.io.y, 2)

  poke(dut.io.a, 1)
  poke(dut.io.b, 1)
  poke(dut.io.fn, 1)
  expect(dut.io.y, 0)

  poke(dut.io.a, 1)
  poke(dut.io.b, 2)
  poke(dut.io.fn, 2)
  expect(dut.io.y, 2)

  poke(dut.io.a, 4)
  poke(dut.io.b, 2)
  poke(dut.io.fn, 3)
  expect(dut.io.y, 2)

//  println("ALU def tester")
//  def alu_verliator(a:Int, b: Int, fn: Int): Int = {
//    fn match{
//      case 0 => a + b
//      case 1 => a - b
//      case 2 => a * b
//      case 3 => a / b
//      case _ => -100
//    }
//  }
//  def def_tester(values: Seq[Int]):Unit = {
//    for (fun <- Types.add to Types.div){
//      for (a <- values ){
//        for (b <- values){
//          poke(dut.io.fn, fun)
//          poke(dut.io.a, a)
//          poke(dut.io.b, b)
//          step(1)
//          expect(dut.io.y, alu_verliator(a, b, fun.toInt))
//        }
//      }
//    }
//  }
//  val randomSeq = Seq.fill(10)(scala.util.Random.nextInt(10) + 1)
//  def_tester(randomSeq)
}

class WaveformSpec_ALU extends FlatSpec with Matchers{
  "Wavefrom-test-ALU" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new ALU ){
      c => new WaveformTester_ALU(c)
    } should be (true)
  }
}


class WaveformTester_Reg(dut: Reg) extends PeekPokeTester(dut){
  poke(dut.io.inVal, 3)
  step(1)
  poke(dut.io.inVal, 5)
  step(1)
  poke(dut.io.inVal, 2)
  step(1)
  poke(dut.io.inVal, 7)
  step(1)
  poke(dut.io.inVal, 4)
  step(2)

}


class WaveformSpec_Reg extends FlatSpec with Matchers{
  "Wavefrom-test-Reg" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "off"), () => new Reg() ){
      c => new WaveformTester_Reg(c)
    } should be (true)
  }
}


class WaveformTester_shiftReg_serIn(dut: shiftReg) extends PeekPokeTester(dut){
  println("shift_Reg tester")
  poke(dut.io.serIn, 1)
  step(1)
  poke(dut.io.serIn, 0)
  step(1)
  poke(dut.io.serIn, 1)
  step(1)
  poke(dut.io.serIn, 0)
  step(2)

}

class WaveformTester_shiftReg_serOut(dut: shiftReg_serOut) extends PeekPokeTester(dut){
  println("shift_Reg_serOut tester")
  //并行输入测试，需要激励值位完成的4bit数字
  poke(dut.io.load, true)
  poke(dut.io.d, 5)
  step(1)
  poke(dut.io.load, false)
  step(4)
}


class WaveformTester_Reg_en(dut: Reg_en) extends PeekPokeTester(dut){
  println("Reg_en tester")
  poke(dut.io.alter_reset, true)

  poke(dut.io.enable, true)
  step(1)
  poke(dut.io.data, 2)
  step(1)
  poke(dut.io.data, 3)
  step(1)
  poke(dut.io.data, 5)
  step(1)
  poke(dut.io.enable, false)
  poke(dut.io.data, 2)
  step(1)
  poke(dut.io.enable,true)
  step(1)
  poke(dut.io.data, 7)
  step(1)
  poke(dut.io.data,4)

}

class WaveformTester_Memory(dut: Memory) extends PeekPokeTester(dut){
  poke(dut.io.wrData, 88)
  poke(dut.io.wrAddr, 0)
  poke(dut.io.wrEna, true)
  step(1)
  poke(dut.io.rdAddr, 0)
  step(1)
}










class WaveformSpec_shiftReg extends FlatSpec with Matchers{
  "Wavefrom-test-shiftReg" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new shiftReg() ){
      c => new WaveformTester_shiftReg_serIn(c)
    } should be (true)
  }
}

class WaveformSpec_shiftReg_serOut extends FlatSpec with Matchers {
  "Wavefrom-test-shiftReg_serOut" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new shiftReg_serOut() ){
      c =>  new WaveformTester_shiftReg_serOut(c)
    } should be (true)
  }
}

class WaveformSpec_Reg_en extends FlatSpec with Matchers {
  "WaveformSpec-Reg_en" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new Reg_en()){
      c => new WaveformTester_Reg_en(c)
    } should be (true)
  }
}

class WaveformSpec_Memory extends FlatSpec with Matchers {
  "WaveformSpec-Memory" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new Memory()){
      c => new WaveformTester_Memory(c)
    } should be (true)
  }
}


//基础测试类 继承App类即可执行
/*
object PTtester extends App {
  //添加drvier的执行参数，写vcd波形文件到目标文件夹
  Driver.execute(
    Array("--generate-vcd-output", "on", "--target-dir", "test_run_dir/make_a_vcd", "--top-name", "make_a_vcd"),
    //Array("--target-dir", "test_generated"),
    () => new PTgenerator(4)) {
    c =>
      new PeekPokeTester[PTgenerator](c) {
        poke(c.io.in, 0) // Set our input to value 0
        expect(c.io.out, 0) // Assert that the output correctly has 0
        poke(c.io.in, 1) // Set our input to value 1
        expect(c.io.out, 1) // Assert that the output correctly has 1
        poke(c.io.in, 2) // Set our input to value 2
        expect(c.io.out, 2) // Assert that the output correctly has 2
      }
  }
  println("Test over")
}
  val testResult = Driver(() => new PTgenerator(4)) {

    c => new PeekPokeTester(c) {
      poke(c.io.in, 0)     // Set our input to value 0
      expect(c.io.out, 0)  // Assert that the output correctly has 0
      poke(c.io.in, 1)     // Set our input to value 1
      expect(c.io.out, 1)  // Assert that the output correctly has 1
      poke(c.io.in, 2)     // Set our input to value 2
      expect(c.io.out, 2)  // Assert that the output correctly has 2
    }
  }

  assert(testResult)
*/