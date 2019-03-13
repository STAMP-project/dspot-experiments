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


import io.bootique.config.ConfigurationFactory;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.type.TypeRef;
import io.bootique.unit.BQInternalTestFactory;
import java.util.Map;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class Bootique_ConfigurationIT {
    @Rule
    public BQInternalTestFactory testFactory = new BQInternalTestFactory();

    @Test
    public void testEmptyConfig() {
        BQRuntime runtime = testFactory.app("--config=src/test/resources/io/bootique/empty.yml").createRuntime();
        Map<String, String> config = runtime.getInstance(ConfigurationFactory.class).config(new TypeRef<Map<String, String>>() {}, "");
        Assert.assertTrue(config.isEmpty());
    }

    @Test
    public void testCombineConfigAndEmptyConfig() {
        BQRuntime runtime = testFactory.app("--config=classpath:io/bootique/test1.yml", "--config=classpath:io/bootique/empty.yml").createRuntime();
        Map<String, String> config = runtime.getInstance(ConfigurationFactory.class).config(new TypeRef<Map<String, String>>() {}, "");
        Assert.assertEquals(1, config.size());
        Assert.assertEquals("b", config.get("a"));
    }

    @Test
    public void testCombineConfigs() {
        BQRuntime runtime = testFactory.app("--config=classpath:io/bootique/test1.yml", "--config=classpath:io/bootique/test2.yml").createRuntime();
        Map<String, String> config = runtime.getInstance(ConfigurationFactory.class).config(new TypeRef<Map<String, String>>() {}, "");
        Assert.assertEquals(2, config.size());
        Assert.assertEquals("e", config.get("a"));
        Assert.assertEquals("d", config.get("c"));
    }

    @Test
    public void testCombineConfigs_ReverseOrder() {
        BQRuntime runtime = testFactory.app("--config=classpath:io/bootique/test2.yml", "--config=classpath:io/bootique/test1.yml").createRuntime();
        Map<String, String> config = runtime.getInstance(ConfigurationFactory.class).config(new TypeRef<Map<String, String>>() {}, "");
        Assert.assertEquals(2, config.size());
        Assert.assertEquals("b", config.get("a"));
        Assert.assertEquals("d", config.get("c"));
    }

    @Test
    public void testDIConfig() {
        BQRuntime runtime = testFactory.app().module(( b) -> BQCoreModule.extend(b).addConfig("classpath:io/bootique/diconfig1.yml").addConfig("classpath:io/bootique/diconfig2.yml")).createRuntime();
        Map<String, String> config = runtime.getInstance(ConfigurationFactory.class).config(new TypeRef<Map<String, String>>() {}, "");
        Assert.assertEquals(3, config.size());
        Assert.assertEquals("1", config.get("a"));
        Assert.assertEquals("2", config.get("b"));
        Assert.assertEquals("3", config.get("c"));
    }

    @Test
    public void testDIConfig_VsCliOrder() {
        BQRuntime runtime = testFactory.app("-c", "classpath:io/bootique/cliconfig.yml").module(( b) -> BQCoreModule.extend(b).addConfig("classpath:io/bootique/diconfig1.yml").addConfig("classpath:io/bootique/diconfig2.yml")).createRuntime();
        Map<String, Integer> config = runtime.getInstance(ConfigurationFactory.class).config(new TypeRef<Map<String, Integer>>() {}, "");
        Assert.assertEquals(3, config.size());
        Assert.assertEquals(Integer.valueOf(5), config.get("a"));
        Assert.assertEquals(Integer.valueOf(2), config.get("b"));
        Assert.assertEquals(Integer.valueOf(6), config.get("c"));
    }

    @Test
    public void testDIOnOptionConfig() {
        Function<String, Map<String, Integer>> configReader = ( arg) -> {
            BQRuntime runtime = testFactory.app(arg).module(( b) -> BQCoreModule.extend(b).mapConfigResource("opt", "classpath:io/bootique/diconfig1.yml").mapConfigResource("opt", "classpath:io/bootique/diconfig2.yml").addOption(OptionMetadata.builder("opt").build())).createRuntime();
            return runtime.getInstance(ConfigurationFactory.class).config(new TypeRef<Map<String, Integer>>() {}, "");
        };
        Assert.assertTrue(configReader.apply("").isEmpty());
        Map<String, Integer> config = configReader.apply("--opt");
        Assert.assertEquals(3, config.size());
        Assert.assertEquals(Integer.valueOf(1), config.get("a"));
        Assert.assertEquals(Integer.valueOf(2), config.get("b"));
        Assert.assertEquals(Integer.valueOf(3), config.get("c"));
    }

    @Test
    public void testDIOnOptionConfig_OverrideWithOption() {
        Function<String, Map<String, Integer>> configReader = ( arg) -> {
            BQRuntime runtime = testFactory.app(arg).module(( b) -> BQCoreModule.extend(b).mapConfigResource("opt", "classpath:io/bootique/diconfig1.yml").mapConfigResource("opt", "classpath:io/bootique/diconfig2.yml").addOption(OptionMetadata.builder("opt").valueOptional().build()).mapConfigPath("opt", "a")).createRuntime();
            return runtime.getInstance(ConfigurationFactory.class).config(new TypeRef<Map<String, Integer>>() {}, "");
        };
        Assert.assertTrue(configReader.apply("").isEmpty());
        Map<String, Integer> config = configReader.apply("--opt=8");
        Assert.assertEquals(3, config.size());
        Assert.assertEquals(Integer.valueOf(8), config.get("a"));
        Assert.assertEquals(Integer.valueOf(2), config.get("b"));
        Assert.assertEquals(Integer.valueOf(3), config.get("c"));
    }

    @Test
    public void testConfigEnvOverrides_Alias() {
        BQRuntime runtime = testFactory.app("--config=src/test/resources/io/bootique/test3.yml").declareVar("a", "V1").declareVar("c.m.f", "V2").declareVar("c.m.k", "V3").var("V1", "K").var("V2", "K1").var("V3", "4").createRuntime();
        Bootique_ConfigurationIT.Bean1 b1 = runtime.getInstance(ConfigurationFactory.class).config(Bootique_ConfigurationIT.Bean1.class, "");
        Assert.assertEquals("K", b1.a);
        Assert.assertEquals(4, b1.c.m.k);
        Assert.assertEquals("n", b1.c.m.l);
        Assert.assertEquals("K1", b1.c.m.f);
    }

    static class Bean1 {
        private String a;

        private Bootique_ConfigurationIT.Bean2 c;

        public void setA(String a) {
            this.a = a;
        }

        public void setC(Bootique_ConfigurationIT.Bean2 c) {
            this.c = c;
        }
    }

    static class Bean2 {
        private Bootique_ConfigurationIT.Bean3 m;

        public void setM(Bootique_ConfigurationIT.Bean3 m) {
            this.m = m;
        }
    }

    static class Bean3 {
        private int k;

        private String f;

        private String l;

        public void setK(int k) {
            this.k = k;
        }

        public void setF(String f) {
            this.f = f;
        }

        public void setL(String l) {
            this.l = l;
        }
    }
}

