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
package org.eclipse.che.ide.ext.java.client.command.valueproviders;


import com.google.common.base.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseProvider;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.ext.java.client.command.ClasspathContainer;
import org.eclipse.che.ide.ext.java.client.project.classpath.ClasspathResolver;
import org.eclipse.che.ide.resource.Path;
import org.eclipse.che.jdt.ls.extension.api.dto.ClasspathEntry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
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
public class ClasspathMacroTest {
    @Mock
    private ClasspathContainer classpathContainer;

    @Mock
    private ClasspathResolver classpathResolver;

    @Mock
    private AppContext appContext;

    @Mock
    private PromiseProvider promises;

    @Mock
    private Resource resource;

    @Mock
    private Optional<Project> projectOptional;

    @Mock
    private Project project;

    @Mock
    private Promise<List<ClasspathEntry>> classpathEntriesPromise;

    @Captor
    private ArgumentCaptor<Function<List<ClasspathEntry>, String>> classpathEntriesCapture;

    @InjectMocks
    private ClasspathMacro classpathMacro;

    private Resource[] resources = new Resource[1];

    @Test
    public void classpathShouldBeBuiltWith2Libraries() throws Exception {
        String lib1 = "lib1.jar";
        String lib2 = "lib2.jar";
        List<ClasspathEntry> entries = new ArrayList<>();
        Set<String> libs = new HashSet<>();
        libs.add(lib1);
        libs.add(lib2);
        Mockito.when(classpathContainer.getClasspathEntries(ArgumentMatchers.anyString())).thenReturn(classpathEntriesPromise);
        Mockito.when(classpathResolver.getLibs()).thenReturn(libs);
        classpathMacro.expand();
        Mockito.verify(classpathEntriesPromise).then(classpathEntriesCapture.capture());
        String classpath = classpathEntriesCapture.getValue().apply(entries);
        Mockito.verify(classpathResolver).resolveClasspathEntries(entries);
        Assert.assertEquals("lib2.jar:lib1.jar:", classpath);
    }

    @Test
    public void classpathShouldBeBuiltWith2ExternalLibrariesAnd2LibrariesFromContainer() throws Exception {
        String lib1 = "lib1.jar";
        String lib2 = "lib2.jar";
        List<ClasspathEntry> entries = new ArrayList<>();
        Set<String> libs = new HashSet<>();
        libs.add(lib1);
        libs.add(lib2);
        ClasspathEntry container = Mockito.mock(ClasspathEntry.class);
        ClasspathEntry cLib1 = Mockito.mock(ClasspathEntry.class);
        ClasspathEntry cLib2 = Mockito.mock(ClasspathEntry.class);
        Mockito.when(container.getPath()).thenReturn("containerPath");
        Mockito.when(container.getChildren()).thenReturn(Arrays.asList(cLib1, cLib2));
        Mockito.when(cLib1.getPath()).thenReturn("cLib1.jar");
        Mockito.when(cLib1.getEntryKind()).thenReturn(LIBRARY);
        Mockito.when(cLib2.getPath()).thenReturn("cLib2.jar");
        Mockito.when(cLib2.getEntryKind()).thenReturn(LIBRARY);
        Set<ClasspathEntry> containers = new HashSet<>();
        containers.add(container);
        Mockito.when(classpathContainer.getClasspathEntries(ArgumentMatchers.anyString())).thenReturn(classpathEntriesPromise);
        Mockito.when(classpathResolver.getLibs()).thenReturn(libs);
        Mockito.when(classpathResolver.getContainers()).thenReturn(containers);
        classpathMacro.expand();
        Mockito.verify(classpathEntriesPromise).then(classpathEntriesCapture.capture());
        String classpath = classpathEntriesCapture.getValue().apply(entries);
        Mockito.verify(classpathResolver).resolveClasspathEntries(entries);
        Assert.assertEquals("lib2.jar:lib1.jar:cLib1.jar:cLib2.jar:", classpath);
    }

    @Test
    public void defaultValueOfClasspathShouldBeBuilt() throws Exception {
        List<ClasspathEntry> entries = new ArrayList<>();
        Set<String> libs = new HashSet<>();
        Path projectsRoot = Path.valueOf("/projects");
        Mockito.when(appContext.getProjectsRoot()).thenReturn(projectsRoot);
        Mockito.when(classpathContainer.getClasspathEntries(ArgumentMatchers.anyString())).thenReturn(classpathEntriesPromise);
        Mockito.when(classpathResolver.getLibs()).thenReturn(libs);
        classpathMacro.expand();
        Mockito.verify(classpathEntriesPromise).then(classpathEntriesCapture.capture());
        String classpath = classpathEntriesCapture.getValue().apply(entries);
        Mockito.verify(classpathResolver).resolveClasspathEntries(entries);
        Assert.assertEquals("/projects/name:", classpath);
    }

    @Test
    public void keyOfTheClasspathShouldBeReturned() throws Exception {
        Assert.assertEquals("${project.java.classpath}", classpathMacro.getName());
    }
}

