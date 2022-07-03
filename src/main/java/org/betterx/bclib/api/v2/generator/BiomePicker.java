package org.betterx.bclib.api.v2.generator;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiome;
import org.betterx.bclib.api.v2.levelgen.biomes.BiomeAPI;
import org.betterx.bclib.util.WeighTree;
import org.betterx.bclib.util.WeightedList;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BiomePicker {
    private final Map<BCLBiome, ActualBiome> all = new HashMap<>();
    public final Registry<Biome> biomeRegistry;
    private final List<ActualBiome> biomes = Lists.newArrayList();
    private final List<String> allowedBiomes;
    public final ActualBiome fallbackBiome;
    private WeighTree<ActualBiome> tree;

    public BiomePicker(Registry<Biome> biomeRegistry) {
        this(biomeRegistry, null);
    }

    public BiomePicker(Registry<Biome> biomeRegistry, List<Holder<Biome>> allowedBiomes) {
        this.biomeRegistry = biomeRegistry;
        this.allowedBiomes = allowedBiomes != null ? allowedBiomes
                .stream()
                .map(h -> h.unwrapKey())
                .filter(o -> o.isPresent())
                .map(o -> o.get().location().toString()).toList() : null;
        this.fallbackBiome = create(BiomeAPI.EMPTY_BIOME);
    }

    private boolean isAllowed(BCLBiome b) {
        if (allowedBiomes == null) return true;
        return allowedBiomes.contains(b.getID().toString());
    }

    private ActualBiome create(BCLBiome bclBiome) {
        ActualBiome e = all.get(bclBiome);
        if (e != null) return e;
        return new ActualBiome(bclBiome);
    }

    public void addBiome(BCLBiome biome) {
        ActualBiome a = create(biome);
        BCLib.LOGGER.info("Adding Biome " + a + " from " + biome);
        biomes.add(a);
    }

    public ActualBiome getBiome(WorldgenRandom random) {
        return biomes.isEmpty() ? fallbackBiome : tree.get(random);
    }

    public boolean isEmpty() {
        return biomes.isEmpty();
    }

    public void rebuild() {
        WeightedList<ActualBiome> list = new WeightedList<>();

        biomes.forEach(biome -> {
            if (biome.isValid)
                list.add(biome, biome.bclBiome.getGenChance());
        });
        //only a single biome, we need to add the edges as well
        if (list.size() == 1) {
            ActualBiome biome = list.get(0);

            if (biome.getEdge() != null) {
                float defaultBiomeSize = 128;
                float edgeSize = (biome.bclBiome.getEdgeSize() * list.getWeight(0)) / defaultBiomeSize;
                list.add(biome.getEdge(), edgeSize);
            }
        }

        //no Biome, make sure we add at least one, otherwise bad things will happen
        if (list.isEmpty()) {
            list.add(create(BiomeAPI.EMPTY_BIOME), 1);
        }


        tree = new WeighTree<>(list);
    }

    public class ActualBiome {
        public final BCLBiome bclBiome;
        public final Holder<Biome> biome;
        public final ResourceKey<Biome> key;

        private final WeightedList<ActualBiome> subbiomes = new WeightedList<>();
        private final ActualBiome edge;
        private final ActualBiome parent;
        public final boolean isValid;

        private ActualBiome(BCLBiome bclBiome) {
            all.put(bclBiome, this);
            this.bclBiome = bclBiome;

            this.key = biomeRegistry.getResourceKey(biomeRegistry.get(bclBiome.getID())).orElse(null);
            this.biome = key != null ? biomeRegistry.getOrCreateHolderOrThrow(key) : null;
            this.isValid = key != null;
            bclBiome.forEachSubBiome((b, w) -> {
                if (isAllowed(b))
                    subbiomes.add(create(b), w);
            });

            if (bclBiome.getEdge() != null && isAllowed(bclBiome.getEdge())) {
                edge = create(bclBiome.getEdge());
            } else {
                edge = null;
            }

            parent = bclBiome.getParentBiome() != null ? create(bclBiome.getParentBiome()) : null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ActualBiome entry = (ActualBiome) o;
            return bclBiome.equals(entry.bclBiome);
        }

        @Override
        public int hashCode() {
            return Objects.hash(bclBiome);
        }

        public ActualBiome getSubBiome(WorldgenRandom random) {
            return subbiomes.get(random);
        }

        public ActualBiome getEdge() {
            return edge;
        }

        public ActualBiome getParentBiome() {
            return parent;
        }

        public boolean isSame(ActualBiome e) {
            return bclBiome.isSame(e.bclBiome);
        }

        @Override
        public String toString() {
            return "ActualBiome{" +
                    "key=" + key.location() +
                    ", subbiomes=" + subbiomes.size() +
                    ", edge=" + (edge != null ? edge.key.location() : "null") +
                    ", parent=" + (parent != null ? parent.key.location() : "null") +
                    ", isValid=" + isValid +
                    '}';
        }
    }
}
