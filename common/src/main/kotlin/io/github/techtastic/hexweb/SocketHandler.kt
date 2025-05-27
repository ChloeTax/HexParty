package io.github.techtastic.hexweb

import io.github.techtastic.hexweb.utils.HexWebOperatorUtils
import java.net.ServerSocket
import java.net.Socket
import java.util.*

object SocketHandler {
    val clientSockets = mutableMapOf<UUID, Socket>()
    val serverSockets = mutableMapOf<UUID, ServerSocket>()

    fun getOrMakeClientSocket(uuid: UUID, url: String, port: Int): Socket {
        clientSockets[uuid]?.let { s ->
            HexWebOperatorUtils.checkBlacklist(s.remoteSocketAddress.toString())
            return s
        }

        HexWebOperatorUtils.checkBlacklist("$url:$port")

        val s = Socket(url, port)
        clientSockets[uuid] = s
        return s
    }

    fun getOrMakeServerSocket(uuid: UUID, port: Int): ServerSocket {
        serverSockets[uuid]?.let { s ->
            // TODO: Check Port Blacklist

            return s
        }

        // TODO: Check Port Blacklist

        val s = ServerSocket(port)
        serverSockets[uuid] = s
        return s
    }
}