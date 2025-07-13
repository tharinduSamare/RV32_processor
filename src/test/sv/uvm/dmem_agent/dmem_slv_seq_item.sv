`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;

class dmem_slv_seq_item extends uvm_sequence_item;
    bit [ADDR_WIDTH-1:0] addr; // input (non-rand)
    bit [DATA_WIDTH-1:0] data_in; // input (non-rand)
    rand bit [DATA_WIDTH-1:0] data_out; // output (rand)
    bit wrEn;

    `uvm_object_utils_begin (dmem_slv_seq_item)
        `uvm_field_int(addr, UVM_DEFAULT)
        `uvm_field_int(data_in, UVM_DEFAULT)
        `uvm_field_int(data_out, UVM_DEFAULT)
        `uvm_field_int(wrEn, UVM_DEFAULT)
    `uvm_object_utils_end

    function new (string name = "dmem_slv_seq_item");
        super.new(name);
    endfunction

    virtual function string convert2str();
        return $sformatf("addr: 0x%0x, data_in: 0x%0x, data_out: 0x%0x wrEn: 0x%0x", addr, data_in, data_out, wrEn);
    endfunction



endclass