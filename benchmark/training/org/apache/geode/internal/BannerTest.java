/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal;


import Banner.BannerHeader;
import org.apache.geode.internal.logging.Banner;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit tests for {@link Banner} and {@link Banner.BannerHeader}.
 */
@Category(LoggingTest.class)
public class BannerTest {
    private String banner;

    @Test
    public void moreThanZeroBannerHeaderValues() {
        assertThat(BannerHeader.values().length).isGreaterThan(0);
    }

    @Test
    public void moreThanZeroBannerHeaderDisplayValues() {
        assertThat(BannerHeader.displayValues().length).isGreaterThan(0);
    }

    @Test
    public void displayValuesReturnsDisplayValueForEveryBannerHeader() {
        for (Banner.BannerHeader bannerHeader : BannerHeader.values()) {
            assertThat(BannerHeader.displayValues()).contains(bannerHeader.displayValue());
        }
    }

    @Test
    public void bannerContainsEveryBannerHeader() {
        assertThat(banner).contains(BannerHeader.displayValues());
    }
}

