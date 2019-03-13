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
package org.apache.camel.maven;


import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.camel.component.salesforce.internal.client.RestClient;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;


public class AbstractSalesforceMojoIntegrationTest {
    private static final Map<String, List<String>> NO_HEADERS = Collections.emptyMap();

    private static final String TEST_LOGIN_PROPERTIES = "../test-salesforce-login.properties";

    @Test
    public void shouldLoginAndProvideRestClient() throws IOException, MojoExecutionException, MojoFailureException {
        final AbstractSalesforceMojo mojo = new AbstractSalesforceMojo() {
            @Override
            protected void executeWithClient(final RestClient client) throws MojoExecutionException {
                assertThat(client).isNotNull();
                client.getGlobalObjects(AbstractSalesforceMojoIntegrationTest.NO_HEADERS, ( response, headers, exception) -> {
                    assertThat(exception).isNull();
                });
            }
        };
        AbstractSalesforceMojoIntegrationTest.setup(mojo);
        mojo.execute();
    }
}

