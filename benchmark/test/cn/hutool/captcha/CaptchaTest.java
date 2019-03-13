package cn.hutool.captcha;


import org.junit.Assert;
import org.junit.Test;


/**
 * ???????????
 *
 * @author looly
 */
public class CaptchaTest {
    @Test
    public void lineCaptchaTest1() {
        // ???????????
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);
        Assert.assertNotNull(lineCaptcha.getCode());
        Assert.assertTrue(lineCaptcha.verify(lineCaptcha.getCode()));
    }
}

