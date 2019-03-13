package com.intuit.karate.junit4.files;


import com.intuit.karate.FileUtils;
import com.intuit.karate.Resource;
import com.intuit.karate.Runner;
import com.intuit.karate.core.Feature;
import com.intuit.karate.core.FeatureParser;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author pthomas3
 */
public class JarLoadingTest {
    private static final Logger logger = LoggerFactory.getLogger(JarLoadingTest.class);

    @Test
    public void testRunningFromJarFile() throws Exception {
        ClassLoader cl = JarLoadingTest.getJarClassLoader();
        Class main = cl.loadClass("demo.jar1.Main");
        Method meth = main.getMethod("hello");
        Object result = meth.invoke(null);
        Assert.assertEquals("hello world", result);
        List<Resource> list = FileUtils.scanForFeatureFiles(Collections.singletonList("classpath:demo"), cl);
        Assert.assertEquals(4, list.size());
        JarLoadingTest.logger.debug("resources: {}", list);
        list = FileUtils.scanForFeatureFiles(Collections.singletonList("classpath:demo/jar1/caller.feature"), cl);
        Assert.assertEquals(1, list.size());
        Resource resource = list.get(0);
        Assert.assertTrue(FileUtils.isJarPath(resource.getPath().toUri()));
        Path path = FileUtils.fromRelativeClassPath("classpath:demo/jar1/caller.feature", cl);
        String relativePath = FileUtils.toRelativeClassPath(path, cl);
        Assert.assertEquals("classpath:demo/jar1/caller.feature", relativePath);
        Feature feature = FeatureParser.parse(resource);
        Map<String, Object> map = Runner.runFeature(feature, null, false);
        Assert.assertEquals(true, map.get("success"));
    }

    @Test
    public void testFileUtilsForJarFile() throws Exception {
        File file = new File("src/test/java/common.feature");
        Assert.assertTrue((!(FileUtils.isJarPath(file.toPath().toUri()))));
        ClassLoader cl = JarLoadingTest.getJarClassLoader();
        Class main = cl.loadClass("demo.jar1.Main");
        Path path = FileUtils.getPathContaining(main);
        Assert.assertTrue(FileUtils.isJarPath(path.toUri()));
        String relativePath = FileUtils.toRelativeClassPath(path, cl);
        Assert.assertEquals("classpath:", relativePath);// TODO doesn't matter but fix in future if possible

        path = FileUtils.fromRelativeClassPath("classpath:demo/jar1", cl);
        Assert.assertEquals(path.toString(), "/demo/jar1");
    }

    @Test
    public void testUsingKarateBase() throws Exception {
        String relativePath = "classpath:demo/jar1/caller.feature";
        ClassLoader cl = JarLoadingTest.getJarClassLoader();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Callable<Boolean>> list = new ArrayList();
        for (int i = 0; i < 10; i++) {
            list.add(() -> {
                Path path = FileUtils.fromRelativeClassPath(relativePath, cl);
                JarLoadingTest.logger.debug("path: {}", path);
                Resource resource = new Resource(path, relativePath);
                Feature feature = FeatureParser.parse(resource);
                Map<String, Object> map = Runner.runFeature(feature, null, true);
                Boolean result = ((Boolean) (map.get("success")));
                JarLoadingTest.logger.debug("done: {}", result);
                return result;
            });
        }
        List<Future<Boolean>> futures = executor.invokeAll(list);
        for (Future<Boolean> f : futures) {
            Assert.assertTrue(f.get());
        }
        executor.shutdownNow();
    }
}

