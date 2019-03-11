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
package org.eclipse.che.ide.editor.orion.client;


import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.api.editor.document.Document;
import org.eclipse.che.ide.api.editor.text.Position;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 */
@RunWith(GwtMockitoTestRunner.class)
public class WordDetectionUtilTest {
    @Mock
    private Document document;

    private WordDetectionUtil detectionUtil;

    @Test
    public void testGetWordAtOffset() throws Exception {
        Mockito.when(document.getLineAtOffset(5)).thenReturn(1);
        Mockito.when(document.getLineContent(1)).thenReturn("foo bar foo   ");
        Mockito.when(document.getLineStart(1)).thenReturn(0);
        Position wordAtOffset = detectionUtil.getWordAtOffset(document, 5);
        Assert.assertNotNull(wordAtOffset);
        Assert.assertEquals(4, wordAtOffset.offset);
        Assert.assertEquals(3, wordAtOffset.length);
    }

    @Test
    public void testGetWordAtOffsetMultiLine() throws Exception {
        Mockito.when(document.getLineAtOffset(16)).thenReturn(2);
        Mockito.when(document.getLineContent(2)).thenReturn("foo bar !fooBar'  ");
        Mockito.when(document.getLineStart(2)).thenReturn(5);
        Position wordAtOffset = detectionUtil.getWordAtOffset(document, 16);
        Assert.assertNotNull(wordAtOffset);
        Assert.assertEquals(14, wordAtOffset.offset);
        Assert.assertEquals(6, wordAtOffset.length);
    }
}

