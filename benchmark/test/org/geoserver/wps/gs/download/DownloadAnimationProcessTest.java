/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.gs.download;


import DownloadServiceConfiguration.MAX_ANIMATION_FRAMES_NAME;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.resource.Resource;
import org.geotools.image.test.ImageAssert;
import org.hamcrest.CoreMatchers;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.Demuxer;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.Format;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.scale.AWTUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.w3c.dom.Document;


public class DownloadAnimationProcessTest extends BaseDownloadImageProcessTest {
    @Test
    public void testDescribeProcess() throws Exception {
        Document d = getAsDOM(((root()) + "service=wps&request=describeprocess&identifier=gs:DownloadAnimation"));
        // print(d);
        assertXpathExists("//ComplexOutput/Supported/Format[MimeType='video/mp4']", d);
    }

    @Test
    public void testAnimateBmTime() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("animateBlueMarble.xml"));
        MockHttpServletResponse response = postAsServletResponse("wps", xml);
        Assert.assertEquals("video/mp4", response.getContentType());
        // JCodec API works off files only...
        File testFile = new File("target/animateBmTime.mp4");
        FileUtils.writeByteArrayToFile(testFile, response.getContentAsByteArray());
        // check frames and duration
        Format f = JCodecUtil.detectFormat(testFile);
        Demuxer d = JCodecUtil.createDemuxer(f, testFile);
        DemuxerTrack vt = d.getVideoTracks().get(0);
        DemuxerTrackMeta dtm = vt.getMeta();
        Assert.assertEquals(4, dtm.getTotalFrames());
        Assert.assertEquals(8, dtm.getTotalDuration(), 0.0);
        // grab frames for checking
        File source = new File("src/test/resources/org/geoserver/wps/gs/download/bm_time.zip");
        FrameGrab grabber = FrameGrab.createFrameGrab(NIOUtils.readableChannel(testFile));
        // first
        BufferedImage frame1 = AWTUtil.toBufferedImage(grabber.getNativeFrame());
        BufferedImage expected1 = grabImageFromZip(source, "world.200402.3x5400x2700.tiff");
        ImageAssert.assertEquals(expected1, frame1, 100);
        // second
        BufferedImage frame2 = AWTUtil.toBufferedImage(grabber.getNativeFrame());
        BufferedImage expected2 = grabImageFromZip(source, "world.200403.3x5400x2700.tiff");
        ImageAssert.assertEquals(expected2, frame2, 100);
        // third
        BufferedImage frame3 = AWTUtil.toBufferedImage(grabber.getNativeFrame());
        BufferedImage expected3 = grabImageFromZip(source, "world.200404.3x5400x2700.tiff");
        ImageAssert.assertEquals(expected3, frame3, 100);
        // fourth
        BufferedImage frame4 = AWTUtil.toBufferedImage(grabber.getNativeFrame());
        BufferedImage expected4 = grabImageFromZip(source, "world.200405.3x5400x2700.tiff");
        ImageAssert.assertEquals(expected4, frame4, 100);
    }

    @Test
    public void testAnimateFrameLimits() throws Exception {
        // set a limit of 1 frame
        GeoServerDataDirectory dd = getDataDirectory();
        Properties props = new Properties();
        props.put(MAX_ANIMATION_FRAMES_NAME, "1");
        Resource config = dd.get("download.properties");
        try (OutputStream os = config.out()) {
            props.store(os, null);
        }
        try {
            String xml = IOUtils.toString(getClass().getResourceAsStream("animateBlueMarble.xml"));
            Document dom = postAsDOM("wps", xml);
            // print(dom);
            XMLAssert.assertXpathExists("//wps:ProcessFailed", dom);
            String message = XMLUnit.newXpathEngine().evaluate("//ows:ExceptionText", dom);
            Assert.assertThat(message, CoreMatchers.containsString("More than 1 times specified in the request"));
        } finally {
            Assert.assertTrue("Failed to remove download configuration file", config.delete());
            // force reset of default configuration
            final DownloadServiceConfigurationWatcher watcher = GeoServerExtensions.bean(DownloadServiceConfigurationWatcher.class);
            watcher.loadConfiguration();
        }
    }

    @Test
    public void testAnimateDecoration() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("animateDecoration.xml"));
        MockHttpServletResponse response = postAsServletResponse("wps", xml);
        Assert.assertEquals("video/mp4", response.getContentType());
        // JCodec API works off files only...
        File testFile = new File("target/animateWaterDecoration.mp4");
        FileUtils.writeByteArrayToFile(testFile, response.getContentAsByteArray());
        // check frames and duration
        Format f = JCodecUtil.detectFormat(testFile);
        Demuxer d = JCodecUtil.createDemuxer(f, testFile);
        DemuxerTrack vt = d.getVideoTracks().get(0);
        DemuxerTrackMeta dtm = vt.getMeta();
        Assert.assertEquals(2, dtm.getTotalFrames());
        Assert.assertEquals(2, dtm.getTotalDuration(), 0.0);
        // grab first frame for test
        FrameGrab grabber = FrameGrab.createFrameGrab(NIOUtils.readableChannel(testFile));
        BufferedImage frame1 = AWTUtil.toBufferedImage(grabber.getNativeFrame());
        ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "animateDecorateFirstFrame.png")), frame1, 100);
    }

    @Test
    public void testAnimateTimestamped() throws Exception {
        String xml = IOUtils.toString(getClass().getResourceAsStream("animateBlueMarbleTimestamped.xml"));
        MockHttpServletResponse response = postAsServletResponse("wps", xml);
        Assert.assertEquals("video/mp4", response.getContentType());
        // JCodec API works off files only...
        File testFile = new File("target/animateTimestamped.mp4");
        FileUtils.writeByteArrayToFile(testFile, response.getContentAsByteArray());
        // check frames and duration
        Format f = JCodecUtil.detectFormat(testFile);
        Demuxer d = JCodecUtil.createDemuxer(f, testFile);
        DemuxerTrack vt = d.getVideoTracks().get(0);
        DemuxerTrackMeta dtm = vt.getMeta();
        Assert.assertEquals(4, dtm.getTotalFrames());
        Assert.assertEquals(8, dtm.getTotalDuration(), 0.0);
        // grab first frame for test
        FrameGrab grabber = FrameGrab.createFrameGrab(NIOUtils.readableChannel(testFile));
        BufferedImage frame1 = AWTUtil.toBufferedImage(grabber.getNativeFrame());
        ImageAssert.assertEquals(new File(((BaseDownloadImageProcessTest.SAMPLES) + "animateBlueMarbleTimestampedFrame1.png")), frame1, 100);
    }
}

