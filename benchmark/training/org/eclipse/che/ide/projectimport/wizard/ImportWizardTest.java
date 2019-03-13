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
package org.eclipse.che.ide.projectimport.wizard;


import org.eclipse.che.ide.api.project.MutableProjectConfig;
import org.eclipse.che.ide.api.wizard.Wizard.CompleteCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Artem Zatsarynnyi
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportWizardTest {
    @Mock
    private ProjectImporter importer;

    @Mock
    private MutableProjectConfig projectConfig;

    @Mock
    private CompleteCallback completeCallback;

    @InjectMocks
    private ImportWizard wizard;

    @Test
    public void shouldCallImporterOnCompletion() {
        wizard.complete(completeCallback);
        Mockito.verify(importer).importProject(completeCallback, projectConfig);
    }
}

