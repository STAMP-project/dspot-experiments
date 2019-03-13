package jadx.tests.integration.enums;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestEnums4 extends IntegrationTest {
    public static class TestCls {
        public enum ResType {

            CODE(".dex", ".class"),
            MANIFEST("AndroidManifest.xml"),
            XML(".xml"),
            ARSC(".arsc"),
            FONT(".ttf"),
            IMG(".png", ".gif", ".jpg"),
            LIB(".so"),
            UNKNOWN;
            private final String[] exts;

            private ResType(String... exts) {
                this.exts = exts;
            }

            public String[] getExts() {
                return exts;
            }
        }

        public void check() {
            Assert.assertThat(TestEnums4.TestCls.ResType.CODE.getExts(), Matchers.is(new String[]{ ".dex", ".class" }));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestEnums4.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("CODE(\".dex\", \".class\"),"));
        Assert.assertThat(code, JadxMatchers.containsOne("ResType(String... exts) {"));
        // assertThat(code, not(containsString("private ResType")));
    }
}

