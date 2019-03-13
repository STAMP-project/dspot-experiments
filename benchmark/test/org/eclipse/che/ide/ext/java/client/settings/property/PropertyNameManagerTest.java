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
package org.eclipse.che.ide.ext.java.client.settings.property;


import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions;
import org.hamcrest.CoreMatchers;
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
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class PropertyNameManagerTest {
    private static final String SOME_TEXT = "someText";

    @Mock
    private JavaLocalizationConstant locale;

    @InjectMocks
    private PropertyNameManager nameManager;

    @Test
    public void constructorShouldBeVerified() {
        Mockito.verify(locale).propertyUnusedLocal();
        Mockito.verify(locale).propertyUnusedImport();
        Mockito.verify(locale).propertyDeadCode();
        Mockito.verify(locale).propertyWithConstructorName();
        Mockito.verify(locale).propertyUnnecessaryElse();
        Mockito.verify(locale).comparingIdenticalValues();
        Mockito.verify(locale).noEffectAssignment();
        Mockito.verify(locale).missingSerialVersionUid();
        Mockito.verify(locale).typeParameterHideAnotherType();
        Mockito.verify(locale).fieldHidesAnotherField();
        Mockito.verify(locale).missingSwitchDefaultCase();
        Mockito.verify(locale).unusedPrivateMember();
        Mockito.verify(locale).uncheckedTypeOperation();
        Mockito.verify(locale).usageRawType();
        Mockito.verify(locale).missingOverrideAnnotation();
        Mockito.verify(locale).nullPointerAccess();
        Mockito.verify(locale).potentialNullPointerAccess();
        Mockito.verify(locale).redundantNullCheck();
    }

    @Test
    public void parameterNameShouldBeReturned() {
        Mockito.when(locale.propertyUnusedLocal()).thenReturn(PropertyNameManagerTest.SOME_TEXT);
        PropertyNameManager nameManager = new PropertyNameManager(locale);
        String name = nameManager.getName(ErrorWarningsOptions.COMPILER_UNUSED_LOCAL);
        Assert.assertThat(name, CoreMatchers.equalTo(PropertyNameManagerTest.SOME_TEXT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgumentExceptionShouldBeThrownWhenNameIsNotFound() {
        nameManager.getName(ErrorWarningsOptions.COMPILER_UNUSED_LOCAL);
    }
}

