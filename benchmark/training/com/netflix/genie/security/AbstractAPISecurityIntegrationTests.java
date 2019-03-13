/**
 * Copyright 2016 Netflix, Inc.
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
package com.netflix.genie.security;


import com.netflix.genie.common.dto.Application;
import com.netflix.genie.common.dto.Cluster;
import com.netflix.genie.common.dto.Command;
import com.netflix.genie.web.jpa.repositories.JpaApplicationRepository;
import com.netflix.genie.web.jpa.repositories.JpaClusterRepository;
import com.netflix.genie.web.jpa.repositories.JpaCommandRepository;
import com.netflix.genie.web.jpa.repositories.JpaFileRepository;
import com.netflix.genie.web.jpa.repositories.JpaJobRepository;
import com.netflix.genie.web.jpa.repositories.JpaTagRepository;
import java.util.UUID;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;


/**
 * Shared tests for accessing API resources. Any API configuration integration tests should extend this for consistent
 * behavior.
 *
 * @author tgianos
 * @since 3.0.0
 */
public abstract class AbstractAPISecurityIntegrationTests {
    private static final Application APPLICATION = withId(UUID.randomUUID().toString()).build();

    private static final Cluster CLUSTER = withId(UUID.randomUUID().toString()).build();

    private static final Command COMMAND = withId(UUID.randomUUID().toString()).build();

    private static final String APPLICATIONS_API = "/api/v3/applications";

    private static final String CLUSTERS_API = "/api/v3/clusters";

    private static final String COMMANDS_API = "/api/v3/commands";

    private static final String JOBS_API = "/api/v3/jobs";

    private static final ResultMatcher OK = MockMvcResultMatchers.status().isOk();

    private static final ResultMatcher BAD_REQUEST = MockMvcResultMatchers.status().isBadRequest();

    private static final ResultMatcher CREATED = MockMvcResultMatchers.status().isCreated();

    private static final ResultMatcher NO_CONTENT = MockMvcResultMatchers.status().isNoContent();

    private static final ResultMatcher NOT_FOUND = MockMvcResultMatchers.status().isNotFound();

    private static final ResultMatcher FORBIDDEN = MockMvcResultMatchers.status().isForbidden();

    private static final ResultMatcher UNAUTHORIZED = MockMvcResultMatchers.status().isUnauthorized();

    @Autowired
    private WebEndpointProperties endpointProperties;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JpaApplicationRepository applicationRepository;

    @Autowired
    private JpaClusterRepository clusterRepository;

    @Autowired
    private JpaCommandRepository commandRepository;

    @Autowired
    private JpaJobRepository jobRepository;

    @Autowired
    private JpaFileRepository fileRepository;

    @Autowired
    private JpaTagRepository tagRepository;

    private MockMvc mvc;

    /**
     * Make sure we can get root.
     *
     * @throws Exception
     * 		on any error
     */
    @Test
    public void canGetRoot() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Make sure we can't call any API if not authenticated.
     *
     * @throws Exception
     * 		on any error
     */
    @Test
    public void cantCallAnyAPIIfUnauthenticated() throws Exception {
        final ResultMatcher expectedUnauthenticatedStatus = this.getUnauthorizedExpectedStatus();
        this.get(AbstractAPISecurityIntegrationTests.APPLICATIONS_API, expectedUnauthenticatedStatus);
        this.get(AbstractAPISecurityIntegrationTests.CLUSTERS_API, expectedUnauthenticatedStatus);
        this.get(AbstractAPISecurityIntegrationTests.COMMANDS_API, expectedUnauthenticatedStatus);
        this.get(AbstractAPISecurityIntegrationTests.JOBS_API, expectedUnauthenticatedStatus);
        this.checkActuatorEndpoints(AbstractAPISecurityIntegrationTests.UNAUTHORIZED);
    }

    /**
     * Make sure we can't call anything under admin control as a regular user.
     *
     * @throws Exception
     * 		on any error
     */
    @Test
    @WithMockUser
    public void cantCallAdminAPIsAsRegularUser() throws Exception {
        this.get(AbstractAPISecurityIntegrationTests.APPLICATIONS_API, AbstractAPISecurityIntegrationTests.OK);
        this.delete(AbstractAPISecurityIntegrationTests.APPLICATIONS_API, AbstractAPISecurityIntegrationTests.FORBIDDEN);
        this.post(AbstractAPISecurityIntegrationTests.APPLICATIONS_API, AbstractAPISecurityIntegrationTests.APPLICATION, AbstractAPISecurityIntegrationTests.FORBIDDEN);
        this.get((((AbstractAPISecurityIntegrationTests.APPLICATIONS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.NOT_FOUND);
        this.put((((AbstractAPISecurityIntegrationTests.APPLICATIONS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.APPLICATION, AbstractAPISecurityIntegrationTests.FORBIDDEN);
        this.get(AbstractAPISecurityIntegrationTests.CLUSTERS_API, AbstractAPISecurityIntegrationTests.OK);
        this.delete(AbstractAPISecurityIntegrationTests.CLUSTERS_API, AbstractAPISecurityIntegrationTests.FORBIDDEN);
        this.post(AbstractAPISecurityIntegrationTests.CLUSTERS_API, AbstractAPISecurityIntegrationTests.CLUSTER, AbstractAPISecurityIntegrationTests.FORBIDDEN);
        this.get((((AbstractAPISecurityIntegrationTests.CLUSTERS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.NOT_FOUND);
        this.put((((AbstractAPISecurityIntegrationTests.CLUSTERS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.CLUSTER, AbstractAPISecurityIntegrationTests.FORBIDDEN);
        this.get(AbstractAPISecurityIntegrationTests.COMMANDS_API, AbstractAPISecurityIntegrationTests.OK);
        this.delete(AbstractAPISecurityIntegrationTests.COMMANDS_API, AbstractAPISecurityIntegrationTests.FORBIDDEN);
        this.post(AbstractAPISecurityIntegrationTests.COMMANDS_API, AbstractAPISecurityIntegrationTests.COMMAND, AbstractAPISecurityIntegrationTests.FORBIDDEN);
        this.get((((AbstractAPISecurityIntegrationTests.COMMANDS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.NOT_FOUND);
        this.put((((AbstractAPISecurityIntegrationTests.COMMANDS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.COMMAND, AbstractAPISecurityIntegrationTests.FORBIDDEN);
        this.get(AbstractAPISecurityIntegrationTests.JOBS_API, AbstractAPISecurityIntegrationTests.OK);
        this.post(AbstractAPISecurityIntegrationTests.JOBS_API, "{\"key\":\"value\"}", AbstractAPISecurityIntegrationTests.BAD_REQUEST);
        this.get((((AbstractAPISecurityIntegrationTests.JOBS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.NOT_FOUND);
        this.delete((((AbstractAPISecurityIntegrationTests.JOBS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.NOT_FOUND);
        this.checkActuatorEndpoints(AbstractAPISecurityIntegrationTests.FORBIDDEN);
    }

    /**
     * Make sure we get get anything under admin control if we're an admin.
     *
     * @throws Exception
     * 		on any error
     */
    @Test
    @WithMockUser(roles = { "USER", "ADMIN" })
    public void canCallAdminAPIsAsAdminUser() throws Exception {
        this.get(AbstractAPISecurityIntegrationTests.APPLICATIONS_API, AbstractAPISecurityIntegrationTests.OK);
        this.delete(AbstractAPISecurityIntegrationTests.APPLICATIONS_API, AbstractAPISecurityIntegrationTests.NO_CONTENT);
        this.post(AbstractAPISecurityIntegrationTests.APPLICATIONS_API, AbstractAPISecurityIntegrationTests.APPLICATION, AbstractAPISecurityIntegrationTests.CREATED);
        this.get((((AbstractAPISecurityIntegrationTests.APPLICATIONS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.NOT_FOUND);
        this.put((((AbstractAPISecurityIntegrationTests.APPLICATIONS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.APPLICATION, AbstractAPISecurityIntegrationTests.NOT_FOUND);
        this.get(AbstractAPISecurityIntegrationTests.CLUSTERS_API, AbstractAPISecurityIntegrationTests.OK);
        this.delete(AbstractAPISecurityIntegrationTests.CLUSTERS_API, AbstractAPISecurityIntegrationTests.NO_CONTENT);
        this.post(AbstractAPISecurityIntegrationTests.CLUSTERS_API, AbstractAPISecurityIntegrationTests.CLUSTER, AbstractAPISecurityIntegrationTests.CREATED);
        this.get((((AbstractAPISecurityIntegrationTests.CLUSTERS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.NOT_FOUND);
        this.put((((AbstractAPISecurityIntegrationTests.CLUSTERS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.CLUSTER, AbstractAPISecurityIntegrationTests.NOT_FOUND);
        this.get(AbstractAPISecurityIntegrationTests.COMMANDS_API, AbstractAPISecurityIntegrationTests.OK);
        this.delete(AbstractAPISecurityIntegrationTests.COMMANDS_API, AbstractAPISecurityIntegrationTests.NO_CONTENT);
        this.post(AbstractAPISecurityIntegrationTests.COMMANDS_API, AbstractAPISecurityIntegrationTests.COMMAND, AbstractAPISecurityIntegrationTests.CREATED);
        this.get((((AbstractAPISecurityIntegrationTests.COMMANDS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.NOT_FOUND);
        this.put((((AbstractAPISecurityIntegrationTests.COMMANDS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.COMMAND, AbstractAPISecurityIntegrationTests.NOT_FOUND);
        this.get(AbstractAPISecurityIntegrationTests.JOBS_API, AbstractAPISecurityIntegrationTests.OK);
        this.post(AbstractAPISecurityIntegrationTests.JOBS_API, "{\"key\":\"value\"}", AbstractAPISecurityIntegrationTests.BAD_REQUEST);
        this.get((((AbstractAPISecurityIntegrationTests.JOBS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.NOT_FOUND);
        this.delete((((AbstractAPISecurityIntegrationTests.JOBS_API) + "/") + (UUID.randomUUID().toString())), AbstractAPISecurityIntegrationTests.NOT_FOUND);
        this.checkActuatorEndpoints(AbstractAPISecurityIntegrationTests.OK);
    }
}

