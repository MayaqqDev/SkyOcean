package me.owdding.skyocean.config.patcher

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.skyocean.SkyOcean
import me.owdding.skyocean.utils.Utils
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.util.function.UnaryOperator
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import kotlin.io.path.walk

object ConfigPatches {

    private val registry = mutableMapOf<ResourceLocation, MapCodec<out Patch>>()

    val CODEC: Codec<Patch> = ResourceLocation.CODEC.dispatch(Patch::id, registry::get)

    fun register(id: ResourceLocation, codec: MapCodec<out Patch>) {
        registry[id] = codec
    }

    fun loadPatches(): Map<Int, UnaryOperator<JsonObject>> {
        val orElseThrow = SkyOcean.SELF.findPath("repo/patches").orElseThrow()
        val patches = orElseThrow.walk().map {
            it.nameWithoutExtension.filter { c -> c.isDigit() }.toInt() to Utils.applyPatch(
                it.readText().readJson<JsonObject>(),
                "patches/${it.nameWithoutExtension}",
            ).toDataOrThrow(CODEC)
        }.sortedBy { it.first }.toMap()
        return patches
    }

    init {
        register(MovePatch.ID, MovePatch.CODEC)
        register(CompoundPatch.ID, CompoundPatch.CODEC)
    }

}
