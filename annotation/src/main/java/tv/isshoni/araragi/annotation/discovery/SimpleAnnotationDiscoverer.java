package tv.isshoni.araragi.annotation.discovery;

import org.reflections8.Reflections;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.reflections8.util.ConfigurationBuilder;
import org.reflections8.util.FilterBuilder;
import tv.isshoni.araragi.annotation.manager.IAnnotationManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SimpleAnnotationDiscoverer implements IAnnotationDiscoverer {

    protected final List<String> packages;

    protected final IAnnotationManager annotationManager;

    public SimpleAnnotationDiscoverer(IAnnotationManager annotationManager) {
        this(annotationManager, new LinkedList<>());
    }

    public SimpleAnnotationDiscoverer(IAnnotationManager annotationManager, List<String> packages) {
        this.annotationManager = annotationManager;
        this.packages = packages;
    }

    @Override
    public SimpleAnnotationDiscoverer withPackages(String... packages) {
        this.packages.addAll(Arrays.asList(packages));

        return this;
    }

    @Override
    public SimpleAnnotationDiscoverer withPackages(Collection<String> packages) {
        this.packages.addAll(packages);

        return this;
    }

    @Override
    public List<String> getPackages() {
        return Collections.unmodifiableList(this.packages);
    }

    @Override
    public Reflections construct() {
        return new Reflections(new ConfigurationBuilder()
                .addScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false))
                .forPackages(this.packages.toArray(new String[0]))
                .filterInputsBy(new FilterBuilder().includePackage(this.packages.toArray(new String[0]))));
    }

    @Override
    public IAnnotationManager getAnnotationManager() {
        return this.annotationManager;
    }
}
