/**
 * Copyright (C) 2016-2017 Samuel Audet
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bytedeco.javacv;


import FrameGrabber.SampleMode.FLOAT;
import java.io.File;
import java.io.FileInputStream;
import org.bytedeco.javacpp.Loader;
import org.junit.Assert;
import org.junit.Test;


/**
 * Complex Test case for FrameGrabber classes - change the resolution during runtime.
 * Also uses other classes from JavaCV.
 *
 * @author Samuel Audet, Michael Fritscher
 */
public class FrameGrabberChangingResolutionTest {
    private File tempFile = new File(Loader.getTempDir(), "test.mkv");

    private File tempTargetFile = new File(Loader.getTempDir(), "target.mkv");

    private boolean endRequested;

    @Test
    public void testFFmpegFrameGrabber() {
        System.out.println("FFmpegFrameGrabber");
        try {
            makeTestfile();
            setupUDPReceiver();
            System.out.println("Changing to 160x120");
            setupUDPSender(160, 120, 50000, 60);
            System.out.println("Changing to 320x240");
            setupUDPSender(320, 240, 100000, 60);
            System.out.println("Changing to 640x480");
            setupUDPSender(640, 480, 200000, 60);
            System.out.println("Changing to 160x120");
            setupUDPSender(160, 120, 50000, 60);
            System.out.println("Changing to 320x240");
            setupUDPSender(320, 240, 100000, 60);
            System.out.println("Changing to 640x480");
            setupUDPSender(640, 480, 200000, 60);
            System.out.println("Changing to 320x240");
            setupUDPSender(320, 240, 100000, 60);
            System.out.println("Changing to 160x120");
            setupUDPSender(160, 120, 50000, 60);
            Thread.sleep(3000);
            endRequested = true;
        } catch (Exception e) {
            tempFile.delete();
            tempTargetFile.delete();
            Assert.fail(("Exception should not have been thrown: " + e));
        }
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(new FileInputStream(tempTargetFile));
            grabber.setSampleMode(FLOAT);
            grabber.start();
            int n = 0;
            Frame frame2;
            while ((frame2 = grabber.grab()) != null) {
                if ((frame2.image) != null) {
                    n++;
                    Assert.assertEquals(640, frame2.imageWidth);
                }
            } 
            // It seems that ffmpeg lose some frames while switching (ideal
            // value would be 240)
            // System.out.println("END NUMBER: " + n);
            Assert.assertTrue((n > 300));
            Assert.assertTrue((n <= 480));
            Assert.assertEquals(null, grabber.grab());
            grabber.restart();
            grabber.stop();
            grabber.release();
        } catch (Exception e) {
            Assert.fail(("Exception should not have been thrown: " + e));
        } finally {
            tempFile.delete();
            tempTargetFile.delete();
        }
    }
}

