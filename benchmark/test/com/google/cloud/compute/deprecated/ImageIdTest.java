/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.compute.deprecated;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ImageIdTest {
    private static final String PROJECT = "project";

    private static final String NAME = "image";

    private static final String URL = "https://www.googleapis.com/compute/v1/projects/project/global/images/image";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOf() {
        ImageId imageId = ImageId.of(ImageIdTest.PROJECT, ImageIdTest.NAME);
        Assert.assertEquals(ImageIdTest.PROJECT, imageId.getProject());
        Assert.assertEquals(ImageIdTest.NAME, imageId.getImage());
        Assert.assertEquals(ImageIdTest.URL, imageId.getSelfLink());
        imageId = ImageId.of(ImageIdTest.NAME);
        Assert.assertNull(imageId.getProject());
        Assert.assertEquals(ImageIdTest.NAME, imageId.getImage());
    }

    @Test
    public void testToAndFromUrl() {
        ImageId imageId = ImageId.of(ImageIdTest.PROJECT, ImageIdTest.NAME);
        compareImageId(imageId, ImageId.fromUrl(imageId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid image URL");
        ImageId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testSetProjectId() {
        ImageId imageId = ImageId.of(ImageIdTest.PROJECT, ImageIdTest.NAME);
        Assert.assertSame(imageId, imageId.setProjectId(ImageIdTest.PROJECT));
        compareImageId(imageId, ImageId.of(ImageIdTest.NAME).setProjectId(ImageIdTest.PROJECT));
    }

    @Test
    public void testMatchesUrl() {
        Assert.assertTrue(ImageId.matchesUrl(ImageId.of(ImageIdTest.PROJECT, ImageIdTest.NAME).getSelfLink()));
        Assert.assertFalse(ImageId.matchesUrl("notMatchingUrl"));
    }
}

