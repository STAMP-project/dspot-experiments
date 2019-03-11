package jadx.tests.smali;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConstructor extends SmaliTest {
    @Test
    public void test() {
        disableCompilation();
        ClassNode cls = getClassNodeFromSmali("TestConstructor");
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("new SomeObject(arg3);"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("= someObject")));
    }
}

