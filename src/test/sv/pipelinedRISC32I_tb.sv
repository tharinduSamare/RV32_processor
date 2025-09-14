module PipelinedRV32I_tb();

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

    data_mem #(.MEM_INIT_FILE(IMEM_INIT_FILE))dmem(
        .mem_if(dmem_if)
    );

    initial begin
        @(posedge clk) imem_if.rstn = 1'b0;
        repeat(2) #(CLK_PERIOD);
        @(posedge clk) imem_if.rstn = 1'b1;
        forever begin
            @(posedge clk);
        end
    end



endmodule

module ins_mem #(
    parameter string MEM_INIT_FILE = IMEM_INIT_FILE
) (
    imem_if mem_if
);

logic [INSTR_WIDTH-1:0]mem[0:IMEM_DEPTH-1];

initial begin
    $readmemh(MEM_INIT_FILE, mem); 
end

assign mem_if.instr = mem[mem_if.pc>>2];

endmodule

module data_mem#(
    parameter string MEM_INIT_FILE = IMEM_INIT_FILE
    )(
    dmem_if mem_if
);

logic [DATA_WIDTH-1:0]mem[0:DMEM_DEPTH-1];

initial begin
    $readmemh(MEM_INIT_FILE, mem); 
end

always_ff @(posedge mem_if.clk) begin
    if(mem_if.wrEn) begin
        mem[mem_if.addr] <= mem_if.data_in;
    end
end

assign mem_if.data_out = (!mem_if.rstn)? '0 : mem[mem_if.addr];

endmodule