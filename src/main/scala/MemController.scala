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

        val mem_addr = Output(UInt(32.W))
        val mem_wrEn = Output(UInt(1.W))
        val mem_wData = Output(UInt(32.W))
        val mem_rData = Input(UInt(32.W))
    })

    io.mem_addr := io.addr

    io.rData := 0.U
    switch(io.rdOp){
        is(memRdOpT.IDLE){
            io.rData := 0.U
        }
        is(memRdOpT.LB){
            switch(io.addr(1,0)){
                is("b00".U){io.rData := Cat(Fill(24,io.mem_rData(7)), io.mem_rData(7,0))}
                is("b01".U){io.rData := Cat(Fill(24,io.mem_rData(15)), io.mem_rData(15,8))}
                is("b10".U){io.rData := Cat(Fill(24,io.mem_rData(23)), io.mem_rData(23,16))}
                is("b11".U){io.rData := Cat(Fill(24,io.mem_rData(31)), io.mem_rData(31,24))}
            }
        }
        is(memRdOpT.LBU){
            switch(io.addr(1,0)){
                is("b00".U){io.rData := Cat(Fill(24, 0.U), io.mem_rData(7,0))}
                is("b01".U){io.rData := Cat(Fill(24, 0.U), io.mem_rData(15,8))}
                is("b10".U){io.rData := Cat(Fill(24, 0.U), io.mem_rData(23,16))}
                is("b11".U){io.rData := Cat(Fill(24, 0.U), io.mem_rData(31,24))}
            }
        }
        is(memRdOpT.LH){
            switch(io.addr(1,1)){ // [TODO] check this syntax ***************************************
                is("b0".U){io.rData := Cat(Fill(16, io.mem_rData(15)), io.mem_rData(15,0))}
                is("b1".U){io.rData := Cat(Fill(24, io.mem_rData(31)), io.mem_rData(31,16))}
            }
        }
        is(memRdOpT.LHU){
            switch(io.addr(1,1)){ // [TODO] check this syntax ***************************************
                is("b0".U){io.rData := Cat(Fill(16, 0.U), io.mem_rData(15,0))}
                is("b1".U){io.rData := Cat(Fill(24, 0.U), io.mem_rData(31,16))}
            }
        }
        is(memRdOpT.LW){io.rData := io.mem_rData}
    }

    io.mem_wData := 0.U
    switch(io.wrOp){
        is(memWrOpT.IDLE){io.mem_wData := 0.U}
        is(memWrOpT.SB){
            switch(io.addr(1,0)){
                is("b00".U){io.mem_wData := Cat(io.mem_rData(31,8), io.wData(7,0))}
                is("b01".U){io.mem_wData := Cat(io.mem_rData(31,16), io.wData(15,8), io.mem_rData(7,0))}
                is("b10".U){io.mem_wData := Cat(io.mem_rData(31,24), io.wData(23,16), io.mem_rData(15,0))}
                is("b11".U){io.mem_wData := Cat(io.wData(31,24), io.mem_rData(24,0))}
            }
        }
        is(memWrOpT.SH){
            switch(io.addr(1,1)){
                is("b0".U){io.mem_wData := Cat(io.mem_rData(31,16), io.wData(15,0))}
                is("b1".U){io.mem_wData := Cat(io.wData(31,16), io.mem_rData(15,0))}
            }
        }
        is(memWrOpT.SW){
            io.mem_wData := io.wData
        }
    }
    
    io.mem_wrEn := (io.wrOp =/= memWrOpT.IDLE)
}

class DMEM (DEPTH: Int = 4096) extends Module {
    val io = IO(new Bundle{
        val addr = Input(UInt(32.W))
        val wData = Input(UInt(32.W))
        val wrEn = Input(UInt(1.W))
        val rData = Output(UInt(32.W))
    })

    val mem = Mem(DEPTH, UInt(32.W))

    when(io.wrEn === 1.U){
        mem(io.addr) := io.wData
    }
    io.rData := mem(io.addr)
}