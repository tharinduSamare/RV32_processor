package BTB_Tester

import chisel3._
import chiseltest._
import btb_pkg._
import org.scalatest.flatspec.AnyFlatSpec

class BTBTest extends AnyFlatSpec with ChiselScalatestTester {

    "BTB_Tester" should "work" in {
        test(new TwoWayBTB(NSETS = 8)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

            dut.clock.setTimeout(0)

            // initialize
            dut.clock.step(1)
            dut.io.PC.poke(0.U)
            dut.io.update.poke(0.U)
            dut.io.updatePC.poke(0.U)
            dut.io.updateTarget.poke(0.U)
            dut.io.mispredicted.poke(0.U)

            // TEST 1: Update BTB and read values

            //1. update set:0, addr: 0x20, target: 0x58, mispredicted: 1
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x20.U)
            dut.io.updateTarget.poke(0x58.U)
            dut.io.mispredicted.poke(1.U)

            //2. update set:1, addr: 0x644, target: 0x7534, mispredicted: 0
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x644.U)
            dut.io.updateTarget.poke(0x7534.U)
            dut.io.mispredicted.poke(0.U)

            //3. update set:5, addr: 0xBB4, target: 0x105C, mispredicted: 0
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0xBB4.U)
            dut.io.updateTarget.poke(0x105C.U)
            dut.io.mispredicted.poke(0.U)

            //4. update set:7, addr: 0x01C, target: 0x5388, mispredicted: 1
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x01C.U)
            dut.io.updateTarget.poke(0x5388.U)
            dut.io.mispredicted.poke(1.U)

            // check the stored values

            //1. check set:0, addr: 0x20, target: 0x58, prediction: 1
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.updatePC.poke(0.U)
            dut.io.updateTarget.poke(0.U)
            dut.io.mispredicted.poke(0.U)

            dut.io.PC.poke(0x20.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x58.U)
            dut.io.predictedTaken.expect(1.U)

            //2. check set:1, addr: 0x644, target: 0x7534, prediction: 0
            dut.clock.step(1)
            dut.io.PC.poke(0x644.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7534.U)
            dut.io.predictedTaken.expect(0.U)

            //3. check set:5, addr: 0xBB4, target: 0x105C, prediction: 0
            dut.clock.step(1)
            dut.io.PC.poke(0xBB4.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0X105C.U)
            dut.io.predictedTaken.expect(0.U)

            //4. check set:7, addr: 0x01C, target: 0x5388, prediction: 1
            dut.clock.step(1)
            dut.io.PC.poke(0x01C.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x5388.U)
            dut.io.predictedTaken.expect(1.U)

            // TEST2: Update existing value multiple times
            // a. Initialize with branch = 0
            //1. update set:3, addr: 0x36C, target: 0x7370, mispredicted: 0
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x36C.U)
            dut.io.updateTarget.poke(0x7370.U)
            dut.io.mispredicted.poke(0.U)

            //2. check set:3, addr: 0x36C, target: 0x7370, prediction: 0 (strong not taken)
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x36C.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7370.U)
            dut.io.predictedTaken.expect(0.U)

            //3. update set:3, addr: 0x36C, target: 0x7370, mispredicted: 1
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x36C.U)
            dut.io.updateTarget.poke(0x7370.U)
            dut.io.mispredicted.poke(1.U)

            //4. check set:3, addr: 0x36C, target: 0x7370, prediction: 0 (weak not taken)
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x36C.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7370.U)
            dut.io.predictedTaken.expect(0.U)

            //5. update set:3, addr: 0x36C, target: 0x7370, mispredicted: 1
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x36C.U)
            dut.io.updateTarget.poke(0x7370.U)
            dut.io.mispredicted.poke(1.U)

            //6. check set:3, addr: 0x36C, target: 0x7370, prediction: 0 (strong taken)
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x36C.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7370.U)
            dut.io.predictedTaken.expect(1.U)

            //7. update set:3, addr: 0x36C, target: 0x7370, mispredicted: 1
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x36C.U)
            dut.io.updateTarget.poke(0x7370.U)
            dut.io.mispredicted.poke(1.U)

            //8. check set:3, addr: 0x36C, target: 0x7370, prediction: 0 (weak taken)
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x36C.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7370.U)
            dut.io.predictedTaken.expect(1.U)

            //9. update set:3, addr: 0x36C, target: 0x7370, mispredicted: 1
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x36C.U)
            dut.io.updateTarget.poke(0x7370.U)
            dut.io.mispredicted.poke(1.U)

            //10. check set:3, addr: 0x36C, target: 0x7370, prediction: 0 (strong not taken)
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x36C.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7370.U)
            dut.io.predictedTaken.expect(0.U)

            // b. Initialize with branch = 1
            //1. update set:3, addr: 0x34C, target: 0x7378, mispredicted: 1
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x34C.U)
            dut.io.updateTarget.poke(0x7378.U)
            dut.io.mispredicted.poke(1.U)

            //2. check set:3, addr: 0x34C, target: 0x7378, prediction: 0 (strong taken)
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x34C.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7378.U)
            dut.io.predictedTaken.expect(1.U)

            //3. update set:3, addr: 0x34C, target: 0x7378, mispredicted: 1
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x34c.U)
            dut.io.updateTarget.poke(0x7378.U)
            dut.io.mispredicted.poke(1.U)

            //4. check set:3, addr: 0x34C, target: 0x7378, prediction: 0 (weak taken)
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x34C.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7378.U)
            dut.io.predictedTaken.expect(1.U)

            //5. update set:3, addr: 0x34C, target: 0x7378, mispredicted: 1
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x34c.U)
            dut.io.updateTarget.poke(0x7378.U)
            dut.io.mispredicted.poke(1.U)

            //6. check set:3, addr: 0x34C, target: 0x7378, prediction: 0 (strong not taken)
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x34C.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7378.U)
            dut.io.predictedTaken.expect(0.U)

            //7. update set:3, addr: 0x34C, target: 0x7378, mispredicted: 1
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x34c.U)
            dut.io.updateTarget.poke(0x7378.U)
            dut.io.mispredicted.poke(1.U)

            //8. check set:3, addr: 0x34C, target: 0x7378, prediction: 0 (weak not taken)
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x34C.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7378.U)
            dut.io.predictedTaken.expect(0.U)

            //9. update set:3, addr: 0x34C, target: 0x7378, mispredicted: 1
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x34c.U)
            dut.io.updateTarget.poke(0x7378.U)
            dut.io.mispredicted.poke(1.U)

            //10. check set:3, addr: 0x34C, target: 0x7378, prediction: 0 (strong taken)
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x34C.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7378.U)
            dut.io.predictedTaken.expect(1.U)


            // TEST3: Write to and read from the same set twice. (Both should be valid as there are 2 ways per set)

            //1. update set:1, addr: 0x364, target: 0x7378, mispredicted: 1
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x364.U)
            dut.io.updateTarget.poke(0x7378.U)
            dut.io.mispredicted.poke(1.U)

            //2. check set:1, addr: 0x364, target: 0x7378, prediction: 1
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x364.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7378.U)
            dut.io.predictedTaken.expect(1.U)

            //3. update set:1, addr: 0x444, target: 0x2034, mispredicted: 0
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x444.U)
            dut.io.updateTarget.poke(0x2034.U)
            dut.io.mispredicted.poke(0.U)

            //4. check set:1, addr: 0x444, target: 0x2034, prediction: 0
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x444.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x2034.U)
            dut.io.predictedTaken.expect(0.U)

            //2. check set:1, addr: 0x364, target: 0x7378, prediction: 1
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x364.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7378.U)
            dut.io.predictedTaken.expect(1.U)

            // TEST4: Write to and read from the same set three times. (Only the oldest one (0x444) should be missed)
            //1. update set:1, addr: 0x5E4, target: 0x102C, mispredicted: 1
            dut.clock.step(1)
            dut.io.update.poke(1.U)
            dut.io.updatePC.poke(0x5E4.U)
            dut.io.updateTarget.poke(0x102C.U)
            dut.io.mispredicted.poke(1.U)
            
            //2. check set:1, addr: 0x5E4, target: 0x102C, prediction: 1 (should hit)
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x5E4.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x102C.U)
            dut.io.predictedTaken.expect(1.U)

            //3. check set:1, addr: 0x364, target: 0x7378, prediction: 1 (should miss)
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x364.U)
            dut.io.valid.expect(1.U)
            dut.io.target.expect(0x7378.U)
            dut.io.predictedTaken.expect(1.U)

            //4. check set:1, addr: 0x444, target: 0x2034, prediction: 0 (should hit)
            dut.clock.step(1)
            dut.io.update.poke(0.U)
            dut.io.PC.poke(0x444.U)
            dut.io.valid.expect(0.U)  
        }
    }
}