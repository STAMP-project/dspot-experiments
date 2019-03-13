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
package org.eclipse.jetty.websocket.common.events;


import examples.AnnotatedBinaryArraySocket;
import examples.AnnotatedBinaryStreamSocket;
import examples.AnnotatedTextSocket;
import examples.AnnotatedTextStreamSocket;
import org.eclipse.jetty.websocket.api.InvalidWebSocketException;
import org.eclipse.jetty.websocket.common.annotations.BadBinarySignatureSocket;
import org.eclipse.jetty.websocket.common.annotations.BadDuplicateBinarySocket;
import org.eclipse.jetty.websocket.common.annotations.BadDuplicateFrameSocket;
import org.eclipse.jetty.websocket.common.annotations.BadTextSignatureSocket;
import org.eclipse.jetty.websocket.common.annotations.FrameSocket;
import org.eclipse.jetty.websocket.common.annotations.MyEchoBinarySocket;
import org.eclipse.jetty.websocket.common.annotations.MyEchoSocket;
import org.eclipse.jetty.websocket.common.annotations.MyStatelessEchoSocket;
import org.eclipse.jetty.websocket.common.annotations.NoopSocket;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class JettyAnnotatedScannerTest {
    /**
     * Test Case for bad declaration (duplicate OnWebSocketBinary declarations)
     */
    @Test
    public void testAnnotatedBadDuplicateBinarySocket() {
        JettyAnnotatedScanner impl = new JettyAnnotatedScanner();
        InvalidWebSocketException e = Assertions.assertThrows(InvalidWebSocketException.class, () -> {
            // Should toss exception
            impl.scan(BadDuplicateBinarySocket.class);
        });
        // Validate that we have clear error message to the developer
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Duplicate @OnWebSocketMessage declaration"));
    }

    /**
     * Test Case for bad declaration (duplicate frame type methods)
     */
    @Test
    public void testAnnotatedBadDuplicateFrameSocket() {
        JettyAnnotatedScanner impl = new JettyAnnotatedScanner();
        InvalidWebSocketException e = Assertions.assertThrows(InvalidWebSocketException.class, () -> {
            // Should toss exception
            impl.scan(BadDuplicateFrameSocket.class);
        });
        // Validate that we have clear error message to the developer
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Duplicate @OnWebSocketFrame"));
    }

    /**
     * Test Case for bad declaration a method with a non-void return type
     */
    @Test
    public void testAnnotatedBadSignature_NonVoidReturn() {
        JettyAnnotatedScanner impl = new JettyAnnotatedScanner();
        InvalidWebSocketException e = Assertions.assertThrows(InvalidWebSocketException.class, () -> {
            // Should toss exception
            impl.scan(BadBinarySignatureSocket.class);
        });
        // Validate that we have clear error message to the developer
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("must be void"));
    }

    /**
     * Test Case for bad declaration a method with a public static method
     */
    @Test
    public void testAnnotatedBadSignature_Static() {
        JettyAnnotatedScanner impl = new JettyAnnotatedScanner();
        InvalidWebSocketException e = Assertions.assertThrows(InvalidWebSocketException.class, () -> {
            // Should toss exception
            impl.scan(BadTextSignatureSocket.class);
        });
        // Validate that we have clear error message to the developer
        MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("may not be static"));
    }

    /**
     * Test Case for socket for binary array messages
     */
    @Test
    public void testAnnotatedBinaryArraySocket() {
        JettyAnnotatedScanner impl = new JettyAnnotatedScanner();
        JettyAnnotatedMetadata metadata = impl.scan(AnnotatedBinaryArraySocket.class);
        String classId = AnnotatedBinaryArraySocket.class.getSimpleName();
        MatcherAssert.assertThat(("EventMethods for " + classId), metadata, Matchers.notNullValue());
        assertHasEventMethod((classId + ".onBinary"), metadata.onBinary);
        assertHasEventMethod((classId + ".onClose"), metadata.onClose);
        assertHasEventMethod((classId + ".onConnect"), metadata.onConnect);
        assertNoEventMethod((classId + ".onException"), metadata.onError);
        assertNoEventMethod((classId + ".onText"), metadata.onText);
        assertNoEventMethod((classId + ".onFrame"), metadata.onFrame);
        Assertions.assertFalse(metadata.onBinary.isSessionAware(), (classId + ".onBinary.isSessionAware"));
        Assertions.assertFalse(metadata.onBinary.isStreaming(), (classId + ".onBinary.isStreaming"));
    }

    /**
     * Test Case for socket for binary stream messages
     */
    @Test
    public void testAnnotatedBinaryStreamSocket() {
        JettyAnnotatedScanner impl = new JettyAnnotatedScanner();
        JettyAnnotatedMetadata metadata = impl.scan(AnnotatedBinaryStreamSocket.class);
        String classId = AnnotatedBinaryStreamSocket.class.getSimpleName();
        MatcherAssert.assertThat(("EventMethods for " + classId), metadata, Matchers.notNullValue());
        assertHasEventMethod((classId + ".onBinary"), metadata.onBinary);
        assertHasEventMethod((classId + ".onClose"), metadata.onClose);
        assertHasEventMethod((classId + ".onConnect"), metadata.onConnect);
        assertNoEventMethod((classId + ".onException"), metadata.onError);
        assertNoEventMethod((classId + ".onText"), metadata.onText);
        assertNoEventMethod((classId + ".onFrame"), metadata.onFrame);
        Assertions.assertFalse(metadata.onBinary.isSessionAware(), (classId + ".onBinary.isSessionAware"));
        Assertions.assertTrue(metadata.onBinary.isStreaming(), (classId + ".onBinary.isStreaming"));
    }

    /**
     * Test Case for no exceptions and 4 methods (3 methods from parent)
     */
    @Test
    public void testAnnotatedMyEchoBinarySocket() {
        JettyAnnotatedScanner impl = new JettyAnnotatedScanner();
        JettyAnnotatedMetadata metadata = impl.scan(MyEchoBinarySocket.class);
        String classId = MyEchoBinarySocket.class.getSimpleName();
        MatcherAssert.assertThat(("EventMethods for " + classId), metadata, Matchers.notNullValue());
        assertHasEventMethod((classId + ".onBinary"), metadata.onBinary);
        assertHasEventMethod((classId + ".onClose"), metadata.onClose);
        assertHasEventMethod((classId + ".onConnect"), metadata.onConnect);
        assertNoEventMethod((classId + ".onException"), metadata.onError);
        assertHasEventMethod((classId + ".onText"), metadata.onText);
        assertNoEventMethod((classId + ".onFrame"), metadata.onFrame);
    }

    /**
     * Test Case for no exceptions and 3 methods
     */
    @Test
    public void testAnnotatedMyEchoSocket() {
        JettyAnnotatedScanner impl = new JettyAnnotatedScanner();
        JettyAnnotatedMetadata metadata = impl.scan(MyEchoSocket.class);
        String classId = MyEchoSocket.class.getSimpleName();
        MatcherAssert.assertThat(("EventMethods for " + classId), metadata, Matchers.notNullValue());
        assertNoEventMethod((classId + ".onBinary"), metadata.onBinary);
        assertHasEventMethod((classId + ".onClose"), metadata.onClose);
        assertHasEventMethod((classId + ".onConnect"), metadata.onConnect);
        assertNoEventMethod((classId + ".onException"), metadata.onError);
        assertHasEventMethod((classId + ".onText"), metadata.onText);
        assertNoEventMethod((classId + ".onFrame"), metadata.onFrame);
    }

    /**
     * Test Case for annotated for text messages w/connection param
     */
    @Test
    public void testAnnotatedMyStatelessEchoSocket() {
        JettyAnnotatedScanner impl = new JettyAnnotatedScanner();
        JettyAnnotatedMetadata metadata = impl.scan(MyStatelessEchoSocket.class);
        String classId = MyStatelessEchoSocket.class.getSimpleName();
        MatcherAssert.assertThat(("EventMethods for " + classId), metadata, Matchers.notNullValue());
        assertNoEventMethod((classId + ".onBinary"), metadata.onBinary);
        assertNoEventMethod((classId + ".onClose"), metadata.onClose);
        assertNoEventMethod((classId + ".onConnect"), metadata.onConnect);
        assertNoEventMethod((classId + ".onException"), metadata.onError);
        assertHasEventMethod((classId + ".onText"), metadata.onText);
        assertNoEventMethod((classId + ".onFrame"), metadata.onFrame);
        Assertions.assertTrue(metadata.onText.isSessionAware(), (classId + ".onText.isSessionAware"));
        Assertions.assertFalse(metadata.onText.isStreaming(), (classId + ".onText.isStreaming"));
    }

    /**
     * Test Case for no exceptions and no methods
     */
    @Test
    public void testAnnotatedNoop() {
        JettyAnnotatedScanner impl = new JettyAnnotatedScanner();
        JettyAnnotatedMetadata metadata = impl.scan(NoopSocket.class);
        String classId = NoopSocket.class.getSimpleName();
        MatcherAssert.assertThat(("Methods for " + classId), metadata, Matchers.notNullValue());
        assertNoEventMethod((classId + ".onBinary"), metadata.onBinary);
        assertNoEventMethod((classId + ".onClose"), metadata.onClose);
        assertNoEventMethod((classId + ".onConnect"), metadata.onConnect);
        assertNoEventMethod((classId + ".onException"), metadata.onError);
        assertNoEventMethod((classId + ".onText"), metadata.onText);
        assertNoEventMethod((classId + ".onFrame"), metadata.onFrame);
    }

    /**
     * Test Case for no exceptions and 1 methods
     */
    @Test
    public void testAnnotatedOnFrame() {
        JettyAnnotatedScanner impl = new JettyAnnotatedScanner();
        JettyAnnotatedMetadata metadata = impl.scan(FrameSocket.class);
        String classId = FrameSocket.class.getSimpleName();
        MatcherAssert.assertThat(("EventMethods for " + classId), metadata, Matchers.notNullValue());
        assertNoEventMethod((classId + ".onBinary"), metadata.onBinary);
        assertNoEventMethod((classId + ".onClose"), metadata.onClose);
        assertNoEventMethod((classId + ".onConnect"), metadata.onConnect);
        assertNoEventMethod((classId + ".onException"), metadata.onError);
        assertNoEventMethod((classId + ".onText"), metadata.onText);
        assertHasEventMethod((classId + ".onFrame"), metadata.onFrame);
    }

    /**
     * Test Case for socket for simple text messages
     */
    @Test
    public void testAnnotatedTextSocket() {
        JettyAnnotatedScanner impl = new JettyAnnotatedScanner();
        JettyAnnotatedMetadata metadata = impl.scan(AnnotatedTextSocket.class);
        String classId = AnnotatedTextSocket.class.getSimpleName();
        MatcherAssert.assertThat(("EventMethods for " + classId), metadata, Matchers.notNullValue());
        assertNoEventMethod((classId + ".onBinary"), metadata.onBinary);
        assertHasEventMethod((classId + ".onClose"), metadata.onClose);
        assertHasEventMethod((classId + ".onConnect"), metadata.onConnect);
        assertHasEventMethod((classId + ".onException"), metadata.onError);
        assertHasEventMethod((classId + ".onText"), metadata.onText);
        assertNoEventMethod((classId + ".onFrame"), metadata.onFrame);
        Assertions.assertFalse(metadata.onText.isSessionAware(), (classId + ".onText.isSessionAware"));
        Assertions.assertFalse(metadata.onText.isStreaming(), (classId + ".onText.isStreaming"));
    }

    /**
     * Test Case for socket for text stream messages
     */
    @Test
    public void testAnnotatedTextStreamSocket() {
        JettyAnnotatedScanner impl = new JettyAnnotatedScanner();
        JettyAnnotatedMetadata metadata = impl.scan(AnnotatedTextStreamSocket.class);
        String classId = AnnotatedTextStreamSocket.class.getSimpleName();
        MatcherAssert.assertThat(("EventMethods for " + classId), metadata, Matchers.notNullValue());
        assertNoEventMethod((classId + ".onBinary"), metadata.onBinary);
        assertHasEventMethod((classId + ".onClose"), metadata.onClose);
        assertHasEventMethod((classId + ".onConnect"), metadata.onConnect);
        assertNoEventMethod((classId + ".onException"), metadata.onError);
        assertHasEventMethod((classId + ".onText"), metadata.onText);
        assertNoEventMethod((classId + ".onFrame"), metadata.onFrame);
        Assertions.assertFalse(metadata.onText.isSessionAware(), (classId + ".onText.isSessionAware"));
        Assertions.assertTrue(metadata.onText.isStreaming(), (classId + ".onText.isStreaming"));
    }
}

