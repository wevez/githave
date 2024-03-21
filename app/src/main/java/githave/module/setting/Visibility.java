package githave.module.setting;

public interface Visibility {

    Visibility VISIBLE = () -> true;

    Visibility INVISIBLE = () -> false;

    boolean isVisible();
}
