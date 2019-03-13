/**
 * Copyright (c) 2016 Spotify AB.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.spotify.docker;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class CompositeImageNameTest {
    @Test
    public void testStandardCompositeNameWithoutImageTags() throws MojoExecutionException {
        final CompositeImageName compositeImageName = CompositeImageName.create("imageName:tagName", null);
        Assert.assertEquals("imageName", compositeImageName.getName());
        Assert.assertEquals("tagName", compositeImageName.getImageTags().get(0));
    }

    @Test
    public void testStandardCompositeNameWithImageTags() throws MojoExecutionException {
        final List<String> imageTags = Arrays.asList("tag1", "tag2");
        final CompositeImageName compositeImageName = CompositeImageName.create("imageName:tagName", imageTags);
        Assert.assertEquals("imageName", compositeImageName.getName());
        Assert.assertEquals("tagName", compositeImageName.getImageTags().get(0));
        Assert.assertEquals("tag1", compositeImageName.getImageTags().get(1));
        Assert.assertEquals("tag2", compositeImageName.getImageTags().get(2));
    }

    @Test
    public void testCompositeNameWithoutTagWithImageTags() throws MojoExecutionException {
        final List<String> imageTags = Arrays.asList("tag1", "tag2");
        final CompositeImageName compositeImageName = CompositeImageName.create("imageName", imageTags);
        Assert.assertEquals("imageName", compositeImageName.getName());
        Assert.assertEquals(2, compositeImageName.getImageTags().size());
        Assert.assertEquals("tag1", compositeImageName.getImageTags().get(0));
        Assert.assertEquals("tag2", compositeImageName.getImageTags().get(1));
    }

    @Test
    public void testImageWithRegistryHostnameAndPortWithoutTag() throws MojoExecutionException {
        final List<String> imageTags = Collections.singletonList("tag1");
        final String imageNameWithRegistry = "registry:8888/imageName";
        final CompositeImageName compositeImageName = CompositeImageName.create(imageNameWithRegistry, imageTags);
        Assert.assertEquals(imageNameWithRegistry, compositeImageName.getName());
        Assert.assertEquals(1, compositeImageName.getImageTags().size());
        Assert.assertEquals("tag1", compositeImageName.getImageTags().get(0));
    }

    @Test
    public void testImageWithRegistryHostnameAndPortWithTag() throws MojoExecutionException {
        final List<String> imageTags = Collections.singletonList("tag2");
        final String imageNameWithRegistryAndTag = "registry:8888/imageName:tag1";
        final CompositeImageName compositeImageName = CompositeImageName.create(imageNameWithRegistryAndTag, imageTags);
        Assert.assertEquals("registry:8888/imageName", compositeImageName.getName());
        Assert.assertEquals(2, compositeImageName.getImageTags().size());
        Assert.assertEquals("tag1", compositeImageName.getImageTags().get(0));
        Assert.assertEquals("tag2", compositeImageName.getImageTags().get(1));
    }

    @Test
    public void testInvalidCompositeImageNameAndTagCombinations() throws MojoExecutionException {
        final String noImageNameErrorMessage = "imageName not set";
        final String noImageTagsErrorMessage = "no imageTags set";
        final List<String> emtpy = new ArrayList<>();
        final List<String> tags = Arrays.asList("tag1", "tag2");
        compositeImageNameExpectsException(noImageNameErrorMessage, null, null);
        compositeImageNameExpectsException(noImageNameErrorMessage, null, emtpy);
        compositeImageNameExpectsException(noImageNameErrorMessage, null, tags);
        compositeImageNameExpectsException(noImageNameErrorMessage, "", null);
        compositeImageNameExpectsException(noImageNameErrorMessage, "", emtpy);
        compositeImageNameExpectsException(noImageNameErrorMessage, "", tags);
        compositeImageNameExpectsException(noImageNameErrorMessage, ":tagname", null);
        compositeImageNameExpectsException(noImageNameErrorMessage, ":tagname", emtpy);
        compositeImageNameExpectsException(noImageNameErrorMessage, ":tagname", tags);
        compositeImageNameExpectsException(noImageTagsErrorMessage, "imageName", null);
        compositeImageNameExpectsException(noImageTagsErrorMessage, "imageName", emtpy);
    }

    @Test
    public void testContainsTag() {
        Assert.assertTrue(CompositeImageName.containsTag("imageName:tag"));
        Assert.assertTrue(CompositeImageName.containsTag("registry/imageName:tag"));
        Assert.assertTrue(CompositeImageName.containsTag("registry:8888/imageName:tag"));
        Assert.assertFalse(CompositeImageName.containsTag("imageName"));
        Assert.assertFalse(CompositeImageName.containsTag("registry/imageName"));
        Assert.assertFalse(CompositeImageName.containsTag("registry:8888/imageName"));
    }
}

