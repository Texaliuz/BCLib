package org.betterx.bclib.items.tool;

import org.betterx.bclib.client.models.ModelsHelper;
import org.betterx.bclib.interfaces.ItemModelProvider;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BaseHoeItem extends HoeItem implements ItemModelProvider {
    public BaseHoeItem(Tier material, int attackDamage, float attackSpeed, Properties settings) {
        this(material, settings.attributes(HoeItem.createAttributes(material, attackDamage, attackSpeed)));
    }

    public BaseHoeItem(Tier material, Properties settings) {
        super(material, settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public BlockModel getItemModel(ResourceLocation resourceLocation) {
        return ModelsHelper.createHandheldItem(resourceLocation);
    }
}
