package jadx.tests.integration.enums;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSwitchOverEnum2 extends IntegrationTest {
    public enum Count {

        ONE,
        TWO,
        THREE;}

    public enum Animal {

        CAT,
        DOG;}

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSwitchOverEnum2.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.countString(1, "synthetic"));
        Assert.assertThat(code, JadxMatchers.countString(2, "switch (c) {"));
        Assert.assertThat(code, JadxMatchers.countString(2, "case ONE:"));
        Assert.assertThat(code, JadxMatchers.countString(2, "case DOG:"));
    }
}

