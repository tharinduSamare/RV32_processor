
`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;

class dmem_monitor extends uvm_monitor;
    `uvm_component_utils(dmem_monitor)

    virtual dmem_if dmem_vif;

    uvm_analysis_port #(dmem_slv_seq_item) dmem_analysis_port;

    function new(string name = "dmem_monitor", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);

        dmem_analysis_port = new("dmem_analysis_port", this);

        if(!uvm_config_db #(virtual dmem_if)::get(this, "", "dmem_vif", dmem_vif))begin
            `uvm_error(get_type_name(), "dmem_if interface not found")
        end
    endfunction

    virtual task run_phase(uvm_phase phase);
        dmem_slv_seq_item req, req_clone;
        req = dmem_slv_seq_item::type_id::create("req", this);

        super.run_phase(phase);

        forever begin
            @(dmem_vif.clk);
            if(dmem_vif.rstn) begin
                req.addr = dmem_vif.addr;
                req.data_in = dmem_vif.data_in;
                req.data_out = dmem_vif.data_out;
                req.wrEn = dmem_vif.wrEn;
                $cast(req_clone, req.clone()); // pass a copy of data instead passing a reference (which could change in next clk cycle)
                dmem_analysis_port.write(req_clone);
            end
        end
    endtask

endclass