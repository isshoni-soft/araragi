package institute.isshoni.araragi.functional;

import institute.isshoni.araragi.data.Pair;

public interface BiSupplier<F, S> {

    Pair<F, S> supply();
}
