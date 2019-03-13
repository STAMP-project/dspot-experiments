/**
 * Copyright (C) 2016 Square, Inc.
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
package okhttp3;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import org.junit.Assert;
import org.junit.Test;


public final class ResponseBodyTest {
    @Test
    public void stringEmpty() throws IOException {
        ResponseBody body = ResponseBodyTest.body("");
        Assert.assertEquals("", body.string());
    }

    @Test
    public void stringLooksLikeBomButTooShort() throws IOException {
        ResponseBody body = ResponseBodyTest.body("000048");
        Assert.assertEquals("\u0000\u0000H", body.string());
    }

    @Test
    public void stringDefaultsToUtf8() throws IOException {
        ResponseBody body = ResponseBodyTest.body("68656c6c6f");
        Assert.assertEquals("hello", body.string());
    }

    @Test
    public void stringExplicitCharset() throws IOException {
        ResponseBody body = ResponseBodyTest.body("00000068000000650000006c0000006c0000006f", "utf-32be");
        Assert.assertEquals("hello", body.string());
    }

    @Test
    public void stringBomOverridesExplicitCharset() throws IOException {
        ResponseBody body = ResponseBodyTest.body("0000ffff00000068000000650000006c0000006c0000006f", "utf-8");
        Assert.assertEquals("hello", body.string());
    }

    @Test
    public void stringBomUtf8() throws IOException {
        ResponseBody body = ResponseBodyTest.body("efbbbf68656c6c6f");
        Assert.assertEquals("hello", body.string());
    }

    @Test
    public void stringBomUtf16Be() throws IOException {
        ResponseBody body = ResponseBodyTest.body("feff00680065006c006c006f");
        Assert.assertEquals("hello", body.string());
    }

    @Test
    public void stringBomUtf16Le() throws IOException {
        ResponseBody body = ResponseBodyTest.body("fffe680065006c006c006f00");
        Assert.assertEquals("hello", body.string());
    }

    @Test
    public void stringBomUtf32Be() throws IOException {
        ResponseBody body = ResponseBodyTest.body("0000ffff00000068000000650000006c0000006c0000006f");
        Assert.assertEquals("hello", body.string());
    }

    @Test
    public void stringBomUtf32Le() throws IOException {
        ResponseBody body = ResponseBodyTest.body("ffff000068000000650000006c0000006c0000006f000000");
        Assert.assertEquals("hello", body.string());
    }

    @Test
    public void stringClosesUnderlyingSource() throws IOException {
        final AtomicBoolean closed = new AtomicBoolean();
        ResponseBody body = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 5;
            }

            @Override
            public BufferedSource source() {
                Buffer source = new Buffer().writeUtf8("hello");
                return Okio.buffer(new ForwardingSource(source) {
                    @Override
                    public void close() throws IOException {
                        closed.set(true);
                        super.close();
                    }
                });
            }
        };
        Assert.assertEquals("hello", body.string());
        Assert.assertTrue(closed.get());
    }

    @Test
    public void readerEmpty() throws IOException {
        ResponseBody body = ResponseBodyTest.body("");
        Assert.assertEquals("", ResponseBodyTest.exhaust(body.charStream()));
    }

    @Test
    public void readerLooksLikeBomButTooShort() throws IOException {
        ResponseBody body = ResponseBodyTest.body("000048");
        Assert.assertEquals("\u0000\u0000H", ResponseBodyTest.exhaust(body.charStream()));
    }

    @Test
    public void readerDefaultsToUtf8() throws IOException {
        ResponseBody body = ResponseBodyTest.body("68656c6c6f");
        Assert.assertEquals("hello", ResponseBodyTest.exhaust(body.charStream()));
    }

    @Test
    public void readerExplicitCharset() throws IOException {
        ResponseBody body = ResponseBodyTest.body("00000068000000650000006c0000006c0000006f", "utf-32be");
        Assert.assertEquals("hello", ResponseBodyTest.exhaust(body.charStream()));
    }

    @Test
    public void readerBomUtf8() throws IOException {
        ResponseBody body = ResponseBodyTest.body("efbbbf68656c6c6f");
        Assert.assertEquals("hello", ResponseBodyTest.exhaust(body.charStream()));
    }

    @Test
    public void readerBomUtf16Be() throws IOException {
        ResponseBody body = ResponseBodyTest.body("feff00680065006c006c006f");
        Assert.assertEquals("hello", ResponseBodyTest.exhaust(body.charStream()));
    }

    @Test
    public void readerBomUtf16Le() throws IOException {
        ResponseBody body = ResponseBodyTest.body("fffe680065006c006c006f00");
        Assert.assertEquals("hello", ResponseBodyTest.exhaust(body.charStream()));
    }

    @Test
    public void readerBomUtf32Be() throws IOException {
        ResponseBody body = ResponseBodyTest.body("0000ffff00000068000000650000006c0000006c0000006f");
        Assert.assertEquals("hello", ResponseBodyTest.exhaust(body.charStream()));
    }

    @Test
    public void readerBomUtf32Le() throws IOException {
        ResponseBody body = ResponseBodyTest.body("ffff000068000000650000006c0000006c0000006f000000");
        Assert.assertEquals("hello", ResponseBodyTest.exhaust(body.charStream()));
    }

    @Test
    public void readerClosedBeforeBomClosesUnderlyingSource() throws IOException {
        final AtomicBoolean closed = new AtomicBoolean();
        ResponseBody body = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 5;
            }

            @Override
            public BufferedSource source() {
                ResponseBody body = ResponseBodyTest.body("fffe680065006c006c006f00");
                return Okio.buffer(new ForwardingSource(body.source()) {
                    @Override
                    public void close() throws IOException {
                        closed.set(true);
                        super.close();
                    }
                });
            }
        };
        body.charStream().close();
        Assert.assertTrue(closed.get());
    }

    @Test
    public void readerClosedAfterBomClosesUnderlyingSource() throws IOException {
        final AtomicBoolean closed = new AtomicBoolean();
        ResponseBody body = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 5;
            }

            @Override
            public BufferedSource source() {
                ResponseBody body = ResponseBodyTest.body("fffe680065006c006c006f00");
                return Okio.buffer(new ForwardingSource(body.source()) {
                    @Override
                    public void close() throws IOException {
                        closed.set(true);
                        super.close();
                    }
                });
            }
        };
        Reader reader = body.charStream();
        Assert.assertEquals('h', reader.read());
        reader.close();
        Assert.assertTrue(closed.get());
    }

    @Test
    public void sourceEmpty() throws IOException {
        ResponseBody body = ResponseBodyTest.body("");
        BufferedSource source = body.source();
        Assert.assertTrue(source.exhausted());
        Assert.assertEquals("", source.readUtf8());
    }

    @Test
    public void sourceSeesBom() throws IOException {
        ResponseBody body = ResponseBodyTest.body("efbbbf68656c6c6f");
        BufferedSource source = body.source();
        Assert.assertEquals(239, ((source.readByte()) & 255));
        Assert.assertEquals(187, ((source.readByte()) & 255));
        Assert.assertEquals(191, ((source.readByte()) & 255));
        Assert.assertEquals("hello", source.readUtf8());
    }

    @Test
    public void sourceClosesUnderlyingSource() throws IOException {
        final AtomicBoolean closed = new AtomicBoolean();
        ResponseBody body = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 5;
            }

            @Override
            public BufferedSource source() {
                Buffer source = new Buffer().writeUtf8("hello");
                return Okio.buffer(new ForwardingSource(source) {
                    @Override
                    public void close() throws IOException {
                        closed.set(true);
                        super.close();
                    }
                });
            }
        };
        body.source().close();
        Assert.assertTrue(closed.get());
    }

    @Test
    public void bytesEmpty() throws IOException {
        ResponseBody body = ResponseBodyTest.body("");
        Assert.assertEquals(0, body.bytes().length);
    }

    @Test
    public void bytesSeesBom() throws IOException {
        ResponseBody body = ResponseBodyTest.body("efbbbf68656c6c6f");
        byte[] bytes = body.bytes();
        Assert.assertEquals(239, ((bytes[0]) & 255));
        Assert.assertEquals(187, ((bytes[1]) & 255));
        Assert.assertEquals(191, ((bytes[2]) & 255));
        Assert.assertEquals("hello", new String(bytes, 3, 5, StandardCharsets.UTF_8));
    }

    @Test
    public void bytesClosesUnderlyingSource() throws IOException {
        final AtomicBoolean closed = new AtomicBoolean();
        ResponseBody body = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 5;
            }

            @Override
            public BufferedSource source() {
                Buffer source = new Buffer().writeUtf8("hello");
                return Okio.buffer(new ForwardingSource(source) {
                    @Override
                    public void close() throws IOException {
                        closed.set(true);
                        super.close();
                    }
                });
            }
        };
        Assert.assertEquals(5, body.bytes().length);
        Assert.assertTrue(closed.get());
    }

    @Test
    public void bytesThrowsWhenLengthsDisagree() {
        ResponseBody body = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 10;
            }

            @Override
            public BufferedSource source() {
                return new Buffer().writeUtf8("hello");
            }
        };
        try {
            body.bytes();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("Content-Length (10) and stream length (5) disagree", e.getMessage());
        }
    }

    @Test
    public void bytesThrowsMoreThanIntMaxValue() {
        ResponseBody body = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return (Integer.MAX_VALUE) + 1L;
            }

            @Override
            public BufferedSource source() {
                throw new AssertionError();
            }
        };
        try {
            body.bytes();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("Cannot buffer entire body for content length: 2147483648", e.getMessage());
        }
    }

    @Test
    public void byteStreamEmpty() throws IOException {
        ResponseBody body = ResponseBodyTest.body("");
        InputStream bytes = body.byteStream();
        Assert.assertEquals((-1), bytes.read());
    }

    @Test
    public void byteStreamSeesBom() throws IOException {
        ResponseBody body = ResponseBodyTest.body("efbbbf68656c6c6f");
        InputStream bytes = body.byteStream();
        Assert.assertEquals(239, bytes.read());
        Assert.assertEquals(187, bytes.read());
        Assert.assertEquals(191, bytes.read());
        Assert.assertEquals("hello", ResponseBodyTest.exhaust(new InputStreamReader(bytes, StandardCharsets.UTF_8)));
    }

    @Test
    public void byteStreamClosesUnderlyingSource() throws IOException {
        final AtomicBoolean closed = new AtomicBoolean();
        ResponseBody body = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 5;
            }

            @Override
            public BufferedSource source() {
                Buffer source = new Buffer().writeUtf8("hello");
                return Okio.buffer(new ForwardingSource(source) {
                    @Override
                    public void close() throws IOException {
                        closed.set(true);
                        super.close();
                    }
                });
            }
        };
        body.byteStream().close();
        Assert.assertTrue(closed.get());
    }

    @Test
    public void throwingUnderlyingSourceClosesQuietly() throws IOException {
        ResponseBody body = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 5;
            }

            @Override
            public BufferedSource source() {
                Buffer source = new Buffer().writeUtf8("hello");
                return Okio.buffer(new ForwardingSource(source) {
                    @Override
                    public void close() throws IOException {
                        throw new IOException("Broken!");
                    }
                });
            }
        };
        Assert.assertEquals("hello", body.source().readUtf8());
        body.close();
    }
}

