package com.github.dreamhead.moco;


import com.github.dreamhead.moco.support.JsonSupport;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;


public class MocoJsonStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_expected_response_based_on_specified_json_request() throws IOException {
        runWithConfiguration("json.json");
        Assert.assertThat(helper.postContent(remoteUrl("/json"), "{\n\t\"foo\":\"bar\"\n}"), CoreMatchers.is("response_for_json_request"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_json_request_shortcut() throws IOException {
        runWithConfiguration("json.json");
        Assert.assertThat(helper.postContent(remoteUrl("/json_shortcut"), "{\n\t\"foo\":\"bar\"\n}"), CoreMatchers.is("response_for_json_shortcut"));
    }

    @Test
    public void should_return_expected_json_response_based_on_specified_json_request_shortcut() throws IOException, JSONException {
        runWithConfiguration("json.json");
        JsonSupport.assertEquals("{\"foo\":\"bar\"}", helper.getResponse(remoteUrl("/json_response_shortcut")));
    }

    @Test
    public void should_return_expected_reponse_based_on_json_path_request() throws IOException {
        runWithConfiguration("jsonpath.json");
        Assert.assertThat(helper.postContent(remoteUrl("/jsonpath"), "{\"book\":{\"price\":\"1\"}}"), CoreMatchers.is("response_for_json_path_request"));
    }
}

