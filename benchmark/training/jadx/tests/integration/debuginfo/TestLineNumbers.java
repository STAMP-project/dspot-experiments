package jadx.tests.integration.debuginfo;


import CodeWriter.NL;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.FieldNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;


public class TestLineNumbers extends IntegrationTest {
    public static class TestCls {
        int field;

        public void func() {
        }

        public static class Inner {
            int innerField;

            public void innerFunc() {
            }

            public void innerFunc2() {
                new Runnable() {
                    @Override
                    public void run() {
                    }
                }.run();
            }

            public void innerFunc3() {
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestLineNumbers.TestCls.class);
        String code = cls.getCode().toString();
        FieldNode field = cls.searchFieldByName("field");
        MethodNode func = cls.searchMethodByName("func()V");
        ClassNode inner = cls.getInnerClasses().get(0);
        MethodNode innerFunc = inner.searchMethodByName("innerFunc()V");
        MethodNode innerFunc2 = inner.searchMethodByName("innerFunc2()V");
        MethodNode innerFunc3 = inner.searchMethodByName("innerFunc3()V");
        FieldNode innerField = inner.searchFieldByName("innerField");
        // check source lines (available only for instructions and methods)
        int testClassLine = 18;
        Assert.assertEquals((testClassLine + 3), func.getSourceLine());
        Assert.assertEquals((testClassLine + 9), innerFunc.getSourceLine());
        Assert.assertEquals((testClassLine + 12), innerFunc2.getSourceLine());
        Assert.assertEquals((testClassLine + 20), innerFunc3.getSourceLine());
        // check decompiled lines
        String[] lines = code.split(NL);
        TestLineNumbers.checkLine(lines, field, "int field;");
        TestLineNumbers.checkLine(lines, func, "public void func() {");
        TestLineNumbers.checkLine(lines, inner, "public static class Inner {");
        TestLineNumbers.checkLine(lines, innerField, "int innerField;");
        TestLineNumbers.checkLine(lines, innerFunc, "public void innerFunc() {");
        TestLineNumbers.checkLine(lines, innerFunc2, "public void innerFunc2() {");
        TestLineNumbers.checkLine(lines, innerFunc3, "public void innerFunc3() {");
    }
}

