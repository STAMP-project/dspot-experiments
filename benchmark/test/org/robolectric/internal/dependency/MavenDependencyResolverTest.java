package org.robolectric.internal.dependency;


import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import org.apache.maven.artifact.ant.DependenciesTask;
import org.apache.maven.artifact.ant.RemoteRepository;
import org.apache.maven.model.Dependency;
import org.apache.tools.ant.Project;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class MavenDependencyResolverTest {
    private static final String REPOSITORY_URL = "https://default-repo";

    private static final String REPOSITORY_ID = "remote";

    private static final String REPOSITORY_USERNAME = "username";

    private static final String REPOSITORY_PASSWORD = "password";

    private DependenciesTask dependenciesTask;

    private Project project;

    @Test
    public void getLocalArtifactUrl_shouldAddConfiguredRemoteRepository() {
        DependencyResolver dependencyResolver = createResolver();
        DependencyJar dependencyJar = new DependencyJar("group1", "artifact1", "", null);
        dependencyResolver.getLocalArtifactUrl(dependencyJar);
        List<RemoteRepository> repositories = dependenciesTask.getRemoteRepositories();
        Assert.assertEquals(1, repositories.size());
        RemoteRepository remoteRepository = repositories.get(0);
        Assert.assertEquals(MavenDependencyResolverTest.REPOSITORY_URL, remoteRepository.getUrl());
        Assert.assertEquals(MavenDependencyResolverTest.REPOSITORY_ID, remoteRepository.getId());
    }

    @Test
    public void getLocalArtifactUrl_shouldAddDependencyToDependenciesTask() {
        DependencyResolver dependencyResolver = createResolver();
        DependencyJar dependencyJar = new DependencyJar("group1", "artifact1", "3", null);
        dependencyResolver.getLocalArtifactUrl(dependencyJar);
        List<Dependency> dependencies = dependenciesTask.getDependencies();
        Assert.assertEquals(1, dependencies.size());
        Dependency dependency = dependencies.get(0);
        Assert.assertEquals("group1", dependency.getGroupId());
        Assert.assertEquals("artifact1", dependency.getArtifactId());
        Assert.assertEquals("3", dependency.getVersion());
        Assert.assertEquals("jar", dependency.getType());
        Assert.assertNull(dependency.getClassifier());
    }

    @Test
    public void getLocalArtifactUrl_shouldExecuteDependenciesTask() {
        DependencyResolver dependencyResolver = createResolver();
        DependencyJar dependencyJar = new DependencyJar("group1", "artifact1", "", null);
        dependencyResolver.getLocalArtifactUrl(dependencyJar);
        Mockito.verify(dependenciesTask).execute();
    }

    @Test
    public void getLocalArtifactUrl_shouldReturnCorrectUrlForArtifactKey() throws Exception {
        DependencyResolver dependencyResolver = createResolver();
        DependencyJar dependencyJar = new DependencyJar("group1", "artifact1", "", null);
        URL url = dependencyResolver.getLocalArtifactUrl(dependencyJar);
        Assert.assertEquals(Paths.get("path1").toUri().toURL().toExternalForm(), url.toExternalForm());
    }

    @Test
    public void getLocalArtifactUrl_shouldReturnCorrectUrlForArtifactKeyWithClassifier() throws Exception {
        DependencyResolver dependencyResolver = createResolver();
        DependencyJar dependencyJar = new DependencyJar("group3", "artifact3", "", "classifier3");
        URL url = dependencyResolver.getLocalArtifactUrl(dependencyJar);
        Assert.assertEquals(Paths.get("path3").toUri().toURL().toExternalForm(), url.toExternalForm());
    }
}

