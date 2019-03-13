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
package org.apache.camel.itest;


import java.net.URL;
import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.karaf.AbstractFeatureTest;
import org.apache.camel.util.ObjectHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * CAMEL-11471: Unable to update the cron details from Quartz scheduler MBean
 */
@RunWith(PaxExam.class)
public class CamelQuartz2JmxUpdateTest extends AbstractFeatureTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelQuartz2JmxUpdateTest.class);

    @Test
    public void testUpdateCronDetails() throws Exception {
        // install camel-quartz2 here as 'wrap:' is not available at boot time
        installCamelFeature("camel-quartz2");
        // install the camel blueprint xml file we use in this test
        URL url = ObjectHelper.loadResourceAsURL("org/apache/camel/itest/CamelQuartz2JmxUpdateTest.xml", CamelQuartz2JmxUpdateTest.class.getClassLoader());
        installBlueprintAsBundle("CamelQuartz2JmxUpdateTest", url, true);
        // lookup Camel from OSGi
        CamelContext camel = getOsgiService(bundleContext, CamelContext.class);
        // test camel
        MockEndpoint mock = camel.getEndpoint("mock:result", MockEndpoint.class);
        mock.expectedBodiesReceived("Hello World");
        mock.assertIsSatisfied(5000);
        doUpdateCronDetails();
    }
}

