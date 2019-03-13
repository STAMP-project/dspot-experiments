package org.drools.compiler.integrationtests;


import ResourceType.DRL;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class SwitchOverStringTest {
    private static final String FUNCTION_WITH_SWITCH_OVER_STRING = "function void theTest(String input) {\n" + (((((((("  switch(input) {\n" + "    case \"Hello World\" :") + "      System.out.println(\"yep\");\n") + "      break;\n") + "    default :\n") + "      System.out.println(\"uh\");\n") + "      break;\n") + "  }\n") + "}");

    @Test
    public void testCompileSwitchOverStringWithLngLevel17() {
        double javaVersion = Double.valueOf(System.getProperty("java.specification.version"));
        Assume.assumeTrue("Test only makes sense on Java 7+.", (javaVersion >= 1.7));
        System.setProperty("drools.dialect.java.compiler.lnglevel", "1.7");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(SwitchOverStringTest.FUNCTION_WITH_SWITCH_OVER_STRING.getBytes()), DRL);
        Assert.assertFalse("Compilation error(s) occurred!", kbuilder.hasErrors());
    }

    @Test
    public void testShouldFailToCompileSwitchOverStringWithLngLevel16() {
        System.setProperty("drools.dialect.java.compiler.lnglevel", "1.6");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(SwitchOverStringTest.FUNCTION_WITH_SWITCH_OVER_STRING.getBytes()), DRL);
        Assert.assertTrue("Compilation error(s) expected!", kbuilder.hasErrors());
    }
}

