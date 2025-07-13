
import tb_config_pkg::*;
class dmem_agent extends uvm_agent;
    `uvm_component_utils(dmem_agent)

    dmem_slv_driver dmem_drv;
    dmem_monitor dmem_mon;
    uvm_sequencer #(dmem_slv_seq_item) dmem_slv_seqr;

    function new(string name="dmem_agent", uvm_component parent=null);
        super.new(name, parent);
    endfunction

    virtual function void build_phase (uvm_phase phase);
        super.build_phase(phase);
        if(get_is_active())begin
            dmem_slv_seqr = uvm_sequencer #(dmem_slv_seq_item, dmem_slv_seq_item)::type_id::create("dmem_slv_seqr", this);
            dmem_drv  = dmem_slv_driver::type_id::create("dmem_drv", this);
        end
        dmem_mon  = dmem_monitor::type_id::create("dmem_mon", this);

    endfunction

    virtual function void connect_phase (uvm_phase phase);
        if(get_is_active())begin
            dmem_drv.seq_item_port.connect(dmem_slv_seqr.seq_item_export);
        end
    endfunction

endclass