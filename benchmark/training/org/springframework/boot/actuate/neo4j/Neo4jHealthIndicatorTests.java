/**
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.neo4j;


import Neo4jHealthIndicator.CYPHER;
import Status.DOWN;
import Status.UP;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.neo4j.ogm.exception.CypherException;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.boot.actuate.health.Health;


/**
 * Tests for {@link Neo4jHealthIndicator}.
 *
 * @author Eric Spiegelberg
 * @author Stephane Nicoll
 * @author Michael Simons
 */
public class Neo4jHealthIndicatorTests {
    private Session session;

    private Neo4jHealthIndicator neo4jHealthIndicator;

    @Test
    public void neo4jUp() {
        Result result = Mockito.mock(Result.class);
        BDDMockito.given(this.session.query(CYPHER, Collections.emptyMap())).willReturn(result);
        int nodeCount = 500;
        Map<String, Object> expectedCypherDetails = new HashMap<>();
        expectedCypherDetails.put("nodes", nodeCount);
        List<Map<String, Object>> queryResults = new ArrayList<>();
        queryResults.add(expectedCypherDetails);
        BDDMockito.given(result.queryResults()).willReturn(queryResults);
        Health health = this.neo4jHealthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        Map<String, Object> details = health.getDetails();
        int nodeCountFromDetails = ((int) (details.get("nodes")));
        Assert.assertEquals(nodeCount, nodeCountFromDetails);
    }

    @Test
    public void neo4jDown() {
        CypherException cypherException = new CypherException("Neo.ClientError.Statement.SyntaxError", "Error executing Cypher");
        BDDMockito.given(this.session.query(CYPHER, Collections.emptyMap())).willThrow(cypherException);
        Health health = this.neo4jHealthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
    }
}

