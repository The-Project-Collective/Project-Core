package com.collective.projectcore.util.networking;

import com.collective.projectcore.util.networking.packets.BoxTrapParticlePacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;

public class CoreClientPacketHandler {

    public static void registerPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                BoxTrapParticlePacket.ID,
                BoxTrapParticlePacket.CODEC,
                (packet, context) -> {
                    MinecraftClient.getInstance().execute(() -> {
                        BlockPos pos = packet.pos();
                        for (int i = 0; i < 7; i++) {
                            assert MinecraftClient.getInstance().world != null;
                            double offsetX = MinecraftClient.getInstance().world.random.nextDouble();
                            double offsetY = 0.5;
                            double offsetZ = MinecraftClient.getInstance().world.random.nextDouble();

                            double velocityX = (MinecraftClient.getInstance().world.random.nextDouble() - 0.5) * 0.04;
                            double velocityY = (MinecraftClient.getInstance().world.random.nextDouble() - 0.5) * 0.04;
                            double velocityZ = (MinecraftClient.getInstance().world.random.nextDouble() - 0.5) * 0.04;

                            MinecraftClient.getInstance().world.addParticle(ParticleTypes.POOF,
                                    pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ,
                                    velocityX, velocityY, velocityZ);
                        }
                    });
                });
    }

}
