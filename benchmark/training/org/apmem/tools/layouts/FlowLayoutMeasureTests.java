package org.apmem.tools.layouts;


import FlowLayout.LayoutParams;
import View.MeasureSpec;
import View.MeasureSpec.AT_MOST;
import View.MeasureSpec.EXACTLY;
import ViewGroup.LayoutParams.WRAP_CONTENT;
import android.widget.Button;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 21)
public class FlowLayoutMeasureTests {
    TestActivity activity = Robolectric.setupActivity(TestActivity.class);

    @Test
    public void WrapContent_NoChildViews_ZeroSize() {
        final FlowLayout layout = new FlowLayout(getApplicationContext());
        Assert.assertEquals(0, layout.getMeasuredWidth());
        Assert.assertEquals(0, layout.getMeasuredHeight());
    }

    @Test
    public void WrapContent_OneChildView_EqualSize() {
        final FlowLayout layout = new FlowLayout(getApplicationContext());
        final Button btn = new Button(activity);
        btn.setLayoutParams(new FlowLayout.LayoutParams(30, 40));
        layout.addView(btn);
        layout.measure(MeasureSpec.makeMeasureSpec(WRAP_CONTENT, EXACTLY), MeasureSpec.makeMeasureSpec(WRAP_CONTENT, EXACTLY));
        Assert.assertEquals(30, layout.getMeasuredWidth());
        Assert.assertEquals(40, layout.getMeasuredHeight());
    }

    @Test
    public void SpecifiedSize_OneChildView_SizeNotChanged() {
        final FlowLayout layout = new FlowLayout(getApplicationContext());
        final Button btn = new Button(activity);
        btn.setLayoutParams(new FlowLayout.LayoutParams(30, 40));
        layout.addView(btn);
        layout.measure(MeasureSpec.makeMeasureSpec(20, EXACTLY), MeasureSpec.makeMeasureSpec(30, EXACTLY));
        Assert.assertEquals(20, layout.getMeasuredWidth());
        Assert.assertEquals(30, layout.getMeasuredHeight());
    }

    @Test
    public void MaximumSize_OneChildViewFit_SizeReduced() {
        final FlowLayout layout = new FlowLayout(getApplicationContext());
        final Button btn = new Button(activity);
        btn.setLayoutParams(new FlowLayout.LayoutParams(30, 40));
        layout.addView(btn);
        layout.measure(MeasureSpec.makeMeasureSpec(40, AT_MOST), MeasureSpec.makeMeasureSpec(50, AT_MOST));
        Assert.assertEquals(30, layout.getMeasuredWidth());
        Assert.assertEquals(40, layout.getMeasuredHeight());
    }

    @Test
    public void MaximumSize_OneChildViewNotFit_SizeNotChanged() {
        final FlowLayout layout = new FlowLayout(getApplicationContext());
        final Button btn = new Button(activity);
        btn.setLayoutParams(new FlowLayout.LayoutParams(30, 40));
        layout.addView(btn);
        layout.measure(MeasureSpec.makeMeasureSpec(20, AT_MOST), MeasureSpec.makeMeasureSpec(30, AT_MOST));
        Assert.assertEquals(20, layout.getMeasuredWidth());
        Assert.assertEquals(30, layout.getMeasuredHeight());
    }

    @Test
    public void WrapContent_TakesChildMarginIntoAccount() {
        final FlowLayout layout = new FlowLayout(getApplicationContext());
        final Button btn = new Button(activity);
        FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(30, 40);
        lp.setMargins(1, 2, 3, 4);
        btn.setLayoutParams(lp);
        layout.addView(btn);
        layout.measure(MeasureSpec.makeMeasureSpec(40, AT_MOST), MeasureSpec.makeMeasureSpec(50, AT_MOST));
        Assert.assertEquals(34, layout.getMeasuredWidth());
        Assert.assertEquals(46, layout.getMeasuredHeight());
    }
}

