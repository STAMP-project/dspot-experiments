package com.github.dreamhead.moco.resource.reader;


import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.Request;
import com.google.common.base.Optional;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class FileResourceReaderTest {
    @Test
    public void should_return_class_path_file_content() {
        FileResourceReader reader = new FileResourceReader(Moco.text(new File("src/test/resources/foo.response").getPath()));
        Assert.assertThat(reader.readFor(Optional.<Request>absent()).toString(), CoreMatchers.is("foo.response"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_file_does_not_exist() {
        FileResourceReader reader = new FileResourceReader(Moco.text(new File("src/test/resources/unknown.response").getPath()));
        reader.readFor(Optional.<Request>absent());
    }
}

