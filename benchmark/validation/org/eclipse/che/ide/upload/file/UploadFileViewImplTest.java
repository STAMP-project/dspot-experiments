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
package org.eclipse.che.ide.upload.file;


import FormPanel.ENCODING_MULTIPART;
import FormPanel.METHOD_POST;
import UploadFileViewImpl.UploadFileViewBinder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.core.AgentURLModifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Testing {@link UploadFileViewImpl} functionality.
 *
 * @author Roman Nikitenko.
 */
@RunWith(GwtMockitoTestRunner.class)
public class UploadFileViewImplTest {
    private UploadFileViewImpl view;

    UploadFileViewBinder binder;

    CoreLocalizationConstant locale;

    AgentURLModifier agentURLModifier;

    @Test
    public void getFileNameShouldBeExecuted() {
        view.file = Mockito.mock(FileUpload.class);
        Mockito.when(view.file.getFilename()).thenReturn("fileName");
        view.getFileName();
        Mockito.verify(view.file).getFilename();
    }

    @Test
    public void submitShouldBeExecuted() {
        view.submitForm = Mockito.mock(FormPanel.class);
        view.submit();
        Mockito.verify(view.submitForm).submit();
    }

    @Test
    public void setActionShouldBeExecuted() {
        view.submitForm = Mockito.mock(FormPanel.class);
        final String url = "url";
        Mockito.when(agentURLModifier.modify(url)).thenReturn(url);
        view.setAction(url);
        Mockito.verify(view.submitForm).setAction(ArgumentMatchers.eq(url));
        Mockito.verify(view.submitForm).setMethod(ArgumentMatchers.eq(METHOD_POST));
    }

    @Test
    public void setEncodingShouldBeExecuted() {
        view.submitForm = Mockito.mock(FormPanel.class);
        view.setEncoding(ENCODING_MULTIPART);
        Mockito.verify(view.submitForm).setEncoding(ArgumentMatchers.eq(ENCODING_MULTIPART));
    }

    @Test
    public void setEnabledUploadButtonShouldBeExecuted() {
        view.btnUpload = Mockito.mock(Button.class);
        view.setEnabledUploadButton(true);
        Mockito.verify(view.btnUpload).setEnabled(ArgumentMatchers.eq(true));
    }

    @Test
    public void closeShouldBeExecuted() {
        view.uploadPanel = Mockito.mock(FlowPanel.class);
        view.file = Mockito.mock(FileUpload.class);
        view.overwrite = Mockito.mock(CheckBox.class);
        view.btnUpload = Mockito.mock(Button.class);
        view.closeDialog();
        Mockito.verify(view.uploadPanel).remove(((FileUpload) (ArgumentMatchers.anyObject())));
        Mockito.verify(view.btnUpload).setEnabled(ArgumentMatchers.eq(false));
        Mockito.verify(view.overwrite).setValue(ArgumentMatchers.eq(false));
    }
}

