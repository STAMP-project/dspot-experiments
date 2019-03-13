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
package org.apache.camel.component.jgroups;


import java.util.UUID;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;


public class JGroupsClusterTest extends Assert {
    // Tested state
    String master;

    int nominationCount;

    // Routing fixtures
    String jgroupsEndpoint = String.format("jgroups:%s?enableViewMessages=true", UUID.randomUUID());

    DefaultCamelContext firstCamelContext;

    DefaultCamelContext secondCamelContext;

    class Builder extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from(jgroupsEndpoint).filter(JGroupsFilters.dropNonCoordinatorViews()).process(new Processor() {
                @Override
                public void process(org.apache.camel.Exchange exchange) throws Exception {
                    String camelContextName = exchange.getContext().getName();
                    if (!(camelContextName.equals(master))) {
                        master = camelContextName;
                        (nominationCount)++;
                    }
                }
            });
        }
    }

    // Tests
    @Test
    public void shouldElectSecondNode() throws Exception {
        // When
        firstCamelContext.start();
        String firstMaster = master;
        secondCamelContext.start();
        firstCamelContext.stop();
        String secondMaster = master;
        secondCamelContext.stop();
        // Then
        Assert.assertEquals(firstCamelContext.getName(), firstMaster);
        Assert.assertEquals(secondCamelContext.getName(), secondMaster);
        Assert.assertEquals(2, nominationCount);
    }

    @Test
    public void shouldKeepMaster() throws Exception {
        // When
        firstCamelContext.start();
        String firstMaster = master;
        secondCamelContext.start();
        secondCamelContext.stop();
        String secondMaster = master;
        firstCamelContext.stop();
        // Then
        Assert.assertEquals(firstCamelContext.getName(), firstMaster);
        Assert.assertEquals(firstCamelContext.getName(), secondMaster);
        Assert.assertEquals(1, nominationCount);
    }

    @Test
    public void shouldElectSecondNodeAndReturnToFirst() throws Exception {
        // When
        firstCamelContext.start();
        String firstMaster = master;
        secondCamelContext.start();
        firstCamelContext.stop();
        String secondMaster = master;
        firstCamelContext.start();
        String masterAfterRestartOfFirstNode = master;
        secondCamelContext.stop();
        String finalMaster = master;
        firstCamelContext.stop();
        // Then
        Assert.assertEquals(firstCamelContext.getName(), firstMaster);
        Assert.assertEquals(secondCamelContext.getName(), secondMaster);
        Assert.assertEquals(secondCamelContext.getName(), masterAfterRestartOfFirstNode);
        Assert.assertEquals(firstCamelContext.getName(), finalMaster);
        Assert.assertEquals(3, nominationCount);
    }
}

