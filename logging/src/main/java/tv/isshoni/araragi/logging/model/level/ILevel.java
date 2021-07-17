package tv.isshoni.araragi.logging.model.level;

// The extends is commented out because java won't let us
// override the Comparable generic extension(?) in enum classes, so tv.isshoni.araragi.logging.model.level.Level
// wouldn't be possible if we extend comparable.
public interface ILevel /*extends Comparable<ILevel>*/ {

    String getName();

    int getWeight();

    default int compareTo(ILevel other) {
        return Integer.compare(this.getWeight(), other.getWeight());
    }
}
