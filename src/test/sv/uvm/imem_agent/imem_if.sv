import tb_config_pkg::*;
interface imem_if(input bit clk);
    logic rstn;
    logic [PC_WIDTH-1:0]pc;
    logic [INSTR_WIDTH-1:0]instr;

    clocking cb @(posedge clk);
        default input #1step output #3ns;
        input pc;
        output instr;
    endclocking



endinterface