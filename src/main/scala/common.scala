package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

object ALUOpT extends ChiselEnum {

  val isADD   = Value(0x01.U)
  val isSUB   = Value(0x02.U)
  val isXOR   = Value(0x03.U)
  val isOR    = Value(0x04.U)
  val isAND   = Value(0x05.U)
  val isSLL   = Value(0x06.U)
  val isSRL   = Value(0x07.U)
  val isSRA   = Value(0x08.U)
  val isSLT   = Value(0x09.U)
  val isSLTU  = Value(0x0A.U)
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
    val INV1 = Value("b010".U) // invalid value for branch opcode
    val INV2 = Value("b011".U) // invalid value for branch opcode
    val BLT  = Value("b100".U)
    val BGE  = Value("b101".U)
    val BLTU = Value("b110".U)
    val BGEU = Value("b111".U)
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
    val SB, SH, SW, IDLE = Value
}

object memRdOpT extends ChiselEnum {
    val LB, LH, LW, LBU, LHU, IDLE = Value
}

object csrT extends ChiselEnum {
    val is_stvec   = Value(0x105.U)

    val is_mhartid = Value(0xF14.U)

    val is_mstatus = Value(0x300.U)
    val is_misa    = Value(0x301.U)
    val is_medeleg = Value(0x302.U)
    val is_mideleg = Value(0x303.U)
    val is_mie     = Value(0x304.U)
    val is_mtvec   = Value(0x305.U)

    val is_mepc    = Value(0x341.U)
    val is_mcause  = Value(0x342.U)

    val is_pmpcfg0 = Value(0x3A0.U)
}