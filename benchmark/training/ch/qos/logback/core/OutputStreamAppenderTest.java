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
package ch.qos.logback.core;


import org.junit.Test;


public class OutputStreamAppenderTest {
    Context context = new ContextBase();

    @Test
    public void smoke() {
        String FILE_HEADER = "FILE_HEADER ";
        String PRESENTATION_HEADER = "PRESENTATION_HEADER";
        String PRESENTATION_FOOTER = "PRESENTATION_FOOTER ";
        String FILE_FOOTER = "FILE_FOOTER";
        headerFooterCheck(FILE_HEADER, PRESENTATION_HEADER, PRESENTATION_FOOTER, FILE_FOOTER);
    }

    @Test
    public void nullFileHeader() {
        String FILE_HEADER = null;
        String PRESENTATION_HEADER = "PRESENTATION_HEADER";
        String PRESENTATION_FOOTER = "PRESENTATION_FOOTER ";
        String FILE_FOOTER = "FILE_FOOTER";
        headerFooterCheck(FILE_HEADER, PRESENTATION_HEADER, PRESENTATION_FOOTER, FILE_FOOTER);
    }

    @Test
    public void nullPresentationHeader() {
        String FILE_HEADER = "FILE_HEADER ";
        String PRESENTATION_HEADER = null;
        String PRESENTATION_FOOTER = "PRESENTATION_FOOTER ";
        String FILE_FOOTER = "FILE_FOOTER";
        headerFooterCheck(FILE_HEADER, PRESENTATION_HEADER, PRESENTATION_FOOTER, FILE_FOOTER);
    }

    @Test
    public void nullPresentationFooter() {
        String FILE_HEADER = "FILE_HEADER ";
        String PRESENTATION_HEADER = "PRESENTATION_HEADER";
        String PRESENTATION_FOOTER = null;
        String FILE_FOOTER = "FILE_FOOTER";
        headerFooterCheck(FILE_HEADER, PRESENTATION_HEADER, PRESENTATION_FOOTER, FILE_FOOTER);
    }

    @Test
    public void nullFileFooter() {
        String FILE_HEADER = "FILE_HEADER ";
        String PRESENTATION_HEADER = "PRESENTATION_HEADER";
        String PRESENTATION_FOOTER = "PRESENTATION_FOOTER ";
        String FILE_FOOTER = null;
        headerFooterCheck(FILE_HEADER, PRESENTATION_HEADER, PRESENTATION_FOOTER, FILE_FOOTER);
    }
}

