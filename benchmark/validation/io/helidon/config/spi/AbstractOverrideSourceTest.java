/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.config.spi;


import AbstractOverrideSource.Builder;
import AbstractSource.Builder.DEFAULT_CHANGES_EXECUTOR;
import io.helidon.common.reactive.Flow;
import io.helidon.config.ConfigException;
import io.helidon.config.PollingStrategies;
import java.time.Instant;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link AbstractOverrideSource}.
 */
public class AbstractOverrideSourceTest {
    @Test
    public void testBuilderDefault() {
        AbstractOverrideSource.Builder builder = new AbstractOverrideSource.Builder(Void.class) {
            @Override
            public OverrideSource build() {
                return Optional::empty;
            }
        };
        Assert.assertThat(builder.isMandatory(), Matchers.is(true));
        Assert.assertThat(builder.changesExecutor(), Matchers.is(DEFAULT_CHANGES_EXECUTOR));
        Assert.assertThat(builder.changesMaxBuffer(), Matchers.is(Flow.defaultBufferSize()));
        Assert.assertThat(builder.pollingStrategy(), Matchers.is(PollingStrategies.nop()));
    }

    @Test
    public void testFormatDescriptionOptionalNoParams() {
        AbstractOverrideSourceTest.TestingOverrideSource testingOverrideSource = pollingStrategy(((PollingStrategy) (() -> null))).build();
        Assert.assertThat(description(), Matchers.is("TestingOverride[]?*"));
    }

    @Test
    public void testFormatDescriptionOptionalNoParamsNoPolling() {
        AbstractOverrideSourceTest.UidOverrideSource overrideSource = optional().build();
        Assert.assertThat(description(), Matchers.is("UidOverride[]?"));
    }

    @Test
    public void testFormatDescriptionOptionalWithParams() {
        AbstractOverrideSourceTest.UidOverrideSource overrideSource = optional().build();
        Assert.assertThat(description(), Matchers.is("UidOverride[PA,RAMS]?*"));
    }

    @Test
    public void testFormatDescriptionMandatoryNoParams() {
        AbstractOverrideSourceTest.UidOverrideSource overrideSource = pollingStrategy(((PollingStrategy) (() -> null))).build();
        Assert.assertThat(description(), Matchers.is("UidOverride[]*"));
    }

    @Test
    public void testFormatDescriptionMandatoryWithParams() {
        AbstractOverrideSourceTest.UidOverrideSource overrideSource = pollingStrategy(((PollingStrategy) (() -> null))).build();
        Assert.assertThat(description(), Matchers.is("UidOverride[PA,RAMS]*"));
    }

    private static class TestingOverrideSource extends AbstractOverrideSource {
        /**
         * Initializes config source from builder.
         *
         * @param builder
         * 		builder to be initialized from
         */
        protected TestingOverrideSource(AbstractOverrideSourceTest.TestingOverrideSource.Builder builder) {
            super(builder);
        }

        public static AbstractOverrideSourceTest.TestingOverrideSource.Builder builder() {
            return new AbstractOverrideSourceTest.TestingOverrideSource.Builder();
        }

        @Override
        protected Optional dataStamp() {
            return Optional.empty();
        }

        @Override
        protected Data<OverrideData, Instant> loadData() throws ConfigException {
            return new Data(null, Optional.of(Instant.MIN));
        }

        public static class Builder extends AbstractOverrideSource.Builder<AbstractOverrideSourceTest.TestingOverrideSource.Builder, Void> {
            protected Builder() {
                super(Void.class);
            }

            @Override
            public AbstractOverrideSourceTest.TestingOverrideSource build() {
                return new AbstractOverrideSourceTest.TestingOverrideSource(this);
            }
        }
    }

    private static class UidOverrideSource extends AbstractOverrideSource {
        private final String uid;

        public UidOverrideSource(AbstractOverrideSourceTest.UidOverrideSource.UidBuilder builder, String uid) {
            super(builder);
            this.uid = uid;
        }

        public static AbstractOverrideSourceTest.UidOverrideSource.UidBuilder builder() {
            return new AbstractOverrideSourceTest.UidOverrideSource.UidBuilder();
        }

        public static AbstractOverrideSourceTest.UidOverrideSource.UidBuilder builderWithUid(String uid) {
            return AbstractOverrideSourceTest.UidOverrideSource.builder().uid(uid);
        }

        public static AbstractOverrideSourceTest.UidOverrideSource withUid(String uid) {
            return AbstractOverrideSourceTest.UidOverrideSource.builderWithUid(uid).build();
        }

        @Override
        protected String uid() {
            return uid;
        }

        @Override
        protected Optional dataStamp() {
            return Optional.empty();
        }

        @Override
        protected Data loadData() throws ConfigException {
            return null;
        }

        public static final class UidBuilder extends Builder<AbstractOverrideSourceTest.UidOverrideSource.UidBuilder, String> {
            private String uid;

            private UidBuilder() {
                super(String.class);
            }

            public AbstractOverrideSourceTest.UidOverrideSource.UidBuilder uid(String uid) {
                this.uid = uid;
                return this;
            }

            @Override
            public AbstractOverrideSourceTest.UidOverrideSource build() {
                return new AbstractOverrideSourceTest.UidOverrideSource(this, uid);
            }
        }
    }
}

