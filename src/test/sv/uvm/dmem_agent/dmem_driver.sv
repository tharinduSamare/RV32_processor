class dmem_driver extends uvm_driver;
    `uvm_component_utils(dmem_driver)

    function new(string name="dmem_driver", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    virtual dmem_if dmem_vif;

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);
        if(!uvm_config_db #(virtual dmem_if)::get(this, "", "item_vif", dmem_vif))begin
            `uvm_fatal(get_type_name(), "Didn't get handle to virtual interface dmem_vif");
        end
    endfunction

    task run_phase(uvm_phase phase);
        d_data d_data_obj;
        super.run_phase(phase);

        forever begin
            `uvm_info(get_type_name(), $sformatf("Waiting for data from sequencer"), UVM_MEDIUM)
            seq_item_port.get_next_item(d_data_obj);
            drive_item(d_data_obj);
            seq_item_port.item_done();
        end
    endtask

    virtual task drive_item(d_data d_data_obj);

    endtask

endclass