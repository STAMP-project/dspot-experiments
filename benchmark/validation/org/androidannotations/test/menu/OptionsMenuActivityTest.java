/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test.menu;


import R.id.menu_add;
import R.id.menu_refresh;
import R.id.menu_search;
import R.id.menu_share;
import android.view.MenuItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class OptionsMenuActivityTest {
    private OptionsMenuActivity_ activity;

    @Test
    public void defaultIdSelected() {
        MenuItem item = Mockito.mock(MenuItem.class);
        Mockito.when(item.getItemId()).thenReturn(menu_refresh);
        activity.onOptionsItemSelected(item);
        assertThat(activity.menuRefreshSelected).isTrue();
    }

    @Test
    public void multipleIdsSelected() {
        MenuItem item = Mockito.mock(MenuItem.class);
        Mockito.when(item.getItemId()).thenReturn(menu_search);
        boolean result = activity.onOptionsItemSelected(item);
        assertThat(activity.multipleMenuItems).isTrue();
        assertThat(result).isFalse();
        activity.multipleMenuItems = false;
        Mockito.when(item.getItemId()).thenReturn(menu_share);
        result = activity.onOptionsItemSelected(item);
        assertThat(activity.multipleMenuItems).isTrue();
        assertThat(result).isFalse();
    }

    @Test
    public void defaultIdUnderscore() {
        MenuItem item = Mockito.mock(MenuItem.class);
        Mockito.when(item.getItemId()).thenReturn(menu_add);
        activity.onOptionsItemSelected(item);
        assertThat(activity.menuAdd).isTrue();
    }

    @Test
    public void subclassTakesPrecedenceInMenuItemHandling() {
        MenuItem item = Mockito.mock(MenuItem.class);
        Mockito.when(item.getItemId()).thenReturn(menu_refresh);
        activity.onOptionsItemSelected(item);
        assertThat(activity.menuRefreshSelected).isTrue();
        assertThat(activity.menuRefreshSelectedFromAnnotatedClass).isFalse();
    }
}

