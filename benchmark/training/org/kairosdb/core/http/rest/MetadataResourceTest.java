package org.kairosdb.core.http.rest;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.testing.JsonResponse;


public class MetadataResourceTest extends ResourceBase {
    private static final String SERVICE = "service";

    private static final String UNAUTHORIZED_SERVICE = "_service";

    private static final String SERVICE_KEY1 = "service_key1";

    private static final String SERVICE_KEY2 = "service_key2";

    private static final String METADATA_URL = "http://localhost:9001/api/v1/metadata/";

    private static final int OK = 200;

    private static final int NO_CONTENT = 204;

    private static final int UNAUTHORIZED_ERROR = 401;

    private static final int INTERNAL_SERVER_ERROR = 500;

    @Test(expected = NullPointerException.class)
    public void test_constructor_nullDatastore_invalid() {
        new MetadataResource(null);
    }

    @Test
    public void listKeysStartsWith() throws Exception {
        JsonResponse response = ResourceBase.client.get((((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "?startsWith=foo"));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.OK));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo("{\"results\":[\"foo\",\"foobar\"]}"));
        response = ResourceBase.client.get((((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "?startsWith=fi"));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.OK));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo("{\"results\":[]}"));
    }

    @Test
    public void listKeys() throws Exception {
        JsonResponse response = ResourceBase.client.get(((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.OK));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo("{\"results\":[\"foo\",\"foobar\",\"tee\"]}"));
    }

    @Test
    public void listKeys_notAuthorized() throws IOException {
        JsonResponse response = ResourceBase.client.get(((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.UNAUTHORIZED_SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.UNAUTHORIZED_ERROR));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo(""));
    }

    @Test
    public void listKeys_withException() throws Exception {
        ResourceBase.datastore.throwException(new DatastoreException("expected"));
        JsonResponse response = ResourceBase.client.get(((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.INTERNAL_SERVER_ERROR));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo("{\"errors\":[\"expected\"]}"));
        ResourceBase.datastore.throwException(null);
    }

    @Test
    public void listServiceKeys() throws Exception {
        JsonResponse response = ResourceBase.client.get(((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.OK));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo((((("{\"results\":[\"" + (MetadataResourceTest.SERVICE_KEY1)) + "\",\"") + (MetadataResourceTest.SERVICE_KEY2)) + "\"]}")));
    }

    @Test
    public void listServiceKeys_notAuthorized() throws Exception {
        JsonResponse response = ResourceBase.client.get(((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.UNAUTHORIZED_SERVICE)));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.UNAUTHORIZED_ERROR));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo(""));
    }

    @Test
    public void getValue() throws Exception {
        JsonResponse response = ResourceBase.client.get((((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/foobar"));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.OK));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo("fi"));
    }

    @Test
    public void getValue_notAuthorized() throws Exception {
        JsonResponse response = ResourceBase.client.get((((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.UNAUTHORIZED_SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/foobar"));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.UNAUTHORIZED_ERROR));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo(""));
    }

    @Test
    public void getValue_withException() throws Exception {
        ResourceBase.datastore.throwException(new DatastoreException("expected"));
        JsonResponse response = ResourceBase.client.get((((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/foobar"));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.INTERNAL_SERVER_ERROR));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo("{\"errors\":[\"expected\"]}"));
        ResourceBase.datastore.throwException(null);
    }

    @Test
    public void getValue_empty() throws Exception {
        JsonResponse response = ResourceBase.client.get((((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/bogus"));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.OK));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo(""));
    }

    @Test
    public void setValue_withException() throws Exception {
        ResourceBase.datastore.throwException(new DatastoreException("expected"));
        JsonResponse response = ResourceBase.client.post("value", (((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/foobar"));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.INTERNAL_SERVER_ERROR));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo("{\"errors\":[\"expected\"]}"));
        ResourceBase.datastore.throwException(null);
    }

    @Test
    public void setValue_notAuthorized() throws Exception {
        JsonResponse response = ResourceBase.client.post("value", (((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.UNAUTHORIZED_SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/foobar"));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.UNAUTHORIZED_ERROR));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo(""));
    }

    @SuppressWarnings("UnusedAssignment")
    @Test
    public void deleteKey() throws Exception {
        JsonResponse response = ResourceBase.client.post("newValue", (((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/newKey"));
        response = ResourceBase.client.get((((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/newKey"));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo("newValue"));
        response = ResourceBase.client.delete((((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/newKey"));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.NO_CONTENT));
        response = ResourceBase.client.get((((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/newKey"));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo(""));
    }

    @Test
    public void deleteKey_notAuthorized() throws Exception {
        JsonResponse response = ResourceBase.client.get((((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.UNAUTHORIZED_SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/newKey"));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.UNAUTHORIZED_ERROR));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo(""));
    }

    @SuppressWarnings("UnusedAssignment")
    @Test
    public void deleteKey_withException() throws Exception {
        JsonResponse response = ResourceBase.client.post("newValue", (((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/newKey"));
        ResourceBase.datastore.throwException(new DatastoreException("expected"));
        response = ResourceBase.client.delete((((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/newKey"));
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(MetadataResourceTest.INTERNAL_SERVER_ERROR));
        Assert.assertThat(response.getJson(), CoreMatchers.equalTo("{\"errors\":[\"expected\"]}"));
        // clean up
        ResourceBase.datastore.throwException(null);
        response = ResourceBase.client.delete((((((MetadataResourceTest.METADATA_URL) + (MetadataResourceTest.SERVICE)) + "/") + (MetadataResourceTest.SERVICE_KEY1)) + "/newKey"));
    }
}

