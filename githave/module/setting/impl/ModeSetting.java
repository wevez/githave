package githave.module.setting.impl;

import githave.module.setting.ModuleSetting;

import java.util.function.Consumer;

public class ModeSetting extends ModuleSetting {

    private static final Consumer<String> PASS = v -> {
    };

    private final String[] option;

    private String value;

    private int index;

    private final Consumer<String> onUpdate;

    public boolean expand;

    protected ModeSetting(Builder builder) {
        super(builder);
        this.option = builder.option;
        this.value = builder.option[0];
        this.index = 0;
        this.onUpdate = builder.onUpdate;
    }

    public String getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    public String[] getOption() {
        return option;
    }

    public void setValue(String value) {
        this.value = value;
        this.index = indexOf(this.value);
        onUpdate.accept(value);
    }

    public void setValue(int index) {
        this.index = index;
        this.value = this.option[index];
        onUpdate.accept(this.value);
    }

    private int indexOf(final String a) {
        for (int i = 0; i < option.length; i++) {
            if (option[i].equals(a)) return i;
        }
        return -1;
    }

    public static class Builder extends ModuleSetting.Builder<Builder> {

        private final String[] option;

        private Consumer<String> onUpdate = PASS;

        public Builder(String name, String... option) {
            super(name);
            this.option = option;
        }

        public Builder onUpdate(Consumer<String> onUpdate) {
            this.onUpdate = onUpdate;
            return this;
        }

        @Override
        public ModeSetting build() {
            return new ModeSetting(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
