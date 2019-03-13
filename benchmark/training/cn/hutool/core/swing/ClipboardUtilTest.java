package cn.hutool.core.swing;


import java.awt.HeadlessException;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????????
 *
 * @author looly
 */
public class ClipboardUtilTest {
    @Test
    public void setAndGetStrTest() {
        try {
            ClipboardUtil.setStr("test");
            String test = ClipboardUtil.getStr();
            Assert.assertEquals("test", test);
        } catch (HeadlessException e) {
            // ?? No X11 DISPLAY variable was set, but this program performed an operation which requires it.
            // ignore
        }
    }
}

