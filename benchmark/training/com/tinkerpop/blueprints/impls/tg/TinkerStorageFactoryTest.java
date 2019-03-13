package com.tinkerpop.blueprints.impls.tg;


import TinkerGraph.FileType.GML;
import TinkerGraph.FileType.GRAPHML;
import TinkerGraph.FileType.GRAPHSON;
import TinkerGraph.FileType.JAVA;
import com.tinkerpop.blueprints.BaseTest;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Victor Su
 */
public class TinkerStorageFactoryTest extends BaseTest {
    @Test
    public void storageFactoryIsSingleton() {
        TinkerStorageFactory factory = TinkerStorageFactory.getInstance();
        Assert.assertSame(factory, TinkerStorageFactory.getInstance());
    }

    @Test
    public void testGMLStorage() throws IOException {
        final String path = ((getDirectory()) + "/") + "storage-test-gml";
        createDirectory(new File(path));
        TinkerStorage storage = TinkerStorageFactory.getInstance().getTinkerStorage(GML);
        TinkerGraph graph = TinkerGraphFactory.createTinkerGraph();
        storage.save(graph, path);
        Assert.assertEquals(1, TinkerStorageFactoryTest.findFilesByExt(path, "gml").length);
        Assert.assertEquals(1, TinkerStorageFactoryTest.findFilesByExt(path, "dat").length);
    }

    @Test
    public void testGraphMLStorage() throws IOException {
        final String path = ((getDirectory()) + "/") + "storage-test-graphml";
        createDirectory(new File(path));
        TinkerStorage storage = TinkerStorageFactory.getInstance().getTinkerStorage(GRAPHML);
        TinkerGraph graph = TinkerGraphFactory.createTinkerGraph();
        storage.save(graph, path);
        Assert.assertEquals(1, TinkerStorageFactoryTest.findFilesByExt(path, "xml").length);
        Assert.assertEquals(1, TinkerStorageFactoryTest.findFilesByExt(path, "dat").length);
    }

    @Test
    public void testGraphSONStorageFactory() throws IOException {
        final String path = ((getDirectory()) + "/") + "storage-test-graphson";
        createDirectory(new File(path));
        TinkerStorage storage = TinkerStorageFactory.getInstance().getTinkerStorage(GRAPHSON);
        TinkerGraph graph = TinkerGraphFactory.createTinkerGraph();
        storage.save(graph, path);
        Assert.assertEquals(1, TinkerStorageFactoryTest.findFilesByExt(path, "json").length);
        Assert.assertEquals(1, TinkerStorageFactoryTest.findFilesByExt(path, "dat").length);
    }

    @Test
    public void testJavaStorageFactory() throws IOException {
        final String path = ((getDirectory()) + "/") + "storage-test-java";
        createDirectory(new File(path));
        TinkerStorage storage = TinkerStorageFactory.getInstance().getTinkerStorage(JAVA);
        TinkerGraph graph = TinkerGraphFactory.createTinkerGraph();
        storage.save(graph, path);
        Assert.assertEquals(1, TinkerStorageFactoryTest.findFilesByExt(path, "dat").length);
    }
}

