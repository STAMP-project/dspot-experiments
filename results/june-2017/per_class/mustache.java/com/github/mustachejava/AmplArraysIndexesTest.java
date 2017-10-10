

package com.github.mustachejava;


/**
 * Shows a simple way to add indexes for arrays.
 * <p/>
 * User: sam
 * Date: 4/7/13
 * Time: 11:12 AM
 */
public class AmplArraysIndexesTest {
    @org.junit.Test
    public void testArrayIndexExtension() throws java.io.IOException {
        java.lang.String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        java.lang.String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        java.lang.Object scope = new java.lang.Object() {
            java.lang.String[] test = new java.lang.String[]{ "a" , "b" , "c" , "d" };
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
        mf.setObjectHandler(oh);
        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader(template), "template");
        java.io.StringWriter writer = new java.io.StringWriter();
        m.execute(writer, scope).flush();
        org.junit.Assert.assertEquals(result, writer.toString());
    }

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

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    @org.junit.Test
    public void testArrayIndexExtension_literalMutation43_failAssert41() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "") + "</ol>");
            java.lang.String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            java.lang.Object scope = new java.lang.Object() {
                java.lang.String[] test = new java.lang.String[]{ "a" , "b" , "c" , "d" };
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
            mf.setObjectHandler(oh);
            com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader(template), "template");
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            // MethodAssertGenerator build local variable
            Object o_32_0 = writer.toString();
            org.junit.Assert.fail("testArrayIndexExtension_literalMutation43 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    @org.junit.Test
    public void testArrayIndexExtension_literalMutation34_failAssert32() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "T)-ef&bk*2") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
            java.lang.String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            java.lang.Object scope = new java.lang.Object() {
                java.lang.String[] test = new java.lang.String[]{ "a" , "b" , "c" , "d" };
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
            mf.setObjectHandler(oh);
            com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader(template), "template");
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            // MethodAssertGenerator build local variable
            Object o_32_0 = writer.toString();
            org.junit.Assert.fail("testArrayIndexExtension_literalMutation34 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    @org.junit.Test
    public void testArrayIndexExtension_literalMutation118() throws java.io.IOException {
        java.lang.String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{{/test}}\n") + "</ol>");
        java.lang.String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
        java.lang.Object scope = new java.lang.Object() {
            java.lang.String[] test = new java.lang.String[]{ "a" , "b" , "c" , "d" };
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
        mf.setObjectHandler(oh);
        com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader(template), "teplate");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)m).getName(), "teplate");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)m).isRecursive());
        java.io.StringWriter writer = new java.io.StringWriter();
        m.execute(writer, scope).flush();
        org.junit.Assert.assertEquals(result, writer.toString());
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    @org.junit.Test
    public void testArrayIndexExtension_literalMutation44_failAssert42() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{n/test}}\n") + "</ol>");
            java.lang.String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            java.lang.Object scope = new java.lang.Object() {
                java.lang.String[] test = new java.lang.String[]{ "a" , "b" , "c" , "d" };
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
            mf.setObjectHandler(oh);
            com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader(template), "template");
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            // MethodAssertGenerator build local variable
            Object o_32_0 = writer.toString();
            org.junit.Assert.fail("testArrayIndexExtension_literalMutation44 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    @org.junit.Test
    public void testArrayIndexExtension_literalMutation44_failAssert42_literalMutation1469() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{est.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "    <li>{{.}}</li>\n") + "{n/test}}\n") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(template, "<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{est.3}}</li>\n</ol>\n<ol>\n{{#test}}\n    <li>{{.}}</li>\n{n/test}}\n</ol>");
            java.lang.String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            java.lang.Object scope = new java.lang.Object() {
                java.lang.String[] test = new java.lang.String[]{ "a" , "b" , "c" , "d" };
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
            mf.setObjectHandler(oh);
            com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader(template), "template");
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            // MethodAssertGenerator build local variable
            Object o_32_0 = writer.toString();
            org.junit.Assert.fail("testArrayIndexExtension_literalMutation44 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ArraysIndexesTest#testArrayIndexExtension */
    @org.junit.Test
    public void testArrayIndexExtension_literalMutation118_literalMutation411_failAssert35_literalMutation17589() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String template = "<ol>\n" + (((((((("    <li>{{test.1}}</li>\n" + "    <li>{{test.0}}</li>\n") + "    <li>{{test.3}}</li>\n") + "</ol>\n") + "<ol>\n") + "{{#test}}\n") + "   w<li>{{.}}</li>\n") + "imrOiR]O2;") + "</ol>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(template, "<ol>\n    <li>{{test.1}}</li>\n    <li>{{test.0}}</li>\n    <li>{{test.3}}</li>\n</ol>\n<ol>\n{{#test}}\n   w<li>{{.}}</li>\nimrOiR]O2;</ol>");
            java.lang.String result = "<ol>\n" + ((((((((("    <li>b</li>\n" + "    <li>a</li>\n") + "    <li>d</li>\n") + "</ol>\n") + "<ol>\n") + "    <li>a</li>\n") + "    <li>b</li>\n") + "    <li>c</li>\n") + "    <li>d</li>\n") + "</ol>");
            java.lang.Object scope = new java.lang.Object() {
                java.lang.String[] test = new java.lang.String[]{ "a" , "b" , "c" , "d" };
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
            mf.setObjectHandler(oh);
            com.github.mustachejava.Mustache m = mf.compile(new java.io.StringReader(template), "teplate");
            // MethodAssertGenerator build local variable
            Object o_28_0 = ((com.github.mustachejava.codes.DefaultMustache)m).getName();
            // MethodAssertGenerator build local variable
            Object o_30_0 = ((com.github.mustachejava.codes.DefaultMustache)m).isRecursive();
            java.io.StringWriter writer = new java.io.StringWriter();
            m.execute(writer, scope).flush();
            // MethodAssertGenerator build local variable
            Object o_36_0 = writer.toString();
            org.junit.Assert.fail("testArrayIndexExtension_literalMutation118_literalMutation411 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }
}

