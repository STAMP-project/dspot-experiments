/**
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.beans.support;


import TestGroup.LONG_RUNNING;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.Assume;
import org.springframework.tests.sample.beans.TestBean;

import static PagedListHolder.DEFAULT_PAGE_SIZE;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Jean-Pierre PAWLAK
 * @author Chris Beams
 * @since 20.05.2003
 */
public class PagedListHolderTests {
    @Test
    public void testPagedListHolder() {
        Assume.group(LONG_RUNNING);
        TestBean tb1 = new TestBean();
        tb1.setName("eva");
        tb1.setAge(25);
        TestBean tb2 = new TestBean();
        tb2.setName("juergen");
        tb2.setAge(99);
        TestBean tb3 = new TestBean();
        tb3.setName("Rod");
        tb3.setAge(32);
        List tbs = new ArrayList();
        tbs.add(tb1);
        tbs.add(tb2);
        tbs.add(tb3);
        PagedListHolder holder = new PagedListHolder(tbs);
        Assert.assertTrue("Correct source", ((holder.getSource()) == tbs));
        Assert.assertTrue("Correct number of elements", ((holder.getNrOfElements()) == 3));
        Assert.assertTrue("Correct number of pages", ((holder.getPageCount()) == 1));
        Assert.assertTrue("Correct page size", ((holder.getPageSize()) == (DEFAULT_PAGE_SIZE)));
        Assert.assertTrue("Correct page number", ((holder.getPage()) == 0));
        Assert.assertTrue("First page", holder.isFirstPage());
        Assert.assertTrue("Last page", holder.isLastPage());
        Assert.assertTrue("Correct first element", ((holder.getFirstElementOnPage()) == 0));
        Assert.assertTrue("Correct first element", ((holder.getLastElementOnPage()) == 2));
        Assert.assertTrue("Correct page list size", ((holder.getPageList().size()) == 3));
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(0)) == tb1));
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(1)) == tb2));
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(2)) == tb3));
        holder.setPageSize(2);
        Assert.assertTrue("Correct number of pages", ((holder.getPageCount()) == 2));
        Assert.assertTrue("Correct page size", ((holder.getPageSize()) == 2));
        Assert.assertTrue("Correct page number", ((holder.getPage()) == 0));
        Assert.assertTrue("First page", holder.isFirstPage());
        Assert.assertFalse("Last page", holder.isLastPage());
        Assert.assertTrue("Correct first element", ((holder.getFirstElementOnPage()) == 0));
        Assert.assertTrue("Correct last element", ((holder.getLastElementOnPage()) == 1));
        Assert.assertTrue("Correct page list size", ((holder.getPageList().size()) == 2));
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(0)) == tb1));
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(1)) == tb2));
        holder.setPage(1);
        Assert.assertTrue("Correct page number", ((holder.getPage()) == 1));
        Assert.assertFalse("First page", holder.isFirstPage());
        Assert.assertTrue("Last page", holder.isLastPage());
        Assert.assertTrue("Correct first element", ((holder.getFirstElementOnPage()) == 2));
        Assert.assertTrue("Correct last element", ((holder.getLastElementOnPage()) == 2));
        Assert.assertTrue("Correct page list size", ((holder.getPageList().size()) == 1));
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(0)) == tb3));
        holder.setPageSize(3);
        Assert.assertTrue("Correct number of pages", ((holder.getPageCount()) == 1));
        Assert.assertTrue("Correct page size", ((holder.getPageSize()) == 3));
        Assert.assertTrue("Correct page number", ((holder.getPage()) == 0));
        Assert.assertTrue("First page", holder.isFirstPage());
        Assert.assertTrue("Last page", holder.isLastPage());
        Assert.assertTrue("Correct first element", ((holder.getFirstElementOnPage()) == 0));
        Assert.assertTrue("Correct last element", ((holder.getLastElementOnPage()) == 2));
        holder.setPage(1);
        holder.setPageSize(2);
        Assert.assertTrue("Correct number of pages", ((holder.getPageCount()) == 2));
        Assert.assertTrue("Correct page size", ((holder.getPageSize()) == 2));
        Assert.assertTrue("Correct page number", ((holder.getPage()) == 1));
        Assert.assertFalse("First page", holder.isFirstPage());
        Assert.assertTrue("Last page", holder.isLastPage());
        Assert.assertTrue("Correct first element", ((holder.getFirstElementOnPage()) == 2));
        Assert.assertTrue("Correct last element", ((holder.getLastElementOnPage()) == 2));
        holder.setPageSize(2);
        holder.setPage(1);
        setProperty("name");
        setIgnoreCase(false);
        holder.resort();
        Assert.assertTrue("Correct source", ((holder.getSource()) == tbs));
        Assert.assertTrue("Correct number of elements", ((holder.getNrOfElements()) == 3));
        Assert.assertTrue("Correct number of pages", ((holder.getPageCount()) == 2));
        Assert.assertTrue("Correct page size", ((holder.getPageSize()) == 2));
        Assert.assertTrue("Correct page number", ((holder.getPage()) == 0));
        Assert.assertTrue("First page", holder.isFirstPage());
        Assert.assertFalse("Last page", holder.isLastPage());
        Assert.assertTrue("Correct first element", ((holder.getFirstElementOnPage()) == 0));
        Assert.assertTrue("Correct last element", ((holder.getLastElementOnPage()) == 1));
        Assert.assertTrue("Correct page list size", ((holder.getPageList().size()) == 2));
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(0)) == tb3));
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(1)) == tb1));
        setProperty("name");
        holder.resort();
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(0)) == tb2));
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(1)) == tb1));
        setProperty("name");
        holder.resort();
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(0)) == tb3));
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(1)) == tb1));
        holder.setPage(1);
        Assert.assertTrue("Correct page list size", ((holder.getPageList().size()) == 1));
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(0)) == tb2));
        setProperty("age");
        holder.resort();
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(0)) == tb1));
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(1)) == tb3));
        setIgnoreCase(true);
        holder.resort();
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(0)) == tb1));
        Assert.assertTrue("Correct page list contents", ((holder.getPageList().get(1)) == tb3));
        holder.nextPage();
        Assert.assertEquals(1, holder.getPage());
        holder.previousPage();
        Assert.assertEquals(0, holder.getPage());
        holder.nextPage();
        Assert.assertEquals(1, holder.getPage());
        holder.nextPage();
        Assert.assertEquals(1, holder.getPage());
        holder.previousPage();
        Assert.assertEquals(0, holder.getPage());
        holder.previousPage();
        Assert.assertEquals(0, holder.getPage());
    }

    public static class MockFilter {
        private String name = "";

        private String age = "";

        private String extendedInfo = "";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getExtendedInfo() {
            return extendedInfo;
        }

        public void setExtendedInfo(String extendedInfo) {
            this.extendedInfo = extendedInfo;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof PagedListHolderTests.MockFilter))
                return false;

            final PagedListHolderTests.MockFilter mockFilter = ((PagedListHolderTests.MockFilter) (o));
            if (!(age.equals(mockFilter.age)))
                return false;

            if (!(extendedInfo.equals(mockFilter.extendedInfo)))
                return false;

            if (!(name.equals(mockFilter.name)))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            result = name.hashCode();
            result = (29 * result) + (age.hashCode());
            result = (29 * result) + (extendedInfo.hashCode());
            return result;
        }
    }
}

