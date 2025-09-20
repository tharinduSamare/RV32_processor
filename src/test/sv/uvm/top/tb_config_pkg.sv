package tb_config_pkg;
    parameter int ADDR_WIDTH = 32;
    parameter int DATA_WIDTH = 32;
    parameter int INSTR_WIDTH = 32;
    parameter int PC_WIDTH = 32;
    parameter int IMEM_DEPTH = 32'h4000;
    parameter int DMEM_DEPTH = 32'h4000;
    parameter string IMEM_INIT_FILE = "/home/tharindu/software/riscv/riscv-tests2/isa/rv32ui-p-auipc.hex"; // path relative to the build folder
    parameter string DMEM_INIT_FILE = IMEM_INIT_FILE; // Initialize .data section

    parameter string RISCV_TESTS_DIR = "/home/tharindu/software/riscv/riscv-tests2/isa/";
    string TESTS[] = '{ "lui", "auipc", "jal", "jalr", "beq", "bne", "lw", "sw", "addi", "ori", "slli", "srai", "add", "sub"};
    // string TESTS[] = '{"and"};

endpackage