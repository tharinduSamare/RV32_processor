class base_test extends uvm_test;
    `uvm_component_utils(base_test)

    function new(string name = "base_test", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    core_env core_env0;
    virtual imem_if imem_vif;
    virtual dmem_if dmem_vif;
    virtual top_if top_vif;
    string mem_init_file;
    // string TESTS[4] = '{ "and", "andi", "add", "addi" };

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);
        core_env0 = core_env::type_id::create("core_env0", this);

        if(!uvm_config_db#(virtual imem_if)::get(this, "", "imem_vif", imem_vif)) `uvm_fatal(get_type_name(), "Did not get imem_vif")
        uvm_config_db#(virtual imem_if)::set(this, "core_env0.imem_agnt.*", "imem_vif", imem_vif);
        if(!uvm_config_db#(virtual dmem_if)::get(this, "", "dmem_vif", dmem_vif)) `uvm_fatal(get_type_name(), "Did not get dmem_vif")
        uvm_config_db#(virtual dmem_if)::set(this, "core_env0.dmem_agnt.*", "dmem_vif", dmem_vif);

        if(!uvm_config_db#(virtual top_if)::get(this, "", "top_vif", top_vif)) `uvm_fatal(get_type_name(), "Did not get top_vif")

    endfunction

    virtual function void end_of_elaboration_phase (uvm_phase phase);
        uvm_top.print_topology();
    endfunction

    virtual task run_phase (uvm_phase phase);
        // super.run_phase(phase);
        phase.raise_objection(this);
        foreach (TESTS[i]) begin
            imem_slv_sequence imem_slv_seq = imem_slv_sequence::type_id::create("imem_slv_seq");
            dmem_slv_sequence dmem_slv_seq = dmem_slv_sequence::type_id::create("dmem_slv_seq");

            `uvm_info(get_type_name(), $sformatf("=== Start test %0d: rv32ui-p-%0s ===", i, TESTS[i]), UVM_LOW)
            mem_init_file = $sformatf("%0srv32ui-p-%0s.hex", RISCV_TESTS_DIR, TESTS[i]);

            uvm_config_db#(string)::set(null, "uvm_test_top.core_env0.imem_agnt.imem_slv_seqr", "imem_init_file", mem_init_file);
            uvm_config_db#(string)::set(null, "uvm_test_top.core_env0.dmem_agnt.dmem_slv_seqr", "dmem_init_file", mem_init_file);

            imem_slv_seq.randomize();
            dmem_slv_seq.randomize();

            fork
                apply_rstn();
                imem_slv_seq.start(core_env0.imem_agnt.imem_slv_seqr);
                dmem_slv_seq.start(core_env0.dmem_agnt.dmem_slv_seqr);
            join_none

            fork
                begin
                    #(`SIM_TIMEOUT);
                    `uvm_warning(get_type_name(), $sformatf("Simulation timedout: test: rv32ui-p-%0s", TESTS[i]))
                    
                end
                begin
                    @(posedge top_vif.io_coreDone);       
                    `uvm_info(get_type_name(), $sformatf("=== Finish test: rv32ui-p-%0s ===", TESTS[i]), UVM_MEDIUM)
                end
            join_any
            disable fork;

            if(top_vif.io_gpRegVal == 32'b1)begin
                `uvm_info(get_type_name(), $sformatf("rv32ui-p-%0s test PASS", TESTS[i]), UVM_LOW)
            end
            else begin
                `uvm_error(get_type_name(), $sformatf("rv32ui-p-%0s test FAIL. regFile[gp]: 0x%0x", TESTS[i], top_vif.io_gpRegVal))
                break;
            end

            @(posedge imem_vif.clk); // wait until last sequence - driver handshake finishes

            imem_slv_seq.kill();
            dmem_slv_seq.kill();
        end
        phase.drop_objection(this);
    endtask

    virtual task apply_rstn();
        imem_vif.rstn <= 1'b0;
        repeat(10) @(posedge imem_vif.clk);
        imem_vif.rstn <= 1'b1;
        repeat(10) @(posedge imem_vif.clk);
    endtask

    

endclass