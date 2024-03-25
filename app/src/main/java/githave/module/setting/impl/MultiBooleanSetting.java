package githave.module.setting.impl;

import githave.module.setting.ModuleSetting;

import java.util.HashMap;
import java.util.Map;

public class MultiBooleanSetting extends ModuleSetting {

    private final Map<String, Boolean> value;
    public boolean expand;

    protected MultiBooleanSetting(Builder builder) {
        super(builder);
        this.value = builder.value;
    }

    public Map<String, Boolean> getValue() {
        return value;
    }

    public static class Builder extends ModuleSetting.Builder<Builder> {

        private final Map<String, Boolean> value;

        public Builder(String name, String... values) {
            super(name);
            this.value = new HashMap<>();
            for (String s : values) {
                this.value.put(s, false);
            }
        }

        public Builder value(String name, boolean value) {
            this.value.put(name, value);
            return this;
        }

        @Override
        public MultiBooleanSetting build() {
            return new MultiBooleanSetting(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
