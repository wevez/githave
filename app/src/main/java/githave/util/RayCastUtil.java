package githave.util;

import com.google.common.base.Predicates;
import githave.MCHook;
import githave.util.data.BlockData;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.optifine.reflect.Reflector;

import java.util.List;

public class RayCastUtil implements MCHook {

    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = (float) Math.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = (float) Math.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = (float) -Math.cos(-pitch * 0.017453292F);
        float f3 = (float) Math.sin(-pitch * 0.017453292F);
        return new Vec3((f1 * f2), f3, (f * f2));
    }

    public static Entity rayTrace(double range, float[] rotations) {
        if (range == 3) {
            return mc.objectMouseOver.entityHit;
        }

        Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0f);

        Vec3 vec31 = getVectorForRotation(rotations[1], rotations[0]);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);

        Entity pointedEntity = null;

        float f = 1.0F;
        List<?> list = mc.theWorld.getEntitiesInAABBexcluding(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
        double d2 = range;

        for (Object o : list) {
            Entity entity1 = (Entity) o;
            float f1 = entity1.getCollisionBorderSize();
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1,  f1);
            MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

            if (axisalignedbb.isVecInside(vec3)) {
                if (d2 >= 0.0D) {
                    pointedEntity = entity1;
                    d2 = 0.0D;
                }
            } else if (movingobjectposition != null) {
                double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                if (d3 < d2 || d2 == 0.0D) {
                    boolean flag2 = false;

                    if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                        flag2 = Reflector.callBoolean(entity1, Reflector.ForgeEntity_canRiderInteract);
                    }

                    if (entity1 == mc.getRenderViewEntity().ridingEntity && !flag2) {
                        if (d2 == 0.0D) {
                            pointedEntity = entity1;
                        }
                    } else {
                        pointedEntity = entity1;
                        d2 = d3;
                    }
                }
            }
        }

        return pointedEntity;
    }
}
