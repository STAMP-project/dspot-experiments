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
package org.eclipse.che.ide.api.editor.changeintercept.changeintercept;


import org.eclipse.che.ide.api.editor.changeintercept.CloseCStyleCommentChangeInterceptor;
import org.eclipse.che.ide.api.editor.changeintercept.TextChange;
import org.eclipse.che.ide.api.editor.document.ReadOnlyDocument;
import org.eclipse.che.ide.api.editor.text.TextPosition;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test of the c-style bloc comment close interceptor.
 */
@RunWith(MockitoJUnitRunner.class)
public class CloseCStyleCommentChangeInterceptorTest {
    @Mock
    private ReadOnlyDocument document;

    @InjectMocks
    private CloseCStyleCommentChangeInterceptor interceptor;

    @Test
    public void testStartNotEmptyLine() {
        Mockito.doReturn("s/*").when(document).getLineContent(1);
        final TextChange input = new TextChange.Builder().from(new TextPosition(1, 3)).to(new TextPosition(2, 2)).insert("\n *").build();
        final TextChange output = interceptor.processChange(input, document);
        Assert.assertNull(output);
    }

    @Test
    public void testPasteWholeCommentStart() {
        final TextChange input = new TextChange.Builder().from(new TextPosition(0, 0)).to(new TextPosition(1, 2)).insert("/**\n *").build();
        final TextChange output = interceptor.processChange(input, document);
        Assert.assertNull(output);
    }

    @Test
    public void testCloseComment() {
        final TextChange input = new TextChange.Builder().from(new TextPosition(0, 0)).to(new TextPosition(1, 2)).insert("/**\n *").build();
        final TextChange output = interceptor.processChange(input, document);
        Assert.assertNull(output);
    }
}

