package tv.isshoni.araragi.stream;

import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.stream.model.IAraragiStream;
import tv.isshoni.araragi.stream.model.IPairStream;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class PairStream<F, S> implements IPairStream<F, S> {

    private final AraragiStream<Pair<F, S>> stream;

    protected PairStream(AraragiStream<Pair<F, S>> stream) {
        this.stream = stream;
    }

    protected PairStream(Pair<F, S>[] pairs) {
        this(Arrays.stream(pairs));
    }

    protected PairStream(Stream<Pair<F, S>> stream) {
        this(Streams.to(stream));
    }

    protected PairStream(Map<F, S> map) {
        this.stream = Streams.to(map.entrySet())
                .map(Pair::new);
    }

    protected PairStream(Collection<Pair<F, S>> collection) {
        this.stream = Streams.to(collection);
    }

    @Override
    public IPairStream<F, S> filterInverted(BiPredicate<F, S> predicate) {
        return new PairStream<>(this.filterInverted(p -> predicate.test(p.getFirst(), p.getSecond())));
    }

    @Override
    public IPairStream<F, S> add(Map<F, S> map) {
        return new PairStream<>(this.stream.add(Streams.to(map)
                .collect(Collectors.toList())));
    }

    @Override
    public <NF> PairStream<NF, S> mapFirst(Function<? super F, ? extends NF> mapper) {
        return new PairStream<>(this.stream.map(p -> new Pair<>(mapper.apply(p.getFirst()), p.getSecond())));
    }

    @Override
    public <NS> PairStream<F, NS> mapSecond(Function<? super S, ? extends NS> mapper) {
        return new PairStream<>(this.stream.map(p -> new Pair<>(p.getFirst(), mapper.apply(p.getSecond()))));
    }

    @Override
    public <NF, NS> PairStream<NF, NS> map(Function<? super F, ? extends NF> firstMapper, Function<? super S, ? extends NS> secondMapper) {
        return new PairStream<>(this.stream.map(p -> new Pair<>(firstMapper.apply(p.getFirst()), secondMapper.apply(p.getSecond()))));
    }

    @Override
    public <R> AraragiStream<R> map(BiFunction<? super F, ? super S, ? extends R> mapper) {
        return this.stream.map(p -> mapper.apply(p.getFirst(), p.getSecond()));
    }

    @Override
    public IPairStream<F, S> filter(BiPredicate<F, S> predicate) {
        return new PairStream<>(this.stream.filter(p -> predicate.test(p.getFirst(), p.getSecond())));
    }

    @Override
    public AraragiStream<F> mapFirst() {
        return this.stream.map(Pair::getFirst);
    }

    @Override
    public AraragiStream<S> mapSecond() {
        return this.stream.map(Pair::getSecond);
    }

    @Override
    public Map<F, S> toMap() {
        return this.stream.collect(Streams.collectPairsToMap());
    }

    @Override
    public Map<F, S> toUnmodifiableMap() {
        return this.stream.collect(Streams.collectPairsToUnmodifiableMap());
    }

    @Override
    public void forEach(BiConsumer<? super F, ? super S> consumer) {
        this.stream.forEach(p -> consumer.accept(p.getFirst(), p.getSecond()));
    }

    @Override
    public void forEachOrdered(BiConsumer<? super F, ? super S> consumer) {
        this.stream.forEachOrdered(p -> consumer.accept(p.getFirst(), p.getSecond()));
    }

    @Override
    public void addTo(Map<F, S> map) {
        this.forEach(map::put);
    }

    @Override
    public void addToOrdered(Map<F, S> map) {
        this.forEachOrdered(map::put);
    }

    @Override
    public boolean matches(Stream<Pair<F, S>> stream, BiFunction<Pair<F, S>, Pair<F, S>, Boolean> matcher) {
        return this.stream.matches(stream, matcher);
    }

    @Override
    public boolean matches(List<Pair<F, S>> other, BiFunction<Pair<F, S>, Pair<F, S>, Boolean> matcher) {
        return this.stream.matches(other, matcher);
    }

    @Override
    public AraragiStream<Pair<F, S>> filterInverted(Predicate<? super Pair<F, S>> predicate) {
        return this.stream.filterInverted(predicate);
    }

    @Override
    public AraragiStream<Pair<F, S>> add(Collection<? extends Pair<F, S>> collection) {
        return this.stream.add(collection);
    }

    @Override
    public AraragiStream<Pair<F, S>> add(Supplier<Collection<? extends Pair<F, S>>> collection) {
        return this.stream.add(collection);
    }

    @SafeVarargs
    @Override
    public final <R> AraragiStream<R> expand(Class<R> to, Function<? super Pair<F, S>, Collection<? extends R>>... collectionSupplier) {
        return this.stream.expand(to, collectionSupplier);
    }

    @SafeVarargs
    @Override
    public final AraragiStream<Pair<F, S>> expand(Function<? super Pair<F, S>, Collection<? extends Pair<F, S>>>... collectionSupplier) {
        return this.stream.expand(collectionSupplier);
    }

    @Override
    public <R> AraragiStream<R> cast(Class<R> clazz) {
        return this.stream.cast(clazz);
    }

    @Override
    public <NF, NS> PairStream<NF, NS> mapToPair(Function<? super Pair<F, S>, ? extends NF> firstMapper, Function<? super Pair<F, S>, ? extends NS> secondMapper) {
        return this.stream.mapToPair(firstMapper, secondMapper);
    }

    @Override
    public <NF, NS> PairStream<NF, NS> flatMapToPair(Function<? super Pair<F, S>, ? extends Stream<? extends Pair<NF, NS>>> mapper) {
        return this.stream.flatMapToPair(mapper);
    }

    @Override
    public <R> R collapse(BiFunction<? super Pair<F, S>, R, R> mapper) {
        return this.stream.collapse(mapper);
    }

    @Override
    public <R> R collapse(BiFunction<? super Pair<F, S>, R, R> mapper, R first) {
        return this.stream.collapse(mapper, first);
    }

    @SafeVarargs
    @Override
    public final Optional<Pair<F, S>> find(Predicate<Pair<F, S>>... selectors) {
        return this.stream.find(selectors);
    }

    @Override
    public Optional<Pair<F, S>> find(Predicate<Pair<F, S>> selector, Function<IAraragiStream<Pair<F, S>>, Optional<Pair<F, S>>> otherwise) {
        return this.stream.find(selector, otherwise);
    }

    @Override
    public Optional<Pair<F, S>> find(Predicate<Pair<F, S>> selector, Optional<Pair<F, S>> otherwise) {
        return this.stream.find(selector, otherwise);
    }

    @Override
    public Optional<Pair<F, S>> find(Predicate<Pair<F, S>> selector, Supplier<Optional<Pair<F, S>>> otherwise) {
        return this.stream.find(selector, otherwise);
    }

    @Override
    public void addTo(Collection<Pair<F, S>> collection) {
        this.forEach(collection::add);
    }

    @Override
    public void addToOrdered(Collection<Pair<F, S>> collection) {
        this.forEachOrdered(collection::add);
    }

    @Override
    public AraragiStream<Pair<F, S>> filter(Predicate<? super Pair<F, S>> predicate) {
        return this.stream.filter(predicate);
    }

    @Override
    public <R> AraragiStream<R> map(Function<? super Pair<F, S>, ? extends R> mapper) {
        return this.stream.map(mapper);
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super Pair<F, S>> mapper) {
        return this.stream.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super Pair<F, S>> mapper) {
        return this.stream.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super Pair<F, S>> mapper) {
        return this.stream.mapToDouble(mapper);
    }

    @Override
    public <R> AraragiStream<R> flatMap(Function<? super Pair<F, S>, ? extends Stream<? extends R>> mapper) {
        return this.stream.flatMap(mapper);
    }

    @Override
    public IntStream flatMapToInt(Function<? super Pair<F, S>, ? extends IntStream> mapper) {
        return this.stream.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(Function<? super Pair<F, S>, ? extends LongStream> mapper) {
        return this.stream.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super Pair<F, S>, ? extends DoubleStream> mapper) {
        return this.stream.flatMapToDouble(mapper);
    }

    @Override
    public PairStream<F, S> distinct() {
        return new PairStream<>(this.stream.distinct());
    }

    @Override
    public PairStream<F, S> sorted() {
        return new PairStream<>(this.stream.sorted());
    }

    @Override
    public PairStream<F, S> sorted(Comparator<? super Pair<F, S>> comparator) {
        return new PairStream<>(this.stream.sorted(comparator));
    }

    @Override
    public PairStream<F, S> peek(Consumer<? super Pair<F, S>> action) {
        return new PairStream<>(this.stream.peek(action));
    }

    @Override
    public PairStream<F, S> limit(long maxSize) {
        return new PairStream<>(this.stream.limit(maxSize));
    }

    @Override
    public PairStream<F, S> skip(long n) {
        return new PairStream<>(this.stream.skip(n));
    }

    @Override
    public void forEach(Consumer<? super Pair<F, S>> action) {
        this.stream.forEach(action);
    }

    @Override
    public void forEachOrdered(Consumer<? super Pair<F, S>> action) {
        this.stream.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return this.stream.toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return this.stream.toArray(generator);
    }

    @Override
    public Pair<F, S> reduce(Pair<F, S> identity, BinaryOperator<Pair<F, S>> accumulator) {
        return this.stream.reduce(identity, accumulator);
    }

    @Override
    public Optional<Pair<F, S>> reduce(BinaryOperator<Pair<F, S>> accumulator) {
        return this.stream.reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super Pair<F, S>, U> accumulator, BinaryOperator<U> combiner) {
        return this.stream.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Pair<F, S>> accumulator, BiConsumer<R, R> combiner) {
        return this.stream.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super Pair<F, S>, A, R> collector) {
        return this.stream.collect(collector);
    }

    @Override
    public Optional<Pair<F, S>> min(Comparator<? super Pair<F, S>> comparator) {
        return this.stream.min(comparator);
    }

    @Override
    public Optional<Pair<F, S>> max(Comparator<? super Pair<F, S>> comparator) {
        return this.stream.max(comparator);
    }

    @Override
    public long count() {
        return this.stream.count();
    }

    @Override
    public boolean anyMatch(Predicate<? super Pair<F, S>> predicate) {
        return this.stream.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super Pair<F, S>> predicate) {
        return this.stream.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super Pair<F, S>> predicate) {
        return this.stream.noneMatch(predicate);
    }

    @Override
    public Optional<Pair<F, S>> findFirst() {
        return this.stream.findFirst();
    }

    @Override
    public Optional<Pair<F, S>> findAny() {
        return this.stream.findAny();
    }

    @Override
    public Iterator<Pair<F, S>> iterator() {
        return this.stream.iterator();
    }

    @Override
    public Spliterator<Pair<F, S>> spliterator() {
        return this.stream.spliterator();
    }

    @Override
    public boolean isParallel() {
        return this.stream.isParallel();
    }

    @Override
    public PairStream<F, S> sequential() {
        return new PairStream<>(this.stream.sequential());
    }

    @Override
    public PairStream<F, S> parallel() {
        return new PairStream<>(this.stream.parallel());
    }

    @Override
    public PairStream<F, S> unordered() {
        return new PairStream<>(this.stream.unordered());
    }

    @Override
    public PairStream<F, S> onClose(Runnable closeHandler) {
        return new PairStream<>(this.stream.onClose(closeHandler));
    }

    @Override
    public void close() {
        this.stream.close();
    }
}
