package com.github.dreamhead.moco;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoXmlStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_expected_response_based_on_specified_xpath_request() throws IOException {
        runWithConfiguration("xpath.json");
        Assert.assertThat(helper.postFile(remoteUrl("/xpath"), "foo.xml"), CoreMatchers.is("response_for_xpath_request"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_unknown_xpath_request() throws IOException {
        runWithConfiguration("xpath.json");
        helper.postFile(remoteUrl("/xpath"), "bar.xml");
    }

    @Test
    public void should_return_expected_response_based_on_specified_xml_request() throws IOException {
        runWithConfiguration("xml.json");
        Assert.assertThat(helper.postFile(remoteUrl("/xml"), "foo.xml"), CoreMatchers.is("response_for_xml_request"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_xml_file_request() throws IOException {
        runWithConfiguration("xml.json");
        Assert.assertThat(helper.postFile(remoteUrl("/xmlfile"), "foo.xml"), CoreMatchers.is("response_for_xml_file_request"));
    }
}

