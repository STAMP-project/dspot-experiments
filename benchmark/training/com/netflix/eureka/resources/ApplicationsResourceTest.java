package com.netflix.eureka.resources;


import CodecWrappers.LegacyJacksonJson;
import EurekaAccept.compact;
import EurekaAccept.full;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import Version.V2;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.converters.wrappers.CodecWrappers;
import com.netflix.discovery.converters.wrappers.DecoderWrapper;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.util.EurekaEntityComparators;
import com.netflix.eureka.AbstractTester;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 *
 *
 * @author David Liu
 */
public class ApplicationsResourceTest extends AbstractTester {
    private ApplicationsResource applicationsResource;

    private Applications testApplications;

    @Test
    public void testFullAppsGetJson() throws Exception {
        Response response = // encoding
        // uriInfo
        // remote regions
        applicationsResource.getContainers(V2.name(), APPLICATION_JSON, null, full.name(), null, null);
        String json = String.valueOf(response.getEntity());
        DecoderWrapper decoder = CodecWrappers.getDecoder(LegacyJacksonJson.class);
        Applications decoded = decoder.decode(json, Applications.class);
        // test per app as the full apps list include the mock server that is not part of the test apps
        for (Application application : testApplications.getRegisteredApplications()) {
            Application decodedApp = decoded.getRegisteredApplications(application.getName());
            MatcherAssert.assertThat(EurekaEntityComparators.equal(application, decodedApp), CoreMatchers.is(true));
        }
    }

    @Test
    public void testFullAppsGetGzipJsonHeaderType() throws Exception {
        Response response = // encoding
        // uriInfo
        // remote regions
        applicationsResource.getContainers(V2.name(), APPLICATION_JSON, "gzip", full.name(), null, null);
        MatcherAssert.assertThat(response.getMetadata().getFirst("Content-Encoding").toString(), CoreMatchers.is("gzip"));
        MatcherAssert.assertThat(response.getMetadata().getFirst("Content-Type").toString(), CoreMatchers.is(APPLICATION_JSON));
    }

    @Test
    public void testFullAppsGetGzipXmlHeaderType() throws Exception {
        Response response = // encoding
        // uriInfo
        // remote regions
        applicationsResource.getContainers(V2.name(), APPLICATION_XML, "gzip", full.name(), null, null);
        MatcherAssert.assertThat(response.getMetadata().getFirst("Content-Encoding").toString(), CoreMatchers.is("gzip"));
        MatcherAssert.assertThat(response.getMetadata().getFirst("Content-Type").toString(), CoreMatchers.is(APPLICATION_XML));
    }

    @Test
    public void testMiniAppsGet() throws Exception {
        Response response = // encoding
        // uriInfo
        // remote regions
        applicationsResource.getContainers(V2.name(), APPLICATION_JSON, null, compact.name(), null, null);
        String json = String.valueOf(response.getEntity());
        DecoderWrapper decoder = CodecWrappers.getDecoder(LegacyJacksonJson.class);
        Applications decoded = decoder.decode(json, Applications.class);
        // test per app as the full apps list include the mock server that is not part of the test apps
        for (Application application : testApplications.getRegisteredApplications()) {
            Application decodedApp = decoded.getRegisteredApplications(application.getName());
            // assert false as one is mini, so should NOT equal
            MatcherAssert.assertThat(EurekaEntityComparators.equal(application, decodedApp), CoreMatchers.is(false));
        }
        for (Application application : testApplications.getRegisteredApplications()) {
            Application decodedApp = decoded.getRegisteredApplications(application.getName());
            MatcherAssert.assertThat(application.getName(), CoreMatchers.is(decodedApp.getName()));
            // now do mini equals
            for (InstanceInfo instanceInfo : application.getInstances()) {
                InstanceInfo decodedInfo = decodedApp.getByInstanceId(instanceInfo.getId());
                MatcherAssert.assertThat(EurekaEntityComparators.equalMini(instanceInfo, decodedInfo), CoreMatchers.is(true));
            }
        }
    }
}

