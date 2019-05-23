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

    public void testReadmeParallel_add6051() throws MustacheException, IOException {
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
        String o_testReadmeParallel_add6051__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6051__14);
        String o_testReadmeParallel_add6051__15 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6051__15);
        String o_testReadmeParallel_add6051__16 = sw.toString();
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6051__16);
        String String_155 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_155);
        boolean boolean_156 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6051__14);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6051__15);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add6051__16);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1002", String_155);
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

    public void testVariableInhertiance_literalMutationNumber224_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber224 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull308_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiancenull308 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_remove294_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiance_remove294 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber225_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber225 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber219_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber219 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString236_failAssert0() throws IOException {
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
                        int index = args.indexOf("Q");
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
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString236 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationString237_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationString237 should have thrown MustacheException");
        } catch (MustacheException expected) {
            TestCase.assertEquals("Pragme \'set\' must have varname=value as an argument", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull310_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiancenull310 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber220_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber220 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            TestCase.assertEquals("-1", expected.getMessage());
        }
    }

    public void testVariableInhertiance_literalMutationNumber249_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiance_literalMutationNumber249 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            TestCase.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    public void testVariableInhertiancenull313_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiancenull313 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testVariableInhertiancenull309_failAssert0() throws IOException {
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
            junit.framework.TestCase.fail("testVariableInhertiancenull309 should have thrown NullPointerException");
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

