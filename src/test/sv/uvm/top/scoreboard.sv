
`uvm_analysis_imp_decl(_imem)
`uvm_analysis_imp_decl(_dmem)

class scoreboard extends uvm_scoreboard;
    `uvm_component_utils(scoreboard)

    function new (string name = "scoreboard", uvm_component parent = null);
        super.new(name, parent);
    endfunction

    uvm_analysis_imp_imem #(imem_slv_seq_item, scoreboard) imem_export;
    uvm_analysis_imp_dmem #(dmem_slv_seq_item, scoreboard) dmem_export;

    function void build_phase(uvm_phase phase);
        imem_export = new("imem_export", this);
        dmem_export = new("dmem_export", this);
    endfunction

    virtual function void write_imem (imem_slv_seq_item imem_req);
        `uvm_info(get_type_name(), $sformatf("imem_req: PC: 0x%0x Ins: 0x%0x", imem_req.pc, imem_req.instr), UVM_MEDIUM)
    endfunction

    virtual function void write_dmem (dmem_slv_seq_item dmem_req);
        `uvm_info(get_type_name(), $sformatf("dmem_req: addr: 0x%0x data_in: 0x%0x data_out: 0x%0x wrEn: 0b%0b", dmem_req.addr, dmem_req.data_in, dmem_req.data_out, dmem_req.wrEn), UVM_MEDIUM)
    endfunction

    virtual task run_phase(uvm_phase phase);
        // [TODO] Add checkers here
    endtask

    virtual function void check_phase(uvm_phase phase);
        // [TODO] Add final checkers
    endfunction


endclass