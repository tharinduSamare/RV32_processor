# How to modify riscv-tests to have only rv32ui unprivileged  instructions only.

- By default the [riscv-tests](https://github.com/riscv-software-src/riscv-tests) generates assembly codes for rv32ui with privileged  (csrr, csrw, csrwi, ecall, fense etc.) instructions.
- Therefore, we can not use it on a simple rv32ui core (without privileged instructions).
- This folder is a patch to skip those assembly instructions.
- Also, this patch will generate the corresponding .hex files that can be loaded using $readmemh to (instruction) memory. 

## Steps
```
$ git clone https://github.com/riscv/riscv-tests
$ cd riscv-tests
$ git submodule update --init --recursive
```
- Replace `riscv-tests/env/p/link.ld` with `RV32_PROCESSOR/riscv-tests_modified_files/env/p/link.ld`
- Replace `riscv-tests/env/p/riscv_test.h` with `RV32_PROCESSOR/riscv-tests_modified_files/env/p/riscv_test.h`
- Replace `riscv-tests/isa/Makefile` with `RV32_PROCESSOR/riscv-tests_modified_files/isa/Makefile`
```
$ autoconf
$ ./configure --prefix=$RISCV/target
$ make
$ make install
```

- This will generate .hex to be used to initialize instruction memory (and data memory).
  - ex:- `isa/rv32ui-p-beq.hex`, `isa/rv32ui-p-beq.dump`
  - For a simple riscv core, use `rv32ui-p-<xxx>` files which use riscv 32-bit unsigned integer physical addressing instructions.
