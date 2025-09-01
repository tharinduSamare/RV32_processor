package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

import ALUOpT._
import aluOpAMux._
import aluOpBMux._

class BranchCheck extends Module{
    val io = IO(new Bundle{
        val instr = Input(UInt(32.W))
        val PC = Input(UInt(32.W))
        val imme = Input(UInt(32.W))
        val operandA = Input(UInt(32.W))
        val operandB = Input(UInt(32.W))
        val PCSrc = Output(UInt(1.W))
        val PC_JB = Output(UInt(32.W))
    })

    val (opcode, opcode_cast) = opcodeT.safe(io.instr(6,0))
    val (branch_func3, func3_cast) = branchT.safe(io.instr(14,12))
    assert(opcode_cast, "Opcode must be a valid one, got 0x%x.", io.instr(6,0))
    assert(func3_cast, "Opcode must be a valid one, got 0x%x.", io.instr(14,12))

    val branch_condition = Wire(UInt(1.W))

    branch_condition := 0.U
    switch(branch_func3){
        is(branchT.BEQ) {branch_condition := (io.operandA === io.operandB)}
        is(branchT.BNE) {branch_condition := (io.operandA =/= io.operandB)}
        is(branchT.BLT) {branch_condition := (io.operandA.asSInt < io.operandB.asSInt)}
        is(branchT.BGE) {branch_condition := (io.operandA.asSInt >= io.operandB.asSInt)}
        is(branchT.BLTU){branch_condition := (io.operandA.asUInt < io.operandB.asUInt)}
        is(branchT.BGEU){branch_condition := (io.operandA.asUInt >= io.operandB.asUInt)}
    }

    io.PCSrc := ((opcode === opcodeT.J_type) || (opcode === opcodeT.JR_type) || ((opcode === opcodeT.B_type) && (branch_condition === 1.U)))

    io.PC_JB := Mux((opcode === opcodeT.JR_type), (io.operandA + io.imme), 
                Mux((opcode === opcodeT.J_type),  (io.PC + io.imme),
                Mux((opcode === opcodeT.B_type),  (io.PC + io.imme),
                (io.PC + io.imme))))
}

