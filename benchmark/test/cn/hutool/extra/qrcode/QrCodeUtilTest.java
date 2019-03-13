package cn.hutool.extra.qrcode;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import org.junit.Test;


/**
 * ??????????
 *
 * @author looly
 */
public class QrCodeUtilTest {
    // @Ignore
    @Test
    public void decodeTest() {
        String decode = QrCodeUtil.decode(FileUtil.file("e:/pic/qr.png"));
        Console.log(decode);
    }
}

