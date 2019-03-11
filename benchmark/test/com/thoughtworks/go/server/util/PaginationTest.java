/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.server.util;


import Pagination.PageNumber.DOTS;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PaginationTest {
    @Test
    public void shouldNotCreatePaginationWithMoreThan300Records() {
        try {
            Pagination.pageStartingAt(0, 1000, 301);
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("The max number of perPage is [300]."));
        }
    }

    @Test
    public void shouldCreatePaginationWithLessEquals300Records() {
        try {
            Pagination.pageStartingAt(0, 1000, 300);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldCreatePaginationProvidingNull() {
        try {
            Pagination.pageStartingAt(0, 1000, null);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void shouldHavePage1ForFirst() {
        Pagination pagination = Pagination.pageStartingAt(0, 1000, 10);
        Assert.assertThat(pagination.getCurrentPage(), Matchers.is(1));
        Assert.assertThat(pagination.getTotalPages(), Matchers.is(100));
    }

    @Test
    public void shouldHavePage1ForEndOfPage() {
        Pagination pagination = Pagination.pageStartingAt(9, 1000, 10);
        Assert.assertThat(pagination.getCurrentPage(), Matchers.is(1));
    }

    @Test
    public void shouldHavePage2ForTenth() {
        Pagination pagination = Pagination.pageStartingAt(10, 1000, 10);
        Assert.assertThat(pagination.getCurrentPage(), Matchers.is(2));
    }

    @Test
    public void shouldHaveOffsetForPreviousPage() {
        Pagination pagination = Pagination.pageStartingAt(70, 1000, 10);
        Assert.assertThat(pagination.getCurrentPage(), Matchers.is(8));
        Assert.assertThat(pagination.getPreviousPage(), Matchers.is(7));
        Assert.assertThat(pagination.getPreviousOffset(), Matchers.is(60));
        Assert.assertThat(pagination.getNextPage(), Matchers.is(9));
        Assert.assertThat(pagination.getNextOffset(), Matchers.is(80));
        Assert.assertThat(pagination.hasNextPage(), Matchers.is(true));
        Assert.assertThat(pagination.hasPreviousPage(), Matchers.is(true));
    }

    @Test
    public void shouldHaveOffsetsForFinalPage() {
        Pagination pagination = Pagination.pageStartingAt(11, 16, 10);
        Assert.assertThat(pagination.getCurrentPage(), Matchers.is(2));
        Assert.assertThat(pagination.hasNextPage(), Matchers.is(false));
        Assert.assertThat(pagination.hasPreviousPage(), Matchers.is(true));
    }

    @Test
    public void shouldReturnHasPreviousAndNextPages() {
        Pagination pagination = Pagination.pageStartingAt(10, 16, 10);
        Assert.assertThat(pagination.hasPreviousPage(), Matchers.is(true));
        Assert.assertThat(pagination.hasNextPage(), Matchers.is(false));
    }

    @Test
    public void shouldHaveOffsetsForFirstPage() {
        Pagination pagination = Pagination.pageStartingAt(0, 16, 10);
        Assert.assertThat(pagination.getCurrentPage(), Matchers.is(1));
        Assert.assertThat(pagination.hasNextPage(), Matchers.is(true));
        Assert.assertThat(pagination.hasPreviousPage(), Matchers.is(false));
    }

    @Test
    public void shouldFindPageForAGivenOffset() {
        Assert.assertThat(Pagination.pageFor(0, 10, 3), Matchers.is(Pagination.pageStartingAt(0, 10, 3)));
        Assert.assertThat(Pagination.pageFor(1, 10, 3), Matchers.is(Pagination.pageStartingAt(0, 10, 3)));
        Assert.assertThat(Pagination.pageFor(2, 10, 3), Matchers.is(Pagination.pageStartingAt(0, 10, 3)));
        Assert.assertThat(Pagination.pageFor(3, 10, 3), Matchers.is(Pagination.pageStartingAt(3, 10, 3)));
        Assert.assertThat(Pagination.pageFor(4, 10, 3), Matchers.is(Pagination.pageStartingAt(3, 10, 3)));
        Assert.assertThat(Pagination.pageFor(5, 10, 3), Matchers.is(Pagination.pageStartingAt(3, 10, 3)));
        Assert.assertThat(Pagination.pageFor(6, 10, 3), Matchers.is(Pagination.pageStartingAt(6, 10, 3)));
    }

    @Test
    public void shouldFindPageForAGivenPage() {
        Assert.assertThat(Pagination.pageByNumber(1, 10, 3), Matchers.is(Pagination.pageStartingAt(0, 10, 3)));
        Assert.assertThat(Pagination.pageByNumber(2, 10, 3), Matchers.is(Pagination.pageStartingAt(3, 10, 3)));
        Assert.assertThat(Pagination.pageByNumber(3, 10, 3), Matchers.is(Pagination.pageStartingAt(6, 10, 3)));
    }

    @Test
    public void shouldUnderstandFirstPage() {
        Assert.assertThat(Pagination.pageStartingAt(3, 10, 3).getFirstPage(), Matchers.is(1));
        Assert.assertThat(Pagination.pageStartingAt(6, 10, 3).getFirstPage(), Matchers.is(1));
    }

    @Test
    public void shouldUnderstandLastPage() {
        Assert.assertThat(Pagination.pageStartingAt(3, 10, 3).getLastPage(), Matchers.is(4));
        Assert.assertThat(Pagination.pageStartingAt(6, 10, 3).getLastPage(), Matchers.is(4));
        Assert.assertThat(Pagination.pageStartingAt(0, 2, 3).getLastPage(), Matchers.is(1));
        Assert.assertThat(Pagination.pageStartingAt(2, 3, 3).getLastPage(), Matchers.is(1));
    }

    @Test
    public void shouldReturnAllPagesIfLessThan9() {
        Assert.assertThat(Pagination.pageByNumber(1, 8, 1).getPages(), Matchers.is(Arrays.asList(Pagination.currentPage(1), Pagination.page(2), Pagination.page(3), Pagination.page(4), Pagination.page(5), Pagination.page(6), Pagination.page(7), Pagination.page(8), Pagination.page(2, "next"))));
        Assert.assertThat(Pagination.pageByNumber(1, 3, 1).getPages(), Matchers.is(Arrays.asList(Pagination.currentPage(1), Pagination.page(2), Pagination.page(3), Pagination.page(2, "next"))));
    }

    @Test
    public void shouldShowDotsIfMoreThan8Pages() {
        Assert.assertThat(Pagination.pageByNumber(5, 9, 1).getPages(), Matchers.is(Arrays.asList(Pagination.page(4, "prev"), Pagination.page(1), DOTS, Pagination.page(3), Pagination.page(4), Pagination.currentPage(5), Pagination.page(6), Pagination.page(7), DOTS, Pagination.page(9), Pagination.page(6, "next"))));
        Assert.assertThat(Pagination.pageByNumber(5, 100, 1).getPages(), Matchers.is(Arrays.asList(Pagination.page(4, "prev"), Pagination.page(1), DOTS, Pagination.page(3), Pagination.page(4), Pagination.currentPage(5), Pagination.page(6), Pagination.page(7), DOTS, Pagination.page(100), Pagination.page(6, "next"))));
    }

    @Test
    public void shouldShowDotsAtEndIfOnFirst4Pages() {
        Assert.assertThat(Pagination.pageByNumber(1, 100, 1).getPages(), Matchers.is(Arrays.asList(Pagination.currentPage(1), Pagination.page(2), Pagination.page(3), DOTS, Pagination.page(100), Pagination.page(2, "next"))));
        Assert.assertThat(Pagination.pageByNumber(2, 100, 1).getPages(), Matchers.is(Arrays.asList(Pagination.page(1, "prev"), Pagination.page(1), Pagination.currentPage(2), Pagination.page(3), Pagination.page(4), DOTS, Pagination.page(100), Pagination.page(3, "next"))));
        Assert.assertThat(Pagination.pageByNumber(3, 100, 1).getPages(), Matchers.is(Arrays.asList(Pagination.page(2, "prev"), Pagination.page(1), Pagination.page(2), Pagination.currentPage(3), Pagination.page(4), Pagination.page(5), DOTS, Pagination.page(100), Pagination.page(4, "next"))));
        Assert.assertThat(Pagination.pageByNumber(4, 100, 1).getPages(), Matchers.is(Arrays.asList(Pagination.page(3, "prev"), Pagination.page(1), Pagination.page(2), Pagination.page(3), Pagination.currentPage(4), Pagination.page(5), Pagination.page(6), DOTS, Pagination.page(100), Pagination.page(5, "next"))));
    }

    @Test
    public void shouldShowDotsAtStartIfOnLast4Pages() {
        Assert.assertThat(Pagination.pageByNumber(97, 100, 1).getPages(), Matchers.is(Arrays.asList(Pagination.page(96, "prev"), Pagination.page(1), DOTS, Pagination.page(95), Pagination.page(96), Pagination.currentPage(97), Pagination.page(98), Pagination.page(99), Pagination.page(100), Pagination.page(98, "next"))));
        Assert.assertThat(Pagination.pageByNumber(98, 100, 1).getPages(), Matchers.is(Arrays.asList(Pagination.page(97, "prev"), Pagination.page(1), DOTS, Pagination.page(96), Pagination.page(97), Pagination.currentPage(98), Pagination.page(99), Pagination.page(100), Pagination.page(99, "next"))));
        Assert.assertThat(Pagination.pageByNumber(99, 100, 1).getPages(), Matchers.is(Arrays.asList(Pagination.page(98, "prev"), Pagination.page(1), DOTS, Pagination.page(97), Pagination.page(98), Pagination.currentPage(99), Pagination.page(100), Pagination.page(100, "next"))));
        Assert.assertThat(Pagination.pageByNumber(100, 100, 1).getPages(), Matchers.is(Arrays.asList(Pagination.page(99, "prev"), Pagination.page(1), DOTS, Pagination.page(98), Pagination.page(99), Pagination.currentPage(100))));
    }

    @Test
    public void shouldUnderstandDotsPageNumber() {
        Assert.assertThat(DOTS.isDots(), Matchers.is(true));
        Assert.assertThat(Pagination.page(1).isDots(), Matchers.is(false));
    }

    @Test
    public void shouldUnderstandPageNumberAndLabel() {
        Assert.assertThat(DOTS.getLabel(), Matchers.is("..."));
        Assert.assertThat(DOTS.getNumber(), Matchers.is((-1)));
        Assert.assertThat(Pagination.page(5).getLabel(), Matchers.is("5"));
        Assert.assertThat(Pagination.page(5).getNumber(), Matchers.is(5));
        Assert.assertThat(Pagination.page(10, "foo").getLabel(), Matchers.is("foo"));
        Assert.assertThat(Pagination.page(10, "foo").getNumber(), Matchers.is(10));
    }

    @Test
    public void shouldUnderstandIfCurrent() {
        Assert.assertThat(Pagination.currentPage(5).isCurrent(), Matchers.is(true));
        Assert.assertThat(Pagination.page(5).isCurrent(), Matchers.is(false));
    }
}

