package com.github.dreamhead.moco;


import com.google.common.io.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoPathResourceTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_path_resource_based_on_specified_request() throws IOException {
        runWithConfiguration("path_resource.json");
        Assert.assertThat(helper.get(remoteUrl("/path-resource")), CoreMatchers.is("response from path"));
    }

    @Test
    public void should_return_response_based_on_path_resource() throws IOException {
        runWithConfiguration("path_resource.json");
        URL resource = Resources.getResource("path.request");
        InputStream stream = resource.openStream();
        Assert.assertThat(helper.postStream(root(), stream), CoreMatchers.is("path resource"));
    }
}

