package io.github.techtastic.hexweb.casting.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList

import at.petrak.hexcasting.api.casting.iota.Iota
import io.github.techtastic.hexweb.casting.iota.JsonIota
import io.github.techtastic.hexweb.utils.IotaParser

object OpJsonFromPatternList : ConstMediaAction {
    override val argc: Int
        get() = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val list = args.getList(0,argc)
        return listOf(JsonIota(IotaParser.json_from_list(list)))
    }
}