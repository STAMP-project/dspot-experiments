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
package org.eclipse.che.ide.projecttype;


import BlankProjectWizardRegistrar.BLANK_CATEGORY;
import BlankProjectWizardRegistrar.BLANK_ID;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Artem Zatsarynnyi
 */
@RunWith(MockitoJUnitRunner.class)
public class BlankProjectWizardRegistrarTest {
    @InjectMocks
    private BlankProjectWizardRegistrar wizardRegistrar;

    @Test
    public void shouldReturnCorrectProjectTypeId() throws Exception {
        Assert.assertThat(wizardRegistrar.getProjectTypeId(), CoreMatchers.equalTo(BLANK_ID));
    }

    @Test
    public void shouldReturnCorrectCategory() throws Exception {
        Assert.assertThat(wizardRegistrar.getCategory(), CoreMatchers.equalTo(BLANK_CATEGORY));
    }

    @Test
    public void shouldNotReturnAnyPages() throws Exception {
        Assert.assertTrue(wizardRegistrar.getWizardPages().isEmpty());
    }
}

