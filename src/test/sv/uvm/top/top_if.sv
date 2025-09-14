import tb_config_pkg::*;
interface top_if(input bit clk);
    logic io_coreDone; // end of simulation when "unimp" instruction in Instruction decode stage
    logic [DATA_WIDTH-1:0]io_gpRegVal; // gp (x3) reg contains the riscv-tests pass fail status
    logic [DATA_WIDTH-1:0]io_check_res;

    clocking cb @(posedge clk);
        default input #1step output #3ns;
        input io_coreDone;
        input io_check_res;
        input io_gpRegVal;
    endclocking

endinterface