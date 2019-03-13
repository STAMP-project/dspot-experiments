package jadx.tests.integration.enums;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSwitchOverEnum extends IntegrationTest {
    public enum Count {

        ONE,
        TWO,
        THREE;}

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSwitchOverEnum.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.countString(1, "synthetic"));
        Assert.assertThat(code, JadxMatchers.countString(2, "switch (c) {"));
        Assert.assertThat(code, JadxMatchers.countString(2, "case ONE:"));
    }
}

