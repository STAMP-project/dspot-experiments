package com.fishercoder;


import _588.FileSystem;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/23/17.
 */
public class _588Test {
    private static FileSystem fileSystem;

    @Test
    public void test1() {
        _588Test.fileSystem = new FileSystem();
        List<String> list = new ArrayList<>();
        Assert.assertEquals(list, _588Test.fileSystem.ls("/"));
        _588Test.fileSystem.mkdir("/a/b/c");
        _588Test.fileSystem.addContentToFile("/a/b/c/d", "hello");
        list.add("a");
        Assert.assertEquals(list, _588Test.fileSystem.ls("/"));
        Assert.assertEquals("hello", _588Test.fileSystem.readContentFromFile("/a/b/c/d"));
    }

    @Test
    public void test2() {
        _588Test.fileSystem = new FileSystem();
        List<String> list = new ArrayList<>();
        Assert.assertEquals(list, _588Test.fileSystem.ls("/"));
        _588Test.fileSystem.mkdir("/a/b/c");
        list.add("c");
        Assert.assertEquals(list, _588Test.fileSystem.ls("/a/b"));
    }
}

