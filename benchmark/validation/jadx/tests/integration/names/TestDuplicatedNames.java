package jadx.tests.integration.names;


import jadx.tests.api.SmaliTest;
import org.junit.Test;


public class TestDuplicatedNames extends SmaliTest {
    /* public static class TestCls {

    public Object fieldName;
    public String fieldName;

    public Object run() {
    return this.fieldName;
    }

    public String run() {
    return this.fieldName;
    }
    }
     */
    @Test
    public void test() {
        commonChecks();
    }

    @Test
    public void testWithDeobf() {
        enableDeobfuscation();
        commonChecks();
    }
}

