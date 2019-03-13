package com.netflix.eureka.resources;


import CodecWrappers.LegacyJacksonJson;
import EurekaAccept.compact;
import EurekaAccept.full;
import Key.EntityType.VIP;
import MediaType.APPLICATION_JSON;
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
public class AbstractVIPResourceTest extends AbstractTester {
    private String vipName;

    private AbstractVIPResource resource;

    private Application testApplication;

    @Test
    public void testFullVipGet() throws Exception {
        Response response = resource.getVipResponse(V2.name(), vipName, APPLICATION_JSON, full, VIP);
        String json = String.valueOf(response.getEntity());
        DecoderWrapper decoder = CodecWrappers.getDecoder(LegacyJacksonJson.class);
        Applications decodedApps = decoder.decode(json, Applications.class);
        Application decodedApp = decodedApps.getRegisteredApplications(testApplication.getName());
        MatcherAssert.assertThat(EurekaEntityComparators.equal(testApplication, decodedApp), CoreMatchers.is(true));
    }

    @Test
    public void testMiniVipGet() throws Exception {
        Response response = resource.getVipResponse(V2.name(), vipName, APPLICATION_JSON, compact, VIP);
        String json = String.valueOf(response.getEntity());
        DecoderWrapper decoder = CodecWrappers.getDecoder(LegacyJacksonJson.class);
        Applications decodedApps = decoder.decode(json, Applications.class);
        Application decodedApp = decodedApps.getRegisteredApplications(testApplication.getName());
        // assert false as one is mini, so should NOT equal
        MatcherAssert.assertThat(EurekaEntityComparators.equal(testApplication, decodedApp), CoreMatchers.is(false));
        for (InstanceInfo instanceInfo : testApplication.getInstances()) {
            InstanceInfo decodedInfo = decodedApp.getByInstanceId(instanceInfo.getId());
            MatcherAssert.assertThat(EurekaEntityComparators.equalMini(instanceInfo, decodedInfo), CoreMatchers.is(true));
        }
    }
}

