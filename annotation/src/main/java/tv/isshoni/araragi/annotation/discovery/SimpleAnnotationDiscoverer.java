package tv.isshoni.araragi.annotation.discovery;

import tv.isshoni.araragi.annotation.model.IAnnotationDiscoverer;
import tv.isshoni.araragi.annotation.model.IAnnotationManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.reflections8.Reflections;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.reflections8.util.ConfigurationBuilder;

// TODO: Write some sort of load order management code into me; aka, find dependent annotations and
// TODO: Ensure that they're loaded first.
public class SimpleAnnotationDiscoverer implements IAnnotationDiscoverer {

    protected final List<String> packages;

    protected final IAnnotationManager annotationManager;

    private Reflections reflections;

    public SimpleAnnotationDiscoverer(IAnnotationManager annotationManager) {
        this.annotationManager = annotationManager;
        this.packages = new LinkedList<>();
    }

    @Override
    public SimpleAnnotationDiscoverer withPackages(String... packages) {
        this.packages.addAll(Arrays.asList(packages));

        return this;
    }

    @Override
    public IAnnotationDiscoverer withPackages(Collection<String> packages) {
        this.packages.addAll(packages);

        return this;
    }

    @Override
    public List<String> getPackages() {
        return Collections.unmodifiableList(this.packages);
    }

    @Override
    public final Reflections getReflections() {
        if (Objects.isNull(this.reflections)) {
            this.reflections = construct();
        }

        return this.reflections;
    }

    @Override
    public Reflections construct() {
        return new Reflections(new ConfigurationBuilder()
                .addScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false))
                .forPackages(this.packages.toArray(new String[0])));
    }

    @Override
    public IAnnotationManager getAnnotationManager() {
        return this.annotationManager;
    }
}
