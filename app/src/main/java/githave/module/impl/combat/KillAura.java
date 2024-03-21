package githave.module.impl.combat;

import githave.event.Events;
import githave.manager.rotation.RotationManager;
import githave.util.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.BooleanSetting;
import githave.module.setting.impl.DoubleSetting;
import githave.module.setting.impl.ModeSetting;
import githave.module.setting.impl.MultiBooleanSetting;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class KillAura extends Module {

    public static EntityLivingBase target;

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

    private final BooleanSetting throughWalls = new BooleanSetting.Builder("Through Walls")
            .build();

    private final ModeSetting clickMode = new ModeSetting.Builder("Click Mode", "Normal", "Timing", "1.9+")
            .build();

    private final DoubleSetting minCPS = new DoubleSetting.Builder("Min CPS", 8, 0, 20, 0.1)
            .visibility(() -> !clickMode.getValue().equals("1.9+"))
            .build();

    private final DoubleSetting maxCPS = new DoubleSetting.Builder("Max CPS", 12, 0, 20, 0.1)
            .visibility(() -> !clickMode.getValue().equals("1.9+"))
            .build();

    public KillAura() {
        super("KillAura", "Attacks entities around you", ModuleCategory.Combat);
        this.getSettingList().addAll(Arrays.asList(
           targetMode, targets, teams, sortMode, blockMode, aimRange, attackRange, blockRange, throughWalls, clickMode, minCPS, maxCPS
        ));
    }

    @Override
    protected void onEnable() {
        RotationManager.serverYaw = mc.thePlayer.rotationYaw;
        RotationManager.serverPitch = mc.thePlayer.rotationPitch;
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
        super.onTick(event);
    }

    @Override
    public void onRenderRotation(Events.RenderRotation event) {
        super.onRenderRotation(event);
    }

    @Override
    public void onLook(Events.Look event) {
        super.onLook(event);
    }

    @Override
    public void onJump(Events.Jump event) {
        super.onJump(event);
    }

    @Override
    public void onMotion(Events.Motion event) {
        super.onMotion(event);
    }

    @Override
    public void onMove(Events.Move event) {
        super.onMove(event);
    }

    private void unblock() {

    }

    private void block() {}

    private void updateTarget() {
         List<EntityLivingBase> entry = mc.theWorld.loadedEntityList.stream()
                .filter(e -> e instanceof EntityLivingBase && e.getNearestDistanceToEntity(mc.thePlayer) > aimRange.getValue())
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
    }
}
