package liquibase.util;


import org.junit.Assert;
import org.junit.Test;


public class SpringBootFatJarTest {
    @Test
    public void testGetPathForResourceWithTwoBangs() {
        String result = SpringBootFatJar.getPathForResource("some/path!/that/has!/two/bangs");
        Assert.assertEquals("that/has/two/bangs", result);
    }

    @Test
    public void testGetPathForResourceWithOneBang() {
        String result = SpringBootFatJar.getPathForResource("some/path!/that/has/one/bang");
        Assert.assertEquals("that/has/one/bang", result);
    }

    @Test
    public void testGetPathForResourceWithSimplePath() {
        String result = SpringBootFatJar.getPathForResource("some/simple/path");
        Assert.assertEquals("some/simple/path", result);
    }

    @Test
    public void testGetPathForResourceWithSpringBootFatJar() {
        String path = "jar:file:/some/fat.jar!/BOOT-INF/lib/some.jar!/db/changelogs";
        String result = SpringBootFatJar.getPathForResource(path);
        Assert.assertEquals("BOOT-INF/lib/some.jar", result);
        path = "jar:file:/some/fat.jar!/BOOT-INF/classes!/db/changelogs";
        result = SpringBootFatJar.getPathForResource(path);
        Assert.assertEquals("BOOT-INF/classes/db/changelogs", result);
    }

    @Test
    public void testGetSimplePathForResourceWithFatJarPath() {
        String result = SpringBootFatJar.getSimplePathForResources("/that/has/two/bangs/entryname", "some/path!/that/has!/two/bangs");
        Assert.assertEquals("two/bangs/entryname", result);
    }

    @Test
    public void testGetSimplePathForResourceWithSimplePath() {
        String result = SpringBootFatJar.getSimplePathForResources("/that/has/one/bang", "some/path!/that/has/one/bang");
        Assert.assertEquals("/that/has/one/bang", result);
    }

    @Test
    public void testGetSimplePathForResourceWithNestedJarPath() {
        String entryName = "BOOT-INF/lib/some.jar";
        String path = "jar:file:/some/fat.jar!/BOOT-INF/lib/some.jar!/db/changelogs";
        String result = SpringBootFatJar.getSimplePathForResources(entryName, path);
        Assert.assertEquals("db/changelogs", result);
    }
}

