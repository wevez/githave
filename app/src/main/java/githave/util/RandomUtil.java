package githave.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    private static final ThreadLocalRandom INSTANCE = ThreadLocalRandom.current();

    private static final String randomString = "abcdefghijklmnopqrstuvwxyz";

    public static int nextInt(int range) {
        return INSTANCE.nextInt(range);
    }

    public static int nextInt(int min, int max) {
        final int dis = Math.abs(max - min);
        return dis == 0 ? min : min + INSTANCE.nextInt(dis);
    }

    public static double nextDouble(double range) {
        return INSTANCE.nextDouble(range);
    }

    public static double nextDouble(double min, double max) {
        final double dis = Math.abs(max - min);
        return dis == 0 ? min : min + INSTANCE.nextDouble(dis);
    }

    public static float nextFloat(float range) {
        return INSTANCE.nextFloat() * range;
    }

    public static float nextFloat(float min, float max) {
        final float dis = Math.abs(max - min);
        return dis == 0 ? min : min + INSTANCE.nextFloat() * (dis);
    }

    public static char nextChar() {
        final char c = randomString.charAt(INSTANCE.nextInt(0, randomString.length()));
        return INSTANCE.nextBoolean() ? c : Character.toUpperCase(c);
    }

    public static String nextString(int length) {
        final char[] value = new char[length];
        for (int i = 0; i < length; i++) {
            value[i] = nextChar();
        }
        return new String(value);
    }

    public static boolean percent(int percent) {
        return nextInt(100) <= percent;
    }
}
