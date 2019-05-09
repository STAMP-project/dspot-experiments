package com.github.mustachejava;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.github.mustachejava.codes.DefaultCode;
import com.github.mustachejava.codes.DefaultMustache;
import com.github.mustachejava.codes.PartialCode;
import com.github.mustachejava.reflect.SimpleObjectHandler;
import com.github.mustachejava.resolver.DefaultResolver;
import com.github.mustachejava.util.CapturingMustacheVisitor;
import com.github.mustachejavabenchmarks.JsonCapturer;
import com.github.mustachejavabenchmarks.JsonInterpreterTest;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import junit.framework.TestCase;


@SuppressWarnings("unused")
public class AmplInterpreterTest extends TestCase {
    protected File root;

    public void testSimple_literalMutationString72592_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("simple.ht]ml");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimple_literalMutationString72592 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simple.ht]ml not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString72592_failAssert0_add74557_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("simple.ht]ml");
                Mustache m = c.compile("simple.ht]ml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString72592 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString72592_failAssert0_add74557 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simple.ht]ml not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString72592_failAssert0null74757_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("simple.ht]ml");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString72592 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString72592_failAssert0null74757 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simple.ht]ml not found", expected.getMessage());
        }
    }

    public void testSimple_literalMutationString72592_failAssert0_literalMutationNumber73766_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("simple.ht]ml");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 5000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimple_literalMutationString72592 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimple_literalMutationString72592_failAssert0_literalMutationNumber73766 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simple.ht]ml not found", expected.getMessage());
        }
    }

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

    public void testSimpleI18N_literalMutationNumber25002_literalMutationString26542_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(root, Locale.KOREAN));
                Mustache m = c.compile("j{2e:k-3^zk");
                StringWriter sw = new StringWriter();
                Writer o_testSimpleI18N_literalMutationNumber25002__9 = m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10001;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                String o_testSimpleI18N_literalMutationNumber25002__17 = TestUtil.getContents(root, "simple_ko.txt");
                sw.toString();
            }
            {
                MustacheFactory c = new DefaultMustacheFactory(new AmplInterpreterTest.LocalizedMustacheResolver(this.root, Locale.JAPANESE));
                Mustache m = c.compile("simple.html");
                StringWriter sw = new StringWriter();
                Writer o_testSimpleI18N_literalMutationNumber25002__27 = m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                String o_testSimpleI18N_literalMutationNumber25002__34 = TestUtil.getContents(this.root, "simple.txt");
                sw.toString();
            }
            junit.framework.TestCase.fail("testSimpleI18N_literalMutationNumber25002_literalMutationString26542 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template j{2e:k-3^zk not found", expected.getMessage());
        }
    }

    public void testRootCheck_add71701_literalMutationString71727_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            createMustacheFactory();
            MustacheFactory c = createMustacheFactory();
            {
                Mustache m = c.compile("wF@KG6UgT}LG:kaA");
            }
            junit.framework.TestCase.fail("testRootCheck_add71701_literalMutationString71727 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template wF@KG6UgT}LG:kaA not found", expected.getMessage());
        }
    }

    public void testRootCheck_literalMutationString71698_literalMutationString71736_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            {
                Mustache m = c.compile("9fXT$YRT#dx,W}(");
            }
            junit.framework.TestCase.fail("testRootCheck_literalMutationString71698_literalMutationString71736 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 9fXT$YRT#dx,W}( not found", expected.getMessage());
        }
    }

    public void testRootCheck_literalMutationString71696_literalMutationString71755_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            {
                Mustache m = c.compile("r:.2R[#vd!YEHl)]Q");
            }
            junit.framework.TestCase.fail("testRootCheck_literalMutationString71696_literalMutationString71755 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r:.2R[#vd!YEHl)]Q not found", expected.getMessage());
        }
    }

    public void testRootCheck_literalMutationString71700_literalMutationString71751_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            {
                Mustache m = c.compile("*p>^ynN[Sm (,&U,");
            }
            junit.framework.TestCase.fail("testRootCheck_literalMutationString71700_literalMutationString71751 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template *p>^ynN[Sm (,&U, not found", expected.getMessage());
        }
    }

    public void testRootCheck_literalMutationString71697_literalMutationString71744_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            {
                Mustache m = c.compile("}L^_#8#>6");
            }
            junit.framework.TestCase.fail("testRootCheck_literalMutationString71697_literalMutationString71744 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template }L^_#8#>6 not found", expected.getMessage());
        }
    }

    public void testRootCheck_literalMutationString71699_literalMutationString71763_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            {
                Mustache m = c.compile("[?kh[m#v(FR)NL#v");
            }
            junit.framework.TestCase.fail("testRootCheck_literalMutationString71699_literalMutationString71763 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [?kh[m#v(FR)NL#v not found", expected.getMessage());
        }
    }

    public void testRootCheck_literalMutationString71697_literalMutationString71742_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            {
                Mustache m = c.compile(" does not exist");
            }
            junit.framework.TestCase.fail("testRootCheck_literalMutationString71697_literalMutationString71742 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testRootCheck_literalMutationString71700_literalMutationString71752_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            {
                Mustache m = c.compile("#0dbz&l_6O_LhR_!");
            }
            junit.framework.TestCase.fail("testRootCheck_literalMutationString71700_literalMutationString71752 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #0dbz&l_6O_LhR_! not found", expected.getMessage());
        }
    }

    public void testRootCheck_literalMutationString71697_literalMutationString71745_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            {
                Mustache m = c.compile("pag e1.txt");
            }
            junit.framework.TestCase.fail("testRootCheck_literalMutationString71697_literalMutationString71745 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template pag e1.txt not found", expected.getMessage());
        }
    }

    public void testRootCheck_literalMutationString71699_literalMutationString71762_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            {
                Mustache m = c.compile("../../../po%#.xml");
            }
            junit.framework.TestCase.fail("testRootCheck_literalMutationString71699_literalMutationString71762 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ../../../po%#.xml not found", expected.getMessage());
        }
    }

    public void testRootCheck_literalMutationString71699_literalMutationString71760_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            {
                Mustache m = c.compile("..`../../po#.xml");
            }
            junit.framework.TestCase.fail("testRootCheck_literalMutationString71699_literalMutationString71760 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ..`../../po#.xml not found", expected.getMessage());
        }
    }

    public void testRootCheck_add71702_literalMutationString71719_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            {
                c.compile("QxfsapF/H<TY5U]J");
                Mustache m = c.compile("../../../pom.xml");
            }
            junit.framework.TestCase.fail("testRootCheck_add71702_literalMutationString71719 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template QxfsapF/H<TY5U]J not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString37251_failAssert0_add38757_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended.replaceAll("^[\t ]+", "");
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("C[#+f)=`47OQCNL>!+N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString37251 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString37251_failAssert0_add38757 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C[#+f)=`47OQCNL>!+N not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString37251_failAssert0_add38764_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("C[#+f)=`47OQCNL>!+N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString37251 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString37251_failAssert0_add38764 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C[#+f)=`47OQCNL>!+N not found", expected.getMessage());
        }
    }

    public void testSimpleFilterednull37290_failAssert0_literalMutationString37692_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll(null, " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("0*>lv]mt/tbj{mTP`JL");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFilterednull37290 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSimpleFilterednull37290_failAssert0_literalMutationString37692 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 0*>lv]mt/tbj{mTP`JL not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString37251_failAssert0_literalMutationNumber38447_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("C[#+f)=`47OQCNL>!+N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.0)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString37251 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString37251_failAssert0_literalMutationNumber38447 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C[#+f)=`47OQCNL>!+N not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString37251_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public String filterText(String appended, boolean startOfLine) {
                    if (startOfLine) {
                        appended = appended.replaceAll("^[\t ]+", "");
                    }
                    return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                }
            };
            Mustache m = c.compile("C[#+f)=`47OQCNL>!+N");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestUtil.getContents(root, "simplefiltered.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString37251 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C[#+f)=`47OQCNL>!+N not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString37251_failAssert0_literalMutationString38453_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("C[#+f)=`47OQCNL>!+N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplfiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString37251 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString37251_failAssert0_literalMutationString38453 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C[#+f)=`47OQCNL>!+N not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString37251_failAssert0null38941_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll(null, "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll("[ \n\t]*\n[ \n\t]*", "\n");
                    }
                };
                Mustache m = c.compile("C[#+f)=`47OQCNL>!+N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString37251 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString37251_failAssert0null38941 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C[#+f)=`47OQCNL>!+N not found", expected.getMessage());
        }
    }

    public void testSimpleFiltered_literalMutationString37251_failAssert0null38945_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public String filterText(String appended, boolean startOfLine) {
                        if (startOfLine) {
                            appended = appended.replaceAll("^[\t ]+", "");
                        }
                        return appended.replaceAll("[ \t]+", " ").replaceAll(null, "\n");
                    }
                };
                Mustache m = c.compile("C[#+f)=`47OQCNL>!+N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                TestUtil.getContents(root, "simplefiltered.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString37251 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleFiltered_literalMutationString37251_failAssert0null38945 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C[#+f)=`47OQCNL>!+N not found", expected.getMessage());
        }
    }

    public void testTypedSimple_add33301_literalMutationString33469_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            final Object scope = new Object() {
                String name = "Chris";

                int value = 10000;

                class MyObject {
                    int taxed_value() {
                        return ((int) ((value) - ((value) * 0.4)));
                    }

                    String fred = "";
                }

                MyObject in_ca = new MyObject();

                boolean test = false;
            };
            DefaultMustacheFactory c = new DefaultMustacheFactory(root);
            c.setObjectHandler(new TypeCheckingHandler());
            Mustache m = c.compile("OXvl3> K=^6");
            StringWriter sw = new StringWriter();
            m.execute(sw, scope.getClass()).flush();
            m.execute(sw, scope.getClass()).flush();
            String o_testTypedSimple_add33301__26 = TestUtil.getContents(root, "simpletyped.txt");
            sw.toString();
            junit.framework.TestCase.fail("testTypedSimple_add33301_literalMutationString33469 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template OXvl3> K=^6 not found", expected.getMessage());
        }
    }

    public void testTypedSimple_literalMutationString33289_failAssert0null35995_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                final Object scope = new Object() {
                    String name = "Chris";

                    int value = 10000;

                    class MyObject {
                        int taxed_value() {
                            return ((int) ((value) - ((value) * 0.4)));
                        }

                        String fred = "";
                    }

                    MyObject in_ca = new MyObject();

                    boolean test = false;
                };
                DefaultMustacheFactory c = new DefaultMustacheFactory(root);
                c.setObjectHandler(new TypeCheckingHandler());
                Mustache m = c.compile("0W_IhKd7%jQ");
                StringWriter sw = new StringWriter();
                m.execute(sw, scope.getClass()).flush();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testTypedSimple_literalMutationString33289 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testTypedSimple_literalMutationString33289_failAssert0null35995 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 0W_IhKd7%jQ not found", expected.getMessage());
        }
    }

    public void testTypedSimple_literalMutationString33289_failAssert0_add35643_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                final Object scope = new Object() {
                    String name = "Chris";

                    int value = 10000;

                    class MyObject {
                        int taxed_value() {
                            return ((int) ((value) - ((value) * 0.4)));
                        }

                        String fred = "";
                    }

                    MyObject in_ca = new MyObject();

                    boolean test = false;
                };
                DefaultMustacheFactory c = new DefaultMustacheFactory(root);
                c.setObjectHandler(new TypeCheckingHandler());
                Mustache m = c.compile("0W_IhKd7%jQ");
                StringWriter sw = new StringWriter();
                scope.getClass();
                m.execute(sw, scope.getClass()).flush();
                TestUtil.getContents(root, "simpletyped.txt");
                sw.toString();
                junit.framework.TestCase.fail("testTypedSimple_literalMutationString33289 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testTypedSimple_literalMutationString33289_failAssert0_add35643 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 0W_IhKd7%jQ not found", expected.getMessage());
        }
    }

    public void testTypedSimple_literalMutationString33289_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            final Object scope = new Object() {
                String name = "Chris";

                int value = 10000;

                class MyObject {
                    int taxed_value() {
                        return ((int) ((value) - ((value) * 0.4)));
                    }

                    String fred = "";
                }

                MyObject in_ca = new MyObject();

                boolean test = false;
            };
            DefaultMustacheFactory c = new DefaultMustacheFactory(root);
            c.setObjectHandler(new TypeCheckingHandler());
            Mustache m = c.compile("0W_IhKd7%jQ");
            StringWriter sw = new StringWriter();
            m.execute(sw, scope.getClass()).flush();
            TestUtil.getContents(root, "simpletyped.txt");
            sw.toString();
            junit.framework.TestCase.fail("testTypedSimple_literalMutationString33289 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 0W_IhKd7%jQ not found", expected.getMessage());
        }
    }

    public void testTypedSimple_literalMutationString33289_failAssert0_literalMutationNumber34809_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                final Object scope = new Object() {
                    String name = "Chris";

                    int value = 10000;

                    class MyObject {
                        int taxed_value() {
                            return ((int) ((value) - ((value) * -0.6)));
                        }

                        String fred = "";
                    }

                    MyObject in_ca = new MyObject();

                    boolean test = false;
                };
                DefaultMustacheFactory c = new DefaultMustacheFactory(root);
                c.setObjectHandler(new TypeCheckingHandler());
                Mustache m = c.compile("0W_IhKd7%jQ");
                StringWriter sw = new StringWriter();
                m.execute(sw, scope.getClass()).flush();
                TestUtil.getContents(root, "simpletyped.txt");
                sw.toString();
                junit.framework.TestCase.fail("testTypedSimple_literalMutationString33289 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testTypedSimple_literalMutationString33289_failAssert0_literalMutationNumber34809 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 0W_IhKd7%jQ not found", expected.getMessage());
        }
    }

    private DefaultMustacheFactory createMustacheFactory() {
        return new DefaultMustacheFactory(root);
    }

    public void testRecurision_add75037_literalMutationString75110_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("^Zt-p4Y7_?lUXa", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            sw.toString();
            String o_testRecurision_add75037__12 = TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_add75037_literalMutationString75110 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^Zt-p4Y7_?lUXa not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationBoolean75028_failAssert0_literalMutationString75293_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("7_]HE?_42zB85!", new Object() {
                    Object value = new Object() {
                        boolean value = true;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationBoolean75028 should have thrown MustacheException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationBoolean75028_failAssert0_literalMutationString75293 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 7_]HE?_42zB85! not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString75032_failAssert0_literalMutationString75273_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("oIM;N=,E|`&LlG", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.htxt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString75032 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString75032_failAssert0_literalMutationString75273 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template oIM;N=,E|`&LlG not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString75024_failAssert0null75472_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute(",QaFToNO}9H*Nr", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString75024 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString75024_failAssert0null75472 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,QaFToNO}9H*Nr not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString75024_failAssert0() throws IOException {
        try {
            StringWriter sw = execute(",QaFToNO}9H*Nr", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecurision_literalMutationString75024 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,QaFToNO}9H*Nr not found", expected.getMessage());
        }
    }

    public void testRecurision_literalMutationString75024_failAssert0_add75399_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute(",QaFToNO}9H*Nr", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecurision_literalMutationString75024 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecurision_literalMutationString75024_failAssert0_add75399 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,QaFToNO}9H*Nr not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString71014_failAssert0_literalMutationString71197_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("Yy4;o#uo^3/g:8VVD$dlz_DxwUpA!g?", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "rcursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71014 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71014_failAssert0_literalMutationString71197 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Yy4;o#uo^3/g:8VVD$dlz_DxwUpA!g? not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_remove71021_literalMutationString71124_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("q*&W<|Vm<N1sM!CWw*}.@$K^c+R]q37", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            String o_testRecursionWithInheritance_remove71021__10 = TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_remove71021_literalMutationString71124 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template q*&W<|Vm<N1sM!CWw*}.@$K^c+R]q37 not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString71007_failAssert0_add71391_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("$ewq#+@tT?Ve%@OwzEjR;1]:`<7>.iL", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71007_failAssert0_add71391 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $ewq#+@tT?Ve%@OwzEjR;1]:`<7>.iL not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString71009_failAssert0_add71376_failAssert0() throws IOException {
        try {
            {
                execute("recurs|on_with_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                StringWriter sw = execute("recurs|on_with_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71009 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71009_failAssert0_add71376 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recurs|on_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString71009_failAssert0_literalMutationString71223_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recurs|on_with_inheriWance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71009 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71009_failAssert0_literalMutationString71223 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recurs|on_with_inheriWance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString71007_failAssert0_literalMutationString71262_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("$ewq#+@tT?Ve%@OwzEjR;g]:`<7>.iL", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71007 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71007_failAssert0_literalMutationString71262 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $ewq#+@tT?Ve%@OwzEjR;g]:`<7>.iL not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString71009_failAssert0_literalMutationString71230_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recurs|on_with_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "^#CYu0%,Ok6E/");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71009 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71009_failAssert0_literalMutationString71230 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recurs|on_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString71009_failAssert0_add71378_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recurs|on_with_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71009 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71009_failAssert0_add71378 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recurs|on_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString71009_failAssert0null71452_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recurs|on_with_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71009 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71009_failAssert0null71452 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recurs|on_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString71009_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("recurs|on_with_inheritance.html", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71009 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recurs|on_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString71007_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("$ewq#+@tT?Ve%@OwzEjR;1]:`<7>.iL", new Object() {
                Object value = new Object() {
                    boolean value = false;
                };
            });
            TestUtil.getContents(root, "recursion.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71007 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $ewq#+@tT?Ve%@OwzEjR;1]:`<7>.iL not found", expected.getMessage());
        }
    }

    public void testRecursionWithInheritance_literalMutationString71009_failAssert0_add71377_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("recurs|on_with_inheritance.html", new Object() {
                    Object value = new Object() {
                        boolean value = false;
                    };
                });
                TestUtil.getContents(root, "recursion.txt");
                TestUtil.getContents(root, "recursion.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71009 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRecursionWithInheritance_literalMutationString71009_failAssert0_add71377 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template recurs|on_with_inheritance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString71906_failAssert0_literalMutationString72111_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("w9lcS9m<5qQ:]#c,-qOfRgH[pc#nJs&8,c[", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString71906 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString71906_failAssert0_literalMutationString72111 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template w9lcS9m<5qQ:]#c,-qOfRgH[pc#nJs&8,c[ not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_remove71917_literalMutationString72036_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("*+u0?WXf>V7c0WT#?8MR{.,#t@e(=.d0F6", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            String o_testPartialRecursionWithInheritance_remove71917__10 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_remove71917_literalMutationString72036 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template *+u0?WXf>V7c0WT#?8MR{.,#t@e(=.d0F6 not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString71904_failAssert0_add72302_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("E9b/)f&V4+/rAtE(o#@Sy{ynHZ;z@b-Jqa", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString71904 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString71904_failAssert0_add72302 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString71904_failAssert0null72362_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("E9b/)f&V4+/rAtE(o#@Sy{ynHZ;z@b-Jqa", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString71904 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString71904_failAssert0null72362 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString71904_failAssert0_literalMutationString72210_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("E9b/)f&V4+/rAtE(o#@Sy{ynHZ;z@=-Jqa", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString71904 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString71904_failAssert0_literalMutationString72210 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template E9b/)f&V4+/rAtE(o#@Sy{ynHZ;z@=-Jqa not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritancenull71920_failAssert0_literalMutationString72067_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("su^eCp?&ZLRI)6?9G/d-;&ok0H}V^S:y|[", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritancenull71920 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritancenull71920_failAssert0_literalMutationString72067 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template su^eCp?&ZLRI)6?9G/d-;&ok0H}V^S:y|[ not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_add71916_literalMutationString72008_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("r|ecursive_partial_inheritance.html", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            sw.toString();
            String o_testPartialRecursionWithInheritance_add71916__12 = TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_add71916_literalMutationString72008 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r|ecursive_partial_inheritance.html not found", expected.getMessage());
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString71904_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("E9b/)f&V4+/rAtE(o#@Sy{ynHZ;z@b-Jqa", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            TestUtil.getContents(root, "recursive_partial_inheritance.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString71904 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testPartialRecursionWithInheritance_literalMutationString71904_failAssert0_add72303_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("E9b/)f&V4+/rAtE(o#@Sy{ynHZ;z@b-Jqa", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "recursive_partial_inheritance.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString71904 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialRecursionWithInheritance_literalMutationString71904_failAssert0_add72303 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
        }
    }

    public void testChainedInheritance_literalMutationString52375_failAssert0_literalMutationString52624_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("#QA[/DsOm", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "page.txt");
                sw.toString();
                junit.framework.TestCase.fail("testChainedInheritance_literalMutationString52375 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testChainedInheritance_literalMutationString52375_failAssert0_literalMutationString52624 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #QA[/DsOm not found", expected.getMessage());
        }
    }

    public void testChainedInheritance_literalMutationString52373_failAssert0_add52751_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("iw{^n(fa[", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "page.txt");
                sw.toString();
                junit.framework.TestCase.fail("testChainedInheritance_literalMutationString52373 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testChainedInheritance_literalMutationString52373_failAssert0_add52751 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template iw{^n(fa[ not found", expected.getMessage());
        }
    }

    public void testChainedInheritance_literalMutationString52373_failAssert0() throws IOException {
        try {
            StringWriter sw = execute("iw{^n(fa[", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            TestUtil.getContents(root, "page.txt");
            sw.toString();
            junit.framework.TestCase.fail("testChainedInheritance_literalMutationString52373 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template iw{^n(fa[ not found", expected.getMessage());
        }
    }

    public void testChainedInheritance_literalMutationString52373_failAssert0_literalMutationString52615_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("iw{^n(fa[", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "pagKe.txt");
                sw.toString();
                junit.framework.TestCase.fail("testChainedInheritance_literalMutationString52373 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testChainedInheritance_literalMutationString52373_failAssert0_literalMutationString52615 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template iw{^n(fa[ not found", expected.getMessage());
        }
    }

    public void testChainedInheritance_add52385_literalMutationString52462_failAssert0() throws IOException {
        try {
            StringWriter o_testChainedInheritance_add52385__1 = execute("page}html", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            StringWriter sw = execute("page.html", new Object() {
                Object test = new Object() {
                    boolean test = false;
                };
            });
            String o_testChainedInheritance_add52385__20 = TestUtil.getContents(root, "page.txt");
            sw.toString();
            junit.framework.TestCase.fail("testChainedInheritance_add52385_literalMutationString52462 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template page}html not found", expected.getMessage());
        }
    }

    public void testChainedInheritance_literalMutationString52377_failAssert0_literalMutationString52684_failAssert0() throws IOException {
        try {
            {
                StringWriter sw = execute("tx7SSz *", new Object() {
                    Object test = new Object() {
                        boolean test = false;
                    };
                });
                TestUtil.getContents(root, "page.txt");
                sw.toString();
                junit.framework.TestCase.fail("testChainedInheritance_literalMutationString52377 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testChainedInheritance_literalMutationString52377_failAssert0_literalMutationString52684 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template tx7SSz * not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationString19487_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("8cuPW[MLa%f)DIxZc");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationString19487 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 8cuPW[MLa%f)DIxZc not found", expected.getMessage());
        }
    }

    public void testSimplePragma_literalMutationNumber19495_literalMutationString19958_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("m&q(H_I?x>K^6v%`7");
            StringWriter sw = new StringWriter();
            Writer o_testSimplePragma_literalMutationNumber19495__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 0;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;
            });
            String o_testSimplePragma_literalMutationNumber19495__15 = TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimplePragma_literalMutationNumber19495_literalMutationString19958 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template m&q(H_I?x>K^6v%`7 not found", expected.getMessage());
        }
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

    public void testMultipleWrappers_literalMutationString44404_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("3k4GkkI`p_&");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                Object o = new Object() {
                    int taxed_value() {
                        return ((int) ((value) - ((value) * 0.4)));
                    }

                    String fred = "test";
                };

                Object in_ca = Arrays.asList(o, new Object() {
                    int taxed_value = ((int) ((value) - ((value) * 0.2)));
                }, o);
            });
            TestUtil.getContents(root, "simplerewrap.txt");
            sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString44404 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 3k4GkkI`p_& not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString44404_failAssert0null48078_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("3k4GkkI`p_&");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    Object o = new Object() {
                        int taxed_value() {
                            return ((int) ((value) - ((value) * 0.4)));
                        }

                        String fred = "test";
                    };

                    Object in_ca = Arrays.asList(o, new Object() {
                        int taxed_value = ((int) ((value) - ((value) * 0.2)));
                    }, o);
                });
                TestUtil.getContents(root, "simplerewrap.txt");
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString44404 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString44404_failAssert0null48078 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 3k4GkkI`p_& not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString44426_literalMutationString46155_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("|imple.html");
            StringWriter sw = new StringWriter();
            Writer o_testMultipleWrappers_literalMutationString44426__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 10000;

                Object o = new Object() {
                    int taxed_value() {
                        return ((int) ((value) - ((value) * 0.4)));
                    }

                    String fred = "tst";
                };

                Object in_ca = Arrays.asList(o, new Object() {
                    int taxed_value = ((int) ((value) - ((value) * 0.2)));
                }, o);
            });
            String o_testMultipleWrappers_literalMutationString44426__23 = TestUtil.getContents(root, "simplerewrap.txt");
            sw.toString();
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString44426_literalMutationString46155 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template |imple.html not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString44404_failAssert0_literalMutationString46781_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("3k4GkkI`p_&");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chrqis";

                    int value = 10000;

                    Object o = new Object() {
                        int taxed_value() {
                            return ((int) ((value) - ((value) * 0.4)));
                        }

                        String fred = "test";
                    };

                    Object in_ca = Arrays.asList(o, new Object() {
                        int taxed_value = ((int) ((value) - ((value) * 0.2)));
                    }, o);
                });
                TestUtil.getContents(root, "simplerewrap.txt");
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString44404 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString44404_failAssert0_literalMutationString46781 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 3k4GkkI`p_& not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString44404_failAssert0_add47752_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("3k4GkkI`p_&");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    Object o = new Object() {
                        int taxed_value() {
                            return ((int) ((value) - ((value) * 0.4)));
                        }

                        String fred = "test";
                    };

                    Object in_ca = Arrays.asList(o, new Object() {
                        int taxed_value = ((int) ((value) - ((value) * 0.2)));
                    }, o);
                });
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    Object o = new Object() {
                        int taxed_value() {
                            return ((int) ((value) - ((value) * 0.4)));
                        }

                        String fred = "test";
                    };

                    Object in_ca = Arrays.asList(o, new Object() {
                        int taxed_value = ((int) ((value) - ((value) * 0.2)));
                    }, o);
                });
                TestUtil.getContents(root, "simplerewrap.txt");
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString44404 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString44404_failAssert0_add47752 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 3k4GkkI`p_& not found", expected.getMessage());
        }
    }

    public void testMultipleWrappers_literalMutationString44405_failAssert0_literalMutationString46648_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("<ezo&NT%Dq7N");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    Object o = new Object() {
                        int taxed_value() {
                            return ((int) ((value) - ((value) * 0.4)));
                        }

                        String fred = "test";
                    };

                    Object in_ca = Arrays.asList(o, new Object() {
                        int taxed_value = ((int) ((value) - ((value) * 0.2)));
                    }, o);
                });
                TestUtil.getContents(root, "simplerewrap.txt");
                sw.toString();
                junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString44405 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testMultipleWrappers_literalMutationString44405_failAssert0_literalMutationString46648 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <ezo&NT%Dq7N not found", expected.getMessage());
        }
    }

    public void testBrokenSimple_literalMutationNumber69905_literalMutationString70152_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("brok}ensimple.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10001;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                String String_170 = "Should have failed: " + (sw.toString());
            }
            junit.framework.TestCase.fail("testBrokenSimple_literalMutationNumber69905_literalMutationString70152 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template brok}ensimple.html not found", expected.getMessage());
        }
    }

    public void testBrokenSimple_literalMutationString69891null70773_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("5W{xgXaPH@{oZ.#+W");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                String String_185 = "Should have failed: " + (sw.toString());
            }
            junit.framework.TestCase.fail("testBrokenSimple_literalMutationString69891null70773 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 5W{xgXaPH@{oZ.#+W not found", expected.getMessage());
        }
    }

    public void testBrokenSimple_literalMutationString69901_literalMutationString70424_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("dO?(p%}}QkE5wg/[X");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chrs";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                String String_207 = "Should have failed: " + (sw.toString());
            }
            junit.framework.TestCase.fail("testBrokenSimple_literalMutationString69901_literalMutationString70424 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template dO?(p%}}QkE5wg/[X not found", expected.getMessage());
        }
    }

    public void testBrokenSimple_literalMutationString69891_literalMutationNumber70413_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("5W{xgXaPH@{oZ.#+W");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 1.4)));
                    }

                    boolean in_ca = true;
                });
                String String_160 = "Should have failed: " + (sw.toString());
            }
            junit.framework.TestCase.fail("testBrokenSimple_literalMutationString69891_literalMutationNumber70413 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 5W{xgXaPH@{oZ.#+W not found", expected.getMessage());
        }
    }

    public void testBrokenSimple_literalMutationString69891_literalMutationNumber70415_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("5W{xgXaPH@{oZ.#+W");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 10000;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.0)));
                    }

                    boolean in_ca = true;
                });
                String String_210 = "Should have failed: " + (sw.toString());
            }
            junit.framework.TestCase.fail("testBrokenSimple_literalMutationString69891_literalMutationNumber70415 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 5W{xgXaPH@{oZ.#+W not found", expected.getMessage());
        }
    }

    public void testBrokenSimple_literalMutationNumber69906_literalMutationString70125_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile(";5@<$Il9?{k,ps?!=");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String name = "Chris";

                    int value = 9999;

                    int taxed_value() {
                        return ((int) ((this.value) - ((this.value) * 0.4)));
                    }

                    boolean in_ca = true;
                });
                String String_217 = "Should have failed: " + (sw.toString());
            }
            junit.framework.TestCase.fail("testBrokenSimple_literalMutationNumber69906_literalMutationString70125 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ;5@<$Il9?{k,ps?!= not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString39399_failAssert0_literalMutationString39801_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("t8mq1mVsch%O", object);
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString39399 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString39399_failAssert0_literalMutationString39801 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template t8mq1mVsch%O not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString39396_failAssert0null40227_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList(null);
                };
                StringWriter sw = execute("C6a5%8/s1tUy", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString39396 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString39396_failAssert0null40227 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C6a5%8/s1tUy not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString39396_failAssert0_literalMutationString39922_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Tesst");
                };
                StringWriter sw = execute("C6a5%8/s1tUy", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString39396 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString39396_failAssert0_literalMutationString39922 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C6a5%8/s1tUy not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString39396_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("C6a5%8/s1tUy", object);
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString39396 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C6a5%8/s1tUy not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString39396_failAssert0_add40091_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("C6a5%8/s1tUy", object);
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString39396 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString39396_failAssert0_add40091 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C6a5%8/s1tUy not found", expected.getMessage());
        }
    }

    public void testIsNotEmpty_literalMutationString39389_literalMutationString39670_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("TeJst");
            };
            StringWriter sw = execute("isempty.h:ml", object);
            String o_testIsNotEmpty_literalMutationString39389__9 = TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testIsNotEmpty_literalMutationString39389_literalMutationString39670 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template isempty.h:ml not found", expected.getMessage());
        }
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

    public void testImmutableList_literalMutationString61840_failAssert0_add62574_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                Collections.singletonList(object);
                StringWriter sw = execute("m}q;M%:Gz`y,", Collections.singletonList(object));
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString61840 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString61840_failAssert0_add62574 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template m}q;M%:Gz`y, not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString61840_failAssert0null62732_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Test");
                };
                StringWriter sw = execute("m}q;M%:Gz`y,", Collections.singletonList(object));
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString61840 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString61840_failAssert0null62732 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template m}q;M%:Gz`y, not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString61840_failAssert0_literalMutationString62333_failAssert0() throws IOException {
        try {
            {
                Object object = new Object() {
                    List<String> people = Collections.singletonList("Tet");
                };
                StringWriter sw = execute("m}q;M%:Gz`y,", Collections.singletonList(object));
                TestUtil.getContents(root, "isempty.txt");
                sw.toString();
                junit.framework.TestCase.fail("testImmutableList_literalMutationString61840 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testImmutableList_literalMutationString61840_failAssert0_literalMutationString62333 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template m}q;M%:Gz`y, not found", expected.getMessage());
        }
    }

    public void testImmutableList_literalMutationString61840_failAssert0() throws IOException {
        try {
            Object object = new Object() {
                List<String> people = Collections.singletonList("Test");
            };
            StringWriter sw = execute("m}q;M%:Gz`y,", Collections.singletonList(object));
            TestUtil.getContents(root, "isempty.txt");
            sw.toString();
            junit.framework.TestCase.fail("testImmutableList_literalMutationString61840 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template m}q;M%:Gz`y, not found", expected.getMessage());
        }
    }

    public void testSecuritynull21945_literalMutationString22301_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("<ecurity.html");
            StringWriter sw = new StringWriter();
            Writer o_testSecuritynull21945__7 = m.execute(null, new Object() {
                String name = "Chris";

                int value = 10000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private String test = "Test";
            });
            String o_testSecuritynull21945__15 = TestUtil.getContents(root, "security.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSecuritynull21945_literalMutationString22301 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <ecurity.html not found", expected.getMessage());
        }
    }

    public void testSecurity_literalMutationNumber21916_literalMutationString22895_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("+)d8tUS@$Ke4%");
            StringWriter sw = new StringWriter();
            Writer o_testSecurity_literalMutationNumber21916__7 = m.execute(sw, new Object() {
                String name = "Chris";

                int value = 5000;

                int taxed_value() {
                    return ((int) ((this.value) - ((this.value) * 0.4)));
                }

                boolean in_ca = true;

                private String test = "Test";
            });
            String o_testSecurity_literalMutationNumber21916__16 = TestUtil.getContents(root, "security.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSecurity_literalMutationNumber21916_literalMutationString22895 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +)d8tUS@$Ke4% not found", expected.getMessage());
        }
    }

    public void testIdentitySimple_literalMutationString55296_failAssert0null57676_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("r=u_x?h41}c");
                StringWriter sw = new StringWriter();
                m.identity(sw);
                TestUtil.getContents(root, "simple.html").replaceAll("\\s+", null);
                sw.toString().replaceAll("\\s+", "");
                junit.framework.TestCase.fail("testIdentitySimple_literalMutationString55296 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIdentitySimple_literalMutationString55296_failAssert0null57676 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r=u_x?h41}c not found", expected.getMessage());
        }
    }

    public void testIdentitySimple_literalMutationString55296_failAssert0_add57330_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("r=u_x?h41}c");
                StringWriter sw = new StringWriter();
                m.identity(sw);
                TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
                sw.toString();
                sw.toString().replaceAll("\\s+", "");
                junit.framework.TestCase.fail("testIdentitySimple_literalMutationString55296 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIdentitySimple_literalMutationString55296_failAssert0_add57330 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r=u_x?h41}c not found", expected.getMessage());
        }
    }

    public void testIdentitySimple_literalMutationString55296_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("r=u_x?h41}c");
            StringWriter sw = new StringWriter();
            m.identity(sw);
            TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
            sw.toString().replaceAll("\\s+", "");
            junit.framework.TestCase.fail("testIdentitySimple_literalMutationString55296 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r=u_x?h41}c not found", expected.getMessage());
        }
    }

    public void testIdentitySimple_literalMutationString55296_failAssert0_literalMutationString56675_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("r=u_x?h41}c");
                StringWriter sw = new StringWriter();
                m.identity(sw);
                TestUtil.getContents(root, "simple.html").replaceAll("\\s+", "");
                sw.toString().replaceAll("\\+", "");
                junit.framework.TestCase.fail("testIdentitySimple_literalMutationString55296 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testIdentitySimple_literalMutationString55296_failAssert0_literalMutationString56675 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template r=u_x?h41}c not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString41558_failAssert0_literalMutationNumber42860_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("1SL6YXB)O[M");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String getName() {
                        return "Chris";
                    }

                    int getValue() {
                        return 0;
                    }

                    int taxed_value() {
                        return ((int) ((this.getValue()) - ((this.getValue()) * 0.4)));
                    }

                    boolean isIn_ca() {
                        return true;
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testProperties_literalMutationString41558 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString41558_failAssert0_literalMutationNumber42860 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1SL6YXB)O[M not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString41558_failAssert0_add43724_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("1SL6YXB)O[M");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String getName() {
                        return "Chris";
                    }

                    int getValue() {
                        return 10000;
                    }

                    int taxed_value() {
                        return ((int) ((this.getValue()) - ((this.getValue()) * 0.4)));
                    }

                    boolean isIn_ca() {
                        return true;
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testProperties_literalMutationString41558 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString41558_failAssert0_add43724 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1SL6YXB)O[M not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString41558_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("1SL6YXB)O[M");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                String getName() {
                    return "Chris";
                }

                int getValue() {
                    return 10000;
                }

                int taxed_value() {
                    return ((int) ((this.getValue()) - ((this.getValue()) * 0.4)));
                }

                boolean isIn_ca() {
                    return true;
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testProperties_literalMutationString41558 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1SL6YXB)O[M not found", expected.getMessage());
        }
    }

    public void testProperties_literalMutationString41558_failAssert0_add43721_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("1SL6YXB)O[M");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    String getName() {
                        return "Chris";
                    }

                    int getValue() {
                        return 10000;
                    }

                    int taxed_value() {
                        this.getValue();
                        return ((int) ((this.getValue()) - ((this.getValue()) * 0.4)));
                    }

                    boolean isIn_ca() {
                        return true;
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testProperties_literalMutationString41558 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testProperties_literalMutationString41558_failAssert0_add43721 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 1SL6YXB)O[M not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString13322_failAssert0_literalMutationString17323_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                StringWriter sw = execute("simp le.html", new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                    }
                });
                TestUtil.getContents(root, "simpl.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString13322 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString13322_failAssert0_literalMutationString17323 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simp le.html not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString13322_failAssert0null19128_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                StringWriter sw = execute("simp le.html", new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put(null, true);
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString13322 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString13322_failAssert0null19128 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simp le.html not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString13322_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            StringWriter sw = execute("simp le.html", new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString13322 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simp le.html not found", expected.getMessage());
        }
    }

    public void testSimpleWithMap_literalMutationString13322_failAssert0_add18254_failAssert0() throws MustacheException, IOException, InterruptedException, ExecutionException {
        try {
            {
                StringWriter sw = execute("simp le.html", new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("in_ca", true);
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString13322 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSimpleWithMap_literalMutationString13322_failAssert0_add18254 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template simp le.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11800_failAssert0_add12089_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("partialintem<latefunction.html");
                Mustache m = c.compile("partialintem<latefunction.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11800 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11800_failAssert0_add12089 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialintem<latefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_remove11807_literalMutationString11879_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("7Z!3b<_Q3r($%ijDGH^]e1+Y&<n7on");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_remove11807__7 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_remove11807_literalMutationString11879 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 7Z!3b<_Q3r($%ijDGH^]e1+Y&<n7on not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11799_failAssert0_literalMutationString11913_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("g,+i*6W=^+y9#9meh0Ap*#TOc`7!c,/");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799_failAssert0_literalMutationString11913 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template g,+i*6W=^+y9#9meh0Ap*#TOc`7!c,/ not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11799_failAssert0_literalMutationString11910_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("U!`t}JL]<S)FOVGUsAjC02%pEEH8 1");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799_failAssert0_literalMutationString11910 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template U!`t}JL]<S)FOVGUsAjC02%pEEH8 1 not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11799_failAssert0_literalMutationString11912_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("g,+i*6W=+y9#9meh0Ap*#TOc`7Jc,/");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799_failAssert0_literalMutationString11912 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template g,+i*6W=+y9#9meh0Ap*#TOc`7Jc,/ not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11800_failAssert0_add12091_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("partialintem<latefunction.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11800 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11800_failAssert0_add12091 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialintem<latefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11799_failAssert0null12134_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("g,+i*6W=+y9#9meh0Ap*#TOc`7!c,/");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799_failAssert0null12134 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template g,+i*6W=+y9#9meh0Ap*#TOc`7!c,/ not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11799_failAssert0_add12083_failAssert0() throws MustacheException, IOException {
        try {
            {
                createMustacheFactory();
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("g,+i*6W=+y9#9meh0Ap*#TOc`7!c,/");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799_failAssert0_add12083 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template g,+i*6W=+y9#9meh0Ap*#TOc`7!c,/ not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11799_failAssert0_add12085_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("g,+i*6W=+y9#9meh0Ap*#TOc`7!c,/");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799_failAssert0_add12085 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template g,+i*6W=+y9#9meh0Ap*#TOc`7!c,/ not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11800_failAssert0_literalMutationString11915_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("partialintem<Hlatefunction.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11800 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11800_failAssert0_literalMutationString11915 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialintem<Hlatefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add11803_literalMutationString11867_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache o_testPartialWithTF_add11803__3 = c.compile(")[eQd};!z]{iLeee)H*lAO1wDu?5,(");
            Mustache m = c.compile("partialintemplatefunction.html");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add11803__8 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add11803_literalMutationString11867 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )[eQd};!z]{iLeee)H*lAO1wDu?5,( not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11800_failAssert0_literalMutationString11917_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("$[o9S[&/^CEWU[B(!bEMk&!k!&plv5");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11800 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11800_failAssert0_literalMutationString11917 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $[o9S[&/^CEWU[B(!bEMk&!k!&plv5 not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_add11805_literalMutationString11854_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile(")P{6L/qXOXEcH!`{pkIYBBt&V<M#{u");
            StringWriter sw = new StringWriter();
            Writer o_testPartialWithTF_add11805__7 = m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_add11805_literalMutationString11854 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )P{6L/qXOXEcH!`{pkIYBBt&V<M#{u not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11800_failAssert0null12136_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("partialintem<latefunction.html");
                StringWriter sw = new StringWriter();
                m.execute(null, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11800 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11800_failAssert0null12136 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialintem<latefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11801_failAssert0_literalMutationString11921_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("]7_e_0d0>nv[qpt1--1Y>(1k@7YN3");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11801 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11801_failAssert0_literalMutationString11921 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ]7_e_0d0>nv[qpt1--1Y>(1k@7YN3 not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11800_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("partialintem<latefunction.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11800 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template partialintem<latefunction.html not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11799_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("g,+i*6W=+y9#9meh0Ap*#TOc`7!c,/");
            StringWriter sw = new StringWriter();
            m.execute(sw, new Object() {
                public TemplateFunction i() {
                    return ( s) -> s;
                }
            });
            sw.toString();
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template g,+i*6W=+y9#9meh0Ap*#TOc`7!c,/ not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11799_failAssert0_add12084_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("g,+i*6W=+y9#9meh0Ap*#TOc`7!c,/");
                Mustache m = c.compile("g,+i*6W=+y9#9meh0Ap*#TOc`7!c,/");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799_failAssert0_add12084 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template g,+i*6W=+y9#9meh0Ap*#TOc`7!c,/ not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11799_failAssert0_add12086_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("g,+i*6W=+y9#9meh0Ap*#TOc`7!c,/");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11799_failAssert0_add12086 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template g,+i*6W=+y9#9meh0Ap*#TOc`7!c,/ not found", expected.getMessage());
        }
    }

    public void testPartialWithTF_literalMutationString11798_failAssert0_literalMutationString11902_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("6<SD5S9?z tFWF^8b8]Amq]+<Q;BUB-");
                StringWriter sw = new StringWriter();
                m.execute(sw, new Object() {
                    public TemplateFunction i() {
                        return ( s) -> s;
                    }
                });
                sw.toString();
                junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11798 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPartialWithTF_literalMutationString11798_failAssert0_literalMutationString11902 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 6<SD5S9?z tFWF^8b8]Amq]+<Q;BUB- not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString2381_failAssert0_literalMutationString3004_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("Kmw|fBcV:sr@");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "cogmplex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString2381 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString2381_failAssert0_literalMutationString3004 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Kmw|fBcV:sr@ not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString2361_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("eL&B8eK:V.T.");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("complex.html");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString2361 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template eL&B8eK:V.T. not found", expected.getMessage());
        }
    }

    public void testComplexnull2409_failAssert0_literalMutationString2639_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("::Q9-LEr&wrK");
                StringWriter sw = new StringWriter();
                m.execute(null, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexnull2409 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull2409_failAssert0_literalMutationString2639 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ::Q9-LEr&wrK not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString2361_failAssert0_literalMutationString3208_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("eL&B8eK:V.T.");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString2361 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString2361_failAssert0_literalMutationString3208 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template eL&B8eK:V.T. not found", expected.getMessage());
        }
    }

    public void testComplexnull75759_failAssert0_literalMutationString75988_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("m>_*w]!t|TYu");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(null, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexnull75759 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull75759_failAssert0_literalMutationString75988 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template m>_*w]!t|TYu not found", expected.getMessage());
        }
    }

    public void testComplex_remove45_failAssert0_literalMutationString486_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("n[sV{!Og@J_(");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_remove45 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testComplex_remove45_failAssert0_literalMutationString486 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template n[sV{!Og@J_( not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString2361_failAssert0_add3954_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("eL&B8eK:V.T.");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                json.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString2361 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString2361_failAssert0_add3954 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template eL&B8eK:V.T. not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString2374_failAssert0_add3913_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("nb+S&8{lpW=4");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString2374 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString2374_failAssert0_add3913 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template nb+S&8{lpW=4 not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString2361_failAssert0null4407_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("eL&B8eK:V.T.");
                StringWriter sw = new StringWriter();
                m.execute(null, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString2361 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString2361_failAssert0null4407 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template eL&B8eK:V.T. not found", expected.getMessage());
        }
    }

    public void testComplex_remove46_failAssert0_literalMutationString519_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("*iPiCdNJLK<S");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_remove46 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplex_remove46_failAssert0_literalMutationString519 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template *iPiCdNJLK<S not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString75707_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("compl x.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("complex.html");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString75707 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template compl x.html not found", expected.getMessage());
        }
    }

    public void testComplexnull52_failAssert0_literalMutationString282_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("xY#0idRb<liB");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, null);
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexnull52 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull52_failAssert0_literalMutationString282 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template xY#0idRb<liB not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString75706_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("_k%sR4SX}v ;");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("complex.html");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString75706 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template _k%sR4SX}v ; not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString15_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("complex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("dA&#Y-<y(}yc");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString15 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template dA&#Y-<y(}yc not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString75719_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("complex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("*.uLa_MYr`G_");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString75719 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template *.uLa_MYr`G_ not found", expected.getMessage());
        }
    }

    public void testComplexnull2415_failAssert0_literalMutationString2807_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("complex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile(" :0*{ova`1/@");
                m.execute(sw, o);
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testComplexnull2415 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull2415_failAssert0_literalMutationString2807 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  :0*{ova`1/@ not found", expected.getMessage());
        }
    }

    public void testComplexnull2411_failAssert0_literalMutationString2769_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("n  |AL[O=|Q!");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(null);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexnull2411 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull2411_failAssert0_literalMutationString2769 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template n  |AL[O=|Q! not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString2361_failAssert0_add3961_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("eL&B8eK:V.T.");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString2361 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString2361_failAssert0_add3961 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template eL&B8eK:V.T. not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString2374_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter json = new StringWriter();
            MappingJsonFactory jf = new MappingJsonFactory();
            final JsonGenerator jg = jf.createJsonGenerator(json);
            jg.writeStartObject();
            final JsonCapturer captured = new JsonCapturer(jg);
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new CapturingMustacheVisitor(this, captured);
                }
            };
            Mustache m = c.compile("complex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ComplexObject());
            jg.writeEndObject();
            jg.flush();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
            Object o = JsonInterpreterTest.toObject(jsonNode);
            sw = new StringWriter();
            m = createMustacheFactory().compile("nb+S&8{lpW=4");
            m.execute(sw, o);
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplex_literalMutationString2374 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template nb+S&8{lpW=4 not found", expected.getMessage());
        }
    }

    public void testComplex_literalMutationString2361_failAssert0_literalMutationString3187_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("eL&B8eK:V.T.");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile("complex.html");
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplex_literalMutationString2361 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplex_literalMutationString2361_failAssert0_literalMutationString3187 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template eL&B8eK:V.T. not found", expected.getMessage());
        }
    }

    public void testComplexnull54_failAssert0_literalMutationString300_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter json = new StringWriter();
                MappingJsonFactory jf = new MappingJsonFactory();
                final JsonGenerator jg = jf.createJsonGenerator(json);
                jg.writeStartObject();
                final JsonCapturer captured = new JsonCapturer(jg);
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new CapturingMustacheVisitor(this, captured);
                    }
                };
                Mustache m = c.compile("^omplex.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ComplexObject());
                jg.writeEndObject();
                jg.flush();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                JsonNode jsonNode = jf.createJsonParser(json.toString()).readValueAsTree();
                Object o = JsonInterpreterTest.toObject(jsonNode);
                sw = new StringWriter();
                m = createMustacheFactory().compile(null);
                m.execute(sw, o);
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexnull54 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testComplexnull54_failAssert0_literalMutationString300 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ^omplex.html not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString48333_failAssert0_literalMutationString48655_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("9v_`z;i!dK#(");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString48333 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString48333_failAssert0_literalMutationString48655 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 9v_`z;i!dK#( not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString48329_failAssert0_literalMutationString48581_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("{m 94;LhL%6Fx");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString48329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString48329_failAssert0_literalMutationString48581 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {m 94;LhL%6Fx not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString48329_failAssert0_add48890_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("{m 94;LhL6Fx");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject());
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString48329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString48329_failAssert0_add48890 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {m 94;LhL6Fx not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString48329_failAssert0null49000_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("{m 94;LhL6Fx");
                StringWriter sw = new StringWriter();
                m.execute(null, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString48329 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString48329_failAssert0null49000 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {m 94;LhL6Fx not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString48331_failAssert0_literalMutationString48534_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("yDb*?{Kat%G[");
                StringWriter sw = new StringWriter();
                m.execute(sw, new ParallelComplexObject()).close();
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testComplexParallel_literalMutationString48331 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString48331_failAssert0_literalMutationString48534 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template yDb*?{Kat%G[ not found", expected.getMessage());
        }
    }

    public void testComplexParallel_add48340_literalMutationString48441_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache o_testComplexParallel_add48340__3 = c.compile("bx>|OE]AM D(");
            Mustache m = c.compile("complex.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            String o_testComplexParallel_add48340__11 = TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_add48340_literalMutationString48441 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template bx>|OE]AM D( not found", expected.getMessage());
        }
    }

    public void testComplexParallel_literalMutationString48329_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("{m 94;LhL6Fx");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject()).close();
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_literalMutationString48329 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {m 94;LhL6Fx not found", expected.getMessage());
        }
    }

    public void testComplexParallel_add48342_literalMutationString48416_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("complex.h{tml");
            StringWriter sw = new StringWriter();
            m.execute(sw, new ParallelComplexObject());
            m.execute(sw, new ParallelComplexObject()).close();
            String o_testComplexParallel_add48342__12 = TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testComplexParallel_add48342_literalMutationString48416 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template complex.h{tml not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString54720_failAssert0null55077_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("Q:YzI5J(v-8S", new ParallelComplexObject());
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString54720 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString54720_failAssert0null55077 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Q:YzI5J(v-8S not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString54720_failAssert0_literalMutationString54946_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("Q:YzI5J(-8S", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString54720 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString54720_failAssert0_literalMutationString54946 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Q:YzI5J(-8S not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString54720_failAssert0_literalMutationString54955_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("Q:YzI5J(v-8S", new ParallelComplexObject());
                TestUtil.getContents(root, "complextxt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString54720 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString54720_failAssert0_literalMutationString54955 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Q:YzI5J(v-8S not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString54720_failAssert0() throws MustacheException, IOException {
        try {
            StringWriter sw = execute("Q:YzI5J(v-8S", new ParallelComplexObject());
            TestUtil.getContents(root, "complex.txt");
            sw.toString();
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString54720 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Q:YzI5J(v-8S not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString54718_failAssert0_literalMutationString54960_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("[GbbT A-#+6", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString54718 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString54718_failAssert0_literalMutationString54960 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [GbbT A-#+6 not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString54716_failAssert0_literalMutationString54923_failAssert0() throws MustacheException, IOException {
        try {
            {
                StringWriter sw = execute("CgoK{8n$q p*", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString54716 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString54716_failAssert0_literalMutationString54923 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template CgoK{8n$q p* not found", expected.getMessage());
        }
    }

    public void testSerialCallable_literalMutationString54720_failAssert0_add55033_failAssert0() throws MustacheException, IOException {
        try {
            {
                execute("Q:YzI5J(v-8S", new ParallelComplexObject());
                StringWriter sw = execute("Q:YzI5J(v-8S", new ParallelComplexObject());
                TestUtil.getContents(root, "complex.txt");
                sw.toString();
                junit.framework.TestCase.fail("testSerialCallable_literalMutationString54720 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testSerialCallable_literalMutationString54720_failAssert0_add55033 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Q:YzI5J(v-8S not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString57933_failAssert0_literalMutationString60255_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("page1.txt")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chrs");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57933 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57933_failAssert0_literalMutationString60255 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartialnull58101_failAssert0_literalMutationNumber59201_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartialnull58101 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDynamicPartialnull58101_failAssert0_literalMutationNumber59201 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + simple.html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString57933_failAssert0_add60980_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("page1.txt")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57933 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57933_failAssert0_add60980 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString57933_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("page1.txt")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
                                        return appendText(execute);
                                    }
                                });
                            } else {
                                super.partial(tc, variable);
                            }
                        }
                    };
                }
            };
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57933 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString57932_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("c")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
                                        return appendText(execute);
                                    }
                                });
                            } else {
                                super.partial(tc, variable);
                            }
                        }
                    };
                }
            };
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57932 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString57933_failAssert0_literalMutationString60230_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("page1.txt")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("" + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57933 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57933_failAssert0_literalMutationString60230 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString57932_failAssert0_add60868_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("c")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    variable.substring(1).trim();
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57932 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57932_failAssert0_add60868 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString57951_failAssert0null61587_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "S", null);
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57951 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57951_failAssert0null61587 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_remove58072_failAssert0_literalMutationNumber59301_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(0).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_remove58072 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_remove58072_failAssert0_literalMutationNumber59301 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + .html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString57932_failAssert0_literalMutationString59818_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("c")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "&");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57932 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57932_failAssert0_literalMutationString59818 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString57951_failAssert0_literalMutationString60410_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "S", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simdle.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57951 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57951_failAssert0_literalMutationString60410 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_add58044_failAssert0_literalMutationString58379_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("x")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_add58044 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_add58044_failAssert0_literalMutationString58379 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString57933_failAssert0null61575_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("page1.txt")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", null);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57933 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57933_failAssert0null61575 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString57951_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            if (variable.startsWith("+")) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                    @Override
                                    public synchronized void init() {
                                        filterText();
                                        partial = df.compile(new StringReader(name), "__dynpartial__", "S", "]");
                                        if ((partial) == null) {
                                            throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                        }
                                    }

                                    ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                    @Override
                                    public Writer execute(Writer writer, List<Object> scopes) {
                                        StringWriter sw = new StringWriter();
                                        partial.execute(sw, scopes);
                                        Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                        Writer execute = mustache.execute(writer, scopes);
                                        return appendText(execute);
                                    }
                                });
                            } else {
                                super.partial(tc, variable);
                            }
                        }
                    };
                }
            };
            Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, new HashMap<String, Object>() {
                {
                    put("name", "Chris");
                    put("value", 10000);
                    put("taxed_value", 6000);
                    put("in_ca", true);
                    put("foo", "simple");
                }
            });
            TestUtil.getContents(root, "simple.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57951 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString57932_failAssert0null61463_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("c")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "[", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", null);
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57932 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57932_failAssert0null61463 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template + [foo].html not found", expected.getMessage());
        }
    }

    public void testDynamicPartial_literalMutationString57951_failAssert0_add61005_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                if (variable.startsWith("+")) {
                                    TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                    list.add(new PartialCode(partialTC, df, variable.substring(1).trim()) {
                                        @Override
                                        public synchronized void init() {
                                            filterText();
                                            partial = df.compile(new StringReader(name), "__dynpartial__", "S", "]");
                                            if ((partial) == null) {
                                                throw new MustacheException(("Failed to parse partial name template: " + (name)));
                                            }
                                        }

                                        ConcurrentMap<String, Mustache> dynamicaPartialCache = new ConcurrentHashMap<>();

                                        @Override
                                        public Writer execute(Writer writer, List<Object> scopes) {
                                            StringWriter sw = new StringWriter();
                                            partial.execute(sw, scopes);
                                            Mustache mustache = dynamicaPartialCache.computeIfAbsent(sw.toString(), df::compilePartial);
                                            Writer execute = mustache.execute(writer, scopes);
                                            return appendText(execute);
                                        }
                                    });
                                } else {
                                    super.partial(tc, variable);
                                }
                            }
                        };
                    }
                };
                Mustache m = c.compile(new StringReader("{{>+ [foo].html}}"), "test.html");
                StringWriter sw = new StringWriter();
                m.execute(sw, new HashMap<String, Object>() {
                    {
                        put("name", "Chris");
                        put("name", "Chris");
                        put("value", 10000);
                        put("taxed_value", 6000);
                        put("in_ca", true);
                        put("foo", "simple");
                    }
                });
                TestUtil.getContents(root, "simple.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57951 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDynamicPartial_literalMutationString57951_failAssert0_add61005 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template [foo].html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString12368_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("{|?<;xign$");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_literalMutationString12368 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {|?<;xign$ not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString12368_failAssert0_add13021_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                c.compile("{|?<;xign$");
                Mustache m = c.compile("{|?<;xign$");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString12368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString12368_failAssert0_add13021 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {|?<;xign$ not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString12368_failAssert0_add13022_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("{|?<;xign$");
                StringWriter sw = new StringWriter();
                System.currentTimeMillis();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString12368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString12368_failAssert0_add13022 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {|?<;xign$ not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString12372_failAssert0_literalMutationString12679_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("i|ems.html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString12372 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString12372_failAssert0_literalMutationString12679 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template i|ems.html not found", expected.getMessage());
        }
    }

    public void testReadmenull12387_failAssert0_literalMutationString12561_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("Q/##%!Hat(");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testReadmenull12387 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testReadmenull12387_failAssert0_literalMutationString12561 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Q/##%!Hat( not found", expected.getMessage());
        }
    }

    public void testReadme_add12377_literalMutationString12455_failAssert0() throws MustacheException, IOException {
        try {
            DefaultMustacheFactory o_testReadme_add12377__1 = createMustacheFactory();
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("item|s.html");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            Writer o_testReadme_add12377__10 = m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            String o_testReadme_add12377__14 = TestUtil.getContents(root, "items.txt");
            sw.toString();
            junit.framework.TestCase.fail("testReadme_add12377_literalMutationString12455 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template item|s.html not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString12374_failAssert0_literalMutationString12635_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("X(W9u3Ll>8");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "c!XsI;e`M");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString12374 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString12374_failAssert0_literalMutationString12635 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template X(W9u3Ll>8 not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString12368_failAssert0_literalMutationString12707_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("{|?<;xign$");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString12368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString12368_failAssert0_literalMutationString12707 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {|?<;xign$ not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString12368_failAssert0_literalMutationString12705_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("{|?<;ign$");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString12368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString12368_failAssert0_literalMutationString12705 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {|?<;ign$ not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString12368_failAssert0null13106_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("{|?<;xign$");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString12368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString12368_failAssert0null13106 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {|?<;xign$ not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString12369_failAssert0_literalMutationString12625_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("Caj}F81N.");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString12369 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString12369_failAssert0_literalMutationString12625 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Caj}F81N. not found", expected.getMessage());
        }
    }

    public void testReadme_literalMutationString12368_failAssert0null13107_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("{|?<;xign$");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testReadme_literalMutationString12368 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadme_literalMutationString12368_failAssert0null13107 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template {|?<;xign$ not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString4716_failAssert0null5733_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items2.h[ml");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_29 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_30 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4716 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4716_failAssert0null5733 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items2.h[ml not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_add4731_remove5691() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add4731__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        String o_testReadmeSerial_add4731__13 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4731__13);
        String o_testReadmeSerial_add4731__14 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4731__14);
        String String_5 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_5);
        boolean boolean_6 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4731__13);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add4731__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_5);
    }

    public void testReadmeSerial_literalMutationString4716_failAssert0_literalMutationString5133_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items2.h[ml");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.t-xt");
                sw.toString();
                String String_29 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_30 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4716 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4716_failAssert0_literalMutationString5133 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items2.h[ml not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString4718_failAssert0null5742_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("Nw(}R)McbnC");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(null, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_35 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_36 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4718 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4718_failAssert0null5742 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Nw(}R)McbnC not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString4716_failAssert0_literalMutationString5138_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items2.h[ml");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_29 = "Sh<ould be a little bit more than 4 seconds: " + diff;
                boolean boolean_30 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4716 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4716_failAssert0_literalMutationString5138 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items2.h[ml not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString4716_failAssert0_add5618_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items2.h[ml");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_29 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_30 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4716 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4716_failAssert0_add5618 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items2.h[ml not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString4718_failAssert0_literalMutationNumber5241_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("Nw(}R)McbnC");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_35 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_36 = (diff > 3999) && (diff < 12000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4718 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4718_failAssert0_literalMutationNumber5241 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Nw(}R)McbnC not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString4716_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("items2.h[ml");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_29 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_30 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4716 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items2.h[ml not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString4720_failAssert0_literalMutationString5367_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("items2 html");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "");
                sw.toString();
                String String_47 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_48 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4720 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4720_failAssert0_literalMutationString5367 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template items2 html not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString4718_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = createMustacheFactory();
            Mustache m = c.compile("Nw(}R)McbnC");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context());
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_35 = "Should be a little bit more than 4 seconds: " + diff;
            boolean boolean_36 = (diff > 3999) && (diff < 6000);
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4718 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Nw(}R)McbnC not found", expected.getMessage());
        }
    }

    public void testReadmeSerial_literalMutationString4718_failAssert0_add5637_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = createMustacheFactory();
                Mustache m = c.compile("Nw(}R)McbnC");
                StringWriter sw = new StringWriter();
                System.currentTimeMillis();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context());
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_35 = "Should be a little bit more than 4 seconds: " + diff;
                boolean boolean_36 = (diff > 3999) && (diff < 6000);
                junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4718 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeSerial_literalMutationString4718_failAssert0_add5637 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Nw(}R)McbnC not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString53298_failAssert0null54462_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("abt:C!U{2!#");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, null);
                sw.toString();
                String String_75 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_76 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString53298 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString53298_failAssert0null54462 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template abt:C!U{2!# not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString53298_failAssert0() throws MustacheException, IOException {
        try {
            MustacheFactory c = initParallel();
            Mustache m = c.compile("abt:C!U{2!#");
            StringWriter sw = new StringWriter();
            long start = System.currentTimeMillis();
            m.execute(sw, new AmplInterpreterTest.Context()).close();
            long diff = (System.currentTimeMillis()) - start;
            TestUtil.getContents(root, "items.txt");
            sw.toString();
            String String_75 = "Should be a little bit more than 1 second: " + diff;
            boolean boolean_76 = (diff > 999) && (diff < 2000);
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString53298 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template abt:C!U{2!# not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_add53311_remove54413() throws MustacheException, IOException {
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
        String o_testReadmeParallel_add53311__16 = TestUtil.getContents(root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add53311__16);
        sw.toString();
        String String_61 = "Should be a little bit more than 1 second: " + diff;
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_61);
        boolean boolean_62 = (diff > 999) && (diff < 2000);
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isShutdown());
        TestCase.assertFalse(((ExecutorService) (((DefaultMustacheFactory) (c)).getExecutorService())).isTerminated());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeParallel_add53311__16);
        TestCase.assertEquals("Should be a little bit more than 1 second: 1001", String_61);
    }

    public void testReadmeParallel_literalMutationString53298_failAssert0_add54312_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("abt:C!U{2!#");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                sw.toString();
                String String_75 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_76 = (diff > 999) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString53298 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString53298_failAssert0_add54312 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template abt:C!U{2!# not found", expected.getMessage());
        }
    }

    public void testReadmeParallel_literalMutationString53298_failAssert0_literalMutationNumber53752_failAssert0() throws MustacheException, IOException {
        try {
            {
                MustacheFactory c = initParallel();
                Mustache m = c.compile("abt:C!U{2!#");
                StringWriter sw = new StringWriter();
                long start = System.currentTimeMillis();
                m.execute(sw, new AmplInterpreterTest.Context()).close();
                long diff = (System.currentTimeMillis()) - start;
                TestUtil.getContents(root, "items.txt");
                sw.toString();
                String String_75 = "Should be a little bit more than 1 second: " + diff;
                boolean boolean_76 = (diff > 0) && (diff < 2000);
                junit.framework.TestCase.fail("testReadmeParallel_literalMutationString53298 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testReadmeParallel_literalMutationString53298_failAssert0_literalMutationNumber53752 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template abt:C!U{2!# not found", expected.getMessage());
        }
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

    public void testDeferred_literalMutationString50868_failAssert0_add51933_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile(",%9(;`ts.K,J3");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                TestUtil.getContents(root, "deferred.txt");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString50868 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString50868_failAssert0_add51933 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,%9(;`ts.K,J3 not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString50868_failAssert0_literalMutationString51453_failAssert0() throws IOException {
        try {
            {
                DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
                mf.setExecutorService(Executors.newCachedThreadPool());
                Object context = new Object() {
                    String title = "Deferred";

                    Object deferred = new DeferringMustacheFactory.DeferredCallable();

                    Object deferredpartial = DeferringMustacheFactory.DEFERRED;
                };
                Mustache m = mf.compile(",%9(6;`ts.K,J3");
                StringWriter sw = new StringWriter();
                m.execute(sw, context).close();
                TestUtil.getContents(root, "deferred.txt");
                sw.toString();
                junit.framework.TestCase.fail("testDeferred_literalMutationString50868 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testDeferred_literalMutationString50868_failAssert0_literalMutationString51453 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,%9(6;`ts.K,J3 not found", expected.getMessage());
        }
    }

    public void testDeferred_add50878_literalMutationString51040_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
            mf.setExecutorService(Executors.newCachedThreadPool());
            Object context = new Object() {
                String title = "Deferred";

                Object deferred = new DeferringMustacheFactory.DeferredCallable();

                Object deferredpartial = DeferringMustacheFactory.DEFERRED;
            };
            Mustache o_testDeferred_add50878__11 = mf.compile(")SZ`gHtmedr?p");
            Mustache m = mf.compile("deferred.html");
            StringWriter sw = new StringWriter();
            m.execute(sw, context).close();
            String o_testDeferred_add50878__18 = TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_add50878_literalMutationString51040 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template )SZ`gHtmedr?p not found", expected.getMessage());
        }
    }

    public void testDeferred_literalMutationString50868_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
            mf.setExecutorService(Executors.newCachedThreadPool());
            Object context = new Object() {
                String title = "Deferred";

                Object deferred = new DeferringMustacheFactory.DeferredCallable();

                Object deferredpartial = DeferringMustacheFactory.DEFERRED;
            };
            Mustache m = mf.compile(",%9(;`ts.K,J3");
            StringWriter sw = new StringWriter();
            m.execute(sw, context).close();
            TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_literalMutationString50868 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ,%9(;`ts.K,J3 not found", expected.getMessage());
        }
    }

    public void testDeferred_remove50884_literalMutationString51118_failAssert0() throws IOException {
        try {
            DefaultMustacheFactory mf = new DeferringMustacheFactory(root);
            mf.setExecutorService(Executors.newCachedThreadPool());
            Object context = new Object() {
                String title = "Deferred";

                Object deferred = new DeferringMustacheFactory.DeferredCallable();

                Object deferredpartial = DeferringMustacheFactory.DEFERRED;
            };
            Mustache m = mf.compile("SYRt5zsiq>HqY");
            StringWriter sw = new StringWriter();
            m.execute(sw, context).close();
            String o_testDeferred_remove50884__16 = TestUtil.getContents(root, "deferred.txt");
            sw.toString();
            junit.framework.TestCase.fail("testDeferred_remove50884_literalMutationString51118 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SYRt5zsiq>HqY not found", expected.getMessage());
        }
    }

    public void testRelativePathsSameDir_literalMutationString62999_failAssert0null63823_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("+#t#*^QGJ,]or#T5Dq_");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62999 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62999_failAssert0null63823 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +#t#*^QGJ,]or#T5Dq_ not found", expected.getMessage());
        }
    }

    public void testRelativePathsSameDir_literalMutationString62999_failAssert0_literalMutationString63342_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("+#t#[*^QGJ,]or#T5Dq_");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62999 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62999_failAssert0_literalMutationString63342 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +#t#[*^QGJ,]or#T5Dq_ not found", expected.getMessage());
        }
    }

    public void testRelativePathsSameDir_literalMutationString63006_failAssert0_literalMutationString63431_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile(">t2e ;F`fhGC>SD<emu");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "relative/paths4txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString63006 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString63006_failAssert0_literalMutationString63431 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template >t2e ;F`fhGC>SD<emu not found", expected.getMessage());
        }
    }

    public void testRelativePathsSameDir_add63010_literalMutationString63155_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache o_testRelativePathsSameDir_add63010__3 = mf.compile("*iN=/NO<le>xiV!OmEI");
            Mustache compile = mf.compile("relative/paths.html");
            StringWriter sw = new StringWriter();
            compile.execute(sw, "").close();
            String o_testRelativePathsSameDir_add63010__10 = TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsSameDir_add63010_literalMutationString63155 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template *iN=/NO<le>xiV!OmEI not found", expected.getMessage());
        }
    }

    public void testRelativePathsSameDir_literalMutationString62999_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("+#t#*^QGJ,]or#T5Dq_");
            StringWriter sw = new StringWriter();
            compile.execute(sw, "").close();
            TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62999 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +#t#*^QGJ,]or#T5Dq_ not found", expected.getMessage());
        }
    }

    public void testRelativePathsSameDir_literalMutationString62997_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("relat^ive/paths.html");
            StringWriter sw = new StringWriter();
            compile.execute(sw, "").close();
            TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62997 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template relat^ive/paths.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsSameDir_literalMutationString62997_failAssert0null63831_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("relat^ive/paths.html");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62997 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62997_failAssert0null63831 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template relat^ive/paths.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsSameDir_literalMutationString62997_failAssert0_literalMutationString63371_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("relat^ive/p{aths.html");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62997 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62997_failAssert0_literalMutationString63371 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template relat^ive/p{aths.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsSameDir_literalMutationString62999_failAssert0_add63673_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                mf.compile("+#t#*^QGJ,]or#T5Dq_");
                Mustache compile = mf.compile("+#t#*^QGJ,]or#T5Dq_");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62999 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62999_failAssert0_add63673 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template +#t#*^QGJ,]or#T5Dq_ not found", expected.getMessage());
        }
    }

    public void testRelativePathsSameDir_literalMutationString62997_failAssert0_add63686_failAssert0() throws IOException {
        try {
            {
                createMustacheFactory();
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("relat^ive/paths.html");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62997 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString62997_failAssert0_add63686 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template relat^ive/paths.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsSameDir_literalMutationString63008_failAssert0_literalMutationString63323_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("h=uJ>F6]-HmNepbT7$s");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "relatie/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString63008 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsSameDir_literalMutationString63008_failAssert0_literalMutationString63323 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template h=uJ>F6]-HmNepbT7$s not found", expected.getMessage());
        }
    }

    public void testRelativePathsRootDir_literalMutationString49243_failAssert0null50083_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("o][n4iGan2vg4E@5d4(:Ze");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsRootDir_literalMutationString49243 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsRootDir_literalMutationString49243_failAssert0null50083 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template o][n4iGan2vg4E@5d4(:Ze not found", expected.getMessage());
        }
    }

    public void testRelativePathsRootDir_literalMutationString49243_failAssert0_literalMutationString49650_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("o][n4iGan2vg4E@5d4(:Ze");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsRootDir_literalMutationString49243 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsRootDir_literalMutationString49243_failAssert0_literalMutationString49650 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template o][n4iGan2vg4E@5d4(:Ze not found", expected.getMessage());
        }
    }

    public void testRelativePathsRootDir_literalMutationString49243_failAssert0_add49945_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                mf.compile("o][n4iGan2vg4E@5d4(:Ze");
                Mustache compile = mf.compile("o][n4iGan2vg4E@5d4(:Ze");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsRootDir_literalMutationString49243 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsRootDir_literalMutationString49243_failAssert0_add49945 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template o][n4iGan2vg4E@5d4(:Ze not found", expected.getMessage());
        }
    }

    public void testRelativePathsRootDirnull49261_failAssert0_literalMutationString49516_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("0tYYLbWwe+j_g>y]HOXbF`");
                StringWriter sw = new StringWriter();
                compile.execute(null, "").close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsRootDirnull49261 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testRelativePathsRootDirnull49261_failAssert0_literalMutationString49516 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 0tYYLbWwe+j_g>y]HOXbF` not found", expected.getMessage());
        }
    }

    public void testRelativePathsRootDir_add49255_literalMutationString49405_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("tp1|0u>z>m`Mt#%%044DX(");
            StringWriter sw = new StringWriter();
            compile.execute(sw, "").close();
            compile.execute(sw, "").close();
            String o_testRelativePathsRootDir_add49255__11 = TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsRootDir_add49255_literalMutationString49405 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template tp1|0u>z>m`Mt#%%044DX( not found", expected.getMessage());
        }
    }

    public void testRelativePathsRootDir_literalMutationString49243_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("o][n4iGan2vg4E@5d4(:Ze");
            StringWriter sw = new StringWriter();
            compile.execute(sw, "").close();
            TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsRootDir_literalMutationString49243 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template o][n4iGan2vg4E@5d4(:Ze not found", expected.getMessage());
        }
    }

    public void testRelativePathsRootDir_literalMutationString49250_failAssert0_literalMutationString49558_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("relative^rootpath.html");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "page1.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsRootDir_literalMutationString49250 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsRootDir_literalMutationString49250_failAssert0_literalMutationString49558 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template relative^rootpath.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsRootDir_literalMutationString49248_failAssert0_literalMutationString49574_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("$!o1?]9PK-oP|b#*r9L/mU");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "relative/pths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsRootDir_literalMutationString49248 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsRootDir_literalMutationString49248_failAssert0_literalMutationString49574 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $!o1?]9PK-oP|b#*r9L/mU not found", expected.getMessage());
        }
    }

    public void testPathsWithExtension_literalMutationString10729_failAssert0_literalMutationString11056_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("t<KPU7N[2]P*Tz-Ffdo7mv;!");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPathsWithExtension_literalMutationString10729 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPathsWithExtension_literalMutationString10729_failAssert0_literalMutationString11056 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template t<KPU7N[2]P*Tz-Ffdo7mv;! not found", expected.getMessage());
        }
    }

    public void testPathsWithExtension_literalMutationString10729_failAssert0_add11398_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("t<KPU7N[2]P*TzFfdo7mv;!");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "");
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testPathsWithExtension_literalMutationString10729 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testPathsWithExtension_literalMutationString10729_failAssert0_add11398 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template t<KPU7N[2]P*TzFfdo7mv;! not found", expected.getMessage());
        }
    }

    public void testPathsWithExtension_add10742_literalMutationString10905_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("ihosE+LTY VOl?Pu5#YeUWW");
            StringWriter sw = new StringWriter();
            Writer o_testPathsWithExtension_add10742__7 = compile.execute(sw, "");
            compile.execute(sw, "").close();
            String o_testPathsWithExtension_add10742__10 = TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPathsWithExtension_add10742_literalMutationString10905 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ihosE+LTY VOl?Pu5#YeUWW not found", expected.getMessage());
        }
    }

    public void testPathsWithExtension_literalMutationString10729_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("t<KPU7N[2]P*TzFfdo7mv;!");
            StringWriter sw = new StringWriter();
            compile.execute(sw, "").close();
            TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testPathsWithExtension_literalMutationString10729 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template t<KPU7N[2]P*TzFfdo7mv;! not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString36271_failAssert0null36976_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("L :^0nt!-L(O?YW-*yj(^:>-Xxl");
                StringWriter sw = new StringWriter();
                compile.execute(null, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString36271 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString36271_failAssert0null36976 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template L :^0nt!-L(O?YW-*yj(^:>-Xxl not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunctionnull36291_failAssert0_literalMutationString36494_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("i(+`o f]ah|,O/R@N1n,xU{`d{H");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunctionnull36291 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunctionnull36291_failAssert0_literalMutationString36494 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template i(+`o f]ah|,O/R@N1n,xU{`d{H not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_remove36288_literalMutationString36436_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("relative/functionpa`ths.html");
            StringWriter sw = new StringWriter();
            compile.execute(sw, new Object() {
                Function i = new TemplateFunction() {
                    @Override
                    public String apply(String s) {
                        return s;
                    }
                };
            }).close();
            String o_testRelativePathsTemplateFunction_remove36288__18 = TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_remove36288_literalMutationString36436 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template relative/functionpa`ths.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString36271_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("L :^0nt!-L(O?YW-*yj(^:>-Xxl");
            StringWriter sw = new StringWriter();
            compile.execute(sw, new Object() {
                Function i = new TemplateFunction() {
                    @Override
                    public String apply(String s) {
                        return s;
                    }
                };
            }).close();
            TestUtil.getContents(root, "relative/paths.txt");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString36271 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template L :^0nt!-L(O?YW-*yj(^:>-Xxl not found", expected.getMessage());
        }
    }

    public void testRelativePathsTemplateFunction_literalMutationString36271_failAssert0_add36829_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("L :^0nt!-L(O?YW-*yj(^:>-Xxl");
                StringWriter sw = new StringWriter();
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                });
                compile.execute(sw, new Object() {
                    Function i = new TemplateFunction() {
                        @Override
                        public String apply(String s) {
                            return s;
                        }
                    };
                }).close();
                TestUtil.getContents(root, "relative/paths.txt");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString36271 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsTemplateFunction_literalMutationString36271_failAssert0_add36829 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template L :^0nt!-L(O?YW-*yj(^:>-Xxl not found", expected.getMessage());
        }
    }

    public void testRelativePathFail_literalMutationString44195_literalMutationString44260_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            {
                Mustache compile = mf.compile(" does not exist");
            }
            junit.framework.TestCase.fail("testRelativePathFail_literalMutationString44195_literalMutationString44260 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testRelativePathFail_literalMutationString44195_literalMutationString44262_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            {
                Mustache compile = mf.compile("*,@uD2({)");
            }
            junit.framework.TestCase.fail("testRelativePathFail_literalMutationString44195_literalMutationString44262 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template *,@uD2({) not found", expected.getMessage());
        }
    }

    public void testRelativePathFail_literalMutationString44198_literalMutationString44241_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            {
                Mustache compile = mf.compile("C<GuGZU`>gtLQ9##6UP#UC-");
            }
            junit.framework.TestCase.fail("testRelativePathFail_literalMutationString44198_literalMutationString44241 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C<GuGZU`>gtLQ9##6UP#UC- not found", expected.getMessage());
        }
    }

    public void testRelativePathFail_literalMutationString44197_literalMutationString44254_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            {
                Mustache compile = mf.compile("XcPc)(;Ooi)>r)iqN#&QDIX");
            }
            junit.framework.TestCase.fail("testRelativePathFail_literalMutationString44197_literalMutationString44254 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template XcPc)(;Ooi)>r)iqN#&QDIX not found", expected.getMessage());
        }
    }

    public void testRelativePathFail_literalMutationString44197_literalMutationString44256_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            {
                Mustache compile = mf.compile("XcPc)(;Ooi)>r)iqN&QIX");
            }
            junit.framework.TestCase.fail("testRelativePathFail_literalMutationString44197_literalMutationString44256 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template XcPc)(;Ooi)>r)iqN&QIX not found", expected.getMessage());
        }
    }

    public void testRelativePathFail_literalMutationString44196_literalMutationString44231_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            {
                Mustache compile = mf.compile("relati7e/pat]hfail.html");
            }
            junit.framework.TestCase.fail("testRelativePathFail_literalMutationString44196_literalMutationString44231 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template relati7e/pat]hfail.html not found", expected.getMessage());
        }
    }

    public void testRelativePathFail_add44201_literalMutationString44216_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            {
                mf.compile("si{WJM^b(aP*R0:z6?}:U=");
                Mustache compile = mf.compile("relative/pathfail.html");
            }
            junit.framework.TestCase.fail("testRelativePathFail_add44201_literalMutationString44216 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template si{WJM^b(aP*R0:z6?}:U= not found", expected.getMessage());
        }
    }

    public void testRelativePathFail_literalMutationString44199_literalMutationString44249_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            {
                Mustache compile = mf.compile("*fR}&0Dm?;R;fh`i q]eM");
            }
            junit.framework.TestCase.fail("testRelativePathFail_literalMutationString44199_literalMutationString44249 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template *fR}&0Dm?;R;fh`i q]eM not found", expected.getMessage());
        }
    }

    public void testRelativePathFail_add44200_literalMutationString44229_failAssert0() throws IOException {
        try {
            createMustacheFactory();
            MustacheFactory mf = createMustacheFactory();
            {
                Mustache compile = mf.compile("DwN@?)QFlV-mKF5N8 @|i#");
            }
            junit.framework.TestCase.fail("testRelativePathFail_add44200_literalMutationString44229 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template DwN@?)QFlV-mKF5N8 @|i# not found", expected.getMessage());
        }
    }

    public void testRelativePathFail_literalMutationString44197_literalMutationString44255_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            {
                Mustache compile = mf.compile("XcPc)(;Ooij>r)iqN#&QIX");
            }
            junit.framework.TestCase.fail("testRelativePathFail_literalMutationString44197_literalMutationString44255 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template XcPc)(;Ooij>r)iqN#&QIX not found", expected.getMessage());
        }
    }

    public void testRelativePathFail_literalMutationString44197_literalMutationString44257_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            {
                Mustache compile = mf.compile("|r+v[mu*S02#r*_vA-uXBq");
            }
            junit.framework.TestCase.fail("testRelativePathFail_literalMutationString44197_literalMutationString44257 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template |r+v[mu*S02#r*_vA-uXBq not found", expected.getMessage());
        }
    }

    public void testRelativePathFail_literalMutationString44196_literalMutationString44232_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            {
                Mustache compile = mf.compile("?{? 2IjWD15p%7Gj&tU<bZ");
            }
            junit.framework.TestCase.fail("testRelativePathFail_literalMutationString44196_literalMutationString44232 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ?{? 2IjWD15p%7Gj&tU<bZ not found", expected.getMessage());
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

    public void testRelativePathsDotDotDir_literalMutationString40484_failAssert0_add41136_failAssert0() throws IOException {
        try {
            {
                createMustacheFactory();
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("xDz7Aa:lH!h/J(01pQ$-");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "uninterestingpartial.html");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDir_literalMutationString40484 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDir_literalMutationString40484_failAssert0_add41136 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template xDz7Aa:lH!h/J(01pQ$- not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDir_literalMutationString40484_failAssert0_literalMutationString40783_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = createMustacheFactory();
                Mustache compile = mf.compile("xDz7Aa:lH!h+/J(01pQ$-");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "uninterestingpartial.html");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDir_literalMutationString40484 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDir_literalMutationString40484_failAssert0_literalMutationString40783 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template xDz7Aa:lH!h+/J(01pQ$- not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDir_literalMutationString40484_failAssert0() throws IOException {
        try {
            MustacheFactory mf = createMustacheFactory();
            Mustache compile = mf.compile("xDz7Aa:lH!h/J(01pQ$-");
            StringWriter sw = new StringWriter();
            compile.execute(sw, "").close();
            TestUtil.getContents(root, "uninterestingpartial.html");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsDotDotDir_literalMutationString40484 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template xDz7Aa:lH!h/J(01pQ$- not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78066_failAssert0null78453_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                Mustache compile = mf.compile("Lvj#?s!fhk]m39@-1eIc6i^w<");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78066 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78066_failAssert0null78453 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Lvj#?s!fhk]m39@-1eIc6i^w< not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78066_failAssert0null78451_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                Mustache compile = mf.compile("Lvj#?s!fhk]m39@-1eIc6i^w<");
                StringWriter sw = new StringWriter();
                compile.execute(null, "").close();
                TestUtil.getContents(root, "nonrelative.html");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78066 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78066_failAssert0null78451 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Lvj#?s!fhk]m39@-1eIc6i^w< not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78066_failAssert0_add78377_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                Mustache compile = mf.compile("Lvj#?s!fhk]m39@-1eIc6i^w<");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "nonrelative.html");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78066 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78066_failAssert0_add78377 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Lvj#?s!fhk]m39@-1eIc6i^w< not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78066_failAssert0() throws IOException {
        try {
            MustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public String resolvePartialPath(String dir, String name, String extension) {
                    return name + extension;
                }
            };
            Mustache compile = mf.compile("Lvj#?s!fhk]m39@-1eIc6i^w<");
            StringWriter sw = new StringWriter();
            compile.execute(sw, "").close();
            TestUtil.getContents(root, "nonrelative.html");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78066 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Lvj#?s!fhk]m39@-1eIc6i^w< not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78063_failAssert0_literalMutationString78244_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                Mustache compile = mf.compile("rel]tive/nonrelative.html");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "nonrelative_html");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78063 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78063_failAssert0_literalMutationString78244 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template rel]tive/nonrelative.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78071_failAssert0_literalMutationString78337_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                Mustache compile = mf.compile("glG8kuzsY5^pXWEC4b}bxbp$w");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78071 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78071_failAssert0_literalMutationString78337 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template glG8kuzsY5^pXWEC4b}bxbp$w not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78063_failAssert0null78461_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                Mustache compile = mf.compile("rel]tive/nonrelative.html");
                StringWriter sw = new StringWriter();
                compile.execute(null, "").close();
                TestUtil.getContents(root, "nonrelative.html");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78063 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78063_failAssert0null78461 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template rel]tive/nonrelative.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78062_failAssert0_literalMutationString78189_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                Mustache compile = mf.compile(" does not exist");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "nonrelative.html");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78062 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78062_failAssert0_literalMutationString78189 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78065_failAssert0_literalMutationString78281_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                Mustache compile = mf.compile("rela<tive/nonrelativke.html");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "nonrelative.html");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78065 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78065_failAssert0_literalMutationString78281 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template rela<tive/nonrelativke.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78063_failAssert0_literalMutationString78245_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                Mustache compile = mf.compile("rel]tive/nonrelative.html");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "nonrelative.h/tml");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78063 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78063_failAssert0_literalMutationString78245 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template rel]tive/nonrelative.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78063_failAssert0_add78384_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                mf.compile("rel]tive/nonrelative.html");
                Mustache compile = mf.compile("rel]tive/nonrelative.html");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "nonrelative.html");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78063 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78063_failAssert0_add78384 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template rel]tive/nonrelative.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78073_failAssert0_literalMutationString78311_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                Mustache compile = mf.compile("jiQXDw+53wfywVr]rt3sA;_5L");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "nonrelativ.html");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78073 should have thrown FileNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78073_failAssert0_literalMutationString78311 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template jiQXDw+53wfywVr]rt3sA;_5L not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78063_failAssert0() throws IOException {
        try {
            MustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public String resolvePartialPath(String dir, String name, String extension) {
                    return name + extension;
                }
            };
            Mustache compile = mf.compile("rel]tive/nonrelative.html");
            StringWriter sw = new StringWriter();
            compile.execute(sw, "").close();
            TestUtil.getContents(root, "nonrelative.html");
            sw.toString();
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78063 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template rel]tive/nonrelative.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78063_failAssert0_add78388_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                Mustache compile = mf.compile("rel]tive/nonrelative.html");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "nonrelative.html");
                sw.toString();
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78063 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78063_failAssert0_add78388 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template rel]tive/nonrelative.html not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78066_failAssert0_add78372_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                mf.compile("Lvj#?s!fhk]m39@-1eIc6i^w<");
                Mustache compile = mf.compile("Lvj#?s!fhk]m39@-1eIc6i^w<");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "nonrelative.html");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78066 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78066_failAssert0_add78372 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Lvj#?s!fhk]m39@-1eIc6i^w< not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverride_literalMutationString78066_failAssert0_literalMutationString78215_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                Mustache compile = mf.compile("Lvj#?s!fhk]m39@-1eIc6i^w<");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, "nonrelVative.html");
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78066 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverride_literalMutationString78066_failAssert0_literalMutationString78215 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Lvj#?s!fhk]m39@-1eIc6i^w< not found", expected.getMessage());
        }
    }

    public void testRelativePathsDotDotDirOverridenull78087_failAssert0_literalMutationString78180_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public String resolvePartialPath(String dir, String name, String extension) {
                        return name + extension;
                    }
                };
                Mustache compile = mf.compile("NMZ!GdP)mmDhQ+X 1(O((8(q6");
                StringWriter sw = new StringWriter();
                compile.execute(sw, "").close();
                TestUtil.getContents(root, null);
                sw.toString();
                junit.framework.TestCase.fail("testRelativePathsDotDotDirOverridenull78087 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testRelativePathsDotDotDirOverridenull78087_failAssert0_literalMutationString78180 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template NMZ!GdP)mmDhQ+X 1(O((8(q6 not found", expected.getMessage());
        }
    }

    public void testOverrideExtension_literalMutationString50318_failAssert0_literalMutationString50510_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                            }
                        };
                    }
                };
                StringWriter sw = new StringWriter();
                mf.compile("(=&C^K[Ay").execute(sw, "").close();
                sw.toString();
                junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50318 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50318_failAssert0_literalMutationString50510 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template (=&C^K[Ay not found", expected.getMessage());
        }
    }

    public void testOverrideExtension_literalMutationString50321_failAssert0null50624_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                            }
                        };
                    }
                };
                StringWriter sw = new StringWriter();
                mf.compile("SA=EL9@,L+>iSP/qKrJg>$").execute(sw, "").close();
                sw.toString();
                junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321_failAssert0null50624 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SA=EL9@,L+>iSP/qKrJg>$ not found", expected.getMessage());
        }
    }

    public void testOverrideExtension_literalMutationString50321_failAssert0() throws IOException {
        try {
            MustacheFactory mf = new DefaultMustacheFactory(root) {
                @Override
                public MustacheVisitor createMustacheVisitor() {
                    return new DefaultMustacheVisitor(this) {
                        @Override
                        public void partial(TemplateContext tc, String variable) {
                            TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                            list.add(new PartialCode(partialTC, df, variable) {
                                @Override
                                protected String partialName() {
                                    return name;
                                }
                            });
                        }
                    };
                }
            };
            StringWriter sw = new StringWriter();
            mf.compile("SA=EL9@,L+>iSP/qKrJg>$").execute(sw, "").close();
            sw.toString();
            junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SA=EL9@,L+>iSP/qKrJg>$ not found", expected.getMessage());
        }
    }

    public void testOverrideExtension_literalMutationString50321_failAssert0_add50565_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                            }
                        };
                    }
                };
                StringWriter sw = new StringWriter();
                mf.compile("SA=EL9@,L+>iSP/qKrJg>$").execute(sw, "").close();
                sw.toString();
                junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321_failAssert0_add50565 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SA=EL9@,L+>iSP/qKrJg>$ not found", expected.getMessage());
        }
    }

    public void testOverrideExtension_literalMutationString50321_failAssert0_add50563_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                tc.line();
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                            }
                        };
                    }
                };
                StringWriter sw = new StringWriter();
                mf.compile("SA=EL9@,L+>iSP/qKrJg>$").execute(sw, "").close();
                sw.toString();
                junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321_failAssert0_add50563 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SA=EL9@,L+>iSP/qKrJg>$ not found", expected.getMessage());
        }
    }

    public void testOverrideExtension_literalMutationString50321_failAssert0_literalMutationString50473_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                TemplateContext partialTC = new TemplateContext("{{", "page1.txt", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                            }
                        };
                    }
                };
                StringWriter sw = new StringWriter();
                mf.compile("SA=EL9@,L+>iSP/qKrJg>$").execute(sw, "").close();
                sw.toString();
                junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321_failAssert0_literalMutationString50473 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SA=EL9@,L+>iSP/qKrJg>$ not found", expected.getMessage());
        }
    }

    public void testOverrideExtension_literalMutationString50321_failAssert0_literalMutationString50469_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                TemplateContext partialTC = new TemplateContext("", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                            }
                        };
                    }
                };
                StringWriter sw = new StringWriter();
                mf.compile("SA=EL9@,L+>iSP/qKrJg>$").execute(sw, "").close();
                sw.toString();
                junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321_failAssert0_literalMutationString50469 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SA=EL9@,L+>iSP/qKrJg>$ not found", expected.getMessage());
        }
    }

    public void testOverrideExtensionnull50344_failAssert0_literalMutationString50465_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                            }
                        };
                    }
                };
                StringWriter sw = new StringWriter();
                mf.compile("$1t>NM6k=F&o48Yw:&:1V,").execute(null, "").close();
                sw.toString();
                junit.framework.TestCase.fail("testOverrideExtensionnull50344 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testOverrideExtensionnull50344_failAssert0_literalMutationString50465 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template $1t>NM6k=F&o48Yw:&:1V, not found", expected.getMessage());
        }
    }

    public void testOverrideExtension_literalMutationString50321_failAssert0_literalMutationString50471_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                TemplateContext partialTC = new TemplateContext(";", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                            }
                        };
                    }
                };
                StringWriter sw = new StringWriter();
                mf.compile("SA=EL9@,L+>iSP/qKrJg>$").execute(sw, "").close();
                sw.toString();
                junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321_failAssert0_literalMutationString50471 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SA=EL9@,L+>iSP/qKrJg>$ not found", expected.getMessage());
        }
    }

    public void testOverrideExtension_literalMutationString50321_failAssert0null50621_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                            }
                        };
                    }
                };
                StringWriter sw = new StringWriter();
                mf.compile("SA=EL9@,L+>iSP/qKrJg>$").execute(sw, "").close();
                sw.toString();
                junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321_failAssert0null50621 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SA=EL9@,L+>iSP/qKrJg>$ not found", expected.getMessage());
        }
    }

    public void testOverrideExtension_literalMutationString50321_failAssert0null50623_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                            }
                        };
                    }
                };
                StringWriter sw = new StringWriter();
                mf.compile("SA=EL9@,L+>iSP/qKrJg>$").execute(sw, "").close();
                sw.toString();
                junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321_failAssert0null50623 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SA=EL9@,L+>iSP/qKrJg>$ not found", expected.getMessage());
        }
    }

    public void testOverrideExtension_literalMutationString50321_failAssert0_add50570_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                            }
                        };
                    }
                };
                StringWriter sw = new StringWriter();
                mf.compile("SA=EL9@,L+>iSP/qKrJg>$").execute(sw, "").close();
                sw.toString();
                junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321_failAssert0_add50570 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SA=EL9@,L+>iSP/qKrJg>$ not found", expected.getMessage());
        }
    }

    public void testOverrideExtension_literalMutationString50321_failAssert0_add50562_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                tc.file();
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                            }
                        };
                    }
                };
                StringWriter sw = new StringWriter();
                mf.compile("SA=EL9@,L+>iSP/qKrJg>$").execute(sw, "").close();
                sw.toString();
                junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321_failAssert0_add50562 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SA=EL9@,L+>iSP/qKrJg>$ not found", expected.getMessage());
        }
    }

    public void testOverrideExtension_literalMutationString50321_failAssert0_literalMutationString50483_failAssert0() throws IOException {
        try {
            {
                MustacheFactory mf = new DefaultMustacheFactory(root) {
                    @Override
                    public MustacheVisitor createMustacheVisitor() {
                        return new DefaultMustacheVisitor(this) {
                            @Override
                            public void partial(TemplateContext tc, String variable) {
                                TemplateContext partialTC = new TemplateContext("{{", "}}", tc.file(), tc.line(), tc.startOfLine());
                                list.add(new PartialCode(partialTC, df, variable) {
                                    @Override
                                    protected String partialName() {
                                        return name;
                                    }
                                });
                            }
                        };
                    }
                };
                StringWriter sw = new StringWriter();
                mf.compile("SA=EL9@,L+>iSP/qKrJg>$").execute(sw, "page1.txt").close();
                sw.toString();
                junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321 should have thrown MustacheNotFoundException");
            }
            junit.framework.TestCase.fail("testOverrideExtension_literalMutationString50321_failAssert0_literalMutationString50483 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template SA=EL9@,L+>iSP/qKrJg>$ not found", expected.getMessage());
        }
    }

    public void testMustacheNotFoundException_literalMutationString53069_literalMutationString53131_failAssert0() throws Exception {
        try {
            String nonExistingMustache = "Whee%)/A6";
            {
                new DefaultMustacheFactory().compile(nonExistingMustache);
            }
            junit.framework.TestCase.fail("testMustacheNotFoundException_literalMutationString53069_literalMutationString53131 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template Whee%)/A6 not found", expected.getMessage());
        }
    }

    public void testMustacheNotFoundException_literalMutationString53069_literalMutationString53130_failAssert0() throws Exception {
        try {
            String nonExistingMustache = " does not exist";
            {
                new DefaultMustacheFactory().compile(nonExistingMustache);
            }
            junit.framework.TestCase.fail("testMustacheNotFoundException_literalMutationString53069_literalMutationString53130 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testMustacheNotFoundException_literalMutationString53065_literalMutationString53124_failAssert0() throws Exception {
        try {
            String nonExistingMustache = "%";
            {
                new DefaultMustacheFactory().compile(nonExistingMustache);
            }
            junit.framework.TestCase.fail("testMustacheNotFoundException_literalMutationString53065_literalMutationString53124 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template % not found", expected.getMessage());
        }
    }

    public void testMustacheNotFoundException_add53071_literalMutationString53097_failAssert0() throws Exception {
        try {
            String nonExistingMustache = "4]M";
            {
                new DefaultMustacheFactory().compile(nonExistingMustache);
                new DefaultMustacheFactory().compile(nonExistingMustache);
            }
            junit.framework.TestCase.fail("testMustacheNotFoundException_add53071_literalMutationString53097 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 4]M not found", expected.getMessage());
        }
    }

    public void testMustacheNotFoundException_literalMutationString53068_literalMutationString53109_failAssert0() throws Exception {
        try {
            String nonExistingMustache = "<4";
            {
                new DefaultMustacheFactory().compile(nonExistingMustache);
            }
            junit.framework.TestCase.fail("testMustacheNotFoundException_literalMutationString53068_literalMutationString53109 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <4 not found", expected.getMessage());
        }
    }

    public void testMustacheNotFoundException_literalMutationString53068_literalMutationString53107_failAssert0() throws Exception {
        try {
            String nonExistingMustache = "J>4";
            {
                new DefaultMustacheFactory().compile(nonExistingMustache);
            }
            junit.framework.TestCase.fail("testMustacheNotFoundException_literalMutationString53068_literalMutationString53107 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template J>4 not found", expected.getMessage());
        }
    }

    public void testMustacheNotFoundException_literalMutationString53067_literalMutationString53115_failAssert0() throws Exception {
        try {
            String nonExistingMustache = "O}04";
            {
                new DefaultMustacheFactory().compile(nonExistingMustache);
            }
            junit.framework.TestCase.fail("testMustacheNotFoundException_literalMutationString53067_literalMutationString53115 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template O}04 not found", expected.getMessage());
        }
    }

    public void testMustacheNotFoundException_literalMutationString53066_literalMutationString53125_failAssert0() throws Exception {
        try {
            String nonExistingMustache = "`";
            {
                new DefaultMustacheFactory().compile(nonExistingMustache);
            }
            junit.framework.TestCase.fail("testMustacheNotFoundException_literalMutationString53066_literalMutationString53125 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ` not found", expected.getMessage());
        }
    }

    public void testMustacheNotFoundException_literalMutationString53068_literalMutationString53104_failAssert0() throws Exception {
        try {
            String nonExistingMustache = "<2>4";
            {
                new DefaultMustacheFactory().compile(nonExistingMustache);
            }
            junit.framework.TestCase.fail("testMustacheNotFoundException_literalMutationString53068_literalMutationString53104 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template <2>4 not found", expected.getMessage());
        }
    }

    public void testMustacheNotFoundException_literalMutationString53070_literalMutationString53121_failAssert0() throws Exception {
        try {
            String nonExistingMustache = "#prn";
            {
                new DefaultMustacheFactory().compile(nonExistingMustache);
            }
            junit.framework.TestCase.fail("testMustacheNotFoundException_literalMutationString53070_literalMutationString53121 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template #prn not found", expected.getMessage());
        }
    }

    public void testMustacheNotFoundException_add53071_literalMutationString53096_failAssert0() throws Exception {
        try {
            String nonExistingMustache = "4<04";
            {
                new DefaultMustacheFactory().compile(nonExistingMustache);
                new DefaultMustacheFactory().compile(nonExistingMustache);
            }
            junit.framework.TestCase.fail("testMustacheNotFoundException_add53071_literalMutationString53096 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 4<04 not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_add39187_literalMutationString39213_failAssert0() throws Exception {
        try {
            {
                StringWriter sw = execute("8s_SmlA}c^4s10.:n^a", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_add39187_literalMutationString39213 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 8s_SmlA}c^4s10.:n^a not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_literalMutationString39182_literalMutationString39227_failAssert0() throws Exception {
        try {
            {
                StringWriter sw = execute("@b#5jb3}m", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_literalMutationString39182_literalMutationString39227 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template @b#5jb3}m not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_add39186_literalMutationString39199_failAssert0() throws Exception {
        try {
            {
                execute("C3,g880=:/I&1:mbZSx", new AmplInterpreterTest.Context());
                StringWriter sw = execute("infiniteparent.html", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_add39186_literalMutationString39199 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template C3,g880=:/I&1:mbZSx not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_literalMutationString39181_literalMutationString39247_failAssert0() throws Exception {
        try {
            {
                StringWriter sw = execute("j(co9+-6C}sNpdQ2c>%", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_literalMutationString39181_literalMutationString39247 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template j(co9+-6C}sNpdQ2c>% not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_literalMutationString39181_literalMutationString39249_failAssert0() throws Exception {
        try {
            {
                StringWriter sw = execute("infinitepare&t.h{tml", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_literalMutationString39181_literalMutationString39249 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template infinitepare&t.h{tml not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_literalMutationString39182_literalMutationString39224_failAssert0() throws Exception {
        try {
            {
                StringWriter sw = execute(" does not exist", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_literalMutationString39182_literalMutationString39224 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template  does not exist not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_literalMutationString39183_literalMutationString39243_failAssert0() throws Exception {
        try {
            {
                StringWriter sw = execute("wSv|<Qe5E/+]oJ3OV_", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_literalMutationString39183_literalMutationString39243 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template wSv|<Qe5E/+]oJ3OV_ not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_literalMutationString39183_literalMutationString39241_failAssert0() throws Exception {
        try {
            {
                StringWriter sw = execute("``}fWgi^S2B=Hb#f16S", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_literalMutationString39183_literalMutationString39241 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template ``}fWgi^S2B=Hb#f16S not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_literalMutationString39185_literalMutationString39220_failAssert0() throws Exception {
        try {
            {
                StringWriter sw = execute("infinite^arent.tml", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_literalMutationString39185_literalMutationString39220 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template infinite^arent.tml not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_literalMutationString39184_literalMutationString39237_failAssert0() throws Exception {
        try {
            {
                StringWriter sw = execute("36@r[>NND[YYZE?t!g6=", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_literalMutationString39184_literalMutationString39237 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template 36@r[>NND[YYZE?t!g6= not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_literalMutationString39184_literalMutationString39234_failAssert0() throws Exception {
        try {
            {
                StringWriter sw = execute("infiniteparent:.h.tml", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_literalMutationString39184_literalMutationString39234 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template infiniteparent:.h.tml not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_literalMutationString39183_literalMutationString39242_failAssert0() throws Exception {
        try {
            {
                StringWriter sw = execute("w2v|<LQe5E/+]oJ3OV_", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_literalMutationString39183_literalMutationString39242 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template w2v|<LQe5E/+]oJ3OV_ not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_literalMutationString39185_literalMutationString39221_failAssert0() throws Exception {
        try {
            {
                StringWriter sw = execute("P4hI[#%*/MQf|nzr[m", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_literalMutationString39185_literalMutationString39221 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template P4hI[#%*/MQf|nzr[m not found", expected.getMessage());
        }
    }

    public void testLimitedDepthRecursion_literalMutationString39183_literalMutationString39240_failAssert0() throws Exception {
        try {
            {
                StringWriter sw = execute("wSv|<LQe5E/+]oJ23OV_", new AmplInterpreterTest.Context());
            }
            junit.framework.TestCase.fail("testLimitedDepthRecursion_literalMutationString39183_literalMutationString39240 should have thrown MustacheNotFoundException");
        } catch (MustacheNotFoundException expected) {
            TestCase.assertEquals("Template wSv|<LQe5E/+]oJ23OV_ not found", expected.getMessage());
        }
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

