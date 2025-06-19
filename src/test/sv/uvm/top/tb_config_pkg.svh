package tb_config_pkg;
    parameter int ADDR_WIDTH = 32;
    parameter int DATA_WIDTH = 32;
    parameter int INSTR_WIDTH = 32;
    parameter int PC_WIDTH = 32;
    parameter int IMEM_DEPTH = 256;
    parameter string IMEM_INIT_FILE = "../src/test/programs/BinaryFile"; // path relative to the build folder
endpackage