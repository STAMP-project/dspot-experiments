package cucumber.runtime.io;


import java.io.File;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;


public class FileResourceTest {
    @Test
    public void for_classpath_files_get_path_should_return_relative_path_from_classpath_root() {
        FileResource toTest1 = FileResource.createClasspathFileResource(new File("/testPath"), new File("/testPath/test1/test.feature"));
        FileResource toTest2 = FileResource.createClasspathFileResource(new File("testPath"), new File("testPath/test1/test.feature"));
        Assert.assertEquals(URI.create("classpath:test1/test.feature"), toTest1.getPath());
        Assert.assertEquals(URI.create("classpath:test1/test.feature"), toTest2.getPath());
    }

    @Test
    public void for_filesystem_files_get_path_should_return_the_path() {
        // setup
        FileResource toTest1 = FileResource.createFileResource(new File("test1"), new File("test1/test.feature"));
        FileResource toTest2 = FileResource.createFileResource(new File("test1/test.feature"), new File("test1/test.feature"));
        // test
        Assert.assertEquals(URI.create("file:test.feature"), toTest1.getPath());
        Assert.assertEquals(new File("test1/test.feature").toURI(), toTest2.getPath());
    }
}

