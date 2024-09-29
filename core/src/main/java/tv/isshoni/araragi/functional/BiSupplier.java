package tv.isshoni.araragi.functional;

import tv.isshoni.araragi.data.Pair;

public interface BiSupplier<F, S> {

    Pair<F, S> supply();
}
