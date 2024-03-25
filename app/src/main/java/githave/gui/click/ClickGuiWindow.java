package githave.gui.click;

import githave.GitHave;
import githave.gui.font.TTFFontRenderer;
import githave.module.Module;
import githave.module.ModuleCategory;
import githave.module.setting.ModuleSetting;
import githave.module.setting.impl.BooleanSetting;
import githave.module.setting.impl.DoubleSetting;
import githave.module.setting.impl.ModeSetting;
import githave.module.setting.impl.MultiBooleanSetting;
import githave.util.animation.AnimationUtil;
import githave.util.animation.LinearAnimation;
import githave.util.render.ClickUtil;
import githave.util.render.ColorUtil;
import githave.util.render.StencilUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static githave.util.render.Render2DUtil.*;

public class ClickGuiWindow {

    private float x, y, lastX, lastY;
    private boolean dragging = false, expand = true;

    private final ModuleCategory category;
    private final List<Module> modules;
    private final boolean[] mExpand;

    private final AnimationUtil expandAnimation = new LinearAnimation();

    private static final TTFFontRenderer font = TTFFontRenderer.of("Roboto-Regular", 20);
    private static final TTFFontRenderer mini = TTFFontRenderer.of("Roboto-Regular", 18);
    private static final TTFFontRenderer setting = TTFFontRenderer.of("Roboto-Regular", 15);

    private ModuleSetting focusedSetting = null;

    private int getColor() {
        return 0xffEC1BF6;
    }

    public ClickGuiWindow(float x, float y, ModuleCategory category) {
        this.x = x;
        this.y = y;
        this.category = category;
        modules = GitHave.INSTANCE.moduleManager
                .getModules()
                .stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
        mExpand = new boolean[modules.size()];
    }

    public void initGui() {
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (dragging) {
            x = mouseX + lastX;
            y = mouseY + lastY;
        }
        rect(this.x - 2, this.y, 125 + 4, 24, 0xff0F0F11);
        font.drawString(category.getName(), x + 25, y + 8, 0xffE0DFE2);
        float offset = y + 24;
        for (int i = 0; i < modules.size(); i++) {
            Module m = modules.get(i);
            rect(x, offset, 125, 18, 0xff1D1D21);
            m.toggleAnimation.uodate(m.isToggled() ? 0.05 : -0.05);
            mini.drawCenteredString(m.getName(), x + 62.5f, offset + 5, ColorUtil.interpolateColor(0xffE0DFE2, getColor(), (float) m.toggleAnimation.calcPercent()));
            offset += 18;
            if (!mExpand[i]) continue;
            for (ModuleSetting s : m.getSettingList()) {
                if (s instanceof BooleanSetting) {
                    rect(x, offset, 125, 18, 0xff151519);
                    setting.drawString(s.getName(), x + 5, offset + 7, 0xffE0DFE2);
                    rect(x + 100, offset + 4, 20, 10, 0xff3C3941);
                    final BooleanSetting bs = (BooleanSetting) s;
                    rect(x + 102 + 10 * s.animation.uodate(bs.getValue() ? -0.1 : 0.1).calcPercent(),
                            offset + 2, 6, 14, ColorUtil.interpolateColor(getColor(), 0xff3C3941, (float) s.animation.calcPercent()));
                    offset += 18;
                } else if (s instanceof DoubleSetting) {
                    rect(x, offset, 125, 18, 0xff151519);
                    setting.drawString(s.getName(), x + 5, offset + 5, 0xffE0DFE2);
                    rect(x + 5, offset + 12,115, 2, 0xff38393D);
                    DoubleSetting ds = (DoubleSetting) s;
                    if (ds == focusedSetting) {
                        ds.setValue(x + 5, 115, mouseX);
                    }
                    rect(x + 5, offset + 12, 115 * ds.getPercentage(), 2, getColor());
                    setting.drawRightString(String.valueOf(ds.getValue()), x + 120, offset + 5, 0xffE0DFE2);
                    offset += 18;
                } else if (s instanceof ModeSetting) {
                    rect(x, offset, 125, 18, 0xff151519);
                    rect(x + 5, offset + 2, 115, 14, 0xff0F0F11);
                    rect(x + 5, offset + 2, 2, 14, 0xff35383E);
                    rect(x + 118, offset + 2, 2, 14, 0xff35383E);
                    setting.drawCenteredString(s.getName(), x + 62.5f, offset + 7, 0xffE0DFE2);
                    offset += 18;
                    ModeSetting ms = (ModeSetting) s;
                    float cpOffset = offset;
                    float p = (float) s.animation.uodate(ms.expand ? 0.1 : -0.1).calcPercent();
                    StencilUtil.initStencilToWrite();
                    rect(x, offset, 125, 14 * ms.getOption().length * p, -1);
                    StencilUtil.readStencilBuffer();
                    for (int mi = 0; mi < ms.getOption().length; mi++) {
                        rect(x, cpOffset, 125, 18, 0xff151519);
                        rect(x + 7, cpOffset - 2, 111, 14, 0xff202226);
                        setting.drawCenteredString(ms.getOption()[mi], x + 62.5f, cpOffset + 3, mi == ms.getIndex() ? getColor() : 0xffE0DFE2);
                        cpOffset += 14;
                    }
                    offset += 14 * ms.getOption().length * p;
                    StencilUtil.uninitStencilBuffer();
                } else if (s instanceof MultiBooleanSetting) {
                    rect(x, offset, 125, 18, 0xff151519);
                    rect(x + 5, offset + 2, 115, 14, 0xff0F0F11);
                    rect(x + 5, offset + 2, 2, 14, 0xff35383E);
                    rect(x + 118, offset + 2, 2, 14, 0xff35383E);
                    setting.drawCenteredString(s.getName(), x + 62.5f, offset + 7, 0xffE0DFE2);
                    offset += 18;
                    MultiBooleanSetting ms = (MultiBooleanSetting) s;
                    float cpOffset = offset;
                    float p = (float) s.animation.uodate(ms.expand ? 0.1 : -0.1).calcPercent();
                    StencilUtil.initStencilToWrite();
                    rect(x, offset, 125, 14 * ms.getValue().size() * p, -1);
                    StencilUtil.readStencilBuffer();
                    for (Map.Entry<String, Boolean> entry : ms.getValue().entrySet()) {
                        rect(x, cpOffset, 125, 18, 0xff151519);
                        rect(x + 7, cpOffset - 2, 111, 14, 0xff202226);
                        setting.drawCenteredString(entry.getKey(), x + 62.5f, cpOffset + 3, entry.getValue() ? getColor() : 0xffE0DFE2);
                        cpOffset += 14;
                    }
                    offset += 14 * ms.getValue().size() * p;
                    StencilUtil.uninitStencilBuffer();
                }
            }
        }
        rect(x, offset, 125, 2, getColor());
    }

    public void onBlur(int mouseX, int mouseY, float partialTicks) {}

    public void onPost(int mouseX, int mouseY, float partialTicks) {}

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (ClickUtil.isHovered(this.x - 2, this.y, 125 + 4, 24, mouseX, mouseY)) {
            if (mouseButton == 0) {
                this.dragging = true;
                lastX = x - mouseX;
                lastY = y - mouseY;
            } else {
                this.expand = !this.expand;
            }
            return;
        }
        float offset = y + 24;
        for (int i = 0; i < modules.size(); i++) {
            Module m = modules.get(i);
            if (ClickUtil.isHovered(x, offset, 125, 18, mouseX, mouseY)) {
                if (mouseButton == 0) m.toggle();
                else mExpand[i] = !mExpand[i];
                return;
            }
            offset += 18;
            if (!mExpand[i]) continue;
            for (ModuleSetting s : m.getSettingList()) {
                if (s instanceof BooleanSetting) {
                    BooleanSetting bs = (BooleanSetting) s;
                    if (ClickUtil.isHovered(x, offset, 125, 18, mouseX, mouseY)) {
                        bs.setValue(!bs.getValue());
                        return;
                    }
                    offset += 18;
                } else if (s instanceof DoubleSetting) {
                    if (ClickUtil.isHovered(x, offset, 125, 18, mouseX, mouseY)) {
                        focusedSetting = s;
                        return;
                    }
                    offset += 18;
                } else if (s instanceof ModeSetting) {
                    ModeSetting ms = (ModeSetting) s;
                    if (ClickUtil.isHovered(x, offset, 125, 18, mouseX, mouseY)) {
                        ms.expand = !ms.expand;
                        return;
                    }
                    offset += 18;
                    if (ms.expand) {
                        for (int mi = 0; mi < ms.getOption().length; mi++) {
                            if (ClickUtil.isHovered(x, offset, 125, 18, mouseX, mouseY)) {
                                ms.setValue(mi);
                                return;
                            }
                            offset += 14;
                        }
                    }
                } else if (s instanceof MultiBooleanSetting) {
                    MultiBooleanSetting ms = (MultiBooleanSetting) s;
                    if (ClickUtil.isHovered(x, offset, 125, 18, mouseX, mouseY)) {
                        ms.expand = !ms.expand;
                        return;
                    }
                    offset += 18;
                    if (ms.expand) {
                        for (Map.Entry<String, Boolean> entry : ms.getValue().entrySet()) {
                            if (ClickUtil.isHovered(x, offset, 125, 18, mouseX, mouseY)) {
                                ms.getValue().put(entry.getKey(), !entry.getValue());
                                return;
                            }
                            offset += 14;
                        }
                    }
                }
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        focusedSetting = null;
    }
}
