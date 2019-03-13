/**
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config.git;


import Flow.Subscription;
import io.helidon.common.CollectionsHelper;
import io.helidon.common.reactive.Flow;
import io.helidon.config.Config;
import io.helidon.config.ConfigException;
import io.helidon.config.ConfigParsers;
import io.helidon.config.ConfigSources;
import io.helidon.config.MissingValueException;
import io.helidon.config.PollingStrategies;
import io.helidon.config.git.GitConfigSourceBuilder.GitEndpoint;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.spi.ConfigSource;
import io.helidon.config.spi.PollingStrategy;
import io.helidon.config.test.infra.TemporaryFolderExt;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.junit.RepositoryTestCase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link GitConfigSourceBuilder}.
 */
public class GitConfigSourceBuilderTest extends RepositoryTestCase {
    private Git git;

    @RegisterExtension
    static TemporaryFolderExt folder = TemporaryFolderExt.build();

    @Test
    public void testMaster() throws Exception {
        try (ConfigSource source = GitConfigSourceBuilder.create("application.properties").uri(URI.create(fileUri())).parser(ConfigParsers.properties()).build()) {
            Optional<ObjectNode> root = source.load();
            MatcherAssert.assertThat(root.isPresent(), Is.is(true));
            MatcherAssert.assertThat(root.get().get("greeting"), valueNode("ahoy"));
        }
    }

    @Test
    public void testBranch() throws Exception {
        try (ConfigSource source = GitConfigSourceBuilder.create("application.properties").uri(URI.create(fileUri())).branch("test").parser(ConfigParsers.properties()).build()) {
            Optional<ObjectNode> root = source.load();
            MatcherAssert.assertThat(root.isPresent(), Is.is(true));
            MatcherAssert.assertThat(root.get().get("greeting"), valueNode("hello"));
        }
    }

    @Test
    public void testDirectory() throws IOException, Exception, InterruptedException, GitAPIException {
        File tempDir = GitConfigSourceBuilderTest.folder.newFolder();
        try (Git clone = Git.cloneRepository().setURI(fileUri()).setDirectory(tempDir).call();ConfigSource source = GitConfigSourceBuilder.create("application.properties").directory(tempDir.toPath()).parser(ConfigParsers.properties()).build()) {
            MatcherAssert.assertThat(tempDir.toPath().resolve("application.properties").toFile().exists(), Is.is(true));
        }
    }

    @Test
    public void testDirectoryEmpty() throws IOException, Exception {
        Path tempDir = GitConfigSourceBuilderTest.folder.newFolder().toPath();
        try (ConfigSource source = GitConfigSourceBuilder.create("application.properties").uri(URI.create(fileUri())).directory(tempDir).parser(ConfigParsers.properties()).build()) {
            MatcherAssert.assertThat(tempDir.resolve("application.properties").toFile().exists(), Is.is(true));
        }
    }

    @Test
    public void testDirNotEmpty() throws IOException {
        Path tempDir = GitConfigSourceBuilderTest.folder.newFolder().toPath();
        final ConfigException ce = Assertions.assertThrows(ConfigException.class, () -> {
            tempDir.resolve("dust").toFile().createNewFile();
            GitConfigSourceBuilder.create("application.properties").uri(URI.create(fileUri())).directory(tempDir).parser(ConfigParsers.properties()).build();
        });
        MatcherAssert.assertThat(ce.getMessage(), Matchers.startsWith(String.format("Directory '%s' is not empty and it is not a valid repository.", tempDir.toString())));
    }

    @Test
    public void testDirAndUriIsEmpty() throws IOException {
        final ConfigException ce = Assertions.assertThrows(ConfigException.class, () -> {
            GitConfigSourceBuilder.create("application.properties").parser(ConfigParsers.properties()).build();
        });
        MatcherAssert.assertThat(ce.getMessage(), Matchers.startsWith("Directory or Uri must be set."));
    }

    @Test
    public void testPolling() throws IOException, Exception, InterruptedException {
        checkoutBranch("refs/heads/master");
        try (ConfigSource source = GitConfigSourceBuilder.create("application.properties").uri(URI.create(fileUri())).pollingStrategy(PollingStrategies.regular(Duration.ofMillis(50))).parser(ConfigParsers.properties()).build()) {
            Optional<ObjectNode> root = source.load();
            MatcherAssert.assertThat(root.isPresent(), Is.is(true));
            MatcherAssert.assertThat(root.get().get("greeting"), valueNode("ahoy"));
            CountDownLatch subscribeLatch = new CountDownLatch(1);
            CountDownLatch changeLatch = new CountDownLatch(1);
            GitConfigSourceBuilderTest.CancelableSubscriber sub = new GitConfigSourceBuilderTest.CancelableSubscriber(subscribeLatch, changeLatch);
            source.changes().subscribe(sub);
            MatcherAssert.assertThat(subscribeLatch.await(100, TimeUnit.MILLISECONDS), Is.is(true));
            commitFile("application.properties", "greeting=hi", "master");
            MatcherAssert.assertThat(changeLatch.await(1000, TimeUnit.MILLISECONDS), Is.is(true));
            sub.cancel();
            /* Even after canceling the subscription event(s) might be delivered,
            so stall a moment before ending the test and triggering the clean-up
            to avoid warnings if the polling strategy publishes more events
            to its subscribers.
             */
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Test
    public void testDescriptionWithDirAndUri() throws IOException, Exception, GitAPIException {
        Path dir = GitConfigSourceBuilderTest.folder.newFolder().toPath();
        try (Git clone = Git.cloneRepository().setURI(fileUri()).setDirectory(dir.toFile()).call();ConfigSource source = GitConfigSourceBuilder.create("application.conf").uri(URI.create(fileUri())).directory(dir).build()) {
            MatcherAssert.assertThat(source.description(), Is.is(String.format("GitConfig[%s|%s#application.conf]", dir, fileUri())));
        }
    }

    @Test
    public void testDescriptionWithDir() throws IOException, Exception, GitAPIException {
        Path dir = GitConfigSourceBuilderTest.folder.newFolder().toPath();
        try (Git clone = Git.cloneRepository().setURI(fileUri()).setDirectory(dir.toFile()).call();ConfigSource source = GitConfigSourceBuilder.create("application.conf").directory(dir).build()) {
            MatcherAssert.assertThat(source.description(), Is.is(String.format("GitConfig[%s#application.conf]", dir)));
        }
    }

    @Test
    public void testDescriptionWithUri() throws IOException, Exception, GitAPIException {
        Path dir = GitConfigSourceBuilderTest.folder.newFolder().toPath();
        try (Git clone = Git.cloneRepository().setURI(fileUri()).setDirectory(dir.toFile()).call();ConfigSource source = GitConfigSourceBuilder.create("application.conf").uri(URI.create(fileUri())).build()) {
            MatcherAssert.assertThat(source.description(), Is.is(String.format("GitConfig[%s#application.conf]", fileUri())));
        }
    }

    @Test
    public void testFromConfigNothing() {
        Assertions.assertThrows(MissingValueException.class, () -> {
            GitConfigSourceBuilder.create(Config.empty());
        });
    }

    @Test
    public void testFromConfigMandatory() {
        Config metaConfig = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("path", "application.properties"))).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
        GitConfigSourceBuilder builder = GitConfigSourceBuilder.create(metaConfig);
        MatcherAssert.assertThat(builder.target().path(), Is.is("application.properties"));
        MatcherAssert.assertThat(builder.target().uri(), Is.is(Matchers.nullValue()));
        MatcherAssert.assertThat(builder.target().branch(), Is.is("master"));
        MatcherAssert.assertThat(builder.target().directory(), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testFromConfigAll() throws IOException {
        Path directory = GitConfigSourceBuilderTest.folder.newFolder().toPath();
        Config metaConfig = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("path", "application.properties", "uri", fileUri(), "branch", "test", "directory", directory.toString()))).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
        GitConfigSourceBuilder builder = GitConfigSourceBuilder.create(metaConfig);
        MatcherAssert.assertThat(builder.target().path(), Is.is("application.properties"));
        MatcherAssert.assertThat(builder.target().uri(), Is.is(URI.create(fileUri())));
        MatcherAssert.assertThat(builder.target().branch(), Is.is("test"));
        MatcherAssert.assertThat(builder.target().directory(), Is.is(directory));
    }

    @Test
    public void testFromConfigWithCustomPollingStrategy() throws IOException {
        Path directory = GitConfigSourceBuilderTest.folder.newFolder().toPath();
        Config metaConfig = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("path", "application.properties", "uri", fileUri(), "branch", "test", "directory", directory.toString(), "polling-strategy.class", GitConfigSourceBuilderTest.TestingGitEndpointPollingStrategy.class.getName()))).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
        GitConfigSourceBuilder builder = GitConfigSourceBuilder.create(metaConfig);
        MatcherAssert.assertThat(builder.target().path(), Is.is("application.properties"));
        MatcherAssert.assertThat(builder.target().uri(), Is.is(URI.create(fileUri())));
        MatcherAssert.assertThat(builder.target().branch(), Is.is("test"));
        MatcherAssert.assertThat(builder.target().directory(), Is.is(directory));
        MatcherAssert.assertThat(builder.pollingStrategyInternal(), Is.is(Matchers.instanceOf(GitConfigSourceBuilderTest.TestingGitEndpointPollingStrategy.class)));
        GitEndpoint strategyEndpoint = ((GitConfigSourceBuilderTest.TestingGitEndpointPollingStrategy) (builder.pollingStrategyInternal())).gitEndpoint();
        MatcherAssert.assertThat(strategyEndpoint.path(), Is.is("application.properties"));
        MatcherAssert.assertThat(strategyEndpoint.uri(), Is.is(URI.create(fileUri())));
        MatcherAssert.assertThat(strategyEndpoint.branch(), Is.is("test"));
        MatcherAssert.assertThat(strategyEndpoint.directory(), Is.is(directory));
    }

    @Test
    public void testSourceFromConfigByClass() throws Exception {
        Path directory = GitConfigSourceBuilderTest.folder.newFolder().toPath();
        Config metaConfig = Config.builder(ConfigSources.create(ObjectNode.builder().addValue("class", GitConfigSource.class.getName()).addObject("properties", ObjectNode.builder().addValue("path", "application.properties").addValue("uri", fileUri()).addValue("branch", "test").addValue("directory", directory.toString()).build()).build())).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        try (ConfigSource source = metaConfig.as(ConfigSource.class).get()) {
            MatcherAssert.assertThat(source, Is.is(Matchers.instanceOf(GitConfigSource.class)));
            GitConfigSource gitSource = ((GitConfigSource) (source));
            MatcherAssert.assertThat(gitSource.gitEndpoint().path(), Is.is("application.properties"));
            MatcherAssert.assertThat(gitSource.gitEndpoint().uri(), Is.is(URI.create(fileUri())));
            MatcherAssert.assertThat(gitSource.gitEndpoint().branch(), Is.is("test"));
            MatcherAssert.assertThat(gitSource.gitEndpoint().directory(), Is.is(directory));
        }
    }

    @Test
    public void testSourceFromConfigByType() throws Exception {
        Path directory = GitConfigSourceBuilderTest.folder.newFolder().toPath();
        Config metaConfig = Config.builder(ConfigSources.create(ObjectNode.builder().addValue("type", "git").addObject("properties", ObjectNode.builder().addValue("path", "application.properties").addValue("uri", fileUri()).addValue("branch", "test").addValue("directory", directory.toString()).build()).build())).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
        try (ConfigSource source = metaConfig.as(ConfigSource.class).get()) {
            MatcherAssert.assertThat(source, Is.is(Matchers.instanceOf(GitConfigSource.class)));
            GitConfigSource gitSource = ((GitConfigSource) (source));
            MatcherAssert.assertThat(gitSource.gitEndpoint().path(), Is.is("application.properties"));
            MatcherAssert.assertThat(gitSource.gitEndpoint().uri(), Is.is(URI.create(fileUri())));
            MatcherAssert.assertThat(gitSource.gitEndpoint().branch(), Is.is("test"));
            MatcherAssert.assertThat(gitSource.gitEndpoint().directory(), Is.is(directory));
        }
    }

    public static class TestingGitEndpointPollingStrategy implements PollingStrategy {
        private final GitEndpoint gitEndpoint;

        public TestingGitEndpointPollingStrategy(GitEndpoint gitEndpoint) {
            this.gitEndpoint = gitEndpoint;
            MatcherAssert.assertThat(gitEndpoint, Matchers.notNullValue());
        }

        @Override
        public Flow.Publisher<PollingEvent> ticks() {
            return Flow.Subscriber::onComplete;
        }

        public GitEndpoint gitEndpoint() {
            return gitEndpoint;
        }
    }

    /**
     * A subscriber we can cancel at the end of a test so it will not continue
     * to try to access the config after the test is done (and the git
     * infrastructure has been cleaned up) which leads to noisy warnings in
     * the test output.
     */
    private static class CancelableSubscriber implements Flow.Subscriber<Optional<ObjectNode>> {
        private final CountDownLatch subscribeLatch;

        private final CountDownLatch changeLatch;

        private volatile Subscription subscription = null;

        CancelableSubscriber(CountDownLatch subscribeLatch, CountDownLatch changeLatch) {
            this.subscribeLatch = subscribeLatch;
            this.changeLatch = changeLatch;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
            subscribeLatch.countDown();
        }

        @Override
        public void onNext(Optional<ObjectNode> item) {
            if ((subscription) == null) {
                return;
            }
            System.out.println(item);
            if (get().equals("hi")) {
                changeLatch.countDown();
            }
        }

        @Override
        public void onError(Throwable throwable) {
        }

        @Override
        public void onComplete() {
        }

        public void cancel() {
            if ((subscription) != null) {
                subscription.cancel();
                subscription = null;
            }
        }
    }
}

