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


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.config.ConfigurationFactory;
import io.bootique.unit.BQInternalTestFactory;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class Bootique_Configuration_VarsIT {
    @Rule
    public BQInternalTestFactory testFactory = new BQInternalTestFactory();

    @Test
    public void testOverrideValue() {
        BQRuntime runtime = app().var("MY_VAR", "myValue").declareVar("testOverrideValue.c.m.l", "MY_VAR").createRuntime();
        Bootique_Configuration_VarsIT.Bean1 b1 = runtime.getInstance(ConfigurationFactory.class).config(Bootique_Configuration_VarsIT.Bean1.class, "testOverrideValue");
        Assert.assertEquals("myValue", b1.c.m.l);
    }

    @Test
    public void testOverrideValueArray() {
        BQRuntime runtime = app().var("MY_VAR", "J").declareVar("testOverrideValueArray.h[1]", "MY_VAR").createRuntime();
        Bootique_Configuration_VarsIT.TestOverrideValueArrayBean b = runtime.getInstance(ConfigurationFactory.class).config(Bootique_Configuration_VarsIT.TestOverrideValueArrayBean.class, "testOverrideValueArray");
        Assert.assertEquals("i", b.h.get(0));
        Assert.assertEquals("J", b.h.get(1));
        Assert.assertEquals("k", b.h.get(2));
    }

    @Test
    public void testDeclareVar_ConfigPathCaseSensitivity() {
        BQRuntime runtime = testFactory.app().declareVar("m.propCamelCase", "MY_VAR").var("MY_VAR", "myValue").createRuntime();
        Bootique_Configuration_VarsIT.Bean4 b4 = runtime.getInstance(ConfigurationFactory.class).config(Bootique_Configuration_VarsIT.Bean4.class, "");
        Assert.assertNotNull("Map did not resolve", b4.m);
        Assert.assertEquals(("Unexpected map contents: " + (b4.m)), "myValue", b4.m.get("propCamelCase"));
    }

    @Test
    public void testDeclareVar_NameCaseSensitivity() {
        BQRuntime runtime = testFactory.app().declareVar("m.propCamelCase", "MY_VAR").var("my_var", "myValue").createRuntime();
        Bootique_Configuration_VarsIT.Bean4 b4 = runtime.getInstance(ConfigurationFactory.class).config(Bootique_Configuration_VarsIT.Bean4.class, "");
        Assert.assertNull(b4.m);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Bean1 {
        private String a;

        private Bootique_Configuration_VarsIT.Bean2 c;

        public void setA(String a) {
            this.a = a;
        }

        public void setC(Bootique_Configuration_VarsIT.Bean2 c) {
            this.c = c;
        }
    }

    static class Bean2 {
        private Bootique_Configuration_VarsIT.Bean3 m;

        public void setM(Bootique_Configuration_VarsIT.Bean3 m) {
            this.m = m;
        }
    }

    static class Bean3 {
        private String k;

        private String f;

        private String l;

        public void setK(String k) {
            this.k = k;
        }

        public void setF(String f) {
            this.f = f;
        }

        public void setL(String l) {
            this.l = l;
        }
    }

    @BQConfig
    static class Bean4 {
        private Map<String, String> m;

        @BQConfigProperty
        public void setM(Map<String, String> m) {
            this.m = m;
        }
    }

    static class TestOverrideValueArrayBean {
        private List<String> h;

        public void setH(List<String> h) {
            this.h = h;
        }
    }
}

