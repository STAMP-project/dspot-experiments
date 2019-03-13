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
package org.eclipse.che.ide.ext.java.client.navigation.openimplementation;


import Resource.FILE;
import SymbolKind.Class;
import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.Collections;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.editor.position.PositionConverter;
import org.eclipse.che.ide.api.editor.position.PositionConverter.PixelCoordinates;
import org.eclipse.che.ide.api.editor.text.TextPosition;
import org.eclipse.che.ide.api.editor.texteditor.TextEditor;
import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.api.resources.File;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.JavaResources;
import org.eclipse.che.ide.ext.java.client.service.JavaLanguageExtensionServiceClient;
import org.eclipse.che.ide.ext.java.dto.DtoClientImpls.ImplementersResponseDto;
import org.eclipse.che.ide.resource.Path;
import org.eclipse.che.ide.ui.popup.PopupResources;
import org.eclipse.che.ide.ui.popup.PopupResources.PopupStyle;
import org.eclipse.che.jdt.ls.extension.api.dto.ImplementersResponse;
import org.eclipse.che.plugin.languageserver.ide.util.OpenFileInEditorHelper;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolInformation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class OpenImplementationPresenterTest {
    // constructor mocks
    @Mock
    private JavaLanguageExtensionServiceClient javaLanguageExtensionServiceClient;

    @Mock
    private OpenFileInEditorHelper openHelper;

    @Mock
    private JavaResources javaResources;

    @Mock
    private PopupStyle popupStyle;

    @Mock
    private PopupResources popupResources;

    @Mock
    private JavaLocalizationConstant locale;

    // other mocks
    @Mock
    private TextEditor editor;

    @Mock
    private EditorInput editorInput;

    @Mock
    private File file;

    @Mock
    private Project relatedProject;

    @Mock
    private Container srcFolder;

    @Mock
    private ImplementersResponseDto implementationDescriptor;

    @Mock
    private SymbolInformation type;

    @Mock
    private PositionConverter positionConverter;

    @Mock
    private Promise<ImplementersResponse> implementersPromise;

    @Captor
    ArgumentCaptor<Operation<ImplementersResponse>> implementationsOperation;

    private OpenImplementationPresenter presenter;

    @Test
    public void testShouldDisplayOneImplementationIsRealFile() throws Exception {
        Mockito.when(editor.getEditorInput()).thenReturn(editorInput);
        Mockito.when(editorInput.getFile()).thenReturn(file);
        Mockito.when(file.getLocation()).thenReturn(Path.valueOf("/a/b/c/d/file.java"));
        Mockito.when(srcFolder.getLocation()).thenReturn(Path.valueOf("/a/b"));
        Mockito.when(file.getResourceType()).thenReturn(FILE);
        Mockito.when(file.getExtension()).thenReturn("java");
        Mockito.when(file.getName()).thenReturn("file.java");
        Mockito.when(relatedProject.getLocation()).thenReturn(Path.valueOf("/a"));
        Mockito.when(editor.getCursorPosition()).thenReturn(new TextPosition(1, 1));
        Mockito.when(editor.getCursorOffset()).thenReturn(123);
        Mockito.when(editor.getPositionConverter()).thenReturn(positionConverter);
        Mockito.when(implementersPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(implementersPromise);
        Mockito.when(javaLanguageExtensionServiceClient.findImplementations(ArgumentMatchers.any())).thenReturn(implementersPromise);
        Mockito.when(implementationDescriptor.getImplementers()).thenReturn(Collections.singletonList(type));
        Mockito.when(implementationDescriptor.getSearchedElement()).thenReturn("memberName");
        Mockito.when(locale.openImplementationWindowTitle(ArgumentMatchers.eq("memberName"), ArgumentMatchers.eq(1))).thenReturn("foo");
        Mockito.when(type.getKind()).thenReturn(Class);
        Mockito.when(type.getLocation()).thenReturn(new Location("/memberPath", Mockito.mock(Range.class)));
        presenter.show(editor);
        Mockito.verify(implementersPromise).then(implementationsOperation.capture());
        implementationsOperation.getValue().apply(implementationDescriptor);
        Mockito.verify(openHelper).openLocation(ArgumentMatchers.eq(type.getLocation()));
    }

    @Test
    public void testShouldDisplayNoImplementations() throws Exception {
        Mockito.when(editor.getEditorInput()).thenReturn(editorInput);
        Mockito.when(editorInput.getFile()).thenReturn(file);
        Mockito.when(file.getLocation()).thenReturn(Path.valueOf("/a/b/c/d/file.java"));
        Mockito.when(srcFolder.getLocation()).thenReturn(Path.valueOf("/a/b"));
        Mockito.when(file.getResourceType()).thenReturn(FILE);
        Mockito.when(file.getExtension()).thenReturn("java");
        Mockito.when(file.getName()).thenReturn("file.java");
        Mockito.when(relatedProject.getLocation()).thenReturn(Path.valueOf("/a"));
        Mockito.when(editor.getCursorPosition()).thenReturn(new TextPosition(1, 1));
        Mockito.when(editor.getCursorOffset()).thenReturn(123);
        Mockito.when(editor.getPositionConverter()).thenReturn(positionConverter);
        Mockito.when(positionConverter.offsetToPixel(ArgumentMatchers.eq(123))).thenReturn(new PixelCoordinates(1, 1));
        Mockito.when(implementersPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(implementersPromise);
        Mockito.when(javaLanguageExtensionServiceClient.findImplementations(ArgumentMatchers.any())).thenReturn(implementersPromise);
        Mockito.when(implementationDescriptor.getImplementers()).thenReturn(Collections.emptyList());
        Mockito.when(implementationDescriptor.getSearchedElement()).thenReturn("memberName");
        Mockito.when(locale.openImplementationWindowTitle(ArgumentMatchers.eq("memberName"), ArgumentMatchers.eq(1))).thenReturn("foo");
        presenter.show(editor);
        Mockito.verify(implementersPromise).then(implementationsOperation.capture());
        implementationsOperation.getValue().apply(implementationDescriptor);
        Mockito.verify(locale).noImplementations();
    }
}

