/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.cxf.jaxrs;


import MediaType.TEXT_PLAIN_TYPE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import javax.ws.rs.core.MediaType;
import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.support.service.ServiceSupport;
import org.apache.cxf.helpers.IOUtils;
import org.junit.Assert;
import org.junit.Test;


public class DataFormatProviderTest extends Assert {
    @Test
    public void testIsReadableWriteableSpecificMatch() {
        DataFormatProvider<DataFormatProviderTest.Book> p = new DataFormatProvider();
        p.setFormat("text/plain", new DataFormatProviderTest.TestDataFormat());
        Assert.assertTrue(p.isReadable(DataFormatProviderTest.Book.class, DataFormatProviderTest.Book.class, new Annotation[]{  }, TEXT_PLAIN_TYPE));
        Assert.assertTrue(p.isWriteable(DataFormatProviderTest.Book.class, DataFormatProviderTest.Book.class, new Annotation[]{  }, TEXT_PLAIN_TYPE));
    }

    @Test
    public void testIsReadableWriteableComplexSubMatch() {
        DataFormatProvider<DataFormatProviderTest.Book> p = new DataFormatProvider();
        p.setFormat("text/plain", new DataFormatProviderTest.TestDataFormat());
        Assert.assertTrue(p.isReadable(DataFormatProviderTest.Book.class, DataFormatProviderTest.Book.class, new Annotation[]{  }, MediaType.valueOf("text/plain+v2")));
        Assert.assertTrue(p.isWriteable(DataFormatProviderTest.Book.class, DataFormatProviderTest.Book.class, new Annotation[]{  }, MediaType.valueOf("text/plain+v2")));
    }

    @Test
    public void testIsReadableWriteableStarMatch() {
        DataFormatProvider<DataFormatProviderTest.Book> p = new DataFormatProvider();
        p.setFormat(new DataFormatProviderTest.TestDataFormat());
        Assert.assertTrue(p.isReadable(DataFormatProviderTest.Book.class, DataFormatProviderTest.Book.class, new Annotation[]{  }, TEXT_PLAIN_TYPE));
        Assert.assertTrue(p.isWriteable(DataFormatProviderTest.Book.class, DataFormatProviderTest.Book.class, new Annotation[]{  }, TEXT_PLAIN_TYPE));
    }

    @Test
    public void testNotReadableWriteable() {
        DataFormatProvider<DataFormatProviderTest.Book> p = new DataFormatProvider();
        p.setFormat("application/json", new DataFormatProviderTest.TestDataFormat());
        Assert.assertFalse(p.isReadable(DataFormatProviderTest.Book.class, DataFormatProviderTest.Book.class, new Annotation[]{  }, TEXT_PLAIN_TYPE));
        Assert.assertFalse(p.isWriteable(DataFormatProviderTest.Book.class, DataFormatProviderTest.Book.class, new Annotation[]{  }, TEXT_PLAIN_TYPE));
    }

    @Test
    public void testReadFrom() throws Exception {
        DataFormatProvider<DataFormatProviderTest.Book> p = new DataFormatProvider();
        p.setFormat("text/plain", new DataFormatProviderTest.TestDataFormat());
        ByteArrayInputStream bis = new ByteArrayInputStream("dataformat".getBytes());
        DataFormatProviderTest.Book b = p.readFrom(DataFormatProviderTest.Book.class, DataFormatProviderTest.Book.class, new Annotation[]{  }, TEXT_PLAIN_TYPE, new org.apache.cxf.jaxrs.impl.MetadataMap<String, String>(), bis);
        Assert.assertEquals("dataformat", b.getName());
    }

    @Test
    public void testWriteTo() throws Exception {
        DataFormatProvider<DataFormatProviderTest.Book> p = new DataFormatProvider();
        p.setFormat("text/plain", new DataFormatProviderTest.TestDataFormat());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        p.writeTo(new DataFormatProviderTest.Book("dataformat"), DataFormatProviderTest.Book.class, DataFormatProviderTest.Book.class, new Annotation[]{  }, TEXT_PLAIN_TYPE, new org.apache.cxf.jaxrs.impl.MetadataMap<String, Object>(), bos);
        Assert.assertEquals("dataformat", bos.toString());
    }

    private static class Book {
        private String name;

        @SuppressWarnings("unused")
        Book() {
        }

        Book(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @SuppressWarnings("unused")
        public void setName(String name) {
            this.name = name;
        }
    }

    private static class TestDataFormat extends ServiceSupport implements DataFormat {
        @Override
        public void marshal(Exchange ex, Object obj, OutputStream os) throws Exception {
            os.write(((DataFormatProviderTest.Book) (obj)).getName().getBytes());
            os.flush();
        }

        @Override
        public Object unmarshal(Exchange ex, InputStream is) throws Exception {
            return new DataFormatProviderTest.Book(IOUtils.readStringFromStream(is));
        }

        @Override
        protected void doStart() throws Exception {
            // noop
        }

        @Override
        protected void doStop() throws Exception {
            // noop
        }
    }
}

