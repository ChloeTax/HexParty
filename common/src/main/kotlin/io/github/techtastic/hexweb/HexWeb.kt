package io.github.techtastic.hexweb

import com.mojang.logging.LogUtils
import io.github.techtastic.hexweb.blocks.HexWebBlockEntities
import io.github.techtastic.hexweb.blocks.HexWebBlocks
import io.github.techtastic.hexweb.config.HexWebConfig

object HexWeb {
    const val MOD_ID: String = "hexweb"
    val LOGGER = LogUtils.getLogger()

    @JvmStatic
    fun init() {
        HexWebBlocks.register()
        HexWebBlockEntities.register()

        HexWebConfig.setup()
    }
}