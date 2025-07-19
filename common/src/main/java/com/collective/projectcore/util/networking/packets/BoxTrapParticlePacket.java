package com.collective.projectcore.util.networking.packets;

import com.collective.projectcore.ProjectCore;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record BoxTrapParticlePacket(BlockPos pos) implements CustomPayload {

    public static final Id<BoxTrapParticlePacket> ID =
            CustomPayload.id("box_trap_particle");

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final PacketCodec<RegistryByteBuf, BoxTrapParticlePacket> CODEC = new PacketCodec<>() {

        @Override
        public void encode(RegistryByteBuf buf, BoxTrapParticlePacket value) {
            buf.writeBlockPos(value.pos);
        }

        @Override
        public BoxTrapParticlePacket decode(RegistryByteBuf buf) {
            return new BoxTrapParticlePacket(buf.readBlockPos());
        }
    };
}
