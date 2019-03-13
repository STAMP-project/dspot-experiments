/**
 * Copyright 2002-2018 the original author or authors.
 *
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
 */
package org.springframework.core.io;


import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;


/**
 * Unit tests for various {@link Resource} implementations.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 * @since 09.09.2004
 */
public class ResourceTests {
    @Test
    public void testByteArrayResource() throws IOException {
        Resource resource = new ByteArrayResource("testString".getBytes());
        Assert.assertTrue(resource.exists());
        Assert.assertFalse(resource.isOpen());
        String content = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream()));
        Assert.assertEquals("testString", content);
        Assert.assertEquals(resource, new ByteArrayResource("testString".getBytes()));
    }

    @Test
    public void testByteArrayResourceWithDescription() throws IOException {
        Resource resource = new ByteArrayResource("testString".getBytes(), "my description");
        Assert.assertTrue(resource.exists());
        Assert.assertFalse(resource.isOpen());
        String content = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream()));
        Assert.assertEquals("testString", content);
        Assert.assertTrue(resource.getDescription().contains("my description"));
        Assert.assertEquals(resource, new ByteArrayResource("testString".getBytes()));
    }

    @Test
    public void testInputStreamResource() throws IOException {
        InputStream is = new ByteArrayInputStream("testString".getBytes());
        Resource resource = new InputStreamResource(is);
        Assert.assertTrue(resource.exists());
        Assert.assertTrue(resource.isOpen());
        String content = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream()));
        Assert.assertEquals("testString", content);
        Assert.assertEquals(resource, new InputStreamResource(is));
    }

    @Test
    public void testInputStreamResourceWithDescription() throws IOException {
        InputStream is = new ByteArrayInputStream("testString".getBytes());
        Resource resource = new InputStreamResource(is, "my description");
        Assert.assertTrue(resource.exists());
        Assert.assertTrue(resource.isOpen());
        String content = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream()));
        Assert.assertEquals("testString", content);
        Assert.assertTrue(resource.getDescription().contains("my description"));
        Assert.assertEquals(resource, new InputStreamResource(is));
    }

    @Test
    public void testClassPathResource() throws IOException {
        Resource resource = new ClassPathResource("org/springframework/core/io/Resource.class");
        doTestResource(resource);
        Resource resource2 = new ClassPathResource("org/springframework/core/../core/io/./Resource.class");
        Assert.assertEquals(resource, resource2);
        Resource resource3 = new ClassPathResource("org/springframework/core/").createRelative("../core/io/./Resource.class");
        Assert.assertEquals(resource, resource3);
        // Check whether equal/hashCode works in a HashSet.
        HashSet<Resource> resources = new HashSet<>();
        resources.add(resource);
        resources.add(resource2);
        Assert.assertEquals(1, resources.size());
    }

    @Test
    public void testClassPathResourceWithClassLoader() throws IOException {
        Resource resource = new ClassPathResource("org/springframework/core/io/Resource.class", getClass().getClassLoader());
        doTestResource(resource);
        Assert.assertEquals(resource, new ClassPathResource("org/springframework/core/../core/io/./Resource.class", getClass().getClassLoader()));
    }

    @Test
    public void testClassPathResourceWithClass() throws IOException {
        Resource resource = new ClassPathResource("Resource.class", getClass());
        doTestResource(resource);
        Assert.assertEquals(resource, new ClassPathResource("Resource.class", getClass()));
    }

    @Test
    public void testFileSystemResource() throws IOException {
        String file = getClass().getResource("Resource.class").getFile();
        Resource resource = new FileSystemResource(file);
        doTestResource(resource);
        Assert.assertEquals(new FileSystemResource(file), resource);
    }

    @Test
    public void testFileSystemResourceWithFilePath() throws Exception {
        Path filePath = Paths.get(getClass().getResource("Resource.class").toURI());
        Resource resource = new FileSystemResource(filePath);
        doTestResource(resource);
        Assert.assertEquals(new FileSystemResource(filePath), resource);
    }

    @Test
    public void testFileSystemResourceWithPlainPath() {
        Resource resource = new FileSystemResource("core/io/Resource.class");
        Assert.assertEquals(resource, new FileSystemResource("core/../core/io/./Resource.class"));
    }

    @Test
    public void testUrlResource() throws IOException {
        Resource resource = new UrlResource(getClass().getResource("Resource.class"));
        doTestResource(resource);
        Assert.assertEquals(new UrlResource(getClass().getResource("Resource.class")), resource);
        Resource resource2 = new UrlResource("file:core/io/Resource.class");
        Assert.assertEquals(resource2, new UrlResource("file:core/../core/io/./Resource.class"));
        Assert.assertEquals("test.txt", new UrlResource("file:/dir/test.txt?argh").getFilename());
        Assert.assertEquals("test.txt", new UrlResource("file:\\dir\\test.txt?argh").getFilename());
        Assert.assertEquals("test.txt", new UrlResource("file:\\dir/test.txt?argh").getFilename());
    }

    @Test
    public void testClassPathResourceWithRelativePath() throws IOException {
        Resource resource = new ClassPathResource("dir/");
        Resource relative = resource.createRelative("subdir");
        Assert.assertEquals(new ClassPathResource("dir/subdir"), relative);
    }

    @Test
    public void testFileSystemResourceWithRelativePath() throws IOException {
        Resource resource = new FileSystemResource("dir/");
        Resource relative = resource.createRelative("subdir");
        Assert.assertEquals(new FileSystemResource("dir/subdir"), relative);
    }

    @Test
    public void testUrlResourceWithRelativePath() throws IOException {
        Resource resource = new UrlResource("file:dir/");
        Resource relative = resource.createRelative("subdir");
        Assert.assertEquals(new UrlResource("file:dir/subdir"), relative);
    }

    @Test
    public void testAbstractResourceExceptions() throws Exception {
        final String name = "test-resource";
        Resource resource = new AbstractResource() {
            @Override
            public String getDescription() {
                return name;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                throw new FileNotFoundException();
            }
        };
        try {
            resource.getURL();
            Assert.fail("FileNotFoundException should have been thrown");
        } catch (FileNotFoundException ex) {
            Assert.assertTrue(ex.getMessage().contains(name));
        }
        try {
            resource.getFile();
            Assert.fail("FileNotFoundException should have been thrown");
        } catch (FileNotFoundException ex) {
            Assert.assertTrue(ex.getMessage().contains(name));
        }
        try {
            resource.createRelative("/testing");
            Assert.fail("FileNotFoundException should have been thrown");
        } catch (FileNotFoundException ex) {
            Assert.assertTrue(ex.getMessage().contains(name));
        }
        Assert.assertThat(resource.getFilename(), CoreMatchers.nullValue());
    }

    @Test
    public void testContentLength() throws IOException {
        AbstractResource resource = new AbstractResource() {
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(new byte[]{ 'a', 'b', 'c' });
            }

            @Override
            public String getDescription() {
                return "";
            }
        };
        Assert.assertThat(resource.contentLength(), CoreMatchers.is(3L));
    }

    @Test
    public void testReadableChannel() throws IOException {
        Resource resource = new FileSystemResource(getClass().getResource("Resource.class").getFile());
        ReadableByteChannel channel = null;
        try {
            channel = resource.readableChannel();
            ByteBuffer buffer = ByteBuffer.allocate(((int) (resource.contentLength())));
            channel.read(buffer);
            buffer.rewind();
            Assert.assertTrue(((buffer.limit()) > 0));
        } finally {
            if (channel != null) {
                channel.close();
            }
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void testInputStreamNotFoundOnFileSystemResource() throws IOException {
        new FileSystemResource(getClass().getResource("Resource.class").getFile()).createRelative("X").getInputStream();
    }

    @Test(expected = FileNotFoundException.class)
    public void testReadableChannelNotFoundOnFileSystemResource() throws IOException {
        new FileSystemResource(getClass().getResource("Resource.class").getFile()).createRelative("X").readableChannel();
    }

    @Test(expected = FileNotFoundException.class)
    public void testInputStreamNotFoundOnClassPathResource() throws IOException {
        new ClassPathResource("Resource.class", getClass()).createRelative("X").getInputStream();
    }

    @Test(expected = FileNotFoundException.class)
    public void testReadableChannelNotFoundOnClassPathResource() throws IOException {
        new ClassPathResource("Resource.class", getClass()).createRelative("X").readableChannel();
    }
}

