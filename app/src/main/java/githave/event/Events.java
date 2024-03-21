package githave.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class Events {

    public static class AirCollide extends EventArgument {
        public AxisAlignedBB returnValue;
        public World worldIn;
        public BlockPos pos;
        public IBlockState state;
        public double minX, minY, minZ, maxX, maxY, maxZ;
        public AirCollide(World worldIn, BlockPos pos, IBlockState state, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            this.worldIn = worldIn;
            this.pos = pos;
            this.state = state;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        @Override
        public void call(EventListener listener) {
            listener.onAirCollide(this);
        }
    }

    public static class GameLoop extends EventArgument {

        @Override
        public void call(EventListener listener) {
            listener.onGameLoop(this);
        }
    }

    public static class Tick extends EventArgument {

        @Override
        public void call(EventListener listener) {
            listener.onTick(this);
        }
    }

    public static class TimeDelay extends EventArgument {

        @Override
        public void call(EventListener listener) {
            listener.onTimeDelay(this);
        }
    }

    public static class Look extends EventArgument {

        public float yaw, pitch;

        public Look(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        @Override
        public void call(EventListener listener) {
            listener.onLook(this);
        }
    }

    public static class Move extends EventArgument {

        public final boolean pre;
        public double x, y, z;

        public Move(boolean pre, double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.pre = pre;
        }

        @Override
        public void call(EventListener listener) {
            listener.onMove(this);
        }
    }

    public static class Update extends EventArgument {

        public final boolean pre;

        public Update(boolean pre) {
            this.pre = pre;
        }

        @Override
        public void call(EventListener listener) {
            listener.onUpdate(this);
        }
    }

    public static class Motion extends EventArgument {

        public final boolean pre;

        public double x, y ,z;
        public float yaw, pitch;
        public boolean onGround, sneaking, sprinting;

        public Motion(boolean pre, double x, double y, double z, float yaw, float pitch, boolean onGround, boolean sneaking, boolean sprinting) {
            this.x = x;
            this.pre = pre;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.onGround = onGround;
            this.sneaking = sneaking;
            this.sprinting = sprinting;
        }

        @Override
        public void call(EventListener listener) {
            listener.onMotion(this);
        }
    }

    public static class Chat extends EventArgument {

        public String message;

        public Chat(String message) {
            this.message = message;
        }

        @Override
        public void call(EventListener listener) {
            listener.onChat(this);
        }
    }

    public static class NoSlow extends EventArgument {

        @Override
        public void call(EventListener listener) {
            listener.onNoSlow(this);
        }
    }

    public static class Attack extends EventArgument {

        public Entity entity;

        public Attack(Entity entity) {
            this.entity = entity;
        }

        @Override
        public void call(EventListener listener) {
            listener.onAttack(this);
        }
    }

    public static class WorldChange extends EventArgument {
        @Override
        public void call(EventListener listener) {
            listener.onWorldChange(this);
        }
    }

    public static class Reach extends EventArgument {

        public double reach;

        public Reach(double reach) {
            this.reach = reach;
        }

        @Override
        public void call(EventListener listener) {
            listener.onReach(this);
        }
    }

    public static class Rotation extends EventArgument {

        @Override
        public void call(EventListener listener) {
            listener.onRotation(this);
        }
    }

    public static class PreRenderGui extends EventArgument {

        @Override
        public void call(EventListener listener) {
            listener.onRenderGui(this);
        }
    }

    public static class BlurEvent extends EventArgument {

        @Override
        public void call(EventListener listener) {
            listener.onBlur(this);
        }
    }

    public static class PostRenderGui extends EventArgument {

        @Override
        public void call(EventListener listener) {
            listener.onPostRenderGui(this);
        }
    }

    public static class Render3D extends EventArgument {

        @Override
        public void call(EventListener listener) {
            listener.onRender3D(this);
        }
    }

    public static class NametagRenderer extends EventArgument {

        public final Entity e;

        public NametagRenderer(Entity e) {
            this.e = e;
        }

        @Override
        public void call(EventListener listener) {
            listener.onNametagRenderer(this);
        }
    }

    public static class ModelPlayer extends EventArgument {

        public AbstractClientPlayer player;
        public ModelPlayer model;

        public ModelPlayer(AbstractClientPlayer player, ModelPlayer model) {
            this.player = player;
            this.model = model;
        }

        @Override
        public void call(EventListener listener) {
            listener.onModelPlayer(this);
        }
    }

    public static class EntityRenderer extends EventArgument {

        public final boolean pre;

        public final Entity e;

        public EntityRenderer(boolean pre, Entity e) {
            this.e = e;
            this.pre = pre;
        }

        @Override
        public void call(EventListener listener) {
            listener.onEntityRenderer(this);
        }
    }

    public static class RenderRotation extends EventArgument {

        public float yaw, pitch;

        public RenderRotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        @Override
        public void call(EventListener listener) {
            listener.onRenderRotation(this);
        }
    }

    public static class RenderTileEntity extends EventArgument {

        public final TileEntity entity;

        public RenderTileEntity(TileEntity entity) {
            this.entity = entity;
        }

        @Override
        public void call(EventListener listener) {
            listener.onRenderTileEntity(this);
        }
    }

    public static class SafeWalk extends EventArgument {
        public boolean safe;

        @Override
        public void call(EventListener listener) {
            listener.onSafeWalk(this);
        }
    }

    public static class Ground extends EventArgument {

        public boolean onGround;

        public Ground(boolean onGround) {
            this.onGround = onGround;
        }

        @Override
        public void call(EventListener listener) {
            listener.onGround(this);
        }
    }

    public static class MoveFlying extends EventArgument {

        public float yaw;

        public MoveFlying(float yaw, float strafe, float forward, float friction) {
            this.yaw = yaw;
            this.strafe = strafe;
            this.forward = forward;
            this.friction = friction;
        }

        public float strafe, forward, friction;

        @Override
        public void call(EventListener listener) {
            listener.onMoveFlying(this);
        }
    }

    public static class Jump extends EventArgument {

        public float yaw;
        public double motionY;

        public Jump(float yaw, double motionY) {
            this.yaw = yaw;
            this.motionY = motionY;
        }

        @Override
        public void call(EventListener listener) {
            listener.onJump(this);
        }
    }

    public static class Knockback extends EventArgument {
        public double motion = 0.6;
        public boolean full, strong, reduceY;
        public int power, postPower;

        public Knockback(double motion, boolean full, int power, int postPower, boolean strong, boolean reduceY) {
            this.motion = motion;
            this.full = full;
            this.power = power;
            this.postPower = postPower;
            this.strong = strong;
            this.reduceY = reduceY;
        }

        @Override
        public void call(EventListener listener) {
            listener.onKnockback(this);
        }
    }

    public static class GetPacket extends EventArgument {

        public final boolean pre;
        public Packet packet;

        public GetPacket(boolean pre, Packet packet) {
            this.packet = packet;
            this.pre = pre;
        }

        @Override
        public void call(EventListener listener) {
            listener.onGetPacket(this);
        }
    }

    public static class SendPacket extends EventArgument {

        public final boolean pre;
        public Packet packet;

        public SendPacket(boolean pre, Packet packet) {
            this.packet = packet;
            this.pre = pre;
        }

        @Override
        public void call(EventListener listener) {
            listener.onSendPacket(this);
        }
    }

    public static class MoveButton extends EventArgument {

        public boolean left;
        public boolean right;
        public boolean backward;
        public boolean forward;
        public boolean sneak;
        public boolean jump;

        public MoveButton(boolean button, boolean button2, boolean button3, boolean button4, boolean sneak, boolean jump) {
            this.left = button;
            this.right = button2;
            this.backward = button3;
            this.forward = button4;
            this.sneak = sneak;
            this.jump = jump;
        }

        @Override
        public void call(EventListener listener) {
            listener.onMoveButton(this);
        }
    }

    public static class PlayerPush extends EventArgument {

        @Override
        public void call(EventListener listener) {
            listener.onPlayerPush(this);
        }
    }
}
