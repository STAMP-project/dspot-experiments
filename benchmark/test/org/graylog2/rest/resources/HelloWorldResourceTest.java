/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.rest.resources;


import HttpConfiguration.PATH_WEB;
import javax.ws.rs.core.Response;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.system.NodeId;
import org.graylog2.rest.models.HelloWorldResponse;
import org.junit.Test;


public class HelloWorldResourceTest extends RestResourceBaseTest {
    private static final String CK_CLUSTER_ID = "dummyclusterid";

    private static final String CK_NODE_ID = "dummynodeid";

    private HelloWorldResource helloWorldResource;

    private NodeId nodeId;

    private ClusterConfigService clusterConfigService;

    @Test
    public void rootResourceShouldReturnGeneralStats() throws Exception {
        final HelloWorldResponse helloWorldResponse = this.helloWorldResource.helloWorld();
        assertThat(helloWorldResponse).isNotNull();
        assertThat(helloWorldResponse.clusterId()).isEqualTo(HelloWorldResourceTest.CK_CLUSTER_ID);
        assertThat(helloWorldResponse.nodeId()).isEqualTo(HelloWorldResourceTest.CK_NODE_ID);
    }

    @Test
    public void rootResourceShouldRedirectToWebInterfaceIfHtmlIsRequested() throws Exception {
        final Response response = helloWorldResource.redirectToWebConsole();
        assertThat(response).isNotNull();
        final String locationHeader = response.getHeaderString("Location");
        assertThat(locationHeader).isNotNull().isEqualTo(PATH_WEB);
    }
}

