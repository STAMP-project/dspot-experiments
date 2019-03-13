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


import R.id.viewPager1;
import R.id.viewPager2;
import R.id.viewPager3;
import R.id.viewPager4;
import ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class PageChangeListenerActivityTest {
    private PageChangeListenerActivity_ activity;

    private ViewPager viewPager;

    private OnPageChangeListener listener;

    @Test
    public void handlePageSelected() {
        initViewPager(viewPager1);
        assertViewPagerIsNull();
        assertPositionIsNotSet();
        int position = 100;
        listener.onPageSelected(position);
        assertThat(activity.viewPager).isEqualTo(viewPager);
        assertThat(activity.position).isEqualTo(position);
    }

    @Test
    public void handlePageScrolled() {
        initViewPager(viewPager1);
        assertReadyForTest();
        int position = 101;
        float positionOffset = 102.0F;
        int positionOffsetPixels = 103;
        listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        assertThat(activity.viewPager).isEqualTo(viewPager);
        assertThat(activity.position).isEqualTo(position);
        assertThat(activity.positionOffset).isEqualTo(positionOffset);
        assertThat(activity.positionOffsetPixels).isEqualTo(positionOffsetPixels);
    }

    @Test
    public void handlePageScrollStateChanged() {
        initViewPager(viewPager1);
        assertReadyForTest();
        int state = 104;
        listener.onPageScrollStateChanged(state);
        assertThat(activity.viewPager).isEqualTo(viewPager);
        assertThat(activity.state).isEqualTo(state);
    }

    @Test
    public void handleNoPageSelectedParameter() {
        initViewPager(viewPager2);
        assertReadyForTest();
        listener.onPageSelected(1);
        assertViewPagerIsNull();
        assertPositionIsNotSet();
    }

    @Test
    public void handleNoPageScrolledParameter() {
        initViewPager(viewPager2);
        assertReadyForTest();
        listener.onPageScrolled(1, 1.0F, 2);
        assertViewPagerIsNull();
        assertPositionIsNotSet();
        assertPositionOffsetIsNotSet();
        assertPositionOffsetPixelsIsNotSet();
    }

    @Test
    public void handleNoPageScrollStateChangedParameter() {
        initViewPager(viewPager2);
        assertReadyForTest();
        listener.onPageScrollStateChanged(1);
        assertViewPagerIsNull();
        assertStateIsNotSet();
    }

    @Test
    public void handleAnyOrderPageSelectedParameter() {
        initViewPager(viewPager3);
        assertReadyForTest();
        int position = 105;
        listener.onPageSelected(position);
        assertThat(activity.viewPager).isEqualTo(viewPager);
        assertThat(activity.position).isEqualTo(position);
    }

    @Test
    public void handleAnyOrderPageScrollStateChangedParameter() {
        initViewPager(viewPager3);
        assertReadyForTest();
        int state = 106;
        listener.onPageScrollStateChanged(state);
        assertThat(activity.viewPager).isEqualTo(viewPager);
        assertThat(activity.state).isEqualTo(state);
    }

    @Test
    public void handlePageScrollFirstIntBecomesPositionParameter() {
        initViewPager(viewPager3);
        assertReadyForTest();
        int position = 107;
        listener.onPageScrolled(position, 108.0F, 109);
        assertViewPagerIsNull();
        assertThat(activity.position).isEqualTo(position);
        assertPositionOffsetIsNotSet();
        assertPositionOffsetPixelsIsNotSet();
    }

    @Test
    public void handleIntAfterPositionOffsetBecomesPositionOffsetPixels() {
        initViewPager(viewPager4);
        assertReadyForTest();
        float positionOffset = 111.0F;
        int positionOffsetPixels = 112;
        listener.onPageScrolled(110, positionOffset, positionOffsetPixels);
        assertViewPagerIsNull();
        assertPositionIsNotSet();
        assertThat(activity.positionOffset).isEqualTo(positionOffset);
        assertThat(activity.positionOffsetPixels).isEqualTo(positionOffsetPixels);
    }
}

