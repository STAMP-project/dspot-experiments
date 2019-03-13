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
import SolrConstants.OPERATION_DELETE_BY_ID;
import SolrConstants.OPERATION_DELETE_BY_QUERY;
import java.util.Arrays;
import org.junit.Test;


public class SolrDeleteTest extends SolrComponentTestSupport {
    public SolrDeleteTest(SolrFixtures.TestServerType serverToTest) {
        super(serverToTest);
    }

    @Test
    public void testDeleteById() throws Exception {
        // insert, commit and verify
        solrInsertTestEntry();
        solrCommit();
        assertEquals("wrong number of entries found", 1, executeSolrQuery(("id:" + (SolrComponentTestSupport.TEST_ID))).getResults().getNumFound());
        // delete
        template.sendBodyAndHeader("direct:start", SolrComponentTestSupport.TEST_ID, OPERATION, OPERATION_DELETE_BY_ID);
        solrCommit();
        // verify
        assertEquals("wrong number of entries found", 0, executeSolrQuery(("id:" + (SolrComponentTestSupport.TEST_ID))).getResults().getNumFound());
    }

    @Test
    public void testDeleteListOfIDs() throws Exception {
        // insert, commit and verify
        solrInsertTestEntry(SolrComponentTestSupport.TEST_ID);
        solrInsertTestEntry(SolrComponentTestSupport.TEST_ID2);
        solrCommit();
        assertEquals("wrong number of entries found", 2, executeSolrQuery("id:test*").getResults().getNumFound());
        // delete
        template.sendBodyAndHeader("direct:splitThenCommit", Arrays.asList(SolrComponentTestSupport.TEST_ID, SolrComponentTestSupport.TEST_ID2), OPERATION, OPERATION_DELETE_BY_ID);
        // verify
        assertEquals("wrong number of entries found", 0, executeSolrQuery("id:test*").getResults().getNumFound());
    }

    @Test
    public void testDeleteByQuery() throws Exception {
        // insert, commit and verify
        solrInsertTestEntry(SolrComponentTestSupport.TEST_ID);
        solrInsertTestEntry(SolrComponentTestSupport.TEST_ID2);
        solrCommit();
        assertEquals("wrong number of entries found", 2, executeSolrQuery("id:test*").getResults().getNumFound());
        // delete
        template.sendBodyAndHeader("direct:start", "id:test*", OPERATION, OPERATION_DELETE_BY_QUERY);
        solrCommit();
        // verify
        assertEquals("wrong number of entries found", 0, executeSolrQuery("id:test*").getResults().getNumFound());
    }
}

