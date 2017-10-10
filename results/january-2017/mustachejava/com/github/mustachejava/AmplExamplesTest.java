

package com.github.mustachejava;


public class AmplExamplesTest {
    @org.junit.Test
    public void testExpressionsInNames() throws java.io.IOException {
        com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
        mf.setObjectHandler(new com.github.mustachejava.reflect.ReflectionObjectHandler() {
            @java.lang.Override
            public com.github.mustachejava.util.Wrapper find(java.lang.String name, java.util.List<java.lang.Object> scopes) {
                // Worst expression parser ever written follows
                java.lang.String[] split = name.split("[*]");
                if ((split.length) > 1) {
                    final double multiplier = java.lang.Double.parseDouble(split[1].trim());
                    final com.github.mustachejava.util.Wrapper wrapper = super.find(split[0].trim(), scopes);
                    return new com.github.mustachejava.util.Wrapper() {
                        @java.lang.Override
                        public java.lang.Object call(java.util.List<java.lang.Object> scopes) throws com.github.mustachejava.util.GuardException {
                            java.lang.Object value = wrapper.call(scopes);
                            if (value instanceof java.lang.Number) {
                                value = ((java.lang.Number) (value)).doubleValue();
                            }else {
                                value = (value == null) ? 0.0 : java.lang.Double.parseDouble(value.toString());
                            }
                            return ((java.lang.Double) (value)) * multiplier;
                        }
                    };
                }
                return super.find(name, scopes);
            }
        });
        com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{number * 2.2}}"), "test");
        java.io.StringWriter sw = new java.io.StringWriter();
        test.execute(sw, new java.lang.Object() {
            double number = 10;
        }).flush();
        org.junit.Assert.assertEquals("22.0", sw.toString());
    }

    /* amplification of com.github.mustachejava.ExamplesTest#testExpressionsInNames */
    @org.junit.Test
    public void testExpressionsInNames_literalMutation7_failAssert3_literalMutation138() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            mf.setObjectHandler(new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @java.lang.Override
                public com.github.mustachejava.util.Wrapper find(java.lang.String name, java.util.List<java.lang.Object> scopes) {
                    // Worst expression parser ever written follows
                    java.lang.String[] split = name.split("[*]");
                    if ((split.length) > 1) {
                        final double multiplier = java.lang.Double.parseDouble(split[0].trim());
                        final com.github.mustachejava.util.Wrapper wrapper = super.find(split[0].trim(), scopes);
                        return new com.github.mustachejava.util.Wrapper() {
                            @java.lang.Override
                            public java.lang.Object call(java.util.List<java.lang.Object> scopes) throws com.github.mustachejava.util.GuardException {
                                java.lang.Object value = wrapper.call(scopes);
                                if (value instanceof java.lang.Number) {
                                    value = ((java.lang.Number) (value)).doubleValue();
                                }else {
                                    value = (value == null) ? 0.0 : java.lang.Double.parseDouble(value.toString());
                                }
                                return ((java.lang.Double) (value)) * multiplier;
                            }
                        };
                    }
                    return super.find(name, scopes);
                }
            });
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{numbr * 2.2}}"), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                double number = 10;
            }).flush();
            // MethodAssertGenerator build local variable
            Object o_51_0 = sw.toString();
            org.junit.Assert.fail("testExpressionsInNames_literalMutation7 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }

    /* amplification of com.github.mustachejava.ExamplesTest#testExpressionsInNames */
    @org.junit.Test
    public void testExpressionsInNames_literalMutation7_failAssert3_literalMutation139_literalMutation2231() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.github.mustachejava.DefaultMustacheFactory mf = new com.github.mustachejava.DefaultMustacheFactory();
            mf.setObjectHandler(new com.github.mustachejava.reflect.ReflectionObjectHandler() {
                @java.lang.Override
                public com.github.mustachejava.util.Wrapper find(java.lang.String name, java.util.List<java.lang.Object> scopes) {
                    // Worst expression parser ever written follows
                    java.lang.String[] split = name.split("[*]");
                    if ((split.length) > 1) {
                        final double multiplier = java.lang.Double.parseDouble(split[0].trim());
                        final com.github.mustachejava.util.Wrapper wrapper = super.find(split[0].trim(), scopes);
                        return new com.github.mustachejava.util.Wrapper() {
                            @java.lang.Override
                            public java.lang.Object call(java.util.List<java.lang.Object> scopes) throws com.github.mustachejava.util.GuardException {
                                java.lang.Object value = wrapper.call(scopes);
                                if (value instanceof java.lang.Number) {
                                    value = ((java.lang.Number) (value)).doubleValue();
                                }else {
                                    value = (value == null) ? // TestDataMutator on numbers
                                    1.0 : java.lang.Double.parseDouble(value.toString());
                                }
                                return ((java.lang.Double) (value)) * multiplier;
                            }
                        };
                    }
                    return super.find(name, scopes);
                }
            });
            com.github.mustachejava.Mustache test = mf.compile(new java.io.StringReader("{{nuKmber * 2.2}}"), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((com.github.mustachejava.codes.DefaultMustache)test).isRecursive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.github.mustachejava.codes.DefaultMustache)test).getName(), "test");
            java.io.StringWriter sw = new java.io.StringWriter();
            test.execute(sw, new java.lang.Object() {
                double number = 10;
            }).flush();
            // MethodAssertGenerator build local variable
            Object o_51_0 = sw.toString();
            org.junit.Assert.fail("testExpressionsInNames_literalMutation7 should have thrown MustacheException");
        } catch (com.github.mustachejava.MustacheException eee) {
        }
    }
}

