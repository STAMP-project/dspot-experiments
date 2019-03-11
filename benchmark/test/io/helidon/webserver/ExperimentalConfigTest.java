/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver;


import Http2Configuration.Builder;
import Http2Configuration.DEFAULT_MAX_CONTENT_LENGTH;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Class ExperimentalConfigTest.
 */
public class ExperimentalConfigTest {
    @Test
    public void http2BuilderDefaults() {
        Http2Configuration http2 = new Http2Configuration.Builder().build();
        MatcherAssert.assertThat(http2.enable(), CoreMatchers.is(false));
        MatcherAssert.assertThat(http2.maxContentLength(), CoreMatchers.is(DEFAULT_MAX_CONTENT_LENGTH));
    }

    @Test
    public void configBuilder() {
        Http2Configuration.Builder builder = new Http2Configuration.Builder();
        builder.enable(true);
        builder.maxContentLength((32 * 1024));
        Http2Configuration http2 = builder.build();
        MatcherAssert.assertThat(http2.enable(), CoreMatchers.is(true));
        MatcherAssert.assertThat(http2.maxContentLength(), CoreMatchers.is((32 * 1024)));
        ExperimentalConfiguration config = new ExperimentalConfiguration.Builder().http2(http2).build();
        MatcherAssert.assertThat(config.http2(), CoreMatchers.is(http2));
    }

    @Test
    public void configResource() {
        Config http2 = Config.create(ConfigSources.classpath("experimental/application.yaml")).get("webserver").get("experimental").get("http2");
        MatcherAssert.assertThat(http2.get("enable").asBoolean().get(), CoreMatchers.is(true));
        MatcherAssert.assertThat(((int) (http2.get("max-content-length").asInt().get())), CoreMatchers.is((16 * 1024)));
    }
}

