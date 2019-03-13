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
package io.bootique.config.jackson;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.resource.ResourceFactory;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.junit.Assert;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;


public class JsonNodeConfigurationFactoryTest {
    @Test
    public void testConfig() {
        JsonNodeConfigurationFactoryTest.Bean1 b1 = factory("s: SS\ni: 55").config(JsonNodeConfigurationFactoryTest.Bean1.class, "");
        Assert.assertNotNull(b1);
        Assert.assertEquals("SS", b1.getS());
        Assert.assertEquals(55, b1.getI());
    }

    @Test
    public void testConfig_Nested() {
        JsonNodeConfigurationFactoryTest.Bean2 b2 = factory("b1:\n  s: SS\n  i: 55").config(JsonNodeConfigurationFactoryTest.Bean2.class, "");
        Assert.assertNotNull(b2);
        Assert.assertNotNull(b2.getB1());
        Assert.assertEquals("SS", b2.getB1().getS());
        Assert.assertEquals(55, b2.getB1().getI());
    }

    @Test
    public void testConfig_Subconfig() {
        JsonNodeConfigurationFactoryTest.Bean1 b1 = factory("b1:\n  s: SS\n  i: 55").config(JsonNodeConfigurationFactoryTest.Bean1.class, "b1");
        Assert.assertNotNull(b1);
        Assert.assertEquals("SS", b1.getS());
        Assert.assertEquals(55, b1.getI());
    }

    @Test
    public void testConfig_Subconfig_MultiLevel() {
        JsonNodeConfigurationFactoryTest.Bean1 b1 = factory("b0:\n  b1:\n    s: SS\n    i: 55").config(JsonNodeConfigurationFactoryTest.Bean1.class, "b0.b1");
        Assert.assertNotNull(b1);
        Assert.assertEquals("SS", b1.getS());
        Assert.assertEquals(55, b1.getI());
    }

    @Test
    public void testConfig_Subconfig_Missing() {
        JsonNodeConfigurationFactoryTest.Bean1 b1 = factory("b1:\n  s: SS\n  i: 55").config(JsonNodeConfigurationFactoryTest.Bean1.class, "no.such.path");
        Assert.assertNotNull(b1);
        Assert.assertEquals(null, b1.getS());
        Assert.assertEquals(0, b1.getI());
    }

    @Test
    public void testList_SingleLevel() {
        List<Object> l = factory("- SS\n- 55").config(new io.bootique.type.TypeRef<List<Object>>() {}, "");
        Assert.assertNotNull(l);
        Assert.assertEquals("SS", l.get(0));
        Assert.assertEquals(55, l.get(1));
    }

    @Test
    public void testList_MultiLevel() {
        List<List<Object>> l = factory("-\n  - SS\n  - 55\n-\n  - X").config(new io.bootique.type.TypeRef<List<List<Object>>>() {}, "");
        Assert.assertNotNull(l);
        Assert.assertEquals(2, l.size());
        List<Object> sl1 = l.get(0);
        Assert.assertEquals(2, sl1.size());
        Assert.assertEquals("SS", sl1.get(0));
        Assert.assertEquals(55, sl1.get(1));
        List<Object> sl2 = l.get(1);
        Assert.assertEquals(1, sl2.size());
        Assert.assertEquals("X", sl2.get(0));
    }

    @Test
    public void testMap_SingleLevel() {
        Map<String, Object> m = factory("b1: SS\ni: 55").config(new io.bootique.type.TypeRef<Map<String, Object>>() {}, "");
        Assert.assertNotNull(m);
        Assert.assertEquals("SS", m.get("b1"));
        Assert.assertEquals(55, m.get("i"));
    }

    @Test
    public void testMap_MultiLevel() {
        Map<String, Map<String, Object>> m = factory("b1:\n  k1: SS\n  i: 55").config(new io.bootique.type.TypeRef<Map<String, Map<String, Object>>>() {}, "");
        Assert.assertNotNull(m);
        Map<String, Object> subM = m.get("b1");
        Assert.assertNotNull(subM);
        Assert.assertEquals("SS", subM.get("k1"));
        Assert.assertEquals(55, subM.get("i"));
    }

    @Test
    public void testConfig_Polimorphic_Super() {
        JsonNodeConfigurationFactoryTest.BeanSuper b1 = factory("type: sup1").config(JsonNodeConfigurationFactoryTest.BeanSuper.class, "");
        Assert.assertEquals(JsonNodeConfigurationFactoryTest.BeanSuper.class, b1.getClass());
    }

    @Test
    public void testConfig_Polimorphic_Sub1() {
        JsonNodeConfigurationFactoryTest.BeanSuper b1 = factory("type: sub1\np1: p111").config(JsonNodeConfigurationFactoryTest.BeanSuper.class, "");
        Assert.assertEquals(JsonNodeConfigurationFactoryTest.BeanSub1.class, b1.getClass());
        Assert.assertEquals("p111", ((JsonNodeConfigurationFactoryTest.BeanSub1) (b1)).getP1());
    }

    @Test
    public void testConfig_Polimorphic_Sub2() {
        JsonNodeConfigurationFactoryTest.BeanSuper b1 = factory("type: sub2\np2: p222").config(JsonNodeConfigurationFactoryTest.BeanSuper.class, "");
        Assert.assertEquals(JsonNodeConfigurationFactoryTest.BeanSub2.class, b1.getClass());
        Assert.assertEquals("p222", ((JsonNodeConfigurationFactoryTest.BeanSub2) (b1)).getP2());
    }

    @Test
    public void testConfig_ResourceFactory() throws IOException {
        JsonNodeConfigurationFactoryTest.ResourceFactoryHolder rfh = factory("resourceFactory: classpath:io/bootique/config/resourcefactory.txt").config(JsonNodeConfigurationFactoryTest.ResourceFactoryHolder.class, "");
        Assert.assertNotNull(rfh);
        Assert.assertNotNull(rfh.resourceFactory);
        try (Scanner scanner = new Scanner(rfh.resourceFactory.getUrl().openStream(), "UTF-8")) {
            Assert.assertEquals("resource factory worked!", scanner.useDelimiter("\\Z").nextLine());
        }
    }

    public static class Bean1 {
        private String s;

        private int i;

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }

    public static class Bean2 {
        private JsonNodeConfigurationFactoryTest.Bean1 b1;

        public JsonNodeConfigurationFactoryTest.Bean1 getB1() {
            return b1;
        }

        public void setB1(JsonNodeConfigurationFactoryTest.Bean1 b1) {
            this.b1 = b1;
        }
    }

    @JsonTypeInfo(use = NAME, property = "type")
    @JsonTypeName("sup1")
    @JsonSubTypes({ @JsonSubTypes.Type(JsonNodeConfigurationFactoryTest.BeanSub1.class), @JsonSubTypes.Type(JsonNodeConfigurationFactoryTest.BeanSub2.class) })
    public static class BeanSuper {}

    @JsonTypeName("sub1")
    public static class BeanSub1 extends JsonNodeConfigurationFactoryTest.BeanSuper {
        private String p1;

        public String getP1() {
            return p1;
        }

        public void setP1(String p1) {
            this.p1 = p1;
        }
    }

    @JsonTypeName("sub2")
    public static class BeanSub2 extends JsonNodeConfigurationFactoryTest.BeanSuper {
        private String p2;

        public void setP1(String p2) {
            this.p2 = p2;
        }

        public String getP2() {
            return p2;
        }
    }

    public static class ResourceFactoryHolder {
        private ResourceFactory resourceFactory;

        public void setResourceFactory(ResourceFactory resourceFactory) {
            this.resourceFactory = resourceFactory;
        }
    }
}

