package com.github.promeg.pinyinhelper;


import java.util.ArrayList;
import java.util.List;
import org.ahocorasick.trie.Emit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * Created by guyacong on 2016/12/28.
 */
// CHECKSTYLE:OFF
public class ForwardLongestSelectorTest {
    ForwardLongestSelector mSelector = new ForwardLongestSelector();

    @Test
    public void select_single_hit() throws Exception {
        List<Emit> list = new ArrayList<Emit>();
        list.add(new Emit(0, 4, null));
        List<Emit> result = mSelector.select(list);
        MatcherAssert.assertThat(result.size(), Matchers.is(1));
        MatcherAssert.assertThat(result.get(0).getStart(), Matchers.is(0));
        MatcherAssert.assertThat(result.get(0).getEnd(), Matchers.is(4));
    }

    @Test
    public void select_multi_hit_no_overlap() throws Exception {
        List<Emit> list = new ArrayList<Emit>();
        list.add(new Emit(0, 5, null));
        list.add(new Emit(7, 8, null));
        list.add(new Emit(9, 10, null));
        List<Emit> result = mSelector.select(list);
        MatcherAssert.assertThat(result.size(), Matchers.is(3));
        MatcherAssert.assertThat(result.get(0).getStart(), Matchers.is(0));
        MatcherAssert.assertThat(result.get(0).getEnd(), Matchers.is(5));
        MatcherAssert.assertThat(result.get(1).getStart(), Matchers.is(7));
        MatcherAssert.assertThat(result.get(1).getEnd(), Matchers.is(8));
        MatcherAssert.assertThat(result.get(2).getStart(), Matchers.is(9));
        MatcherAssert.assertThat(result.get(2).getEnd(), Matchers.is(10));
    }

    @Test
    public void select_multi_hit_with_overlap() throws Exception {
        List<Emit> list = new ArrayList<Emit>();
        list.add(new Emit(0, 4, null));
        list.add(new Emit(0, 4, null));
        list.add(new Emit(0, 5, null));
        list.add(new Emit(2, 3, null));
        list.add(new Emit(2, 10, null));
        list.add(new Emit(5, 7, null));
        list.add(new Emit(7, 8, null));
        list.add(new Emit(8, 9, null));
        List<Emit> result = mSelector.select(list);
        MatcherAssert.assertThat(result.size(), Matchers.is(2));
        MatcherAssert.assertThat(result.get(0).getStart(), Matchers.is(0));
        MatcherAssert.assertThat(result.get(0).getEnd(), Matchers.is(5));
        MatcherAssert.assertThat(result.get(1).getStart(), Matchers.is(7));
        MatcherAssert.assertThat(result.get(1).getEnd(), Matchers.is(8));
    }

    @Test
    public void select_null_return_null() throws Exception {
        List<Emit> result = mSelector.select(null);
        MatcherAssert.assertThat(result, Matchers.nullValue());
    }
}

/**
 * CHECKSTYLE:ON
 */
