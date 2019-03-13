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
package org.apache.camel.component.solr;


import SolrConstants.OPERATION;
import SolrConstants.OPERATION_ADD_BEANS;
import SolrConstants.OPERATION_COMMIT;
import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.beans.Field;
import org.junit.Test;


public class SolrAddBeansTest extends SolrComponentTestSupport {
    public SolrAddBeansTest(SolrFixtures.TestServerType serverToTest) {
        super(serverToTest);
    }

    @Test
    public void testAddBeans() throws Exception {
        List<SolrAddBeansTest.Item> beans = new ArrayList<>();
        // add bean1
        SolrAddBeansTest.Item item1 = new SolrAddBeansTest.Item();
        item1.id = SolrComponentTestSupport.TEST_ID;
        item1.categories = new String[]{ "aaa", "bbb", "ccc" };
        beans.add(item1);
        // add bean2
        SolrAddBeansTest.Item item2 = new SolrAddBeansTest.Item();
        item2.id = SolrComponentTestSupport.TEST_ID2;
        item2.categories = new String[]{ "aaa", "bbb", "ccc" };
        beans.add(item2);
        template.sendBodyAndHeader("direct:start", beans, OPERATION, OPERATION_ADD_BEANS);
        template.sendBodyAndHeader("direct:start", null, OPERATION, OPERATION_COMMIT);
        // verify
        assertEquals("wrong number of entries found", 1, executeSolrQuery(("id:" + (SolrComponentTestSupport.TEST_ID))).getResults().getNumFound());
        assertEquals("wrong number of entries found", 1, executeSolrQuery(("id:" + (SolrComponentTestSupport.TEST_ID2))).getResults().getNumFound());
        assertEquals("wrong number of entries found", 2, executeSolrQuery("*:*").getResults().getNumFound());
    }

    public class Item {
        @Field
        String id;

        @Field("cat")
        String[] categories;
    }
}

