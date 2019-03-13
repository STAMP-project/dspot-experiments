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
package org.graylog.plugins.sidecar.collectors.rest;


import java.util.List;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.graylog.plugins.sidecar.collectors.rest.assertj.ResponseAssert;
import org.graylog.plugins.sidecar.collectors.rest.resources.RestResourceBaseTest;
import org.graylog.plugins.sidecar.filter.ActiveSidecarFilter;
import org.graylog.plugins.sidecar.mapper.SidecarStatusMapper;
import org.graylog.plugins.sidecar.rest.models.NodeDetails;
import org.graylog.plugins.sidecar.rest.models.Sidecar;
import org.graylog.plugins.sidecar.rest.models.SidecarSummary;
import org.graylog.plugins.sidecar.rest.requests.RegistrationRequest;
import org.graylog.plugins.sidecar.rest.resources.SidecarResource;
import org.graylog.plugins.sidecar.services.ActionService;
import org.graylog.plugins.sidecar.services.SidecarService;
import org.graylog.plugins.sidecar.system.SidecarConfiguration;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SidecarResourceTest extends RestResourceBaseTest {
    private SidecarResource resource;

    private List<Sidecar> sidecars;

    @Mock
    private SidecarService sidecarService;

    @Mock
    private ActionService actionService;

    @Mock
    private SidecarStatusMapper statusMapper;

    @Mock
    private ClusterConfigService clusterConfigService;

    @Mock
    private SidecarConfiguration sidecarConfiguration;

    @Test(expected = NotFoundException.class)
    public void testGetNotExisting() throws Exception {
        final SidecarSummary response = this.resource.get("Nonexisting");
        Assert.assertNull(response);
    }

    @Test
    public void testGet() throws Exception {
        final Sidecar sidecar = sidecars.get(((sidecars.size()) - 1));
        Mockito.when(sidecarService.findByNodeId(sidecar.nodeId())).thenReturn(sidecar);
        final SidecarSummary sidecarSummary = Mockito.mock(SidecarSummary.class);
        Mockito.when(sidecar.toSummary(ArgumentMatchers.any(ActiveSidecarFilter.class))).thenReturn(sidecarSummary);
        final SidecarSummary response = this.resource.get(sidecar.nodeId());
        Assert.assertNotNull(response);
        Assert.assertEquals(sidecarSummary, response);
    }

    @Test
    public void testRegister() throws Exception {
        final RegistrationRequest input = RegistrationRequest.create("nodeName", NodeDetails.create("DummyOS 1.0", null, null, null, null));
        final Response response = this.resource.register("sidecarId", input, "0.0.1");
        ResponseAssert.assertThat(response).isSuccess();
    }
}

