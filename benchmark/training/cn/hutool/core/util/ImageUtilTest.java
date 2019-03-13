package cn.hutool.core.util;


import cn.hutool.core.io.FileUtil;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Test;


public class ImageUtilTest {
    // @Ignore
    @Test
    public void rotateTest() throws IOException {
        BufferedImage image = ImageUtil.rotate(ImageIO.read(FileUtil.file("e:/pic/366466.jpg")), 180);
        ImageUtil.write(image, FileUtil.file("e:/pic/result.png"));
    }
}

