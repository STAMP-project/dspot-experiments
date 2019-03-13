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


public class ManagedFromRestGetEmbeddedRouteTest extends ManagementTestSupport {
    @Test
    public void testFromRestModel() throws Exception {
        // JMX tests dont work well on AIX CI servers (hangs them)
        if (isPlatform("aix")) {
            return;
        }
        MBeanServer mbeanServer = getMBeanServer();
        ObjectName on = ObjectName.getInstance("org.apache.camel:context=camel-1,type=context,name=\"camel-1\"");
        String xml = ((String) (mbeanServer.invoke(on, "dumpRestsAsXml", null, null)));
        assertNotNull(xml);
        log.info(xml);
        assertTrue(xml.contains("<rests"));
        assertTrue(xml.contains("<rest path=\"/say/hello\">"));
        assertTrue(xml.contains("<rest path=\"/say/bye\">"));
        assertTrue(xml.contains("</rest>"));
        assertTrue(xml.contains("<get"));
        assertTrue(xml.contains("<route"));
        assertTrue(xml.contains("<transform"));
        assertTrue(xml.contains("<post"));
        assertTrue(xml.contains("application/json"));
        assertTrue(xml.contains("</rests>"));
        String xml2 = ((String) (mbeanServer.invoke(on, "dumpRoutesAsXml", null, null)));
        log.info(xml2);
        // and we should have rest in the routes that indicate its from a rest dsl
        assertTrue(xml2.contains("rest=\"true\""));
        // there should be 2 + 1 routes
        assertEquals((2 + 1), context.getRouteDefinitions().size());
    }
}

