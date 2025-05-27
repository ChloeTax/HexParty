package io.github.techtastic.hexweb.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.utils.hasString
import io.github.techtastic.hexweb.SocketHandler
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import java.util.UUID

class SocketIota(payload: SocketInfo) : Iota(TYPE, payload) {
    fun getSocketInfo() = payload as SocketInfo

    override fun isTruthy(): Boolean {
        val info = getSocketInfo()
        info.getClientSocket()?.let { s -> return s.isConnected }
            ?: info.getServerSocket()?.let { s -> return s.isBound }

        return false
    }

    override fun toleratesOther(that: Iota?) = false

    override fun serialize(): Tag {
        return getSocketInfo().serialize()
    }

    companion object {
        val TYPE = object : IotaType<SocketIota>() {
            override fun deserialize(tag: Tag, world: ServerLevel?): SocketIota {
                val tag = tag as CompoundTag
                val info = SocketInfo.deserialize(tag)

                if (world != null) {
                    info.getServerSocket()
                    info.getClientSocket()
                }

                return SocketIota(info)
            }

            override fun display(tag: Tag): Component {
                val tag = tag as CompoundTag
                val info = SocketInfo.deserialize(tag)

                if (info.isServer())
                    return Component.translatable("hexweb.iota.socket.server")
                return Component.translatable("hexweb.iota.socket.client")
            }

            override fun color() = 0xFFFFFF
        }

    data class SocketInfo(val uuid: UUID, val url: String?, val port: Int) {
        fun isServer() = url == null

        fun getClientSocket() =
            if (!isServer())
                SocketHandler.getOrMakeClientSocket(uuid, url!!, port)
            else
                null

        fun getServerSocket() =
            if (isServer())
                SocketHandler.getOrMakeServerSocket(uuid, port)
            else
                null

        fun serialize(): CompoundTag {
            val tag = CompoundTag()

            tag.putUUID("uuid", uuid)
            if (url != null)
                tag.putString("url", url)
            tag.putInt("port", port)

            return tag
        }

        companion object {
            fun deserialize(tag: CompoundTag) =
                SocketInfo(
                    tag.getUUID("uuid"),
                    if (tag.hasString("url")) tag.getString("url") else null,
                    tag.getInt("port")
                )
            }
        }
    }
}