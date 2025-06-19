class dmem_monitor extends uvm_monitor;
    `uvm_component_utils(dmem_monitor)

    virtual dmem_if vif
    bit enable_check = 1;

    uvm_analysis_port #(dmem_data) dmem_mon_analysis_port;

    function new(string name = "dmem_monitor", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);

        dmem_mon_analysis_port = new("dmem_mon_analysis_port", this);

        if(!uvm_config_db #(virtual dmem_if)::get(this, "", "vif", vif))begin
            `uvm_error(get_type_name(), "dmem_if interface not found")
        end
    endfunction

    virtual task run_phase(uvm_phase phase)
        dmem_data d_data_obj = dmem_data::type_id::create("d_data_obj", this);
        forever begin
            @(vif.valid);
            d_data_obj.data = vif.data;
            d_data_obj.addr = vif.addr;

            if(enable_check) check_protocol();

            dmem_mon_analysis_port.write(d_data_obj);
        end
    endtask

    virtual function void check_protocol();

    endfunction



endclass