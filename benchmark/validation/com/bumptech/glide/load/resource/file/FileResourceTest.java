package com.bumptech.glide.load.resource.file;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class FileResourceTest {
    private File file;

    private FileResource resource;

    @Test
    public void testReturnsGivenFile() {
        Assert.assertEquals(file, resource.get());
    }
}

