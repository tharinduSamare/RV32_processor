class alu_test extends uvm_test;
    `uvm_component_utils(alu_test)

    function new(string name = "alu_test", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    alu_env env;
    virtual alu_if vif;

    virtual function void build_phase(uvm_phase phase);
        super.build_phase(phase);
        env = alu_env::type_id::create("env", this);
        if(!uvm_config_db#(virtual alu_if)::get(this, "", "alu_if", vif)) begin
            `uvm_fatal(get_type_name(), "Did not get vif")
        end

        uvm_config_db#(virtual alu_if)::set(this, "env.agent.*", "alu_if", vif);
    endfunction

    virtual function void end_of_elaboration_phase (uvm_phase phase);
        uvm_top.print_topology();
    endfunction

    virtual task run_phase(uvm_phase phase);
        alu_sequence seq = alu_sequence::type_id::create("seq");
        phase.raise_objection(this);
        
        seq.randomize() with {iteration_count inside {[20:30]};};
        seq.start(env.agent.sequencer);
        #200;
        phase.drop_objection(this);
    endtask
endclass

