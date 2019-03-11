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
package org.eclipse.che.ide.api.wizard;


import Wizard.UpdateDelegate;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Testing {@link AbstractWizardPage}.
 *
 * @author Artem Zatsarynnyi
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractWizardPageTest {
    private AbstractWizardPage<String> wizardPage;

    @Test
    public void shouldInitPage() throws Exception {
        String dataObject = "dataObject";
        wizardPage.init(dataObject);
        Assert.assertEquals(dataObject, wizardPage.dataObject);
    }

    @Test
    public void shouldSetContext() throws Exception {
        Map<String, String> context = new HashMap<>();
        wizardPage.setContext(context);
        Assert.assertEquals(context, wizardPage.context);
    }

    @Test
    public void shouldSetUpdateDelegate() throws Exception {
        Wizard.UpdateDelegate updateDelegate = Mockito.mock(UpdateDelegate.class);
        wizardPage.setUpdateDelegate(updateDelegate);
        Assert.assertEquals(updateDelegate, wizardPage.updateDelegate);
    }

    @Test
    public void shouldNotSkipped() throws Exception {
        Assert.assertFalse(wizardPage.canSkip());
    }

    @Test
    public void shouldBeCompleted() throws Exception {
        Assert.assertTrue(wizardPage.isCompleted());
    }

    private class DummyWizardPage extends AbstractWizardPage<String> {
        @Override
        public void go(AcceptsOneWidget container) {
            // do nothing
        }
    }
}

