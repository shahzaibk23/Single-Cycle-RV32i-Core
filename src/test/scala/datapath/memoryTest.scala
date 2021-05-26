package datapath

import org.scalatest._ 
import chisel3._ 
import chiseltest._ 
import chiseltest.experimental.TestOptionBuilder._ 
import chiseltest.internal.VerilatorBackendAnnotation

class memoryTest extends FreeSpec with ChiselScalatestTester {
    "MEMORY TEST" in {
        test(new MainMem).withAnnotations(Seq(VerilatorBackendAnnotation)){ c =>
            c.io.Address.poke(0.U)
            c.io.DataIn.poke(4.S)
            c.io.str.poke(1.U)
            c.io.ld.poke(0.U)
            // c.clock.step(1)
            // ----
            c.io.Address.poke(0.U)
            c.io.DataIn.poke(0.S)
            c.io.str.poke(0.U)
            c.io.ld.poke(1.U)
            c.clock.step(5)
        }
    }
}