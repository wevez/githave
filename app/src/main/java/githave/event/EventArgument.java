package githave.event;

public abstract class EventArgument {

    private boolean cancel;

    protected EventArgument() {
        this.cancel = false;
    }

    public final void cancel() {
        this.cancel = true;
    }

    public final boolean isCanceled() {
        return this.cancel;
    }

    public abstract void call(final EventListener listener);
}
