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
package org.apache.camel.processor.aggregator;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class AlbertoAggregatorTest extends ContextTestSupport {
    private static final String SURNAME_HEADER = "surname";

    private static final String TYPE_HEADER = "type";

    private static final String BROTHERS_TYPE = "brothers";

    @Test
    public void testAggregator() throws Exception {
        String allNames = "Harpo Marx,Fiodor Karamazov,Chico Marx,Ivan Karamazov,Groucho Marx,Alexei Karamazov,Dimitri Karamazov";
        List<String> marxBrothers = new ArrayList<>();
        marxBrothers.add("Harpo");
        marxBrothers.add("Chico");
        marxBrothers.add("Groucho");
        List<String> karamazovBrothers = new ArrayList<>();
        karamazovBrothers.add("Fiodor");
        karamazovBrothers.add("Ivan");
        karamazovBrothers.add("Alexei");
        karamazovBrothers.add("Dimitri");
        Map<String, List<String>> allBrothers = new HashMap<>();
        allBrothers.put("Marx", marxBrothers);
        allBrothers.put("Karamazov", karamazovBrothers);
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedBodiesReceived(allBrothers);
        template.sendBody("direct:start", allNames);
        assertMockEndpointsSatisfied();
    }
}

