/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.annotation;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.util.MockUtil;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


@SuppressWarnings({ "unchecked", "unused" })
public class MockInjectionUsingSetterOrPropertyTest extends TestBase {
    private MockInjectionUsingSetterOrPropertyTest.SuperUnderTesting superUnderTestWithoutInjection = new MockInjectionUsingSetterOrPropertyTest.SuperUnderTesting();

    @InjectMocks
    private MockInjectionUsingSetterOrPropertyTest.SuperUnderTesting superUnderTest = new MockInjectionUsingSetterOrPropertyTest.SuperUnderTesting();

    @InjectMocks
    private MockInjectionUsingSetterOrPropertyTest.BaseUnderTesting baseUnderTest = new MockInjectionUsingSetterOrPropertyTest.BaseUnderTesting();

    @InjectMocks
    private MockInjectionUsingSetterOrPropertyTest.SubUnderTesting subUnderTest = new MockInjectionUsingSetterOrPropertyTest.SubUnderTesting();

    @InjectMocks
    private MockInjectionUsingSetterOrPropertyTest.OtherBaseUnderTesting otherBaseUnderTest = new MockInjectionUsingSetterOrPropertyTest.OtherBaseUnderTesting();

    @InjectMocks
    private MockInjectionUsingSetterOrPropertyTest.HasTwoFieldsWithSameType hasTwoFieldsWithSameType = new MockInjectionUsingSetterOrPropertyTest.HasTwoFieldsWithSameType();

    private MockInjectionUsingSetterOrPropertyTest.BaseUnderTesting baseUnderTestingInstance = new MockInjectionUsingSetterOrPropertyTest.BaseUnderTesting();

    @InjectMocks
    private MockInjectionUsingSetterOrPropertyTest.BaseUnderTesting initializedBase = baseUnderTestingInstance;

    @InjectMocks
    private MockInjectionUsingSetterOrPropertyTest.BaseUnderTesting notInitializedBase;

    @Spy
    @InjectMocks
    private MockInjectionUsingSetterOrPropertyTest.SuperUnderTesting initializedSpy = new MockInjectionUsingSetterOrPropertyTest.SuperUnderTesting();

    @Spy
    @InjectMocks
    private MockInjectionUsingSetterOrPropertyTest.SuperUnderTesting notInitializedSpy;

    @Mock
    private Map<?, ?> map;

    @Mock
    private List<?> list;

    @Mock
    private Set<?> histogram1;

    @Mock
    private Set<?> histogram2;

    @Mock
    private IMethods candidate2;

    @Spy
    private TreeSet<String> searchTree = new TreeSet<String>();

    @Test
    public void should_keep_same_instance_if_field_initialized() {
        Assert.assertSame(baseUnderTestingInstance, initializedBase);
    }

    @Test
    public void should_initialize_annotated_field_if_null() {
        Assert.assertNotNull(notInitializedBase);
    }

    @Test
    public void should_inject_mocks_in_spy() {
        Assert.assertNotNull(initializedSpy.getAList());
        Assert.assertTrue(MockUtil.isMock(initializedSpy));
    }

    @Test
    public void should_initialize_spy_if_null_and_inject_mocks() {
        Assert.assertNotNull(notInitializedSpy);
        Assert.assertNotNull(notInitializedSpy.getAList());
        Assert.assertTrue(MockUtil.isMock(notInitializedSpy));
    }

    @Test
    public void should_inject_mocks_if_annotated() {
        MockitoAnnotations.initMocks(this);
        Assert.assertSame(list, superUnderTest.getAList());
    }

    @Test
    public void should_not_inject_if_not_annotated() {
        MockitoAnnotations.initMocks(this);
        Assert.assertNull(superUnderTestWithoutInjection.getAList());
    }

    @Test
    public void should_inject_mocks_for_class_hierarchy_if_annotated() {
        MockitoAnnotations.initMocks(this);
        Assert.assertSame(list, baseUnderTest.getAList());
        Assert.assertSame(map, baseUnderTest.getAMap());
    }

    @Test
    public void should_inject_mocks_by_name() {
        MockitoAnnotations.initMocks(this);
        Assert.assertSame(histogram1, subUnderTest.getHistogram1());
        Assert.assertSame(histogram2, subUnderTest.getHistogram2());
    }

    @Test
    public void should_inject_spies() {
        MockitoAnnotations.initMocks(this);
        Assert.assertSame(searchTree, otherBaseUnderTest.getSearchTree());
    }

    @Test
    public void should_insert_into_field_with_matching_name_when_multiple_fields_of_same_type_exists_in_injectee() {
        MockitoAnnotations.initMocks(this);
        Assert.assertNull("not injected, no mock named 'candidate1'", hasTwoFieldsWithSameType.candidate1);
        Assert.assertNotNull("injected, there's a mock named 'candidate2'", hasTwoFieldsWithSameType.candidate2);
    }

    @Test
    public void should_instantiate_inject_mock_field_if_possible() throws Exception {
        Assert.assertNotNull(notInitializedBase);
    }

    @Test
    public void should_keep_instance_on_inject_mock_field_if_present() throws Exception {
        Assert.assertSame(baseUnderTestingInstance, initializedBase);
    }

    @Test
    public void should_report_nicely() throws Exception {
        Object failing = new Object() {
            @InjectMocks
            MockInjectionUsingSetterOrPropertyTest.ThrowingConstructor failingConstructor;
        };
        try {
            MockitoAnnotations.initMocks(failing);
            Assert.fail();
        } catch (MockitoException e) {
            Assertions.assertThat(e.getMessage()).contains("failingConstructor").contains("constructor").contains("threw an exception");
            Assertions.assertThat(e.getCause()).isInstanceOf(RuntimeException.class);
        }
    }

    static class ThrowingConstructor {
        ThrowingConstructor() {
            throw new RuntimeException("aha");
        }
    }

    static class SuperUnderTesting {
        private List<?> aList;

        public List<?> getAList() {
            return aList;
        }
    }

    static class BaseUnderTesting extends MockInjectionUsingSetterOrPropertyTest.SuperUnderTesting {
        private Map<?, ?> aMap;

        public Map<?, ?> getAMap() {
            return aMap;
        }
    }

    static class OtherBaseUnderTesting extends MockInjectionUsingSetterOrPropertyTest.SuperUnderTesting {
        private TreeSet<?> searchTree;

        public TreeSet<?> getSearchTree() {
            return searchTree;
        }
    }

    static class SubUnderTesting extends MockInjectionUsingSetterOrPropertyTest.BaseUnderTesting {
        private Set<?> histogram1;

        private Set<?> histogram2;

        public Set<?> getHistogram1() {
            return histogram1;
        }

        public Set<?> getHistogram2() {
            return histogram2;
        }
    }

    static class HasTwoFieldsWithSameType {
        private IMethods candidate1;

        private IMethods candidate2;
    }
}

