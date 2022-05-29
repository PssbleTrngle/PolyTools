package com.possible_triangle.polytools.datagen

import com.google.common.collect.ImmutableMap
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.util.Identifier
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Function
import java.util.function.Supplier

private data class CustomModelData(val id: Identifier, val data: Int, val model: Model, val key: TextureKey) {
    val type: String
        get() = if (key == TextureKey.LAYER0) "item" else "block"

    val modelId: Identifier
        get() = Identifier(id.namespace, "${type}/${id.path}")
}

class CustomModel(private val parent: Identifier?, private vararg val requiredTextures: TextureKey) :
    Model(Optional.ofNullable(parent), Optional.empty(), *requiredTextures) {

    private val customModels = arrayListOf<CustomModelData>()

    fun add(id: Identifier, data: Int, model: Model, key: TextureKey) {
        customModels.add(CustomModelData(id, data, model, key))
    }

    override fun upload(
        id: Identifier,
        textureMap: TextureMap,
        modelCollector: BiConsumer<Identifier, Supplier<JsonElement>>,
    ): Identifier {
        val stream = requiredTextures.toList() + textureMap.inherited.toList()

        val map = stream.stream().collect(ImmutableMap.toImmutableMap(Function.identity()) { key: TextureKey ->
            textureMap.getTexture(key)
        })

        customModels.forEach {
            val textureMap = TextureMap().put(it.key, it.modelId)
            it.model.upload(it.modelId, textureMap, modelCollector)
        }

        modelCollector.accept(id, Supplier {
            val jsonObject = JsonObject()
            if (parent != null) jsonObject.addProperty("parent", parent.toString())
            if (map.isNotEmpty()) {
                jsonObject.add("textures", JsonObject().also {
                    map.forEach { (textureKey, textureId) ->
                        it.addProperty(textureKey.name, textureId.toString())
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