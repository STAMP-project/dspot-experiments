package com.github.dreamhead.moco;


import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.http.HttpVersion.HTTP_1_0;


public class MocoTemplateStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_content_with_template() throws IOException {
        runWithConfiguration("template.json");
        Assert.assertThat(helper.get(remoteUrl("/template")), CoreMatchers.is("GET"));
    }

    @Test
    public void should_return_content_from_file_template() throws IOException {
        runWithConfiguration("template.json");
        Assert.assertThat(helper.get(remoteUrl("/file_template")), CoreMatchers.is("GET"));
    }

    @Test
    public void should_return_content_from_path_resource_template() throws IOException {
        runWithConfiguration("template.json");
        Assert.assertThat(helper.get(remoteUrl("/file_template")), CoreMatchers.is("GET"));
    }

    @Test
    public void should_return_version_from_template() throws IOException {
        runWithConfiguration("template.json");
        ProtocolVersion version = helper.execute(Request.Get(remoteUrl("/version_template")).version(HTTP_1_0)).getProtocolVersion();
        Assert.assertThat(version.toString(), CoreMatchers.is("HTTP/1.0"));
    }

    @Test
    public void should_return_header_from_template() throws IOException {
        runWithConfiguration("template.json");
        Header header = helper.execute(Request.Get(remoteUrl("/header_template")).addHeader("foo", "bar")).getFirstHeader("foo");
        Assert.assertThat(header.getValue(), CoreMatchers.is("bar"));
    }

    @Test
    public void should_return_cookie_from_template() throws IOException {
        runWithConfiguration("template.json");
        int status = helper.getForStatus(remoteUrl("/cookie_template"));
        Assert.assertThat(status, CoreMatchers.is(302));
        String content = helper.get(remoteUrl("/cookie_template"));
        Assert.assertThat(content, CoreMatchers.is("GET"));
    }

    @Test
    public void should_return_form_value_from_template() throws IOException {
        runWithConfiguration("template.json");
        Request request = Request.Post(remoteUrl("/form_template")).bodyForm(new BasicNameValuePair("foo", "dreamhead"));
        Assert.assertThat(helper.executeAsString(request), CoreMatchers.is("dreamhead"));
    }

    @Test
    public void should_return_query_value_from_template() throws IOException {
        runWithConfiguration("template.json");
        String content = helper.get(remoteUrl("/query_template?foo=dreamhead"));
        Assert.assertThat(content, CoreMatchers.is("dreamhead"));
    }

    @Test
    public void should_return_template_with_vars() throws IOException {
        runWithConfiguration("template_with_var.json");
        String content = helper.get(remoteUrl("/var_template"));
        Assert.assertThat(content, CoreMatchers.is("another template"));
    }

    @Test
    public void should_return_template_with_extractor() throws IOException {
        runWithConfiguration("template_with_extractor.json");
        String content = helper.postContent(remoteUrl("/extractor_template"), "{\"book\":{\"price\":\"1\"}}");
        Assert.assertThat(content, CoreMatchers.is("1"));
    }

    @Test
    public void should_return_file_with_template_name() throws IOException {
        runWithConfiguration("response_with_template_name.json");
        Assert.assertThat(helper.get(root()), CoreMatchers.is("foo.response"));
    }

    @Test
    public void should_return_json_field_from_template() throws IOException {
        runWithConfiguration("template.json");
        String content = helper.postContent(remoteUrl("/json_template"), "{\"foo\":\"bar\"}");
        Assert.assertThat(content, CoreMatchers.is("bar"));
    }

    @Test
    public void should_return_now_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Assert.assertThat(helper.get(remoteUrl("/now_template")), CoreMatchers.is(format.format(date)));
    }

    @Test
    public void should_return_random_with_range_and_format_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        String response = helper.get(remoteUrl("/random_template_with_range_and_format"));
        double result = Double.parseDouble(response);
        Assert.assertThat(result, Matchers.lessThan(100.0));
        Assert.assertThat(result, Matchers.greaterThan(0.0));
        String target = Iterables.get(Splitter.on('.').split(response), 1);
        Assert.assertThat(target.length(), Matchers.lessThanOrEqualTo(6));
    }

    @Test
    public void should_return_random_with_range_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        String response = helper.get(remoteUrl("/random_template_with_range"));
        double result = Double.parseDouble(response);
        Assert.assertThat(result, Matchers.lessThan(100.0));
        Assert.assertThat(result, Matchers.greaterThan(0.0));
    }

    @Test
    public void should_return_random_with_format_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        String response = helper.get(remoteUrl("/random_template_with_format"));
        String target = Iterables.get(Splitter.on('.').split(response), 1);
        Assert.assertThat(target.length(), Matchers.lessThanOrEqualTo(6));
    }

    @Test
    public void should_return_random_without_arg_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        String response = helper.get(remoteUrl("/random_template_without_arg"));
        double result = Double.parseDouble(response);
        Assert.assertThat(result, Matchers.lessThan(1.0));
        Assert.assertThat(result, Matchers.greaterThan(0.0));
    }
}

