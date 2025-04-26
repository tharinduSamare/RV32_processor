package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

import ALUOpT._
import aluOpAMux._
import aluOpBMux._


// -----------------------------------------
// Fetch Stage
// -----------------------------------------

class IF (BinaryFile: String) extends Module {
    val io = IO(new Bundle {
        val instr = Output(UInt(32.W))
        val pc = Output(UInt(32.W))
        val PC_JB = Input(UInt(32.W))
        val PCSrc = Input(UInt(1.W))
        val PCWrite = Input(UInt(1.W))
    })

    val IMem = Mem(4096, UInt(32.W))
    loadMemoryFromFile(IMem, BinaryFile)

    val PC = RegInit(0.U(32.W))
    
    io.instr := IMem(PC>>2.U)
    io.pc := PC

    // Update PC
    when(io.PCWrite === 1.U){
        PC := Mux((io.PCSrc === 0.U), (PC+4.U), io.PC_JB)
    }
}


// -----------------------------------------
// Decode Stage
// -----------------------------------------

class ID extends Module {
    val io = IO(new Bundle {
        val regFileReq_A  = Flipped(new regFileReadReq) 
        val regFileResp_A = Flipped(new regFileReadResp) 
        val regFileReq_B  = Flipped(new regFileReadReq) 
        val regFileResp_B = Flipped(new regFileReadResp) 
        val instr         = Input(UInt(32.W))
        val rd            = Output(UInt(5.W))
        val rs1           = Output(UInt(5.W))
        val rs2           = Output(UInt(5.W))
        val imme          = Output(UInt(32.W))
        val operandA      = Output(UInt(32.W))
        val operandB      = Output(UInt(32.W))
    })

    val opcode  = io.instr(6, 0)
    io.rd      := io.instr(11, 7)
    val funct3  = io.instr(14, 12)
    val rs1     = io.instr(19, 15)

    // R-Type
    val funct7  = io.instr(31, 25)
    val rs2     = io.instr(24, 20)

    val immediateGen_inst = Module(new ImmediateGen)

    immediateGen_inst.io.instr := io.instr
    io.imme := immediateGen_inst.io.imme

    // Operands
    io.regFileReq_A.addr := rs1
    io.regFileReq_B.addr := rs2

    io.operandA := io.regFileResp_A.data
    io.operandB := io.regFileResp_B.data

    io.rs1     := rs1
    io.rs2     := rs2
}

// -----------------------------------------
// Execute Stage
// -----------------------------------------

class EX extends Module {
    val io = IO(new Bundle {
        val ALUOp     = Input(ALUOpT())
        val ALUSrcA   = Input(aluOpAPCMux())
        val ALUSrcB   = Input(aluOpBImmMux())
        val operandA  = Input(UInt(32.W))
        val operandB  = Input(UInt(32.W))
        val imme      = Input(UInt(32.W))
        val PC        = Input(UInt(32.W))
        val instr     = Input(UInt(32.W))
        val aluResult = Output(UInt(32.W))
        val PCSrc     = Output(UInt(1.W))
        val PC_JB     = Output(UInt(32.W))
    })

    val aluOpA = Mux((io.ALUSrcA === aluOpAPCMux.PC), io.PC, io.operandA)
    val aluOpB = Mux((io.ALUSrcB === aluOpBImmMux.imme), io.imme, Mux((io.ALUSrcB === aluOpBImmMux.forwardMuxB), io.operandB, 4.U))
    val ALUOp      = io.ALUOp

    val alu = Module (new ALU)
    alu.io.ALUOp := io.ALUOp
    alu.io.operandA := aluOpA
    alu.io.operandB := aluOpB
    io.aluResult := alu.io.aluResult

    val BranchCheck_inst = Module (new BranchCheck)
    BranchCheck_inst.io.instr       := io.instr
    BranchCheck_inst.io.PC          := io.PC
    BranchCheck_inst.io.imme        := io.imme
    BranchCheck_inst.io.operandA    := io.operandA
    BranchCheck_inst.io.operandB    := io.operandB
    io.PCSrc := BranchCheck_inst.io.PCSrc
    io.PC_JB := BranchCheck_inst.io.PC_JB
}

// -----------------------------------------
// Memory Stage
// -----------------------------------------

class MEM extends Module {
    val io = IO(new Bundle {
        val addr = Input(UInt(32.W))
        val writeData = Input(UInt(32.W))
        val memRd = Input(memRdOpT())
        val memWr = Input(memWrOpT())
        val readData = Output(UInt(32.W))
    })

    val DMEM_inst = Module(new DMEM(DEPTH = 4096))
    val MemController_inst = Module(new MemController)

    MemController_inst.io.addr := io.addr
    MemController_inst.io.wrOp := io.memWr
    MemController_inst.io.wData := io.writeData
    MemController_inst.io.rdOp := io.memRd
    MemController_inst.io.mem_rData := DMEM_inst.io.rData
    io.readData := MemController_inst.io.rData

    DMEM_inst.io.addr := MemController_inst.io.mem_addr
    DMEM_inst.io.wData := MemController_inst.io.mem_wData
    DMEM_inst.io.wrEn := MemController_inst.io.mem_wrEn

}

// -----------------------------------------
// Writeback Stage
// -----------------------------------------

class WB extends Module {
    val io = IO(new Bundle {
        val regFileReq = Flipped(new regFileWriteReq) 
        val rd         = Input(UInt(5.W))
        val aluResult  = Input(UInt(32.W))
        val memData    = Input(UInt(32.W))
        val memtoReg   = Input(UInt(1.W))
        val wrEn      = Input(UInt(1.W))
        val check_res  = Output(UInt(32.W))
    })

    io.regFileReq.addr  := io.rd
    io.regFileReq.data  := Mux((io.memtoReg === 1.U), io.memData, io.aluResult)
    io.regFileReq.wr_en := io.wrEn
    //  io.regFileReq.wr_en := io.aluResult =/= "h_FFFF_FFFF".U  // could depend on the current ALUOpT, if ISA is extendet beyond R-type and I-type instructions

    io.check_res := io.aluResult

}
