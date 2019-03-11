/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.junitrunner;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class JUnit45RunnerTest {
    @InjectMocks
    private JUnit45RunnerTest.ListDependent listDependent = new JUnit45RunnerTest.ListDependent();

    @Mock
    private List<String> list;

    @Test
    public void shouldInitMocksUsingRunner() {
        list.add("test");
        Mockito.verify(list).add("test");
    }

    @Test
    public void shouldInjectMocksUsingRunner() {
        Assert.assertNotNull(list);
        Assert.assertSame(list, listDependent.getList());
    }

    @Test
    public void shouldFilterTestMethodsCorrectly() throws Exception {
        MockitoJUnitRunner runner = new MockitoJUnitRunner(this.getClass());
        runner.filter(Filters.methodNameContains("shouldInitMocksUsingRunner"));
        Assert.assertEquals(1, runner.testCount());
    }

    class ListDependent {
        private List<?> list;

        public List<?> getList() {
            return list;
        }
    }
}

