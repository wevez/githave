package githave.util;

public class TimerUtil {

    public long time = System.currentTimeMillis();

    public boolean hasTimeElapsed(long delay)
    {
        if (System.currentTimeMillis() - time > delay)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public long getTime()
    {
        return System.currentTimeMillis() - time;
    }
    public void reset()
    {
        this.time = System.currentTimeMillis();
    }
    public void setTime(long time) {
        this.time = time;
    }
}
