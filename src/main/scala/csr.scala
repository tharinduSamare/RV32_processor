package core_tile

import chisel3._
import chisel3.util._
import scala.annotation.switch

import csrT._

class CSR extends Module{

    val io = IO(new Bundle{
        val wr_en = Input(UInt(1.W))
        val addr = Input(csrT)
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

    when(wr_en){
        switch(addr){
            is(is_stvec)    {stvec   := data_in}
            is(is_mhartid)  {mhartid := data_in}
            is(is_mstatus)  {mstatus := data_in}
            is(is_misa)     {misa    := data_in}
            is(is_medeleg)  {medeleg := data_in}
            is(is_mideleg)  {mideleg := data_in}
            is(is_mie)      {mie     := data_in}
            is(is_mtvec)    {mtvec   := data_in}
            is(is_mepc)     {mepc    := data_in}
            is(is_mcause)   {mcause  := data_in}
            is(is_pmpcfg0)  {pmpcfg0 := data_in}
        }
    }
    
    switch(addr){
        is(is_stvec)    {data_out := stvec   }
        is(is_mhartid)  {data_out := mhartid }
        is(is_mstatus)  {data_out := mstatus }
        is(is_misa)     {data_out := misa    }
        is(is_medeleg)  {data_out := medeleg }
        is(is_mideleg)  {data_out := mideleg }
        is(is_mie)      {data_out := mie     }
        is(is_mtvec)    {data_out := mtvec   }
        is(is_mepc)     {data_out := mepc    }
        is(is_mcause)   {data_out := mcause  }
        is(is_pmpcfg0)  {data_out := pmpcfg0 }
    }

}