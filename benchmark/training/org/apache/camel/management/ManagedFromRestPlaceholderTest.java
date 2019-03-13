/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.management;


import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.junit.Test;


public class ManagedFromRestPlaceholderTest extends ManagementTestSupport {
    @Test
    public void testFromRestModelPlaceholder() throws Exception {
        // JMX tests dont work well on AIX CI servers (hangs them)
        if (isPlatform("aix")) {
            return;
        }
        MBeanServer mbeanServer = getMBeanServer();
        ObjectName on = ObjectName.getInstance("org.apache.camel:context=camel-1,type=context,name=\"camel-1\"");
        String xml = ((String) (mbeanServer.invoke(on, "dumpRestsAsXml", new Object[]{ true }, new String[]{ "boolean" })));
        assertNotNull(xml);
        log.info(xml);
        assertTrue(xml.contains("<rests"));
        assertTrue(xml.contains("<rest path=\"/say/hello\">"));
        assertTrue(xml.contains("<rest path=\"/say/bye\">"));
        assertTrue(xml.contains("</rest>"));
        assertTrue(xml.contains("<get"));
        assertTrue(xml.contains("application/json"));
        assertTrue(xml.contains("<post"));
        assertTrue(xml.contains("application/json"));
        assertTrue(xml.contains("</rests>"));
        assertTrue(xml.contains(("<param collectionFormat=\"multi\" dataType=\"string\" defaultValue=\"b\" description=\"header param description2\" " + "name=\"header_letter\" required=\"false\" type=\"query\">")));
        assertTrue(xml.contains(("<param dataType=\"integer\" defaultValue=\"1\" description=\"header param description1\" " + "name=\"header_count\" required=\"true\" type=\"header\">")));
        assertTrue(xml.contains("<value>1</value>"));
        assertTrue(xml.contains("<value>a</value>"));
        assertTrue(xml.contains("<responseMessage code=\"300\" message=\"test msg\" responseModel=\"java.lang.Integer\"/>"));
        String xml2 = ((String) (mbeanServer.invoke(on, "dumpRoutesAsXml", null, null)));
        log.info(xml2);
        // and we should have rest in the routes that indicate its from a rest dsl
        assertTrue(xml2.contains("rest=\"true\""));
        // there should be 3 + 2 routes
        assertEquals((3 + 2), context.getRouteDefinitions().size());
    }
}

