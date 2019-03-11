package org.kairosdb.core.http.rest;


import java.io.IOException;
import org.junit.Test;
import org.kairosdb.testing.JsonResponse;


public class FeaturesResourceTest extends ResourceBase {
    private static final String FEATURE_PROCESSING_URL = "http://localhost:9001/api/v1/features/";

    @Test
    public void testGetAggregatorList() throws IOException {
        JsonResponse response = ResourceBase.client.get(((FeaturesResourceTest.FEATURE_PROCESSING_URL) + "aggregators"));
        MetricsResourceTest.assertResponse(response, 200, "[]");
    }

    @Test
    public void testGetInvalidFeature() throws IOException {
        JsonResponse response = ResourceBase.client.get(((FeaturesResourceTest.FEATURE_PROCESSING_URL) + "intel"));
        MetricsResourceTest.assertResponse(response, 404, "{\"errors\":[\"Unknown feature \'intel\'\"]}");
    }

    @Test
    public void getTestGetFeatures() throws IOException {
        JsonResponse response = ResourceBase.client.get(FeaturesResourceTest.FEATURE_PROCESSING_URL);
        MetricsResourceTest.assertResponse(response, 200, "[{\"name\":\"group_by\",\"label\":\"Test GroupBy\",\"properties\":[]},{\"name\":\"aggregators\",\"label\":\"Test Aggregator\",\"properties\":[]}]");
    }
}

