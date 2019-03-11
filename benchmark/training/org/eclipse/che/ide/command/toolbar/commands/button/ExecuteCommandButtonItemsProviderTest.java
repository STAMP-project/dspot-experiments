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
package org.eclipse.che.ide.command.toolbar.commands.button;


import java.util.Optional;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.command.goal.RunGoal;
import org.eclipse.che.ide.ui.menubutton.ItemsProvider.DataChangedHandler;
import org.eclipse.che.ide.ui.menubutton.MenuItem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link ExecuteCommandButtonItemsProvider}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExecuteCommandButtonItemsProviderTest {
    @Mock
    private AppContext appContext;

    @Mock
    private MenuItemsFactory menuItemsFactory;

    @Mock
    private RunGoal goal;

    @InjectMocks
    private ExecuteCommandButtonItemsProvider provider;

    @Test
    public void shouldReturnDefaultItem() throws Exception {
        MenuItem menuItem = Mockito.mock(MenuItem.class);
        provider.setDefaultItem(menuItem);
        Assert.assertEquals(Optional.of(menuItem), provider.getDefaultItem());
    }

    @Test
    public void testAddCommand() throws Exception {
        // given
        CommandImpl command1 = Mockito.mock(CommandImpl.class);
        CommandImpl command2 = Mockito.mock(CommandImpl.class);
        CommandItem commandItem1 = Mockito.mock(CommandItem.class);
        CommandItem commandItem2 = Mockito.mock(CommandItem.class);
        Mockito.when(menuItemsFactory.newCommandItem(ArgumentMatchers.eq(command1))).thenReturn(commandItem1);
        Mockito.when(menuItemsFactory.newCommandItem(ArgumentMatchers.eq(command2))).thenReturn(commandItem2);
        DataChangedHandler dataChangedHandler = Mockito.mock(DataChangedHandler.class);
        provider.setDataChangedHandler(dataChangedHandler);
        // when
        provider.addCommand(command1);
        provider.addCommand(command2);
        // then
        assertThat(provider.getItems()).hasSize(2).contains(commandItem1, commandItem2);
        Mockito.verify(dataChangedHandler, Mockito.times(2)).onDataChanged();
    }

    @Test
    public void testRemoveCommand() throws Exception {
        // given
        CommandImpl command1 = Mockito.mock(CommandImpl.class);
        CommandImpl command2 = Mockito.mock(CommandImpl.class);
        CommandItem commandItem1 = Mockito.mock(CommandItem.class);
        CommandItem commandItem2 = Mockito.mock(CommandItem.class);
        Mockito.when(menuItemsFactory.newCommandItem(ArgumentMatchers.eq(command1))).thenReturn(commandItem1);
        Mockito.when(menuItemsFactory.newCommandItem(ArgumentMatchers.eq(command2))).thenReturn(commandItem2);
        DataChangedHandler dataChangedHandler = Mockito.mock(DataChangedHandler.class);
        provider.setDataChangedHandler(dataChangedHandler);
        provider.addCommand(command1);
        provider.addCommand(command2);
        // when
        provider.removeCommand(command1);
        // then
        assertThat(provider.getItems()).hasSize(1).containsOnly(commandItem2);
        Mockito.verify(dataChangedHandler, Mockito.times(3)).onDataChanged();
    }

    @Test
    public void shouldProvideGuideItemOnlyWhenNoCommands() throws Exception {
        GuideItem guideItem = Mockito.mock(GuideItem.class);
        Mockito.when(menuItemsFactory.newGuideItem(goal)).thenReturn(guideItem);
        assertThat(provider.getItems()).hasSize(1).containsOnly(guideItem);
    }
}

