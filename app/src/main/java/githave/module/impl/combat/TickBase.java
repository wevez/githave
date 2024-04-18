package githave.module.impl.combat;

import githave.GitHave;
import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.BooleanSetting;
import githave.module.setting.impl.DoubleSetting;
import githave.util.MoveUtil;
import githave.util.TimerUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class TickBase extends Module {

    private LinkedList<Packet> outPackets = new LinkedList<>();

    private double balance, lastBalance, smartMaxBalance;
    private boolean fast;

    private final TimerUtil delayTimer = new TimerUtil();
    private final TimerUtil clickTimer = new TimerUtil();

    public DoubleSetting minRange = new DoubleSetting.Builder("Min Range", 6, 3, 8, 0.01)
            .build();
    public DoubleSetting maxRange = new DoubleSetting.Builder("Max Range", 6, 3, 6, 0.01)
            .build();
    public DoubleSetting slowTimer = new DoubleSetting.Builder("Slow Timer", 0, 0, 1, 0.01)
            .build();
    public DoubleSetting chargeMultiplier = new DoubleSetting.Builder("Charge Multiplier", 1, 0, 1, 0.01)
            .build();;
    public DoubleSetting delay = new DoubleSetting.Builder("Delay", 200, 0, 3000, 50)
            .build();
    public BooleanSetting notInCombo = new BooleanSetting.Builder("Not In Combo")
            .value(false)
            .build();
    public BooleanSetting onlyForward = new BooleanSetting.Builder("Only Forward")
            .value(true)
            .build();
    public BooleanSetting preLoad = new BooleanSetting.Builder("Pre Load")
            .value(false)
            .build();
    public BooleanSetting blink = new BooleanSetting.Builder("Blink")
            .value(false)
            .build();
    public BooleanSetting onlyOnGround = new BooleanSetting.Builder("Only On Ground")
            .value(false)
            .build();
    public BooleanSetting noFluid = new BooleanSetting.Builder("No Fluid")
            .value(true)
            .build();

    public TickBase() {
        super("TickBase", "", ModuleCategory.Combat);
        this.getSettingList().addAll(Arrays.asList(
                minRange,
                maxRange,
                slowTimer,
                delay,
                noFluid
        ));
    }

    @Override
    protected void onDisable() {
        outPackets.clear();
        super.onDisable();
    }

    @Override
    public void onSendPacket(Events.SendPacket event) {
        if (event.packet instanceof C03PacketPlayer) {
            C03PacketPlayer packet = (C03PacketPlayer) event.packet;
            if (mc.timer.timerSpeed > 1f && blink.getValue()) {
                event.cancel();
                outPackets.add(event.packet);
            }
        }
        super.onSendPacket(event);
    }

    @Override
    public void onWorldChange(Events.WorldChange event) {
        balance = 0;
        lastBalance = 0;
        super.onWorldChange(event);
    }

    @Override
    public void onTick(Events.Tick event) {
        if (mc.thePlayer == null) return;
        if (preLoad.getValue()) {
            if (mc.timer.timerSpeed != 1f) {
                balance += chargeMultiplier.getValue();
            } else {
                balance++;
            }
        } else {
            if (fast) {
                balance += chargeMultiplier.getValue();
            } else {
                balance++;
            }
        }

        if (shouldStop()) {
            if (fast) {
                release();
                mc.timer.timerSpeed = 1f;
                fast = false;
            }
        }
        super.onTick(event);
    }

    @Override
    public void onTimeDelay(Events.TimeDelay event) {
        balance--;
        super.onTimeDelay(event);
    }

    @Override
    public void onGameLoop(Events.GameLoop event) {
        EntityLivingBase target = KillAura.target;

        if (mc == null || mc.thePlayer == null || mc.theWorld == null
                || mc.thePlayer.ticksExisted < 10) {
            if (mc.thePlayer != null) {
                if (mc.thePlayer.ticksExisted < 20) {
                    mc.timer.timerSpeed = 1f;
                }
            }
            if (mc.timer.timerSpeed == slowTimer.getValue()) {
                mc.timer.timerSpeed = 1f;
            }
            target = null;
            return;
        }

        if (target != null && outOfRange()) {
            target = null;
        }

        if (fast) {
            if (preLoad.getValue() ? balance < lastBalance : balance < (smartMaxBalance + lastBalance)) {
                if (target != null) {
                    if (!isTargetCloseOrVisible()) {
                        if (isHurtTime()) {
                            try {
                                boolean shouldStop = false;
                                boolean shouldStopNext = false;
                                while (!shouldStop) {
                                    if (shouldStopNext) {
                                        shouldStop = true;
                                    }

                                    if (isTargetCloseOrVisible() || !isHurtTime()
                                            || (!preLoad.getValue() && shouldStop())
                                            || (preLoad.getValue() ? balance >= lastBalance
                                            : balance >= smartMaxBalance + lastBalance)) {
                                        shouldStopNext = true;
                                        delayTimer.reset();

                                        release();
                                        mc.timer.timerSpeed = 1f;
                                        fast = false;

                                        if (preLoad.getValue())
                                            delayTimer.reset();

                                        if (clickTimer.hasTimeElapsed(350)) {
                                            clickTimer.reset();
                                            GitHave.INSTANCE.eventManager.call(new Events.Attack(target));

//                                            Entity rayTracedEntity = RayCastUtil.rayTrace(3,
//                                                    new float[] { mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch });
                                            if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
                                                mc.clickMouse();
                                                System.out.println("Tickbase force");
                                            }
                                        }
                                    }
                                    if (!shouldStop) {
                                        mc.runTick();
                                        balance += chargeMultiplier.getValue();
                                    }

                                }
                            } catch (IOException e1) {
                            }
                        } else {
                            if (!preLoad.getValue()) {
                                mc.timer.timerSpeed = 1f;
                                fast = false;
                                if (preLoad.getValue())
                                    delayTimer.reset();
                            }
                        }
                    } else {
                        release();
                        mc.timer.timerSpeed = 1f;
                        fast = false;

                        if (preLoad.getValue())
                            delayTimer.reset();

                        if (clickTimer.hasTimeElapsed(350)) {
                            clickTimer.reset();
                            GitHave.INSTANCE.eventManager.call(new Events.Attack(target));

//                            Entity rayTracedEntity = RayCastUtil.rayTrace(3,
//                                    new float[] { mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch });
//                            if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
//                                mc.clickMouse();
//                                System.out.println("Tickbase force");
//                            }
                        }
                    }
                } else {
                    mc.timer.timerSpeed = 1f;
                    fast = false;
                    if (preLoad.getValue())
                        delayTimer.reset();
                }
            } else {
                mc.timer.timerSpeed = 1f;
                fast = false;
                if (preLoad.getValue())
                    delayTimer.reset();
            }
        }
        if (!fast) {
            if (preLoad.getValue()) {
                if (!delayTimer.hasTimeElapsed((long) delay.getValue())) {
                    return;
                }
                if (target != null) {
                    if (!shouldStop()) {
                        if (mc.timer.timerSpeed == 1f)
                            setSmartBalance();
                    }

                    if (!isTargetCloseOrVisible() && isHurtTime()) {
                        if (balance > -smartMaxBalance + lastBalance) {
                            if (shouldStop()) {
                                if (mc.timer.timerSpeed != (float) slowTimer.getValue())
                                    lastBalance = balance;
                                mc.timer.timerSpeed = 1f;
                                return;
                            }

                            mc.timer.timerSpeed = (float) slowTimer.getValue();
                        } else {
                            fast = true;
                            mc.timer.timerSpeed = 1f;
                        }
                    } else {
                        if (mc.timer.timerSpeed != (float) slowTimer.getValue())
                            lastBalance = balance;
                        mc.timer.timerSpeed = 1f;
                    }
                } else {
                    if (mc.timer.timerSpeed != (float) slowTimer.getValue())
                        lastBalance = balance;
                    mc.timer.timerSpeed = 1f;
                }
                release();
            } else {
                if (balance > lastBalance) {
                    mc.timer.timerSpeed = (float) slowTimer.getValue();
                } else {
                    if (mc.timer.timerSpeed == (float) slowTimer.getValue()) {
                        mc.timer.timerSpeed = 1f;
                    }

                    if (!delayTimer.hasTimeElapsed((long) delay.getValue())) {
                        return;
                    }

                    if (target != null) {
                        if (!isTargetCloseOrVisible() && isHurtTime()) {
                            fast = true;
                            delayTimer.reset();
                            lastBalance = balance;
                        }
                    }
                }
                release();
            }
            if (fast) {
                if (!preLoad.getValue()) {
                    setSmartBalance();
                }
            }
        }
        if (mc.thePlayer.ticksExisted <= 20) {
            mc.timer.timerSpeed = 1f;
        }
        super.onGameLoop(event);
    }

    public void release() {
        try {
            while (!outPackets.isEmpty()) {
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(outPackets.poll());
            }
        } catch (Exception ex) {

        }
    }

    public void setSmartBalance() {
        EntityLivingBase target = KillAura.target;

        double predictX = target.posX + ((target.posX - target.lastTickPosX) * 2);
        double predictZ = target.posZ + ((target.posZ - target.lastTickPosZ) * 2);

        double distance = mc.thePlayer.getDistanceToEntity(target) - Math.abs(distanceAdjust());

        if (target == null) {
            smartMaxBalance = 0;
            return;
        }
        if (shouldStop()) {
            smartMaxBalance = 0;
            return;
        }

        double playerBPS = Math
                .sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);

        double targetMotionX = Math.abs(target.lastTickPosX - target.posX);
        double targetMotionZ = Math.abs(target.lastTickPosZ - target.posZ);
        double targetBPS = Math.sqrt(targetMotionX * targetMotionX + targetMotionZ * targetMotionZ);

        playerBPS = Math.max(0.15, playerBPS);
        targetBPS = Math.max(preLoad.getValue() ? 0.15 : 0, targetBPS);

        double finalDistance = distance - 3;

        if (preLoad.getValue()) {
            // +1が動くかどうか
            smartMaxBalance = 1 + finalDistance / (playerBPS + (targetBPS * 3));
        } else {
            smartMaxBalance = 1 + finalDistance / (playerBPS * 2);
        }
    }

    public boolean shouldStop() {
        EntityLivingBase target = KillAura.target;
        boolean stop = false;

        if (target == null) {
            stop = true;
            return stop;
        }

        double predictX = mc.thePlayer.posX + ((mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * 2);
        double predictZ = mc.thePlayer.posZ + ((mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * 2);

        float f = (float) (predictX - target.posX);
        float f1 = (float) (mc.thePlayer.posY - target.posY);
        float f2 = (float) (predictZ - target.posZ);

        double predictedDistance = Math.sqrt(f * f + f1 * f1 + f2 * f2);

        if (onlyOnGround.getValue() && !mc.thePlayer.onGround) {
            stop = true;
        }

        if (mc.thePlayer.getNearestDistanceToEntity(target) < minRange.getValue()) {
            if (preLoad.getValue()) {
                if (!fast) {
                    stop = true;
                }
            } else {
                if (!fast) {
                    if (mc.timer.timerSpeed != slowTimer.getValue()) {
                        stop = true;
                    }
                }
            }
        }

        if (isTargetCloseOrVisible()) {
            stop = true;
        }

        if (!isHurtTime()) {
            stop = true;
        }

        if (outOfRange()) {
            stop = true;
        }

        if ((MoveUtil.getSpeed() <= 0.08 || !mc.gameSettings.keyBindForward.pressed
                || predictedDistance > mc.thePlayer.getDistanceToEntity(target) + 0.08) && onlyForward.getValue()) {
            stop = true;
        }

        if (outOfRange()) {
            stop = true;
        }
        if (noFluid.getValue() && (mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || mc.thePlayer.isInWeb)) {
            stop = true;
        }

        if (mc.thePlayer.getDistance(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ) < mc.thePlayer
                .getDistance(target.posX, target.posY, target.posZ)) {
            stop = notInCombo.getValue();
        }

        if (Backtrack.isStaticToggled() && !Backtrack.packets.isEmpty()) {
            if (mc.thePlayer.getDistance(target.posX, target.posY, target.posZ) < mc.thePlayer
                    .getDistance(target.realPosX, target.realPosY, target.realPosZ)) {
                stop = notInCombo.getValue();
            }
        }

        return fast ? stop : preLoad.getValue() ? stop : false;
    }

    public boolean outOfRange() {
        EntityLivingBase target = KillAura.target;
        return mc.thePlayer.getDistanceToEntity(target) > getMaxDistance() + distanceAdjust();
    }

    public boolean isTargetCloseOrVisible() {
        EntityLivingBase target = KillAura.target;
        if (target == null) return false;
        return mc.objectMouseOver.entityHit != null || mc.thePlayer.getNearestDistanceToEntity(target) <= 3;
    }

    public boolean isHurtTime() {
        return mc.thePlayer.hurtTime <= (!preLoad.getValue() ? 10 : 10);
    }

    public double distanceAdjust() {
        EntityLivingBase target = KillAura.target;
        if (mc.thePlayer.getDistance(target.lastTickPosX, target.lastTickPosY,
                target.lastTickPosZ) < mc.thePlayer.getDistance(target.posX, target.posY, target.posZ) - 0.05) {
            return -0.5;
        } else if (mc.thePlayer.getDistance(target.lastTickPosX, target.lastTickPosY,
                target.lastTickPosZ) > mc.thePlayer.getDistance(target.posX, target.posY, target.posZ) + 0.1) {
            return 0.3;
        }

        return 0;
    }

    public double getMaxDistance() {
        return maxRange.getValue() + 1;
    }
}
