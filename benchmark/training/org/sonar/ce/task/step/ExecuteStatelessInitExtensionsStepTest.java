/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.task.step;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ExecuteStatelessInitExtensionsStepTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test_getDescription() {
        ExecuteStatelessInitExtensionsStep underTest = new ExecuteStatelessInitExtensionsStep();
        assertThat(underTest.getDescription()).isEqualTo("Initialize");
    }

    @Test
    public void do_nothing_if_no_extensions() {
        ExecuteStatelessInitExtensionsStep underTest = new ExecuteStatelessInitExtensionsStep();
        // no failure
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void execute_extensions() {
        StatelessInitExtension ext1 = Mockito.mock(StatelessInitExtension.class);
        StatelessInitExtension ext2 = Mockito.mock(StatelessInitExtension.class);
        ExecuteStatelessInitExtensionsStep underTest = new ExecuteStatelessInitExtensionsStep(new StatelessInitExtension[]{ ext1, ext2 });
        underTest.execute(new TestComputationStepContext());
        InOrder inOrder = Mockito.inOrder(ext1, ext2);
        inOrder.verify(ext1).onInit();
        inOrder.verify(ext2).onInit();
    }

    @Test
    public void fail_if_an_extension_throws_an_exception() {
        StatelessInitExtension ext1 = Mockito.mock(StatelessInitExtension.class);
        StatelessInitExtension ext2 = Mockito.mock(StatelessInitExtension.class);
        Mockito.doThrow(new IllegalStateException("BOOM")).when(ext2).onInit();
        StatelessInitExtension ext3 = Mockito.mock(StatelessInitExtension.class);
        ExecuteStatelessInitExtensionsStep underTest = new ExecuteStatelessInitExtensionsStep(new StatelessInitExtension[]{ ext1, ext2, ext3 });
        try {
            underTest.execute(new TestComputationStepContext());
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("BOOM");
            Mockito.verify(ext1).onInit();
            Mockito.verify(ext3, Mockito.never()).onInit();
        }
    }
}

