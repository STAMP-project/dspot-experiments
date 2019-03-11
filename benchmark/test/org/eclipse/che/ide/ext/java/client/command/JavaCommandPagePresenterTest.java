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
package org.eclipse.che.ide.ext.java.client.command;


import CommandPage.FieldStateActionDelegate;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.ext.java.client.command.mainclass.SelectNodePresenter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class JavaCommandPagePresenterTest {
    private static final String MAIN_CLASS_PATH = "src/Main.java";

    private static final String COMMAND_LINE = "cd ${current.project.path} &&" + ((("javac -classpath ${project.java.classpath}" + "-sourcepath ${project.java.sourcepath} -d ${project") + ".java.output.dir} src/Main.java &&") + "java -classpath ${project.java.classpath}${project.java.output.dir} Main");

    @Mock
    private JavaCommandPageView view;

    @Mock
    private SelectNodePresenter selectNodePresenter;

    @Mock
    private CommandImpl command;

    @Mock
    private FieldStateActionDelegate fieldStateDelegate;

    @InjectMocks
    private JavaCommandPagePresenter presenter;

    @Test
    public void delegateShouldBeSet() throws Exception {
        Mockito.verify(view).setDelegate(presenter);
    }

    @Test
    public void pageShouldBeInitialized() throws Exception {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        presenter.resetFrom(command);
        presenter.setFieldStateActionDelegate(fieldStateDelegate);
        presenter.go(container);
        Mockito.verify(container).setWidget(view);
        Mockito.verify(view).setMainClass(JavaCommandPagePresenterTest.MAIN_CLASS_PATH);
        Mockito.verify(view).setCommandLine(JavaCommandPagePresenterTest.COMMAND_LINE);
        Mockito.verify(fieldStateDelegate).updatePreviewURLState(false);
    }

    @Test
    public void selectedNodeWindowShouldBeShowed() throws Exception {
        presenter.onAddMainClassBtnClicked();
        Mockito.verify(selectNodePresenter).show(presenter);
    }

    @Test
    public void pageIsNotDirty() throws Exception {
        presenter.resetFrom(command);
        Assert.assertFalse(presenter.isDirty());
    }
}

