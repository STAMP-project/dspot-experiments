package fj.data;


import fj.F;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by amar on 28/12/14.
 */
public class List_Traverse_Tests {
    @Test
    public void shouldTraverseListWithGivenFunction() {
        List<String> strings = List.list("some1", "some2", "some3", "not_some", "  ");
        F<String, Option<String>> f = ( s) -> {
            if (s.startsWith("some"))
                return some(s);
            else
                return Option.none();

        };
        Option<List<String>> optStr = strings.traverseOption(f);
        Assert.assertEquals("optStr should be none", Option.none(), optStr);
    }

    @Test
    public void shouldTraverseListWithGivenFunction2() {
        List<String> strings = List.list("some1", "some2", "some3");
        F<String, Option<String>> f = ( s) -> {
            if (s.startsWith("some"))
                return some(s);
            else
                return Option.none();

        };
        Option<List<String>> optStr = strings.traverseOption(f);
        Assert.assertEquals("optStr should be some", optStr.isSome(), true);
        Assert.assertThat(optStr.some(), CoreMatchers.is(List.list("some1", "some2", "some3")));
    }
}

