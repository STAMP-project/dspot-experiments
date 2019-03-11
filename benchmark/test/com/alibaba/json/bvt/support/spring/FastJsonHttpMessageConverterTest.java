package com.alibaba.json.bvt.support.spring;


import MediaType.ALL;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_JSON_UTF8;
import MediaType.TEXT_PLAIN;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Assert;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;


public class FastJsonHttpMessageConverterTest extends TestCase {
    public void test_1() throws Exception {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        Assert.assertNotNull(converter.getFastJsonConfig());
        converter.setFastJsonConfig(new FastJsonConfig());
        converter.canRead(FastJsonHttpMessageConverterTest.VO.class, APPLICATION_JSON_UTF8);
        converter.canWrite(FastJsonHttpMessageConverterTest.VO.class, APPLICATION_JSON_UTF8);
        converter.canRead(FastJsonHttpMessageConverterTest.VO.class, FastJsonHttpMessageConverterTest.VO.class, APPLICATION_JSON_UTF8);
        converter.canWrite(FastJsonHttpMessageConverterTest.VO.class, FastJsonHttpMessageConverterTest.VO.class, APPLICATION_JSON_UTF8);
        HttpInputMessage input = new HttpInputMessage() {
            public HttpHeaders getHeaders() {
                // TODO Auto-generated method stub
                return null;
            }

            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("{\"id\":123}".getBytes(Charset.forName("UTF-8")));
            }
        };
        FastJsonHttpMessageConverterTest.VO vo = ((FastJsonHttpMessageConverterTest.VO) (converter.read(FastJsonHttpMessageConverterTest.VO.class, FastJsonHttpMessageConverterTest.VO.class, input)));
        Assert.assertEquals(123, vo.getId());
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        HttpOutputMessage out = new HttpOutputMessage() {
            public HttpHeaders getHeaders() {
                return new HttpHeaders();
            }

            public OutputStream getBody() throws IOException {
                return byteOut;
            }
        };
        converter.write(vo, FastJsonHttpMessageConverterTest.VO.class, TEXT_PLAIN, out);
        byte[] bytes = byteOut.toByteArray();
        Assert.assertEquals("{\"id\":123}", new String(bytes, "UTF-8"));
        converter.setSupportedMediaTypes(Collections.singletonList(APPLICATION_JSON));
        converter.write(vo, FastJsonHttpMessageConverterTest.VO.class, null, out);
        converter.write(vo, FastJsonHttpMessageConverterTest.VO.class, ALL, out);
        HttpOutputMessage out2 = new HttpOutputMessage() {
            public HttpHeaders getHeaders() {
                return new HttpHeaders() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public MediaType getContentType() {
                        return MediaType.APPLICATION_JSON;
                    }

                    @Override
                    public long getContentLength() {
                        return 1;
                    }
                };
            }

            public OutputStream getBody() throws IOException {
                return byteOut;
            }
        };
        converter.write(vo, FastJsonHttpMessageConverterTest.VO.class, ALL, out2);
    }

    private SerializeFilter serializeFilter = new ValueFilter() {
        @Override
        public Object process(Object object, String name, Object value) {
            if (value == null) {
                return "";
            }
            if (value instanceof Number) {
                return String.valueOf(value);
            }
            return value;
        }
    };

    public static class VO {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

