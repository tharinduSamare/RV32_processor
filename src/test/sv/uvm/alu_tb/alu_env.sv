`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;

class alu_env extends uvm_env;
    `uvm_component_utils(alu_env)

    function new(string name = "alu_env", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    alu_agent agent;
    alu_scoreboard scoreboard;

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);
        agent = alu_agent::type_id::create("agent", this);
        scoreboard = alu_scoreboard::type_id::create("scoreboard", this);
    endfunction

    virtual function void connect_phase(uvm_phase phase);
        super.connect_phase(phase);
        agent.monitor.mon_analysis_port.connect(scoreboard.m_analysis_imp);
    endfunction

endclass