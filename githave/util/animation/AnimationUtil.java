package githave.util.animation;


public abstract class AnimationUtil {


    protected double tick;

    public AnimationUtil uodate(double speed) {
        tick += speed;
        // MathHelper.clamp makes bugs
        tick = Math.min(tick, 1);
        tick = Math.max(tick, 0);
        return this;
    }

    public abstract double calcPercent();
}
