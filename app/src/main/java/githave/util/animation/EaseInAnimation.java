package githave.util.animation;

public class EaseInAnimation extends AnimationUtil {
    @Override
    public double calcPercent() {
        return tick * tick;
    }
}
