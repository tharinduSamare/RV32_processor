import tb_config_pkg::*;
interface dmem_if(input bit clk, input bit rstn);
    // logic rstn;
    logic [ADDR_WIDTH-1:0]addr;
    logic [DATA_WIDTH-1:0]data_in;
    logic [DATA_WIDTH-1:0]data_out;
    logic wrEn;

    clocking cb @(posedge clk);
        default input #1step output #3ns;
        input addr;
        input data_in;
        input wrEn;
        output data_out;
    endclocking

endinterface