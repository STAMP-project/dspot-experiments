/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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
package org.keycloak.testsuite.authz;


import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.authorization.attribute.Attributes;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class AttributeTest {
    @Test
    public void testManageAttributes() throws ParseException {
        Map<String, Collection<String>> map = new HashedMap();
        map.put("integer", Arrays.asList("1"));
        map.put("long", Arrays.asList(("" + (Long.MAX_VALUE))));
        map.put("string", Arrays.asList("some string"));
        map.put("date", Arrays.asList("12/12/2016"));
        map.put("ip_network_address", Arrays.asList("127.0.0.1"));
        map.put("host_network_address", Arrays.asList("localhost"));
        map.put("multi_valued", Arrays.asList("1", "2", "3", "4"));
        Attributes attributes = Attributes.from(map);
        map.keySet().forEach(new Consumer<String>() {
            @Override
            public void accept(String name) {
                Assert.assertTrue(attributes.exists(name));
            }
        });
        Assert.assertFalse(attributes.exists("not_found"));
        Assert.assertTrue(attributes.containsValue("integer", "1"));
        Assert.assertTrue(attributes.containsValue("multi_valued", "3"));
        Assert.assertEquals(1, attributes.getValue("multi_valued").asInt(0));
        Assert.assertEquals(4, attributes.getValue("multi_valued").asInt(3));
        Assert.assertEquals(new SimpleDateFormat("dd/MM/yyyy").parse("12/12/2016"), attributes.getValue("date").asDate(0, "dd/MM/yyyy"));
        Assert.assertEquals(InetAddress.getLoopbackAddress(), attributes.getValue("ip_network_address").asInetAddress(0));
        Assert.assertEquals(InetAddress.getLoopbackAddress(), attributes.getValue("host_network_address").asInetAddress(0));
    }
}

