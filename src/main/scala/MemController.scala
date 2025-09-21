package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class MemController extends Module{
    val io = IO(new Bundle{
        val addr = Input(UInt(32.W))
        val wrOp = Input(memWrOpT())
        val wData = Input(UInt(32.W))
        val rdOp = Input(memRdOpT())
        val rData = Output(UInt(32.W))

        val dmem = Flipped(new DMEM_IO)
    })

    io.dmem.addr := io.addr>>2 // byte address to word address convert

    io.rData := 0.U
    switch(io.rdOp){
        is(memRdOpT.IDLE) {io.rData := 0.U}
        is(memRdOpT.LW)   {io.rData := io.dmem.rData}
    }

    io.dmem.wData := 0.U
    switch(io.wrOp){
        is(memWrOpT.IDLE) {io.dmem.wData := 0.U}
        is(memWrOpT.SW)   {io.dmem.wData := io.wData}
    }
    
    io.dmem.wrEn := (io.wrOp =/= memWrOpT.IDLE)
}