package net.minecraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import githave.GitHave;
import githave.event.Events;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;

    private Minecraft mc = Minecraft.getMinecraft();
    private float lastForward;
    private float lastStrafe;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState()
    {
        Events.MoveButton event = new Events.MoveButton(this.gameSettings.keyBindLeft.isKeyDown(), this.gameSettings.keyBindRight.isKeyDown(), this.gameSettings.keyBindBack.isKeyDown(), this.gameSettings.keyBindForward.isKeyDown(), this.gameSettings.keyBindSneak.isKeyDown(), this.gameSettings.keyBindJump.isKeyDown());
        GitHave.INSTANCE.eventManager.call(event);
        if(event.isCanceled())return;
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (event.isCanceled())
        {
            return;
        }

        if (event.forward)
        {
            ++this.moveForward;
        }

        if (event.backward)
        {
            --this.moveForward;
        }

        if (event.left)
        {
            ++this.moveStrafe;
        }

        if (event.right)
        {
            --this.moveStrafe;
        }

        this.jump = event.jump;
        this.sneak = event.sneak;

        if (this.sneak)
        {
            this.moveStrafe = (float)((double) this.moveStrafe * 0.3D);
            this.moveForward = (float)((double) this.moveForward * 0.3D);
        }
    }
}