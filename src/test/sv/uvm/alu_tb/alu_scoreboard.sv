`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;

class alu_scoreboard extends uvm_scoreboard;
    `uvm_component_utils(alu_scoreboard)

    function new(string name = "alu_scoreboard", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    uvm_analysis_imp #(alu_seq_item, alu_scoreboard) m_analysis_imp;

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);
        m_analysis_imp = new("m_analysis_imp", this);
    endfunction

    virtual function void write(alu_seq_item item);
        ALUOpT ALUOp;
        bit signed [DATA_WIDTH-1:0]operandA, operandB, aluResult;
        ALUOp = item.ALUOp;
        operandA = item.operandA;
        operandB = item.operandB;

        case (ALUOp)
            isADD  : aluResult = operandA + operandB;
            isSUB  : aluResult = operandA - operandB;
            isXOR  : aluResult = operandA ^ operandB;
            isOR   : aluResult = operandA | operandB;
            isAND  : aluResult = operandA & operandB;
            isSLL  : aluResult = operandA << operandB[4:0];
            isSRL  : aluResult = operandA >> operandB[4:0];
            isSRA  : aluResult = operandA >>> operandB[4:0];
            isSLT  : aluResult = (operandA < operandB)? 1'b1 : 1'b0;
            isSLTU : aluResult = (unsigned'(operandA) < unsigned'(operandB))? 1'b1 : 1'b0;
            isPASSB: aluResult = operandB;
            default: aluResult = 32'hFFFF_FFFF;
        endcase
        if(aluResult != item.aluResult) begin
            `uvm_error(get_type_name(), $sformatf("DUT result: 0x%0x, expected result: 0x%0x, operation: %0p, opA: 0x%0x, opB: 0x%0x", item.aluResult, aluResult, ALUOp, operandA, operandB))
        end
        else begin
            `uvm_info(get_type_name(), $sformatf("Pass: DUT result: 0x%0x, expected result: 0x%0x, operation: %0p, opA: 0x%0x, opB: 0x%0x", item.aluResult, aluResult, ALUOp, operandA, operandB), UVM_LOW)
        end
    endfunction

endclass