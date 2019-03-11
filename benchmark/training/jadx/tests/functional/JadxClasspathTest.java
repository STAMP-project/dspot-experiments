package jadx.tests.functional;


import ArgType.OBJECT;
import jadx.core.clsp.ClspGraph;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.nodes.DexNode;
import org.junit.Assert;
import org.junit.Test;


public class JadxClasspathTest {
    private static final String JAVA_LANG_EXCEPTION = "java.lang.Exception";

    private static final String JAVA_LANG_THROWABLE = "java.lang.Throwable";

    private DexNode dex;

    private ClspGraph clsp;

    @Test
    public void test() {
        ArgType objExc = object(JadxClasspathTest.JAVA_LANG_EXCEPTION);
        ArgType objThr = object(JadxClasspathTest.JAVA_LANG_THROWABLE);
        Assert.assertTrue(clsp.isImplements(JadxClasspathTest.JAVA_LANG_EXCEPTION, JadxClasspathTest.JAVA_LANG_THROWABLE));
        Assert.assertFalse(clsp.isImplements(JadxClasspathTest.JAVA_LANG_THROWABLE, JadxClasspathTest.JAVA_LANG_EXCEPTION));
        Assert.assertFalse(ArgType.isCastNeeded(dex, objExc, objThr));
        Assert.assertTrue(ArgType.isCastNeeded(dex, objThr, objExc));
        Assert.assertTrue(ArgType.isCastNeeded(dex, OBJECT, STRING));
    }
}

