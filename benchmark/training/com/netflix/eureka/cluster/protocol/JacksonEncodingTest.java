package com.netflix.eureka.cluster.protocol;


import com.netflix.discovery.converters.EurekaJacksonCodec;
import com.netflix.discovery.shared.transport.ClusterSampleData;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tomasz Bak
 */
public class JacksonEncodingTest {
    private final EurekaJacksonCodec jacksonCodec = new EurekaJacksonCodec();

    @Test
    public void testReplicationInstanceEncoding() throws Exception {
        ReplicationInstance replicationInstance = ClusterSampleData.newReplicationInstance();
        // Encode / decode
        String jsonText = jacksonCodec.writeToString(replicationInstance);
        ReplicationInstance decodedValue = jacksonCodec.readValue(ReplicationInstance.class, jsonText);
        Assert.assertThat(decodedValue, CoreMatchers.is(CoreMatchers.equalTo(replicationInstance)));
    }

    @Test
    public void testReplicationInstanceResponseEncoding() throws Exception {
        ReplicationInstanceResponse replicationInstanceResponse = ClusterSampleData.newReplicationInstanceResponse(true);
        // Encode / decode
        String jsonText = jacksonCodec.writeToString(replicationInstanceResponse);
        ReplicationInstanceResponse decodedValue = jacksonCodec.readValue(ReplicationInstanceResponse.class, jsonText);
        Assert.assertThat(decodedValue, CoreMatchers.is(CoreMatchers.equalTo(replicationInstanceResponse)));
    }

    @Test
    public void testReplicationListEncoding() throws Exception {
        ReplicationList replicationList = new ReplicationList();
        replicationList.addReplicationInstance(ClusterSampleData.newReplicationInstance());
        // Encode / decode
        String jsonText = jacksonCodec.writeToString(replicationList);
        ReplicationList decodedValue = jacksonCodec.readValue(ReplicationList.class, jsonText);
        Assert.assertThat(decodedValue, CoreMatchers.is(CoreMatchers.equalTo(replicationList)));
    }

    @Test
    public void testReplicationListResponseEncoding() throws Exception {
        ReplicationListResponse replicationListResponse = new ReplicationListResponse();
        replicationListResponse.addResponse(ClusterSampleData.newReplicationInstanceResponse(false));
        // Encode / decode
        String jsonText = jacksonCodec.writeToString(replicationListResponse);
        ReplicationListResponse decodedValue = jacksonCodec.readValue(ReplicationListResponse.class, jsonText);
        Assert.assertThat(decodedValue, CoreMatchers.is(CoreMatchers.equalTo(replicationListResponse)));
    }
}

