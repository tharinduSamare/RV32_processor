
`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;

class imem_monitor extends uvm_monitor;
    `uvm_component_utils(imem_monitor)

    virtual imem_if imem_vif;

    uvm_analysis_port #(imem_slv_seq_item) imem_analysis_port;

    function new(string name = "imem_monitor", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);

        imem_analysis_port = new("imem_analysis_port", this);

        if(!uvm_config_db #(virtual imem_if)::get(this, "", "imem_vif", imem_vif))begin
            `uvm_error(get_type_name(), "imem_if interface not found")
        end
    endfunction

    virtual task run_phase(uvm_phase phase);
        imem_slv_seq_item req, req_clone;
        req = imem_slv_seq_item::type_id::create("req", this);

        super.run_phase(phase);

        forever begin
            @(imem_vif.clk);
            if(imem_vif.rstn) begin
                req.pc = imem_vif.pc;
                req.instr = imem_vif.instr;
                $cast(req_clone, req.clone()); // pass a copy of data instead passing a reference (which could change in next clk cycle)
                imem_analysis_port.write(req_clone);
            end
        end
    endtask

endclass