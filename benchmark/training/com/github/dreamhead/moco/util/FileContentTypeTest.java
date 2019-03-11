package com.github.dreamhead.moco.util;


import com.google.common.base.Optional;
import com.google.common.net.MediaType;
import java.nio.charset.Charset;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class FileContentTypeTest {
    @Test
    public void should_get_type_from_filename() {
        FileContentType contentType = new FileContentType("logo.png");
        Assert.assertThat(contentType.getContentType(), CoreMatchers.is(MediaType.PNG));
    }

    @Test
    public void should_get_default_type_from_unknown_name() {
        FileContentType contentType = new FileContentType("UNKNOWN_FILE");
        Assert.assertThat(contentType.getContentType(), CoreMatchers.is(MediaType.PLAIN_TEXT_UTF_8));
    }

    @Test
    public void should_have_charset_for_file() {
        Charset gbk = Charset.forName("gbk");
        FileContentType contentType = new FileContentType("result.response", Optional.of(gbk));
        Assert.assertThat(contentType.getContentType(), CoreMatchers.is(MediaType.create("text", "plain").withCharset(gbk)));
    }

    @Test
    public void should_have_charset_for_css_file() {
        FileContentType contentType = new FileContentType("result.css");
        Assert.assertThat(contentType.getContentType(), CoreMatchers.is(MediaType.create("text", "css")));
    }
}

