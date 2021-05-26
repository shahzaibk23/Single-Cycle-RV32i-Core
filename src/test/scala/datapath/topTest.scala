package datapath

import org.scalatest._ 
import chisel3._
import chisel3.util._ 
import chiseltest._
import chiseltest.experimental.TestOptionBuilder._ 
import chiseltest.internal.VerilatorBackendAnnotation

class topTest extends FreeSpec with ChiselScalatestTester {
    "TOP Test" in {
        test(new Top).withAnnotations(Seq(VerilatorBackendAnnotation)){ c =>

        c.clock.step(16)

        }
    }
}