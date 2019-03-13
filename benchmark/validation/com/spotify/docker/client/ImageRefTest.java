/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.docker.client;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ImageRefTest {
    @Test
    public void testImageWithoutTag() {
        final ImageRef sut = new ImageRef("foobar");
        Assert.assertThat(sut.getImage(), Matchers.equalTo("foobar"));
        Assert.assertThat(sut.getTag(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testImageWithTag() {
        final ImageRef sut = new ImageRef("foobar:12345");
        Assert.assertThat(sut.getImage(), Matchers.equalTo("foobar"));
        Assert.assertThat(sut.getTag(), Matchers.is("12345"));
    }

    @Test
    public void testImageWithTagAndRegistry() {
        final ImageRef sut = new ImageRef("registry:4711/foo/bar:12345");
        Assert.assertThat(sut.getImage(), Matchers.equalTo("registry:4711/foo/bar"));
        Assert.assertThat(sut.getTag(), Matchers.is("12345"));
    }

    @Test
    public void testImageWithDigest() {
        final ImageRef sut = new ImageRef("bar@sha256:12345");
        Assert.assertThat(sut.getImage(), Matchers.equalTo("bar@sha256:12345"));
    }

    @Test
    public void testImageWithDigestAndRegistry() {
        final ImageRef sut = new ImageRef("registry:4711/foo/bar@sha256:12345");
        Assert.assertThat(sut.getImage(), Matchers.equalTo("registry:4711/foo/bar@sha256:12345"));
    }

    @Test
    public void testRegistry() {
        final String defaultRegistry = "docker.io";
        Assert.assertThat(new ImageRef("ubuntu"), ImageRefTest.hasRegistry(defaultRegistry));
        Assert.assertThat(new ImageRef("library/ubuntu"), ImageRefTest.hasRegistry(defaultRegistry));
        Assert.assertThat(new ImageRef("docker.io/library/ubuntu"), ImageRefTest.hasRegistry(defaultRegistry));
        Assert.assertThat(new ImageRef("index.docker.io/library/ubuntu"), ImageRefTest.hasRegistry("index.docker.io"));
        Assert.assertThat(new ImageRef("quay.io/library/ubuntu"), ImageRefTest.hasRegistry("quay.io"));
        Assert.assertThat(new ImageRef("gcr.io/library/ubuntu"), ImageRefTest.hasRegistry("gcr.io"));
        Assert.assertThat(new ImageRef("us.gcr.io/library/ubuntu"), ImageRefTest.hasRegistry("us.gcr.io"));
        Assert.assertThat(new ImageRef("gcr.kubernetes.io/library/ubuntu"), ImageRefTest.hasRegistry("gcr.kubernetes.io"));
        Assert.assertThat(new ImageRef("registry.example.net/foo/bar"), ImageRefTest.hasRegistry("registry.example.net"));
        Assert.assertThat(new ImageRef("registry.example.net/foo/bar:1.2.3"), ImageRefTest.hasRegistry("registry.example.net"));
        Assert.assertThat(new ImageRef("registry.example.net/foo/bar:latest"), ImageRefTest.hasRegistry("registry.example.net"));
        Assert.assertThat(new ImageRef("registry.example.net:5555/foo/bar:latest"), ImageRefTest.hasRegistry("registry.example.net:5555"));
    }

    @Test
    public void testRegistryUrl() throws Exception {
        final String defaultRegistry = "https://index.docker.io/v1/";
        Assert.assertThat(new ImageRef("ubuntu"), ImageRefTest.hasRegistryUrl(defaultRegistry));
        Assert.assertThat(new ImageRef("library/ubuntu"), ImageRefTest.hasRegistryUrl(defaultRegistry));
        Assert.assertThat(new ImageRef("docker.io/library/ubuntu"), ImageRefTest.hasRegistryUrl(defaultRegistry));
        Assert.assertThat(new ImageRef("index.docker.io/library/ubuntu"), ImageRefTest.hasRegistryUrl(defaultRegistry));
        Assert.assertThat(new ImageRef("quay.io/library/ubuntu"), ImageRefTest.hasRegistryUrl("quay.io"));
        Assert.assertThat(new ImageRef("gcr.io/library/ubuntu"), ImageRefTest.hasRegistryUrl("https://gcr.io"));
        Assert.assertThat(new ImageRef("us.gcr.io/library/ubuntu"), ImageRefTest.hasRegistryUrl("https://us.gcr.io"));
        Assert.assertThat(new ImageRef("gcr.kubernetes.io/library/ubuntu"), ImageRefTest.hasRegistryUrl("https://gcr.kubernetes.io"));
        Assert.assertThat(new ImageRef("registry.example.net/foo/bar"), ImageRefTest.hasRegistryUrl("https://registry.example.net"));
        Assert.assertThat(new ImageRef("registry.example.net/foo/bar:1.2.3"), ImageRefTest.hasRegistryUrl("https://registry.example.net"));
        Assert.assertThat(new ImageRef("registry.example.net/foo/bar:latest"), ImageRefTest.hasRegistryUrl("https://registry.example.net"));
        Assert.assertThat(new ImageRef("registry.example.net:5555/foo/bar:latest"), ImageRefTest.hasRegistryUrl("https://registry.example.net:5555"));
    }

    @Test
    public void testParseUrl() throws Exception {
        Assert.assertThat(ImageRef.parseRegistryUrl("docker.io"), Matchers.equalTo("https://index.docker.io/v1/"));
        Assert.assertThat(ImageRef.parseRegistryUrl("index.docker.io"), Matchers.equalTo("https://index.docker.io/v1/"));
        Assert.assertThat(ImageRef.parseRegistryUrl("registry.net"), Matchers.equalTo("https://registry.net"));
        Assert.assertThat(ImageRef.parseRegistryUrl("registry.net:80"), Matchers.equalTo("https://registry.net:80"));
    }
}

