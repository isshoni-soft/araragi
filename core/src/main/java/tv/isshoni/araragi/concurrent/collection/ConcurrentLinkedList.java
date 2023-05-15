package tv.isshoni.araragi.concurrent.collection;

import tv.isshoni.araragi.functional.ObjHelpers;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConcurrentLinkedList<T> implements List<T> {

    private final Node<T> root;
    private Node<T> last;

    private int size;

    public ConcurrentLinkedList() {
        this.root = new Node<>();
        this.last = this.root;
        this.size = 0;
    }

    public synchronized T getFirst() {
        checkSize();
        return this.root.data;
    }

    public synchronized T getLast() {
        checkSize();
        return this.last.data;
    }

    @Override
    public synchronized int size() {
        return this.size;
    }

    @Override
    public synchronized boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public synchronized boolean contains(Object o) {
        return findNodeContaining(o).isPresent();
    }

    @Override
    public synchronized Iterator<T> iterator() {
        return listIterator();
    }

    @Override
    public synchronized Object[] toArray() {
        return toArray(new Object[0]);
    }

    @Override
    public synchronized <E> E[] toArray(E[] a) {
        if (a.length < this.size) {
            a = (E[]) Array.newInstance(a.getClass().getComponentType(), this.size);
        }

        E[] finalA = a;
        forEachNode(ref -> finalA[ref.index] = (E) ref.node.data);

        return a;
    }

    @Override
    public synchronized boolean add(T t) {
        if (this.size == 0) {
            this.root.data = t;
        } else {
            this.last.next = new Node<>(t, this.last);
            this.last = this.last.next;
        }
        this.size++;

        return true;
    }

    @Override
    public synchronized boolean remove(Object o) {
        return remove(indexOf(o)) != null;
    }

    @Override
    public synchronized boolean containsAll(Collection<?> c) {
        for (Object obj : c) {
            if (!contains(obj)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public synchronized boolean addAll(Collection<? extends T> c) {
        c.forEach(this::add);

        return true;
    }

    @Override
    public synchronized boolean addAll(int index, Collection<? extends T> c) {
        AtomicInteger idx = new AtomicInteger();

        c.forEach(o -> add(idx.getAndIncrement(), o));

        return true;
    }

    @Override
    public synchronized boolean removeAll(Collection<?> c) {
        c.forEach(this::remove);

        return true;
    }

    @Override
    public synchronized boolean retainAll(Collection<?> c) {
        ConcurrentLinkedList<T> n = new ConcurrentLinkedList<>();
        forEachNode(ref -> {
            if (c.contains(ref.node)) {
                n.add(ref.node.data);
            }
        });

        clear();
        this.root.data = n.root.data;
        this.root.next = n.root.next;

        return true;
    }

    @Override
    public synchronized void clear() {
        forEachNode(node -> {
            node.node.data = null;
            node.node.next = null;
            node.node.prev = null;
        });
    }

    @Override
    public synchronized T get(int index) {
        if (index == 0) {
            return getFirst();
        } else if (index == (this.size - 1)) {
            return getLast();
        }

        return getNodeAt(index).data;
    }

    @Override
    public synchronized T set(int index, T element) {
        Node<T> at = getNodeAt(index);
        T prev = at.data;
        at.data = element;

        return prev;
    }

    @Override
    public synchronized void add(int index, T element) {
        Node<T> at = getNodeAt(index);
        Node<T> next = at.next;

        if (next != null) {
            at.next = new Node<>(at.data, at, at.next);

            if (next.next != null) {
                next.next.prev = at.next;
            }
        }

        at.data = element;
        this.size++;
    }

    @Override
    public synchronized T remove(int index) {
        Node<T> remove = getNodeAt(index);
        T result = remove.data;
        Node<T> next = remove.next;
        Node<T> prev = remove.prev;

        if (next != null) {
            remove.data = next.data;

            if (next.next != null) {
                next.next.prev = remove;
                remove.next = next.next;
            }
        } else {
            remove.data = null;
        }

        if (prev != null) {
            if (next != null) {
                prev.next = next;
                next.prev = prev;
            } else {
                prev.next = null;
                remove.prev = null;
            }
        }
        this.size--;

        return result;
    }

    @Override
    public synchronized int indexOf(Object o) {
        return findNodeContaining(o).map(r -> r.index)
                .orElse(-1);
    }

    @Override
    public synchronized int lastIndexOf(Object o) {
        return findLastNodeContaining(o).map(r -> r.index)
                .orElse(-1);
    }

    @Override
    public synchronized ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public synchronized ListIterator<T> listIterator(int index) {
        return new ConcurrentLinkedListIterator<>(this, index);
    }

    @Override
    public synchronized List<T> subList(int fromIndex, int toIndex) {
        ConcurrentLinkedList<T> result = new ConcurrentLinkedList<>();

        Node<T> current = getNodeAt(fromIndex);
        int index = fromIndex;
        while (index < toIndex) {
            result.add(current.data);
            current = current.next;
            index++;
        }

        return result;
    }

    private void checkSize() {
        if (this.size == 0) {
            throw new IndexOutOfBoundsException(0);
        }
    }

    private void forEachNode(Consumer<NodeRef<T>> consumer) {
        Node<T> current = this.root;

        int index = 0;
        while (current != null) {
            NodeRef<T> prev = new NodeRef<>(index, current);
            current = current.next;
            index++;

            consumer.accept(prev);
        }
    }

    private Optional<NodeRef<T>> findNodeContaining(Object obj) {
        return findNodeContaining(obj, 0);
    }

    private Optional<NodeRef<T>> findNodeContaining(Object obj, int bound) {
        Node<T> current = this.root;

        int index = 0;
        while (current != null) {
            if (index >= bound && Objects.equals(current.data, obj)) {
                return Optional.of(new NodeRef<>(index, current));
            }

            current = current.next;
            index++;
        }

        return Optional.empty();
    }

    private Optional<NodeRef<T>> findLastNodeContaining(Object obj) {
        Node<T> current = this.last;

        int index = this.size - 1;
        while (current != null) {
            if (Objects.equals(current.data, obj)) {
                return Optional.of(new NodeRef<>(index, current));
            }

            current = current.prev;
            index--;
        }

        return Optional.empty();
    }

    private Node<T> getNodeAt(int index) {
        final int finalIndex = index;

        if (finalIndex >= this.size) {
            throw new IndexOutOfBoundsException(index + " >= maximum bound (" + this.size + ")");
        }

        if (finalIndex == 0) {
            return this.root;
        } else if (finalIndex == this.size - 1) {
            return this.last;
        } else {
            Node<T> current;
            String direction;
            AtomicInteger curIndex = new AtomicInteger();
            Predicate<Node<T>> linkFinder;
            Function<Node<T>, Node<T>> nodeSupplier;

            if (finalIndex <= (size / 2) + 5) {
                current = this.root;
                direction = "next";
                curIndex.set(0);
                linkFinder = (cur) -> cur.next == null;
                nodeSupplier = (cur) -> {
                    curIndex.getAndIncrement();
                    return cur.next;
                };
            } else {
                current = this.last;
                direction = "prev";
                curIndex.set(size - 1);
                linkFinder = (cur) -> cur.prev == null;
                nodeSupplier = (cur) -> {
                    curIndex.getAndDecrement();
                    return cur.prev;
                };
            }

            while (index != 0) {
                index--;

                if (linkFinder.test(current)) {
                    throw new IllegalStateException("Unexpected broken link! Node(" + current.data.toString() + "@" + curIndex.get() + ")." + direction + " is null!");
                }

                current = nodeSupplier.apply(current);
            }

            return current;
        }
    }

    public static class ConcurrentLinkedListIterator<T> implements ListIterator<T> {

        private final ConcurrentLinkedList<T> list;

        private int index;

        private Node<T> current;

        private NodeRef<T> lastRet;

        public ConcurrentLinkedListIterator(ConcurrentLinkedList<T> list, int index) {
            this.list = list;
            this.current = list.getNodeAt(index);
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            synchronized (this.list) {
                return this.current.next != null;
            }
        }

        @Override
        public T next() {
            synchronized (this.list) {
                T result = this.current.data;
                this.lastRet = new NodeRef<>(this.index, this.current);
                this.current = this.current.next;
                this.index++;

                return result;
            }
        }

        @Override
        public boolean hasPrevious() {
            synchronized (this.list) {
                return this.current.prev != null;
            }
        }

        @Override
        public T previous() {
            synchronized (this.list) {
                T result = this.current.prev.data;
                this.lastRet = new NodeRef<>(this.index - 1, this.current.prev);
                this.current = this.current.prev;
                this.index--;

                return result;
            }
        }

        @Override
        public int nextIndex() {
            synchronized (this.list) {
                return this.index + 1;
            }
        }

        @Override
        public int previousIndex() {
            synchronized (this.list) {
                return this.index - 1;
            }
        }

        public int index() {
            synchronized (this.list) {
                return this.index;
            }
        }

        @Override
        public void remove() {
            synchronized (this.list) {
                if (this.lastRet == null) {
                    throw new IllegalStateException("no next() value!");
                }

                this.list.remove(this.lastRet.index);
            }
        }

        @Override
        public void set(T t) {
            synchronized (this.list) {
                this.lastRet.node.data = t;
            }
        }

        @Override
        public void add(T t) {
            synchronized (this.list) {
                this.list.add(this.lastRet.index, t);
            }
        }
    }

    public static class NodeRef<T> {
        private final int index;
        private final Node<T> node;

        public NodeRef(int index, Node<T> node) {
            this.index = index;
            this.node = node;
        }
    }

    public static class Node<T> {
        private T data;

        private Node<T> next;
        private Node<T> prev;

        public Node() {
            this((T) null);
        }

        public Node(T data) {
            this(data, null);
        }

        public Node(T data, Node<T> prev) {
            this(data, prev, null);
        }

        public Node(T data, Node<T> prev, Node<T> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(Node<T> node) {
            this.data = node.data;
            this.next = node.next;
            this.prev = node.prev;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node other)) {
                return false;
            }

            return Objects.equals(this.data, other.data) && Objects.equals(this.next, other.next)
                    && Objects.equals(this.prev, other.prev);
        }

        @Override
        public int hashCode() {
            return ObjHelpers.hashCode(this.data, this.next, this.prev);
        }
    }
}
