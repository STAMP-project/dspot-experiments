package com.baeldung.java9;


import java.awt.Image;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class MultiResultionImageUnitTest {
    @Test
    public void baseMultiResImageTest() {
        int baseIndex = 1;
        int length = 4;
        BufferedImage[] resolutionVariants = new BufferedImage[length];
        for (int i = 0; i < length; i++) {
            resolutionVariants[i] = MultiResultionImageUnitTest.createImage(i);
        }
        MultiResolutionImage bmrImage = new BaseMultiResolutionImage(baseIndex, resolutionVariants);
        List<Image> rvImageList = bmrImage.getResolutionVariants();
        Assert.assertEquals("MultiResoltion Image shoudl contain the same number of resolution variants!", rvImageList.size(), length);
        for (int i = 0; i < length; i++) {
            int imageSize = MultiResultionImageUnitTest.getSize(i);
            Image testRVImage = bmrImage.getResolutionVariant(imageSize, imageSize);
            Assert.assertSame("Images should be the same", testRVImage, resolutionVariants[i]);
        }
    }
}

