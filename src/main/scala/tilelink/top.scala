package tilelink

import chisel3._ 
import chisel3.util._ 
import chisel3.experimental.BundleLiterals._


class TileTop extends Module with OpCodes with Config {

    val io = IO(new Bundle{
        // val opCode = Input(UInt(3.W))
        val channelA = Flipped(Decoupled(new channelABundle))
        val channelD = Decoupled(new channelDBundle)
    })

    val stall = Module(new stallUnit)    

    io.channelA.ready := 1.B

    val mem = SyncReadMem(1024, SInt(32.W))
    // mem.write(2.U, 4.U)


    when(io.channelA.bits.a_opcode === Get.U || io.channelA.bits.a_opcode === PutFullData.U || io.channelA.bits.a_opcode === PutPartialData.U){
        
        when(io.channelA.bits.a_opcode === Get.U){
            stall.io.bundle_in.d_opcode := AccessAckData.U
            stall.io.bundle_in.d_data := mem.read(io.channelA.bits.a_address, true.B)

        }.elsewhen(io.channelA.bits.a_opcode === PutFullData.U || io.channelA.bits.a_opcode === PutPartialData.U){
            stall.io.bundle_in.d_opcode := AccessAck.U
            stall.io.bundle_in.d_data := 0.S
            mem.write(io.channelA.bits.a_address, io.channelA.bits.a_data)
        }.otherwise{
            stall.io.bundle_in.d_opcode := 2.U
            stall.io.bundle_in.d_data := 2.S
        }

        stall.io.bundle_in.d_addr := io.channelA.bits.a_address

        stall.io.bundle_in.d_param := 0.U
        stall.io.bundle_in.d_size := io.channelA.bits.a_size
        stall.io.bundle_in.d_source := io.channelA.bits.a_source
        stall.io.bundle_in.d_sink := 0.U
        stall.io.bundle_in.d_denied := 0.U
        stall.io.bundle_in.d_corrupt := io.channelA.bits.a_corrupt
        stall.io.valid_in := 1.U

    }.otherwise{
        stall.io.bundle_in.d_addr := 0.U
        stall.io.bundle_in.d_opcode := 0.U
        stall.io.bundle_in.d_param := 0.U
        stall.io.bundle_in.d_size := 0.U
        stall.io.bundle_in.d_source := 0.U
        stall.io.bundle_in.d_sink := 0.U
        stall.io.bundle_in.d_denied := 0.U
        stall.io.bundle_in.d_corrupt := 1.U
        stall.io.bundle_in.d_data := 0.S
        stall.io.valid_in := 0.U
    }


    io.channelD.bits := stall.io.bundle_out
    io.channelD.valid := stall.io.valid_out
    // io.channelD.bits.d_data := Mux(stall.io.bundle_out.d_opcode === AccessAckData.U, mem.read(stall.io.bundle_out.d_addr), 0.S ) 
    // io.channelD.bits.d_data := mem.read(stall.io.bundle_out.d_addr)

    

}


// TODO: EXCEPTIONS