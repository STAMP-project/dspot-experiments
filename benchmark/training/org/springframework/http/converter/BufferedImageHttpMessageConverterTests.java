/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.http.converter;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MockHttpInputMessage;
import org.springframework.http.MockHttpOutputMessage;
import org.springframework.util.FileCopyUtils;


/**
 * Unit tests for BufferedImageHttpMessageConverter.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class BufferedImageHttpMessageConverterTests {
    private BufferedImageHttpMessageConverter converter;

    @Test
    public void canRead() {
        Assert.assertTrue("Image not supported", converter.canRead(BufferedImage.class, null));
        Assert.assertTrue("Image not supported", converter.canRead(BufferedImage.class, new MediaType("image", "png")));
    }

    @Test
    public void canWrite() {
        Assert.assertTrue("Image not supported", converter.canWrite(BufferedImage.class, null));
        Assert.assertTrue("Image not supported", converter.canWrite(BufferedImage.class, new MediaType("image", "png")));
        Assert.assertTrue("Image not supported", converter.canWrite(BufferedImage.class, new MediaType("*", "*")));
    }

    @Test
    public void read() throws IOException {
        Resource logo = new ClassPathResource("logo.jpg", BufferedImageHttpMessageConverterTests.class);
        byte[] body = FileCopyUtils.copyToByteArray(logo.getInputStream());
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body);
        inputMessage.getHeaders().setContentType(new MediaType("image", "jpeg"));
        BufferedImage result = converter.read(BufferedImage.class, inputMessage);
        Assert.assertEquals("Invalid height", 500, result.getHeight());
        Assert.assertEquals("Invalid width", 750, result.getWidth());
    }

    @Test
    public void write() throws IOException {
        Resource logo = new ClassPathResource("logo.jpg", BufferedImageHttpMessageConverterTests.class);
        BufferedImage body = ImageIO.read(logo.getFile());
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MediaType contentType = new MediaType("image", "png");
        converter.write(body, contentType, outputMessage);
        Assert.assertEquals("Invalid content type", contentType, outputMessage.getWrittenHeaders().getContentType());
        Assert.assertTrue("Invalid size", ((outputMessage.getBodyAsBytes().length) > 0));
        BufferedImage result = ImageIO.read(new ByteArrayInputStream(outputMessage.getBodyAsBytes()));
        Assert.assertEquals("Invalid height", 500, result.getHeight());
        Assert.assertEquals("Invalid width", 750, result.getWidth());
    }

    @Test
    public void writeDefaultContentType() throws IOException {
        Resource logo = new ClassPathResource("logo.jpg", BufferedImageHttpMessageConverterTests.class);
        MediaType contentType = new MediaType("image", "png");
        converter.setDefaultContentType(contentType);
        BufferedImage body = ImageIO.read(logo.getFile());
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        converter.write(body, new MediaType("*", "*"), outputMessage);
        Assert.assertEquals("Invalid content type", contentType, outputMessage.getWrittenHeaders().getContentType());
        Assert.assertTrue("Invalid size", ((outputMessage.getBodyAsBytes().length) > 0));
        BufferedImage result = ImageIO.read(new ByteArrayInputStream(outputMessage.getBodyAsBytes()));
        Assert.assertEquals("Invalid height", 500, result.getHeight());
        Assert.assertEquals("Invalid width", 750, result.getWidth());
    }
}

