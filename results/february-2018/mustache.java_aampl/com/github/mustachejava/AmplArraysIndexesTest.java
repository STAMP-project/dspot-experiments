package com.github.mustachejava;


/**
 * Shows a simple way to add indexes for arrays.
 * <p/>
 * User: sam
 * Date: 4/7/13
 * Time: 11:12 AM
 */
public class AmplArraysIndexesTest {
    private static class ArrayMap extends java.util.AbstractMap<java.lang.Object, java.lang.Object> implements java.lang.Iterable<java.lang.Object> {
        private final java.lang.Object object;

        public ArrayMap(java.lang.Object object) {
            this.object = object;
        }

        @java.lang.Override
        public java.lang.Object get(java.lang.Object key) {
            try {
                int index = java.lang.Integer.parseInt(key.toString());
                return java.lang.reflect.Array.get(object, index);
            } catch (java.lang.NumberFormatException nfe) {
                return null;
            }
        }

        @java.lang.Override
        public boolean containsKey(java.lang.Object key) {
            return (get(key)) != null;
        }

        @java.lang.Override
        public java.util.Set<java.util.Map.Entry<java.lang.Object, java.lang.Object>> entrySet() {
            throw new java.lang.UnsupportedOperationException();
        }

        /**
         * Returns an iterator over a set of elements of type T.
         *
         * @return an Iterator.
         */
        @java.lang.Override
        public java.util.Iterator<java.lang.Object> iterator() {
            return new java.util.Iterator<java.lang.Object>() {
                int index = 0;

                int length = java.lang.reflect.Array.getLength(object);

                @java.lang.Override
                public boolean hasNext() {
                    return (index) < (length);
                }

                @java.lang.Override
                public java.lang.Object next() {
                    return java.lang.reflect.Array.get(object, ((index)++));
                }

                @java.lang.Override
                public void remove() {
                    throw new java.lang.UnsupportedOperationException();
                }
            };
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testArrayIndexExtension() throws java.io.IOException {
        java.lang.String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        java.lang.String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        java.lang.Object scope = new java.lang.Object() {
            java.lang.String[] test = new java.lang.String[]{ "a", "b", "c", "d" };
        };
        com.github.mustachejava.reflect.ReflectionObjectHandler oh = new com.github.mustachejava.reflect.ReflectionObjectHandler() {
            @java.lang.Override
            public java.lang.Object coerce(final java.lang.Object object) {
                if ((object != null) && (object.getClass().isArray())) {
                    return new com.github.mustachejava.AmplArraysIndexesTest.ArrayMap(object);
                }
                return super.coerce(object);
            }
        };
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
        mf.setObjectHandler(oh);
        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader(template), "template");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("template", ((com.github.mustachejava.codes.DefaultMustache)m).getName());
        java.io.StringWriter writer = new java.io.StringWriter();
        m.execute(writer, scope).flush();
        // AssertGenerator create local variable with return value of invocation
        java.lang.String o_testArrayIndexExtension__32 = writer.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", o_testArrayIndexExtension__32);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{{/test}}\n</ol>", template);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((com.github.mustachejava.DefaultMustacheFactory)mf).getExecutorService());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(100, ((int) (((com.github.mustachejava.DefaultMustacheFactory)mf).getRecursionLimit())));
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("template", ((com.github.mustachejava.codes.DefaultMustache)m).getName());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("<ol>\n    <li>b</li>\n    <li>a</li>\n    <li>d</li>\n</ol>\n<ol>\n    <li>a</li>\n    <li>b</li>\n    <li>c</li>\n    <li>d</li>\n</ol>", result);
    }
}

