`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;

class imem_slv_sequence extends uvm_sequence;
    `uvm_object_utils(imem_slv_sequence)

    rand bit[31:0]imem[0:IMEM_DEPTH-1];

    function new(string name = "imem_slv_sequence");
        super.new(name);
    endfunction

    task pre_body();
        `uvm_info(get_type_name(), $sformatf("Loading imem_init_file: %0s", IMEM_INIT_FILE), UVM_DEFAULT)
        $readmemh(IMEM_INIT_FILE, imem);
    endtask

    task body();
        imem_slv_seq_item req, rsp;
        req = imem_slv_seq_item::type_id::create("req");
        rsp = imem_slv_seq_item::type_id::create("rsp");

        forever begin // slave sequence runs forever
            start_item(req);
            `uvm_info(get_type_name(), "Imem req generated.", UVM_DEBUG)
            finish_item(req);

            start_item(rsp);
            `uvm_info(get_type_name(), "Imem rsp generated.", UVM_DEBUG)
            rsp.copy(req);
            assert(rsp.randomize() with {rsp.instr == imem[rsp.pc>>2];}); // rsp.inst = imem[rsp.pc] would be enough in this case
            `uvm_info(get_type_name(), $sformatf("Imem response: PC: 0x%0x, Ins: 0x%0x", rsp.pc, rsp.instr), UVM_MEDIUM)
            finish_item(rsp);
        end
    endtask

    task post_body();

    endtask
endclass