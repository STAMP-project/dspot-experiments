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
import javax.validation.constraints.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Testing {@link AbstractWizard}.
 *
 * @author Andrey Plotnikov
 * @author Artem Zatsarynnyi
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractWizardTest {
    private final String dataObject = "dataObject";

    @Mock
    private WizardPage<String> page1;

    @Mock
    private WizardPage<String> page2;

    @Mock
    private WizardPage<String> page3;

    @Mock
    private WizardPage<String> page4;

    @Mock
    private UpdateDelegate updateDelegate;

    private AbstractWizard<String> wizard;

    @Test
    public void testAddPage() throws Exception {
        wizard.addPage(page1);
        wizard.addPage(page2);
        wizard.addPage(page3);
        Mockito.verify(page1).setUpdateDelegate(ArgumentMatchers.eq(updateDelegate));
        Mockito.verify(page1).setContext(ArgumentMatchers.anyMapOf(String.class, String.class));
        Mockito.verify(page1).init(ArgumentMatchers.eq(dataObject));
        Mockito.verify(page2).setUpdateDelegate(ArgumentMatchers.eq(updateDelegate));
        Mockito.verify(page2).setContext(ArgumentMatchers.anyMapOf(String.class, String.class));
        Mockito.verify(page2).init(ArgumentMatchers.eq(dataObject));
        Mockito.verify(page3).setUpdateDelegate(ArgumentMatchers.eq(updateDelegate));
        Mockito.verify(page3).setContext(ArgumentMatchers.anyMapOf(String.class, String.class));
        Mockito.verify(page3).init(ArgumentMatchers.eq(dataObject));
        Assert.assertEquals(page1, wizard.navigateToFirst());
        Assert.assertEquals(page2, wizard.navigateToNext());
        Assert.assertEquals(page3, wizard.navigateToNext());
        Assert.assertNull(wizard.navigateToNext());
    }

    @Test
    public void testAddPageByIndex() throws Exception {
        wizard.addPage(page1);
        wizard.addPage(page3);
        wizard.addPage(page2, 1, false);
        Assert.assertEquals(page1, wizard.navigateToFirst());
        Assert.assertEquals(page2, wizard.navigateToNext());
        Assert.assertEquals(page3, wizard.navigateToNext());
        Assert.assertNull(wizard.navigateToNext());
    }

    @Test
    public void testAddPageWithReplace() throws Exception {
        wizard.addPage(page1);
        wizard.addPage(page3);
        wizard.addPage(page2, 1, true);
        Assert.assertEquals(page1, wizard.navigateToFirst());
        Assert.assertEquals(page2, wizard.navigateToNext());
        Assert.assertNull(wizard.navigateToNext());
    }

    @Test
    public void testNavigateToFirstWhenNeedToSkipFirstPages() throws Exception {
        Mockito.when(page1.canSkip()).thenReturn(true);
        wizard.addPage(page1);
        wizard.addPage(page2);
        wizard.addPage(page3);
        Assert.assertEquals(page2, wizard.navigateToFirst());
    }

    @Test
    public void testNavigateToFirst() throws Exception {
        wizard.addPage(page1);
        Assert.assertEquals(page1, wizard.navigateToFirst());
    }

    @Test
    public void testCanCompleteWhenAllPagesIsCompleted() throws Exception {
        Mockito.when(page1.isCompleted()).thenReturn(true);
        Mockito.when(page2.isCompleted()).thenReturn(true);
        Mockito.when(page3.isCompleted()).thenReturn(true);
        wizard.addPage(page1);
        wizard.addPage(page2);
        wizard.addPage(page3);
        Assert.assertEquals(true, wizard.canComplete());
    }

    @Test
    public void testCanCompleteWhenSomePageIsNotCompleted() throws Exception {
        Mockito.when(page1.isCompleted()).thenReturn(true);
        Mockito.when(page2.isCompleted()).thenReturn(false);
        wizard.addPage(page1);
        wizard.addPage(page2);
        Assert.assertEquals(false, wizard.canComplete());
    }

    @Test
    public void testNavigateToNextUseCase1() throws Exception {
        prepareTestCase1();
        Assert.assertEquals(page1, wizard.navigateToFirst());
        Assert.assertEquals(page2, wizard.navigateToNext());
        Assert.assertEquals(page4, wizard.navigateToNext());
        Assert.assertNull(wizard.navigateToNext());
    }

    @Test
    public void testNavigateToPreviousUseCase1() throws Exception {
        prepareTestCase1();
        wizard.navigateToFirst();
        navigatePages(wizard, 2);
        Assert.assertEquals(page2, wizard.navigateToPrevious());
        Assert.assertEquals(page1, wizard.navigateToPrevious());
    }

    @Test
    public void testHasNextUseCase1() throws Exception {
        prepareTestCase1();
        wizard.navigateToFirst();
        Assert.assertEquals(true, wizard.hasNext());
        navigatePages(wizard, 1);
        Assert.assertEquals(true, wizard.hasNext());
        navigatePages(wizard, 1);
        Assert.assertEquals(false, wizard.hasNext());
    }

    @Test
    public void testHasPreviousUseCase1() throws Exception {
        prepareTestCase1();
        wizard.navigateToFirst();
        Assert.assertEquals(false, wizard.hasPrevious());
        navigatePages(wizard, 1);
        Assert.assertEquals(true, wizard.hasPrevious());
        navigatePages(wizard, 1);
        Assert.assertEquals(true, wizard.hasPrevious());
    }

    @Test
    public void testNavigateToNextUseCase2() throws Exception {
        prepareTestCase2();
        Assert.assertEquals(page1, wizard.navigateToFirst());
        Assert.assertEquals(page2, wizard.navigateToNext());
        Assert.assertNull(wizard.navigateToNext());
    }

    @Test
    public void testNavigateToPreviousUseCase2() throws Exception {
        prepareTestCase2();
        wizard.navigateToFirst();
        navigatePages(wizard, 1);
        Assert.assertEquals(page1, wizard.navigateToPrevious());
    }

    @Test
    public void testHasNextUseCase2() throws Exception {
        prepareTestCase2();
        wizard.navigateToFirst();
        Assert.assertEquals(true, wizard.hasNext());
        navigatePages(wizard, 1);
        Assert.assertEquals(false, wizard.hasNext());
    }

    @Test
    public void testHasPreviousUseCase2() throws Exception {
        prepareTestCase2();
        wizard.navigateToFirst();
        Assert.assertEquals(false, wizard.hasPrevious());
        navigatePages(wizard, 1);
        Assert.assertEquals(true, wizard.hasPrevious());
    }

    private class DummyWizard extends AbstractWizard<String> {
        DummyWizard(String dataObject) {
            super(dataObject);
        }

        @Override
        public void complete(@NotNull
        CompleteCallback callback) {
            // do nothing
        }
    }
}

