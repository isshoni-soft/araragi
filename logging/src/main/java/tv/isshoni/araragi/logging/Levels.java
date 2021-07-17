package tv.isshoni.araragi.logging;

import tv.isshoni.araragi.logging.model.ILevel;

public interface Levels {

    ILevel INFO = new SimpleLevel("INFO", 1000);
    ILevel WARNING = new SimpleLevel("WARNING", 900);
}
