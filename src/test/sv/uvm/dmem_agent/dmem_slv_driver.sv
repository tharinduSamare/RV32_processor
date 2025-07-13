class dmem_slv_driver extends uvm_driver #(dmem_slv_seq_item, dmem_slv_seq_item);
    `uvm_component_utils(dmem_slv_driver)

    function new(string name="dmem_slv_driver", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    virtual dmem_if dmem_vif;

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);
        if(!uvm_config_db #(virtual dmem_if)::get(this, "", "dmem_vif", dmem_vif))begin
            `uvm_fatal(get_type_name(), "Didn't get handle to virtual interface dmem_vif");
        end
    endfunction

    task run_phase(uvm_phase phase);
        dmem_slv_seq_item req;
        dmem_slv_seq_item rsp;

        super.run_phase(phase);

        forever begin
            `uvm_info(get_type_name(), $sformatf("Waiting for instruction from sequencer"), UVM_LOW)
            seq_item_port.get_next_item(req);
            setup_phase(req);
            seq_item_port.item_done();

            seq_item_port.get_next_item(rsp);
            access_phase(rsp);
            seq_item_port.item_done();
        end
    endtask

    virtual task setup_phase(dmem_slv_seq_item req);
        // @(posedge dmem_vif.clk); //[TODO] is this required????
        req.addr = dmem_vif.addr;
        req.data_in = dmem_vif.data_in;
        req.wrEn = dmem_vif.wrEn;
    endtask

    virtual task access_phase(dmem_slv_seq_item rsp);
        dmem_vif.data_out = rsp.data_out;
        @(posedge dmem_vif.clk); //[TODO] Is this required???
    endtask



endclass