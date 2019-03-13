/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.collections;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


public class ListUtilTest extends TestBase {
    @Test
    public void shouldFilterList() throws Exception {
        List<String> list = Arrays.asList("one", "x", "two", "x", "three");
        List<String> filtered = ListUtil.filter(list, new ListUtil.Filter<String>() {
            public boolean isOut(String object) {
                return object == "x";
            }
        });
        Assertions.assertThat(filtered).containsSequence("one", "two", "three");
    }

    @Test
    public void shouldReturnEmptyIfEmptyListGiven() throws Exception {
        List<Object> list = new LinkedList<Object>();
        List<Object> filtered = ListUtil.filter(list, null);
        Assert.assertTrue(filtered.isEmpty());
    }
}

