package com.unlikepaladin.pfm.menus;

import com.unlikepaladin.pfm.registry.ScreenHandlerIDs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;

public class IronStoveScreenHandler extends AbstractFurnaceScreenHandler {
    private final Inventory inventory;
    public IronStoveScreenHandler(int i, PlayerInventory playerInventory) {
        super(ScreenHandlerIDs.IRON_STOVE_SCREEN_HANDLER, RecipeType.SMOKING, RecipeBookCategory.SMOKER, i, playerInventory);
        this.inventory = new SimpleInventory(3);
    }

    public IronStoveScreenHandler(int i, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ScreenHandlerIDs.IRON_STOVE_SCREEN_HANDLER, RecipeType.SMOKING, RecipeBookCategory.SMOKER, i, playerInventory, inventory, propertyDelegate);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
    }

    public IronStoveScreenHandler(int i, PlayerInventory playerInventory, PacketByteBuf packetByteBuf) {
        super(ScreenHandlerIDs.IRON_STOVE_SCREEN_HANDLER, RecipeType.SMOKING, RecipeBookCategory.SMOKER, i, playerInventory);
        this.inventory = new SimpleInventory(3);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }
}