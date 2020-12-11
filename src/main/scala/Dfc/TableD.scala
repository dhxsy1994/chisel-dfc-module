package Dfc


import chisel3._
import chisel3.util._
import chisel3.internal.firrtl.Width
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

//TableD Meta
class D_addrMeta extends Bundle {
  val listenAddr = UInt(32.W)
}

class D_infoMeta extends Bundle {
  val TableAId = UInt(6.W)
  val LinkNext = UInt(8.W)
}

class dfc_DIO extends Bundle {
  //write with two type enable
  val wEnAddr = Input(Bool())
  val wEnInfo = Input(Bool())

  val opAddr = Input(UInt(8.W))
  val wData = Input(UInt(32.W))

  //listen write memory address
  val listenAddr = Input(UInt(32.W))

  //TODO: raed port is not fully designed
  //val rAddr = Input(UInt(8.W))
  val rData = Output(UInt(32.W))

  val counterDownAddr = Output(UInt(6.W))
  val counterDownEn = Output(Bool())
}

class dfc_D extends Module {

  val io = IO(new dfc_DIO)

  //ports init
  io.counterDownEn := false.B
  io.counterDownAddr := 0.U

  //buffered input regs
  val lastwEnAddr = RegInit(false.B)
  lastwEnAddr := io.wEnAddr
  val lastwEnInfo = RegInit(false.B)
  lastwEnInfo := io.wEnInfo
  val lastopAddr = RegInit(0.U)
  lastopAddr  := io.opAddr
  val lastwData = RegInit(0.U)
  lastwData := io.wData
  val lastlistenAddr = RegInit(0.U)
  lastlistenAddr := io.listenAddr

  //Meta
  val addrMeta_valid = RegInit(0.U(256.W))
  val infoMeta_valid = RegInit(0.U(256.W))

  val addrMetaMem = Mem(256, new D_addrMeta)
  val infoMetaMem = Mem(256, new D_infoMeta)

  io.rData := addrMetaMem(lastopAddr).listenAddr

  //middle addr wire
  val addr_wire  = Wire(UInt(8.W))
  addr_wire := lastopAddr

  //write Meta wire with init
  //TODO: infoMeta LinkNext data type is UInt, can not recognize NULL. Updating in future version
  val winfoMeta = Wire(new D_infoMeta)
  winfoMeta.LinkNext := 0.U
  winfoMeta.TableAId := 0.U

  val waddrMeta = Wire(new D_addrMeta)
  waddrMeta.listenAddr := 0.U

  //TODO: verify two type enalbe signal with write
  when(lastwEnAddr) {
    waddrMeta.listenAddr := lastwData
  }.elsewhen(lastwEnInfo){
    winfoMeta.TableAId := lastwData(5, 0)
    winfoMeta.LinkNext := lastwData(13, 6)
  }

  //write addrMeta
  when(lastwEnAddr && !addrMeta_valid(addr_wire)){
    addrMetaMem.write(addr_wire, waddrMeta)
    addrMeta_valid := addrMeta_valid.bitSet(addr_wire, true.B)
  }

  //write infoMeta
  when(lastwEnInfo && !infoMeta_valid(addr_wire)){
    infoMetaMem.write(addr_wire, winfoMeta)
    infoMeta_valid := infoMeta_valid.bitSet(addr_wire, true.B)
  }

  /* not used
  //condition judge wData type by wDataTail_orR
  //Tail all 0 judge
    true = type1
    false = type2

  val wDataTail_orR = io.wData(31, 14).orR()

  //condition judge wData type by wDataTail_orR
  when(wDataTail_orR === false.B){
    //is type2
    winfoMeta.LinkNext := io.wData(5, 0)
    winfoMeta.TableAId := io.wData(13, 6)

    waddrMeta.listenAddr := 0.U
  }.elsewhen(wDataTail_orR === true.B){
    //is type1
    winfoMeta.LinkNext := 0.U
    winfoMeta.TableAId := 0.U

    waddrMeta.listenAddr := io.wData
  }

  //write addrMeta
  when(io.wEn === true.B && wDataTail_orR === true.B && addrMeta_valid(addr_wire) === false.B){
    addrMetaMem.write(addr_wire, waddrMeta)
    addrMeta_valid := addrMeta_valid.bitSet(addr_wire, true.B)
  }
  //write infoMeta
  when(io.wEn === true.B && wDataTail_orR === false.B && infoMeta_valid(addr_wire) === false.B){
    infoMetaMem.write(addr_wire, winfoMeta)
    infoMeta_valid := infoMeta_valid.bitSet(addr_wire, true.B)
  }
  */

  val listenHitAddr = Wire(UInt())
  listenHitAddr := 0.U

  //listenAddr parallel compare 256 lines
  for(i <- 0 to 255){
    when(lastlistenAddr === addrMetaMem(i.asUInt()).listenAddr &&
      addrMeta_valid(i) && infoMeta_valid(i)) {
      printf("---listenAddr matched---\n")
      io.counterDownAddr := infoMetaMem(i.asUInt()).TableAId
      io.counterDownEn := true.B
      listenHitAddr := i.asUInt()
    }
  }

  when(io.counterDownEn){
    printf("listenHitaddr = %d\n", listenHitAddr)
    addrMeta_valid := addrMeta_valid.bitSet(listenHitAddr, false.B)
    infoMeta_valid := infoMeta_valid.bitSet(listenHitAddr, false.B)
  }

  printf("addrMeta(%d) = %x\n", lastopAddr, addrMetaMem(lastopAddr).listenAddr)
  printf("infoMeta(%d).LinkNext = %d\n", lastopAddr, infoMetaMem(lastopAddr).LinkNext)
  printf("infoMeta(%d).TableAId = %d\n", lastopAddr, infoMetaMem(lastopAddr).TableAId)

  printf("io.counterDownAddr = %d\n", io.counterDownAddr)
  printf("io.counterDownEn = %d\n", io.counterDownEn)

  //printf("listenHitAddr = %d\n", listenHitAddr)
}

