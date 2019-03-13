package com.github.dreamhead.moco;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoAttachmentStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_attach_text_attchment() throws IOException {
        runWithConfiguration("attachment.json");
        Assert.assertThat(helper.get(remoteUrl("/text_attachment")), CoreMatchers.is("text_attachment"));
    }

    @Test
    public void should_attach_file_attchment() throws IOException {
        runWithConfiguration("attachment.json");
        Assert.assertThat(helper.get(remoteUrl("/file_attachment")), CoreMatchers.is("foo.response"));
    }

    @Test
    public void should_attach_path_attchment() throws IOException {
        runWithConfiguration("attachment.json");
        Assert.assertThat(helper.get(remoteUrl("/path_attachment")), CoreMatchers.is("response from path"));
    }
}

