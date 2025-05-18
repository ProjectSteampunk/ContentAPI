package me.instrumentalityi.__REPO_NAME_LOWER__.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

public class __REPO_NAME__Loader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder pluginClasspathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder("maven-central", "default", "https://repo1.maven.org/maven2/").build());

        //resolver.addDependency(new Dependency(new DefaultArtifact("io.minio:minio:8.5.17"), null));

        pluginClasspathBuilder.addLibrary(resolver);
    }
}
