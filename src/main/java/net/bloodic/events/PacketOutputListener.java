package net.bloodic.events;

import net.bloodic.event.CancellableEvent;
import net.bloodic.event.Listener;
import net.minecraft.network.packet.Packet;

public interface PacketOutputListener extends Listener
{
    void onSentPacket(PacketOutputEvent event);

    public static class PacketOutputEvent extends CancellableEvent<PacketOutputListener>
    {
        private Packet<?> packet;

        public PacketOutputEvent(Packet<?> packet)
        {
            this.packet = packet;
        }

        public Packet<?> getPacket()
        {
            return packet;
        }

        public void setPacket(Packet<?> packet)
        {
            this.packet = packet;
        }

        @Override
        public Class<PacketOutputListener> getListenerType()
        {
            return PacketOutputListener.class;
        }

        @Override
        public void fire(PacketOutputListener listener)
        {
            if(isCancelled())
                return;

            listener.onSentPacket(this);
        }
    }
}
