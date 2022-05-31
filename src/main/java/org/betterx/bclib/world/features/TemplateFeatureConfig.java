package org.betterx.bclib.world.features;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.betterx.bclib.world.structures.StructurePlacementType;
import org.betterx.bclib.world.structures.StructureWorldNBT;

import java.util.List;

public class TemplateFeatureConfig implements FeatureConfiguration {
    public static final Codec<TemplateFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    ExtraCodecs.nonEmptyList(StructureWorldNBT.CODEC.listOf())
                               .fieldOf("structures")
                               .forGetter((TemplateFeatureConfig ruinedPortalStructure) -> ruinedPortalStructure.structures)
            )
            .apply(instance, TemplateFeatureConfig::new)
    );

    public final List<StructureWorldNBT> structures;

    public static StructureWorldNBT cfg(ResourceLocation location,
                                        int offsetY,
                                        StructurePlacementType type,
                                        float chance) {
        return StructureWorldNBT.create(location, offsetY, type, chance);
    }

    public TemplateFeatureConfig(ResourceLocation location, int offsetY, StructurePlacementType type) {
        this(List.of(cfg(location, offsetY, type, 1.0f)));
    }

    public TemplateFeatureConfig(List<StructureWorldNBT> structures) {
        this.structures = structures;
    }
}
