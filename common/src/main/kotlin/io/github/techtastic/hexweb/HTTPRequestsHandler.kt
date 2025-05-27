package io.github.techtastic.hexweb

import com.google.gson.JsonObject
import com.mojang.datafixers.util.Either
import io.github.techtastic.hexweb.casting.mishap.MishapBlacklistUrl
import io.github.techtastic.hexweb.config.HexWebConfig
import io.github.techtastic.hexweb.utils.HexWebOperatorUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.*
import okhttp3.internal.http.HttpMethod
import java.io.IOException
import java.util.*

object HTTPRequestsHandler {
    var client: OkHttpClient? = null
    val responses = mutableMapOf<UUID, Either<Response, IOException>>()
    val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

    fun setup() {
        Http

        this.client = OkHttpClient()
    }

    fun makeAndQueueRequest(uuid: UUID, url: String, headers: List<String>, method: String, body: JsonObject) {
        HexWebOperatorUtils.checkBlacklist(url)

        var builder = Request.Builder().url(url)
            .method(method, if (HttpMethod.permitsRequestBody(method)) body.toString().toRequestBody(JSON) else null)

        headers.map { s -> s.split(":") }.forEach { header -> builder = builder.addHeader(header.first(), header.last()) }

        queueRequest(uuid, builder.build())
    }

    private fun queueRequest(uuid: UUID, req: Request) {
        if (this.client == null) this.client = OkHttpClient()
        client!!.newCall(req).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                responses[uuid] = Either.right(e)
            }

            override fun onResponse(call: Call, response: Response) {
                responses[uuid] = Either.left(response)
            }
        })
    }

    fun getResponse(uuid: UUID) = this.responses[uuid]

    fun clearResponse(uuid: UUID) = this.responses.remove(uuid)
}