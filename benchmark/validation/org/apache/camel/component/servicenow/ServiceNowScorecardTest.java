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
package org.apache.camel.component.servicenow;


import ServiceNowConstants.ACTION;
import ServiceNowConstants.ACTION_RETRIEVE;
import ServiceNowConstants.ACTION_SUBJECT;
import ServiceNowConstants.ACTION_SUBJECT_PERFORMANCE_ANALYTICS;
import ServiceNowConstants.MODEL;
import ServiceNowConstants.RESOURCE;
import ServiceNowConstants.RESOURCE_SCORECARDS;
import java.util.List;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.servicenow.model.Scorecard;
import org.junit.Test;


public class ServiceNowScorecardTest extends ServiceNowTestSupport {
    @Produce(uri = "direct:servicenow")
    ProducerTemplate template;

    @Test
    public void testScorecard() throws Exception {
        List<Scorecard> scorecardList = template.requestBodyAndHeaders("direct:servicenow", null, ServiceNowTestSupport.kvBuilder().put(RESOURCE, RESOURCE_SCORECARDS).put(ACTION, ACTION_RETRIEVE).put(ACTION_SUBJECT, ACTION_SUBJECT_PERFORMANCE_ANALYTICS).put(MODEL, Scorecard.class).build(), List.class);
        assertFalse(scorecardList.isEmpty());
    }
}

