package io.github.techtastic.hexweb.casting.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import com.google.gson.JsonObject
import io.github.techtastic.hexweb.casting.iota.JsonIota
import ram.talia.moreiotas.api.getString

object OpStringtoJson: ConstMediaAction {
    override val argc: Int
        get() = 0

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val string_to_convert = args.getString(0,argc)
        return listOf(JsonIota(JsonObject()))
    }
}