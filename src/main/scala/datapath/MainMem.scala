package datapath

import chisel3._
//import chisel3.util.experimental.loadMemoryFromFile

import tilelink._

class MainMem extends Module{
	val io = IO(new Bundle{
		val Address = Input(UInt(10.W))
		val DataIn = Input(SInt(32.W))
		val DataOut = Output(SInt(32.W))
		val str = Input(UInt(1.W))
		val ld = Input(UInt(1.W))
		val op = Output(UInt(3.W))
		val src = Output(UInt(32.W))
	})


	val tLink = Module(new TileTop())

	tLink.io.channelA.valid := 1.B
	tLink.io.channelA.bits.a_param := 0.U
	tLink.io.channelA.bits.a_size := 2.U
	tLink.io.channelA.bits.a_source := 2.U
	tLink.io.channelA.bits.a_mask := 1.U
	tLink.io.channelA.bits.a_corrupt := 0.U
	tLink.io.channelA.bits.a_opcode := Mux(io.ld === 1.U, 4.U, Mux(io.str === 1.U, 1.U, 2.U))
	tLink.io.channelA.bits.a_address := io.Address
	tLink.io.channelA.bits.a_data := Mux(io.str === 1.U, io.DataIn, 0.S)

	tLink.io.channelD.ready := 1.B

	io.DataOut := tLink.io.channelD.bits.d_data
	io.op := tLink.io.channelD.bits.d_opcode
	io.src := tLink.io.channelD.bits.d_source

	// val dMem = SyncReadMem(1024,UInt(32.W))
	// io.DataOut := 0.S

	// when(io.ld === 1.U){
	// 	io.DataOut := (dMem(io.Address)).asSInt
	// }.elsewhen(io.str === 1.U){
	// 	dMem(io.Address) := (io.DataIn).asUInt
	// }
	//loadMemoryFromFile(dMem, "/home/hellcaster/Memory.txt")
}
