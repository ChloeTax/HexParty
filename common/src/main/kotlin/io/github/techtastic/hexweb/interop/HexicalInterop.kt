package io.github.techtastic.hexweb.interop

import at.petrak.hexcasting.api.casting.iota.Iota
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import miyucomics.hexical.casting.iota.DyeIota
import miyucomics.hexical.casting.iota.IdentifierIota
import net.minecraft.resources.ResourceLocation

object HexicalInterop {
    fun Iota.toHexicalJson(): JsonElement? {
        if (this is IdentifierIota) {
            val json = JsonObject()
            json.addProperty("identifier", this.identifier.toString())
            return json
        }

        if (this is DyeIota) {
            val json = JsonObject()
            json.addProperty("color", this.dye)
            return json
        }

        return null
    }

    fun JsonObject.toHexicalIota(): Iota? {
        if (this.has("identifier"))
            return IdentifierIota(ResourceLocation(this.get("identifier").asString))

        if (this.has("color"))
            return DyeIota(this.get("color").asString)

        return null
    }
}