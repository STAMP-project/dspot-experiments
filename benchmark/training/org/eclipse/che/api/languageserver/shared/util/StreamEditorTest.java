/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.api.languageserver.shared.util;


import org.junit.Test;


public class StreamEditorTest {
    @Test
    public void testInsertEmpty() {
        runEdit("", "foobar", new org.eclipse.lsp4j.TextEdit(newRange(0, 0, 0, 0), "foobar"));
    }

    @Test
    public void testInsertTwo() {
        runEdit("abcdef", "afoobarbcbladef", new org.eclipse.lsp4j.TextEdit(newRange(0, 1, 0, 1), "foobar"), new org.eclipse.lsp4j.TextEdit(newRange(0, 3, 0, 3), "bla"));
    }

    @Test
    public void testInsertNewLine() {
        runEdit("abc\r\ndef", "afoobarbc\r\ndblaef", new org.eclipse.lsp4j.TextEdit(newRange(0, 1, 0, 1), "foobar"), new org.eclipse.lsp4j.TextEdit(newRange(1, 1, 1, 1), "bla"));
        runEdit("abc\ndef", "afoobarbc\ndblaef", new org.eclipse.lsp4j.TextEdit(newRange(0, 1, 0, 1), "foobar"), new org.eclipse.lsp4j.TextEdit(newRange(1, 1, 1, 1), "bla"));
    }

    @Test
    public void testInsertRemoveNewLine() {
        runEdit("abc\r\ndef", "ab\nlaefoobarf", new org.eclipse.lsp4j.TextEdit(newRange(0, 1, 1, 1), "b\nla"), new org.eclipse.lsp4j.TextEdit(newRange(1, 2, 1, 2), "foobar"));
    }

    @Test
    public void testDeleteAll() {
        runEdit("foo\nbar", "", new org.eclipse.lsp4j.TextEdit(newRange(0, 0, 1, 3), ""));
    }

    @Test
    public void testDeleteEnd() {
        runEdit("abc\r\ndef", "abc\r\ndxyz", new org.eclipse.lsp4j.TextEdit(newRange(1, 1, 1, 3), ""), new org.eclipse.lsp4j.TextEdit(newRange(1, 1, 1, 1), "xyz"));
    }
}

