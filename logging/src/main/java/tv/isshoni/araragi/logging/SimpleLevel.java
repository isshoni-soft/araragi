package tv.isshoni.araragi.logging;

import tv.isshoni.araragi.logging.model.ILevel;

public class SimpleLevel implements ILevel {

    private final String name;

    private final int weight;

    public SimpleLevel(String name, int weight) {
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
