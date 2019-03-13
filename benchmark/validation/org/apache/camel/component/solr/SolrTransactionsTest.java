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


import HttpSolrClient.RemoteSolrException;
import SolrConstants.OPERATION;
import SolrConstants.OPERATION_COMMIT;
import SolrConstants.OPERATION_OPTIMIZE;
import SolrConstants.OPERATION_ROLLBACK;
import org.apache.camel.CamelExecutionException;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;

import static org.apache.camel.component.solr.SolrFixtures.TestServerType.USE_CLOUD;


public class SolrTransactionsTest extends SolrComponentTestSupport {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SolrFixtures.TestServerType solrServerType;

    public SolrTransactionsTest(SolrFixtures.TestServerType solrServerType) {
        super(solrServerType);
        this.solrServerType = solrServerType;
    }

    @Test
    public void testCommit() throws Exception {
        // insert and verify
        solrInsertTestEntry();
        assertEquals("wrong number of entries found", 0, executeSolrQuery(("id:" + (SolrComponentTestSupport.TEST_ID))).getResults().getNumFound());
        // commit
        template.sendBodyAndHeader("direct:start", null, OPERATION, OPERATION_COMMIT);
        // verify exists after commit
        assertEquals("wrong number of entries found", 1, executeSolrQuery(("id:" + (SolrComponentTestSupport.TEST_ID))).getResults().getNumFound());
    }

    @Test
    public void testRollback() throws Exception {
        if ((USE_CLOUD) == (this.solrServerType)) {
            // Twisting expectations in this case as rollback is currently no
            // more supported in SolrCloud mode. See SOLR-4895
            thrown.expect(CamelExecutionException.class);
            final String expectedMessagePart = "Rollback is currently not supported in SolrCloud mode. (SOLR-4895)";
            thrown.expectCause(CoreMatchers.allOf(CoreMatchers.isA(RemoteSolrException.class), ThrowableMessageMatcher.hasMessage(CoreMatchers.containsString(expectedMessagePart))));
        }
        // insert and verify
        solrInsertTestEntry();
        assertEquals("wrong number of entries found", 0, executeSolrQuery(("id:" + (SolrComponentTestSupport.TEST_ID))).getResults().getNumFound());
        // rollback
        template.sendBodyAndHeader("direct:start", null, OPERATION, OPERATION_ROLLBACK);
        // verify after rollback
        assertEquals("wrong number of entries found", 0, executeSolrQuery(("id:" + (SolrComponentTestSupport.TEST_ID))).getResults().getNumFound());
        // commit
        template.sendBodyAndHeader("direct:start", null, OPERATION, OPERATION_COMMIT);
        // verify after commit (again)
        assertEquals("wrong number of entries found", 0, executeSolrQuery(("id:" + (SolrComponentTestSupport.TEST_ID))).getResults().getNumFound());
    }

    @Test
    public void testOptimize() throws Exception {
        // insert and verify
        solrInsertTestEntry();
        assertEquals("wrong number of entries found", 0, executeSolrQuery(("id:" + (SolrComponentTestSupport.TEST_ID))).getResults().getNumFound());
        // optimize
        template.sendBodyAndHeader("direct:start", null, OPERATION, OPERATION_OPTIMIZE);
        // verify exists after optimize
        assertEquals("wrong number of entries found", 1, executeSolrQuery(("id:" + (SolrComponentTestSupport.TEST_ID))).getResults().getNumFound());
    }
}

