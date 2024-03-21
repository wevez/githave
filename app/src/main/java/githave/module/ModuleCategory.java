package githave.module;

public enum ModuleCategory {

    Combat("Combat"),
    Movement("Movement"),
    Player("Player"),
    Render("Render"),
    Misc("Misc");

    private final String name;

    ModuleCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
