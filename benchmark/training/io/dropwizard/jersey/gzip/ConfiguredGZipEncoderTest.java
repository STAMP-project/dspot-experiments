package io.dropwizard.jersey.gzip;


import HttpHeaders.CONTENT_ENCODING;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptorContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class ConfiguredGZipEncoderTest {
    @Test
    public void gzipParametersSpec() throws IOException {
        ClientRequestContext context = Mockito.mock(ClientRequestContext.class);
        MultivaluedMap<String, Object> headers = new javax.ws.rs.core.MultivaluedHashMap();
        Mockito.when(context.getHeaders()).thenReturn(headers);
        headers.put(CONTENT_ENCODING, null);
        Mockito.when(context.hasEntity()).thenReturn(true);
        new ConfiguredGZipEncoder(true).filter(context);
        assertThat(headers.getFirst(CONTENT_ENCODING).toString()).isEqualTo("gzip");
    }

    @Test
    public void aroundWriteToSpec() throws IOException, WebApplicationException {
        MultivaluedMap<String, Object> headers = new javax.ws.rs.core.MultivaluedHashMap();
        headers.add(CONTENT_ENCODING, "gzip");
        ConfiguredGZipEncoderTest.WriterInterceptorContextMock context = new ConfiguredGZipEncoderTest.WriterInterceptorContextMock(headers);
        new ConfiguredGZipEncoder(true).aroundWriteTo(context);
        assertThat(context.getOutputStream()).isInstanceOf(GZIPOutputStream.class);
        assertThat(context.isProceedCalled()).isTrue();
    }

    @Test
    public void aroundWriteToSpecX_GZip() throws IOException, WebApplicationException {
        MultivaluedMap<String, Object> headers = new javax.ws.rs.core.MultivaluedHashMap();
        headers.add(CONTENT_ENCODING, "x-gzip");
        ConfiguredGZipEncoderTest.WriterInterceptorContextMock context = new ConfiguredGZipEncoderTest.WriterInterceptorContextMock(headers);
        new ConfiguredGZipEncoder(true).aroundWriteTo(context);
        assertThat(context.getOutputStream()).isInstanceOf(GZIPOutputStream.class);
        assertThat(context.isProceedCalled()).isTrue();
    }

    @Test
    public void otherEncodingWillNotAroundWrite() throws IOException, WebApplicationException {
        MultivaluedMap<String, Object> headers = new javax.ws.rs.core.MultivaluedHashMap();
        headers.add(CONTENT_ENCODING, "someOtherEnc");
        ConfiguredGZipEncoderTest.WriterInterceptorContextMock context = new ConfiguredGZipEncoderTest.WriterInterceptorContextMock(headers);
        new ConfiguredGZipEncoder(true).aroundWriteTo(context);
        assertThat(context.getOutputStream()).isNotInstanceOf(GZIPOutputStream.class);
        assertThat(context.isProceedCalled()).isTrue();
    }

    @Test
    public void noEncodingwillNotAroundWrite() throws IOException, WebApplicationException {
        MultivaluedMap<String, Object> headers = new javax.ws.rs.core.MultivaluedHashMap();
        headers.add(CONTENT_ENCODING, null);
        ConfiguredGZipEncoderTest.WriterInterceptorContextMock context = new ConfiguredGZipEncoderTest.WriterInterceptorContextMock(headers);
        new ConfiguredGZipEncoder(true).aroundWriteTo(context);
        assertThat(context.getOutputStream()).isNotInstanceOf(GZIPOutputStream.class);
        assertThat(context.isProceedCalled()).isTrue();
    }

    private static class WriterInterceptorContextMock implements WriterInterceptorContext {
        private final MultivaluedMap<String, Object> headers;

        private OutputStream os = new OutputStream() {
            @Override
            public void write(int i) throws IOException {
                // void
            }
        };

        private boolean proceedCalled = false;

        public WriterInterceptorContextMock(MultivaluedMap<String, Object> headers) {
            this.headers = headers;
        }

        @Override
        public void proceed() throws IOException, WebApplicationException {
            proceedCalled = true;
        }

        @Override
        @Nullable
        public Object getEntity() {
            return null;
        }

        @Override
        public void setEntity(Object entity) {
        }

        @Override
        public OutputStream getOutputStream() {
            return os;
        }

        @Override
        public void setOutputStream(OutputStream os) {
            this.os = os;
        }

        @Override
        public MultivaluedMap<String, Object> getHeaders() {
            return headers;
        }

        @Override
        @Nullable
        public Object getProperty(String name) {
            return null;
        }

        @Override
        @Nullable
        public Collection<String> getPropertyNames() {
            return null;
        }

        @Override
        public void setProperty(String name, Object object) {
        }

        @Override
        public void removeProperty(String name) {
        }

        @Override
        public Annotation[] getAnnotations() {
            return new Annotation[0];
        }

        @Override
        public void setAnnotations(Annotation[] annotations) {
        }

        @Override
        @Nullable
        public Class<?> getType() {
            return null;
        }

        @Override
        public void setType(Class<?> type) {
        }

        @Override
        @Nullable
        public Type getGenericType() {
            return null;
        }

        @Override
        public void setGenericType(Type genericType) {
        }

        @Override
        @Nullable
        public MediaType getMediaType() {
            return null;
        }

        @Override
        public void setMediaType(MediaType mediaType) {
        }

        public boolean isProceedCalled() {
            return proceedCalled;
        }
    }
}

