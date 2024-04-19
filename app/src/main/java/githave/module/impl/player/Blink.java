package githave.module.impl.player;

import githave.event.Events;
import githave.gui.font.TTFFontRenderer;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.BooleanSetting;
import githave.module.setting.impl.DoubleSetting;
import githave.module.setting.impl.MultiBooleanSetting;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.*;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import tv.twitch.chat.Chat;

import java.util.*;

public class Blink extends Module {
    private TTFFontRenderer font;

    private final BooleanSetting autoDisable = new BooleanSetting.Builder("Auto Disable")
            .value(false)
            .build();

    private final BooleanSetting autoReset = new BooleanSetting.Builder("Auto Reset")
            .value(false)
            .build();

    private final DoubleSetting length = new DoubleSetting.Builder("Length", 100, 1, 1000, 1)
            .build();

    private final MultiBooleanSetting packets = new MultiBooleanSetting.Builder("Cancel",
            "C03Player", "C04Pos", "C05Look", "C06PosLook", "C07Digging", "C08Place", "C09ItemChange", "C13Abilities")
            .value("C03Player", true)
            .build();

    private final List<Packet<?>> packetList = new ArrayList<>();

    public Blink() {
        super("Blink", "Make teleport like a lag", ModuleCategory.Player);
        this.setKeyCode(Keyboard.KEY_LEFT);
        this.getSettingList().addAll(Arrays.asList(
                autoDisable, autoReset, length, packets
        ));
    }

    @Override
    public void init() {
        font = TTFFontRenderer.of("Roboto-Regular", 20);

        super.init();
    }

    @Override
    public void onSendPacket(Events.SendPacket event) {
        Map<String, Boolean> packetMap = packets.getValue();

        if(!((packetMap.get("C03Player") && event.packet instanceof C03PacketPlayer)
                || (packetMap.get("C04Pos") && event.packet instanceof C03PacketPlayer.C04PacketPlayerPosition)
                || (packetMap.get("C05Look") && event.packet instanceof C03PacketPlayer.C05PacketPlayerLook)
                || (packetMap.get("C06PosLook") && event.packet instanceof C03PacketPlayer.C06PacketPlayerPosLook)
                || (packetMap.get("C07Digging") && event.packet instanceof C07PacketPlayerDigging)
                || (packetMap.get("C08Place") && event.packet instanceof C08PacketPlayerBlockPlacement)
                || (packetMap.get("C09ItemChange") && event.packet instanceof C09PacketHeldItemChange)
                || (packetMap.get("C13Abilities") && event.packet instanceof C13PacketPlayerAbilities))
        ) return;

        event.cancel();

        Packet<?> packet = event.packet;
        packetList.add(packet);

        if (length.getValue() <= packetList.size()) {
            if (autoDisable.getValue()) {
                this.toggle();
            } else if (autoReset.getValue()) {
                sendPackets();
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
                for (Packet<?> p : packetList) {
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
                }
            }
        }
        packetList.clear();
    }

    @Override
    public void onRenderGui(Events.PreRenderGui event) {
        ScaledResolution sr = new ScaledResolution(mc);
        String text = "Length: " + packetList.size();

        font.drawString(text, (sr.getScaledWidth() - font.width(text)) / 2, sr.getScaledHeight() / 1.25, 0xffffffff);

        super.onRenderGui(event);
    }
}
