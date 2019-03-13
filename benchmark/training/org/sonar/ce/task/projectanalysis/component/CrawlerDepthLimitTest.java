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
package org.sonar.ce.task.projectanalysis.component;


import CrawlerDepthLimit.DIRECTORY;
import CrawlerDepthLimit.LEAVES;
import CrawlerDepthLimit.PROJECT;
import CrawlerDepthLimit.PROJECT_VIEW;
import CrawlerDepthLimit.SUBVIEW;
import CrawlerDepthLimit.VIEW;
import Type.FILE;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Arrays;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.sonar.ce.task.projectanalysis.component.Component.Type;


@RunWith(DataProviderRunner.class)
public class CrawlerDepthLimitTest {
    private static final Set<Type> REPORT_TYPES = FluentIterable.from(Arrays.asList(Type.values())).filter(new Predicate<Type>() {
        @Override
        public boolean apply(Type input) {
            return input.isReportType();
        }
    }).toSet();

    private static final Set<Type> VIEWS_TYPES = FluentIterable.from(Arrays.asList(Type.values())).filter(new Predicate<Type>() {
        @Override
        public boolean apply(Type input) {
            return input.isViewsType();
        }
    }).toSet();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void PROJECT_isSameAs_only_PROJECT_type() {
        assertIsSameAs(PROJECT, Type.PROJECT);
    }

    @Test
    public void PROJECT_isDeeper_than_no_type() {
        for (Type type : Type.values()) {
            assertThat(PROJECT.isDeeperThan(type)).as("isHigherThan(%s)", type).isFalse();
        }
    }

    @Test
    public void PROJECT_isHigher_than_all_report_types_but_PROJECT() {
        assertThat(PROJECT.isHigherThan(Type.PROJECT)).isFalse();
        for (Type reportType : FluentIterable.from(CrawlerDepthLimitTest.REPORT_TYPES).filter(Predicates.not(Predicates.equalTo(Type.PROJECT)))) {
            assertThat(PROJECT.isHigherThan(reportType)).as("isHigherThan(%s)", reportType).isTrue();
        }
    }

    @Test
    public void PROJECT_isDeeper_than_no_views_types() {
        for (Type viewsType : CrawlerDepthLimitTest.VIEWS_TYPES) {
            assertThat(PROJECT.isDeeperThan(viewsType)).as("isDeeperThan(%s)", viewsType).isFalse();
        }
    }

    @Test
    public void PROJECT_isHigher_than_no_views_types() {
        assertIsHigherThanViewsType(PROJECT);
    }

    @Test
    public void DIRECTORY_isSameAs_only_DIRECTORY_type() {
        assertIsSameAs(DIRECTORY, Type.DIRECTORY);
    }

    @Test
    public void DIRECTORY_isDeeper_than_no_views_types() {
        assertIsDeeperThanViewsType(DIRECTORY);
    }

    @Test
    public void DIRECTORY_isDeeper_than_only_PROJECT_report_type() {
        assertIsDeeperThanReportType(DIRECTORY, Type.PROJECT);
    }

    @Test
    public void DIRECTORY_isHigher_than_only_FILE() {
        assertIsHigherThanReportType(DIRECTORY, FILE);
    }

    @Test
    public void DIRECTORY_isHigher_than_no_views_type() {
        assertIsHigherThanViewsType(DIRECTORY);
    }

    @Test
    public void FILE_isSameAs_only_FILE_type() {
        assertIsSameAs(CrawlerDepthLimit.FILE, FILE);
    }

    @Test
    public void FILE_isDeeper_than_no_views_types() {
        for (Type viewsType : CrawlerDepthLimitTest.VIEWS_TYPES) {
            assertThat(CrawlerDepthLimit.FILE.isDeeperThan(viewsType)).as("isDeeperThan(%s)", viewsType).isFalse();
        }
    }

    @Test
    public void FILE_isHigher_than_no_views_types() {
        assertIsHigherThanViewsType(CrawlerDepthLimit.FILE);
    }

    @Test
    public void FILE_isHigher_than_no_report_types() {
        assertIsHigherThanReportType(CrawlerDepthLimit.FILE);
    }

    @Test
    public void FILE_isDeeper_than_only_PROJECT_MODULE_and_DIRECTORY_report_types() {
        assertIsDeeperThanReportType(CrawlerDepthLimit.FILE, Type.PROJECT, Type.DIRECTORY);
    }

    @Test
    public void VIEW_isSameAs_only_VIEW_type() {
        assertIsSameAs(VIEW, Type.VIEW);
    }

    @Test
    public void VIEW_isDeeper_than_no_type() {
        for (Type type : Type.values()) {
            assertThat(VIEW.isDeeperThan(type)).as("isDeeperThan(%s)", type).isFalse();
        }
    }

    @Test
    public void VIEW_isHigher_than_all_views_types_but_VIEW() {
        assertThat(VIEW.isHigherThan(Type.VIEW)).isFalse();
        for (Type viewsType : FluentIterable.from(CrawlerDepthLimitTest.VIEWS_TYPES).filter(Predicates.not(Predicates.equalTo(Type.VIEW)))) {
            assertThat(VIEW.isHigherThan(viewsType)).as("isHigherThan(%s)", viewsType).isTrue();
        }
    }

    @Test
    public void VIEW_isHigher_than_no_report_types() {
        assertIsHigherThanReportType(VIEW);
    }

    @Test
    public void VIEW_isDeeper_than_no_report_types() {
        assertIsDeeperThanReportType(VIEW);
    }

    @Test
    public void VIEW_isDeeper_than_no_views_types() {
        assertIsDeeperThanViewsType(VIEW);
    }

    @Test
    public void SUBVIEW_isSameAs_only_SUBVIEW_type() {
        assertIsSameAs(SUBVIEW, Type.SUBVIEW);
    }

    @Test
    public void SUBVIEW_isHigher_than_no_report_types() {
        assertIsHigherThanReportType(SUBVIEW);
    }

    @Test
    public void SUBVIEW_isDeeper_than_no_report_types() {
        assertIsDeeperThanReportType(SUBVIEW);
    }

    @Test
    public void SUBVIEW_isDeeper_than_only_VIEW_views_types() {
        assertIsDeeperThanReportType(SUBVIEW, Type.VIEW);
    }

    @Test
    public void PROJECT_VIEW_isSameAs_only_PROJECT_VIEW_type() {
        assertIsSameAs(PROJECT_VIEW, Type.PROJECT_VIEW);
    }

    @Test
    public void PROJECT_VIEW_isHigher_than_no_report_types() {
        assertIsHigherThanReportType(PROJECT_VIEW);
    }

    @Test
    public void PROJECT_VIEW_isDeeper_than_no_report_types() {
        assertIsDeeperThanReportType(PROJECT_VIEW);
    }

    @Test
    public void PROJECT_VIEW_isDeeper_than_VIEWS_and_SUBVIEWS_views_types() {
        assertIsDeeperThanViewsType(PROJECT_VIEW, Type.VIEW, Type.SUBVIEW);
    }

    @Test
    public void LEAVES_is_same_as_FILE_and_PROJECT_VIEW() {
        assertThat(LEAVES.isSameAs(FILE)).isTrue();
        assertThat(LEAVES.isSameAs(Type.PROJECT_VIEW)).isTrue();
        for (Type type : FluentIterable.from(Arrays.asList(Type.values())).filter(Predicates.not(Predicates.in(ImmutableSet.of(FILE, Type.PROJECT_VIEW))))) {
            assertThat(LEAVES.isSameAs(type)).isFalse();
        }
    }

    @Test
    public void LEAVES_isDeeper_than_PROJECT_MODULE_and_DIRECTORY_report_types() {
        assertIsDeeperThanReportType(LEAVES, Type.PROJECT, Type.DIRECTORY);
    }

    @Test
    public void LEAVES_isDeeper_than_VIEW_and_SUBVIEW_views_types() {
        assertIsDeeperThanViewsType(LEAVES, Type.VIEW, Type.SUBVIEW);
    }

    @Test
    public void LEAVES_isHigher_than_no_report_types() {
        assertIsHigherThanReportType(LEAVES);
    }

    @Test
    public void LEAVES_isHigher_than_no_views_types() {
        assertIsHigherThanViewsType(LEAVES);
    }
}

