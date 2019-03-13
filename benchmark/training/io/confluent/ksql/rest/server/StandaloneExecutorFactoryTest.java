package io.confluent.ksql.rest.server;


import StandaloneExecutorFactory.CONFIG_TOPIC_SUFFIX;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.rest.server.StandaloneExecutorFactory.StandaloneExecutorConstructor;
import io.confluent.ksql.rest.server.computation.ConfigStore;
import io.confluent.ksql.rest.util.KsqlInternalTopicUtils;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.version.metrics.VersionCheckerAgent;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class StandaloneExecutorFactoryTest {
    private static final String QUERIES_FILE = "queries";

    private static final String INSTALL_DIR = "install";

    private final Map<String, String> properties = Collections.emptyMap();

    private final KsqlConfig baseConfig = new KsqlConfig(properties);

    private final KsqlConfig mergedConfig = new KsqlConfig(Collections.emptyMap());

    private final String configTopicName = KsqlInternalTopicUtils.getTopicName(baseConfig, CONFIG_TOPIC_SUFFIX);

    @Mock
    private Function<KsqlConfig, ServiceContext> serviceContextFactory;

    @Mock
    private BiFunction<String, KsqlConfig, ConfigStore> configStoreFactory;

    @Mock
    private ServiceContext serviceContext;

    @Mock
    private KafkaTopicClient topicClient;

    @Mock
    private ConfigStore configStore;

    @Mock
    private StandaloneExecutorConstructor constructor;

    @Mock
    private StandaloneExecutor standaloneExecutor;

    @Mock
    private VersionCheckerAgent versionChecker;

    @Captor
    private ArgumentCaptor<KsqlEngine> engineCaptor;

    private static class KsqlConfigMatcher extends TypeSafeMatcher<KsqlConfig> {
        private final KsqlConfig expected;

        KsqlConfigMatcher(final KsqlConfig expected) {
            this.expected = expected;
        }

        @Override
        public void describeTo(final Description description) {
            description.appendValue(expected.getAllConfigPropsWithSecretsObfuscated());
        }

        @Override
        public boolean matchesSafely(final KsqlConfig ksqlConfig) {
            return ksqlConfig.getAllConfigPropsWithSecretsObfuscated().equals(expected.getAllConfigPropsWithSecretsObfuscated());
        }
    }

    @Test
    public void shouldCreateConfigTopicThenGetConfig() {
        // When:
        create();
        // Then:
        final InOrder inOrder = Mockito.inOrder(topicClient, configStoreFactory, constructor);
        inOrder.verify(topicClient).createTopic(ArgumentMatchers.eq(configTopicName), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyMap());
        inOrder.verify(configStoreFactory).apply(ArgumentMatchers.eq(configTopicName), MockitoHamcrest.argThat(StandaloneExecutorFactoryTest.sameConfig(baseConfig)));
        inOrder.verify(constructor).create(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.same(mergedConfig), ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}

