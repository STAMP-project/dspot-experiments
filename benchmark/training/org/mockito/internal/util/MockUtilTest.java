/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util;


import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.internal.configuration.plugins.Plugins;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class MockUtilTest extends TestBase {
    @Test
    public void should_get_handler() {
        List<?> mock = Mockito.mock(List.class);
        Assert.assertNotNull(MockUtil.getMockHandler(mock));
    }

    @Test(expected = NotAMockException.class)
    public void should_scream_when_not_a_mock_passed() {
        MockUtil.getMockHandler("");
    }

    @Test(expected = MockitoException.class)
    public void should_scream_when_null_passed() {
        MockUtil.getMockHandler(null);
    }

    @Test
    public void should_get_mock_settings() {
        List<?> mock = Mockito.mock(List.class);
        Assert.assertNotNull(MockUtil.getMockSettings(mock));
    }

    @Test
    public void should_validate_mock() {
        Assert.assertFalse(MockUtil.isMock("i mock a mock"));
        Assert.assertTrue(MockUtil.isMock(Mockito.mock(List.class)));
    }

    @Test
    public void should_validate_spy() {
        Assert.assertFalse(MockUtil.isSpy("i mock a mock"));
        Assert.assertFalse(MockUtil.isSpy(Mockito.mock(List.class)));
        Assert.assertFalse(MockUtil.isSpy(null));
        Assert.assertTrue(MockUtil.isSpy(Mockito.spy(new ArrayList())));
        Assert.assertTrue(MockUtil.isSpy(Mockito.spy(ArrayList.class)));
        Assert.assertTrue(MockUtil.isSpy(Mockito.mock(ArrayList.class, Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS))));
    }

    @Test
    public void should_redefine_MockName_if_default() {
        List<?> mock = Mockito.mock(List.class);
        MockUtil.maybeRedefineMockName(mock, "newName");
        Assertions.assertThat(MockUtil.getMockName(mock).toString()).isEqualTo("newName");
    }

    @Test
    public void should_not_redefine_MockName_if_default() {
        List<?> mock = Mockito.mock(List.class, "original");
        MockUtil.maybeRedefineMockName(mock, "newName");
        Assertions.assertThat(MockUtil.getMockName(mock).toString()).isEqualTo("original");
    }

    final class FinalClass {}

    class SomeClass {}

    interface SomeInterface {}

    @Test
    public void should_know_if_type_is_mockable() throws Exception {
        Assertions.assertThat(MockUtil.typeMockabilityOf(MockUtilTest.FinalClass.class).mockable()).isEqualTo(Plugins.getMockMaker().isTypeMockable(MockUtilTest.FinalClass.class).mockable());
        Assert.assertFalse(MockUtil.typeMockabilityOf(int.class).mockable());
        Assert.assertTrue(MockUtil.typeMockabilityOf(MockUtilTest.SomeClass.class).mockable());
        Assert.assertTrue(MockUtil.typeMockabilityOf(MockUtilTest.SomeInterface.class).mockable());
    }
}

