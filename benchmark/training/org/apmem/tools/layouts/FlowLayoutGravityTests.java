package org.apmem.tools.layouts;


import FlowLayout.LayoutParams;
import Gravity.CENTER;
import Gravity.FILL;
import View.MeasureSpec;
import View.MeasureSpec.EXACTLY;
import android.view.Gravity;
import android.widget.Button;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 21)
public class FlowLayoutGravityTests {
    TestActivity activity = Robolectric.setupActivity(TestActivity.class);

    @Test
    public void MoveChildToRightBottomCorner() {
        final FlowLayout layout = new FlowLayout(getApplicationContext());
        layout.setGravity(((Gravity.RIGHT) | (Gravity.BOTTOM)));
        final Button btn = new Button(activity);
        FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(30, 40);
        lp.setMargins(1, 2, 3, 4);
        btn.setLayoutParams(lp);
        layout.addView(btn);
        layout.measure(MeasureSpec.makeMeasureSpec(50, EXACTLY), MeasureSpec.makeMeasureSpec(60, EXACTLY));
        layout.layout(0, 0, 0, 0);
        Assert.assertEquals((16 + 1), btn.getLeft());
        Assert.assertEquals((14 + 2), btn.getTop());
    }

    @Test
    public void MoveChildToCenter() {
        final FlowLayout layout = new FlowLayout(getApplicationContext());
        layout.setGravity(CENTER);
        final Button btn = new Button(activity);
        FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(30, 40);
        lp.setMargins(1, 2, 3, 4);
        btn.setLayoutParams(lp);
        layout.addView(btn);
        layout.measure(MeasureSpec.makeMeasureSpec(50, EXACTLY), MeasureSpec.makeMeasureSpec(60, EXACTLY));
        layout.layout(0, 0, 0, 0);
        Assert.assertEquals((8 + 1), btn.getLeft());
        Assert.assertEquals((7 + 2), btn.getTop());
    }

    @Test
    public void ChildLessThenSizeWithFillGravity_IncreaseChildSize() {
        final FlowLayout layout = new FlowLayout(getApplicationContext());
        layout.setGravity(FILL);
        final Button btn = new Button(activity);
        FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(30, 40);
        lp.setMargins(1, 2, 3, 4);
        btn.setLayoutParams(lp);
        layout.addView(btn);
        layout.measure(MeasureSpec.makeMeasureSpec(50, EXACTLY), MeasureSpec.makeMeasureSpec(60, EXACTLY));
        layout.layout(0, 0, 0, 0);
        Assert.assertEquals(46, btn.getWidth());
        Assert.assertEquals(54, btn.getHeight());
    }
}

