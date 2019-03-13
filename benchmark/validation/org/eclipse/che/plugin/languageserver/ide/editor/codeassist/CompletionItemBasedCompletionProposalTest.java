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
package org.eclipse.che.plugin.languageserver.ide.editor.codeassist;


import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.api.languageserver.shared.model.ExtendedCompletionItem;
import org.eclipse.che.ide.api.editor.codeassist.Completion;
import org.eclipse.che.ide.api.editor.document.Document;
import org.eclipse.che.ide.api.editor.link.HasLinkedMode;
import org.eclipse.che.ide.api.editor.text.LinearRange;
import org.eclipse.che.ide.api.editor.text.TextPosition;
import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.plugin.languageserver.ide.LanguageServerResources;
import org.eclipse.che.plugin.languageserver.ide.service.TextDocumentServiceClient;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextEdit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 */
@RunWith(GwtMockitoTestRunner.class)
public class CompletionItemBasedCompletionProposalTest {
    @Mock
    private HasLinkedMode editor;

    @Mock
    private TextDocumentServiceClient documentServiceClient;

    @Mock
    private LanguageServerResources resources;

    @Mock
    private Icon icon;

    @Mock
    private ServerCapabilities serverCapabilities;

    @Mock
    private ExtendedCompletionItem completionItem;

    @Mock
    private CompletionOptions completionOptions;

    @Mock
    private Document document;

    @Mock
    private CompletionItem completion;

    private CompletionItemBasedCompletionProposal proposal;

    @Test
    public void shouldReturnNotNullCompletion() throws Exception {
        Mockito.when(serverCapabilities.getCompletionProvider()).thenReturn(completionOptions);
        Mockito.when(completionOptions.getResolveProvider()).thenReturn(false);
        Completion[] completions = new Completion[1];
        proposal.getCompletion(( completion) -> completions[0] = completion);
        Assert.assertNotNull(completions[0]);
    }

    @Test
    public void shouldUseInsertText() throws Exception {
        Mockito.when(serverCapabilities.getCompletionProvider()).thenReturn(completionOptions);
        Mockito.when(completionOptions.getResolveProvider()).thenReturn(false);
        Mockito.when(document.getCursorPosition()).thenReturn(new TextPosition(0, 5));
        Mockito.when(completion.getInsertText()).thenReturn("foo");
        Completion[] completions = new Completion[1];
        proposal.getCompletion(( completion) -> completions[0] = completion);
        completions[0].apply(document);
        Mockito.verify(document).getCursorPosition();
        Mockito.verify(document, Mockito.times(1)).replace(ArgumentMatchers.eq(0), ArgumentMatchers.eq(5), ArgumentMatchers.eq(0), ArgumentMatchers.eq(5), ArgumentMatchers.eq("foo"));
        Mockito.verify(document, Mockito.times(1)).replace(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldUseLabelIfInsertTextIsNull() throws Exception {
        Mockito.when(serverCapabilities.getCompletionProvider()).thenReturn(completionOptions);
        Mockito.when(completionOptions.getResolveProvider()).thenReturn(false);
        Mockito.when(document.getCursorPosition()).thenReturn(new TextPosition(0, 5));
        Mockito.when(completion.getInsertText()).thenReturn(null);
        Mockito.when(completion.getLabel()).thenReturn("bar");
        Completion[] completions = new Completion[1];
        proposal.getCompletion(( completion) -> completions[0] = completion);
        completions[0].apply(document);
        Mockito.verify(document).getCursorPosition();
        Mockito.verify(document, Mockito.times(1)).replace(ArgumentMatchers.eq(0), ArgumentMatchers.eq(5), ArgumentMatchers.eq(0), ArgumentMatchers.eq(5), ArgumentMatchers.eq("bar"));
        Mockito.verify(document, Mockito.times(1)).replace(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldUseTextEditFirst() throws Exception {
        TextEdit textEdit = Mockito.mock(TextEdit.class);
        Range range = Mockito.mock(Range.class);
        Position startPosition = Mockito.mock(Position.class);
        Position endPosition = Mockito.mock(Position.class);
        Mockito.when(serverCapabilities.getCompletionProvider()).thenReturn(completionOptions);
        Mockito.when(completionOptions.getResolveProvider()).thenReturn(false);
        Mockito.when(document.getCursorPosition()).thenReturn(new TextPosition(0, 5));
        Mockito.when(completion.getInsertText()).thenReturn("foo");
        Mockito.when(completion.getLabel()).thenReturn("bar");
        Mockito.when(completion.getTextEdit()).thenReturn(textEdit);
        Mockito.when(textEdit.getRange()).thenReturn(range);
        Mockito.when(textEdit.getNewText()).thenReturn("fooBar");
        Mockito.when(range.getStart()).thenReturn(startPosition);
        Mockito.when(range.getEnd()).thenReturn(endPosition);
        Mockito.when(startPosition.getLine()).thenReturn(1);
        Mockito.when(startPosition.getCharacter()).thenReturn(5);
        Mockito.when(endPosition.getLine()).thenReturn(1);
        Mockito.when(endPosition.getCharacter()).thenReturn(5);
        Completion[] completions = new Completion[1];
        proposal.getCompletion(( completion) -> completions[0] = completion);
        completions[0].apply(document);
        Mockito.verify(document, Mockito.times(1)).replace(ArgumentMatchers.eq(1), ArgumentMatchers.eq(5), ArgumentMatchers.eq(1), ArgumentMatchers.eq(5), ArgumentMatchers.eq("fooBar"));
        Mockito.verify(document, Mockito.times(1)).replace(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldPlaceCursorInRightPositionWithTextEdit() throws Exception {
        TextEdit textEdit = Mockito.mock(TextEdit.class);
        Range range = Mockito.mock(Range.class);
        Position startPosition = Mockito.mock(Position.class);
        Position endPosition = Mockito.mock(Position.class);
        Mockito.when(serverCapabilities.getCompletionProvider()).thenReturn(completionOptions);
        Mockito.when(completionOptions.getResolveProvider()).thenReturn(false);
        Mockito.when(document.getCursorPosition()).thenReturn(new TextPosition(0, 5));
        Mockito.when(completion.getInsertText()).thenReturn("foo");
        Mockito.when(completion.getLabel()).thenReturn("bar");
        Mockito.when(completion.getTextEdit()).thenReturn(textEdit);
        Mockito.when(textEdit.getRange()).thenReturn(range);
        Mockito.when(textEdit.getNewText()).thenReturn("fooBar");
        Mockito.when(range.getStart()).thenReturn(startPosition);
        Mockito.when(range.getEnd()).thenReturn(endPosition);
        Mockito.when(startPosition.getLine()).thenReturn(1);
        Mockito.when(startPosition.getCharacter()).thenReturn(5);
        Mockito.when(endPosition.getLine()).thenReturn(1);
        Mockito.when(endPosition.getCharacter()).thenReturn(5);
        Mockito.when(document.getIndexFromPosition(ArgumentMatchers.any())).thenReturn(5);
        Completion[] completions = new Completion[1];
        proposal.getCompletion(( completion) -> completions[0] = completion);
        completions[0].apply(document);
        LinearRange selection = completions[0].getSelection(document);
        Assert.assertEquals(11, selection.getStartOffset());
        Assert.assertEquals(0, selection.getLength());
    }

    @Test
    public void shouldPlaceCursorInRightPositionWithInsertedText() throws Exception {
        Mockito.when(serverCapabilities.getCompletionProvider()).thenReturn(completionOptions);
        Mockito.when(completionOptions.getResolveProvider()).thenReturn(false);
        Mockito.when(document.getCursorPosition()).thenReturn(new TextPosition(0, 5));
        Mockito.when(completion.getInsertText()).thenReturn("foo");
        Mockito.when(document.getIndexFromPosition(ArgumentMatchers.any())).thenReturn(5);
        Completion[] completions = new Completion[1];
        proposal.getCompletion(( completion) -> completions[0] = completion);
        completions[0].apply(document);
        LinearRange selection = completions[0].getSelection(document);
        Assert.assertEquals(8, selection.getStartOffset());
        Assert.assertEquals(0, selection.getLength());
    }
}

