/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.net;


import ch.qos.logback.classic.net.testObjectBuilders.Builder;
import ch.qos.logback.classic.net.testObjectBuilders.MinimalSer;
import ch.qos.logback.classic.net.testObjectBuilders.MinimalSerBuilder;
import ch.qos.logback.classic.net.testObjectBuilders.TrivialLoggingEventVOBuilder;
import ch.qos.logback.classic.spi.LoggingEventVO;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;


public class SerializationPerfTest extends TestCase {
    ObjectOutputStream oos;

    int loopNumber = 10000;

    int resetFrequency = 100;

    int pauseFrequency = 10;

    long pauseLengthInMillis = 20;

    /**
     * <p>
     * Run the test with a MockSocketServer or with a NOPOutputStream
     *
     * <p>
     * Run with external mock can be done using the ExternalMockSocketServer. It
     * needs to be launched from a separate JVM. The ExternalMockSocketServer does
     * not consume the events but passes through the available bytes that it is
     * receiving.
     *
     * <p>
     * For example, with 4 test methods, you can launch the
     * ExternalMockSocketServer this way:
     * </p>
     * <p>
     * <code>java ch.qos.logback.classic.net.ExternalMockSocketServer 4</code>
     * </p>
     */
    boolean runWithExternalMockServer = true;

    // public void testWithMinimalExternalization() throws Exception {
    // Builder builder = new MinimalExtBuilder();
    // runPerfTest(builder, "Minimal object externalization");
    // }
    public void testWithMinimalSerialization() throws Exception {
        Builder<MinimalSer> builder = new MinimalSerBuilder();
        runPerfTest(builder, "Minimal object serialization");
    }

    // public void testWithExternalization() throws Exception {
    // Builder builder = new LoggingEventExtBuilder();
    // runPerfTest(builder, "LoggingEvent object externalization");
    // }
    public void testWithSerialization() throws Exception {
        Builder<LoggingEventVO> builder = new TrivialLoggingEventVOBuilder();
        runPerfTest(builder, "LoggingEventVO object serialization");
    }
}

