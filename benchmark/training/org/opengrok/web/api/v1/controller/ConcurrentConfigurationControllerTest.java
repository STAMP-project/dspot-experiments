package org.opengrok.web.api.v1.controller;


import RepositoryInstalled.GitInstalled;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.opengrok.indexer.condition.ConditionalRun;
import org.opengrok.indexer.condition.ConditionalRunRule;
import org.opengrok.indexer.configuration.Project;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.history.RepositoryInfo;
import org.opengrok.indexer.util.TestRepository;
import org.opengrok.web.api.v1.suggester.provider.service.SuggesterService;


@ConditionalRun(GitInstalled.class)
public class ConcurrentConfigurationControllerTest extends JerseyTest {
    private static final int PROJECTS_COUNT = 20;

    private static final int THREAD_COUNT = Math.max(30, ((Runtime.getRuntime().availableProcessors()) * 2));

    private static final int TASK_COUNT = 100;

    @Rule
    public ConditionalRunRule rule = new ConditionalRunRule();

    @Mock
    private SuggesterService suggesterService;

    private RuntimeEnvironment env = RuntimeEnvironment.getInstance();

    TestRepository repository;

    String origSourceRootPath;

    String origDataRootPath;

    Map<String, Project> origProjects;

    List<RepositoryInfo> origRepositories;

    @Test
    public void testConcurrentConfigurationReloads() throws InterruptedException, ExecutionException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(ConcurrentConfigurationControllerTest.THREAD_COUNT);
        List<Future<?>> futures = new LinkedList<>();
        /* Now run setting a value in parallel which triggers configuration reload. */
        for (int i = 0; i < (ConcurrentConfigurationControllerTest.TASK_COUNT); i++) {
            futures.add(threadPool.submit(() -> {
                Response put = target("configuration").path("projectsEnabled").request().put(Entity.text("true"));
                Assert.assertEquals(204, put.getStatus());
                assertTestedProjects();
            }));
        }
        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.MINUTES);
        for (Future<?> future : futures) {
            // calling get on a future will rethrow the exceptions in its thread (i. e. assertions in this case)
            future.get();
        }
    }

    @Test
    public void testConcurrentCInvalidateRepositories() throws InterruptedException, ExecutionException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(ConcurrentConfigurationControllerTest.THREAD_COUNT);
        final List<RepositoryInfo> repositoryInfos = env.getRepositories();
        List<Future<?>> futures = new LinkedList<>();
        /* Now run apply config in parallel */
        for (int i = 0; i < (ConcurrentConfigurationControllerTest.TASK_COUNT); i++) {
            futures.add(threadPool.submit(() -> {
                env.applyConfig(false, false);
                assertTestedProjects();
            }));
        }
        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.MINUTES);
        for (Future<?> future : futures) {
            // calling get on a future will rethrow the exceptions in its thread (i. e. assertions in this case)
            future.get();
        }
    }
}

