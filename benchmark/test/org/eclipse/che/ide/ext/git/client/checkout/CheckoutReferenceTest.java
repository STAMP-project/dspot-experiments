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
package org.eclipse.che.ide.ext.git.client.checkout;


import org.eclipse.che.api.git.shared.CheckoutRequest;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.ext.git.client.BaseTest;
import org.eclipse.che.ide.resource.Path;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link CheckoutReferencePresenter} functionality.
 *
 * @author Roman Nikitenko
 * @author Vlad Zhukovskyi
 */
public class CheckoutReferenceTest extends BaseTest {
    private static final String CORRECT_REFERENCE = "someTag";

    private static final String INCORRECT_REFERENCE = "";

    @Mock
    private CheckoutReferenceView view;

    @Mock
    private CheckoutRequest checkoutRequest;

    private CheckoutReferencePresenter presenter;

    @Test
    public void testOnReferenceValueChangedWhenValueIsIncorrect() throws Exception {
        presenter.referenceValueChanged(CheckoutReferenceTest.INCORRECT_REFERENCE);
        view.setCheckoutButEnableState(ArgumentMatchers.eq(false));
    }

    @Test
    public void testOnReferenceValueChangedWhenValueIsCorrect() throws Exception {
        presenter.referenceValueChanged(CheckoutReferenceTest.CORRECT_REFERENCE);
        view.setCheckoutButEnableState(ArgumentMatchers.eq(true));
    }

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog(project);
        Mockito.verify(view).setCheckoutButEnableState(ArgumentMatchers.eq(false));
        Mockito.verify(view).showDialog();
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();
        Mockito.verify(view).close();
    }

    @Test
    public void onEnterClickedWhenValueIsIncorrect() throws Exception {
        Mockito.reset(service);
        Mockito.when(view.getReference()).thenReturn(CheckoutReferenceTest.INCORRECT_REFERENCE);
        presenter.onEnterClicked();
        Mockito.verify(view, Mockito.never()).close();
        Mockito.verify(service, Mockito.never()).checkout(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(CheckoutRequest.class));
    }

    @Test
    public void onEnterClickedWhenValueIsCorrect() throws Exception {
        Mockito.when(appContext.getRootProject()).thenReturn(project);
        Mockito.when(dtoFactory.createDto(CheckoutRequest.class)).thenReturn(checkoutRequest);
        Mockito.when(checkoutRequest.withName(ArgumentMatchers.anyString())).thenReturn(checkoutRequest);
        Mockito.when(checkoutRequest.withCreateNew(ArgumentMatchers.anyBoolean())).thenReturn(checkoutRequest);
        Mockito.reset(service);
        Mockito.when(service.checkout(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(CheckoutRequest.class))).thenReturn(stringPromise);
        Mockito.when(stringPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(stringPromise);
        Mockito.when(stringPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(stringPromise);
        Mockito.when(view.getReference()).thenReturn(CheckoutReferenceTest.CORRECT_REFERENCE);
        presenter.showDialog(project);
        presenter.onEnterClicked();
        Mockito.verify(stringPromise).then(stringCaptor.capture());
        stringCaptor.getValue().apply(null);
        Mockito.verify(synchronizePromise).then(synchronizeCaptor.capture());
        synchronizeCaptor.getValue().apply(new Resource[0]);
        Mockito.verify(view).close();
        Mockito.verify(service).checkout(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(CheckoutRequest.class));
        Mockito.verify(checkoutRequest).withName(CheckoutReferenceTest.CORRECT_REFERENCE);
        Mockito.verifyNoMoreInteractions(checkoutRequest);
    }
}

