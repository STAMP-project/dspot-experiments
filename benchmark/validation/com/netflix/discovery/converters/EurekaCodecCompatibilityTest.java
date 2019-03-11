package com.netflix.discovery.converters;


import DataCenterInfo.Name;
import InstanceInfo.InstanceStatus.OUT_OF_SERVICE;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.converters.wrappers.CodecWrapper;
import com.netflix.discovery.converters.wrappers.CodecWrappers;
import com.netflix.discovery.converters.wrappers.DecoderWrapper;
import com.netflix.discovery.converters.wrappers.EncoderWrapper;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.util.EurekaEntityComparators;
import com.netflix.discovery.util.InstanceInfoGenerator;
import com.netflix.eureka.cluster.protocol.ReplicationInstance;
import com.netflix.eureka.cluster.protocol.ReplicationInstanceResponse;
import com.netflix.eureka.cluster.protocol.ReplicationList;
import com.netflix.eureka.cluster.protocol.ReplicationListResponse;
import com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl.Action;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tomasz Bak
 */
public class EurekaCodecCompatibilityTest {
    private static final List<CodecWrapper> availableJsonWrappers = new ArrayList<>();

    private static final List<CodecWrapper> availableXmlWrappers = new ArrayList<>();

    static {
        EurekaCodecCompatibilityTest.availableJsonWrappers.add(new CodecWrappers.XStreamJson());
        EurekaCodecCompatibilityTest.availableJsonWrappers.add(new CodecWrappers.LegacyJacksonJson());
        EurekaCodecCompatibilityTest.availableJsonWrappers.add(new CodecWrappers.JacksonJson());
        EurekaCodecCompatibilityTest.availableXmlWrappers.add(new CodecWrappers.JacksonXml());
        EurekaCodecCompatibilityTest.availableXmlWrappers.add(new CodecWrappers.XStreamXml());
    }

    private final InstanceInfoGenerator infoGenerator = InstanceInfoGenerator.newBuilder(4, 2).withMetaData(true).build();

    private final Iterator<InstanceInfo> infoIterator = infoGenerator.serviceIterator();

    interface Action2 {
        void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException;
    }

    /**
     *
     *
     * @deprecated see to do note in {@link com.netflix.appinfo.LeaseInfo} and delete once legacy is removed
     */
    @Deprecated
    @Test
    public void testInstanceInfoEncodeDecodeLegacyJacksonToJackson() throws Exception {
        final InstanceInfo instanceInfo = infoIterator.next();
        EurekaCodecCompatibilityTest.Action2 codingAction = new EurekaCodecCompatibilityTest.Action2() {
            @Override
            public void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException {
                String encodedString = encodingCodec.encode(instanceInfo);
                // convert the field from the json string to what the legacy json would encode as
                encodedString = encodedString.replaceFirst("lastRenewalTimestamp", "renewalTimestamp");
                InstanceInfo decodedValue = decodingCodec.decode(encodedString, InstanceInfo.class);
                Assert.assertThat(EurekaEntityComparators.equal(instanceInfo, decodedValue, new EurekaEntityComparators.RawIdEqualFunc()), CoreMatchers.is(true));
                Assert.assertThat(EurekaEntityComparators.equal(instanceInfo, decodedValue), CoreMatchers.is(true));
            }
        };
        verifyForPair(codingAction, InstanceInfo.class, new CodecWrappers.LegacyJacksonJson(), new CodecWrappers.JacksonJson());
    }

    @Test
    public void testInstanceInfoEncodeDecodeJsonWithEmptyMetadataMap() throws Exception {
        final InstanceInfo base = infoIterator.next();
        final InstanceInfo instanceInfo = setMetadata(Collections.EMPTY_MAP).build();
        EurekaCodecCompatibilityTest.Action2 codingAction = new EurekaCodecCompatibilityTest.Action2() {
            @Override
            public void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException {
                String encodedString = encodingCodec.encode(instanceInfo);
                InstanceInfo decodedValue = decodingCodec.decode(encodedString, InstanceInfo.class);
                Assert.assertThat(EurekaEntityComparators.equal(instanceInfo, decodedValue), CoreMatchers.is(true));
            }
        };
        verifyAllPairs(codingAction, Application.class, EurekaCodecCompatibilityTest.availableJsonWrappers);
        verifyAllPairs(codingAction, Application.class, EurekaCodecCompatibilityTest.availableXmlWrappers);
    }

    /**
     * During deserialization process in compact mode not all fields might be filtered out. If JVM memory
     * is an issue, compact version of the encoder should be used on the server side.
     */
    @Test
    public void testInstanceInfoFullEncodeMiniDecodeJackson() throws Exception {
        final InstanceInfo instanceInfo = infoIterator.next();
        EurekaCodecCompatibilityTest.Action2 codingAction = new EurekaCodecCompatibilityTest.Action2() {
            @Override
            public void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException {
                String encodedString = encodingCodec.encode(instanceInfo);
                InstanceInfo decodedValue = decodingCodec.decode(encodedString, InstanceInfo.class);
                Assert.assertThat(EurekaEntityComparators.equalMini(instanceInfo, decodedValue), CoreMatchers.is(true));
            }
        };
        verifyForPair(codingAction, InstanceInfo.class, new CodecWrappers.JacksonJson(), new CodecWrappers.JacksonJsonMini());
    }

    @Test
    public void testInstanceInfoFullEncodeMiniDecodeJacksonWithMyOwnDataCenterInfo() throws Exception {
        final InstanceInfo base = infoIterator.next();
        final InstanceInfo instanceInfo = setDataCenterInfo(new com.netflix.appinfo.MyDataCenterInfo(Name.MyOwn)).build();
        EurekaCodecCompatibilityTest.Action2 codingAction = new EurekaCodecCompatibilityTest.Action2() {
            @Override
            public void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException {
                String encodedString = encodingCodec.encode(instanceInfo);
                InstanceInfo decodedValue = decodingCodec.decode(encodedString, InstanceInfo.class);
                Assert.assertThat(EurekaEntityComparators.equalMini(instanceInfo, decodedValue), CoreMatchers.is(true));
            }
        };
        verifyForPair(codingAction, InstanceInfo.class, new CodecWrappers.JacksonJson(), new CodecWrappers.JacksonJsonMini());
    }

    @Test
    public void testInstanceInfoMiniEncodeMiniDecodeJackson() throws Exception {
        final InstanceInfo instanceInfo = infoIterator.next();
        EurekaCodecCompatibilityTest.Action2 codingAction = new EurekaCodecCompatibilityTest.Action2() {
            @Override
            public void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException {
                String encodedString = encodingCodec.encode(instanceInfo);
                InstanceInfo decodedValue = decodingCodec.decode(encodedString, InstanceInfo.class);
                Assert.assertThat(EurekaEntityComparators.equalMini(instanceInfo, decodedValue), CoreMatchers.is(true));
            }
        };
        verifyForPair(codingAction, InstanceInfo.class, new CodecWrappers.JacksonJsonMini(), new CodecWrappers.JacksonJsonMini());
    }

    @Test
    public void testInstanceInfoEncodeDecode() throws Exception {
        final InstanceInfo instanceInfo = infoIterator.next();
        EurekaCodecCompatibilityTest.Action2 codingAction = new EurekaCodecCompatibilityTest.Action2() {
            @Override
            public void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException {
                String encodedString = encodingCodec.encode(instanceInfo);
                InstanceInfo decodedValue = decodingCodec.decode(encodedString, InstanceInfo.class);
                Assert.assertThat(EurekaEntityComparators.equal(instanceInfo, decodedValue), CoreMatchers.is(true));
                Assert.assertThat(EurekaEntityComparators.equal(instanceInfo, decodedValue, new EurekaEntityComparators.RawIdEqualFunc()), CoreMatchers.is(true));
            }
        };
        verifyAllPairs(codingAction, InstanceInfo.class, EurekaCodecCompatibilityTest.availableJsonWrappers);
        verifyAllPairs(codingAction, InstanceInfo.class, EurekaCodecCompatibilityTest.availableXmlWrappers);
    }

    // https://github.com/Netflix/eureka/issues/1051
    // test going from camel case to lower case
    @Test
    public void testInstanceInfoEncodeDecodeCompatibilityDueToOverriddenStatusRenamingV1() throws Exception {
        final InstanceInfo instanceInfo = infoIterator.next();
        new InstanceInfo.Builder(instanceInfo).setOverriddenStatus(OUT_OF_SERVICE);
        EurekaCodecCompatibilityTest.Action2 codingAction = new EurekaCodecCompatibilityTest.Action2() {
            @Override
            public void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException {
                String encodedString = encodingCodec.encode(instanceInfo);
                // sed to older naming to test
                encodedString = encodedString.replace("overriddenStatus", "overriddenstatus");
                InstanceInfo decodedValue = decodingCodec.decode(encodedString, InstanceInfo.class);
                Assert.assertThat(EurekaEntityComparators.equal(instanceInfo, decodedValue), CoreMatchers.is(true));
                Assert.assertThat(EurekaEntityComparators.equal(instanceInfo, decodedValue, new EurekaEntityComparators.RawIdEqualFunc()), CoreMatchers.is(true));
            }
        };
        verifyAllPairs(codingAction, Application.class, EurekaCodecCompatibilityTest.availableJsonWrappers);
        verifyAllPairs(codingAction, Application.class, EurekaCodecCompatibilityTest.availableXmlWrappers);
    }

    // same as the above, but go from lower case to camel case
    @Test
    public void testInstanceInfoEncodeDecodeCompatibilityDueToOverriddenStatusRenamingV2() throws Exception {
        final InstanceInfo instanceInfo = infoIterator.next();
        new InstanceInfo.Builder(instanceInfo).setOverriddenStatus(OUT_OF_SERVICE);
        EurekaCodecCompatibilityTest.Action2 codingAction = new EurekaCodecCompatibilityTest.Action2() {
            @Override
            public void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException {
                String encodedString = encodingCodec.encode(instanceInfo);
                // sed to older naming to test
                encodedString = encodedString.replace("overriddenstatus", "overriddenStatus");
                InstanceInfo decodedValue = decodingCodec.decode(encodedString, InstanceInfo.class);
                Assert.assertThat(EurekaEntityComparators.equal(instanceInfo, decodedValue), CoreMatchers.is(true));
                Assert.assertThat(EurekaEntityComparators.equal(instanceInfo, decodedValue, new EurekaEntityComparators.RawIdEqualFunc()), CoreMatchers.is(true));
            }
        };
        verifyAllPairs(codingAction, Application.class, EurekaCodecCompatibilityTest.availableJsonWrappers);
        verifyAllPairs(codingAction, Application.class, EurekaCodecCompatibilityTest.availableXmlWrappers);
    }

    @Test
    public void testApplicationEncodeDecode() throws Exception {
        final Application application = new Application("testApp");
        application.addInstance(infoIterator.next());
        application.addInstance(infoIterator.next());
        EurekaCodecCompatibilityTest.Action2 codingAction = new EurekaCodecCompatibilityTest.Action2() {
            @Override
            public void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException {
                String encodedString = encodingCodec.encode(application);
                Application decodedValue = decodingCodec.decode(encodedString, Application.class);
                Assert.assertThat(EurekaEntityComparators.equal(application, decodedValue), CoreMatchers.is(true));
            }
        };
        verifyAllPairs(codingAction, Application.class, EurekaCodecCompatibilityTest.availableJsonWrappers);
        verifyAllPairs(codingAction, Application.class, EurekaCodecCompatibilityTest.availableXmlWrappers);
    }

    @Test
    public void testApplicationsEncodeDecode() throws Exception {
        final Applications applications = infoGenerator.takeDelta(2);
        EurekaCodecCompatibilityTest.Action2 codingAction = new EurekaCodecCompatibilityTest.Action2() {
            @Override
            public void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException {
                String encodedString = encodingCodec.encode(applications);
                Applications decodedValue = decodingCodec.decode(encodedString, Applications.class);
                Assert.assertThat(EurekaEntityComparators.equal(applications, decodedValue), CoreMatchers.is(true));
            }
        };
        verifyAllPairs(codingAction, Applications.class, EurekaCodecCompatibilityTest.availableJsonWrappers);
        verifyAllPairs(codingAction, Applications.class, EurekaCodecCompatibilityTest.availableXmlWrappers);
    }

    /**
     * For backward compatibility with LegacyJacksonJson codec single item arrays shall not be unwrapped.
     */
    @Test
    public void testApplicationsJsonEncodeDecodeWithSingleAppItem() throws Exception {
        final Applications applications = infoGenerator.takeDelta(1);
        EurekaCodecCompatibilityTest.Action2 codingAction = new EurekaCodecCompatibilityTest.Action2() {
            @Override
            public void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException {
                String encodedString = encodingCodec.encode(applications);
                Assert.assertThat(encodedString.contains("\"application\":[{"), CoreMatchers.is(true));
                Applications decodedValue = decodingCodec.decode(encodedString, Applications.class);
                Assert.assertThat(EurekaEntityComparators.equal(applications, decodedValue), CoreMatchers.is(true));
            }
        };
        List<CodecWrapper> jsonCodes = Arrays.asList(new CodecWrappers.LegacyJacksonJson(), new CodecWrappers.JacksonJson());
        verifyAllPairs(codingAction, Applications.class, jsonCodes);
    }

    @Test
    public void testBatchRequestEncoding() throws Exception {
        InstanceInfo instance = InstanceInfoGenerator.takeOne();
        List<ReplicationInstance> replicationInstances = new ArrayList<>();
        replicationInstances.add(new ReplicationInstance(instance.getAppName(), instance.getId(), System.currentTimeMillis(), null, instance.getStatus().name(), instance, Action.Register));
        final ReplicationList replicationList = new ReplicationList(replicationInstances);
        EurekaCodecCompatibilityTest.Action2 codingAction = new EurekaCodecCompatibilityTest.Action2() {
            @Override
            public void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException {
                String encodedString = encodingCodec.encode(replicationList);
                ReplicationList decodedValue = decodingCodec.decode(encodedString, ReplicationList.class);
                Assert.assertThat(decodedValue.getReplicationList().size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
            }
        };
        // In replication channel we use JSON only
        List<CodecWrapper> jsonCodes = Arrays.asList(new CodecWrappers.JacksonJson(), new CodecWrappers.LegacyJacksonJson());
        verifyAllPairs(codingAction, ReplicationList.class, jsonCodes);
    }

    @Test
    public void testBatchResponseEncoding() throws Exception {
        List<ReplicationInstanceResponse> responseList = new ArrayList<>();
        responseList.add(new ReplicationInstanceResponse(200, InstanceInfoGenerator.takeOne()));
        final ReplicationListResponse replicationListResponse = new ReplicationListResponse(responseList);
        EurekaCodecCompatibilityTest.Action2 codingAction = new EurekaCodecCompatibilityTest.Action2() {
            @Override
            public void call(EncoderWrapper encodingCodec, DecoderWrapper decodingCodec) throws IOException {
                String encodedString = encodingCodec.encode(replicationListResponse);
                ReplicationListResponse decodedValue = decodingCodec.decode(encodedString, ReplicationListResponse.class);
                Assert.assertThat(decodedValue.getResponseList().size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
            }
        };
        // In replication channel we use JSON only
        List<CodecWrapper> jsonCodes = Arrays.asList(new CodecWrappers.JacksonJson(), new CodecWrappers.LegacyJacksonJson());
        verifyAllPairs(codingAction, ReplicationListResponse.class, jsonCodes);
    }
}

