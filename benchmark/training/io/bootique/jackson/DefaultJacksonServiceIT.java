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
package io.bootique.jackson;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bootique.config.PolymorphicConfiguration;
import io.bootique.config.TypesFactory;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;


public class DefaultJacksonServiceIT {
    private TypesFactory<PolymorphicConfiguration> typesFactory;

    @Test
    public void testNewObjectMapper_Inheritance() throws IOException {
        ObjectMapper mapper = newObjectMapper();
        DefaultJacksonServiceIT.Sup1 su1 = readValue(DefaultJacksonServiceIT.Sup1.class, mapper, "{\"type\":\"sub1\",\"p1\":\"p1111\"}");
        Assert.assertTrue((su1 instanceof DefaultJacksonServiceIT.Sub1));
        Assert.assertEquals("p1111", ((DefaultJacksonServiceIT.Sub1) (su1)).getP1());
        DefaultJacksonServiceIT.Sup1 su2 = readValue(DefaultJacksonServiceIT.Sup1.class, mapper, "{\"type\":\"sub2\",\"p2\":15}");
        Assert.assertTrue((su2 instanceof DefaultJacksonServiceIT.Sub2));
        Assert.assertEquals(15, ((DefaultJacksonServiceIT.Sub2) (su2)).getP2());
        DefaultJacksonServiceIT.Sup1 su22 = readValue(DefaultJacksonServiceIT.Sup1.class, mapper, "{\"p2\":18}");
        Assert.assertTrue((su22 instanceof DefaultJacksonServiceIT.Sub2));
        Assert.assertEquals(18, ((DefaultJacksonServiceIT.Sub2) (su22)).getP2());
        DefaultJacksonServiceIT.Sup2 su3 = readValue(DefaultJacksonServiceIT.Sup2.class, mapper, "{\"type\":\"sub3\",\"p3\":\"pxxxx\"}");
        Assert.assertTrue((su3 instanceof DefaultJacksonServiceIT.Sub3));
        Assert.assertEquals("pxxxx", ((DefaultJacksonServiceIT.Sub3) (su3)).getP3());
        DefaultJacksonServiceIT.Sup2 su4 = readValue(DefaultJacksonServiceIT.Sup2.class, mapper, "{\"type\":\"sub4\",\"p4\":150}");
        Assert.assertTrue((su4 instanceof DefaultJacksonServiceIT.Sub4));
        Assert.assertEquals(150, ((DefaultJacksonServiceIT.Sub4) (su4)).getP4());
    }

    @JsonTypeInfo(use = NAME, property = "type", defaultImpl = DefaultJacksonServiceIT.Sub2.class)
    public static interface Sup1 extends PolymorphicConfiguration {}

    @JsonTypeInfo(use = NAME, property = "type")
    public static interface Sup2 extends PolymorphicConfiguration {}

    @JsonTypeName("sub1")
    public static class Sub1 implements DefaultJacksonServiceIT.Sup1 {
        private String p1;

        public String getP1() {
            return p1;
        }
    }

    @JsonTypeName("sub2")
    public static class Sub2 implements DefaultJacksonServiceIT.Sup1 {
        private int p2;

        public int getP2() {
            return p2;
        }
    }

    @JsonTypeName("sub3")
    public static class Sub3 implements DefaultJacksonServiceIT.Sup2 {
        private String p3;

        public String getP3() {
            return p3;
        }
    }

    @JsonTypeName("sub4")
    public static class Sub4 implements DefaultJacksonServiceIT.Sup2 {
        private int p4;

        public int getP4() {
            return p4;
        }
    }
}

