/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs.injection;


import java.lang.reflect.Field;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest {
    private static final Object REFERENCE = new Object();

    @Mock
    private InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.Bean mockedBean;

    @InjectMocks
    private InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.Service illegalInjectionExample = new InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.Service();

    @InjectMocks
    private InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.ServiceWithReversedOrder reversedOrderService = new InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.ServiceWithReversedOrder();

    @InjectMocks
    private InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.WithNullObjectField withNullObjectField = new InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.WithNullObjectField();

    @Test
    public void just_for_information_fields_are_read_in_declaration_order_see_Service() {
        Field[] declaredFields = InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.Service.class.getDeclaredFields();
        Assert.assertEquals("mockShouldNotGoInHere", declaredFields[0].getName());
        Assert.assertEquals("mockShouldGoInHere", declaredFields[1].getName());
    }

    @Test
    public void mock_should_be_injected_once_and_in_the_best_matching_type() {
        Assert.assertSame(InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.REFERENCE, illegalInjectionExample.mockShouldNotGoInHere);
        Assert.assertSame(mockedBean, illegalInjectionExample.mockShouldGoInHere);
    }

    @Test
    public void should_match_be_consistent_regardless_of_order() {
        Assert.assertSame(InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.REFERENCE, reversedOrderService.mockShouldNotGoInHere);
        Assert.assertSame(mockedBean, reversedOrderService.mockShouldGoInHere);
    }

    @Test
    public void should_inject_the_mock_only_once_and_in_the_correct_type() {
        Assert.assertNull(withNullObjectField.keepMeNull);
        Assert.assertSame(mockedBean, withNullObjectField.injectMePlease);
    }

    public static class Bean {}

    public static class Service {
        public final Object mockShouldNotGoInHere = InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.REFERENCE;

        public InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.Bean mockShouldGoInHere;
    }

    public static class ServiceWithReversedOrder {
        public InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.Bean mockShouldGoInHere;

        public final Object mockShouldNotGoInHere = InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.REFERENCE;
    }

    class WithNullObjectField {
        InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest.Bean injectMePlease;

        Object keepMeNull = null;
    }
}

