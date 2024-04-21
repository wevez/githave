package githave.module.impl.combat;

import githave.event.Events;
import githave.manager.rotation.RotationManager;
import githave.util.*;
import githave.util.bypass.BypassRotation;
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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura extends Module {

    public static EntityLivingBase target;
    public static boolean blocking;

    private Comparator<EntityLivingBase> currentComparator = Comparator.comparingDouble(e -> mc.thePlayer.getNearestDistanceToEntity(e));

    private final ModeSetting targetMode = new ModeSetting.Builder("Target Mode", "Single", "Switch")
            .onUpdate(v -> suffix = v)
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

    private final DoubleSetting minCPS = new DoubleSetting.Builder("Min CPS", 12, 0, 20, 0.1)
            .visibility(() -> !clickMode.getValue().equals("1.9+"))
            .build();

    private final DoubleSetting maxCPS = new DoubleSetting.Builder("Max CPS", 15, 0, 20, 0.1)
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

    private IndependentCPS cpsTimer;

    @Override
    public void init() {
        cpsTimer = new IndependentCPS(this.minCPS, this.maxCPS);
        super.init();
    }

    @Override
    protected void onEnable() {
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        target = null;
        unblock();
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
//            System.out.println("Yes");
        } else {
//            System.out.println("No");
        }
        System.out.println(target.getHealth() + target.getAbsorptionAmount());
        super.onTick(event);
    }

    @Override
    public void onRotation(Events.Rotation event) {
        updateTarget();
        if (target == null) {
            unblock();
            return;
        }
        block();
        float[] rot = calcRotation();
        if (rot != null) {
            event.yaw = rot[0];
            event.pitch = rot[1];
//            RotationManager.virtualPrevYaw = RotationManager.virtualYaw = event.yaw = rot[0];
//            RotationManager.virtualPitch = RotationManager.virtualPrevPitch = event.pitch = rot[1];
        }
        super.onRotation(event);
    }

    @Override
    public void onRender3D(Events.Render3D event) {
        super.onRender3D(event);
    }

    private float[] calcRotation() {
        final AxisAlignedBB box = target.getEntityBoundingBox().expand(-0.02, -0.25, -0.02);
        Vec3 eye = mc.thePlayer.getPositionEyes(1).addVector(0, -Math.abs(mc.thePlayer.motionY) * 0.1, 0);
        Vec3 center = null;
        // mc.theWorld.rayTraceBlocks(eye, center, false).typeOfHitがNullPointerException起きる
//        float min = 0;
//        float[] f = RotationUtil.rotation(AlgebraUtil.nearest(box));
//        for (double x = box.minX; x <= box.maxX; x += 0.1) {
//            for (double y = box.minY; y <= box.maxY; y += 0.1) {
//                for (double z = box.minZ; z <= box.maxZ; z += 0.1) {
//                    if (mc.thePlayer.getDistanceSq(x, y, z) > 3) continue;;
//                    float current = RotationUtil.distSq(f, RotationUtil.rotation(x, y, z));
//                    if (current <= min) continue;
////                    if (mc.theWorld.rayTraceBlocks(eye, center, false).typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) continue;
//                    min = current;
//                    center = new Vec3(x, y, z);
//                }
//            }
//        }
        if (center == null) center = box.center();
        float[] rotation = RotationUtil.rotation(center);

        return BypassRotation.getInstance().limitAngle(
                new float[] { mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch },
                rotation,
                center,
                target
        );
    }

    private void unblock() {
        blocking = false;
    }

    private void block() {
        blocking = true;
    }

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
                            case "Team Color": {
                                if (e.getTeam() == null) return mc.thePlayer.getTeam() != null;
                                return !e.getTeam().isSameTeam(mc.thePlayer.getTeam());
                            }
                        }
                    }
                    return false;
                })
                 .sorted(currentComparator)
                .collect(Collectors.toList());

         target = entry.isEmpty() ? null : entry.get(0);
    }
}
