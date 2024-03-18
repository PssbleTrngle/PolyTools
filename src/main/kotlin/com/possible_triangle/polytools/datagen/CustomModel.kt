package com.possible_triangle.polytools.datagen

import com.google.common.collect.ImmutableMap
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.data.models.model.ModelTemplate
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.models.model.TextureSlot
import net.minecraft.resources.ResourceLocation
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Function
import java.util.function.Supplier

private data class CustomModelData(val id: ResourceLocation, val data: Int, val model: ModelTemplate, val key: TextureSlot) {
    val type: String
        get() = if (key ==TextureSlot.LAYER0) "item" else "block"

    val modelId: ResourceLocation
        get() = ResourceLocation(id.namespace, "${type}/${id.path}")
}

class CustomModel(private val parent: ResourceLocation?, private vararg val requiredTextures: TextureSlot) :
    ModelTemplate(Optional.ofNullable(parent), Optional.empty(), *requiredTextures) {

    private val customModels = arrayListOf<CustomModelData>()

    fun add(id: ResourceLocation, data: Int, model: ModelTemplate, key: TextureSlot) {
        customModels.add(CustomModelData(id, data, model, key))
    }

    override fun create(
        id: ResourceLocation,
        textureMap: TextureMapping,
        modelCollector: BiConsumer<ResourceLocation, Supplier<JsonElement>>,
    ): ResourceLocation {
        val stream = requiredTextures.toList() + textureMap.forced.toList()

        val map = stream.stream().collect(ImmutableMap.toImmutableMap(Function.identity()) { key: TextureSlot ->
            textureMap.get(key)
        })

        customModels.forEach {
            val textureMap = TextureMapping().put(it.key, it.modelId)
            it.model.create(it.modelId, textureMap, modelCollector)
        }

        modelCollector.accept(id, Supplier {
            val jsonObject = JsonObject()
            if (parent != null) jsonObject.addProperty("parent", parent.toString())
            if (map.isNotEmpty()) {
                jsonObject.add("textures", JsonObject().also {
                    map.forEach { (textureKey, textureId) ->
                        it.addProperty(textureKey.id, textureId.toString())
                    }
                    jsonObject.add("textures", it)
                })

            }
            if (customModels.isNotEmpty()) {
                jsonObject.add("overrides", JsonArray().also { overrides ->
                    customModels.sortedBy { it.data }.forEach {
                        val override = JsonObject()
                        override.addProperty("model", it.modelId.toString())

                        val predicate = JsonObject()
                        predicate.addProperty("custom_model_data", it.data)
                        override.add("predicate", predicate)

                        overrides.add(override)
                    }
                })
            }
            jsonObject
        })

        return id
    }

}