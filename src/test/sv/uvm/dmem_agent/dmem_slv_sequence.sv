`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;

class dmem_slv_sequence extends uvm_sequence;
    `uvm_object_utils(dmem_slv_sequence)

    rand bit[DATA_WIDTH-1:0]dmem[0:DMEM_DEPTH-1]; //dmem as an associative array
    string dmem_init_file;

    function new(string name = "dmem_slv_sequence");
        super.new(name);
    endfunction

    task pre_body();
        if(!uvm_config_db#(string)::get(m_sequencer, "", "dmem_init_file", dmem_init_file)) begin
            dmem_init_file = DMEM_INIT_FILE;
            `uvm_warning(get_type_name(), $sformatf("Config_db did not have DMEM_INIT_FILE; using default %s", DMEM_INIT_FILE))
        end
        else begin
            `uvm_info(get_type_name(), $sformatf("Initialize dmem with : %0s", dmem_init_file), UVM_DEFAULT)
        end
        $readmemh(dmem_init_file, dmem);
    endtask

    task body();
        dmem_slv_seq_item req, rsp;
        req = dmem_slv_seq_item::type_id::create("req");
        rsp = dmem_slv_seq_item::type_id::create("rsp");

        forever begin // slave sequence runs forever
            start_item(req);
            `uvm_info(get_type_name(), "Imem req generated.", UVM_DEBUG)
            finish_item(req);

            start_item(rsp);
            `uvm_info(get_type_name(), "Imem rsp generated.", UVM_DEBUG)
            rsp.copy(req);
            if(!rsp.wrEn) begin
                assert(rsp.randomize() with {rsp.data_out == dmem[rsp.addr];}); // rsp.data_out = dmem[rsp.addr] would be enough in this case
            end
            else begin
                dmem[rsp.addr] = rsp.data_in;
            end
            `uvm_info(get_type_name(), $sformatf("dmem response: addr: 0x%0x, wrEn: %0b, data_in: 0x%0x, data_out: 0x%0x", rsp.addr, rsp.wrEn, rsp.data_in, rsp.data_out), UVM_MEDIUM)
            finish_item(rsp);
        end
    endtask

    task post_body();

    endtask
endclass