`include "uvm_macros.svh"
import uvm_pkg::*;
import tb_config_pkg::*;

module alu_tb();

timeunit 1ns;
timeprecision 1ns;

localparam real CLK_FREQ = 100; //MHz
localparam real CLK_PERIOD = 1000 / CLK_FREQ; // ns

logic clk;
logic rst = 1'b0;

initial begin
    clk = 0;
    forever begin
        #(CLK_PERIOD/2);
        clk = ~clk;
    end
end

alu_if alu_if(clk);

ALU dut(
    // .clock(clk),
    // .reset(rst),
    .io_ALUOp(alu_if.ALUOp),
    .io_operandA(alu_if.operandA),
    .io_operandB(alu_if.operandB),
    .io_aluResult(alu_if.aluResult)
);

initial begin
    uvm_config_db #(virtual alu_if)::set(null, "uvm_test_top", "alu_if", alu_if);
    run_test("alu_test");
end

// dump waveform
initial begin
    $dumpfile("alu_dump.vcd");
    $dumpvars;
end

endmodule