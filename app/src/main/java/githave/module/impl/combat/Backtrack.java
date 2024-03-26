package githave.module.impl.combat;

import com.mojang.authlib.GameProfile;
import com.sun.org.apache.xpath.internal.operations.Bool;
import githave.event.EventArgument;
import githave.event.Events;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.impl.BooleanSetting;
import githave.module.setting.impl.DoubleSetting;
import githave.module.setting.impl.ModeSetting;
import githave.util.TimerUtil;
import githave.util.render.ColorUtil;
import githave.util.render.Render3DUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Backtrack extends Module {

    public static List<Packet> packets = new ArrayList<>();

    private WorldClient lastWorld;
    private EntityLivingBase entity;

    public TimerUtil timer = new TimerUtil();
    public TimerUtil timer2 = new TimerUtil();

    private EntityOtherPlayerMP entityReal;

    public double delayValue = 300;

    private final DoubleSetting range = new DoubleSetting.Builder("Range", 3, 0, 8, 0.1)
            .build();

    private final DoubleSetting delay = new DoubleSetting.Builder("Delay", 150, 0, 1000, 10)
            .build();

    private final BooleanSetting smart = new BooleanSetting.Builder("Smart")
            .build();

    private final ModeSetting espMode = new ModeSetting.Builder("ESP", "None", "Real")
            .build();

    public Backtrack() {
        super("Backtrack", "Allows you to attack old positions", ModuleCategory.Combat);
        this.getSettingList().addAll(Arrays.asList(
                range,
                delay
        ));
    }

    @Override
    protected void onEnable() {
        packets.clear();
        super.onEnable();
    }

    @Override
    public void onGetPacket(Events.GetPacket event) {
        if (mc.thePlayer == null || mc.theWorld == null || mc.getNetHandler().getNetworkManager().getNetHandler() == null) {
            packets.clear();
            return;
        }
//        if (Client.INSTANCE.getModuleManager().getModule(ScaffoldModule.class).isEnabled()) {
//            packets.clear();
//            return;
//        }

        if (event.packet instanceof S14PacketEntity) {
            S14PacketEntity packet = (S14PacketEntity) event.packet;
            Entity entity = mc.theWorld.getEntityByID(packet.entityId);

            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                entityLivingBase.realPosX += packet.func_149062_c();
                entityLivingBase.realPosY += packet.func_149061_d();
                entityLivingBase.realPosZ += packet.func_149064_e();
            }
        }

        if (event.packet instanceof S18PacketEntityTeleport) {
            S18PacketEntityTeleport packet = (S18PacketEntityTeleport) event.packet;
            final Entity entity = mc.theWorld.getEntityByID(packet.getEntityId());

            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                entityLivingBase.realPosX = packet.getX();
                entityLivingBase.realPosY = packet.getY();
                entityLivingBase.realPosZ = packet.getZ();
            }
        }
        entity = KillAura.target;
        if (mc.theWorld != null) {
            if (lastWorld != mc.theWorld) {
                resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                lastWorld = mc.theWorld;
                return;
            }
        }

        if (entity == null) {
            resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
        } else {
            addPackets(event.packet, event);
        }
        super.onGetPacket(event);
    }

    @Override
    public void onGameLoop(Events.GameLoop event) {
        if (entity != null && entity.getEntityBoundingBox() != null && mc.thePlayer != null && mc.theWorld != null
                && entity.realPosX != 0 && entity.realPosY != 0 && entity.realPosZ != 0 && entity.width != 0
                && entity.height != 0) {

            boolean work = false;
            double realX = entity.realPosX / 32;
            double realY = entity.realPosY / 32;
            double realZ = entity.realPosZ / 32;

            if(!smart.getValue()) {
                if(mc.thePlayer.getDistance(entity.posX, entity.posY, entity.posZ) > 3) {
                    if (mc.thePlayer.getDistance(entity.posX, entity.posY, entity.posZ) >= mc.thePlayer.getDistance(realX,
                            realY, realZ)) {
                        resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                    }
                }
            }else {
                if (mc.thePlayer.getDistance(entity.posX, entity.posY, entity.posZ) >= mc.thePlayer.getDistance(realX,
                        realY, realZ)) {
                    resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                }
            }

            if (mc.thePlayer.getDistanceToEntity(entity) > 3)
                work = true;

            if (!smart.getValue())
                work = true;

            if (!work) {
                if (mc.thePlayer.getDistance(realX, realY, realZ) <= 3) {
                    resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                }
                releasePacketsToDistance(mc.getNetHandler().getNetworkManager().getNetHandler());
            }

            if (mc.thePlayer.getDistance(realX, realY, realZ) > range.getValue()
                    || timer.hasTimeElapsed((long) delay.getValue())) {
                timer.reset();
                resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
            }
        }
        super.onGameLoop(event);
    }

    @Override
    public void onRender3D(Events.Render3D event) {
        if (entity != null && entity.getEntityBoundingBox() != null && mc.thePlayer != null && mc.theWorld != null
                && entity.realPosX != 0 && entity.realPosY != 0 && entity.realPosZ != 0 && entity.width != 0
                && entity.height != 0) {

            boolean render = true;
            boolean work = false;
            double realX = entity.realPosX / 32;
            double realY = entity.realPosY / 32;
            double realZ = entity.realPosZ / 32;

            if (mc.thePlayer.getDistance(entity.posX, entity.posY, entity.posZ) >= mc.thePlayer.getDistance(realX,
                    realY, realZ)) {
                render = false;
            }

            if (mc.thePlayer.getDistanceToEntity(entity) > 3)
                work = true;
            if (!smart.getValue())
                work = true;

            if (!work) {
                if (mc.thePlayer.getDistance(realX, realY, realZ) <= 3) {
                    render = false;
                }
                if (mc.thePlayer.getDistanceToEntity(entity) < 3) {
                    render = false;
                }
            }

            if (smart.getValue() && mc.thePlayer.hurtTime > 3) {
                render = false;
            }

            if (mc.thePlayer.getDistance(realX, realY, realZ) > range.getValue()
                    || timer.hasTimeElapsed((long) delay.getValue())) {
                render = false;
            }

            if (entity != null && entity != mc.thePlayer && !entity.isInvisible() && render) {
                if(entity == null || entity.width == 0 || entity.height == 0)return;

                double x = entity.realPosX / 32D - mc.getRenderManager().renderPosX;
                double y = entity.realPosY / 32D - mc.getRenderManager().renderPosY;
                double z = entity.realPosZ / 32D - mc.getRenderManager().renderPosZ;

                ColorUtil.glColor(0xa0ff0000);
                Render3DUtil.drawBoundingBox(
                        x - entity.width / 2,
                        y,
                        z - entity.width / 2,
                        x + entity.width / 2,
                        y + entity.height,
                        z + entity.width / 2
                );
                ColorUtil.resetColor();
            }
        }
        super.onRender3D(event);
    }

    private void releasePacketsToDistance(INetHandler netHandler) {
        if(entity == null)return;

        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;

        if (!packets.isEmpty()) {
            synchronized (packets) {
                while (mc.thePlayer.getDistance(x, y, z) < 3 && !packets.isEmpty()) {
                    try {
                        packets.get(0).processPacket(netHandler);
                        if (packets.get(0) instanceof S14PacketEntity) {
                            S14PacketEntity packet = (S14PacketEntity) packets.get(0);
                            final Entity entity = mc.theWorld.getEntityByID(packet.entityId);

                            if (entity instanceof EntityLivingBase) {
                                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                                x += packet.func_149062_c();
                                y += packet.func_149061_d();
                                z += packet.func_149064_e();
                            }
                        }

                        if (packets.get(0) instanceof S18PacketEntityTeleport) {
                            S18PacketEntityTeleport packet = (S18PacketEntityTeleport) packets.get(0);
                            final Entity entity = mc.theWorld.getEntityByID(packet.getEntityId());

                            if (entity instanceof EntityLivingBase) {
                                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                                x = packet.getX();
                                y = packet.getY();
                                z = packet.getZ();
                            }
                        }
                    } catch (Exception ex) {
                    }
                    packets.remove(packets.get(0));
                }
            }
        }
    }

    private void resetPackets(INetHandler netHandler) {
        if (!packets.isEmpty()) {
            synchronized (packets) {
                while (packets.size() != 0) {
                    try {
                        packets.get(0).processPacket(netHandler);
                    } catch (Exception ex) {
                    }
                    packets.remove(packets.get(0));
                }
            }
        }
    }

    private void addPackets(Packet packet, EventArgument event) {
        if (event == null || packet == null)
            return;
        synchronized (packets) {
            if (this.blockPacket(packet)) {
                packets.add(packet);
                event.cancel();
            }
        }
    }

    private boolean isEntityPacket(Packet packet) {
        return (packet instanceof S14PacketEntity
                || packet instanceof net.minecraft.network.play.server.S19PacketEntityHeadLook
                || packet instanceof S18PacketEntityTeleport
                || packet instanceof net.minecraft.network.play.server.S0FPacketSpawnMob);
    }

    private boolean blockPacket(Packet packet) {
        if (packet instanceof net.minecraft.network.play.server.S03PacketTimeUpdate)
            return true;
        if (packet instanceof net.minecraft.network.play.server.S00PacketKeepAlive)
            return true;
        if (packet instanceof net.minecraft.network.play.server.S12PacketEntityVelocity)
            return true;
        if (packet instanceof net.minecraft.network.play.server.S27PacketExplosion)
            return true;
        if (packet instanceof net.minecraft.network.play.server.S32PacketConfirmTransaction) {
            return true;
        }
        return (packet instanceof S14PacketEntity
                || packet instanceof net.minecraft.network.play.server.S19PacketEntityHeadLook
                || packet instanceof S18PacketEntityTeleport
                || packet instanceof net.minecraft.network.play.server.S0FPacketSpawnMob
                || packet instanceof net.minecraft.network.play.server.S08PacketPlayerPosLook);
    }
}
