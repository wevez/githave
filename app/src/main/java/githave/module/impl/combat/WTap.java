package githave.module.impl.combat;

import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.DoubleSetting;
import githave.module.setting.impl.ModeSetting;
import githave.util.TimerUtil;
import net.minecraft.network.play.client.C02PacketUseEntity;

import java.util.Arrays;

public class WTap extends Module {

    // TODO
    private final ModeSetting mode = new ModeSetting.Builder("Mode", "Sprint Reset")
            .build();

    private final DoubleSetting delay = new DoubleSetting.Builder("Delay", 100, 0, 500, 10)
            .build();

    public WTap() {
        super("WTap", "Auto WTap", ModuleCategory.Combat);
        this.getSettingList().addAll(Arrays.asList(
                mode,
                delay
        ));
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
    public void onMovementInput(Events.MovementInput event) {
        if (KillAura.target != null && mc.thePlayer.getNearestDistanceToEntity(KillAura.target) < 2 && mc.thePlayer.hurtTime == 0 && mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isPressed() && !timer2.hasTimeElapsed((long) delay.getValue()) && timer.hasTimeElapsed(500) && mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit.hurtResistantTime > 0) {
            event.input.moveForward = 0f;
            event.input.moveStrafe = 0;
        }
        super.onMovementInput(event);
    }
}
