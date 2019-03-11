/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.datavec.image.transform;


import org.bytedeco.javacv.Frame;
import org.datavec.image.data.ImageWritable;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for ResizeImage
 *
 * @author raver119@gmail.com
 */
public class ResizeImageTransformTest {
    @Test
    public void testResizeUpscale1() throws Exception {
        ImageWritable srcImg = TestImageTransform.makeRandomImage(32, 32, 3);
        ResizeImageTransform transform = new ResizeImageTransform(200, 200);
        ImageWritable dstImg = transform.transform(srcImg);
        Frame f = dstImg.getFrame();
        Assert.assertEquals(f.imageWidth, 200);
        Assert.assertEquals(f.imageHeight, 200);
        float[] coordinates = new float[]{ 100, 200 };
        float[] transformed = transform.query(coordinates);
        Assert.assertEquals(((200.0F * 100) / 32), transformed[0], 0);
        Assert.assertEquals(((200.0F * 200) / 32), transformed[1], 0);
    }

    @Test
    public void testResizeDownscale() throws Exception {
        ImageWritable srcImg = TestImageTransform.makeRandomImage(571, 443, 3);
        ResizeImageTransform transform = new ResizeImageTransform(200, 200);
        ImageWritable dstImg = transform.transform(srcImg);
        Frame f = dstImg.getFrame();
        Assert.assertEquals(f.imageWidth, 200);
        Assert.assertEquals(f.imageHeight, 200);
        float[] coordinates = new float[]{ 300, 400 };
        float[] transformed = transform.query(coordinates);
        Assert.assertEquals(((200.0F * 300) / 443), transformed[0], 0);
        Assert.assertEquals(((200.0F * 400) / 571), transformed[1], 0);
    }
}

