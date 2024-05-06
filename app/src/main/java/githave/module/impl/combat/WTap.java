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

    private final BooleanSetting sprintReset = new BooleanSetting.Builder("Sprint Reset")
        .build();

    private final BooleanSetting keepDistance = new BooleanSetting.Builder("Keep Distance")
        .value(true)
        .builder();

    private final DoubleSetting keepRange = new DoubleSetting.Builder("Keep Range", 2.5, 0, 8, 0.1)
        .visibility(keepDistance::getValue)
        .build();

    private long currentCombodelay, lastComboTime;

    private boolean yeah;

    public WTap() {
        super("WTap", "Auto WTap", ModuleCategory.Combat);
        this.getSettingList().addAll(Arrays.asList(
            sprintReset,
            keepDistance,
            keepRange
        ));
    }

    @Override
    protected void onEnable() {
        yeah = true;
    }

    @Override
    public void onSendPacket(Events.SendPacket event) {
        if (event.packet instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity)event.packet;
            if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
            }
        }
        super.onSendPacket(event);
    }

    @Override
    public void onTick(Events.Tick event) {
        if (mc.thePlayer == null) return;
        if (KillAura.target == null) {
            yeah = true;
            return;
        }
        if (KillAura.target.hurtTime == 10) {
            if (yeah) {
                yeah = false;
                final long currentTime = System.currentTimeMillis();
                currentCombodelay = lastComboTime - currentTime;
                lastComboTime = currentTime;
            }
        } else {
            yeah = true;
        }
        super.onTick(event);
    }

    @Override
    public void onMovementInput(Events.MovementInput event) {
        if (KillAura.target == null) return;
        final int nextAttackTick = (int) (System.currentTimeMillis() - (lastComboTime + currentComboDelay) / 50;
        if (nextAttackTick <= 0) return;
        final int tikcs = 10;
        final List<Vec3> positions = PlayerUtil.predict(ticks);
        final List<Vec3> enemyPositions = PlayerUtil.predict(KillAura.target, ticks);
        boolean shouldStop = false;
        for (int i = 0; i < ticks; i++) {
            final Vec3 p = positions.get(i), e = enemyPositions.get(i);
            final boolean shouldInRange = i >= nextAttackTick;
            if (shouldInRange) {
                if (p.distanceTo(e) > this.keepRange.getValue()) {
                    shouldStop = false;
                    break;
                }
            } else {
                if (p.distanceTo(e) < this.keepRange.getValue()) {
                    shouldStop = true;
                }
            }
        }
        if (shouldStop) event.input.moveForward = 0f;
        super.onMovementInput(event);
    }
}
