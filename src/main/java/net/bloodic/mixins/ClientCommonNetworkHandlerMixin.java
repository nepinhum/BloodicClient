package net.bloodic.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.bloodic.event.EventManager;
import net.bloodic.events.PacketOutputListener;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class ClientCommonNetworkHandlerMixin implements ClientCommonPacketListener
{
    @Shadow @Final protected ClientConnection connection;

    @WrapOperation(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V"),
            method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V")
    private void wrapSendPacket(ClientConnection instance, Packet<?> packet, Operation<Void> original)
    {
        PacketOutputListener.PacketOutputEvent event = new PacketOutputListener.PacketOutputEvent(packet);
        EventManager.fire(event);

        if (!event.isCancelled())
            original.call(instance, event.getPacket());
    }
}
