/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique;


import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.bootique.annotation.EnvironmentProperties;
import io.bootique.meta.application.OptionMetadata;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class BQCoreModuleExtenderTest {
    @Test
    public void testSetProperties() {
        Injector i = Guice.createInjector(( b) -> {
            BQCoreModule.extend(b).setProperty("a", "b").setProperty("c", "d");
            b.bind(.class);
        });
        BQCoreModuleExtenderTest.MapInspector inspector = i.getInstance(BQCoreModuleExtenderTest.MapInspector.class);
        Assert.assertEquals("b", inspector.map.get("a"));
        Assert.assertEquals("d", inspector.map.get("c"));
    }

    @Test
    public void testSetOptions() {
        OptionMetadata o1 = OptionMetadata.builder("o1").build();
        OptionMetadata o2 = OptionMetadata.builder("o2").build();
        Injector i = Guice.createInjector(( b) -> {
            BQCoreModule.extend(b).addOptions(o1, o2);
            b.bind(.class);
        });
        BQCoreModuleExtenderTest.OptionsInspector inspector = i.getInstance(BQCoreModuleExtenderTest.OptionsInspector.class);
        Assert.assertEquals(2, inspector.options.size());
        Assert.assertTrue(inspector.options.contains(o1));
        Assert.assertTrue(inspector.options.contains(o2));
    }

    static class MapInspector {
        Map<String, String> map;

        @Inject
        public MapInspector(@EnvironmentProperties
        Map<String, String> map) {
            this.map = map;
        }
    }

    static class OptionsInspector {
        Set<OptionMetadata> options;

        @Inject
        public OptionsInspector(Set<OptionMetadata> options) {
            this.options = options;
        }
    }
}

