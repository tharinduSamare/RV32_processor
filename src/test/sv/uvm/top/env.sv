`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;


class core_env extends uvm_env;
  `uvm_component_utils(core_env)
  function new(string name="core_env", uvm_component parent=null);
    super.new(name, parent);
  endfunction

    imem_agent imem_agnt;
    dmem_agent dmem_agnt;
    scoreboard scrb;

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);
        imem_agnt = imem_agent::type_id::create("imem_agnt", this);
        dmem_agnt = dmem_agent::type_id::create("dmem_agnt", this);
        scrb = scoreboard::type_id::create("scrb", this);
    endfunction

    virtual function void connect_phase(uvm_phase phase);
        super.connect_phase(phase);
        imem_agnt.imem_mon.imem_analysis_port.connect(scrb.imem_export);
        dmem_agnt.dmem_mon.dmem_analysis_port.connect(scrb.dmem_export);
    endfunction

endclass