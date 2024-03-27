package githave.module.impl.combat;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.DoubleSetting;
import githave.util.TimerUtil;
import net.minecraft.network.play.client.C02PacketUseEntity;

import java.util.Arrays;

public class WTap extends Module {

    private final DoubleSetting delay = new DoubleSetting.Builder("Delay", 100, 0, 500, 10)
            .build();

    public WTap() {
        super("WTap", "Auto WTap", ModuleCategory.Combat);
        this.getSettingList().addAll(Arrays.asList(delay));
    }

    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil timer2 = new TimerUtil();

    @Override
    public void onSendPacket(Events.SendPacket event) {
        if (event.packet instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity)event.packet;
            if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                timer2.reset();
            }
        }
        super.onSendPacket(event);
    }

    @Override
    public void onTick(Events.Tick event) {
        if (mc.thePlayer == null) return;
        if (mc.thePlayer.hurtTime == 1) {
            timer.reset();
        }
        if (KillAura.target == null) return;
        if (KillAura.target.hurtTime == 10) {
            mc.thePlayer.reSprint = 2;
        }
        super.onTick(event);
    }

    @Override
    public void onMoveButton(Events.MoveButton event) {
        if (mc.thePlayer.hurtTime == 0 && mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.pressed && !timer2.hasTimeElapsed((long) delay.getValue()) && timer.hasTimeElapsed(500) && mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit.hurtResistantTime > 0) {
            event.forward = false;
            event.backward = false;
        }
        super.onMoveButton(event);
    }
}
