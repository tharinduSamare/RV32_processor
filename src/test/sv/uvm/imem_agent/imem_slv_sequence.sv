`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;

class imem_slv_sequence extends uvm_sequence;
    `uvm_object_utils(imem_slv_sequence)

    rand bit[INSTR_WIDTH-1:0]imem[0:IMEM_DEPTH-1];
    string imem_init_file;
    bit loaded = 0;

    function new(string name = "imem_slv_sequence");
        super.new(name);
    endfunction

    task pre_body();
        if(!uvm_config_db#(string)::get(m_sequencer, "", "imem_init_file", imem_init_file)) begin
            imem_init_file = IMEM_INIT_FILE;
            `uvm_warning(get_type_name(), $sformatf("Config_db did not have IMEM_INIT_FILE; using default %s", imem_init_file))
        end
        else begin
            `uvm_info(get_type_name(), $sformatf("Initialize imem with : %0s", imem_init_file), UVM_DEFAULT)
        end

        $readmemh(imem_init_file, imem);

        // Print first few instructions in imem
        for(int i=0; i<10; i++)begin
            `uvm_info(get_type_name(), $sformatf("imem[%0d]: 0x%0x", i, imem[i]), UVM_DEBUG)
        end
    endtask

    task body();
        imem_slv_seq_item req, rsp;
        req = imem_slv_seq_item::type_id::create("req");
        rsp = imem_slv_seq_item::type_id::create("rsp");

        forever begin // slave sequence runs forever
            start_item(req);
            `uvm_info(get_type_name(), "Imem req generated.", UVM_DEBUG)
            finish_item(req);
            `uvm_info(get_type_name(), $sformatf("seq: pc 0x%0x", req.pc), UVM_DEBUG)

            start_item(rsp);
            `uvm_info(get_type_name(), "Imem rsp generated.", UVM_DEBUG)
            rsp.copy(req);
            assert(rsp.randomize() with {rsp.instr == imem[rsp.pc>>2];}); // rsp.inst = imem[rsp.pc] would be enough in this case
            `uvm_info(get_type_name(), $sformatf("Imem response: PC: 0x%0x, Ins: 0x%0x", rsp.pc, rsp.instr), UVM_DEBUG)
            finish_item(rsp);
        end
    endtask

    task post_body();

    endtask
endclass