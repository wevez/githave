package githave.module;

import com.google.common.collect.Lists;
import githave.GitHave;
import githave.MCHook;
import githave.event.EventListener;
import githave.module.setting.ModuleSetting;
import githave.util.animation.AnimationUtil;
import githave.util.animation.LinearAnimation;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class Module implements EventListener, MCHook {

    public AnimationUtil toggleAnimation = new LinearAnimation();
    public AnimationUtil settingAnimation = new LinearAnimation();

    private final String name, description;

    private final ModuleCategory category;

    protected final List<ModuleSetting> settingList;

    private boolean toggled;

    private int keyCode;

    public Module(String name, String description, ModuleCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.settingList = Lists.newArrayList();
        this.keyCode = Keyboard.KEY_NONE;
    }

    public void init() {}

    public final int getKeyCode() {
        return keyCode;
    }

    public final void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public final void toggle() {
        this.toggled = !this.toggled;
        if (this.toggled) {
            this.onEnable();
            GitHave.INSTANCE.eventManager.register(this);
        } else {
            this.onDisable();
            GitHave.INSTANCE.eventManager.unregister(this);
        }
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final ModuleCategory getCategory() {
        return category;
    }

    public final List<ModuleSetting> getSettingList() {
        return settingList;
    }

    public final boolean isToggled() {
        return this.toggled;
    }
}
