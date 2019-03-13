package com.netflix.eureka.resources;


import AmazonInfo.MetaDataKey.instanceId;
import CodecWrappers.LegacyJacksonJson;
import EurekaAccept.compact;
import EurekaAccept.full;
import MediaType.APPLICATION_JSON;
import Version.V2;
import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.UniqueIdentifier;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.converters.wrappers.CodecWrappers;
import com.netflix.discovery.converters.wrappers.DecoderWrapper;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.util.EurekaEntityComparators;
import com.netflix.discovery.util.InstanceInfoGenerator;
import com.netflix.eureka.AbstractTester;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static Name.MyOwn;


/**
 *
 *
 * @author David Liu
 */
public class ApplicationResourceTest extends AbstractTester {
    private ApplicationResource applicationResource;

    private Application testApplication;

    @Test
    public void testFullAppGet() throws Exception {
        Response response = applicationResource.getApplication(V2.name(), APPLICATION_JSON, full.name());
        String json = String.valueOf(response.getEntity());
        DecoderWrapper decoder = CodecWrappers.getDecoder(LegacyJacksonJson.class);
        Application decodedApp = decoder.decode(json, Application.class);
        MatcherAssert.assertThat(EurekaEntityComparators.equal(testApplication, decodedApp), CoreMatchers.is(true));
    }

    @Test
    public void testMiniAppGet() throws Exception {
        Response response = applicationResource.getApplication(V2.name(), APPLICATION_JSON, compact.name());
        String json = String.valueOf(response.getEntity());
        DecoderWrapper decoder = CodecWrappers.getDecoder(LegacyJacksonJson.class);
        Application decodedApp = decoder.decode(json, Application.class);
        // assert false as one is mini, so should NOT equal
        MatcherAssert.assertThat(EurekaEntityComparators.equal(testApplication, decodedApp), CoreMatchers.is(false));
        for (InstanceInfo instanceInfo : testApplication.getInstances()) {
            InstanceInfo decodedInfo = decodedApp.getByInstanceId(instanceInfo.getId());
            MatcherAssert.assertThat(EurekaEntityComparators.equalMini(instanceInfo, decodedInfo), CoreMatchers.is(true));
        }
    }

    @Test
    public void testGoodRegistration() throws Exception {
        InstanceInfo noIdInfo = InstanceInfoGenerator.takeOne();
        Response response = applicationResource.addInstance(noIdInfo, (false + ""));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(204));
    }

    @Test
    public void testBadRegistration() throws Exception {
        InstanceInfo instanceInfo = Mockito.spy(InstanceInfoGenerator.takeOne());
        Mockito.when(instanceInfo.getId()).thenReturn(null);
        Response response = applicationResource.addInstance(instanceInfo, (false + ""));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(400));
        instanceInfo = Mockito.spy(InstanceInfoGenerator.takeOne());
        Mockito.when(instanceInfo.getHostName()).thenReturn(null);
        response = applicationResource.addInstance(instanceInfo, (false + ""));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(400));
        instanceInfo = Mockito.spy(InstanceInfoGenerator.takeOne());
        Mockito.when(instanceInfo.getIPAddr()).thenReturn(null);
        response = applicationResource.addInstance(instanceInfo, (false + ""));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(400));
        instanceInfo = Mockito.spy(InstanceInfoGenerator.takeOne());
        Mockito.when(instanceInfo.getAppName()).thenReturn("");
        response = applicationResource.addInstance(instanceInfo, (false + ""));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(400));
        instanceInfo = Mockito.spy(InstanceInfoGenerator.takeOne());
        Mockito.when(instanceInfo.getAppName()).thenReturn(((applicationResource.getName()) + "extraExtra"));
        response = applicationResource.addInstance(instanceInfo, (false + ""));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(400));
        instanceInfo = Mockito.spy(InstanceInfoGenerator.takeOne());
        Mockito.when(instanceInfo.getDataCenterInfo()).thenReturn(null);
        response = applicationResource.addInstance(instanceInfo, (false + ""));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(400));
        instanceInfo = Mockito.spy(InstanceInfoGenerator.takeOne());
        Mockito.when(instanceInfo.getDataCenterInfo()).thenReturn(new DataCenterInfo() {
            @Override
            public Name getName() {
                return null;
            }
        });
        response = applicationResource.addInstance(instanceInfo, (false + ""));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(400));
    }

    @Test
    public void testBadRegistrationOfDataCenterInfo() throws Exception {
        try {
            // test 400 when configured to return client error
            ConfigurationManager.getConfigInstance().setProperty("eureka.experimental.registration.validation.dataCenterInfoId", "true");
            InstanceInfo instanceInfo = Mockito.spy(InstanceInfoGenerator.takeOne());
            Mockito.when(instanceInfo.getDataCenterInfo()).thenReturn(new ApplicationResourceTest.TestDataCenterInfo());
            Response response = applicationResource.addInstance(instanceInfo, (false + ""));
            MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(400));
            // test backfill of data for AmazonInfo
            ConfigurationManager.getConfigInstance().setProperty("eureka.experimental.registration.validation.dataCenterInfoId", "false");
            instanceInfo = Mockito.spy(InstanceInfoGenerator.takeOne());
            MatcherAssert.assertThat(instanceInfo.getDataCenterInfo(), CoreMatchers.instanceOf(AmazonInfo.class));
            getMetadata().remove(instanceId.getName());// clear the Id

            response = applicationResource.addInstance(instanceInfo, (false + ""));
            MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(204));
        } finally {
            ConfigurationManager.getConfigInstance().clearProperty("eureka.experimental.registration.validation.dataCenterInfoId");
        }
    }

    private static class TestDataCenterInfo implements DataCenterInfo , UniqueIdentifier {
        @Override
        public Name getName() {
            return MyOwn;
        }

        @Override
        public String getId() {
            return null;// return null to test

        }
    }
}

