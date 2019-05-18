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

    public void testNestedLatches_add22449() throws IOException {
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
        String o_testNestedLatches_add22449__39 = sw.toString();
        TestCase.assertEquals("<outer>\n<outer>\n<inner>How</inner>\n<inner>How</inner>\n<inner>are</inner>\n<inner>are</inner>\n<inner>you?</inner>\n</outer>\n<inner>you?</inner>\n</outer>\n", o_testNestedLatches_add22449__39);
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

    public void testReadmeParallel_add258387() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context());
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add258387__16 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add258387__16);
        String o_testReadmeParallel_add258387__17 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add258387__17);
        String String_167 = "Should be a little bit more than 1 second: " + diff;
        boolean boolean_168 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add258387__16);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\nNew!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add258387__17);
    }

    public void testReadmeParallel_add258386_remove259478_add262932() throws MustacheException, IOException {
        MustacheFactory c = initParallel();
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache o_testReadmeParallel_add258386_remove259478_add262932__3 = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeParallel_add258386_remove259478_add262932__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeParallel_add258386_remove259478_add262932__3)).getName());
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        m.execute(sw, new AmplInterpreterTest.Context()).close();
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeParallel_add258386__17 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add258386__17);
        String o_testReadmeParallel_add258386__18 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add258386__18);
        String String_153 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_153);
        boolean boolean_154 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (o_testReadmeParallel_add258386_remove259478_add262932__3)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (o_testReadmeParallel_add258386_remove259478_add262932__3)).getName());
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add258386__17);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add258386__18);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_153);
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

    public void testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653_failAssert0() throws IOException {
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
                                            comments.size();
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0null13025_failAssert0_literalMutationNumber16366_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0null13025 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0null13025_failAssert0_literalMutationNumber16366 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653_failAssert0null22007_failAssert0() throws IOException {
        try {
            {
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
                                                comments.size();
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
                                        put(null, value);
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036 should have thrown ArrayIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653_failAssert0null22007 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12078_failAssert0null21781_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12078 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12078_failAssert0null21781 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0null13026_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0null13026 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11125_failAssert0_literalMutationNumber11696_failAssert0_literalMutationNumber15016_failAssert0() throws IOException {
        try {
            {
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
                    m.execute(sw, new Object()).close();
                    sw.toString();
                    junit.framework.TestCase.fail("testVariableInhertiancenull11125 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_literalMutationNumber11696 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_literalMutationNumber11696_failAssert0_literalMutationNumber15016 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0null13025_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0null13025 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0null12734_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0null12734 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11125_failAssert0_literalMutationNumber11696_failAssert0() throws IOException {
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
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull11125 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_literalMutationNumber11696 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11124_failAssert0_add12315_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiancenull11124 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11124_failAssert0_add12315 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0_add12297_failAssert0() throws IOException {
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
                m.execute(sw, new Object());
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_add12297 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0_add12297_failAssert0null21023_failAssert0() throws IOException {
        try {
            {
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
                    m.execute(sw, new Object());
                    m.execute(sw, new Object()).close();
                    sw.toString();
                    junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_add12297 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_add12297_failAssert0null21023 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12078_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12078 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11052_failAssert0null12898_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052_failAssert0null12898 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072_failAssert0_literalMutationString14507_failAssert0() throws IOException {
        try {
            {
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
                                int index = args.indexOf("=");
                                if (index == (-1)) {
                                    throw new MustacheException("Pragme \'se\' must have varname=value as an argument");
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072_failAssert0_literalMutationString14507 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_add12632_failAssert0() throws IOException {
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
                                    scopes.add(variable);
                                    return writer;
                                }
                            };
                        });
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_add12632 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11125_failAssert0_add12440_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiancenull11125 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_add12440 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0null13026_failAssert0_add18095_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0null13026 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0null13026_failAssert0_add18095 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0_literalMutationString11404_failAssert0() throws IOException {
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
                                throw new MustacheException("");
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
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_literalMutationString11404 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072_failAssert0null20744_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072_failAssert0null20744 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11036_failAssert0_add12644_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_add12644 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11052_failAssert0null12898_failAssert0_add19619_failAssert0() throws IOException {
        try {
            {
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
                    mf.compile("issue_201/chat.html");
                    Mustache m = mf.compile("issue_201/chat.html");
                    StringWriter sw = new StringWriter();
                    m.execute(sw, new Object()).close();
                    sw.toString();
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052_failAssert0null12898 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052_failAssert0null12898_failAssert0_add19619 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11052_failAssert0_literalMutationNumber11780_failAssert0() throws IOException {
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
                            String value = args.substring((index + 0));
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052_failAssert0_literalMutationNumber11780 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11124_failAssert0null12754_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiancenull11124 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11124_failAssert0null12754 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0null13026_failAssert0null20376_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0null13026 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0null13026_failAssert0null20376 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11036_failAssert0null13047_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0null13047 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0_add12297_failAssert0_literalMutationNumber15128_failAssert0() throws IOException {
        try {
            {
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
                    m.execute(sw, new Object());
                    m.execute(sw, new Object()).close();
                    sw.toString();
                    junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_add12297 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_add12297_failAssert0_literalMutationNumber15128 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12078_failAssert0_add19780_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12078 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12078_failAssert0_add19780 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0null12737_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0null12737 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11129_failAssert0null12768_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiancenull11129 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11129_failAssert0null12768 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11040_failAssert0null13083_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11040 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11040_failAssert0null13083 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11041_failAssert0null12965_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11041 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11041_failAssert0null12965 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0null13025_failAssert0_add19468_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0null13025 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0null13025_failAssert0_add19468 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_add12632_failAssert0null21156_failAssert0() throws IOException {
        try {
            {
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
                                        scopes.add(variable);
                                        return writer;
                                    }
                                };
                            });
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_add12632 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_add12632_failAssert0null21156 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072_failAssert0_add18540_failAssert0() throws IOException {
        try {
            {
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
                                                    comments.get(i);
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072_failAssert0_add18540 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11052_failAssert0_literalMutationString11761_failAssert0() throws IOException {
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
                            int index = args.indexOf("{{#qu6alification}}\n");
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052_failAssert0_literalMutationString11761 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11041_failAssert0_add12562_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11041 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11041_failAssert0_add12562 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11036_failAssert0null13042_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0null13042 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_add12639_failAssert0() throws IOException {
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
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_add12639 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11125_failAssert0null12863_failAssert0() throws IOException {
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
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull11125 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0null12863 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_add12632_failAssert0_literalMutationNumber15470_failAssert0() throws IOException {
        try {
            {
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
                                        scopes.add(variable);
                                        return writer;
                                    }
                                };
                            });
                            visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                                int index = args.indexOf("=");
                                if (index == (-1)) {
                                    throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                                }
                                String name = args.substring(-1, index);
                                String value = args.substring((index + 0));
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_add12632 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_add12632_failAssert0_literalMutationNumber15470 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0_add12297_failAssert0_add18870_failAssert0() throws IOException {
        try {
            {
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
                    m.execute(sw, new Object());
                    m.execute(sw, new Object()).close();
                    sw.toString();
                    sw.toString();
                    junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_add12297 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_add12297_failAssert0_add18870 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653_failAssert0_add20021_failAssert0() throws IOException {
        try {
            {
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
                                                comments.size();
                                                comments.size();
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036 should have thrown ArrayIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653_failAssert0_add20021 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11129_failAssert0_literalMutationNumber11490_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiancenull11129 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11129_failAssert0_literalMutationNumber11490 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11053_failAssert0_literalMutationNumber11797_failAssert0() throws IOException {
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
                                        }
                                    }
                                });
                            }
                        };
                        visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                            int index = args.indexOf("E");
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11053 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11053_failAssert0_literalMutationNumber11797 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11124_failAssert0_add12315_failAssert0_literalMutationNumber16076_failAssert0() throws IOException {
        try {
            {
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
                                args.substring((index + 0));
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
                    junit.framework.TestCase.fail("testVariableInhertiancenull11124 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiancenull11124_failAssert0_add12315 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11124_failAssert0_add12315_failAssert0_literalMutationNumber16076 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11124_failAssert0_add12315_failAssert0null21413_failAssert0() throws IOException {
        try {
            {
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
                                args.substring((index + 1));
                                String value = args.substring((index + 1));
                                Map<String, String> variable = new HashMap<String, String>() {
                                    {
                                        put(null, value);
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
                    junit.framework.TestCase.fail("testVariableInhertiancenull11124 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiancenull11124_failAssert0_add12315 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11124_failAssert0_add12315_failAssert0null21413 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072_failAssert0_add18545_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072_failAssert0_add18545 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0null12734_failAssert0_add17954_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0null12734 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0null12734_failAssert0_add17954 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11053_failAssert0null12915_failAssert0() throws IOException {
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
                            int index = args.indexOf("E");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(0, index);
                            String value = args.substring((index + 1));
                            Map<String, String> variable = new HashMap<String, String>() {
                                {
                                    put(name, null);
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11053 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11053_failAssert0null12915 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11040_failAssert0_add12707_failAssert0() throws IOException {
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
                m.execute(sw, new Object()).close();
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11040 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11040_failAssert0_add12707 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11129_failAssert0null12784_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiancenull11129 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11129_failAssert0null12784 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11036_failAssert0_literalMutationString12135_failAssert0() throws IOException {
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
                            int index = args.indexOf("#");
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_literalMutationString12135 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11125_failAssert0_literalMutationNumber11696_failAssert0null20965_failAssert0() throws IOException {
        try {
            {
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
                                        put(name, null);
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
                    junit.framework.TestCase.fail("testVariableInhertiancenull11125 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_literalMutationNumber11696 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_literalMutationNumber11696_failAssert0null20965 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0_literalMutationString11404_failAssert0_literalMutationNumber14210_failAssert0() throws IOException {
        try {
            {
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
                                                partial.setCodes(newcodes);
                                            }
                                        }
                                    });
                                }
                            };
                            visitor.addPragmaHandler("set", ( tc, pragma, args) -> {
                                int index = args.indexOf("=");
                                if (index == (-1)) {
                                    throw new MustacheException("");
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
                    junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_literalMutationString11404 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_literalMutationString11404_failAssert0_literalMutationNumber14210 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11036_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11052_failAssert0_add12471_failAssert0() throws IOException {
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
                                            comments.size();
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052_failAssert0_add12471 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11125_failAssert0_add12440_failAssert0null21374_failAssert0() throws IOException {
        try {
            {
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
                                        put(null, value);
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
                    junit.framework.TestCase.fail("testVariableInhertiancenull11125 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_add12440 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_add12440_failAssert0null21374 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12078_failAssert0_literalMutationString16978_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12078 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12078_failAssert0_literalMutationString16978 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0null12734_failAssert0null20258_failAssert0() throws IOException {
        try {
            {
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
                                        put(null, value);
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
                    junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0null12734 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0null12734_failAssert0null20258 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11052_failAssert0_add12477_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052_failAssert0_add12477 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11052_failAssert0null12885_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052_failAssert0null12885 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11126_failAssert0null12820_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiancenull11126 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11126_failAssert0null12820 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0null13025_failAssert0null21546_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0null13025 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0null13025_failAssert0null21546 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11124_failAssert0_literalMutationNumber11435_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiancenull11124 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11124_failAssert0_literalMutationNumber11435 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11052_failAssert0null12898_failAssert0null21651_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052 should have thrown MustacheException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052_failAssert0null12898 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052_failAssert0null12898_failAssert0null21651 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0_literalMutationString11404_failAssert0_add18411_failAssert0() throws IOException {
        try {
            {
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
                                    throw new MustacheException("");
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
                    junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_literalMutationString11404 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_literalMutationString11404_failAssert0_add18411 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11040_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11040 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11053_failAssert0_add12504_failAssert0() throws IOException {
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
                            int index = args.indexOf("E");
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11053 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11053_failAssert0_add12504 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11041_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11041 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11125_failAssert0_literalMutationNumber11696_failAssert0_add18792_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiancenull11125 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_literalMutationNumber11696 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_literalMutationNumber11696_failAssert0_add18792 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11053_failAssert0_add12506_failAssert0() throws IOException {
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
                            int index = args.indexOf("E");
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11053 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11053_failAssert0_add12506 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653_failAssert0_add20015_failAssert0() throws IOException {
        try {
            {
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
                                                for (int i = -1; i < (comments.size()); i++) {
                                                    newcodes[i] = comments.get(i);
                                                }
                                                comments.size();
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036 should have thrown ArrayIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653_failAssert0_add20015 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_add12632_failAssert0_add18997_failAssert0() throws IOException {
        try {
            {
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
                                                comments.size();
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_add12632 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_add12632_failAssert0_add18997 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653_failAssert0_literalMutationNumber17500_failAssert0() throws IOException {
        try {
            {
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
                                                comments.size();
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036 should have thrown ArrayIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653_failAssert0_literalMutationNumber17500 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653_failAssert0_literalMutationNumber17503_failAssert0() throws IOException {
        try {
            {
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
                                                comments.size();
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036 should have thrown ArrayIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_add12653_failAssert0_literalMutationNumber17503 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0null12734_failAssert0_literalMutationString13315_failAssert0() throws IOException {
        try {
            {
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
                                int index = args.indexOf("l");
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
                    junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0null12734 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0null12734_failAssert0_literalMutationString13315 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11125_failAssert0_add12440_failAssert0_literalMutationNumber15980_failAssert0() throws IOException {
        try {
            {
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
                                String value = args.substring((index + 2));
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
                    junit.framework.TestCase.fail("testVariableInhertiancenull11125 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_add12440 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_add12440_failAssert0_literalMutationNumber15980 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0_add12292_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_add12292 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11053_failAssert0() throws IOException {
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
                        int index = args.indexOf("E");
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
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11053 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11053_failAssert0null12905_failAssert0() throws IOException {
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
                            int index = args.indexOf("E");
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11053 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11053_failAssert0null12905 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove11110_failAssert0_literalMutationNumber11401_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiance_remove11110 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_remove11110_failAssert0_literalMutationNumber11401 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11036_failAssert0_literalMutationString12145_failAssert0() throws IOException {
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
                                throw new MustacheException("NbVu#KG@2 qY(efFp%P!L(`0F@_+g0<nRRrk [W]6ZYq48#a*-M");
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11036_failAssert0_literalMutationString12145 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11126_failAssert0_add12390_failAssert0() throws IOException {
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
                sw.toString();
                junit.framework.TestCase.fail("testVariableInhertiancenull11126 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11126_failAssert0_add12390 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11129_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiancenull11129 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11129_failAssert0_add12331_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiancenull11129 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11129_failAssert0_add12331 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11124_failAssert0_add12315_failAssert0_add19324_failAssert0() throws IOException {
        try {
            {
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
                                args.substring((index + 1));
                                String value = args.substring((index + 1));
                                Map<String, String> variable = new HashMap<String, String>() {
                                    {
                                        put(name, value);
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
                    junit.framework.TestCase.fail("testVariableInhertiancenull11124 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiancenull11124_failAssert0_add12315 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11124_failAssert0_add12315_failAssert0_add19324 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11129_failAssert0_add12334_failAssert0() throws IOException {
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
                junit.framework.TestCase.fail("testVariableInhertiancenull11129 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11129_failAssert0_add12334 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11125_failAssert0_add12440_failAssert0_add19272_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiancenull11125 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_add12440 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testVariableInhertiancenull11125_failAssert0_add12440_failAssert0_add19272 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11052_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11052 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11126_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiancenull11126 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11125_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiancenull11125 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072_failAssert0null20737_failAssert0() throws IOException {
        try {
            {
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
                    junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065 should have thrown StringIndexOutOfBoundsException");
                }
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072 should have thrown StringIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11065_failAssert0_literalMutationNumber12072_failAssert0null20737 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull11124_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiancenull11124 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString11053_failAssert0_literalMutationNumber11820_failAssert0() throws IOException {
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
                            int index = args.indexOf("E");
                            if (index == (-1)) {
                                throw new MustacheException("Pragme 'set' must have varname=value as an argument");
                            }
                            String name = args.substring(1, index);
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11053 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString11053_failAssert0_literalMutationNumber11820 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber11041_failAssert0_literalMutationNumber11938_failAssert0() throws IOException {
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
                                            System.arraycopy(codes, -2, newcodes, comments.size(), codes.length);
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
                junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11041 should have thrown ArrayIndexOutOfBoundsException");
            }
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber11041_failAssert0_literalMutationNumber11938 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
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

