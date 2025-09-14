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
        dmem_slv_seq_item req, rsp;
        dmem_slv_seq_item req_w, rsp_w;

        super.run_phase(phase);

        // memory read can be done in one req-rsp pair
        // memory write need two req-rsp pairs. First pair reads the current data word. DUT takes it and updates the necessary bytes. Second req-rsp pair writes the updated word to memory.
        forever begin
            @(dmem_vif.cb ) begin
                `uvm_info(get_type_name(), $sformatf("Waiting for instruction from sequencer"), UVM_DEBUG)
                seq_item_port.get_next_item(req);
                setup_phase(req);
                seq_item_port.item_done();
                `uvm_info(get_type_name(), $sformatf("req: addr: 0x%0x, data_in: 0x%0x, wrEn: %0b", req.addr, req.data_in, req.wrEn), UVM_DEBUG)

                seq_item_port.get_next_item(rsp);
                access_phase(rsp);
                seq_item_port.item_done();
                `uvm_info(get_type_name(), $sformatf("rsp: addr: 0x%0x, data_in: 0x%0x, data_out: 0x%0x, wrEn: %0b", rsp.addr, rsp.data_in, rsp.data_out, rsp.wrEn), UVM_DEBUG)

                if(rsp.wrEn) begin
                    `uvm_info(get_type_name(), $sformatf("Waiting for instruction from sequencer"), UVM_DEBUG)
                    seq_item_port.get_next_item(req_w);
                    setup_phase(req_w);
                    seq_item_port.item_done();
                    `uvm_info(get_type_name(), $sformatf("req_w: addr: 0x%0x, data_in: 0x%0x, wrEn: %0b", req_w.addr, req_w.data_in, req_w.wrEn), UVM_DEBUG)

                    seq_item_port.get_next_item(rsp_w);
                    access_phase(rsp_w);
                    seq_item_port.item_done();
                    `uvm_info(get_type_name(), $sformatf("rsp_w: addr: 0x%0x, data_in: 0x%0x, data_out: 0x%0x, wrEn: %0b", rsp_w.addr, rsp_w.data_in, rsp_w.data_out, rsp_w.wrEn), UVM_DEBUG)
                end
            end
        end
    endtask

    virtual task setup_phase(dmem_slv_seq_item req);
        req.addr = dmem_vif.addr;
        req.data_in = dmem_vif.data_in;
        req.wrEn = dmem_vif.wrEn;
    endtask

    virtual task access_phase(dmem_slv_seq_item rsp);
        dmem_vif.data_out = rsp.data_out;
    endtask



endclass