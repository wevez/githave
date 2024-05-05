package githave.module.impl.render;

import githave.event.Events;
import githave.module.ModeModule;
import githave.module.ModeObject;
import githave.module.ModuleCategory;
import githave.module.setting.impl.MultiBooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Arrays;

public class ESP extends ModeModule {

    private final MultiBooleanSetting entities = new MultiBooleanSetting.Builder("Entities")
            .value("Players", true)
            .value("Mobs", true)
            .value("Animals", true)
            .value("Invisible", false)
            .value("Items", false)
            .value("Other", false)
            .build();

    public ESP() {
        super("ESP", "Draws a box around entities", ModuleCategory.Render, "Fill", "Outline", "Both");
        this.getSettingList().addAll(Arrays.asList(
                entities
        ));
    }

    private boolean shouldRender(Entity entity) {
        if (entity == mc.thePlayer || entity.isDead) return false;
        if (entity.isInvisible() && !entities.getValue().get("Invisible")) return false;
        if (entity instanceof net.minecraft.entity.item.EntityItem && !entities.getValue().get("Items")) return false;
        if (entity instanceof EntityPlayer && !entities.getValue().get("Players")) return false;
        if (entity instanceof net.minecraft.entity.monster.EntityMob && !entities.getValue().get("Mobs")) return false;
        if (entity instanceof net.minecraft.entity.passive.EntityAnimal && !entities.getValue().get("Animals")) return false;
        return entities.getValue().get("Other");
    }

    @Override
    protected ModeObject getObject(String value) {
        switch (value) {
            case "Fill": return new Fill();
            case "Outline": return new Outline();
            case "Both": return new Both();
        }
        return null;
    }

    private static class Fill extends ModeObject {

        @Override
        public void onRender3D(Events.Render3D event) {
            super.onRender3D(event);
        }
    }

    private static class Outline extends ModeObject {

        @Override
        public void onRender3D(Events.Render3D event) {
            super.onRender3D(event);
        }
    }

    private static class Both extends ModeObject {

        @Override
        public void onRender3D(Events.Render3D event) {
            super.onRender3D(event);
        }
    }
}
