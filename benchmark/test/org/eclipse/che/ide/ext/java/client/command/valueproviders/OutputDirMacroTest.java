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
import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseProvider;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.resources.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class OutputDirMacroTest {
    @InjectMocks
    private OutputDirMacro provider;

    @Mock
    private AppContext appContext;

    @Mock
    private PromiseProvider promises;

    @Mock
    private Resource resource1;

    @Mock
    private Resource resource2;

    @Mock
    private Optional<Project> projectOptional;

    @Mock
    private Project relatedProject;

    @Captor
    private ArgumentCaptor<Promise<String>> valuePromiseCaptor;

    private Resource[] resources;

    private Map<String, List<String>> attributes = new HashMap<>();

    @Test
    public void keyShouldBeReturned() throws Exception {
        Assert.assertEquals("${project.java.output.dir}", provider.getName());
    }

    @Test
    public void valueShouldBeEmptyIfSelectedResourcesIsNull() throws Exception {
        resources = null;
        Mockito.when(appContext.getResources()).thenReturn(resources);
        provider.expand();
        Mockito.verify(promises).resolve(ArgumentMatchers.eq(""));
    }

    @Test
    public void valueShouldBeEmptyIfSelectedManyResources() throws Exception {
        resources = new Resource[]{ resource1, resource2 };
        Mockito.when(appContext.getResources()).thenReturn(resources);
        provider.expand();
        Mockito.verify(promises).resolve(ArgumentMatchers.eq(""));
    }

    @Test
    public void valueShouldBeEmptyIfRelatedProjectOfSelectedResourceIsNull() throws Exception {
        Mockito.when(projectOptional.isPresent()).thenReturn(false);
        provider.expand();
        Mockito.verify(promises).resolve(ArgumentMatchers.eq(""));
    }

    @Test
    public void valueShouldBeEmptyIfRelatedProjectIsNotJavaProject() throws Exception {
        attributes.put(LANGUAGE, Collections.singletonList("cpp"));
        Mockito.when(relatedProject.getAttributes()).thenReturn(attributes);
        provider.expand();
        Mockito.verify(promises).resolve(ArgumentMatchers.eq(""));
    }

    @Test
    public void outputFolderShouldBeRootOfProjectIfAttributeDoesNotExist() throws Exception {
        provider.expand();
        Mockito.verify(promises).resolve(ArgumentMatchers.eq("/projects/projectParent/project"));
    }

    @Test
    public void outputFolderShouldBeSetAsValueOfAttribute() throws Exception {
        attributes.put(OUTPUT_FOLDER, Collections.singletonList("bin"));
        provider.expand();
        Mockito.verify(promises).resolve(ArgumentMatchers.eq("/projects/projectParent/project/bin"));
    }
}

