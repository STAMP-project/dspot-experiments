package org.drools.modelcompiler;


import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;


public class UseClassFieldsInRulesTest extends BaseModelTest {
    public UseClassFieldsInRulesTest(BaseModelTest.RUN_TYPE testRunType) {
        super(testRunType);
    }

    public static class ClassWithFields {
        public final int field = 3;

        public static final int STATIC_FIELD = 3;

        public int getValue() {
            return field;
        }
    }

    @Test
    public void testUseAccessor() {
        doCheck(true, "value > 2");
    }

    @Test
    public void testUseField() {
        doCheck(true, "field > 2");
    }

    @Test
    public void testUseStaticField() {
        doCheck(true, "STATIC_FIELD > 2");
    }

    @Test
    public void testUseAccessorInFunction() {
        doCheck(true, "greaterThan( value, 2 )");
    }

    @Test
    public void testUseFieldInFunction() {
        doCheck(true, "greaterThan( field, 2 )");
    }

    @Test
    public void testUseStaticFieldInFunction() {
        doCheck(true, "greaterThan( STATIC_FIELD, 2 )");
    }

    @Test
    public void testUseAccessorInMethod() {
        doCheck(false, "greaterThanMethod( value, 2 )");
    }

    @Test
    public void testUseFieldInMethod() {
        doCheck(false, "greaterThanMethod( field, 2 )");
    }

    @Test
    public void testUseStaticFieldInMethod() {
        doCheck(false, "greaterThanMethod( STATIC_FIELD, 2 )");
    }

    @Test
    public void testMethodInFrom() {
        String str = (((((((("import " + (UseClassFieldsInRulesTest.ClassWithFields.class.getCanonicalName())) + "\n") + "import static ") + (UseClassFieldsInRulesTest.class.getCanonicalName())) + ".*\n") + "rule R when\n") + "    Boolean (booleanValue == true) from greaterThanMethod( ClassWithFields.STATIC_FIELD, 2 )\n") + "then\n") + "end ";
        KieSession ksession = getKieSession(str);
        Assert.assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testMethodInEval() {
        String str = (((((((("import " + (UseClassFieldsInRulesTest.ClassWithFields.class.getCanonicalName())) + "\n") + "import static ") + (UseClassFieldsInRulesTest.class.getCanonicalName())) + ".*\n") + "rule R when\n") + "    eval( greaterThanMethod( ClassWithFields.STATIC_FIELD, 2 ) )\n") + "then\n") + "end ";
        KieSession ksession = getKieSession(str);
        Assert.assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testFunctionInFrom() {
        String str = (((((("import " + (UseClassFieldsInRulesTest.ClassWithFields.class.getCanonicalName())) + "\n") + "function boolean greaterThan(int i1, int i2) { return i1 > i2; }\n") + "rule R when\n") + "    Boolean (booleanValue == true) from greaterThan( ClassWithFields.STATIC_FIELD, 2 )\n") + "then\n") + "end ";
        KieSession ksession = getKieSession(str);
        Assert.assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testFunctionWithEval() {
        String str = (((((("import " + (UseClassFieldsInRulesTest.ClassWithFields.class.getCanonicalName())) + "\n") + "function boolean greaterThan(int i1, int i2) { return i1 > i2; }\n") + "rule R when\n") + "    eval( greaterThan( ClassWithFields.STATIC_FIELD, 2 ) )\n") + "then\n") + "end ";
        KieSession ksession = getKieSession(str);
        Assert.assertEquals(1, ksession.fireAllRules());
    }
}

