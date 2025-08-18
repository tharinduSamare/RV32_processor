package core_tile

import chisel3._
import chisel3.util._
import scala.annotation.switch

import csrT._

class CSR extends Module{

    val io = IO(new Bundle{
        val wr_en = Input(UInt(1.W))
        val addr = Input(csrT())
        val data_in = Input(UInt(32.W))
        val data_out = Output(UInt(32.W))
    })

    // Supervisor Trap Setup
    val stvec = RegInit(0.U(32.W))

    // Machine Information Registers
    val mhartid = RegInit(0.U(32.W))

    // Machine Trap Setup
    val mstatus = RegInit(0.U(32.W))
    val misa    = RegInit(0.U(32.W))
    val medeleg = RegInit(0.U(32.W))
    val mideleg = RegInit(0.U(32.W))
    val mie     = RegInit(0.U(32.W))
    val mtvec   = RegInit(0.U(32.W))

    // Machine Trap Handling
    val mepc    = RegInit(0.U(32.W))
    val mcause  = RegInit(0.U(32.W))

    // Machine Memory Protection
    val pmpcfg0 = RegInit(0.U(32.W))

    when(io.wr_en === 1.U){
        switch(io.addr){
            is(is_stvec)    {stvec   := io.data_in}
            is(is_mhartid)  {mhartid := io.data_in}
            is(is_mstatus)  {mstatus := io.data_in}
            is(is_misa)     {misa    := io.data_in}
            is(is_medeleg)  {medeleg := io.data_in}
            is(is_mideleg)  {mideleg := io.data_in}
            is(is_mie)      {mie     := io.data_in}
            is(is_mtvec)    {mtvec   := io.data_in}
            is(is_mepc)     {mepc    := io.data_in}
            is(is_mcause)   {mcause  := io.data_in}
            is(is_pmpcfg0)  {pmpcfg0 := io.data_in}
        }
    }
    
    switch(io.addr){
        is(is_stvec)    {io.data_out := stvec   }
        is(is_mhartid)  {io.data_out := mhartid }
        is(is_mstatus)  {io.data_out := mstatus }
        is(is_misa)     {io.data_out := misa    }
        is(is_medeleg)  {io.data_out := medeleg }
        is(is_mideleg)  {io.data_out := mideleg }
        is(is_mie)      {io.data_out := mie     }
        is(is_mtvec)    {io.data_out := mtvec   }
        is(is_mepc)     {io.data_out := mepc    }
        is(is_mcause)   {io.data_out := mcause  }
        is(is_pmpcfg0)  {io.data_out := pmpcfg0 }
    }

}