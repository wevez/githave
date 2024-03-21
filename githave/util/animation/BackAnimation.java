package githave.util.animation;

public class BackAnimation extends AnimationUtil {

    @Override
    public double calcPercent() {
        return -6 * tick * tick + 7 * tick;
    }
}
