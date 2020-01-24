package org.redrune.network.message

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import mu.KotlinLogging
import org.redrune.network.codec.CodecRegistry
import org.redrune.network.packet.Packet
import org.redrune.network.packet.PacketReader

/**
 * This class invokes the transformation of a [Packet] to a [Message] from the pipeline
 *
 * @author Tyluur <contact@kiaira.tech>
 * @since 2020-01-21
 */
class GameMessageDecoder : MessageToMessageDecoder<Packet>() {
    private val logger = KotlinLogging.logger {}

    override fun decode(ctx: ChannelHandlerContext, packet: Packet, out: MutableList<Any>) {
        println("packet being decoded into message!!!! ${packet.opcode}")

        val decoder = CodecRegistry.getDecoder(packet.opcode)
        if (decoder == null) {
            logger.info("Unable to find decoder for packet #${packet.opcode}")
        } else {
            val message = decoder.decode(PacketReader(packet))
            out.add(message)
            logger.info("Successfully decoded packet #${packet.opcode} and sent it as message $message")
        }
    }

}