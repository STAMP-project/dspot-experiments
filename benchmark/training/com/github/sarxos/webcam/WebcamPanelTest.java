package com.github.sarxos.webcam;


import DrawMode.FILL;
import DrawMode.FIT;
import DrawMode.NONE;
import com.github.sarxos.webcam.ds.test.DummyDriver;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class WebcamPanelTest {
    @Test
    public void test_size() throws InterruptedException {
        Webcam.setDriver(new DummyDriver());
        final Webcam w = Webcam.getDefault();
        final WebcamPanel p = new WebcamPanel(w);
        w.open();
        p.repaint();
        final BufferedImage bi = w.getImage();
        final Dimension d = p.getPreferredSize();
        Assertions.assertThat(d.getWidth()).isEqualTo(bi.getWidth());
        Assertions.assertThat(d.getHeight()).isEqualTo(bi.getHeight());
        p.stop();
        w.close();
    }

    @Test
    public void test_sizeSpecified() throws InterruptedException {
        Webcam.setDriver(new DummyDriver());
        final Webcam w = Webcam.getDefault();
        final WebcamPanel p = new WebcamPanel(w, new Dimension(256, 345), false);
        w.open();
        p.repaint();
        final Dimension d = p.getPreferredSize();
        Assertions.assertThat(d.getWidth()).isEqualTo(256);
        Assertions.assertThat(d.getHeight()).isEqualTo(345);
        p.stop();
        w.close();
    }

    @Test
    public void test_modeFill() throws InterruptedException {
        Webcam.setDriver(new DummyDriver());
        final Webcam w = Webcam.getDefault();
        w.open();
        final WebcamPanel p = new WebcamPanel(w, new Dimension(256, 345), false);
        p.setDrawMode(FILL);
        p.start();
        Assertions.assertThat(p.getDrawMode()).isEqualTo(FILL);
        ScreenImage.createImage(p);
        Thread.sleep(100);
        p.stop();
        w.close();
    }

    @Test
    public void test_modeFit() throws InterruptedException {
        Webcam.setDriver(new DummyDriver());
        final Webcam w = Webcam.getDefault();
        w.open();
        final WebcamPanel p = new WebcamPanel(w, new Dimension(256, 345), false);
        p.setDrawMode(FIT);
        p.start();
        Assertions.assertThat(p.getDrawMode()).isEqualTo(FIT);
        ScreenImage.createImage(p);
        Thread.sleep(100);
        p.stop();
        w.close();
    }

    @Test
    public void test_modeNone() throws InterruptedException {
        Webcam.setDriver(new DummyDriver());
        final Webcam w = Webcam.getDefault();
        w.open();
        final WebcamPanel p = new WebcamPanel(w, new Dimension(256, 345), false);
        p.setDrawMode(NONE);
        p.start();
        Assertions.assertThat(p.getDrawMode()).isEqualTo(NONE);
        ScreenImage.createImage(p);
        Thread.sleep(100);
        p.stop();
        w.close();
    }
}

