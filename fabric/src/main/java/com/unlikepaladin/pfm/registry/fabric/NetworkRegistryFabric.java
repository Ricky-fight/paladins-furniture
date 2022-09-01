package com.unlikepaladin.pfm.registry.fabric;

import com.unlikepaladin.pfm.blocks.BasicToilet;
import com.unlikepaladin.pfm.blocks.ToiletState;
import com.unlikepaladin.pfm.blocks.blockentities.MicrowaveBlockEntity;
import com.unlikepaladin.pfm.client.screens.MicrowaveScreen;
import com.unlikepaladin.pfm.registry.NetworkIDs;
import com.unlikepaladin.pfm.registry.SoundIDs;
import com.unlikepaladin.pfm.registry.Statistics;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class NetworkRegistryFabric {
    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkIDs.MICROWAVE_ACTIVATE_PACKET_ID, (server, player, handler, attachedData, responseSender) -> {
            BlockPos pos = attachedData.readBlockPos();
            boolean active = attachedData.readBoolean();
            server.submitAndJoin(() -> {
                if(Objects.nonNull(player.world.getBlockEntity(pos))){
                    World world = player.world;
                    if (world.isChunkLoaded(pos)) {
                        MicrowaveBlockEntity microwaveBlockEntity = (MicrowaveBlockEntity) player.world.getBlockEntity(pos);
                        microwaveBlockEntity.setActive(active);
                    } else {
                        player.sendMessage(Text.of("Trying to access unloaded chunks, are you cheating?"), false);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(NetworkIDs.TOILET_USE_ID,
                ((server, player, handler, attachedData, responseSender) -> {
                    // Get the BlockPos we put earlier, in the networking thread
                    BlockPos blockPos = attachedData.readBlockPos();
                    server.submitAndJoin(() -> {
                        // Use the pos in the main thread
                        World world = player.world;
                        if (world.isChunkLoaded(blockPos)) {
                            world.setBlockState(blockPos, world.getBlockState(blockPos).with(BasicToilet.TOILET_STATE, ToiletState.DIRTY));
                            world.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundIDs.TOILET_USED_EVENT, SoundCategory.BLOCKS, 0.3f, world.random.nextFloat() * 0.1f + 0.9f);
                        } else {
                            player.sendMessage(Text.of("Trying to access unloaded chunks, are you cheating?"), false);
                        }
                    });
                }));
    }

    public static void registerClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkIDs.MICROWAVE_UPDATE_PACKET_ID,
            (client, handler, buf, responseSender) -> {
                boolean active = buf.readBoolean();
                BlockPos blockPos = buf.readBlockPos();
                if (handler.getWorld().isChunkLoaded(blockPos)) {
                    MicrowaveBlockEntity blockEntity = (MicrowaveBlockEntity) handler.getWorld().getBlockEntity(blockPos);
                    client.execute(() -> {
                        if (Objects.nonNull(client.currentScreen) && client.currentScreen instanceof MicrowaveScreen currentScreen)  {
                            currentScreen.getScreenHandler().setActive(blockEntity, active);}
                    });
                }
                else {
                    client.player.sendMessage(Text.of("Trying to access unloaded chunks, are you cheating?"), false);
                }
            }
        );
    }

}
