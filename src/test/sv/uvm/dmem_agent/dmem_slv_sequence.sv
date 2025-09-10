`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;

class dmem_slv_sequence extends uvm_sequence;
    `uvm_object_utils(dmem_slv_sequence)

    rand bit[DATA_WIDTH:0]dmem[bit [ADDR_WIDTH-1:0]]; //dmem as an associative array

    function new(string name = "dmem_slv_sequence");
        super.new(name);
    endfunction

    task pre_body();
        // `uvm_info(get_type_name(), $sformatf("Loading dmem_init_file: %0s", dmem_INIT_FILE), UVM_DEFAULT)
        // $readmemh(dmem_INIT_FILE, dmem);
    endtask

    task body();
        dmem_slv_seq_item req, rsp;
        req = dmem_slv_seq_item::type_id::create("req");
        rsp = dmem_slv_seq_item::type_id::create("rsp");

        forever begin // slave sequence runs forever
            start_item(req);
            `uvm_info(get_type_name(), "dmem req generated.", UVM_DEBUG)
            finish_item(req);

            start_item(rsp);
            `uvm_info(get_type_name(), "dmem rsp generated.", UVM_DEBUG)
            rsp.copy(req);
            assert (rsp.addr < {ADDR_WIDTH{1'b1}}) else `uvm_fatal(get_type_name(), $sformatf("dmem address: 0x%0x out of range: 0x%0x", rsp.addr, {ADDR_WIDTH{1'b1}}))
            
            if(!rsp.wrEn) begin // read req
                if(!dmem.exists(rsp.addr)) begin
                    bit [DATA_WIDTH-1:0]data_val;
                    void'(std::randomize(data_val));
                    dmem[rsp.addr] = data_val;
                end
                rsp.data_out = dmem[rsp.addr];
            end
            else begin // write req
                dmem[rsp.addr] = rsp.data_in;
            end
            
            `uvm_info(get_type_name(), $sformatf("dmem response: addr: 0x%0x, wrEn: %0b, data_in: 0x%0x, data_out: 0x%0x", rsp.addr, rsp.wrEn, rsp.data_in, rsp.data_out), UVM_MEDIUM)
            finish_item(rsp);
        end
    endtask

    task post_body();

    endtask
endclass