package org.jivesoftware.util;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ListPagerTest {
    private static final List<Integer> LIST_OF_25 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25);

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Test
    public void aDefaultRequestWillHaveAPageSizeOf25() {
        final ListPager<Integer> listPager = new ListPager(request, response, ListPagerTest.LIST_OF_25);
        Assert.assertThat(listPager.getCurrentPageNumber(), CoreMatchers.is(1));
        Assert.assertThat(listPager.getTotalPages(), CoreMatchers.is(5));
        Assert.assertThat(listPager.getFilteredItemCount(), CoreMatchers.is(25));
        Assert.assertThat(listPager.isFiltered(), CoreMatchers.is(false));
        Assert.assertThat(listPager.getFirstItemNumberOnPage(), CoreMatchers.is(1));
        Assert.assertThat(listPager.getLastItemNumberOnPage(), CoreMatchers.is(5));
    }

    @Test
    public void anEmptyListWillStartOnPage1Of1() {
        final ListPager<Integer> listPager = new ListPager(request, response, Collections.emptyList());
        Assert.assertThat(listPager.getCurrentPageNumber(), CoreMatchers.is(1));
        Assert.assertThat(listPager.getTotalPages(), CoreMatchers.is(1));
        Assert.assertThat(listPager.getFilteredItemCount(), CoreMatchers.is(0));
        Assert.assertThat(listPager.isFiltered(), CoreMatchers.is(false));
        Assert.assertThat(listPager.getFirstItemNumberOnPage(), CoreMatchers.is(0));
        Assert.assertThat(listPager.getLastItemNumberOnPage(), CoreMatchers.is(0));
    }

    @Test
    public void theLowerBoundCurrentPageIs1() {
        Mockito.doReturn("-1").when(request).getParameter("listPagerCurrentPage");
        final ListPager<Integer> listPager = new ListPager(request, response, ListPagerTest.LIST_OF_25);
        Assert.assertThat(listPager.getCurrentPageNumber(), CoreMatchers.is(1));
        Assert.assertThat(listPager.getFirstItemNumberOnPage(), CoreMatchers.is(1));
        Assert.assertThat(listPager.getLastItemNumberOnPage(), CoreMatchers.is(5));
    }

    @Test
    public void theUpperBoundCurrentPageIsTotalPages() {
        Mockito.doReturn(String.valueOf(Integer.MAX_VALUE)).when(request).getParameter("listPagerCurrentPage");
        final ListPager<Integer> listPager = new ListPager(request, response, ListPagerTest.LIST_OF_25);
        Assert.assertThat(listPager.getCurrentPageNumber(), CoreMatchers.is(5));
        Assert.assertThat(listPager.getFirstItemNumberOnPage(), CoreMatchers.is(21));
        Assert.assertThat(listPager.getLastItemNumberOnPage(), CoreMatchers.is(25));
    }

    @Test
    public void willFilterTheList() {
        // Filter on even numbers only
        final ListPager<Integer> listPager = new ListPager(request, response, ListPagerTest.LIST_OF_25, ( value) -> (value % 2) == 0);
        Assert.assertThat(listPager.isFiltered(), CoreMatchers.is(true));
        Assert.assertThat(listPager.getFilteredItemCount(), CoreMatchers.is(12));
        Assert.assertThat(listPager.getTotalPages(), CoreMatchers.is(3));
        Assert.assertThat(listPager.getTotalItemCount(), CoreMatchers.is(25));
        Assert.assertThat(listPager.getItemsOnCurrentPage(), Matchers.contains(2, 4, 6, 8, 10));
        Assert.assertThat(listPager.getFirstItemNumberOnPage(), CoreMatchers.is(1));
        Assert.assertThat(listPager.getLastItemNumberOnPage(), CoreMatchers.is(5));
    }

    @Test
    public void willRoundUp() {
        Mockito.doReturn("24").when(request).getParameter("listPagerPageSize");
        final ListPager<Integer> listPager = new ListPager(request, response, ListPagerTest.LIST_OF_25);
        Assert.assertThat(listPager.getTotalPages(), CoreMatchers.is(2));
    }
}

