
file mkdir ../../../../build
cd ../../../../build
exec xvlog -sv -f ../src/test/sv/uvm/compile_list.f -L uvm -define SIM_TIME=1000 ; 
exec xelab top_tb -relax -s top -timescale 1ns/1ps ;  
exec xsim top \
    -testplusarg UVM_TESTNAME=base_test \
    -testplusarg UVM_VERBOSITY=UVM_DEBUG \
    -runall \
    -wdb dump.wdb
