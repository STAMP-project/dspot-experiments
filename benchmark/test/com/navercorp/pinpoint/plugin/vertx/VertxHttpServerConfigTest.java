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
public class VertxHttpServerConfigTest {
    @Test
    public void config() {
        Properties properties = new Properties();
        properties.setProperty("profiler.vertx.http.server.tracerequestparam", "true");
        properties.setProperty("profiler.vertx.http.server.excludeurl", "/l7/check");
        properties.setProperty("profiler.vertx.http.server.realipheader", "RealIp");
        properties.setProperty("profiler.vertx.http.server.realipemptyvalue", "unknown");
        properties.setProperty("profiler.vertx.http.server.excludemethod", "chunk, continue");
        ProfilerConfig profilerConfig = new DefaultProfilerConfig(properties);
        VertxHttpServerConfig config = new VertxHttpServerConfig(profilerConfig);
        Assert.assertEquals(true, config.isTraceRequestParam());
        Assert.assertEquals(true, config.getExcludeUrlFilter().filter("/l7/check"));
        Assert.assertEquals("RealIp", config.getRealIpHeader());
        Assert.assertEquals("unknown", config.getRealIpEmptyValue());
        Assert.assertEquals(true, config.getExcludeProfileMethodFilter().filter("CHUNK"));
        properties = new Properties();
        properties.setProperty("profiler.vertx.http.server.tracerequestparam", "false");
        properties.setProperty("profiler.vertx.http.server.excludeurl", "");
        properties.setProperty("profiler.vertx.http.server.realipheader", "");
        properties.setProperty("profiler.vertx.http.server.realipemptyvalue", "");
        properties.setProperty("profiler.vertx.http.server.excludemethod", "");
        profilerConfig = new DefaultProfilerConfig(properties);
        config = new VertxHttpServerConfig(profilerConfig);
        Assert.assertEquals(false, config.isTraceRequestParam());
        Assert.assertEquals(false, config.getExcludeUrlFilter().filter("/l7/check"));
        Assert.assertEquals("", config.getRealIpHeader());
        Assert.assertEquals("", config.getRealIpEmptyValue());
        Assert.assertEquals(false, config.getExcludeProfileMethodFilter().filter("CHUNK"));
    }
}

