package core_tile

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

import ALUOpT._
import aluOpAMux._
import aluOpBMux._

// -----------------------------------------
// IF-Barrier
// -----------------------------------------

class IFBarrier extends Module {
    val io = IO(new Bundle {
        val if_stall = Input(UInt(1.W))
        val inInstr  = Input(UInt(32.W))
        val inPC     = Input(UInt(32.W))
        val outInstr = Output(UInt(32.W))
        val outPC    = Output(UInt(32.W))
    })

    val instrReg = RegInit(0x00000013.U(32.W)) // ADDI x0, x0, 0
    val pcReg = RegInit(0.U(32.W))

    when(io.if_stall === 0.U){
        instrReg := io.inInstr
        pcReg    := io.inPC
    }

    io.outInstr := instrReg
    io.outPC    := pcReg

}


// -----------------------------------------
// ID-Barrier
// -----------------------------------------

class IDBarrier extends Module {
    val io = IO(new Bundle {
        val inALUOp     = Input(ALUOpT())
        val inInstr     = Input(UInt(32.W))
        val inRS1       = Input(UInt(5.W))
        val inRS2       = Input(UInt(5.W))
        val inOperandA  = Input(UInt(32.W))
        val inOperandB  = Input(UInt(32.W))
        val inImme      = Input(UInt(32.W))
        val inPC        = Input(UInt(32.W))
        val inAluSrcB   = Input(aluOpBImmMux())
        val inAluSrcA   = Input(aluOpAPCMux())
        val inMemRd     = Input(memRdOpT())
        val inMemWr     = Input(memWrOpT())
        val inMemtoReg  = Input(UInt(1.W))
        val inRD        = Input(UInt(5.W))
        val inWrEn      = Input(UInt(1.W))
        val outALUOp    = Output(ALUOpT())
        val outInstr    = Output(UInt(32.W))
        val outRS1      = Output(UInt(5.W))
        val outRS2      = Output(UInt(5.W))
        val outOperandA = Output(UInt(32.W))
        val outOperandB = Output(UInt(32.W))
        val outImme     = Output(UInt(32.W))
        val outPC       = Output(UInt(32.W))
        val outAluSrcA  = Output(aluOpAPCMux())
        val outAluSrcB  = Output(aluOpBImmMux())
        val outMemRd    = Output(memRdOpT())
        val outMemWr    = Output(memWrOpT())
        val outMemtoReg = Output(UInt(1.W))
        val outRD       = Output(UInt(5.W))
        val outWrEn     = Output(UInt(1.W))
    })

    io.outALUOp := RegNext(io.inALUOp, ALUOpT.invalid)
    io.outInstr := RegNext(io.inInstr, 0x00000013.U) // ADDI x0, x0, 0
    io.outRD  := RegNext(io.inRD, 0.U)
    io.outRS1 := RegNext(io.inRS1, 0.U)
    io.outRS2 := RegNext(io.inRS2, 0.U)
    io.outOperandA := RegNext(io.inOperandA, 0.U)
    io.outOperandB := RegNext(io.inOperandB, 0.U)
    io.outImme  := RegNext(io.inImme, 0.U)
    io.outPC    := RegNext(io.inPC, 0.U)
    io.outAluSrcA:= RegNext(io.inAluSrcA, aluOpAPCMux.forwardMuxA)
    io.outAluSrcB:= RegNext(io.inAluSrcB, aluOpBImmMux.forwardMuxB)
    io.outWrEn  := RegNext(io.inWrEn, 0.U)
    io.outMemRd := RegNext(io.inMemRd, memRdOpT.IDLE)
    io.outMemWr := RegNext(io.inMemWr, memWrOpT.IDLE)
    io.outMemtoReg := RegNext(io.inMemtoReg, 0.U)
}


// -----------------------------------------
// EX-Barrier
// -----------------------------------------

class EXBarrier extends Module {
    val io = IO(new Bundle {
        val inAluResult  = Input(UInt(32.W))
        val inRD         = Input(UInt(5.W))
        val inMemWrData  = Input(UInt(32.W))
        val inMemRd      = Input(memRdOpT())
        val inMemWr      = Input(memWrOpT())
        val inMemtoReg   = Input(UInt(1.W))
        val inWrEn       = Input(UInt(1.W))
        val inPCSrc      = Input(UInt(1.W))
        val inPC_JB      = Input(UInt(32.W))
        val outAluResult = Output(UInt(32.W))
        val outRD        = Output(UInt(5.W))
        val outMemWrData = Output(UInt(32.W))
        val outMemRd     = Output(memRdOpT())
        val outMemWr     = Output(memWrOpT())
        val outMemtoReg  = Output(UInt(1.W))
        val outWrEn      = Output(UInt(1.W))
        val outPCSrc     = Output(UInt(1.W))
        val outPC_JB     = Output(UInt(32.W))
    })

    io.outAluResult := RegNext(io.inAluResult, 0.U)
    io.outRD        := RegNext(io.inRD, 0.U)
    io.outMemWrData := RegNext(io.inMemWrData, 0.U)
    io.outMemRd     := RegNext(io.inMemRd, memRdOpT.IDLE)
    io.outMemWr     := RegNext(io.inMemWr, memWrOpT.IDLE)
    io.outMemtoReg  := RegNext(io.inMemtoReg, 0.U)
    io.outWrEn      := RegNext(io.inWrEn, 0.U)
    io.outPCSrc     := RegNext(io.inPCSrc, 0.U)
    io.outPC_JB     := RegNext(io.inPC_JB, 0.U)
}


// -----------------------------------------
// MEM-Barrier
// -----------------------------------------

class MEMBarrier extends Module {
    val io = IO(new Bundle {
        val inAluResult  = Input(UInt(32.W))
        val inMemData    = Input(UInt(32.W))
        val inMemtoReg   = Input(UInt(1.W))
        val inRD         = Input(UInt(5.W))
        val inWrEn       = Input(UInt(1.W))
        val outAluResult = Output(UInt(32.W))
        val outMemData   = Output(UInt(32.W))
        val outMemtoReg  = Output(UInt(1.W))
        val outRD        = Output(UInt(5.W))
        val outWrEn      = Output(UInt(1.W))
    })

    io.outAluResult := RegNext(io.inAluResult, 0.U)
    io.outMemData   := RegNext(io.inMemData, 0.U)
    io.outMemtoReg  := RegNext(io.inMemtoReg, 0.U)
    io.outRD        := RegNext(io.inRD, 0.U)
    io.outWrEn      := RegNext(io.inWrEn, 0.U)

}


// -----------------------------------------
// WB-Barrier
// -----------------------------------------

class WBBarrier extends Module {
    val io = IO(new Bundle {
        val inCheckRes   = Input(UInt(32.W))
        val outCheckRes  = Output(UInt(32.W))
    })

    val check_res   = RegInit(0.U(32.W))

    io.outCheckRes := check_res
    check_res      := io.inCheckRes
}

