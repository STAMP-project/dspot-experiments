/**
 * Copyright 2016 NAVER Corp.
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
package com.navercorp.pinpoint.plugin.vertx;


import com.navercorp.pinpoint.bootstrap.config.DefaultProfilerConfig;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jaehong.kim
 */
public class VertxConfigTest {
    @Test
    public void config() {
        Properties properties = new Properties();
        properties.setProperty("profiler.vertx.enable", "true");
        properties.setProperty("profiler.vertx.http.server.enable", "true");
        properties.setProperty("profiler.vertx.http.client.enable", "true");
        properties.setProperty("profiler.vertx.bootstrap.main", "io.vertx.core.Starter");
        ProfilerConfig profilerConfig = new DefaultProfilerConfig(properties);
        VertxConfig config = new VertxConfig(profilerConfig);
        Assert.assertEquals(true, config.isEnable());
        Assert.assertEquals(true, config.isEnableHttpServer());
        Assert.assertEquals(true, config.isEnableHttpClient());
        Assert.assertEquals(1, config.getBootstrapMains().size());
        Assert.assertEquals("io.vertx.core.Starter", config.getBootstrapMains().get(0));
        properties = new Properties();
        properties.setProperty("profiler.vertx.enable", "false");
        properties.setProperty("profiler.vertx.http.server.enable", "false");
        properties.setProperty("profiler.vertx.http.client.enable", "false");
        properties.setProperty("profiler.vertx.bootstrap.main", "");
        profilerConfig = new DefaultProfilerConfig(properties);
        config = new VertxConfig(profilerConfig);
        Assert.assertEquals(false, config.isEnable());
        Assert.assertEquals(false, config.isEnableHttpServer());
        Assert.assertEquals(false, config.isEnableHttpClient());
        Assert.assertEquals(0, config.getBootstrapMains().size());
    }
}

