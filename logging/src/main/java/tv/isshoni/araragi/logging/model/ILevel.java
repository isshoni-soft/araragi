package tv.isshoni.araragi.logging.model;

public interface ILevel extends Comparable<ILevel> {

    String getName();

    int getWeight();

    default int compareTo(ILevel other) {
        return Integer.compare(this.getWeight(), other.getWeight());
    }
}
