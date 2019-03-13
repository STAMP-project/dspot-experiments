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
package org.apache.camel.itest.cdi;


import javax.inject.Inject;
import org.apache.camel.CamelContext;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Verify {@link CdiCamelExtension} with custom properties.
 */
@RunWith(Arquillian.class)
public class PropertiesConfigurationTest {
    @Inject
    private CamelContext camelContext;

    @Test
    public void checkContext() throws Exception {
        Assert.assertNotNull(camelContext);
        Assert.assertEquals("value1", camelContext.resolvePropertyPlaceholders("{{property1}}"));
        Assert.assertEquals("value2", camelContext.resolvePropertyPlaceholders("{{property2}}"));
        Assert.assertEquals("value1_value2", camelContext.resolvePropertyPlaceholders("{{property1}}_{{property2}}"));
    }
}

