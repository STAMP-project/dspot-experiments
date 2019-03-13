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
package com.iluwatar.intercepting.filter;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Date: 12/13/15 - 3:01 PM
 *
 * @author Jeroen Meulemeester
 */
public class FilterManagerTest {
    @Test
    public void testFilterRequest() {
        final Target target = Mockito.mock(Target.class);
        final FilterManager filterManager = new FilterManager();
        Assertions.assertEquals("RUNNING...", filterManager.filterRequest(Mockito.mock(Order.class)));
        Mockito.verifyZeroInteractions(target);
    }

    @Test
    public void testAddFilter() {
        final Target target = Mockito.mock(Target.class);
        final FilterManager filterManager = new FilterManager();
        Mockito.verifyZeroInteractions(target);
        final Filter filter = Mockito.mock(Filter.class);
        Mockito.when(filter.execute(ArgumentMatchers.any(Order.class))).thenReturn("filter");
        filterManager.addFilter(filter);
        final Order order = Mockito.mock(Order.class);
        Assertions.assertEquals("filter", filterManager.filterRequest(order));
        Mockito.verify(filter, Mockito.times(1)).execute(ArgumentMatchers.any(Order.class));
        Mockito.verifyZeroInteractions(target, filter, order);
    }
}

