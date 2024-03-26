package githave.module.impl.combat;

import githave.event.Events;
import githave.manager.rotation.RotationManager;
import githave.util.*;
import githave.util.bypass.IndependentCPS;
import net.minecraft.entity.EntityLivingBase;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.DoubleSetting;
import githave.module.setting.impl.ModeSetting;
import githave.module.setting.impl.MultiBooleanSetting;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura extends Module {

    public static EntityLivingBase target;

    private boolean allowToRot;

    private final IndependentCPS cpsTimer = new IndependentCPS(this.minCPS, this.maxCPS);

    private Comparator<EntityLivingBase> currentComparator = Comparator.comparingDouble(e -> mc.thePlayer.getNearestDistanceToEntity(e));

    private final ModeSetting targetMode = new ModeSetting.Builder("Target Mode", "Single", "Switch")
            .build();

    private final MultiBooleanSetting targets = new MultiBooleanSetting.Builder("Targets", "Animals", "Monsters", "Villagers", "Players")
            .value("Players", true)
            .build();

    private final ModeSetting teams = new ModeSetting.Builder("Teams", "None", "Team Color")
            .build();

    private final ModeSetting blockMode = new ModeSetting.Builder("Block Mode", "None", "NCP", "Legit")
            .build();

    private final DoubleSetting aimRange = new DoubleSetting.Builder("Aim Range", 4, 0, 8, 0.1)
            .build();

    private final ModeSetting sortMode = new ModeSetting.Builder("Sort Mode", "Distance", "Health", "High Armor", "Low Armor")
            .onUpdate(v -> {
                switch (v) {
                    case "Distance":
                        currentComparator = Comparator.comparingDouble(e -> mc.thePlayer.getNearestDistanceToEntity(e));
                        break;
                        // TODO
                }
            })
            .build();

    private final DoubleSetting attackRange = new DoubleSetting.Builder("Attack Range", 3, 0, 8, 0.1)
            .build();

    private final DoubleSetting blockRange = new DoubleSetting.Builder("Block Range", 4, 0, 8, 0.1)
            .visibility(() -> !blockMode.getValue().equals("None"))
            .build();

    private final ModeSetting clickMode = new ModeSetting.Builder("Click Mode", "Normal", "Timing", "1.9+")
            .build();

    private final DoubleSetting minCPS = new DoubleSetting.Builder("Min CPS", 8, 0, 20, 0.1)
            .visibility(() -> !clickMode.getValue().equals("1.9+"))
            .build();

    private final DoubleSetting maxCPS = new DoubleSetting.Builder("Max CPS", 12, 0, 20, 0.1)
            .visibility(() -> !clickMode.getValue().equals("1.9+"))
            .build();

    private final ModeSetting rotationMode = new ModeSetting.Builder("Rotation Mode", "Test", "Nearest")
            .build();

    private final DoubleSetting minYawSpeed = new DoubleSetting.Builder("Min Yaw Speed", 50, 0, 180, 1)
            .build();

    private final DoubleSetting maxYawSpeed = new DoubleSetting.Builder("Max Yaw Speed", 180, 0, 180, 1)
            .build();

    private final DoubleSetting minPitchSpeed = new DoubleSetting.Builder("Min Pitch Speed", 50, 0, 180, 1)
            .build();

    private final DoubleSetting maxPitchSpeed = new DoubleSetting.Builder("Max Pitch Speed", 180, 0, 180, 1)
            .build();

    public KillAura() {
        super("KillAura", "Attacks entities around you", ModuleCategory.Combat);
        this.setKeyCode(Keyboard.KEY_R);
        this.getSettingList().addAll(Arrays.asList(
           targetMode, targets, teams, sortMode, blockMode, aimRange, attackRange, blockRange, clickMode, minCPS, maxCPS,
                rotationMode, minYawSpeed, maxYawSpeed, minPitchSpeed, maxPitchSpeed
        ));
    }

    @Override
    protected void onEnable() {
        RotationManager.serverYaw = mc.thePlayer.rotationYaw;
        RotationManager.serverPitch = mc.thePlayer.rotationPitch;
        allowToRot = false;
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        target = null;
        unblock();
        RotationManager.customRots = false;
        mc.timer.timerSpeed = 1f;
        super.onDisable();
    }

    @Override
    public void onGameLoop(Events.GameLoop event) {
        super.onGameLoop(event);
    }

    @Override
    public void onTick(Events.Tick event) {
        if (target == null) return;
        if (cpsTimer.onTick()) {
            mc.clickMouse();
        }
        super.onTick(event);
    }

    @Override
    public void onRenderRotation(Events.RenderRotation event) {
        if (allowToRot && RotationManager.customRots) {
            event.yaw = RotationManager.serverYaw;
            event.pitch = RotationManager.serverPitch;
        }
        super.onRenderRotation(event);
    }

    @Override
    public void onLook(Events.Look event) {
        updateTarget();
        RotationManager.customRots = target != null;
        if (target != null) {
            allowToRot = mc.currentScreen == null;
            calcRotation();
        }
        if (allowToRot && RotationManager.customRots) {
            event.yaw = RotationManager.serverYaw;
            event.pitch = RotationManager.serverPitch;
        }
        super.onLook(event);
    }

    @Override
    public void onJump(Events.Jump event) {
        if (allowToRot && RotationManager.customRots) {
            event.yaw = RotationManager.serverYaw;
        }
        super.onJump(event);
    }

    @Override
    public void onMotion(Events.Motion event) {
        if (allowToRot && RotationManager.customRots) {
            event.yaw = RotationManager.serverYaw;
            event.pitch = RotationManager.serverPitch;
        }
        super.onMotion(event);
    }

    @Override
    public void onMoveFlying(Events.MoveFlying event) {
        if (allowToRot && RotationManager.customRots) {
            event.yaw = RotationManager.serverYaw;
        }
        super.onMoveFlying(event);
    }

    @Override
    public void onRender3D(Events.Render3D event) {
        super.onRender3D(event);
    }


    // For rotation bypasses
    private float aYaw, aPitch;
    private long next;

    private void calcRotation() {
        Vec3 eye = mc.thePlayer.getPositionEyes(1f);
        AxisAlignedBB bb = target.getEntityBoundingBox();
        Vec3 nearest = AlgebraUtil.nearest(bb, eye);
        if (RayCastUtil.rayTrace(attackRange.getValue() + 1, new float[] { RotationManager.serverYaw, RotationManager.serverPitch }) == target) {
//        if (bb.intersects(eye, eye.add(mc.player.getRotationVec(1f).multiply(6)))) {
            if (System.currentTimeMillis() > next) {
                final float[] center = RotationUtil.rotation(target.getPositionEyes(1f).addVector(0, -0.3, 0), eye);
                next = System.currentTimeMillis() + RandomUtil.nextInt(50);
                aYaw = RandomUtil.nextFloat(0.3f) * MathHelper.wrapAngleTo180_float(
                        center[0] - RotationManager.serverYaw
                );
                aPitch = RandomUtil.nextFloat(0.3f) * MathHelper.wrapAngleTo180_float(
                        center[1] - RotationManager.serverPitch
                );
            }
            float[] r = { RotationManager.serverYaw + aYaw * RandomUtil.nextFloat(1),
                    RotationManager.serverPitch + aPitch * RandomUtil.nextFloat(1) };
            r = RotationUtil.getFixedRotation(r, new float[] { RotationManager.serverYaw, RotationManager.serverPitch });
            RotationManager.serverYaw = r[0];
            RotationManager.serverPitch = r[1];
            return;
        }
        float[] z = RotationUtil.rotation(nearest.addVector(
                RandomUtil.nextDouble(-0.1f, 0.1),
                RandomUtil.nextDouble(-0.1f, 0.1),
                RandomUtil.nextDouble(-0.1f, 0.1)
        ), eye);
        z[0] = RotationUtil.smoothRot(RotationManager.serverYaw, z[0], RandomUtil.nextFloat(25f, 30));
        z[1] = RotationUtil.smoothRot(RotationManager.serverPitch, z[1], RandomUtil.nextFloat(25f, 30));
        z[1] += (float) (Math.sin(MathHelper.wrapAngleTo180_double(RotationManager.serverYaw - z[0]) / 5) * 5);
        z = RotationUtil.getFixedRotation(z, new float[] { RotationManager.serverYaw, RotationManager.serverPitch });
//        mc.thePlayer.rotationYaw = rots[0];
//        mc.thePlayer.rotationPitch = rots[1];
        RotationManager.serverYaw = z[0];
        RotationManager.serverPitch = z[1];
    }

    private void unblock() {}

    private void block() {}

    private void updateTarget() {
         List<EntityLivingBase> entry = mc.theWorld.loadedEntityList.stream()
                .filter(e -> e instanceof EntityLivingBase && e.getNearestDistanceToEntity(mc.thePlayer) < aimRange.getValue())
                .map(e -> (EntityLivingBase) e)
                .filter(e -> {
                    if (e == mc.thePlayer) return false;
                    if (e instanceof EntityAnimal) return targets.getValue().get("Animals");
                    if (e instanceof EntityMob) return targets.getValue().get("Monsters");
                    if (e instanceof EntityVillager) return targets.getValue().get("Villagers");
                    if (e instanceof EntityPlayer) {
                        if (!targets.getValue().get("Players")) return false;
                        switch (teams.getValue()) {
                            case "None": return true;
                            case "Team Color": return !e.getTeam().isSameTeam(mc.thePlayer.getTeam());
                        }
                    }
                    return false;
                })
                 .sorted(currentComparator)
                .collect(Collectors.toList());

         target = entry.isEmpty() ? null : entry.get(0);
    }
}
