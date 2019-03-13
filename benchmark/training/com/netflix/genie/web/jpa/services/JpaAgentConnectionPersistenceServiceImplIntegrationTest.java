/**
 * Copyright 2017 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.genie.web.jpa.services;


import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.netflix.genie.test.categories.IntegrationTest;
import com.netflix.genie.web.services.AgentConnectionPersistenceService;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Integration tests for the JpaFilePersistenceServiceImpl class.
 *
 * @author tgianos
 * @since 3.3.0
 */
@Category(IntegrationTest.class)
@DatabaseTearDown("cleanup.xml")
public class JpaAgentConnectionPersistenceServiceImplIntegrationTest extends DBIntegrationTestBase {
    private static final String JOB1 = "job1";

    private static final String HOST1 = "host1";

    private static final String JOB2 = "job2";

    private static final String HOST2 = "host2";

    // This needs to be injected as a Spring Bean otherwise transactions don't work as there is no proxy
    @Autowired
    private AgentConnectionPersistenceService agentConnectionPersistenceService;

    /**
     * Perform assorted operations on persisted connections.
     */
    @Test
    public void createUpdateDelete() {
        // Check empty
        verifyExpectedConnections();
        Assert.assertThat(this.agentConnectionRepository.count(), Matchers.is(0L));
        // Create two connections for two jobs on different servers
        this.agentConnectionPersistenceService.saveAgentConnection(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST1);
        this.agentConnectionPersistenceService.saveAgentConnection(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB2, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST2);
        verifyExpectedConnections(Pair.of(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST1), Pair.of(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB2, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST2));
        // Migrate a connection, with delete before update
        this.agentConnectionPersistenceService.removeAgentConnection(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST1);
        this.agentConnectionPersistenceService.saveAgentConnection(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST2);
        verifyExpectedConnections(Pair.of(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST2), Pair.of(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB2, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST2));
        // Migrate a connection with update before delete
        this.agentConnectionPersistenceService.saveAgentConnection(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST1);
        this.agentConnectionPersistenceService.removeAgentConnection(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST2);
        verifyExpectedConnections(Pair.of(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST1), Pair.of(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB2, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST2));
        // Migrate a connection, landing on the same server with deletion
        this.agentConnectionPersistenceService.removeAgentConnection(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST1);
        this.agentConnectionPersistenceService.saveAgentConnection(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST1);
        verifyExpectedConnections(Pair.of(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST1), Pair.of(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB2, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST2));
        // Migrate a connection, landing on the same server without deletion (unexpected in practice)
        this.agentConnectionPersistenceService.saveAgentConnection(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST1);
        this.agentConnectionPersistenceService.saveAgentConnection(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST1);
        verifyExpectedConnections(Pair.of(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST1), Pair.of(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB2, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST2));
        // Delete all
        this.agentConnectionPersistenceService.removeAgentConnection(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB1, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST1);
        this.agentConnectionPersistenceService.removeAgentConnection(JpaAgentConnectionPersistenceServiceImplIntegrationTest.JOB2, JpaAgentConnectionPersistenceServiceImplIntegrationTest.HOST2);
        verifyExpectedConnections();
    }
}

