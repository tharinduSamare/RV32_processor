import uvm_pkg::*;
import tb_config_pkg::*;
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
    dmem_if dmem_if (clk, imem_if.rstn);

    PipelinedRV32Icore DUT(
        .clock(clk),
        .reset(~imem_if.rstn),
        .io_check_res(io_check_res),

        .io_imem_instr(imem_if.instr),
        .io_imem_PC(imem_if.pc),

        .io_dmem_addr(dmem_if.addr),
        .io_dmem_wData(dmem_if.data_in),
        .io_dmem_wrEn(dmem_if.wrEn),
        .io_dmem_rData(dmem_if.data_out)
    );

    ins_mem #(.MEM_INIT_FILE(IMEM_INIT_FILE))imem(
        .mem_if(imem_if)
    );

    data_mem dmem(
        .mem_if(dmem_if)
    );

    initial begin
        uvm_config_db #(virtual imem_if)::set(null, "uvm_test_top", "imem_vif", imem_if);
        uvm_config_db #(virtual dmem_if)::set(null, "uvm_test_top", "dmem_vif", dmem_if);
        run_test("base_test");
    end

    // dump waveform
    initial begin
   		$dumpfile("dump.vcd");
   		$dumpvars;
   end

endmodule

module ins_mem #(
    parameter string MEM_INIT_FILE = "src/test/programs/BinaryFile"
) (
    imem_if mem_if
);

logic [INSTR_WIDTH-1:0]mem[0:IMEM_DEPTH-1];

initial begin
    $readmemh(MEM_INIT_FILE, mem); 
end

always_ff@(posedge mem_if.clk) begin
    if(!mem_if.rstn) begin
        mem_if.instr <= '0;
    end
    else begin
        mem_if.instr <= mem[mem_if.pc];
    end
end

endmodule

module data_mem(
    dmem_if mem_if
);

logic [DATA_WIDTH-1:0]mem[0:DMEM_DEPTH-1];

always_ff @(posedge mem_if.clk) begin
    if(mem_if.wrEn) begin
        mem[mem_if.addr] <= mem_if.data_in;
    end
end

assign mem_if.data_out = (!mem_if.rstn)? '0 : mem[mem_if.addr];

endmodule