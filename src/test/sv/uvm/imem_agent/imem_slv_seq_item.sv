`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;

class imem_slv_seq_item extends uvm_sequence_item;
    bit [PC_WIDTH-1:0] pc; // input (non-rand)
    rand bit [INSTR_WIDTH-1:0] instr; // output (rand)

    `uvm_object_utils_begin (imem_slv_seq_item)
        `uvm_field_int(pc, UVM_DEFAULT)
        `uvm_field_int(instr, UVM_DEFAULT)
    `uvm_object_utils_end

    function new (string name = "imem_slv_seq_item");
        super.new(name);
    endfunction

    virtual function string convert2str();
        return $sformatf("PC: 0x%0x, Instruction: 0x%0x", pc, instr);
    endfunction



endclass