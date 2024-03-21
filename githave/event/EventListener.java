package githave.event;

public interface EventListener {

    default void onAirCollide(Events.AirCollide event) {}

    default void onGameLoop(Events.GameLoop event) {}

    default void onTick(Events.Tick event) {}

    default void onTimeDelay(Events.TimeDelay event) {}

    default void onLook(Events.Look event) {}

    default void onMove(Events.Move event) {}

    default void onUpdate(Events.Update event) {}

    default void onMotion(Events.Motion event) {}

    default void onChat(Events.Chat event) {}

    default void onNoSlow(Events.NoSlow event) {}

    default void onAttack(Events.Attack event) {}

    default void onWorldChange(Events.WorldChange event) {}

    default void onReach(Events.Reach event) {}

    default void onRotation(Events.Rotation event) {}

    default void onRenderGui(Events.PreRenderGui event) {}

    default void onRender3D(Events.Render3D event) {}

    default void onNametagRenderer(Events.NametagRenderer event) {}

    default void onModelPlayer(Events.ModelPlayer event) {}

    default void onEntityRenderer(Events.EntityRenderer event) {}

    default void onRenderRotation(Events.RenderRotation event) {}

    default void onRenderTileEntity(Events.RenderTileEntity event) {}

    default void onSafeWalk(Events.SafeWalk event) {}

    default void onGround(Events.Ground event) {}

    default void onMoveFlying(Events.MoveFlying event) {}

    default void onJump(Events.Jump event) {}

    default void onKnockback(Events.Knockback event) {}

    default void onGetPacket(Events.GetPacket event) {}

    default void onSendPacket(Events.SendPacket event) {}

    default void onMoveButton(Events.MoveButton event) {}

    default void onPlayerPush(Events.PlayerPush event) {}

    default void onBlur(Events.BlurEvent event) {}

    default void onPostRenderGui(Events.PostRenderGui event) {}
}
