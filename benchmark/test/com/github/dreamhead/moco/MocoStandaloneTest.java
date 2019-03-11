package com.github.dreamhead.moco;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_expected_response() throws IOException {
        runWithConfiguration("foo.json");
        Assert.assertThat(helper.get(root()), CoreMatchers.is("foo"));
    }

    @Test
    public void should_return_expected_response_with_file() throws IOException {
        runWithConfiguration("any_response_with_file.json");
        Assert.assertThat(helper.get(root()), CoreMatchers.is("foo.response"));
    }

    @Test
    public void should_return_expected_response_with_file_and_charset() throws IOException {
        runWithConfiguration("response_with_file_and_charset.json");
        Assert.assertThat(helper.get(root()), CoreMatchers.is("foo.response"));
        Assert.assertThat(helper.get(remoteUrl("/charset_first")), CoreMatchers.is("foo.response"));
    }

    @Test
    public void should_return_expected_response_with_path_resource_and_charset() throws IOException {
        runWithConfiguration("response_with_path_resource_and_charset.json");
        Assert.assertThat(helper.get(root()), CoreMatchers.is("response from path"));
    }

    @Test
    public void should_return_expected_response_with_text_based_on_specified_uri() throws IOException {
        runWithConfiguration("foo.json");
        Assert.assertThat(helper.get(remoteUrl("/foo")), CoreMatchers.is("bar"));
    }

    @Test
    public void should_return_expected_response_with_file_based_on_specified_request() throws IOException {
        runWithConfiguration("foo.json");
        Assert.assertThat(helper.get(remoteUrl("/file")), CoreMatchers.is("foo.response"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_text_request() throws IOException {
        runWithConfiguration("foo.json");
        Assert.assertThat(helper.postContent(root(), "text_request"), CoreMatchers.is("response_for_text_request"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_file_request() throws IOException {
        runWithConfiguration("foo.json");
        Assert.assertThat(helper.postFile(root(), "foo.request"), CoreMatchers.is("response_for_file_request"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_get_request() throws IOException {
        runWithConfiguration("get_method.json");
        Assert.assertThat(helper.get(remoteUrl("/get")), CoreMatchers.is("response_for_get_method"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_while_request_non_get_request() throws IOException {
        runWithConfiguration("get_method.json");
        helper.postContent(remoteUrl("/get"), "");
    }

    @Test
    public void should_return_expected_response_based_on_specified_post_request() throws IOException {
        runWithConfiguration("post_method.json");
        Assert.assertThat(helper.postContent(remoteUrl("/post"), ""), CoreMatchers.is("response_for_post_method"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_while_request_non_post_request() throws IOException {
        runWithConfiguration("post_method.json");
        helper.get(remoteUrl("/post"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_put_request() throws IOException {
        runWithConfiguration("put_method.json");
        Request request = Request.Put(remoteUrl("/put"));
        Assert.assertThat(helper.executeAsString(request), CoreMatchers.is("response_for_put_method"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_delete_request() throws IOException {
        runWithConfiguration("delete_method.json");
        String response = helper.executeAsString(Request.Delete(remoteUrl("/delete")));
        Assert.assertThat(response, CoreMatchers.is("response_for_delete_method"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_version() throws IOException {
        runWithConfiguration("foo.json");
        Assert.assertThat(helper.getWithVersion(root(), HttpVersion.HTTP_1_0), CoreMatchers.is("version"));
    }

    @Test
    public void should_return_specified_version_for_request() throws IOException {
        runWithConfiguration("foo.json");
        ProtocolVersion version = helper.execute(Request.Get(remoteUrl("/version10"))).getProtocolVersion();
        Assert.assertThat(version.getProtocol(), CoreMatchers.is("HTTP"));
        Assert.assertThat(version.getMajor(), CoreMatchers.is(1));
        Assert.assertThat(version.getMinor(), CoreMatchers.is(0));
    }

    @Test
    public void should_return_expected_response_based_on_specified_header_request() throws IOException {
        runWithConfiguration("header.json");
        Assert.assertThat(helper.getWithHeader(remoteUrl("/header"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json")), CoreMatchers.is("response_for_header_request"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_unknown_header() throws IOException {
        runWithConfiguration("header.json");
        helper.get(remoteUrl("/header"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_query_request() throws IOException {
        runWithConfiguration("query.json");
        Assert.assertThat(helper.get(remoteUrl("/query?param=foo")), CoreMatchers.is("response_for_query_request"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_different_query_param() throws IOException {
        runWithConfiguration("query.json");
        helper.get(remoteUrl("/query?param2=foo"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_different_query_param_value() throws IOException {
        runWithConfiguration("query.json");
        helper.get(remoteUrl("/query?param=foo2"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_empty_query_request() throws IOException {
        runWithConfiguration("query.json");
        Assert.assertThat(helper.get(remoteUrl("/empty-query?param")), CoreMatchers.is("response_for_empty_query_request"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_multi_query_request() throws IOException {
        runWithConfiguration("query.json");
        Assert.assertThat(helper.get(remoteUrl("/multi-query?param1=foo&param2=bar")), CoreMatchers.is("response_for_multi_query_request"));
    }

    @Test
    public void should_expected_response_status_code() throws IOException {
        runWithConfiguration("foo.json");
        Assert.assertThat(helper.getForStatus(remoteUrl("/status")), CoreMatchers.is(200));
    }

    @Test
    public void should_expected_response_header() throws IOException {
        runWithConfiguration("foo.json");
        HttpResponse response = helper.getResponse(remoteUrl("/response_header"));
        Assert.assertThat(response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue(), CoreMatchers.is("application/json"));
        Assert.assertThat(response.getFirstHeader("foo").getValue(), CoreMatchers.is("bar"));
    }

    @Test
    public void should_run_as_proxy() throws IOException {
        runWithConfiguration("foo.json");
        HttpResponse response = helper.getResponse(remoteUrl("/proxy"));
        String value = response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
        Assert.assertThat(value, CoreMatchers.startsWith("text/plain"));
    }

    @Test
    public void should_expected_composite_response() throws IOException {
        runWithConfiguration("foo.json");
        HttpResponse response = helper.getResponse(remoteUrl("/composite-response"));
        Assert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.is(200));
        Assert.assertThat(response.getFirstHeader("foo").getValue(), CoreMatchers.is("bar"));
    }

    @Test
    public void should_wait_for_awhile() throws IOException {
        final long latency = 1000;
        final long delta = 200;
        runWithConfiguration("foo.json");
        long start = System.currentTimeMillis();
        int code = helper.getForStatus(remoteUrl("/latency"));
        long stop = System.currentTimeMillis();
        long gap = (stop - start) + delta;
        Assert.assertThat(gap, Matchers.greaterThan(latency));
        Assert.assertThat(code, CoreMatchers.is(200));
    }

    @Test
    public void should_wait_for_awhile_with_unit() throws IOException {
        final long latency = 1000;
        final long delta = 200;
        runWithConfiguration("foo.json");
        long start = System.currentTimeMillis();
        int code = helper.getForStatus(remoteUrl("/latency-with-unit"));
        long stop = System.currentTimeMillis();
        long gap = (stop - start) + delta;
        Assert.assertThat(gap, Matchers.greaterThan(latency));
        Assert.assertThat(code, CoreMatchers.is(200));
    }

    @Test
    public void should_match_form_value() throws IOException {
        runWithConfiguration("form.json");
        Request request = Request.Post(root()).bodyForm(new BasicNameValuePair("name", "dreamhead"));
        Assert.assertThat(helper.executeAsString(request), CoreMatchers.is("foobar"));
    }

    @Test
    public void should_have_favicon() throws IOException {
        runWithConfiguration("foo.json");
        String header = helper.getResponse(remoteUrl("/favicon.ico")).getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
        Assert.assertThat(header, CoreMatchers.is("image/png"));
    }
}

