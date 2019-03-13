/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.flux.dispatcher;


import MenuItem.COMPANY;
import MenuItem.HOME;
import com.iluwatar.flux.action.Action;
import com.iluwatar.flux.action.Content;
import com.iluwatar.flux.action.ContentAction;
import com.iluwatar.flux.action.MenuAction;
import com.iluwatar.flux.action.MenuItem;
import com.iluwatar.flux.store.Store;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


/**
 * Date: 12/12/15 - 8:22 PM
 *
 * @author Jeroen Meulemeester
 */
public class DispatcherTest {
    @Test
    public void testGetInstance() throws Exception {
        Assertions.assertNotNull(Dispatcher.getInstance());
        Assertions.assertSame(Dispatcher.getInstance(), Dispatcher.getInstance());
    }

    @Test
    public void testMenuItemSelected() throws Exception {
        final Dispatcher dispatcher = Dispatcher.getInstance();
        final Store store = Mockito.mock(Store.class);
        dispatcher.registerStore(store);
        dispatcher.menuItemSelected(HOME);
        dispatcher.menuItemSelected(COMPANY);
        // We expect 4 events, 2 menu selections and 2 content change actions
        final ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);
        Mockito.verify(store, Mockito.times(4)).onAction(actionCaptor.capture());
        Mockito.verifyNoMoreInteractions(store);
        final List<Action> actions = actionCaptor.getAllValues();
        final List<MenuAction> menuActions = actions.stream().filter(( a) -> a.getType().equals(ActionType.MENU_ITEM_SELECTED)).map(( a) -> ((MenuAction) (a))).collect(Collectors.toList());
        final List<ContentAction> contentActions = actions.stream().filter(( a) -> a.getType().equals(ActionType.CONTENT_CHANGED)).map(( a) -> ((ContentAction) (a))).collect(Collectors.toList());
        Assertions.assertEquals(2, menuActions.size());
        Assertions.assertEquals(1, menuActions.stream().map(MenuAction::getMenuItem).filter(MenuItem.HOME::equals).count());
        Assertions.assertEquals(1, menuActions.stream().map(MenuAction::getMenuItem).filter(MenuItem.COMPANY::equals).count());
        Assertions.assertEquals(2, contentActions.size());
        Assertions.assertEquals(1, contentActions.stream().map(ContentAction::getContent).filter(Content.PRODUCTS::equals).count());
        Assertions.assertEquals(1, contentActions.stream().map(ContentAction::getContent).filter(Content.COMPANY::equals).count());
    }
}

