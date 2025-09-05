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
        is(memRdOpT.IDLE){
            io.rData := 0.U
        }
        is(memRdOpT.LB){
            switch(io.addr(1,0)){
                is("b00".U){io.rData := Cat(Fill(24,io.dmem.rData(7)), io.dmem.rData(7,0))}
                is("b01".U){io.rData := Cat(Fill(24,io.dmem.rData(15)), io.dmem.rData(15,8))}
                is("b10".U){io.rData := Cat(Fill(24,io.dmem.rData(23)), io.dmem.rData(23,16))}
                is("b11".U){io.rData := Cat(Fill(24,io.dmem.rData(31)), io.dmem.rData(31,24))}
            }
        }
        is(memRdOpT.LBU){
            switch(io.addr(1,0)){
                is("b00".U){io.rData := Cat(Fill(24, 0.U), io.dmem.rData(7,0))}
                is("b01".U){io.rData := Cat(Fill(24, 0.U), io.dmem.rData(15,8))}
                is("b10".U){io.rData := Cat(Fill(24, 0.U), io.dmem.rData(23,16))}
                is("b11".U){io.rData := Cat(Fill(24, 0.U), io.dmem.rData(31,24))}
            }
        }
        is(memRdOpT.LH){
            switch(io.addr(1,1)){ // [TODO] check this syntax ***************************************
                is("b0".U){io.rData := Cat(Fill(16, io.dmem.rData(15)), io.dmem.rData(15,0))}
                is("b1".U){io.rData := Cat(Fill(16, io.dmem.rData(31)), io.dmem.rData(31,16))}
            }
        }
        is(memRdOpT.LHU){
            switch(io.addr(1,1)){ // [TODO] check this syntax ***************************************
                is("b0".U){io.rData := Cat(Fill(16, 0.U), io.dmem.rData(15,0))}
                is("b1".U){io.rData := Cat(Fill(16, 0.U), io.dmem.rData(31,16))}
            }
        }
        is(memRdOpT.LW){io.rData := io.dmem.rData}
    }

    io.dmem.wData := 0.U
    switch(io.wrOp){
        is(memWrOpT.IDLE){io.dmem.wData := 0.U}
        is(memWrOpT.SB){
            switch(io.addr(1,0)){
                is("b00".U){io.dmem.wData := Cat(io.dmem.rData(31,8), io.wData(7,0))}
                is("b01".U){io.dmem.wData := Cat(io.dmem.rData(31,16), io.wData(15,8), io.dmem.rData(7,0))}
                is("b10".U){io.dmem.wData := Cat(io.dmem.rData(31,24), io.wData(23,16), io.dmem.rData(15,0))}
                is("b11".U){io.dmem.wData := Cat(io.wData(31,24), io.dmem.rData(24,0))}
            }
        }
        is(memWrOpT.SH){
            switch(io.addr(1,1)){
                is("b0".U){io.dmem.wData := Cat(io.dmem.rData(31,16), io.wData(15,0))}
                is("b1".U){io.dmem.wData := Cat(io.wData(31,16), io.dmem.rData(15,0))}
            }
        }
        is(memWrOpT.SW){
            io.dmem.wData := io.wData
        }
    }
    
    io.dmem.wrEn := (io.wrOp =/= memWrOpT.IDLE)
}