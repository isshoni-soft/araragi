package institute.isshoni.araragi.logging.model.level;

public record SimpleLevel(String name, int weight) implements ILevel {

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }
}
