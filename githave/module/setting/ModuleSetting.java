package githave.module.setting;

import githave.util.animation.AnimationUtil;
import githave.util.animation.LinearAnimation;

public abstract class ModuleSetting {

    public AnimationUtil animation = new LinearAnimation();

    private final String name;

    private final Visibility visibility;

    protected ModuleSetting(Builder<?> builder) {
        this.name = builder.name;
        this.visibility = builder.visibility;
    }

    public final String getName() {
        return name;
    }

    public final boolean isVisible() {
        return this.visibility.isVisible();
    }

    public static abstract class Builder<T extends Builder<T>> {

        private final String name;

        private Visibility visibility = Visibility.VISIBLE;

        public Builder(String name) {
            this.name = name;
        }

        public T visibility(Visibility visibility) {
            this.visibility = visibility;
            return self();
        }

        public abstract ModuleSetting build();

        protected abstract T self();
    }
}
