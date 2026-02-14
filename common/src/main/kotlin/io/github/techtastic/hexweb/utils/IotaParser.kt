package io.github.techtastic.hexweb.utils

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.*
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.github.techtastic.hexweb.HexWeb
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import org.jblas.DoubleMatrix
import ram.talia.moreiotas.api.casting.iota.MatrixIota
import ram.talia.moreiotas.api.casting.iota.StringIota
import java.util.*

object IotaParser {
    fun JsonToIota(json : JsonElement, env : CastingEnvironment) : Iota {
        /*
        * This doesn't mishap when something goes wrong, it just errors (caught by hex).
        * So, don't malform your json!!!
        *
        */
        try {
            return(DoubleIota(json.asDouble))
        } catch (error: Exception) {}
        try {
            return(StringIota.make(json.asString))
        } catch(error: Exception) {}
        try {
            return(BooleanIota(json.asBoolean))
        } catch (error: Exception) {}
        if (json.isJsonObject) {
            val jsonObj = json.asJsonObject


            if (jsonObj.has("null")) {
                return NullIota()
            }
            if (jsonObj.has("startDir") && jsonObj.has("angles")) {
                val angles = jsonObj.get("angles").asString
                val startDir = jsonObj.get("startDir").asString
                return PatternIota(HexPattern.fromAngles(angles, HexDir.fromString(startDir)))
            }
            if (jsonObj.has("x") && jsonObj.has("y") && jsonObj.has("z")) {
                val x = jsonObj.get("x").asDouble
                val y = jsonObj.get("y").asDouble
                val z = jsonObj.get("z").asDouble
                return Vec3Iota(Vec3(x, y, z))
            }
            if (jsonObj.has("uuid")) {
                val uuid = UUID.fromString(jsonObj.get("uuid").asString)
                val ent = env.world.getEntity(uuid)
                if (ent != null) {
                    if (ent is Player) {
                        return NullIota()
                    } else {
                        return EntityIota(ent)
                    }
                }
            }
            if (jsonObj.has("row") && jsonObj.has("col") && jsonObj.has("matrix")) {
                val col = jsonObj.get("col").asInt
                val row = jsonObj.get("row").asInt
                val mat = DoubleMatrix(row,col)
                val matrixTable = jsonObj.get("matrix").asJsonArray
                for (i in 1..col * row) {
                    if (matrixTable[i] != null) {
                        mat.put(i - 1, matrixTable[i].asDouble)
                    }
                }
            }

        }
        return GarbageIota()
    }
    fun json_from_list(list : SpellList) : JsonObject {
        var json = JsonObject()
        for (i in 0..<list.size()) {
            val value = list.toList()[i]
            if(value is ListIota){
                json.add(i.toString(),json_from_list(value.list))
            }
            if(value is Vec3Iota) {
                val temp = JsonObject()
                temp.add("x", JsonPrimitive(value.vec3.x))
                temp.add("y", JsonPrimitive(value.vec3.y))
                temp.add("z", JsonPrimitive(value.vec3.z))
                json.add(i.toString(),temp)
            }
            if(value is StringIota){
                json.add(i.toString(),JsonPrimitive(value.string))
            }
            if(value is PatternIota) {
                val temp = JsonObject()
                temp.add("angles", JsonPrimitive(value.pattern.anglesSignature()))
                temp.add("startDir",JsonPrimitive(value.pattern.startDir.toString()))
                json.add(i.toString(),temp)
            }
            if(value is DoubleIota) {
                json.add(i.toString(), JsonPrimitive(value.double))
            }
            if(value is BooleanIota) {
                json.add(i.toString(), JsonPrimitive(value.bool))
            }
            if(value is NullIota) {
                val temp = JsonObject()
                temp.add("null", JsonPrimitive(true))
                json.add(i.toString(),temp)
            }
            if(value is EntityIota) {
                val temp = JsonObject()
                temp.add("uuid", JsonPrimitive(value.entity.uuid.toString()))
                temp.add("name", JsonPrimitive(value.entity.name.toString()))
                json.add(i.toString(),temp)
            }
            if(value is MatrixIota) {
                val temp = JsonObject()
                temp.add("rows",JsonPrimitive(value.matrix.rows))
                temp.add("cols",JsonPrimitive(value.matrix.columns))
                val arr = JsonArray()
                for (i in 0..< (value.matrix.rows * value.matrix.columns)) {
                    arr.add(value.matrix[i])
                }
                temp.add("matrix",arr)
                json.add(i.toString(),temp)
            }
            if(value is GarbageIota) {
                val temp = JsonObject()
                temp.add("garbage", JsonPrimitive(true))
                json.add(i.toString(),temp)
            }
        }
        return json
    }
    fun parse_json_full(json : JsonObject, env : CastingEnvironment) : ListIota {
        var iotas = mutableListOf<Iota>()
        json.keySet().forEach{key ->
            var iota = JsonToIota(json.get(key), env)
            if ((iota is GarbageIota) && (json.get(key).isJsonObject)) {
                iota = parse_json_full(json.get(key).asJsonObject,env)
            }
            iotas += iota
        }
        return ListIota(iotas)
    }

}