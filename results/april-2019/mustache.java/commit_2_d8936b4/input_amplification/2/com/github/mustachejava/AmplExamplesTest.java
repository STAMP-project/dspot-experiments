package com.github.mustachejava;


import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.github.mustachejava.util.GuardException;
import com.github.mustachejava.util.Wrapper;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AmplExamplesTest {
    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0null1121_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, null);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0null1121 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0null1123_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(null, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0null1123 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0null1122_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=toolyn}}"), null);
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0null1122 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[null:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0_literalMutationNumber754_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0_literalMutationNumber754 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0_literalMutationString762_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=too5yn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0_literalMutationString762 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =too5yn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            mf.setObjectHandler(new ReflectionObjectHandler() {
                @Override
                public Wrapper find(String name, List<Object> scopes) {
                    String[] split = name.split("[*]");
                    if ((split.length) > 1) {
                        final double multiplier = Double.parseDouble(split[1].trim());
                        final Wrapper wrapper = super.find(split[0].trim(), scopes);
                        return new Wrapper() {
                            @Override
                            public Object call(List<Object> scopes) throws GuardException {
                                Object value = wrapper.call(scopes);
                                if (value instanceof Number) {
                                    value = ((Number) (value)).doubleValue();
                                } else {
                                    value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                }
                                return ((Double) (value)) * multiplier;
                            }
                        };
                    }
                    return super.find(name, scopes);
                }
            });
            Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "test");
            StringWriter sw = new StringWriter();
            test.execute(sw, new Object() {
                double number = 10;
            }).flush();
            sw.toString();
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0_literalMutationString766_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=too@lyn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0_literalMutationString766 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =too@lyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_remove58_literalMutationString265_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DefaultMustacheFactory();
            Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "test");
            StringWriter sw = new StringWriter();
            test.execute(sw, new Object() {
                double number = 10;
            }).flush();
            sw.toString();
            org.junit.Assert.fail("testExpressionsInNames_remove58_literalMutationString265 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0_add1012_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            split[0].trim();
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0_add1012 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0_add1014_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        ((Number) (value)).doubleValue();
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0_add1014 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0_add1015_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        Double.parseDouble(value.toString());
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0_add1015 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0_literalMutationString772_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "te=st");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0_literalMutationString772 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[te=st:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0_literalMutationNumber777_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 5;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0_literalMutationNumber777 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0null1117_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split(null);
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0null1117 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0_add1022_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0_add1022 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0_add1021_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), scopes);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0_add1021 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testExpressionsInNames_literalMutationString26_failAssert0null1118_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DefaultMustacheFactory();
                mf.setObjectHandler(new ReflectionObjectHandler() {
                    @Override
                    public Wrapper find(String name, List<Object> scopes) {
                        String[] split = name.split("[*]");
                        if ((split.length) > 1) {
                            final double multiplier = Double.parseDouble(split[1].trim());
                            final Wrapper wrapper = super.find(split[0].trim(), null);
                            return new Wrapper() {
                                @Override
                                public Object call(List<Object> scopes) throws GuardException {
                                    Object value = wrapper.call(scopes);
                                    if (value instanceof Number) {
                                        value = ((Number) (value)).doubleValue();
                                    } else {
                                        value = (value == null) ? 0.0 : Double.parseDouble(value.toString());
                                    }
                                    return ((Double) (value)) * multiplier;
                                }
                            };
                        }
                        return super.find(name, scopes);
                    }
                });
                Mustache test = mf.compile(new StringReader("{{=toolyn}}"), "test");
                StringWriter sw = new StringWriter();
                test.execute(sw, new Object() {
                    double number = 10;
                }).flush();
                sw.toString();
                org.junit.Assert.fail("testExpressionsInNames_literalMutationString26 should have thrown MustacheException");
            }
            org.junit.Assert.fail("testExpressionsInNames_literalMutationString26_failAssert0null1118 should have thrown MustacheException");
        } catch (MustacheException expected) {
            Assert.assertEquals("Invalid delimiter string: =toolyn @[test:1]", expected.getMessage());
        }
    }
}

