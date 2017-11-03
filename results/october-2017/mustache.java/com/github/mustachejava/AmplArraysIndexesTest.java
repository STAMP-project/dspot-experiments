package com.github.mustachejava;


public class AmplArraysIndexesTest {
    private static class ArrayMap extends java.util.AbstractMap<Object, Object> implements Iterable<Object> {
        private final Object object;

        public ArrayMap(Object object) {
            this.object = object;
        }

        @Override
        public Object get(Object key) {
            try {
                int index = Integer.parseInt(key.toString());
                return java.lang.reflect.Array.get(object, index);
            } catch (NumberFormatException nfe) {
                return null;
            }
        }

        @Override
        public boolean containsKey(Object key) {
            return (get(key)) != null;
        }

        @Override
        public java.util.Set<Entry<Object, Object>> entrySet() {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.util.Iterator<Object> iterator() {
            return new java.util.Iterator<Object>() {
                int index = 0;

                int length = java.lang.reflect.Array.getLength(object);

                @Override
                public boolean hasNext() {
                    return (index) < (length);
                }

                @Override
                public Object next() {
                    return java.lang.reflect.Array.get(object, ((index)++));
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension() throws java.io.IOException {
        String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        Object scope = new Object() {
            String[] test = new String[]{ "a", "b", "c", "d" };
        };
        com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        DefaultMustacheFactory mf = new DefaultMustacheFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((DefaultMustacheFactory)mf).getExecutorService());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(100, ((int) (((DefaultMustacheFactory)mf).getRecursionLimit())));
        mf.setObjectHandler(oh);
        Mustache m = mf.compile(new java.io.StringReader(template), "template");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("template", ((com.github.mustachejava.codes.DefaultMustache)m).getName());
        java.io.StringWriter writer = new java.io.StringWriter();
        m.execute(writer, scope).flush();
        // AssertGenerator create local variable with return value of invocation
        String o_testArrayIndexExtension__32 = writer.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension__32);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((DefaultMustacheFactory)mf).getExecutorService());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(100, ((int) (((DefaultMustacheFactory)mf).getRecursionLimit())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("template", ((com.github.mustachejava.codes.DefaultMustache)m).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString38_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "T)-ef&bk*2") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new java.io.StringReader(template), "template");
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString38 should have thrown MustacheException");
        } catch (MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension_literalMutationString41 */
    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString41_failAssert4_literalMutationString1565() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#tet}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#tet}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n", result);
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((DefaultMustacheFactory)mf).getRecursionLimit())));
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new java.io.StringReader(template), "template");
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString41 should have thrown MustacheException");
        } catch (MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension_literalMutationString49 */
    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString49_failAssert6_literalMutationString2005() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n</ol>", template);
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "L)]_h(ex#Z^J-/R") + "    <li>d</li>\n") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\nL)]_h(ex#Z^J-/R    <li>d</li>\n</ol>", result);
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((DefaultMustacheFactory)mf).getRecursionLimit())));
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new java.io.StringReader(template), "template");
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString49 should have thrown MustacheException");
        } catch (MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString50_failAssert7() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{n/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new java.io.StringReader(template), "template");
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString50 should have thrown MustacheException");
        } catch (MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension_literalMutationString52_failAssert9() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new java.io.StringReader(template), "template");
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            org.junit.Assert.fail("testArrayIndexExtension_literalMutationString52 should have thrown MustacheException");
        } catch (MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension_sd158_failAssert12() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            String __DSPOT_name_2 = "w[&oDAIOw? O!T}Lq8xa";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new java.io.StringReader(template), "template");
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            // StatementAdd: add invocation of a method
            mf.compile(__DSPOT_name_2);
            org.junit.Assert.fail("testArrayIndexExtension_sd158 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension_sd158 */
    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension_sd158_failAssert12_add3413() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            String __DSPOT_name_2 = "w[&oDAIOw? O!T}Lq8xa";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((DefaultMustacheFactory)mf).getRecursionLimit())));
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new java.io.StringReader(template), "template");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("template", ((com.github.mustachejava.codes.DefaultMustache)m).getName());
            java.io.StringWriter writer = new java.io.StringWriter();
            // MethodCallAdder
            m.execute(writer, scope).flush();
            m.execute(writer, scope).flush();
            // AssertGenerator create local variable with return value of invocation
            String o_testArrayIndexExtension_sd158_failAssert12_add3413__38 = writer.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol><ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_sd158_failAssert12_add3413__38);
            // StatementAdd: add invocation of a method
            mf.compile(__DSPOT_name_2);
            org.junit.Assert.fail("testArrayIndexExtension_sd158 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension_sd158 */
    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension_sd158_failAssert12_literalMutationString3290() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            String __DSPOT_name_2 = "w[&oDAIOw? O!T}Lq8xa";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "{/ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n{/ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((DefaultMustacheFactory)mf).getRecursionLimit())));
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new java.io.StringReader(template), "template");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("template", ((com.github.mustachejava.codes.DefaultMustache)m).getName());
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            // AssertGenerator create local variable with return value of invocation
            String o_testArrayIndexExtension_sd158_failAssert12_literalMutationString3290__35 = writer.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n{/ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_sd158_failAssert12_literalMutationString3290__35);
            // StatementAdd: add invocation of a method
            mf.compile(__DSPOT_name_2);
            org.junit.Assert.fail("testArrayIndexExtension_sd158 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension_sd158 */
    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension_sd158_failAssert12_literalMutationString3401() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            String __DSPOT_name_2 = "w[&oDAIOw? O!T}Lq8xa";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "N" };
            };
            com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((DefaultMustacheFactory)mf).getRecursionLimit())));
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new java.io.StringReader(template), "template");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("template", ((com.github.mustachejava.codes.DefaultMustache)m).getName());
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            // AssertGenerator create local variable with return value of invocation
            String o_testArrayIndexExtension_sd158_failAssert12_literalMutationString3401__35 = writer.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>N</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>N</li>\n</ol>", o_testArrayIndexExtension_sd158_failAssert12_literalMutationString3401__35);
            // StatementAdd: add invocation of a method
            mf.compile(__DSPOT_name_2);
            org.junit.Assert.fail("testArrayIndexExtension_sd158 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension_sd159_failAssert13() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            String __DSPOT_s_3 = "l:7%uE_&Ml%;sG#Ahw*&";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new java.io.StringReader(template), "template");
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            writer.toString();
            // StatementAdd: add invocation of a method
            mf.compilePartial(__DSPOT_s_3);
            org.junit.Assert.fail("testArrayIndexExtension_sd159 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension_sd159 */
    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension_sd159_failAssert13_literalMutationString3542() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            String __DSPOT_s_3 = "l:7%uE_&Ml%;sG#Ahw*&";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "page1.txt") + "{{/test}}\n") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\npage1.txt{{/test}}\n</ol>", template);
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((DefaultMustacheFactory)mf).getRecursionLimit())));
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new java.io.StringReader(template), "template");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("template", ((com.github.mustachejava.codes.DefaultMustache)m).getName());
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            // AssertGenerator create local variable with return value of invocation
            String o_testArrayIndexExtension_sd159_failAssert13_literalMutationString3542__35 = writer.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\npage1.txtpage1.txtpage1.txtpage1.txt</ol>", o_testArrayIndexExtension_sd159_failAssert13_literalMutationString3542__35);
            // StatementAdd: add invocation of a method
            mf.compilePartial(__DSPOT_s_3);
            org.junit.Assert.fail("testArrayIndexExtension_sd159 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension_sd165 */
    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension_sd165_failAssert14_literalMutationString3751_literalMutationString12040_failAssert1() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                String __DSPOT_resourceName_7 = "%.UJum&)<4oK[>Va&1`i";
                String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "G&AaxJ7XHV&!)2q6NZ4&x2yR") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}\n") + "</ol>");
                String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
                Object scope = new Object() {
                    String[] test = new String[]{ "a", "b", "c", "d" };
                };
                com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                    @Override
                    public Object coerce(final Object object) {
                        if ((object != null) && (object.getClass().isArray())) {
                            return new ArrayMap(object);
                        }
                        return super.coerce(object);
                    }
                };
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(oh);
                Mustache m = mf.compile(new java.io.StringReader(template), "template");
                java.io.StringWriter writer = new java.io.StringWriter();
                m.execute(writer, scope).flush();
                // AssertGenerator create local variable with return value of invocation
                String o_testArrayIndexExtension_sd165_failAssert14_literalMutationString3751__35 = writer.toString();
                // StatementAdd: add invocation of a method
                mf.getReader(__DSPOT_resourceName_7);
                org.junit.Assert.fail("testArrayIndexExtension_sd165 should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("testArrayIndexExtension_sd165_failAssert14_literalMutationString3751_literalMutationString12040 should have thrown MustacheException");
        } catch (MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension_sd165 */
    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension_sd165_failAssert14_literalMutationString3752() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            String __DSPOT_resourceName_7 = "%.UJum&)<4oK[>Va&1`i";
            String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "page1.txt") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\npage1.txt</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
            String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
            Object scope = new Object() {
                String[] test = new String[]{ "a", "b", "c", "d" };
            };
            com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @Override
                public Object coerce(final Object object) {
                    if ((object != null) && (object.getClass().isArray())) {
                        return new ArrayMap(object);
                    }
                    return super.coerce(object);
                }
            };
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((DefaultMustacheFactory)mf).getExecutorService());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(100, ((int) (((DefaultMustacheFactory)mf).getRecursionLimit())));
            mf.setObjectHandler(oh);
            Mustache m = mf.compile(new java.io.StringReader(template), "template");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("template", ((com.github.mustachejava.codes.DefaultMustache)m).getName());
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            // AssertGenerator create local variable with return value of invocation
            String o_testArrayIndexExtension_sd165_failAssert14_literalMutationString3752__35 = writer.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\npage1.txt</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension_sd165_failAssert14_literalMutationString3752__35);
            // StatementAdd: add invocation of a method
            mf.getReader(__DSPOT_resourceName_7);
            org.junit.Assert.fail("testArrayIndexExtension_sd165 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }
}

