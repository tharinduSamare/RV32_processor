package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

import ALUOpT._
import aluOpAMux._
import aluOpBMux._

class ALU extends Module {
    val io = IO(new Bundle {
        val ALUOp = Input(ALUOpT())
        val operandA = Input(UInt(32.W))
        val operandB = Input(UInt(32.W))
        val aluResult = Output(UInt(32.W))
    })

    when(io.ALUOp === isADD)       {io.aluResult := io.operandA + io.operandB}
    .elsewhen(io.ALUOp === isSUB)  {io.aluResult := io.operandA - io.operandB}
    .elsewhen(io.ALUOp === isXOR)  {io.aluResult := io.operandA ^ io.operandB}
    .elsewhen(io.ALUOp === isOR)   {io.aluResult := io.operandA | io.operandB}
    .elsewhen(io.ALUOp === isAND)  {io.aluResult := io.operandA & io.operandB}
    .elsewhen(io.ALUOp === isSLL)  {io.aluResult := io.operandA << io.operandB(4, 0)}
    .elsewhen(io.ALUOp === isSRL)  {io.aluResult := io.operandA >> io.operandB(4, 0)}
    .elsewhen(io.ALUOp === isSRA)  {io.aluResult := io.operandA >> io.operandB(4, 0)}          // automatic sign extension, if SInt datatype is use
    .elsewhen(io.ALUOp === isSLT)  {io.aluResult := Mux(io.operandA < io.operandB, 1.U, 0.U)}  // automatic sign extension, if SInt datatype is use
    .elsewhen(io.ALUOp === isSLTU) {io.aluResult := Mux(io.operandA < io.operandB, 1.U, 0.U)}
    .elsewhen(io.ALUOp === isPASSB){io.aluResult := io.operandB} // pass immediate value (OperandB) to result
    .otherwise                     {io.aluResult := "h_FFFF_FFFF".U} // = 2^32 - 1; self-defined encoding for invalid operation, value is unlikely to be reached in a regular arithmetic operation

    val zero = (io.operandA === io.operandB) // this is not being used. Brach logic is in the decode stage as a seperate module called "BranchCheck"

}

