package githave.module.impl.combat;

import githave.event.Events;
import githave.module.ModeModule;
import githave.module.ModeObject;
import githave.module.ModuleCategory;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class Velocity extends ModeModule {

    public Velocity() {
        super("Veloccity", "Reduce your knockback", ModuleCategory.Combat, "Intave");
    }

    @Override
    protected ModeObject getObject(String value) {
        switch (value) {
            case "Intave":
                return new Intave();
        }
        return null;
    }

    private static class Intave extends ModeObject {

        private boolean blockVelocity;
        private int ticks;
        private boolean isWorking;

        private final boolean jump = false;

        @Override
        public void onUpdate(Events.Update event) {
            blockVelocity = true;

            if(mc.objectMouseOver == null) {
                return;
            }

            if(mc.objectMouseOver.entityHit != null && mc.thePlayer.hurtTime == 9 && !mc.thePlayer.isBurning() && jump) {
                mc.thePlayer.movementInput.jump = true;
                ticks++;
            }
            super.onUpdate(event);
        }

        @Override
        public void onKnockback(Events.Knockback event) {
//            event.motion = (0.6);
//            event.full = (!intaveStrong.isEnabled());
            event.full = false;
            event.reduceY = true;
//            event.reduceY = (verticalReduce.isEnabled());
            super.onKnockback(event);
        }

        @Override
        public void onGetPacket(Events.GetPacket event) {
            if (event.packet instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.packet;

                if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                    isWorking = true;

                    if (mc.thePlayer.isBurning()) {

                    }

                    if (mc.objectMouseOver.entityHit != null && mc.thePlayer.hurtTime == 9 && !mc.thePlayer.isBurning() && jump) {
                        mc.thePlayer.movementInput.jump = true;
                        ticks++;
                    }
                }
            }
            super.onGetPacket(event);
        }

        @Override
        public void onMoveButton(Events.MoveButton event) {
            if(mc.thePlayer.hurtTime > 0 && mc.objectMouseOver.entityHit != null) {
                event.forward = true;
            }
            super.onMoveButton(event);
        }

        @Override
        public void onAttack(Events.Attack event) {
            if(mc.objectMouseOver.entityHit != null) {
                if(mc.thePlayer.hurtTime > 0 && blockVelocity) {
                    mc.thePlayer.setSprinting(false);
                    mc.thePlayer.motionX *= 0.6;
                    mc.thePlayer.motionZ *= 0.6;

//                    if((!intaveReverse.isEnabled() || isWorking) && intaveStrong.isEnabled()) {
//                        mc.thePlayer.motionX *= 0.6;
//                        mc.thePlayer.motionZ *= 0.6;
//                    }
//
//                    if(mc.thePlayer.hurtTime <= 6 && intaveReverse.isEnabled()) {
//                        if(isWorking) {
//                            mc.thePlayer.motionX = -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)) * 0.02f;
//                            mc.thePlayer.motionZ = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)) * 0.02f;
//
//                            isWorking = false;
//                        }
//                    }

                    blockVelocity = false;
                }
            }
            super.onAttack(event);
        }
    }
}
