package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class HazardDetectionUnit extends  Module {
    val io = IO(new Bundle {
        val instr = Input(UInt(32.W))
        val ex_RD = Input(UInt(32.W))
        val ex_memRd = Input(memRdOpT())
        val id_stall = Output(UInt(1.W))
        val if_stall = Output(UInt(1.W))
        val pcWrite = Output(UInt(1.W))
        val coreDone = Output(UInt(1.W)) // unImp instruction has occured 
    })

    val (opcode, opcode_cast) = opcodeT.safe(io.instr(6,0))
    assert(opcode_cast, "Opcode must be a valid one, got 0x%x.\n", io.instr(6,0))

    val id_rs1 = io.instr(19,15)
    val id_rs2 = io.instr(24,20)

    val check_rs1 = Wire(UInt(1.W))
    when((opcode === opcodeT.R_type) || (opcode === opcodeT.I_type) || (opcode === opcodeT.S_type) || (opcode === opcodeT.B_type) || (opcode === opcodeT.L_type) || (opcode === opcodeT.JR_type)){check_rs1 := 1.U}
    .otherwise{check_rs1 := 0.U}
    
    val check_rs2 = Wire(UInt(1.W))
    when((opcode === opcodeT.R_type) || (opcode === opcodeT.S_type) || (opcode === opcodeT.B_type)){check_rs2 := 1.U}
    .otherwise{check_rs2 := 0.U}

    when((io.ex_memRd =/= memRdOpT.IDLE) && (((check_rs1 === 1.U) && (io.ex_RD === id_rs1)) || ((check_rs2 === 1.U) && (io.ex_RD === id_rs2)))){
        io.if_stall := 1.U
        io.id_stall := 1.U
        io.pcWrite := 0.U
    }
    .otherwise{
        io.if_stall := 0.U
        io.id_stall := 0.U
        io.pcWrite := 1.U
    }

    // test finish condition
    val coreDone = RegInit(0.U(1.W))
    coreDone := 0.U
    when(opcode === opcodeT.unimp){
        printf("Unimplemented opcode (0x73): end of test\n")
        coreDone := 1.U
    }
    io.coreDone := coreDone
}
