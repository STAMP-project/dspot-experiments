package io.fabric8.maven.docker.access.log;


import LogRequestor.LOG_LINE;
import io.fabric8.maven.docker.access.UrlBuilder;
import io.fabric8.maven.docker.access.util.RequestUtil;
import io.fabric8.maven.docker.util.Timestamp;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.regex.Matcher;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class LogRequestorTest {
    private static final String containerId = new RandomStringGenerator.Builder().build().generate(64);

    @Mocked(stubOutClassInitialization = true)
    final RequestUtil unused = null;

    @Mocked
    CloseableHttpResponse httpResponse;

    @Mocked
    UrlBuilder urlBuilder;

    @Mocked
    StatusLine statusLine;

    @Mocked
    HttpEntity httpEntity;

    @Mocked
    HttpUriRequest httpUriRequest;

    @Mocked
    LogCallback callback;

    @Mocked
    CloseableHttpClient client;

    @Test
    public void testEmptyMessage() throws Exception {
        final LogRequestorTest.Streams type = LogRequestorTest.Streams.STDOUT;
        final ByteBuffer headerBuffer = ByteBuffer.allocate(8);
        headerBuffer.put(((byte) (type.type)));
        headerBuffer.putInt(4, 0);
        final InputStream inputStream = new ByteArrayInputStream(headerBuffer.array());
        setupMocks(inputStream);
        fetchLogs();
        new Verifications() {
            {
                callback.log(type.type, ((Timestamp) (any)), anyString);
                times = 0;
            }
        };
    }

    @Test
    public void testStdoutMessage() throws Exception {
        final LogRequestorTest.Streams type = LogRequestorTest.Streams.STDOUT;
        RandomStringGenerator randomGenerator = new RandomStringGenerator.Builder().build();
        final String message0 = randomGenerator.generate(257);
        final String message1 = "test test";
        final String message2 = randomGenerator.generate(666);
        final ByteBuffer body = LogRequestorTest.responseContent(type, message0, message1, message2);
        final InputStream inputStream = new ByteArrayInputStream(body.array());
        setupMocks(inputStream);
        fetchLogs();
        new Verifications() {
            {
                callback.log(type.type, ((Timestamp) (any)), message0);
                callback.log(type.type, ((Timestamp) (any)), message1);
                callback.log(type.type, ((Timestamp) (any)), message2);
            }
        };
    }

    @Test
    public void testMessageWithLeadingWhitespace() throws Exception {
        final LogRequestorTest.Streams type = LogRequestorTest.Streams.STDOUT;
        final String message0 = " I have a leading space";
        final String message1 = "\tI have a leading tab";
        final ByteBuffer body = LogRequestorTest.responseContent(type, message0, message1);
        final InputStream inputStream = new ByteArrayInputStream(body.array());
        setupMocks(inputStream);
        fetchLogs();
        new Verifications() {
            {
                callback.log(type.type, ((Timestamp) (any)), message0);
                callback.log(type.type, ((Timestamp) (any)), message1);
            }
        };
    }

    @Test
    public void testAllStreams() throws Exception {
        final Random rand = new Random();
        final int upperBound = 1024;
        RandomStringGenerator randomGenerator = new RandomStringGenerator.Builder().build();
        final LogRequestorTest.Streams type0 = LogRequestorTest.Streams.STDIN;
        final String msg0 = randomGenerator.generate(rand.nextInt(upperBound));
        final ByteBuffer buf0 = LogRequestorTest.messageToBuffer(type0, msg0);
        final LogRequestorTest.Streams type1 = LogRequestorTest.Streams.STDOUT;
        final String msg1 = randomGenerator.generate(rand.nextInt(upperBound));
        final ByteBuffer buf1 = LogRequestorTest.messageToBuffer(type1, msg1);
        final LogRequestorTest.Streams type2 = LogRequestorTest.Streams.STDERR;
        final String msg2 = randomGenerator.generate(rand.nextInt(upperBound));
        final ByteBuffer buf2 = LogRequestorTest.messageToBuffer(type2, msg2);
        final ByteBuffer body = LogRequestorTest.combineBuffers(buf0, buf1, buf2);
        final InputStream inputStream = new ByteArrayInputStream(body.array());
        setupMocks(inputStream);
        fetchLogs();
        new Verifications() {
            {
                callback.log(type0.type, ((Timestamp) (any)), msg0);
                callback.log(type1.type, ((Timestamp) (any)), msg1);
                callback.log(type2.type, ((Timestamp) (any)), msg2);
            }
        };
    }

    @Test
    public void testGarbageMessage() throws Exception {
        final LogRequestorTest.Streams type = LogRequestorTest.Streams.STDERR;
        final ByteBuffer buf0 = LogRequestorTest.messageToBuffer(type, "This is a test message");
        final ByteBuffer buf1 = LogRequestorTest.messageToBuffer(type, "This is another test message!");
        // Add one extra byte to buf0.
        int l0 = buf0.getInt(4);
        buf0.putInt(4, (l0 + 1));
        // Set incorrect length in buf1.
        int l1 = buf1.getInt(4);
        buf1.putInt(4, (l1 + 512));
        final ByteBuffer messages = ByteBuffer.allocate(((buf0.limit()) + (buf1.limit())));
        buf0.position(0);
        buf1.position(0);
        messages.put(buf0);
        messages.put(buf1);
        final InputStream inputStream = new ByteArrayInputStream(messages.array());
        setupMocks(inputStream);
        fetchLogs();
        new Verifications() {
            {
                // Should have called log() one time (for the first message). The message itself would
                // have been incorrect, since we gave it the wrong buffer length. The second message
                // fails to parse as the buffer runs out.
                callback.log(type.type, ((Timestamp) (any)), anyString);
                times = 1;
            }
        };
    }

    @Test
    public void testMessageTooShort() throws Exception {
        final LogRequestorTest.Streams type = LogRequestorTest.Streams.STDIN;
        final ByteBuffer buf = LogRequestorTest.messageToBuffer(type, "A man, a plan, a canal, Panama!");
        // Set length too long so reading buffer overflows.
        int l = buf.getInt(4);
        buf.putInt(4, (l + 1));
        final InputStream inputStream = new ByteArrayInputStream(buf.array());
        setupMocks(inputStream);
        fetchLogs();
        new Verifications() {
            {
                // No calls to .log() should be made, as message parsing fails.
                callback.log(type.type, ((Timestamp) (any)), anyString);
                times = 0;
            }
        };
    }

    @Test
    public void testMessageWithExtraBytes() throws Exception {
        final LogRequestorTest.Streams type = LogRequestorTest.Streams.STDOUT;
        final String message = "A man, a plan, a canal, Panama!";
        final ByteBuffer buf = LogRequestorTest.messageToBuffer(type, message);
        // Set length too short so there is extra buffer left after reading.
        int l = buf.getInt(4);
        buf.putInt(4, (l - 1));
        final InputStream inputStream = new ByteArrayInputStream(buf.array());
        setupMocks(inputStream);
        fetchLogs();
        MatcherAssert.assertThat("Entire InputStream read.", ((ByteArrayInputStream) (inputStream)).available(), CoreMatchers.equalTo(0));
        new Verifications() {
            {
                // .log() is only called once. The one byte that is left off is lost and never recorded.
                callback.log(type.type, ((Timestamp) (any)), message.substring(0, ((message.length()) - 1)));
                times = 1;
            }
        };
    }

    @Test
    public void checkMutlilinePattern() {
        String line = "2016-07-15T20:34:06.024029849Z remote: Compressing objects:   4% (1/23)           \n" + "remote: Compressing objects:   8% (2/23)           \n";
        String matched = "remote: Compressing objects:   4% (1/23)           \n" + "remote: Compressing objects:   8% (2/23)";
        Matcher matcher = LOG_LINE.matcher(line);
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals(matched, matcher.group("entry"));
    }

    @Test
    public void runCallsOpenAndCloseOnHandler() throws Exception {
        final LogRequestorTest.Streams type = LogRequestorTest.Streams.STDOUT;
        final String message = "";
        final ByteBuffer buf = LogRequestorTest.messageToBuffer(type, message);
        final InputStream inputStream = new ByteArrayInputStream(buf.array());
        setupMocks(inputStream);
        new Expectations() {
            {
                callback.open();
                times = 1;
                callback.close();
                times = 1;
            }
        };
        run();
    }

    @Test
    public void runCanConsumeEmptyStream() throws Exception {
        final LogRequestorTest.Streams type = LogRequestorTest.Streams.STDOUT;
        final String message = "";
        final ByteBuffer buf = LogRequestorTest.messageToBuffer(type, message);
        final InputStream inputStream = new ByteArrayInputStream(buf.array());
        setupMocks(inputStream);
        run();
    }

    @Test
    public void runCanConsumeSingleLineStream() throws Exception {
        final LogRequestorTest.Streams type = LogRequestorTest.Streams.STDOUT;
        final String message = "Hello, world!";
        final ByteBuffer buf = LogRequestorTest.messageToBuffer(type, message);
        final InputStream inputStream = new ByteArrayInputStream(buf.array());
        setupMocks(inputStream);
        new Expectations() {
            {
                callback.log(type.type, ((Timestamp) (any)), anyString);
                times = 1;
            }
        };
        run();
    }

    @Test
    public void runCanHandleIOException() throws Exception {
        final LogRequestorTest.IOExcpetionStream stream = new LogRequestorTest.IOExcpetionStream();
        setupMocks(stream);
        new Expectations() {
            {
                callback.error(anyString);
                times = 1;
            }
        };
        run();
    }

    private enum Streams {

        STDIN(0),
        STDOUT(1),
        STDERR(2);
        public final int type;

        Streams(int type) {
            this.type = type;
        }
    }

    private class IOExcpetionStream extends InputStream {
        public int read() throws IOException {
            throw new IOException("Something bad happened");
        }
    }
}

