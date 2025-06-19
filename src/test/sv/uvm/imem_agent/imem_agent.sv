
import tb_config_pkg::*;
class imem_agent extends uvm_agent;
    `uvm_component_utils(imem_agent)

    imem_slv_driver imem_drv;
    imem_monitor imem_mon;
    uvm_sequencer #(imem_slv_seq_item) imem_slv_seqr;

    function new(string name="imem_agent", uvm_component parent=null);
        super.new(name, parent);
    endfunction

    virtual function void build_phase (uvm_phase phase);
        super.build_phase(phase);
        if(get_is_active())begin
            imem_slv_seqr = uvm_sequencer #(imem_slv_seq_item, imem_slv_seq_item)::type_id::create("imem_slv_seqr", this);
            imem_drv  = imem_slv_driver::type_id::create("imem_drv", this);
        end
        imem_mon  = imem_monitor::type_id::create("imem_mon", this);

    endfunction

    virtual function void connect_phase (uvm_phase phase);
        if(get_is_active())begin
            imem_drv.seq_item_port.connect(imem_slv_seqr.seq_item_export);
        end
    endfunction

endclass