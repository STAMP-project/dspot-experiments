/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.profiler.instrument.classloading;


import com.navercorp.pinpoint.common.plugin.Plugin;
import com.navercorp.pinpoint.profiler.plugin.PluginConfig;
import com.navercorp.pinpoint.profiler.plugin.PluginPackageFilter;
import java.net.URL;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class JarProfilerPluginClassInjectorTest {
    public static final String CONTEXT_TYPE_MATCH_CLASS_LOADER = "org.springframework.context.support.ContextTypeMatchClassLoader";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String LOG4_IMPL = "org.slf4j.impl";

    @Test
    public void testInjectClass() throws Exception {
        final Plugin plugin = getMockPlugin("org.slf4j.impl.Log4jLoggerAdapter");
        final ClassLoader contextTypeMatchClassLoader = createContextTypeMatchClassLoader(new URL[]{ plugin.getURL() });
        final PluginPackageFilter pluginPackageFilter = new PluginPackageFilter(Collections.singletonList(JarProfilerPluginClassInjectorTest.LOG4_IMPL));
        PluginConfig pluginConfig = new PluginConfig(plugin, pluginPackageFilter);
        logger.debug("pluginConfig:{}", pluginConfig);
        ClassInjector injector = new PlainClassLoaderHandler(pluginConfig);
        final Class<?> loggerClass = injector.injectClass(contextTypeMatchClassLoader, logger.getClass().getName());
        logger.debug("ClassLoader{}", loggerClass.getClassLoader());
        Assert.assertEquals("check className", loggerClass.getName(), "org.slf4j.impl.Log4jLoggerAdapter");
        Assert.assertEquals("check ClassLoader", loggerClass.getClassLoader().getClass().getName(), JarProfilerPluginClassInjectorTest.CONTEXT_TYPE_MATCH_CLASS_LOADER);
    }
}

