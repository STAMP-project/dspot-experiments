/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc.dispatch;


import GwcServiceDispatcherCallback.GWC_OPERATION;
import java.util.Collections;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.ows.LocalWorkspace;
import org.geoserver.ows.Request;
import org.geoserver.platform.ServiceException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class GwcServiceDispatcherCallbackTest {
    @Test
    public void testThatGwcServiceRequestsAreAccepted() {
        // creating some mocks needed for the test and instantiating the dispatcher call back
        HttpServletRequest httpRequest = newMockHttpRequest();
        Mockito.when(httpRequest.getParameterMap()).thenReturn(Collections.emptyMap());
        Request request = Mockito.mock(Request.class);
        Mockito.when(request.getHttpRequest()).thenReturn(httpRequest);
        // the catalog will not be used so it can be null
        GwcServiceDispatcherCallback callback = new GwcServiceDispatcherCallback(null);
        // not a gwc request
        Mockito.when(request.getContext()).thenReturn("wms/service");
        MatcherAssert.assertThat(callback.init(request), Matchers.nullValue());
        // a simple gwc request
        Mockito.when(request.getContext()).thenReturn("gwc/service");
        MatcherAssert.assertThat(callback.init(request), Matchers.notNullValue());
        // a valid virtual service request (setting a local workspace will make the workspace valid)
        LocalWorkspace.set(Mockito.mock(WorkspaceInfo.class));
        Mockito.when(request.getContext()).thenReturn("validWorkspace/gwc/service");
        MatcherAssert.assertThat(callback.init(request), Matchers.notNullValue());
        // an invalid virtual service request (a missing local workspace will make the workspace
        // invalid)
        LocalWorkspace.remove();
        Mockito.when(request.getContext()).thenReturn("invalidWorkspace/gwc/service");
        try {
            callback.init(request);
            Assert.fail("The workspace is not valid, an exception should have been throw.");
        } catch (ServiceException serviceException) {
            MatcherAssert.assertThat(serviceException.getMessage(), Matchers.is("No such workspace 'invalidWorkspace'"));
        }
    }

    @Test
    public void testGwcVirtualServiceRequestWrapper() {
        // we create a mock for the http request
        HttpServletRequest httpRequest = newMockHttpRequest();
        Mockito.when(httpRequest.getParameterMap()).thenReturn(new HashMap());
        Mockito.when(httpRequest.getContextPath()).thenReturn("geoserver");
        // we create a mock for the geoserver request
        Request request = new Request();
        request.setKvp(Collections.singletonMap("LAYER", "someLayer"));
        request.setHttpRequest(httpRequest);
        request.setContext("someWorkspace/gwc/service");
        // mock for the local workspace
        WorkspaceInfo workspace = Mockito.mock(WorkspaceInfo.class);
        Mockito.when(workspace.getName()).thenReturn("someWorkspace");
        // instantiating the dispatcher callback
        GwcServiceDispatcherCallback callback = new GwcServiceDispatcherCallback(null);
        // setting a local workspace
        LocalWorkspace.set(workspace);
        Request wrappedRequest = callback.init(request);
        MatcherAssert.assertThat(wrappedRequest, Matchers.notNullValue());
        MatcherAssert.assertThat(wrappedRequest.getHttpRequest(), Matchers.notNullValue());
        MatcherAssert.assertThat(wrappedRequest.getHttpRequest().getContextPath(), Matchers.is("geoserver/someWorkspace"));
        MatcherAssert.assertThat(wrappedRequest.getHttpRequest().getParameter("layer"), Matchers.is("someWorkspace:someLayer"));
        MatcherAssert.assertThat(wrappedRequest.getHttpRequest().getParameterMap(), Matchers.notNullValue());
        MatcherAssert.assertThat(wrappedRequest.getHttpRequest().getParameterMap().get("layer"), Matchers.is(new String[]{ "someWorkspace:someLayer" }));
        MatcherAssert.assertThat(wrappedRequest.getHttpRequest().getParameterValues("layer"), Matchers.is(new String[]{ "someWorkspace:someLayer" }));
    }

    @Test
    public void testThatGwcOperationIsStored() {
        // creating some mocks needed for the test and instantiating the dispatcher call back
        HttpServletRequest httpRequest = newMockHttpRequest();
        Mockito.when(httpRequest.getParameterMap()).thenReturn(Collections.emptyMap());
        Request request = new Request();
        request.setKvp(Collections.singletonMap("REQUEST", "GetCapabilities"));
        request.setHttpRequest(httpRequest);
        request.setContext("gwc/service");
        // the catalog will not be used so it can be null
        GwcServiceDispatcherCallback callback = new GwcServiceDispatcherCallback(null);
        // invoke the dispatcher callback
        Request wrappedRequest = callback.init(request);
        MatcherAssert.assertThat(wrappedRequest, Matchers.notNullValue());
        MatcherAssert.assertThat(GWC_OPERATION.get(), Matchers.notNullValue());
        MatcherAssert.assertThat(GWC_OPERATION.get(), Matchers.is("GetCapabilities"));
    }
}

