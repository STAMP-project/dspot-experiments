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
package com.iluwatar.flux.view;


import MenuItem.HOME;
import MenuItem.PRODUCTS;
import com.iluwatar.flux.action.Action;
import com.iluwatar.flux.dispatcher.Dispatcher;
import com.iluwatar.flux.store.MenuStore;
import com.iluwatar.flux.store.Store;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Date: 12/12/15 - 10:31 PM
 *
 * @author Jeroen Meulemeester
 */
public class MenuViewTest {
    @Test
    public void testStoreChanged() throws Exception {
        final MenuStore store = Mockito.mock(MenuStore.class);
        Mockito.when(store.getSelected()).thenReturn(HOME);
        final MenuView view = new MenuView();
        view.storeChanged(store);
        Mockito.verify(store, Mockito.times(1)).getSelected();
        Mockito.verifyNoMoreInteractions(store);
    }

    @Test
    public void testItemClicked() throws Exception {
        final Store store = Mockito.mock(Store.class);
        Dispatcher.getInstance().registerStore(store);
        final MenuView view = new MenuView();
        view.itemClicked(PRODUCTS);
        // We should receive a menu click action and a content changed action
        Mockito.verify(store, Mockito.times(2)).onAction(ArgumentMatchers.any(Action.class));
    }
}

