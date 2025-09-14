package tb_config_pkg;
    parameter int ADDR_WIDTH = 32;
    parameter int DATA_WIDTH = 32;
    parameter int INSTR_WIDTH = 32;
    parameter int PC_WIDTH = 32;
    parameter int IMEM_DEPTH = 32'h4000;
    parameter int DMEM_DEPTH = 32'h4000;
    parameter string IMEM_INIT_FILE = "/home/tharindu/software/riscv/riscv-tests2/isa/rv32ui-p-and.hex"; // path relative to the build folder
    parameter string DMEM_INIT_FILE = IMEM_INIT_FILE; // Initialize .data section

    parameter string RISCV_TESTS_DIR = "/home/tharindu/software/riscv/riscv-tests2/isa/";
    string TESTS[4] = '{ "and", "andi", "add", "addi" };

    typedef enum logic [7:0] { 
        isADD   = 8'h1,
        isSUB   = 8'h2,
        isXOR   = 8'h3,
        isOR    = 8'h4,
        isAND   = 8'h5,
        isSLL   = 8'h6,
        isSRL   = 8'h7,
        isSRA   = 8'h8,
        isSLT   = 8'h9,
        isSLTU  = 8'hA,
        isPASSB = 8'hB
     } ALUOpT;

endpackage