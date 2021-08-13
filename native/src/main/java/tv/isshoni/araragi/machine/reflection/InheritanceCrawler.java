package tv.isshoni.araragi.machine.reflection;

import tv.isshoni.araragi.machine.reflection.model.IInheritanceCrawler;

import java.util.LinkedList;
import java.util.Queue;

public class InheritanceCrawler<E> implements IInheritanceCrawler<E> {

    protected final Class<? super E> original;

    protected final Queue<Class<? super E>> classesLeft;

    public InheritanceCrawler(Class<? super E> clazz) {
        this.original = clazz;
        this.classesLeft = new LinkedList<>();

        crawl(this.original);
    }

    private void crawl(Class<? super E> clazz) {
        this.classesLeft.add(clazz);

        for (Class<?> c : clazz.getInterfaces()) {
            try {
                this.classesLeft.add((Class<? super E>) c);
            } catch (ClassCastException e) { /* SWALLOWED */ }
        }
    }

    @Override
    public boolean hasNext() {
        return !this.classesLeft.isEmpty();
    }

    @Override
    public Class<? super E> next() {
        if (!hasNext()) {
            throw new IndexOutOfBoundsException("Reached the top of " + this.original.getName() + " pedigree");
        }

        return this.classesLeft.poll();
    }
}
