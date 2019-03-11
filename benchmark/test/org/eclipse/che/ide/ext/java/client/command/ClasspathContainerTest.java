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
package org.eclipse.che.ide.ext.java.client.command;


import ProjectClasspathChangedEvent.TYPE;
import com.google.web.bindery.event.shared.EventBus;
import java.util.List;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.ext.java.client.service.JavaLanguageExtensionServiceClient;
import org.eclipse.che.jdt.ls.extension.api.dto.ClasspathEntry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Valeriy Svydenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ClasspathContainerTest {
    private static final String PROJECT_PATH = "/project1";

    @Mock
    private JavaLanguageExtensionServiceClient classpathServiceClient;

    @Mock
    private EventBus eventBus;

    @Mock
    private Promise<List<ClasspathEntry>> classpathEntries;

    @InjectMocks
    private ClasspathContainer classpathContainer;

    @Test
    public void changedClasspathHandlerShouldBeAdded() throws Exception {
        Mockito.verify(eventBus).addHandler(TYPE, classpathContainer);
    }

    @Test
    public void classpathShouldBeAdded() throws Exception {
        Promise<List<ClasspathEntry>> entries = classpathContainer.getClasspathEntries(ClasspathContainerTest.PROJECT_PATH);
        Mockito.verify(classpathServiceClient).classpathTree(ClasspathContainerTest.PROJECT_PATH);
        Assert.assertEquals(classpathEntries, entries);
    }

    @Test
    public void classpathAlreadyIncludes() throws Exception {
        classpathContainer.getClasspathEntries(ClasspathContainerTest.PROJECT_PATH);
        Mockito.reset(classpathServiceClient);
        Promise<List<ClasspathEntry>> entries = classpathContainer.getClasspathEntries(ClasspathContainerTest.PROJECT_PATH);
        Mockito.verify(classpathServiceClient, Mockito.never()).classpathTree(ClasspathContainerTest.PROJECT_PATH);
        Assert.assertEquals(classpathEntries, entries);
    }
}

