package githave.module.setting.impl;

import net.minecraft.util.MathHelper;
import githave.module.setting.ModuleSetting;

import java.util.function.Consumer;

public class DoubleSetting extends ModuleSetting {

    private static final Consumer<Double> PASS = v -> {
    };

    private final double min, max, interval;

    private double value;

    private final Consumer<Double> onUpdate;

    protected DoubleSetting(Builder builder) {
        super(builder);
        this.min = builder.min;
        this.max = builder.max;
        this.interval = builder.interval;
        this.onUpdate = builder.onUpdate;
        this.value = builder.value;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getInterval() {
        return interval;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = checkValue(value);
        this.onUpdate.accept(value);
    }

    public double getPercentage() {
        return (value - min) / (max - min);
    }

    public void setValue(float posX, float width, float mouseX) {
        this.setValue((mouseX - posX) * (this.max - this.min) / width + this.min);
    }

    private double checkValue(double value) {
        double precision = 1 / interval;
        return Math.round(MathHelper.clamp_double(value, this.min, this.max) * precision) / precision;
    }

    public static class Builder extends ModuleSetting.Builder<Builder> {

        private final double value, min, max, interval;

        private Consumer<Double> onUpdate = PASS;

        public Builder(String name, double value, double min, double max, double interval) {
            super(name);
            this.value = value;
            this.min = min;
            this.max = max;
            this.interval = interval;
        }

        public Builder setOnUpdate(Consumer<Double> onUpdate) {
            this.onUpdate = onUpdate;
            return this;
        }

        @Override
        public DoubleSetting build() {
            return new DoubleSetting(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
