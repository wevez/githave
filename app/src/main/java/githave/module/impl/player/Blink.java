package githave.module.impl.player;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.BooleanSetting;
import githave.module.setting.impl.DoubleSetting;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Blink extends Module {
    private final BooleanSetting autoDisable = new BooleanSetting.Builder("Auto Disable")
            .value(false)
            .build();

    private final DoubleSetting length = new DoubleSetting.Builder("Length", 100, 1, 5000, 1)
            .build();

    private final List<C03PacketPlayer> packetList = new ArrayList<>();

    public Blink() {
        super("Blink", "Make teleport like a lag", ModuleCategory.Player);
        this.setKeyCode(Keyboard.KEY_LEFT);
        this.getSettingList().addAll(Arrays.asList(
                autoDisable, length
        ));
    }

    @Override
    public void onSendPacket(Events.SendPacket event) {
        if(event.packet instanceof C03PacketPlayer) {
            event.cancel();

            C03PacketPlayer prevPacket = null;
            C03PacketPlayer packet = (C03PacketPlayer) event.packet;
            if (!packetList.isEmpty()) {
                prevPacket = packetList.get(packetList.size() - 1);
            }

            if (prevPacket != null && packet.isOnGround() == prevPacket.isOnGround()
                && packet.getYaw(-1) == prevPacket.getYaw(-1)
                && packet.getPitch(-1) == prevPacket.getPitch(-1)
                && packet.getPositionX(-1) == prevPacket.getPositionX(-1)
                && packet.getPositionY(-1) == prevPacket.getPositionY(-1)
                && packet.getPositionZ(-1) == prevPacket.getPositionZ(-1))
                return;

            packetList.add(packet);

            if (autoDisable.getValue() && length.getValue() <= packetList.size()) {
                this.toggle();
            }
        }

        super.onSendPacket(event);
    }

    @Override
    protected void onEnable() {
        packetList.clear();

        super.onEnable();
    }

    @Override
    protected void onDisable() {
        sendPackets();

        super.onDisable();
    }

    private void sendPackets() {
        if (!packetList.isEmpty()) {
            synchronized (packetList) {
                for (C03PacketPlayer p : packetList) {
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
                }
            }
        }
    }
}
