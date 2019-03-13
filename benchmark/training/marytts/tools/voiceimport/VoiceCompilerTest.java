package marytts.tools.voiceimport;


import org.junit.Assert;
import org.junit.Test;


public class VoiceCompilerTest {
    @Test
    public void testPackageName1() {
        String voice = "cmu-slt-hsmm";
        String expected = "CmuSltHsmm";
        String actual = VoiceCompiler.toPackageName(voice);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPackageName2() {
        String voice = "Peter M?ller";
        String expected = "PeterMLler";
        String actual = VoiceCompiler.toPackageName(voice);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPackageName3() {
        String voice = "  %123 bla_bla";
        String expected = "V123Bla_bla";
        String actual = VoiceCompiler.toPackageName(voice);
        Assert.assertEquals(expected, actual);
    }
}

