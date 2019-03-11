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
package org.eclipse.che.ide.part.explorer.project.macro;


import ExplorerCurrentFilePathMacro.KEY;
import com.google.common.base.Joiner;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.resource.Path;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for the {@link ExplorerCurrentFilePathMacro}
 *
 * @author Vlad Zhukovskyi
 */
@RunWith(GwtMockitoTestRunner.class)
public class ExplorerCurrentFilePathMacroTest extends AbstractExplorerMacroTest {
    private ExplorerCurrentFilePathMacro provider;

    @Test
    public void testGetKey() throws Exception {
        Assert.assertSame(provider.getName(), KEY);
    }

    @Test
    public void getValue() throws Exception {
        initWithOneFile();
        provider.expand();
        Mockito.verify(promiseProvider).resolve(ArgumentMatchers.eq(Path.valueOf(AbstractExplorerMacroTest.PROJECTS_ROOT).append(AbstractExplorerMacroTest.FILE_1_PATH).toString()));
    }

    @Test
    public void getMultipleValues() throws Exception {
        initWithTwoFiles();
        provider.expand();
        Mockito.verify(promiseProvider).resolve(ArgumentMatchers.eq(Joiner.on(", ").join(Path.valueOf(AbstractExplorerMacroTest.PROJECTS_ROOT).append(AbstractExplorerMacroTest.FILE_1_PATH).toString(), Path.valueOf(AbstractExplorerMacroTest.PROJECTS_ROOT).append(AbstractExplorerMacroTest.FILE_2_PATH).toString())));
    }

    @Test
    public void getEmptyValues() throws Exception {
        initWithNoFiles();
        provider.expand();
        Mockito.verify(promiseProvider).resolve(ArgumentMatchers.eq(""));
    }
}

