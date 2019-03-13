/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.acyclicvisitor;


import org.junit.jupiter.api.Test;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;


/**
 * ConfigureForDosVisitor test class
 */
public class ConfigureForDosVisitorTest {
    TestLogger logger = TestLoggerFactory.getTestLogger(ConfigureForDosVisitor.class);

    @Test
    public void testVisitForZoom() {
        ConfigureForDosVisitor conDos = new ConfigureForDosVisitor();
        Zoom zoom = new Zoom();
        conDos.visit(zoom);
        assertThat(logger.getLoggingEvents()).extracting("level", "message").contains(tuple(Level.INFO, (zoom + " used with Dos configurator.")));
    }

    @Test
    public void testVisitForHayes() {
        ConfigureForDosVisitor conDos = new ConfigureForDosVisitor();
        Hayes hayes = new Hayes();
        conDos.visit(hayes);
        assertThat(logger.getLoggingEvents()).extracting("level", "message").contains(tuple(Level.INFO, (hayes + " used with Dos configurator.")));
    }
}

