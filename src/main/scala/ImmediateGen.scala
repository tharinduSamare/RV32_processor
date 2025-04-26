package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class ImmediateGen extends Module {
    val io = IO(new Bundle {
        val instr = Input(UInt(32.W))
        val imme = Output(UInt(32.W))
    })

    val (opcode, opcode_cast) = opcodeT.safe(io.instr(6,0))
    assert(opcode_cast, "Opcode must be a valid one, got 0x%x.", io.instr(6,0))

    val I_imme = Cat(Fill(20, io.instr(31)), io.instr(31,20))
    val S_imme = Cat(Fill(20, io.instr(31)), io.instr(31,25), io.instr(11,7))
    val B_imme = Cat(Fill(20, io.instr(31)), io.instr(7), io.instr(30,25), io.instr(11,8), 0.U)
    val U_imme = Cat(io.instr(31,12), 0.U)
    val J_imme = Cat(Fill(12, io.instr(31)), io.instr(19,12), io.instr(20), io.instr(30,25), io.instr(24,21), 0.U)

    io.imme := 0.U // default case
    switch(opcode){
        is(opcodeT.I_type)  {io.imme := I_imme}
        is(opcodeT.S_type)  {io.imme := S_imme}
        is(opcodeT.B_type)  {io.imme := B_imme}
        is(opcodeT.U_type)  {io.imme := U_imme}
        is(opcodeT.J_type)  {io.imme := J_imme}
        is(opcodeT.AU_type) {io.imme := U_imme} // AUIPC
        is(opcodeT.JR_type) {io.imme := I_imme} // JALR
        is(opcodeT.L_type)  {io.imme := I_imme} // LOAD
    }
}


