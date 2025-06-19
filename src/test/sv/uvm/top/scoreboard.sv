class scoreboard extends uvm_scoreboard;
    `uvm_component_utils(scoreboard)

    function new (string name = "scoreboard", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    uvm_analysis_imp #(imem_slv_seq_item, scoreboard) imem_export;

    function void build_phase(uvm_phase phase);
        imem_export = new("imem_export", this);
    endfunction

    virtual function void write (imem_slv_seq_item imem_req);
        `uvm_info("write", $sformatf("imem_req: PC: 0x%0x Ins: 0x%0x", imem_req.pc, imem_req.instr), UVM_MEDIUM)
    endfunction

    virtual task run_phase(uvm_phase phase);
        // [TODO] Add checkers here
    endtask

    virtual function void check_phase(uvm_phase phase);
        // [TODO] Add final checkers
    endfunction


endclass