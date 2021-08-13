package tv.isshoni.araragi.machine.reflection.model;

public interface IInheritanceCrawler<E> {

    boolean hasNext();

    Class<? super E> next();
}
