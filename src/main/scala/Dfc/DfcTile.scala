
package Dfc

import chisel3._
import chisel3.util._
import chisel3.internal.firrtl.Width
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}


class DfcTileIO extends Bundle{
  val wEnA = Input(Bool())
  val wEnD_Addr = Input(Bool())
  val wEnD_Info = Input(Bool())

  val wData = Input(UInt(32.W))

  val opAddr_A = Input(UInt(6.W))
  val opAddr_D = Input(UInt(8.W))
  //TODO: Input data control?

  val rData = Output(UInt(32.W))
  //TODO: Only A table need read data out

  val listenAddr = Input(UInt(32.W))
  val interruptPost = Output(Bool())
}


class DfcTile extends Module {
  val io = IO(new DfcTileIO)

  val TableA = Module(new dfc_A)
  val TableD = Module(new dfc_D)

  //wEn connect
  TableA.io.wEn := io.wEnA

  TableD.io.wEnAddr := io.wEnD_Addr
  TableD.io.wEnInfo := io.wEnD_Info

  //wAddr connect
  TableA.io.opAddr := io.opAddr_A
  TableD.io.opAddr := io.opAddr_D

  //wData connect
  TableA.io.wData := io.wData
  TableD.io.wData := io.wData

  //rData connect
  io.rData := TableA.io.rData

  //listenAddr connect
  TableD.io.listenAddr := io.listenAddr

  //interruptPost connect
  io.interruptPost := TableA.io.interruptPost

  //counterDownEn connect
  TableA.io.counterDownEn <> TableD.io.counterDownEn
  //counterDownAddr connect
  TableA.io.counterDownAddr <> TableD.io.counterDownAddr

  printf("TileinterruptPost=%d\n", io.interruptPost)
}

