package githave.util.animation;

public class EaseOutAnimation extends AnimationUtil{
    @Override
    public double calcPercent() {
        return -tick * tick + 2 * tick - 2;
    }
}
