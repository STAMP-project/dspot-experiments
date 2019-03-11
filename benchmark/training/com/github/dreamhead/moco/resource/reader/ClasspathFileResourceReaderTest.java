package com.github.dreamhead.moco.resource.reader;


import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.Request;
import com.google.common.base.Optional;
import java.nio.charset.Charset;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ClasspathFileResourceReaderTest {
    @Test
    public void should_return_class_path_file_content() {
        ClasspathFileResourceReader reader = new ClasspathFileResourceReader(Moco.text("foo.response"), Optional.<Charset>absent());
        Assert.assertThat(reader.readFor(Optional.<Request>absent()).toString(), CoreMatchers.is("foo.response"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_file_does_not_exist() {
        ClasspathFileResourceReader reader = new ClasspathFileResourceReader(Moco.text("unknown.response"), Optional.<Charset>absent());
        reader.readFor(Optional.<Request>absent());
    }
}

