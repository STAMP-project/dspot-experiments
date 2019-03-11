package com.github.dreamhead.moco;


import com.github.dreamhead.moco.util.Jsons;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;
import java.io.IOException;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoRestStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_get_resource() throws IOException {
        runWithConfiguration("rest/rest.json");
        HttpResponse response = helper.getResponseWithHeader(remoteUrl("/targets/1"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json"));
        MocoRestStandaloneTest.Plain plain = asPlain(response);
        Assert.assertThat(plain.code, CoreMatchers.is(1));
        Assert.assertThat(plain.message, CoreMatchers.is("foo"));
        MocoRestStandaloneTest.Plain response2 = getResource("/targets/2");
        Assert.assertThat(response2.code, CoreMatchers.is(2));
        Assert.assertThat(response2.message, CoreMatchers.is("bar"));
    }

    @Test
    public void should_post() throws IOException {
        runWithConfiguration("rest/rest.json");
        final MocoRestStandaloneTest.Plain resource1 = new MocoRestStandaloneTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        HttpResponse httpResponse = helper.postForResponse(remoteUrl("/targets"), Jsons.toJson(resource1));
        Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(201));
        Assert.assertThat(httpResponse.getFirstHeader("Location").getValue(), CoreMatchers.is("/targets/123"));
    }

    @Test
    public void should_put() throws IOException {
        runWithConfiguration("rest/rest.json");
        final MocoRestStandaloneTest.Plain resource1 = new MocoRestStandaloneTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        HttpResponse httpResponse = helper.putForResponse(remoteUrl("/targets/1"), Jsons.toJson(resource1));
        Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(200));
    }

    @Test
    public void should_delete() throws IOException {
        runWithConfiguration("rest/rest.json");
        final MocoRestStandaloneTest.Plain resource1 = new MocoRestStandaloneTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        HttpResponse httpResponse = helper.deleteForResponse(remoteUrl("/targets/1"));
        Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(200));
    }

    @Test
    public void should_head() throws IOException {
        runWithConfiguration("rest/rest.json");
        final MocoRestStandaloneTest.Plain resource1 = new MocoRestStandaloneTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        HttpResponse httpResponse = helper.headForResponse(remoteUrl("/targets/1"));
        Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(200));
        Assert.assertThat(httpResponse.getHeaders("ETag")[0].getValue(), CoreMatchers.is("Moco"));
    }

    @Test
    public void should_patch() throws IOException {
        runWithConfiguration("rest/rest.json");
        final MocoRestStandaloneTest.Plain resource1 = new MocoRestStandaloneTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        Assert.assertThat(helper.patchForResponse(remoteUrl("/targets/1"), "result"), CoreMatchers.is("patch result"));
    }

    @Test
    public void should_get_resource_with_any_id() throws IOException {
        runWithConfiguration("rest/rest.json");
        HttpResponse response = helper.getResponseWithHeader(remoteUrl("/any-targets/1"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json"));
        MocoRestStandaloneTest.Plain plain = asPlain(response);
        Assert.assertThat(plain.code, CoreMatchers.is(1));
        Assert.assertThat(plain.message, CoreMatchers.is("any"));
        HttpResponse response2 = helper.getResponseWithHeader(remoteUrl("/any-targets/1"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json"));
        MocoRestStandaloneTest.Plain plain2 = asPlain(response2);
        Assert.assertThat(plain2.code, CoreMatchers.is(1));
        Assert.assertThat(plain2.message, CoreMatchers.is("any"));
    }

    @Test
    public void should_get_resource_with_any_id_and_any_sub() throws IOException {
        runWithConfiguration("rest/rest.json");
        HttpResponse response = helper.getResponseWithHeader(remoteUrl("/any-targets/1/any-subs/1"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json"));
        MocoRestStandaloneTest.Plain plain = asPlain(response);
        Assert.assertThat(plain.code, CoreMatchers.is(100));
        Assert.assertThat(plain.message, CoreMatchers.is("any-sub"));
        HttpResponse response2 = helper.getResponseWithHeader(remoteUrl("/any-targets/2/any-subs/2"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json"));
        MocoRestStandaloneTest.Plain plain2 = asPlain(response2);
        Assert.assertThat(plain2.code, CoreMatchers.is(100));
        Assert.assertThat(plain2.message, CoreMatchers.is("any-sub"));
    }

    @Test
    public void should_get_sub_resource() throws IOException {
        runWithConfiguration("rest/rest.json");
        HttpResponse response = helper.getResponseWithHeader(remoteUrl("/targets/1/subs/1"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json"));
        MocoRestStandaloneTest.Plain plain = asPlain(response);
        Assert.assertThat(plain.code, CoreMatchers.is(3));
        Assert.assertThat(plain.message, CoreMatchers.is("sub"));
    }

    @Test
    public void should_get_sub_sub_resource() throws IOException {
        runWithConfiguration("rest/rest.json");
        HttpResponse response = helper.getResponseWithHeader(remoteUrl("/targets/1/subs/1/sub-subs/1"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json"));
        MocoRestStandaloneTest.Plain plain = asPlain(response);
        Assert.assertThat(plain.code, CoreMatchers.is(4));
        Assert.assertThat(plain.message, CoreMatchers.is("sub-sub"));
    }

    @Test
    public void should_get_sub_resource_with_any_id() throws IOException {
        runWithConfiguration("rest/rest.json");
        HttpResponse response = helper.getResponseWithHeader(remoteUrl("/targets/1/any-subs/1"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json"));
        MocoRestStandaloneTest.Plain plain = asPlain(response);
        Assert.assertThat(plain.code, CoreMatchers.is(4));
        Assert.assertThat(plain.message, CoreMatchers.is("any-sub"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_while_no_response_found_in_rest_setting() {
        runWithConfiguration("rest/rest_error_without_response.json");
    }

    @Test
    public void should_get_all_resource() throws IOException {
        runWithConfiguration("rest/rest.json");
        HttpResponse response = helper.getResponseWithHeader(remoteUrl("/all-resources"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json"));
        Assert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.is(200));
        HttpEntity entity = response.getEntity();
        List<MocoRestStandaloneTest.Plain> plains = Jsons.toObjects(entity.getContent(), MocoRestStandaloneTest.Plain.class);
        Assert.assertThat(plains.size(), CoreMatchers.is(2));
    }

    @Test
    public void should_head_all_resource() throws IOException {
        runWithConfiguration("rest/rest.json");
        HttpResponse httpResponse = helper.headForResponse(remoteUrl("/all-resources"));
        Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(200));
        Assert.assertThat(httpResponse.getHeaders("ETag")[0].getValue(), CoreMatchers.is("Moco"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_without_put_id() {
        runWithConfiguration("rest/rest_error_without_put_id.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_without_delete_id() {
        runWithConfiguration("rest/rest_error_without_delete_id.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_without_patch_id() {
        runWithConfiguration("rest/rest_error_without_patch_id.json");
    }

    private static class Plain {
        public int code;

        public String message;
    }
}

