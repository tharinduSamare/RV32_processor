package tb_config_pkg;
    parameter int ADDR_WIDTH = 32;
    parameter int DATA_WIDTH = 32;
    parameter int INSTR_WIDTH = 32;
    parameter int PC_WIDTH = 32;
    parameter int IMEM_DEPTH = 32'h4000;
    parameter int DMEM_DEPTH = 32'h4000;
    parameter string IMEM_INIT_FILE = "/home/tharindu/software/riscv/riscv-tests2/isa/rv32ui-p-and.hex"; // path relative to the build folder

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
//     object ALUOpT extends ChiselEnum {

//   val isADD   = Value(0x01.U)
//   val isSUB   = Value(0x02.U)
//   val isXOR   = Value(0x03.U)
//   val isOR    = Value(0x04.U)
//   val isAND   = Value(0x05.U)
//   val isSLL   = Value(0x06.U)
//   val isSRL   = Value(0x07.U)
//   val isSRA   = Value(0x08.U)
//   val isSLT   = Value(0x09.U)
//   val isSLTU  = Value(0x0A.U)
//   val isPASSB = Value(0x0B.U) // aluResult = operandB

//   val invalid = Value(0xFF.U)
// }

endpackage