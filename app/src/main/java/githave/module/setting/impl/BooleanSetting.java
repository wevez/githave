package githave.module.setting.impl;

import githave.module.setting.ModuleSetting;

import java.util.function.Consumer;

public class BooleanSetting extends ModuleSetting {

    private static final Consumer<Boolean> PASS = v -> {
    };

    private boolean value;

    private final Consumer<Boolean> onUpdate;

    protected BooleanSetting(Builder builder) {
        super(builder);
        this.value = builder.value;
        this.onUpdate = builder.onUpdate;
    }

    public void setValue(boolean value) {
        this.value = value;
        this.onUpdate.accept(value);
    }

    public boolean getValue() {
        return value;
    }

    public static class Builder extends ModuleSetting.Builder<Builder> {

        private boolean value;

        private Consumer<Boolean> onUpdate = PASS;

        public Builder(String name) {
            super(name);
        }

        public Builder value(boolean v) {
            this.value = v;
            return this;
        }

        public Builder onUpdate(Consumer<Boolean> onUpdate) {
            this.onUpdate = onUpdate;
            return this;
        }

        @Override
        public BooleanSetting build() {
            return new BooleanSetting(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
