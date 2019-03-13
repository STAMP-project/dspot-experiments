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
package org.eclipse.che.ide.ext.java.client.refactoring.rename.wizard.similarnames;


import MatchStrategy.EMBEDDED;
import MatchStrategy.EXACT;
import MatchStrategy.SUFFIX;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class SimilarNamesConfigurationViewImplTest {
    @Mock
    private JavaLocalizationConstant locale;

    private SimilarNamesConfigurationViewImpl view;

    @Test
    public void machStrategyShouldBeReturnExacValue() throws Exception {
        Mockito.when(view.findExactNames.getValue()).thenReturn(true);
        Mockito.verify(locale).renameSimilarNamesConfigurationTitle();
        Assert.assertEquals(EXACT, view.getMatchStrategy());
    }

    @Test
    public void machStrategyShouldBeReturnEmbeddedValue() throws Exception {
        Mockito.when(view.findExactNames.getValue()).thenReturn(false);
        Mockito.when(view.findEmbeddedNames.getValue()).thenReturn(true);
        Assert.assertEquals(EMBEDDED, view.getMatchStrategy());
    }

    @Test
    public void machStrategyShouldBeReturnSuffixValue() throws Exception {
        Mockito.when(view.findExactNames.getValue()).thenReturn(false);
        Mockito.when(view.findEmbeddedNames.getValue()).thenReturn(false);
        Mockito.when(view.findNameSuffixes.getValue()).thenReturn(true);
        Assert.assertEquals(SUFFIX, view.getMatchStrategy());
    }
}

