package com.netflix.discovery.converters.wrappers;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.core.MediaType;
import junit.framework.Assert;
import org.junit.Test;


/**
 *
 *
 * @author David Liu
 */
public class CodecWrappersTest {
    private static String testWrapperName = "FOO_WRAPPER";

    @Test
    public void testRegisterNewWrapper() {
        Assert.assertNull(CodecWrappers.getEncoder(CodecWrappersTest.testWrapperName));
        Assert.assertNull(CodecWrappers.getDecoder(CodecWrappersTest.testWrapperName));
        CodecWrappers.registerWrapper(new CodecWrappersTest.TestWrapper());
        Assert.assertNotNull(CodecWrappers.getEncoder(CodecWrappersTest.testWrapperName));
        Assert.assertNotNull(CodecWrappers.getDecoder(CodecWrappersTest.testWrapperName));
    }

    private final class TestWrapper implements CodecWrapper {
        @Override
        public <T> T decode(String textValue, Class<T> type) throws IOException {
            return null;
        }

        @Override
        public <T> T decode(InputStream inputStream, Class<T> type) throws IOException {
            return null;
        }

        @Override
        public <T> String encode(T object) throws IOException {
            return null;
        }

        @Override
        public <T> void encode(T object, OutputStream outputStream) throws IOException {
        }

        @Override
        public String codecName() {
            return CodecWrappersTest.testWrapperName;
        }

        @Override
        public boolean support(MediaType mediaType) {
            return false;
        }
    }
}

