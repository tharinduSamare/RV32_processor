
file mkdir ../../../../build
cd ../../../../build
exec xvlog -sv -f ../src/test/sv/uvm/compile_list.f -L uvm -define SIM_TIMEOUT=50000 ; 

# ALU tb
# exec xelab alu_tb -relax -s top -timescale 1ns/1ps -v 2;  
# exec xsim top \
#     -testplusarg UVM_TESTNAME=alu_test \
#     -testplusarg UVM_VERBOSITY=UVM_LOW \
#     -runall \
#     -wdb alu_dump.wdb

# RISCV-core tb
exec xelab top_tb -relax -s top -timescale 1ns/1ps -v 2;  
exec xsim top \
    -testplusarg UVM_TESTNAME=base_test \
    -testplusarg UVM_VERBOSITY=UVM_LOW \
    -runall \
    -wdb dump.wdb
