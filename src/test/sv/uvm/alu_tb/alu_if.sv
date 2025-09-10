import tb_config_pkg::*;

interface alu_if (input clk);
    logic [DATA_WIDTH-1:0] operandA;
    logic [DATA_WIDTH-1:0] operandB;
    ALUOpT ALUOp;
    logic [DATA_WIDTH-1:0] aluResult;

    clocking cb @(posedge clk);
        default input #3ns output #2ns;
        input operandA, operandB, ALUOp;
        output aluResult;
    endclocking 
endinterface