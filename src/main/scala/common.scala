package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

object ALUOpT extends ChiselEnum {

  val isADD   = Value(0x01.U)
  val isSUB   = Value(0x02.U)
  val isOR    = Value(0x04.U)
  val isSLL   = Value(0x06.U)
  val isSRA   = Value(0x08.U)
  val isPASSB = Value(0x0B.U) // aluResult = operandB

  val invalid = Value(0xFF.U)
}


object opcodeT extends  ChiselEnum {
    val L_type  = Value("b0000011".U) // LOAD (I_type)
    val I_type  = Value("b0010011".U)
    val AU_type = Value("b0010111".U) // AUIPC (U_type)
    val S_type  = Value("b0100011".U)
    val R_type  = Value("b0110011".U)
    val U_type  = Value("b0110111".U) // LUI
    val B_type  = Value("b1100011".U)
    val JR_type = Value("b1100111".U) // JALR (I_type)
    val J_type  = Value("b1101111".U)
    val unimp   = Value("b1110011".U) // Unimplemented (finish of simulation)
}

object branchT extends ChiselEnum {
    val BEQ  = Value("b000".U)
    val BNE  = Value("b001".U)
    val BGEU = Value("b111".U) // Keep this even BGEU is not implemented. Otherwise, compiler will use only 1 bit to represent BEQ & BNE
}

object aluOpAMux extends  ChiselEnum { // ForwardingUnit_inst mux for ALU opB
    val opA_id, AluResult_mem, AluResult_wb = Value
}
object aluOpBMux extends  ChiselEnum { // ForwardingUnit_inst mux for ALU opA
    val opB_id, AluResult_mem, AluResult_wb = Value
}
object aluOpBImmMux extends ChiselEnum {
    val forwardMuxB, imme, plus4 = Value
}

object aluOpAPCMux extends ChiselEnum {
    val forwardMuxA, PC = Value
}

object memWrOpT extends  ChiselEnum {
    val SW, IDLE = Value
}

object memRdOpT extends ChiselEnum {
    val LW, IDLE = Value
}