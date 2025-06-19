class dmem_sequencer extends uvm_sequencer;
    `uvm_component_utils(dmem_sequencer)

    function new(string name = "dmem_sequencer", uvm_component parent);
        super.new(name, parent);
    endfunction

endclass