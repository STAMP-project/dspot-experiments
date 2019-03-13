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
package org.eclipse.che.ide.api.editor;


import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.Iterator;
import org.eclipse.che.ide.api.editor.annotation.AnnotationModelImpl;
import org.eclipse.che.ide.api.editor.document.Document;
import org.eclipse.che.ide.api.editor.document.DocumentHandle;
import org.eclipse.che.ide.api.editor.partition.DocumentPositionMap;
import org.eclipse.che.ide.api.editor.text.Position;
import org.eclipse.che.ide.api.editor.text.annotation.Annotation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 */
@RunWith(GwtMockitoTestRunner.class)
public class AnnotationIteratorTest {
    @Mock
    private DocumentPositionMap documentPositionMap;

    @Mock
    private DocumentHandle documentHandle;

    @Mock
    private Document document;

    private AnnotationModelImpl annotationModel;

    @Test
    public void emptyIterator() throws Exception {
        Mockito.when(document.getContentsCharCount()).thenReturn(10);
        annotationModel.addAnnotation(new Annotation("aa", true, "aa"), new Position(0, 8));
        annotationModel.addAnnotation(new Annotation("aa", true, "aa"), new Position(2, 5));
        annotationModel.addAnnotation(new Annotation("aa", true, "aa"), new Position(5, 8));
        Iterator<Annotation> iterator = annotationModel.getAnnotationIterator(9, 1, true, true);
        assertThat(iterator).isNotNull().hasSize(0);
    }

    @Test
    public void testIterator() throws Exception {
        Mockito.when(document.getContentsCharCount()).thenReturn(20);
        annotationModel.addAnnotation(new Annotation("aa", true, "aa"), new Position(0, 10));
        annotationModel.addAnnotation(new Annotation("aa", true, "aa"), new Position(2, 5));
        annotationModel.addAnnotation(new Annotation("aa", true, "aa"), new Position(5, 10));
        Iterator<Annotation> iterator = annotationModel.getAnnotationIterator(1, 6, true, true);
        assertThat(iterator).isNotNull().hasSize(3);
    }

    @Test
    public void testCanStartBefore() throws Exception {
        Mockito.when(document.getContentsCharCount()).thenReturn(20);
        annotationModel.addAnnotation(new Annotation("aa", true, "aa"), new Position(0, 10));
        annotationModel.addAnnotation(new Annotation("aa", true, "aa"), new Position(2, 5));
        annotationModel.addAnnotation(new Annotation("aa", true, "aa"), new Position(5, 10));
        Iterator<Annotation> iterator = annotationModel.getAnnotationIterator(1, 6, false, true);
        assertThat(iterator).isNotNull().hasSize(2);
    }

    @Test
    public void testCanEndAfter() throws Exception {
        Mockito.when(document.getContentsCharCount()).thenReturn(20);
        annotationModel.addAnnotation(new Annotation("aa", true, "aa"), new Position(0, 10));
        annotationModel.addAnnotation(new Annotation("aa", true, "aa"), new Position(2, 5));
        annotationModel.addAnnotation(new Annotation("aa", true, "aa"), new Position(5, 10));
        Iterator<Annotation> iterator = annotationModel.getAnnotationIterator(1, 6, true, false);
        assertThat(iterator).isNotNull().hasSize(2);
    }
}

