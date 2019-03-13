package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestComplexIf extends SmaliTest {
    /* public final class TestComplexIf {
    private String a;
    private int b;
    private float c;

    public final boolean test() {
    if (this.a.equals("GT-P6200") || this.a.equals("GT-P6210") || ... ) {
    return true;
    }
    if (this.a.equals("SM-T810") || this.a.equals("SM-T813") || ...) {
    return false;
    }
    return this.c > 160.0f ? true : this.c <= 0.0f && ((this.b & 15) == 4 ? 1 : null) != null;
    }
    }
     */
    @Test
    public void test() {
        ClassNode cls = getClassNodeFromSmaliWithPkg("conditions", "TestComplexIf");
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne(("if (this.a.equals(\"GT-P6200\") || this.a.equals(\"GT-P6210\") || this.a.equals(\"A100\") " + "|| this.a.equals(\"A101\") || this.a.equals(\"LIFETAB_S786X\") || this.a.equals(\"VS890 4G\")) {")));
    }
}

