class dmem_sequence extend uvm_sequence;
    `uvm_object_utils(dmem_sequence)

    function new(string name = "dmem_sequence")
        super.new(name);
    endfunction

    task pre_body();

    endtask

    task body();
        dmem_data pkt;
        `uvm_do(pkt);
    endtask

    task post_body();

    endtask
endclass