package org.betterx.bclib.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourWood;
import org.betterx.bclib.interfaces.tools.AxeCanStrip;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.item.api.ItemTagProvider;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;


public abstract class BaseStripableLogBlock extends BaseRotatedPillarBlock implements AxeCanStrip {
    private final Block striped;

    protected BaseStripableLogBlock(Block striped, Properties settings) {
        super(settings);
        this.striped = striped;
    }

    @Override
    public BlockState strippedState(BlockState state) {
        return striped.defaultBlockState();
    }

    public static class Wood extends BaseStripableLogBlock implements BehaviourWood, BlockTagProvider, ItemTagProvider {
        private final boolean flammable;

        public Wood(MapColor color, Block striped, boolean flammable) {
            super(
                    striped,
                    (flammable
                            ? Properties.ofFullCopy(striped).ignitedByLava()
                            : Properties.ofFullCopy(striped)).mapColor(color)
            );
            this.flammable = flammable;
        }

        @Override
        public void registerBlockTags(ResourceLocation location, TagBootstrapContext<Block> context) {
            context.add(BlockTags.LOGS, this);
            if (flammable) {
                context.add(BlockTags.LOGS_THAT_BURN, this);
            }
        }

        @Override
        public void registerItemTags(ResourceLocation location, ItemTagBootstrapContext context) {
            context.add(ItemTags.LOGS, this);
            if (flammable) {
                context.add(ItemTags.LOGS_THAT_BURN, this);
            }
        }
    }
}
