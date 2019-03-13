package org.robolectric.shadows;


import View.GONE;
import View.INVISIBLE;
import View.VISIBLE;
import android.content.Context;
import android.widget.LinearLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ViewInnerTextTest {
    private Context context;

    @Test
    public void testInnerText() throws Exception {
        LinearLayout top = new LinearLayout(context);
        top.addView(textView("blah"));
        top.addView(new android.view.View(context));
        top.addView(textView("a b c"));
        LinearLayout innerLayout = new LinearLayout(context);
        top.addView(innerLayout);
        innerLayout.addView(textView("d e f"));
        innerLayout.addView(textView("g h i"));
        innerLayout.addView(textView(""));
        innerLayout.addView(textView(null));
        innerLayout.addView(textView("jkl!"));
        top.addView(textView("mnop"));
        Assert.assertEquals("blah a b c d e f g h i jkl! mnop", Shadows.shadowOf(top).innerText());
    }

    @Test
    public void shouldOnlyIncludeViewTextViewsText() throws Exception {
        LinearLayout top = new LinearLayout(context);
        top.addView(textView("blah", VISIBLE));
        top.addView(textView("blarg", GONE));
        top.addView(textView("arrg", INVISIBLE));
        Assert.assertEquals("blah", Shadows.shadowOf(top).innerText());
    }

    @Test
    public void shouldNotPrefixBogusSpaces() throws Exception {
        LinearLayout top = new LinearLayout(context);
        top.addView(textView("blarg", GONE));
        top.addView(textView("arrg", INVISIBLE));
        top.addView(textView("blah", VISIBLE));
        Assert.assertEquals("blah", Shadows.shadowOf(top).innerText());
    }
}

