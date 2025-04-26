package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

import ALUOpT._
import aluOpAMux._
import aluOpBMux._

// -----------------------------------------
// Main Class
// -----------------------------------------

class PipelinedRV32Icore (BinaryFile: String) extends Module {
    val io = IO(new Bundle {
        val check_res = Output(UInt(32.W))
    })

    // Pipeline Registers
    val IFBarrier  = Module(new IFBarrier)
    val IDBarrier  = Module(new IDBarrier)
    val EXBarrier  = Module(new EXBarrier)
    val MEMBarrier = Module(new MEMBarrier)
    val WBBarrier  = Module(new WBBarrier)

    // Pipeline Stages
    val IF  = Module(new IF(BinaryFile))
    val ID  = Module(new ID)
    val EX  = Module(new EX)
    val MEM = Module(new MEM)
    val WB  = Module(new WB)

    val ForwardingUnit_inst = Module(new ForwardingUnit)
    val RegFile_inst = Module(new RegFile)
    val HazardDetectionUnit_inst = Module(new HazardDetectionUnit)
    val ControlUnit_inst = Module(new ControlUnit)

    IF.io.PCWrite   := HazardDetectionUnit_inst.io.pcWrite
    IF.io.PCSrc     := EXBarrier.io.outPCSrc
    IF.io.PC_JB     := EXBarrier.io.outPC_JB

    IFBarrier.io.inInstr  := IF.io.instr
    IFBarrier.io.inPC     := IF.io.pc
    IFBarrier.io.if_stall := HazardDetectionUnit_inst.io.if_stall

    HazardDetectionUnit_inst.io.instr       := IFBarrier.io.outInstr
    HazardDetectionUnit_inst.io.ex_RD       := IDBarrier.io.outRD
    HazardDetectionUnit_inst.io.ex_memRd    := IDBarrier.io.outMemRd
    
    ControlUnit_inst.io.instr := IFBarrier.io.outInstr
    
    ID.io.instr               := IFBarrier.io.outInstr
    ID.io.regFileReq_A        <> RegFile_inst.io.req_1
    ID.io.regFileReq_B        <> RegFile_inst.io.req_2
    ID.io.regFileResp_A       <> RegFile_inst.io.resp_1
    ID.io.regFileResp_B       <> RegFile_inst.io.resp_2
    
    IDBarrier.io.inInstr      := IFBarrier.io.outInstr
    IDBarrier.io.inRS1        := ID.io.rs1
    IDBarrier.io.inRS2        := ID.io.rs2
    IDBarrier.io.inOperandA   := ID.io.operandA
    IDBarrier.io.inOperandB   := ID.io.operandB
    IDBarrier.io.inImme       := ID.io.imme
    IDBarrier.io.inPC         := IFBarrier.io.outPC
    IDBarrier.io.inRD         := ID.io.rd
    IDBarrier.io.inWrEn       := Mux((HazardDetectionUnit_inst.io.id_stall === 1.U), 0.U, ControlUnit_inst.io.wrEn)
    IDBarrier.io.inALUOp      := Mux((HazardDetectionUnit_inst.io.id_stall === 1.U), ALUOpT.invalid, ControlUnit_inst.io.ALUOp)
    IDBarrier.io.inAluSrcA    := Mux((HazardDetectionUnit_inst.io.id_stall === 1.U), aluOpAPCMux.forwardMuxA, ControlUnit_inst.io.ALUSrcA)
    IDBarrier.io.inAluSrcB    := Mux((HazardDetectionUnit_inst.io.id_stall === 1.U), aluOpBImmMux.forwardMuxB, ControlUnit_inst.io.ALUSrcB)
    IDBarrier.io.inMemRd      := Mux((HazardDetectionUnit_inst.io.id_stall === 1.U), memRdOpT.IDLE, ControlUnit_inst.io.memRd)
    IDBarrier.io.inMemWr      := Mux((HazardDetectionUnit_inst.io.id_stall === 1.U), memWrOpT.IDLE, ControlUnit_inst.io.memWr)
    IDBarrier.io.inMemtoReg   := Mux((HazardDetectionUnit_inst.io.id_stall === 1.U), 0.U, ControlUnit_inst.io.memtoReg)

    ForwardingUnit_inst.io.rs1_id   := IDBarrier.io.outRS1
    ForwardingUnit_inst.io.rs2_id   := IDBarrier.io.outRS2
    ForwardingUnit_inst.io.instr   := IDBarrier.io.outInstr
    ForwardingUnit_inst.io.rd_mem   := EXBarrier.io.outRD
    ForwardingUnit_inst.io.wrEn_mem := EXBarrier.io.outWrEn
    ForwardingUnit_inst.io.rd_wb    := MEMBarrier.io.outRD
    ForwardingUnit_inst.io.wrEn_wb  := MEMBarrier.io.outWrEn

    EX.io.ALUOp     := IDBarrier.io.outALUOp
    EX.io.ALUSrcA   := IDBarrier.io.outAluSrcA
    EX.io.ALUSrcB   := IDBarrier.io.outAluSrcB
    EX.io.imme      := IDBarrier.io.outImme
    EX.io.PC        := IDBarrier.io.outPC
    EX.io.instr     := IDBarrier.io.outInstr
    EX.io.operandA  := IDBarrier.io.outOperandA // default case
    EX.io.operandB  := IDBarrier.io.outOperandB // default case
    switch(ForwardingUnit_inst.io.aluOpA_ctrl){
        is(aluOpAMux.opA_id)        {EX.io.operandA := IDBarrier.io.outOperandA}
        is(aluOpAMux.AluResult_mem) {EX.io.operandA := EXBarrier.io.outAluResult}
        is(aluOpAMux.AluResult_wb)  {EX.io.operandA := MEMBarrier.io.outAluResult}
    }
    switch(ForwardingUnit_inst.io.aluOpB_ctrl){
        is(aluOpBMux.opB_id)        {EX.io.operandB := IDBarrier.io.outOperandB}
        is(aluOpBMux.AluResult_mem) {EX.io.operandB := EXBarrier.io.outAluResult}
        is(aluOpBMux.AluResult_wb)  {EX.io.operandB := MEMBarrier.io.outAluResult}
    }

    EXBarrier.io.inAluResult  := EX.io.aluResult
    EXBarrier.io.inRD         := IDBarrier.io.outRD
    EXBarrier.io.inMemWrData  := IDBarrier.io.outOperandB
    EXBarrier.io.inMemRd      := IDBarrier.io.outMemRd
    EXBarrier.io.inMemWr      := IDBarrier.io.outMemWr
    EXBarrier.io.inMemtoReg   := IDBarrier.io.outMemtoReg
    EXBarrier.io.inWrEn       := IDBarrier.io.outWrEn
    EXBarrier.io.inPCSrc      := EX.io.PCSrc
    EXBarrier.io.inPC_JB      := EX.io.PC_JB

    MEM.io.addr         := EXBarrier.io.outAluResult
    MEM.io.memRd        := EXBarrier.io.outMemRd
    MEM.io.memWr        := EXBarrier.io.outMemWr
    MEM.io.writeData    := EXBarrier.io.outMemWrData

    MEMBarrier.io.inAluResult := EXBarrier.io.outAluResult
    MEMBarrier.io.inMemData   := MEM.io.readData
    MEMBarrier.io.inRD        := EXBarrier.io.outRD
    MEMBarrier.io.inWrEn      := EXBarrier.io.outWrEn
    MEMBarrier.io.inMemtoReg  := EXBarrier.io.outMemtoReg

    WB.io.rd            := MEMBarrier.io.outRD
    WB.io.aluResult     := MEMBarrier.io.outAluResult
    WB.io.memData       := MEMBarrier.io.outMemData
    WB.io.memtoReg      := MEMBarrier.io.outMemtoReg
    WB.io.wrEn          := MEMBarrier.io.outWrEn
    WB.io.regFileReq    <> RegFile_inst.io.req_3

    WBBarrier.io.inCheckRes   := WB.io.check_res

    io.check_res              := WBBarrier.io.outCheckRes

}

