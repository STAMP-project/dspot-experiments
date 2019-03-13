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
package org.androidannotations.test;


import R.id.listView;
import R.id.listViewWithArgument;
import R.id.listViewWithArgumentWithParameterType;
import R.id.listViewWithOneParam;
import R.id.listViewWithPosition;
import R.id.spinner;
import R.id.spinnerWithArgument;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class ItemClicksHandledActivityTest {
    private static final int TESTED_CLICKED_INDEX = 3;

    private String clickedItem;

    private ItemClicksHandledActivity_ activity;

    @Test
    public void handlingSpinnerItemSelect() {
        Spinner spinner = ((Spinner) (activity.findViewById(spinner)));
        assertThat(activity.spinnerItemClicked).isFalse();
        spinner.getOnItemSelectedListener().onItemSelected(spinner, null, ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX, 0);
        assertThat(activity.spinnerItemClicked).isTrue();
    }

    @Test
    public void handlingListViewItemClick() {
        ListView listView = ((ListView) (activity.findViewById(listView)));
        long itemId = listView.getAdapter().getItemId(ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX);
        View view = listView.getChildAt(ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX);
        assertThat(activity.listViewItemClicked).isFalse();
        listView.performItemClick(view, ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX, itemId);
        assertThat(activity.listViewItemClicked).isTrue();
    }

    @Test
    public void handlingSpinnerItemSelectWithArgument() {
        Spinner spinner = ((Spinner) (activity.findViewById(spinnerWithArgument)));
        assertThat(activity.spinnerWithArgumentSelectedItem).isNull();
        spinner.getOnItemSelectedListener().onItemSelected(spinner, null, ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX, 0);
        assertThat(activity.spinnerWithArgumentSelectedItem).isNotNull();
        assertThat(activity.spinnerWithArgumentSelectedItem).isEqualTo(clickedItem);
    }

    @Test
    public void handlingListViewitemClickWithArgument() {
        ListView listView = ((ListView) (activity.findViewById(listViewWithArgument)));
        long itemId = listView.getAdapter().getItemId(ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX);
        View view = listView.getChildAt(ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX);
        assertThat(activity.listViewWithArgumentSelectedItem).isNull();
        listView.performItemClick(view, ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX, itemId);
        assertThat(activity.listViewWithArgumentSelectedItem).isNotNull();
        assertThat(activity.listViewWithArgumentSelectedItem).isEqualTo(clickedItem);
    }

    @Test
    public void handlingListViewItemClickWithPosition() {
        ListView listView = ((ListView) (activity.findViewById(listViewWithPosition)));
        long itemId = listView.getAdapter().getItemId(ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX);
        View view = listView.getChildAt(ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX);
        assertThat(activity.listViewWithPositionClickedPosition).isEqualTo(0);
        listView.performItemClick(view, ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX, itemId);
        assertThat(activity.listViewWithPositionClickedPosition).isEqualTo(ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX);
    }

    @Test
    public void handlingListViewWithPositionItemSelected() {
        final ListView listView = ((ListView) (activity.findViewById(listViewWithPosition)));
        assertThat(activity.listViewWithPositionItemSelectedPosition).isEqualTo(0);
        assertThat(activity.listViewWithPositionItemSelected).isFalse();
        listView.getOnItemSelectedListener().onItemSelected(listView, null, ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX, 0);
        assertThat(activity.listViewWithPositionItemSelected).isTrue();
        assertThat(activity.listViewWithPositionItemSelectedPosition).isEqualTo(ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX);
    }

    @Test
    public void canHaveOneSelectedArgument() {
        ListView listView = ((ListView) (activity.findViewById(listViewWithOneParam)));
        assertThat(activity.listViewWithOneParamItemSelected).isFalse();
        listView.getOnItemSelectedListener().onItemSelected(listView, null, ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX, 0);
        assertThat(activity.listViewWithOneParamItemSelected).isTrue();
    }

    @Test
    public void handlingListViewItemClickWithParametrizedItem() {
        ListView listView = ((ListView) (activity.findViewById(listViewWithArgumentWithParameterType)));
        long itemId = listView.getAdapter().getItemId(ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX);
        View view = listView.getChildAt(ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX);
        assertThat(activity.listViewParametrizedItemClicked).isFalse();
        listView.performItemClick(view, ItemClicksHandledActivityTest.TESTED_CLICKED_INDEX, itemId);
        assertThat(activity.listViewParametrizedItemClicked).isTrue();
    }
}

