class base_test extends uvm_test;
    `uvm_component_utils(base_test)

    function new(string name = "base_test", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    core_env core_env0;
    virtual imem_if imem_vif;

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);
        core_env0 = core_env::type_id::create("core_env0", this);

        if(!uvm_config_db#(virtual imem_if)::get(this, "", "imem_vif", imem_vif)) `uvm_fatal(get_type_name(), "Did not get imem_vif")
        uvm_config_db#(virtual imem_if)::set(this, "core_env0.imem_agnt.*", "imem_vif", imem_vif);
    endfunction

    virtual function void end_of_elaboration_phase (uvm_phase phase);
        uvm_top.print_topology();
    endfunction

    virtual task run_phase (uvm_phase phase);
        imem_slv_sequence imem_slv_seq = imem_slv_sequence::type_id::create("imem_slv_seq");
        // super.run_phase(phase);
        phase.raise_objection(this);
        imem_slv_seq.randomize();
        fork
            apply_rstn();
            imem_slv_seq.start(core_env0.imem_agnt.imem_slv_seqr);
        join_none
        #(`SIM_TIME);
        phase.drop_objection(this);
    endtask

    virtual task apply_rstn();
        imem_vif.rstn <= 1'b0;
        repeat(10) @(posedge imem_vif.clk);
        imem_vif.rstn <= 1'b1;
        repeat(10) @(posedge imem_vif.clk);
    endtask

    

endclass