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
        dmem_slv_seq_item req, rsp, req_w, rsp_w;
        req = dmem_slv_seq_item::type_id::create("req");
        rsp = dmem_slv_seq_item::type_id::create("rsp");
        req_w = dmem_slv_seq_item::type_id::create("req_w");
        rsp_w = dmem_slv_seq_item::type_id::create("rsp_w");

        // memory read can be done in one req-rsp pair
        // memory write need two req-rsp pairs. First pair reads the current data word. DUT takes it and updates the necessary bytes. Second req-rsp pair writes the updated word to memory.
        forever begin // slave sequence runs forever
            start_item(req);
            `uvm_info(get_type_name(), "Dmem req generated.", UVM_DEBUG)
            finish_item(req);
            `uvm_info(get_type_name(), $sformatf("dmem request_r: addr: 0x%0x, wrEn: %0b, data_in: 0x%0x", req.addr, req.wrEn, req.data_in), UVM_DEBUG)

            start_item(rsp);
            `uvm_info(get_type_name(), "Dmem rsp generated.", UVM_DEBUG)
            rsp.copy(req);
            assert(rsp.randomize() with {rsp.data_out == dmem[rsp.addr];}); // rsp.data_out = dmem[rsp.addr] would be enough in this case
            finish_item(rsp);
            `uvm_info(get_type_name(), $sformatf("dmem response_r after finish: addr: 0x%0x, wrEn: %0b, data_in: 0x%0x, data_out: 0x%0x", rsp.addr, rsp.wrEn, rsp.data_in, rsp.data_out), UVM_DEBUG)

            if(rsp.wrEn) begin
                start_item(req_w);
                finish_item(req_w);
                `uvm_info(get_type_name(), $sformatf("dmem_request_w: addr: 0x%0x, wrEn: %0b, data_in: 0x%0x", req_w.addr, req_w.wrEn, req_w.data_in), UVM_DEBUG)

                start_item(rsp_w);
                rsp_w.copy(req_w);
                assert(rsp_w.randomize() with {rsp_w.data_out == dmem[rsp_w.addr];});
                if(rsp_w.wrEn) begin
                    dmem[rsp_w.addr] = rsp_w.data_in;
                end
                finish_item(rsp_w);
                `uvm_info(get_type_name(), $sformatf("dmem_response_w after finish: addr: 0x%0x, wrEn: %0b, data_in: 0x%0x, data_out: 0x%0x", rsp_w.addr, rsp_w.wrEn, rsp_w.data_in, rsp_w.data_out), UVM_DEBUG)

            end
        end
    endtask

    task post_body();

    endtask
endclass