import uvm_pkg::*;
module top_tb();

    timeunit 1ns;
    timeprecision 1ns;

    localparam real CLK_FREQ = 100; //MHz
    localparam real CLK_PERIOD = 1000 / CLK_FREQ; // ns

    logic clk;
    logic [31:0]io_check_res; // debug wire

    initial begin
        clk = 0;
        forever begin
            #(CLK_PERIOD/2);
            clk = ~clk;
        end
    end


    imem_if imem_if (clk);
    PipelinedRV32Icore DUT(
        .clock(clk),
        .reset(~imem_if.rstn),
        .io_check_res(io_check_res),
        .io_instr(imem_if.instr),
        .io_PC(imem_if.pc)
    );

    initial begin
        uvm_config_db #(virtual imem_if)::set(null, "uvm_test_top", "imem_vif", imem_if);
        run_test("base_test");
    end

    // dump waveform
    initial begin
   		$dumpfile("dump.vcd");
   		$dumpvars;
   end

endmodule