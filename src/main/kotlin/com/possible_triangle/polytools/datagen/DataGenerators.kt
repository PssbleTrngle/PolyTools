package com.possible_triangle.polytools.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

class DataGenerators : DataGeneratorEntrypoint {

    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        generator.addProvider(ToolModels(generator))
        generator.addProvider(ChiseledModels(generator))
    }

}