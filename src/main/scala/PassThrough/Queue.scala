package PassThrough

import chisel3._
import chisel3.util._
import chisel3.internal.firrtl.Width

class QueueIO extends Bundle {
  val in = Flipped(Decoupled(UInt(8.W)))
  val out = Decoupled(UInt(8.W))
}

class Queue_ready extends Module{
  val io = IO(new QueueIO)

  val queue = Queue(io.in, 32)
  io.out <> queue
}