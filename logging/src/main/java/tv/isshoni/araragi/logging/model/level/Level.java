package tv.isshoni.araragi.logging.model.level;

public enum Level implements ILevel {
    DEBUG("DEBUG", 0),
    INFO("INFO", 1000),
    WARNING("WARNING", 2000),
    ERROR("ERROR", 3000);

    private final String name;

    private final int weight;

    Level(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }
}
