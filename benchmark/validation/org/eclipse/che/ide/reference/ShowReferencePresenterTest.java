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
package org.eclipse.che.ide.reference;


import java.util.HashMap;
import java.util.Map;
import org.eclipse.che.ide.api.reference.FqnProvider;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.resource.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ShowReferencePresenterTest {
    private static final Path PATH = Path.valueOf("path");

    private static final String PROJECT_TYPE = "type";

    // constructor mocks
    @Mock
    private ShowReferenceView view;

    // additional mocks
    @Mock
    private FqnProvider provider;

    @Mock
    private Resource resource;

    @Mock
    private Project project;

    private ShowReferencePresenter presenter;

    @Test
    public void pathShouldBeShownForNodeWhichDoesNotHaveFqn() {
        Map<String, FqnProvider> providers = new HashMap<>();
        presenter = new ShowReferencePresenter(view, providers);
        presenter.show(resource);
        Mockito.verify(provider, Mockito.never()).getFqn(resource);
        Mockito.verify(view).show("", ShowReferencePresenterTest.PATH);
    }

    @Test
    public void pathAndFqnShouldBeShownForNode() {
        Mockito.when(provider.getFqn(resource)).thenReturn("fqn");
        presenter.show(resource);
        Mockito.verify(provider).getFqn(resource);
        Mockito.verify(view).show("fqn", ShowReferencePresenterTest.PATH);
    }
}

