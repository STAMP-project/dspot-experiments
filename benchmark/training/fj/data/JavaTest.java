package fj.data;


import fj.Show;
import java.util.EnumSet;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 * Created by MarkPerry on 14/07/2014.
 */
public class JavaTest {
    @Test
    public void test1() {
        // #33: Fixes ClassCastException
        final List<JavaTest.Colors> colors = Java.<JavaTest.Colors>EnumSet_List().f(EnumSet.allOf(JavaTest.Colors.class));
        MatcherAssert.assertThat(Show.listShow(Show.<JavaTest.Colors>anyShow()).showS(colors), Is.is("List(red,green,blue)"));
    }

    enum Colors {

        red,
        green,
        blue;}
}

