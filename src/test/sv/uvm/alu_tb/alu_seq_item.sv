`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;

class alu_seq_item extends uvm_sequence_item;

    rand bit [DATA_WIDTH-1:0]operandA;
    rand bit [DATA_WIDTH-1:0]operandB;
    rand ALUOpT ALUOp;
    bit [DATA_WIDTH-1:0]aluResult;

    `uvm_object_utils_begin(alu_seq_item)
        `uvm_field_int (operandA, UVM_DEFAULT)
        `uvm_field_int (operandB, UVM_DEFAULT)
        `uvm_field_enum (ALUOpT, ALUOp, UVM_DEFAULT)
        `uvm_field_int (aluResult, UVM_DEFAULT)
    `uvm_object_utils_end

    virtual function string convert2str();
        return $sformatf("operandA: 0x%0x, operandB: 0x%0x, operation: %0p, aluResult: 0x%0x", operandA, operandB, ALUOp, aluResult);
    endfunction

    function new(string name = "alu_seq_item");
        super.new(name);
    endfunction   

endclass