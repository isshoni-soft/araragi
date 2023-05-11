package tv.isshoni.araragi.stream;

import tv.isshoni.araragi.data.Pair;
import tv.isshoni.araragi.stream.model.IAraragiStream;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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

public class AraragiStream<T> implements IAraragiStream<T> {

    private final Stream<T> stream;

    protected AraragiStream(Collection<T> collection) {
        this(collection.stream());
    }

    protected AraragiStream(Stream<T> stream) {
        this.stream = stream;
    }

    @Override
    public boolean matches(Stream<T> stream, BiFunction<T, T, Boolean> matcher) {
        return matches(stream.toList(), matcher);
    }

    @Override
    public boolean matches(List<T> other, BiFunction<T, T, Boolean> matcher) {
        List<T> ours = this.stream.toList();
        List<T> copyOthers = new LinkedList<>(other);

        if (ours.size() != copyOthers.size()) {
            return false;
        }

        for (T obj : ours) {
            if (!copyOthers.remove(obj)) {
                return false;
            }
        }

        return copyOthers.isEmpty();
    }

    @Override
    public AraragiStream<T> filterInverted(Predicate<? super T> predicate) {
        return Streams.to(this.stream.filter(v -> !predicate.test(v)));
    }

    @Override
    public AraragiStream<T> add(Collection<? extends T> collection) {
        List<T> list = this.stream.collect(Collectors.toCollection(LinkedList::new));
        list.addAll(collection);

        return Streams.to(list.stream());
    }

    @Override
    public AraragiStream<T> add(Supplier<Collection<? extends T>> collection) {
        return add(collection.get());
    }

    @SafeVarargs
    @Override
    public final <R> AraragiStream<R> expand(Class<R> to, Function<? super T, Collection<? extends R>>... collectionSupplier) {
        return Streams.to(this.stream
                .flatMap(o -> Stream.concat(Arrays.stream(collectionSupplier)
                        .flatMap(s -> s.apply(o).stream()), Stream.of((R) o))));
    }

    @SafeVarargs
    @Override
    public final AraragiStream<T> expand(Function<? super T, Collection<? extends T>>... collectionSupplier) {
        return Streams.to(this.stream
                .flatMap(o -> Stream.concat(Arrays.stream(collectionSupplier)
                        .flatMap(s -> s.apply(o).stream()), Stream.of(o))));
    }

    @Override
    public <R> AraragiStream<R> cast(Class<R> clazz) {
        return Streams.to(this.stream.map(clazz::cast));
    }

    @Override
    public <F, S> PairStream<F, S> mapToPair(Function<? super T, ? extends F> firstMapper, Function<? super T, ? extends S> secondMapper) {
        return new PairStream<>(this.stream.map(v -> new Pair<>(firstMapper.apply(v), secondMapper.apply(v))));
    }

    @Override
    public <F, S> PairStream<F, S> flatMapToPair(Function<? super T, ? extends Stream<? extends Pair<F, S>>> mapper) {
        return new PairStream<>(this.stream.flatMap(mapper));
    }

    @Override
    public <R> R collapse(BiFunction<? super T, R, R> mapper) {
        return collapse(mapper, null);
    }

    @Override
    public <R> R collapse(BiFunction<? super T, R, R> mapper, R first) {
        AtomicReference<R> reference = new AtomicReference<>(first);

        this.stream.forEachOrdered(t -> {
            R result = mapper.apply(t, reference.get());

            reference.set(result);
        });

        return reference.get();
    }

    @SafeVarargs
    @Override
    public final Optional<T> find(Predicate<T>... selectors) {
        List<T> snapshot = this.stream.toList();

        for (Predicate<T> selector : selectors) {
            List<T> clone = new LinkedList<>(snapshot);

            Optional<T> result = Streams.to(clone).filter(selector).findFirst();

            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<T> find(Predicate<T> selector, Function<IAraragiStream<T>, Optional<T>> otherwise) {
        List<T> snapshot = this.stream.collect(Collectors.toList());

        Optional<T> result = Streams.to(snapshot).filter(selector).findFirst();

        if (result.isPresent()) {
            return result;
        }

        return otherwise.apply(Streams.to(snapshot));
    }

    @Override
    public Optional<T> find(Predicate<T> selector, Optional<T> otherwise) {
        List<T> snapshot = this.stream.collect(Collectors.toList());

        Optional<T> result = Streams.to(snapshot).filter(selector).findFirst();

        if (result.isPresent()) {
            return result;
        }

        return otherwise;
    }

    @Override
    public Optional<T> find(Predicate<T> selector, Supplier<Optional<T>> otherwise) {
        List<T> snapshot = this.stream.collect(Collectors.toList());

        Optional<T> result = Streams.to(snapshot).filter(selector).findFirst();

        if (result.isPresent()) {
            return result;
        }

        return otherwise.get();
    }

    @Override
    public Optional<T> findLast() {
        List<T> snapshot = this.stream.toList();

        if (snapshot.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(snapshot.get(snapshot.size() - 1));
    }

    @Override
    public void addTo(Collection<T> collection) {
        this.stream.forEach(collection::add);
    }

    @Override
    public void addToOrdered(Collection<T> collection) {
        this.stream.forEachOrdered(collection::add);
    }

    @Override
    public AraragiStream<T> filter(Predicate<? super T> predicate) {
        return Streams.to(this.stream.filter(predicate));
    }

    @Override
    public <R> AraragiStream<R> map(Function<? super T, ? extends R> mapper) {
        return Streams.to(this.stream.map(mapper));
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super T> mapper) {
        return this.stream.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super T> mapper) {
        return this.stream.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
        return this.stream.mapToDouble(mapper);
    }

    @Override
    public <R> AraragiStream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
        return Streams.to(this.stream.flatMap(mapper));
    }

    @Override
    public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
        return this.stream.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
        return this.stream.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
        return this.stream.flatMapToDouble(mapper);
    }

    @Override
    public AraragiStream<T> distinct() {
        return Streams.to(this.stream.distinct());
    }

    @Override
    public AraragiStream<T> sorted() {
        return Streams.to(this.stream.sorted());
    }

    @Override
    public AraragiStream<T> sorted(Comparator<? super T> comparator) {
        return Streams.to(this.stream.sorted(comparator));
    }

    @Override
    public AraragiStream<T> peek(Consumer<? super T> action) {
        return Streams.to(this.stream.peek(action));
    }

    @Override
    public AraragiStream<T> limit(long maxSize) {
        return Streams.to(this.stream.limit(maxSize));
    }

    @Override
    public AraragiStream<T> skip(long n) {
        return Streams.to(this.stream.skip(n));
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        this.stream.forEach(action);
    }

    @Override
    public void forEachOrdered(Consumer<? super T> action) {
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
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        return this.stream.reduce(identity, accumulator);
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        return this.stream.reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
        return this.stream.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        return this.stream.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        return this.stream.collect(collector);
    }

    @Override
    public Optional<T> min(Comparator<? super T> comparator) {
        return this.stream.min(comparator);
    }

    @Override
    public Optional<T> max(Comparator<? super T> comparator) {
        return this.stream.max(comparator);
    }

    @Override
    public long count() {
        return this.stream.count();
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
        return this.stream.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
        return this.stream.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
        return this.stream.noneMatch(predicate);
    }

    @Override
    public Optional<T> findFirst() {
        List<T> objects = this.stream.toList();

        if (objects.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(objects.get(0));
    }

    @Override
    public Optional<T> findAny() {
        return this.stream.findAny();
    }

    @Override
    public Iterator<T> iterator() {
        return this.stream.iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return this.stream.spliterator();
    }

    @Override
    public boolean isParallel() {
        return this.stream.isParallel();
    }

    @Override
    public AraragiStream<T> sequential() {
        return Streams.to(this.stream.sequential());
    }

    @Override
    public AraragiStream<T> parallel() {
        return Streams.to(this.stream.parallel());
    }

    @Override
    public AraragiStream<T> unordered() {
        return Streams.to(this.stream.unordered());
    }

    @Override
    public AraragiStream<T> onClose(Runnable closeHandler) {
        return Streams.to(this.stream.onClose(closeHandler));
    }

    @Override
    public void close() {
        this.stream.close();
    }
}
