/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.server;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.servlet.ReadListener;
import org.eclipse.jetty.util.BufferUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * this tests HttpInput and its interaction with HttpChannelState
 */
public class HttpInputAsyncStateTest {
    private static final Queue<String> __history = new LinkedBlockingQueue<>();

    private ByteBuffer _expected = BufferUtil.allocate((16 * 1024));

    private boolean _eof;

    private boolean _noReadInDataAvailable;

    private boolean _completeInOnDataAvailable;

    private final ReadListener _listener = new ReadListener() {
        @Override
        public void onError(Throwable t) {
            HttpInputAsyncStateTest.__history.add(("onError:" + t));
        }

        @Override
        public void onDataAvailable() throws IOException {
            HttpInputAsyncStateTest.__history.add("onDataAvailable");
            if (((!(_noReadInDataAvailable)) && (readAvailable())) && (_completeInOnDataAvailable)) {
                HttpInputAsyncStateTest.__history.add("complete");
                _state.complete();
            }
        }

        @Override
        public void onAllDataRead() throws IOException {
            HttpInputAsyncStateTest.__history.add("onAllDataRead");
        }
    };

    private HttpInput _in;

    HttpChannelState _state;

    public static class TContent extends HttpInput.Content {
        public TContent(String content) {
            super(BufferUtil.toBuffer(content));
        }
    }

    @Test
    public void testInitialEmptyListenInHandle() throws Exception {
        deliver(HttpInput.EOF_CONTENT);
        check();
        handle(() -> {
            _state.startAsync(null);
            _in.setReadListener(_listener);
            check("onReadReady false");
        });
        check("onAllDataRead");
    }

    @Test
    public void testInitialEmptyListenAfterHandle() throws Exception {
        deliver(HttpInput.EOF_CONTENT);
        handle(() -> {
            _state.startAsync(null);
            check();
        });
        _in.setReadListener(_listener);
        check("onReadReady true", "wake");
        wake();
        check("onAllDataRead");
    }

    @Test
    public void testListenInHandleEmpty() throws Exception {
        handle(() -> {
            _state.startAsync(null);
            _in.setReadListener(_listener);
            check("onReadUnready");
        });
        check("onAsyncWaitForContent");
        deliver(HttpInput.EOF_CONTENT);
        check("onReadPossible true");
        handle();
        check("onAllDataRead");
    }

    @Test
    public void testEmptyListenAfterHandle() throws Exception {
        handle(() -> {
            _state.startAsync(null);
            check();
        });
        deliver(HttpInput.EOF_CONTENT);
        check();
        _in.setReadListener(_listener);
        check("onReadReady true", "wake");
        wake();
        check("onAllDataRead");
    }

    @Test
    public void testListenAfterHandleEmpty() throws Exception {
        handle(() -> {
            _state.startAsync(null);
            check();
        });
        _in.setReadListener(_listener);
        check("onAsyncWaitForContent", "onReadUnready");
        deliver(HttpInput.EOF_CONTENT);
        check("onReadPossible true");
        handle();
        check("onAllDataRead");
    }

    @Test
    public void testInitialEarlyEOFListenInHandle() throws Exception {
        deliver(HttpInput.EARLY_EOF_CONTENT);
        check();
        handle(() -> {
            _state.startAsync(null);
            _in.setReadListener(_listener);
            check("onReadReady false");
        });
        check("onError:org.eclipse.jetty.io.EofException: Early EOF");
    }

    @Test
    public void testInitialEarlyEOFListenAfterHandle() throws Exception {
        deliver(HttpInput.EARLY_EOF_CONTENT);
        handle(() -> {
            _state.startAsync(null);
            check();
        });
        _in.setReadListener(_listener);
        check("onReadReady true", "wake");
        wake();
        check("onError:org.eclipse.jetty.io.EofException: Early EOF");
    }

    @Test
    public void testListenInHandleEarlyEOF() throws Exception {
        handle(() -> {
            _state.startAsync(null);
            _in.setReadListener(_listener);
            check("onReadUnready");
        });
        check("onAsyncWaitForContent");
        deliver(HttpInput.EARLY_EOF_CONTENT);
        check("onReadPossible true");
        handle();
        check("onError:org.eclipse.jetty.io.EofException: Early EOF");
    }

    @Test
    public void testEarlyEOFListenAfterHandle() throws Exception {
        handle(() -> {
            _state.startAsync(null);
            check();
        });
        deliver(HttpInput.EARLY_EOF_CONTENT);
        check();
        _in.setReadListener(_listener);
        check("onReadReady true", "wake");
        wake();
        check("onError:org.eclipse.jetty.io.EofException: Early EOF");
    }

    @Test
    public void testListenAfterHandleEarlyEOF() throws Exception {
        handle(() -> {
            _state.startAsync(null);
            check();
        });
        _in.setReadListener(_listener);
        check("onAsyncWaitForContent", "onReadUnready");
        deliver(HttpInput.EARLY_EOF_CONTENT);
        check("onReadPossible true");
        handle();
        check("onError:org.eclipse.jetty.io.EofException: Early EOF");
    }

    @Test
    public void testInitialAllContentListenInHandle() throws Exception {
        deliver(new HttpInputAsyncStateTest.TContent("Hello"), HttpInput.EOF_CONTENT);
        check();
        handle(() -> {
            _state.startAsync(null);
            _in.setReadListener(_listener);
            check("onReadReady false");
        });
        check("onDataAvailable", "read 5", "read -1", "onAllDataRead");
    }

    @Test
    public void testInitialAllContentListenAfterHandle() throws Exception {
        deliver(new HttpInputAsyncStateTest.TContent("Hello"), HttpInput.EOF_CONTENT);
        handle(() -> {
            _state.startAsync(null);
            check();
        });
        _in.setReadListener(_listener);
        check("onReadReady true", "wake");
        wake();
        check("onDataAvailable", "read 5", "read -1", "onAllDataRead");
    }

    @Test
    public void testListenInHandleAllContent() throws Exception {
        handle(() -> {
            _state.startAsync(null);
            _in.setReadListener(_listener);
            check("onReadUnready");
        });
        check("onAsyncWaitForContent");
        deliver(new HttpInputAsyncStateTest.TContent("Hello"), HttpInput.EOF_CONTENT);
        check("onReadPossible true", "onReadPossible false");
        handle();
        check("onDataAvailable", "read 5", "read -1", "onAllDataRead");
    }

    @Test
    public void testAllContentListenAfterHandle() throws Exception {
        handle(() -> {
            _state.startAsync(null);
            check();
        });
        deliver(new HttpInputAsyncStateTest.TContent("Hello"), HttpInput.EOF_CONTENT);
        check();
        _in.setReadListener(_listener);
        check("onReadReady true", "wake");
        wake();
        check("onDataAvailable", "read 5", "read -1", "onAllDataRead");
    }

    @Test
    public void testListenAfterHandleAllContent() throws Exception {
        handle(() -> {
            _state.startAsync(null);
            check();
        });
        _in.setReadListener(_listener);
        check("onAsyncWaitForContent", "onReadUnready");
        deliver(new HttpInputAsyncStateTest.TContent("Hello"), HttpInput.EOF_CONTENT);
        check("onReadPossible true", "onReadPossible false");
        handle();
        check("onDataAvailable", "read 5", "read -1", "onAllDataRead");
    }

    @Test
    public void testInitialIncompleteContentListenInHandle() throws Exception {
        deliver(new HttpInputAsyncStateTest.TContent("Hello"), HttpInput.EARLY_EOF_CONTENT);
        check();
        handle(() -> {
            _state.startAsync(null);
            _in.setReadListener(_listener);
            check("onReadReady false");
        });
        check("onDataAvailable", "read 5", "read org.eclipse.jetty.io.EofException: Early EOF", "onError:org.eclipse.jetty.io.EofException: Early EOF");
    }

    @Test
    public void testInitialPartialContentListenAfterHandle() throws Exception {
        deliver(new HttpInputAsyncStateTest.TContent("Hello"), HttpInput.EARLY_EOF_CONTENT);
        handle(() -> {
            _state.startAsync(null);
            check();
        });
        _in.setReadListener(_listener);
        check("onReadReady true", "wake");
        wake();
        check("onDataAvailable", "read 5", "read org.eclipse.jetty.io.EofException: Early EOF", "onError:org.eclipse.jetty.io.EofException: Early EOF");
    }

    @Test
    public void testListenInHandlePartialContent() throws Exception {
        handle(() -> {
            _state.startAsync(null);
            _in.setReadListener(_listener);
            check("onReadUnready");
        });
        check("onAsyncWaitForContent");
        deliver(new HttpInputAsyncStateTest.TContent("Hello"), HttpInput.EARLY_EOF_CONTENT);
        check("onReadPossible true", "onReadPossible false");
        handle();
        check("onDataAvailable", "read 5", "read org.eclipse.jetty.io.EofException: Early EOF", "onError:org.eclipse.jetty.io.EofException: Early EOF");
    }

    @Test
    public void testPartialContentListenAfterHandle() throws Exception {
        handle(() -> {
            _state.startAsync(null);
            check();
        });
        deliver(new HttpInputAsyncStateTest.TContent("Hello"), HttpInput.EARLY_EOF_CONTENT);
        check();
        _in.setReadListener(_listener);
        check("onReadReady true", "wake");
        wake();
        check("onDataAvailable", "read 5", "read org.eclipse.jetty.io.EofException: Early EOF", "onError:org.eclipse.jetty.io.EofException: Early EOF");
    }

    @Test
    public void testListenAfterHandlePartialContent() throws Exception {
        handle(() -> {
            _state.startAsync(null);
            check();
        });
        _in.setReadListener(_listener);
        check("onAsyncWaitForContent", "onReadUnready");
        deliver(new HttpInputAsyncStateTest.TContent("Hello"), HttpInput.EARLY_EOF_CONTENT);
        check("onReadPossible true", "onReadPossible false");
        handle();
        check("onDataAvailable", "read 5", "read org.eclipse.jetty.io.EofException: Early EOF", "onError:org.eclipse.jetty.io.EofException: Early EOF");
    }

    @Test
    public void testReadAfterOnDataAvailable() throws Exception {
        _noReadInDataAvailable = true;
        handle(() -> {
            _state.startAsync(null);
            _in.setReadListener(_listener);
            check("onReadUnready");
        });
        check("onAsyncWaitForContent");
        deliver(new HttpInputAsyncStateTest.TContent("Hello"), HttpInput.EOF_CONTENT);
        check("onReadPossible true", "onReadPossible false");
        handle();
        check("onDataAvailable");
        readAvailable();
        check("wake", "read 5", "read -1");
        wake();
        check("onAllDataRead");
    }

    @Test
    public void testReadOnlyExpectedAfterOnDataAvailable() throws Exception {
        _noReadInDataAvailable = true;
        handle(() -> {
            _state.startAsync(null);
            _in.setReadListener(_listener);
            check("onReadUnready");
        });
        check("onAsyncWaitForContent");
        deliver(new HttpInputAsyncStateTest.TContent("Hello"), HttpInput.EOF_CONTENT);
        check("onReadPossible true", "onReadPossible false");
        handle();
        check("onDataAvailable");
        byte[] buffer = new byte[_expected.remaining()];
        MatcherAssert.assertThat(_in.read(buffer), Matchers.equalTo(buffer.length));
        MatcherAssert.assertThat(new String(buffer), Matchers.equalTo(BufferUtil.toString(_expected)));
        BufferUtil.clear(_expected);
        check();
        Assertions.assertTrue(_in.isReady());
        check();
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo((-1)));
        check("wake");
        wake();
        check("onAllDataRead");
    }

    @Test
    public void testReadAndCompleteInOnDataAvailable() throws Exception {
        _completeInOnDataAvailable = true;
        handle(() -> {
            _state.startAsync(null);
            _in.setReadListener(_listener);
            check("onReadUnready");
        });
        check("onAsyncWaitForContent");
        deliver(new HttpInputAsyncStateTest.TContent("Hello"), HttpInput.EOF_CONTENT);
        check("onReadPossible true", "onReadPossible false");
        handle(() -> {
            HttpInputAsyncStateTest.__history.add(_state.getState().toString());
        });
        System.err.println(HttpInputAsyncStateTest.__history);
        check("onDataAvailable", "read 5", "read -1", "complete", "COMPLETE");
    }
}

