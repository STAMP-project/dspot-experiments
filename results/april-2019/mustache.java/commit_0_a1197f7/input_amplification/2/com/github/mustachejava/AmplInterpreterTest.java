package com.github.mustachejava;


import com.github.mustachejava.codes.CommentCode;
import com.github.mustachejava.codes.DefaultCode;
import com.github.mustachejava.codes.DefaultMustache;
import com.github.mustachejava.codes.ExtendCode;
import com.github.mustachejava.codes.PartialCode;
import com.github.mustachejava.reflect.SimpleObjectHandler;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import junit.framework.TestCase;


@SuppressWarnings("unused")
public class AmplInterpreterTest extends TestCase {
    protected File root;

    private static class LocalizedMustacheResolver extends DefaultResolver {
        private final Locale locale;

        LocalizedMustacheResolver(File root, Locale locale) {
            super(root);
            this.locale = locale;
        }

        @Override
        public Reader getReader(String resourceName) {
            int index = resourceName.lastIndexOf('.');
            String newResourceName;
            if (index == (-1)) {
                newResourceName = resourceName;
            } else {
                newResourceName = (((resourceName.substring(0, index)) + "_") + (locale.toLanguageTag())) + (resourceName.substring(index));
            }
            Reader reader = super.getReader(newResourceName);
            if (reader == null) {
                reader = super.getReader(resourceName);
            }
            return reader;
        }
    }

    private DefaultMustacheFactory createMustacheFactory() {
        return new DefaultMustacheFactory(root);
    }

    private class OkGenerator {
        public boolean isItOk() {
            return true;
        }
    }

    private String getOutput(final boolean setObjectHandler) {
        final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
        if (setObjectHandler) {
            mustacheFactory.setObjectHandler(new SimpleObjectHandler());
        }
        final Mustache defaultMustache = mustacheFactory.compile(new StringReader("{{#okGenerator.isItOk}}{{okGenerator.isItOk}}{{/okGenerator.isItOk}}"), "Test template");
        final Map<String, Object> params = new HashMap<>();
        params.put("okGenerator", new AmplInterpreterTest.OkGenerator());
        final Writer writer = new StringWriter();
        defaultMustache.execute(writer, params);
        return writer.toString();
    }

    public void testNestedLatches_add5582() throws IOException {
        DefaultMustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        c.setExecutorService(Executors.newCachedThreadPool());
        Mustache m = c.compile("latchedtest.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("latchedtest.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        m.execute(sw, new Object() {
            Callable<Object> nest = () -> {
                Thread.sleep(300);
                return "How";
            };

            Callable<Object> nested = () -> {
                Thread.sleep(200);
                return "are";
            };

            Callable<Object> nestest = () -> {
                Thread.sleep(100);
                return "you?";
            };
        });
        Writer execute = m.execute(sw, new Object() {
            Callable<Object> nest = () -> {
                Thread.sleep(300);
                return "How";
            };

            Callable<Object> nested = () -> {
                Thread.sleep(200);
                return "are";
            };

            Callable<Object> nestest = () -> {
                Thread.sleep(100);
                return "you?";
            };
        });
        execute.close();
        String o_testNestedLatches_add5582__39 = sw.toString();
        TestCase.assertEquals("<outer>\n<outer>\n<inner>How</inner>\n<inner>How</inner>\n<inner>are</inner>\n<inner>are</inner>\n<inner>you?</inner>\n</outer>\n<inner>you?</inner>\n</outer>\n", o_testNestedLatches_add5582__39);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("latchedtest.html", ((DefaultMustache) (m)).getName());
    }

    private StringWriter execute(String name, Object object) {
        MustacheFactory c = createMustacheFactory();
        Mustache m = c.compile(name);
        StringWriter sw = new StringWriter();
        m.execute(sw, object);
        return sw;
    }

    private StringWriter execute(String name, List<Object> objects) {
        MustacheFactory c = createMustacheFactory();
        Mustache m = c.compile(name);
        StringWriter sw = new StringWriter();
        m.execute(sw, objects);
        return sw;
    }

    public void testReadmeSerial_add2018_literalMutationString2187() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add2018__9 = m.execute(sw, new AmplInterpreterTest.Context());
        System.currentTimeMillis();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add2018__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2018__14);
        String o_testReadmeSerial_add2018__15 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2018__15);
        String String_15 = "Should be a little bit more than 4 seconds " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds 4001", String_15);
        boolean boolean_16 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2018__14);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add2018__15);
        TestCase.assertEquals("Should be a little bit more than 4 seconds 4001", String_15);
    }

    public void testReadmeParallel_add89413_add90291() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        sw.toString();
        sw.toString();
        String o_testReadmeParallel_add89413__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89413__15);
        String o_testReadmeParallel_add89413__16 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89413__16);
        String String_133 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_133);
        boolean boolean_134 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89413__15);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89413__16);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_133);
    }

    public void testReadmeParallel_add89413() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        sw.toString();
        String o_testReadmeParallel_add89413__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89413__15);
        String o_testReadmeParallel_add89413__16 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89413__16);
        String String_133 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_133);
        boolean boolean_134 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89413__15);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89413__16);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_133);
    }

    public void testReadmeParallel_add89411_remove90506() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add89411__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89411__15);
        String o_testReadmeParallel_add89411__16 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89411__16);
        String String_125 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_125);
        boolean boolean_126 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89411__15);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89411__16);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_125);
    }

    public void testReadmeParallel_add89409_literalMutationNumber89531() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add89409__17 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89409__17);
        String o_testReadmeParallel_add89409__18 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nName: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89409__18);
        String String_123 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 2002", String_123);
        boolean boolean_124 = (diff > 999) && (diff < 0);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89409__17);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nName: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89409__18);
        TestCase.assertEquals("Should be a little bit more than 1 second: 2002", String_123);
    }

    public void testReadmeParallel_add89408_add90338() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        System.currentTimeMillis();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add89408_add90338__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89408_add90338__15);
        String o_testReadmeParallel_add89408__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89408__15);
        String o_testReadmeParallel_add89408__16 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89408__16);
        String String_137 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_137);
        boolean boolean_138 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89408_add90338__15);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89408__15);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89408__16);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_137);
    }

    public void testReadmeParallel_add89410_remove90512() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add89410__16 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89410__16);
        String o_testReadmeParallel_add89410__17 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: ", o_testReadmeParallel_add89410__17);
        String String_135 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1", String_135);
        boolean boolean_136 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89410__16);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: ", o_testReadmeParallel_add89410__17);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1", String_135);
    }

    public void testReadmeParallel_add89409_remove90503() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add89409__17 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89409__17);
        String o_testReadmeParallel_add89409__18 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89409__18);
        String String_123 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_123);
        boolean boolean_124 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89409__17);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add89409__18);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_123);
    }

    static class Context {
        List<AmplInterpreterTest.Context.Item> items() {
            return Arrays.asList(new AmplInterpreterTest.Context.Item("Item 1", "$19.99", Arrays.asList(new AmplInterpreterTest.Context.Feature("New!"), new AmplInterpreterTest.Context.Feature("Awesome!"))), new AmplInterpreterTest.Context.Item("Item 2", "$29.99", Arrays.asList(new AmplInterpreterTest.Context.Feature("Old."), new AmplInterpreterTest.Context.Feature("Ugly."))));
        }

        static class Item {
            Item(String name, String price, List<AmplInterpreterTest.Context.Feature> features) {
                this.name = name;
                this.price = price;
                this.features = features;
            }

            String name;

            String price;

            List<AmplInterpreterTest.Context.Feature> features;
        }

        static class Feature {
            Feature(String description) {
                this.description = description;
            }

            String description;

            Callable<String> desc() throws InterruptedException {
                return () -> {
                    Thread.sleep(1000);
                    return description;
                };
            }
        }
    }

    public void testVariableInhertiance_literalMutationNumber3181_failAssert0null5104_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = -1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3181 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3181_failAssert0null5104 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3270_failAssert0null4970_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3270 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3270_failAssert0null4970 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3181_failAssert0_add4652_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = -1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = -1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3181 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3181_failAssert0_add4652 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3270_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiancenull3270 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3197_failAssert0_add4772_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        comments.isEmpty();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3197 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3197_failAssert0_add4772 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3271_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiancenull3271 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3210_failAssert0null5193_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(-1, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(null);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3210 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3210_failAssert0null5193 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3180_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 1; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3180 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3181_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = -1; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3181 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3210_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(-1, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3210 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3271_failAssert0_add4591_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                comments.get(i);
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3271 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3271_failAssert0_add4591 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3271_failAssert0_add4590_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            comments.size();
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3271 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3271_failAssert0_add4590 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3270_failAssert0_add4511_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object());
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3270 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3270_failAssert0_add4511 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3197_failAssert0_add4776_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3197 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3197_failAssert0_add4776 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3269_failAssert0_literalMutationString3636_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3269 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3269_failAssert0_literalMutationString3636 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3197_failAssert0null5197_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3197 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3197_failAssert0null5197 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3197_failAssert0_literalMutationString4136_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3197 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3197_failAssert0_literalMutationString4136 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3269_failAssert0_add4525_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3269 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3269_failAssert0_add4525 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3269_failAssert0_add4536_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3269 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3269_failAssert0_add4536 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3255_failAssert0_literalMutationNumber3546_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == 0) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove3255 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove3255_failAssert0_literalMutationNumber3546 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3255_failAssert0_literalMutationNumber3534_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove3255 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove3255_failAssert0_literalMutationNumber3534 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3271_failAssert0_literalMutationString3774_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("2");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3271 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3271_failAssert0_literalMutationString3774 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3186_failAssert0_add4846_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, -1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3186 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3186_failAssert0_add4846 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3185_failAssert0_literalMutationString3996_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("6");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3185 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3185_failAssert0_literalMutationString3996 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3274_failAssert0_add4569_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf(null);
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3274 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3274_failAssert0_add4569 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3270_failAssert0_literalMutationNumber3572_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3270 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3270_failAssert0_literalMutationNumber3572 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3198_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("Y");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3198 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3185_failAssert0_add4705_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            comments.size();
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 1, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3185 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3185_failAssert0_add4705 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3198_failAssert0_add4693_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("Y");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                mf.compile("issue_201/chat.html");
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3198 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3198_failAssert0_add4693 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3186_failAssert0null5264_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, -1, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3186 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3186_failAssert0null5264 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3198_failAssert0null5121_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("Y");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3198 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3198_failAssert0null5121 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3271_failAssert0_literalMutationNumber3776_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == 2) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3271 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3271_failAssert0_literalMutationNumber3776 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3197_failAssert0null5211_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("{{#qualification}}\n");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3197 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3197_failAssert0null5211 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3197_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("{{#qualification}}\n");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3197 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3255_failAssert0_add4478_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                comments.get(i);
                                                newcodes[i] = comments.get(i);
                                            }
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove3255 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove3255_failAssert0_add4478 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3198_failAssert0_literalMutationNumber3936_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("Y");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3198 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3198_failAssert0_literalMutationNumber3936 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString3198_failAssert0_add4676_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        mustache.getCodes();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("Y");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3198 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString3198_failAssert0_add4676 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3181_failAssert0_literalMutationString3925_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = -1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "F") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3181 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3181_failAssert0_literalMutationString3925 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3271_failAssert0null5043_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3271 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3271_failAssert0null5043 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3254_failAssert0_literalMutationString3501_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("S");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove3254 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove3254_failAssert0_literalMutationString3501 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3255_failAssert0_add4483_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            args.substring((index + 1));
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove3255 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove3255_failAssert0_add4483 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3269_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(null);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiancenull3269 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove3255_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_remove3255 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3271_failAssert0null5039_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, null, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3271 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3271_failAssert0null5039 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3274_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf(null);
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiancenull3274 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3180_failAssert0_add4734_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 1; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            args.indexOf("=");
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3180 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3180_failAssert0_add4734 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3274_failAssert0null5034_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf(null);
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3274 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3274_failAssert0null5034 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3269_failAssert0_literalMutationNumber3638_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == 2) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3269 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3269_failAssert0_literalMutationNumber3638 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3185_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, 1, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3185 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3186_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                        @Override
                        public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                            list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                @Override
                                public synchronized void init() {
                                    List<Code> comments = new ArrayList<>();
                                    for (Code code : mustache.getCodes()) {
                                        if (code instanceof CommentCode) {
                                            comments.add(code);
                                        }
                                    }
                                    super.init();
                                    Code[] codes = partial.getCodes();
                                    if (!(comments.isEmpty())) {
                                        Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                        for (int i = 0; i < (comments.size()); i++) {
                                            newcodes[i] = comments.get(i);
                                        }
                                        System.arraycopy(codes, -1, newcodes, comments.size(), codes.length);
                                        partial.setCodes(newcodes);
                                    }
                                }
                            });
                        }
                    };
                    visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                        int index = args.indexOf("=");
                        if (index == (-1)) {
                            throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                        }
                        String name = args.substring(0, index);
                        String value = args.substring((index + 1));
                        Map<String, String> variable = new HashMap<String, String>() {
                            {
                                put(name, value);
                            }
                        };
                        return new CommentCode(tc, null, "") {
                            @Override
                            public Writer execute(Writer writer, List<Object> scopes) {
                                scopes.add(variable);
                                return writer;
                            }
                        };
                    });
                    return visitor;
                }
            };
            Mustache m = mf.compile("issue_201/chat.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object()).close();
            sw.toString();
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3186 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber3210_failAssert0_add4759_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(-1, index);
                            args.substring((index + 1));
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3210 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber3210_failAssert0_add4759 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3270_failAssert0_add4508_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(code);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(null, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3270 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3270_failAssert0_add4508 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull3269_failAssert0null4997_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        DefaultMustacheVisitor visitor = new DefaultMustacheVisitor(this) {
                            @Override
                            public void extend(TemplateContext templateContext, String variable, Mustache mustache) {
                                list.add(new ExtendCode(templateContext, df, mustache, variable) {
                                    @Override
                                    public synchronized void init() {
                                        List<Code> comments = new ArrayList<>();
                                        for (Code code : mustache.getCodes()) {
                                            if (code instanceof CommentCode) {
                                                comments.add(null);
                                            }
                                        }
                                        super.init();
                                        Code[] codes = partial.getCodes();
                                        if (!(comments.isEmpty())) {
                                            Code[] newcodes = new Code[(comments.size()) + (codes.length)];
                                            for (int i = 0; i < (comments.size()); i++) {
                                                newcodes[i] = comments.get(i);
                                            }
                                            System.arraycopy(codes, 0, newcodes, comments.size(), codes.length);
                                            partial.setCodes(newcodes);
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("=");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, value);
                                }
                            };
                            return new CommentCode(tc, null, "") {
                                @Override
                                public Writer execute(Writer writer, List<Object> scopes) {
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
                        return visitor;
                    }
                };
                Mustache m = mf.compile("issue_201/chat.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull3269 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull3269_failAssert0null4997 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    private static class AccessTrackingMap extends HashMap<String, String> {
        Set<String> accessed = new HashSet<>();

        @Override
        public String get(Object key) {
            accessed.add(((String) (key)));
            return super.get(key);
        }

        void check() {
            Set<String> keyset = new HashSet<>(keySet());
            keyset.removeAll(accessed);
            if (!(keyset.isEmpty())) {
                throw new MustacheException("All keys in the map were not accessed");
            }
        }
    }

    private AmplInterpreterTest.AccessTrackingMap createBaseMap() {
        AmplInterpreterTest.AccessTrackingMap accessTrackingMap = new AmplInterpreterTest.AccessTrackingMap();
        accessTrackingMap.put("first", "Sam");
        accessTrackingMap.put("last", "Pullara");
        return accessTrackingMap;
    }

    private static class SuperClass {
        String values = "value";
    }

    private DefaultMustacheFactory initParallel() {
        DefaultMustacheFactory cf = createMustacheFactory();
        cf.setExecutorService(Executors.newCachedThreadPool());
        return cf;
    }

    protected void setUp() throws Exception {
        super.setUp();
        File file = new File("src/test/resources");
        root = (new File(file, "simple.html").exists()) ? file : new File("../src/test/resources");
    }
}

