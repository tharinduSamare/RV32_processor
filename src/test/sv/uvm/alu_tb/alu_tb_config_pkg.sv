package alu_tb_config_pkg;

    localparam real CLK_FREQ = 100; //MHz
    localparam real CLK_PERIOD = 1000 / CLK_FREQ; // ns
    
    parameter int DATA_WIDTH = 32;

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