package githave.module.impl.combat;

import githave.event.Events;
import githave.manager.RotationManager;
import githave.module.setting.impl.BooleanSetting;
import githave.util.*;
import githave.util.bypass.BypassRotation;
import githave.util.bypass.IndependentCPS;
import githave.util.render.Render3DUtil;
import net.minecraft.client.entity.EntityPlayerSP;
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
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.*;
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

    private final ModeSetting sortMode = new ModeSetting.Builder("Sort Mode", "Distance", "Health", "High Armor", "Low Armor", "Angle", "Client Angle")
            .onUpdate(v -> {
                switch (v) {
                    case "Distance":
                        currentComparator = Comparator.comparingDouble(e -> mc.thePlayer.getNearestDistanceToEntity(e));
                        break;
                    case "Health":
                        currentComparator = Comparator.comparingDouble(e -> e.getAbsorptionAmount() + e.getHealth());
                        break;
                    case "High Armor":
                        // TODO: Is this working collect?
                        currentComparator = Comparator.comparingDouble(e -> -e.getTotalArmorValue());
                        break;
                    case "Low Armor":
                        currentComparator = Comparator.comparingDouble(e -> e.getTotalArmorValue());
                        break;
                    case "Angle":
                        currentComparator = Comparator.comparingDouble(e -> RotationUtil.distSq(RotationUtil.rotation(e.getPositionVector()), new float[] {
                                RotationManager.virtualYaw,
                                RotationManager.virtualPitch
                        }));
                    break;
                    case "Client Angle":
                        currentComparator = Comparator.comparingDouble(e -> RotationUtil.distSq(RotationUtil.rotation(e.getPositionVector()), new float[] {
                                RotationManager.packetYaw,
                                RotationManager.packetPitch
                        }));
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

    private final ModeSetting rotateMethod = new ModeSetting.Builder("Rotate Method", "Legit", "Packet", "None")
            .build();

    private final ModeSetting rotationMode = new ModeSetting.Builder("Rotation Mode", "Test", "Nearest")
            .build();

    private final BooleanSetting rayTrace = new BooleanSetting.Builder("RayTrace")
            .value(true)
            .build();

    private final ModeSetting rotationPointer = new ModeSetting.Builder("Rotation Pointer", "Box", "None")
            .build();

    public KillAura() {
        super("KillAura", "Attacks entities around you", ModuleCategory.Combat);
        this.setKeyCode(Keyboard.KEY_R);
        this.getSettingList().addAll(Arrays.asList(
           targetMode, targets, teams, sortMode, blockMode, aimRange, attackRange, blockRange, clickMode, minCPS, maxCPS,
                rotationMode, rayTrace, rotateMethod, rotationPointer
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
        tickMap.clear();
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

    private final Map<Integer, Long> tickMap = new HashMap<>();

    @Override
    public void onTick(Events.Tick event) {
        if (target == null) return;
        final boolean tick = this.cpsTimer.onTick();
        switch (clickMode.getValue()) {
            case "Timing":
                if (mc.thePlayer.hurtTime != 0) {
                    if (tick) {
                        if (canHit()) tickMap.put(target.getEntityId(), System.currentTimeMillis());
                        handleAttack();
                    }
                } else {
                    if (canHit()) {
                        if (!tickMap.containsKey(target.getEntityId())) {
                            tickMap.put(target.getEntityId(), 0L);
                        }
                        if (System.currentTimeMillis() - tickMap.get(target.getEntityId()) > 500) {
                            handleAttack();
                            tickMap.put(target.getEntityId(), System.currentTimeMillis());
                        }
                    }
                }
                break;
            case "1.9+":
                // TODO
                break;
            case "Normal":
                if (tick) handleAttack();
                break;
        }
        super.onTick(event);
    }

    private boolean canHit() {
        return !rayTrace.getValue() || (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.objectMouseOver.entityHit == target);
    }

    private void handleAttack() {
        if (rayTrace.getValue()) {
            mc.clickMouse();
        } else {
            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(mc.thePlayer, target);
        }
    }

    private boolean rotated;

    @Override
    public void onRotation(Events.Rotation event) {
        updateTarget();
        if (target == null) {
            unblock();
            if (rotated) {
                rotated = false;
                if (rotationMode.getValue().equals("Test")) {
                    EntityPlayerSP.resetTimer.reset();
                }
            }
            return;
        }
        if (rotateMethod.getValue().equals("Legit")) {
            currentRotation = getRotation();
            rotated = true;
            if (currentRotation != null) {
                event.yaw = currentRotation[0];
                event.pitch = currentRotation[1];
            }
        }
        super.onRotation(event);
    }

    private float[] currentRotation;

    @Override
    public void onSendPacket(Events.SendPacket event) {
        if (rotateMethod.getValue().equals("Packet") && event.packet instanceof C03PacketPlayer) {
            C03PacketPlayer packet = (C03PacketPlayer) event.packet;
            if (currentRotation != null) {
                packet.yaw = currentRotation[0];
                packet.pitch = currentRotation[1];
            }
        }
        super.onSendPacket(event);
    }

    private Vec3 currentPointer, lastPointer;

    @Override
    public void onRender3D(Events.Render3D event) {
        if (target == null || currentPointer == null || lastPointer == null) return;
        switch (rotationPointer.getValue()) {
            case "Box":
                final double boxSize = 0.1;
                Vec3 partial = AlgebraUtil.partialVec(lastPointer, currentPointer, mc.timer.renderPartialTicks);
                Render3DUtil.drawFilledBox(
                        partial.xCoord - boxSize,
                        partial.yCoord - boxSize,
                        partial.zCoord - boxSize,
                        partial.xCoord + boxSize,
                        partial.yCoord + boxSize,
                        partial.zCoord + boxSize,
                        0xFFFF0000
                );
                break;
        }
        super.onRender3D(event);
    }

    private float[] getRotation() {
        switch (rotationMode.getValue()) {
            case "Test":
                return calcRotationLegit();
            case "Nearest":
                return calcRotationNearest();
        }
        return null;
    }

    private float[] calcRotationNearest() {
        lastPointer = currentPointer;
        final AxisAlignedBB box = target.getEntityBoundingBox().expand(-0.02, -0.25, -0.02);
        final Vec3 eye = mc.thePlayer.getPositionEyes(1f);
        Vec3 center = null;
        float min = Float.MAX_VALUE;
        for (double x = box.minX; x <= box.maxX; x += 0.1) {
            for (double y = box.minY; y <= box.maxY; y += 0.1) {
                for (double z = box.minZ; z <= box.maxZ; z += 0.1) {
                    if (mc.thePlayer.getDistanceSq(x, y, z) > attackRange.getValue()) continue;;
                    float current = (float) mc.thePlayer.getDistanceSq(x, y, z);
                    if (current > min) continue;
                    Vec3 g = new Vec3(x, y, z);
                    if (mc.theWorld.rayTraceBlocks(eye, g, false) != null) continue;
                    min = current;
                    center = g;
                }
            }
        }
        if (center == null) center = AlgebraUtil.nearest(box);
        currentPointer = center;
        float[] rotation = RotationUtil.rotation(center);
        return rotation;
    }

    private float[] calcRotationLegit() {
        lastPointer = currentPointer;
        final AxisAlignedBB box = target.getEntityBoundingBox().expand(-0.02, -0.25, -0.02);
        final Vec3 eye = mc.thePlayer.getPositionEyes(1f);
        Vec3 center = null;
        float min = 0;
        for (double x = box.minX; x <= box.maxX; x += 0.1) {
            for (double y = box.minY; y <= box.maxY; y += 0.1) {
                for (double z = box.minZ; z <= box.maxZ; z += 0.1) {
                    if (mc.thePlayer.getDistanceSq(x, y, z) > attackRange.getValue()) continue;;
                    float current = (float) mc.thePlayer.getDistanceSq(x, y, z);
                    if (current <= min) continue;
                    Vec3 g = new Vec3(x, y, z);
                    if (mc.theWorld.rayTraceBlocks(eye, g, false) != null) continue;
                    min = current;
                    center = g;
                }
            }
        }
        if (center == null) center = AlgebraUtil.nearest(box);
        currentPointer = center;
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
