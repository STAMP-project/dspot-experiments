package cn.hutool.core.lang;


import cn.hutool.core.text.TextSimilarity;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????????????
 *
 * @author looly
 */
public class TextSimilarityTest {
    @Test
    public void similarDegreeTest() {
        String a = "??????????????";
        String b = "????????????";
        double degree = TextSimilarity.similar(a, b);
        Assert.assertEquals(0.8571428571428571, degree, 16);
        String similarPercent = TextSimilarity.similar(a, b, 2);
        Assert.assertEquals("85.71%", similarPercent);
    }
}

