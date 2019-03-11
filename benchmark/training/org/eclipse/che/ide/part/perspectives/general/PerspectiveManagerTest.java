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
package org.eclipse.che.ide.part.perspectives.general;


import org.eclipse.che.ide.api.parts.Perspective;
import org.eclipse.che.ide.api.parts.PerspectiveManager;
import org.eclipse.che.ide.api.parts.PerspectiveManager.PerspectiveTypeListener;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
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
public class PerspectiveManagerTest {
    @Mock
    private PerspectiveTypeListener typeListener;

    @Mock
    private Perspective projectPerspective;

    @Mock
    private Perspective machinePerspective;

    private PerspectiveManager manager;

    @Test
    public void defaultPerspectiveShouldBeReturned() {
        Perspective perspective = manager.getActivePerspective();
        Assert.assertThat(perspective, CoreMatchers.sameInstance(projectPerspective));
    }

    @Test
    public void perspectiveIdShouldBeSet() {
        manager.addListener(typeListener);
        manager.setPerspectiveId("Machine Perspective");
        Mockito.verify(projectPerspective).storeState();
        Mockito.verify(typeListener).onPerspectiveChanged();
        Assert.assertThat(manager.getActivePerspective(), CoreMatchers.equalTo(machinePerspective));
        Assert.assertThat(manager.getPerspectiveId(), CoreMatchers.equalTo("Machine Perspective"));
    }
}

