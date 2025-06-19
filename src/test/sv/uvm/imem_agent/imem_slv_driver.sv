class imem_slv_driver extends uvm_driver #(imem_slv_seq_item, imem_slv_seq_item);
    `uvm_component_utils(imem_slv_driver)

    function new(string name="imem_slv_driver", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    virtual imem_if imem_vif;

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);
        if(!uvm_config_db #(virtual imem_if)::get(this, "", "imem_vif", imem_vif))begin
            `uvm_fatal(get_type_name(), "Didn't get handle to virtual interface imem_vif");
        end
    endfunction

    task run_phase(uvm_phase phase);
        imem_slv_seq_item req;
        imem_slv_seq_item rsp;

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

    virtual task setup_phase(imem_slv_seq_item req);
        // @(posedge imem_vif.clk); //[TODO] is this required????
        req.pc = imem_vif.pc;
    endtask

    virtual task access_phase(imem_slv_seq_item rsp);
        imem_vif.instr = rsp.instr;
        @(posedge imem_vif.clk); //[TODO] Is this required???
    endtask



endclass