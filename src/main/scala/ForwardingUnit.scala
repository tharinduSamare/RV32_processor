package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class ForwardingUnit extends Module {
    val io = IO(new Bundle {
        // What inputs and / or outputs does the ForwardingUnit_inst unit need?

        // from decode stage
        val rs1_id          = Input(UInt(5.W))
        val rs2_id          = Input(UInt(5.W))
        val instr          = Input(UInt(32.W))

        // forwarded signals
        val rd_mem          = Input(UInt(5.W))
        val wrEn_mem        = Input(UInt(1.W))
        val rd_wb           = Input(UInt(5.W))
        val wrEn_wb         = Input(UInt(1.W))

        //  alu input mux controllers
        val aluOpA_ctrl = Output(aluOpAMux())
        val aluOpB_ctrl = Output(aluOpBMux())
    })

    val (opcode, opcode_cast)  = opcodeT.safe(io.instr(6, 0))
    assert(opcode_cast, "Opcode must be a valid one, got 0x%x.", io.instr(6,0))

    /*
        Hazard detetction logic:
    */
    val rs1_mem_hazard = Wire(UInt(1.W))
    val rs2_mem_hazard = Wire(UInt(1.W))
    val rs1_wb_hazard = Wire(UInt(1.W))
    val rs2_wb_hazard = Wire(UInt(1.W))

    val checkRS1 = Wire(UInt(1.W))
    val checkRS2 = Wire(UInt(1.W))

    checkRS1 := (opcode === opcodeT.B_type) || (opcode === opcodeT.I_type) || (opcode === opcodeT.L_type) || (opcode === opcodeT.R_type) || (opcode === opcodeT.S_type)
    checkRS2 := (opcode === opcodeT.B_type) || (opcode === opcodeT.R_type)

    rs1_mem_hazard := ((checkRS1 === 1.U) && (io.rs1_id === io.rd_mem) && (io.wrEn_mem === 1.U))
    rs1_wb_hazard := ((checkRS1 === 1.U) && (io.rs1_id === io.rd_wb) && (io.wrEn_wb === 1.U))
    
    rs2_mem_hazard := ((checkRS2 === 1.U) && (io.rs2_id === io.rd_mem) && (io.wrEn_mem === 1.U))
    rs2_wb_hazard := ((checkRS2 === 1.U) && (io.rs2_id === io.rd_wb) && (io.wrEn_wb === 1.U))

    /*
        Forwarding Selection:
    */
    // operandA mux
    when (rs1_mem_hazard === 1.U){
        io.aluOpA_ctrl := aluOpAMux.AluResult_mem
    }
    .elsewhen(rs1_wb_hazard === 1.U){
        io.aluOpA_ctrl := aluOpAMux.AluResult_wb
    }
    .otherwise{
        io.aluOpA_ctrl := aluOpAMux.opA_id
    }

    // operandB mux
    when (rs2_mem_hazard === 1.U){
        io.aluOpB_ctrl := aluOpBMux.AluResult_mem
    }
    .elsewhen(rs2_wb_hazard === 1.U){
        io.aluOpB_ctrl := aluOpBMux.AluResult_wb
    }
    .otherwise{
        io.aluOpB_ctrl := aluOpBMux.opB_id
    }

}