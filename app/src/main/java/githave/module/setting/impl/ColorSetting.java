package githave.module.setting.impl;

import githave.module.setting.ModuleSetting;

public class ColorSetting extends ModuleSetting {

    private int color;

    protected ColorSetting(Builder builder) {
        super(builder);
        this.color = builder.color;
    }

    public void setValue(int color) {
        this.color = color;
    }

    public int getValue() {
        return color;
    }

    public static class Builder extends ModuleSetting.Builder<Builder> {

        private int color;

        public Builder(String name) {
            super(name);
        }

        @Override
        public ColorSetting build() {
            return new ColorSetting(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }
    }
}
