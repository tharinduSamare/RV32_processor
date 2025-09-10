`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;

class alu_driver extends uvm_driver #(alu_seq_item);
    `uvm_component_utils(alu_driver)

    function new(string name = "alu_driver", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    virtual alu_if vif;

    virtual function void build_phase (uvm_phase phase);
        super.build_phase(phase);
        if(!uvm_config_db#(virtual alu_if)::get(this, "", "alu_if", vif)) begin
            `uvm_fatal(get_type_name(), "Could not get vif")
        end
    endfunction

    virtual task run_phase(uvm_phase phase);
        super.run_phase(phase);
        forever begin
            alu_seq_item item;
            `uvm_info(get_type_name(), $sformatf("Wait for item from sequencer"), UVM_DEBUG)
            seq_item_port.get_next_item(item);
            `uvm_info(get_type_name(), $sformatf("operandA: 0x%0x, operandB: 0x%0x, aluOp: %p", item.operandA, item.operandB, item.ALUOp), UVM_DEBUG)
            drive_item(item);
            seq_item_port.item_done();
        end
    endtask

    virtual task drive_item(alu_seq_item item);
        @(posedge vif.clk);
        vif.operandA <= item.operandA;
        vif.operandB <= item.operandB;
        vif.ALUOp    <= item.ALUOp;
    endtask

endclass