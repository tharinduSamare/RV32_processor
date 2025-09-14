// ADS I Class Project
// Pipelined RISC-V Core with Hazard Detetcion and Resolution
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/21/2024 by Andro Mazmishvili (@Andrew8846)

package PipelinedRV32I

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

import core_tile._

class PipelinedRV32I (BinaryFile: String) extends Module {

  val io     = IO(new Bundle {
    val result = Output(UInt(32.W)) 
    val coreDone = Output(UInt(1.W)) // unimp instruction has occured 
    val gpRegVal = Output(UInt(32.W)) // gp (x3) reg contains the riscv-tests pass fail status
  })
  
  val core   = Module(new PipelinedRV32Icore)
  val IMem = Mem(4096, UInt(32.W))
  val DMem = Module(new DMEM(4096))
  loadMemoryFromFile(IMem, BinaryFile)

  io.result   := core.io.check_res
  io.coreDone := core.io.coreDone
  io.gpRegVal := core.io.gpRegVal
  core.io.imem.instr := IMem(core.io.imem.PC>>2.U)

  core.io.dmem <> DMem.io


}

class DMEM (DEPTH: Int = 4096) extends Module {
    val io = IO(new DMEM_IO)

    val mem = Mem(DEPTH, UInt(32.W))

    when(io.wrEn === 1.U){
        mem(io.addr) := io.wData
    }
    io.rData := mem(io.addr)
}

