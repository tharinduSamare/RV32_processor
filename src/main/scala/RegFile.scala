package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class regFileReadReq extends Bundle {
    val addr  = Input(UInt(5.W))
}

class regFileReadResp extends Bundle {
    val data  = Output(UInt(32.W))
}

class regFileWriteReq extends Bundle {
    val addr  = Input(UInt(5.W))
    val data  = Input(UInt(32.W))
    val wr_en = Input(Bool())
}

class RegFile extends Module {
  val io = IO(new Bundle {
    val req_1  = new regFileReadReq
    val resp_1 = new regFileReadResp
    val req_2  = new regFileReadReq
    val resp_2 = new regFileReadResp
    val req_3  = new regFileWriteReq
})

  val RegFile_inst = Mem(32, UInt(32.W))
  RegFile_inst(0) := 0.U // hard-wired zero for x0

  when(io.req_3.wr_en){
    when(io.req_3.addr =/= 0.U){
      RegFile_inst(io.req_3.addr) := io.req_3.data
    }
  }

  io.resp_1.data := Mux((io.req_1.addr === 0.U), 0.U, (Mux((io.req_1.addr === io.req_3.addr), io.req_3.data, RegFile_inst(io.req_1.addr))))
  io.resp_2.data := Mux((io.req_2.addr === 0.U), 0.U, (Mux((io.req_2.addr === io.req_3.addr), io.req_3.data, RegFile_inst(io.req_2.addr))))

}


