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


import java.io.EOFException;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import javax.servlet.ReadListener;
import org.eclipse.jetty.util.BufferUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class HttpInputTest {
    private final Queue<String> _history = new LinkedBlockingQueue<>();

    private final Queue<String> _fillAndParseSimulate = new LinkedBlockingQueue<>();

    private final ReadListener _listener = new ReadListener() {
        @Override
        public void onError(Throwable t) {
            _history.add(("l.onError:" + t));
        }

        @Override
        public void onDataAvailable() throws IOException {
            _history.add("l.onDataAvailable");
        }

        @Override
        public void onAllDataRead() throws IOException {
            _history.add("l.onAllDataRead");
        }
    };

    private HttpInput _in;

    public class TContent extends HttpInput.Content {
        private final String _content;

        public TContent(String content) {
            super(BufferUtil.toBuffer(content));
            _content = content;
        }

        @Override
        public void succeeded() {
            _history.add(("Content succeeded " + (_content)));
            super.succeeded();
        }

        @Override
        public void failed(Throwable x) {
            _history.add(("Content failed " + (_content)));
            super.failed(x);
        }
    }

    @Test
    public void testEmpty() throws Exception {
        MatcherAssert.assertThat(_in.available(), Matchers.equalTo(0));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 0"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
    }

    @Test
    public void testRead() throws Exception {
        _in.addContent(new HttpInputTest.TContent("AB"));
        _in.addContent(new HttpInputTest.TContent("CD"));
        _fillAndParseSimulate.offer("EF");
        _fillAndParseSimulate.offer("GH");
        MatcherAssert.assertThat(_in.available(), Matchers.equalTo(2));
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_in.getContentConsumed(), Matchers.equalTo(0L));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('A'))));
        MatcherAssert.assertThat(_in.getContentConsumed(), Matchers.equalTo(1L));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('B'))));
        MatcherAssert.assertThat(_in.getContentConsumed(), Matchers.equalTo(2L));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded AB"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('C'))));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('D'))));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded CD"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('E'))));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('F'))));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 2"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded EF"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('G'))));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('H'))));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded GH"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.getContentConsumed(), Matchers.equalTo(8L));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
    }

    @Test
    public void testBlockingRead() throws Exception {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                _in.addContent(new HttpInputTest.TContent("AB"));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }).start();
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('A'))));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 0"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("blockForContent"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('B'))));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded AB"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
    }

    @Test
    public void testReadEOF() throws Exception {
        _in.addContent(new HttpInputTest.TContent("AB"));
        _in.addContent(new HttpInputTest.TContent("CD"));
        _in.eof();
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_in.available(), Matchers.equalTo(2));
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('A'))));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('B'))));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded AB"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('C'))));
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('D'))));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded CD"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo((-1)));
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
    }

    @Test
    public void testReadEarlyEOF() throws Exception {
        _in.addContent(new HttpInputTest.TContent("AB"));
        _in.addContent(new HttpInputTest.TContent("CD"));
        _in.earlyEOF();
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_in.available(), Matchers.equalTo(2));
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('A'))));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('B'))));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('C'))));
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('D'))));
        Assertions.assertThrows(EOFException.class, () -> _in.read());
        Assertions.assertTrue(_in.isFinished());
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded AB"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded CD"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
    }

    @Test
    public void testBlockingEOF() throws Exception {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                _in.eof();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }).start();
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo((-1)));
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 0"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("blockForContent"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
    }

    @Test
    public void testAsyncEmpty() throws Exception {
        _in.setReadListener(_listener);
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 0"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("s.onReadUnready"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
    }

    @Test
    public void testAsyncRead() throws Exception {
        _in.setReadListener(_listener);
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 0"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("s.onReadUnready"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        _in.addContent(new HttpInputTest.TContent("AB"));
        _fillAndParseSimulate.add("CD");
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("s.onDataAvailable"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        _in.run();
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("l.onDataAvailable"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('A'))));
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('B'))));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded AB"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 1"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("s.onDataAvailable"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('C'))));
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('D'))));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded CD"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 0"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("s.onReadUnready"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
    }

    @Test
    public void testAsyncEOF() throws Exception {
        _in.setReadListener(_listener);
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 0"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("s.onReadUnready"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        _in.eof();
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("s.onDataAvailable"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo((-1)));
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
    }

    @Test
    public void testAsyncReadEOF() throws Exception {
        _in.setReadListener(_listener);
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 0"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("s.onReadUnready"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        _in.addContent(new HttpInputTest.TContent("AB"));
        _fillAndParseSimulate.add("_EOF_");
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("s.onDataAvailable"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        _in.run();
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("l.onDataAvailable"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('A'))));
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('B'))));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded AB"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 1"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("s.onDataAvailable"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo((-1)));
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
    }

    @Test
    public void testAsyncError() throws Exception {
        _in.setReadListener(_listener);
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 0"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("s.onReadUnready"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(false));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        _in.failed(new TimeoutException());
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("s.onDataAvailable"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        _in.run();
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("l.onError:java.util.concurrent.TimeoutException"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
        MatcherAssert.assertThat(_in.isReady(), Matchers.equalTo(true));
        IOException e = Assertions.assertThrows(IOException.class, () -> _in.read());
        MatcherAssert.assertThat(e.getCause(), Matchers.instanceOf(TimeoutException.class));
        MatcherAssert.assertThat(_in.isFinished(), Matchers.equalTo(true));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
    }

    @Test
    public void testRecycle() throws Exception {
        testAsyncRead();
        _in.recycle();
        testAsyncRead();
        _in.recycle();
        testReadEOF();
    }

    @Test
    public void testConsumeAll() throws Exception {
        _in.addContent(new HttpInputTest.TContent("AB"));
        _in.addContent(new HttpInputTest.TContent("CD"));
        _fillAndParseSimulate.offer("EF");
        _fillAndParseSimulate.offer("GH");
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('A'))));
        Assertions.assertFalse(_in.consumeAll());
        MatcherAssert.assertThat(_in.getContentConsumed(), Matchers.equalTo(8L));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded AB"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded CD"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 2"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded EF"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded GH"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 0"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
    }

    @Test
    public void testConsumeAllEOF() throws Exception {
        _in.addContent(new HttpInputTest.TContent("AB"));
        _in.addContent(new HttpInputTest.TContent("CD"));
        _fillAndParseSimulate.offer("EF");
        _fillAndParseSimulate.offer("GH");
        _fillAndParseSimulate.offer("_EOF_");
        MatcherAssert.assertThat(_in.read(), Matchers.equalTo(((int) ('A'))));
        Assertions.assertTrue(_in.consumeAll());
        MatcherAssert.assertThat(_in.getContentConsumed(), Matchers.equalTo(8L));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded AB"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded CD"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("produceContent 3"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded EF"));
        MatcherAssert.assertThat(_history.poll(), Matchers.equalTo("Content succeeded GH"));
        MatcherAssert.assertThat(_history.poll(), Matchers.nullValue());
    }
}

