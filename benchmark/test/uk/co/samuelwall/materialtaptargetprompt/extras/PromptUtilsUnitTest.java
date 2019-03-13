/**
 * Copyright (C) 2017 Samuel Wall
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.samuelwall.materialtaptargetprompt.extras;


import Build.VERSION;
import Build.VERSION_CODES;
import Build.VERSION_CODES.HONEYCOMB_MR2;
import Build.VERSION_CODES.JELLY_BEAN;
import Build.VERSION_CODES.JELLY_BEAN_MR1;
import Gravity.CENTER_HORIZONTAL;
import Gravity.END;
import Gravity.LEFT;
import Gravity.RIGHT;
import Gravity.START;
import Layout.Alignment.ALIGN_CENTER;
import Layout.Alignment.ALIGN_NORMAL;
import Layout.Alignment.ALIGN_OPPOSITE;
import PorterDuff.Mode.ADD;
import PorterDuff.Mode.MULTIPLY;
import PorterDuff.Mode.SCREEN;
import PorterDuff.Mode.SRC_ATOP;
import PorterDuff.Mode.SRC_IN;
import PorterDuff.Mode.SRC_OVER;
import Typeface.BOLD;
import Typeface.BOLD_ITALIC;
import Typeface.DEFAULT;
import Typeface.ITALIC;
import Typeface.MONOSPACE;
import Typeface.NORMAL;
import View.LAYOUT_DIRECTION_LTR;
import View.LAYOUT_DIRECTION_RTL;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import uk.co.samuelwall.materialtaptargetprompt.BuildConfig;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class PromptUtilsUnitTest {
    @Test
    public void testParseTintMode() {
        Assert.assertEquals(PromptUtils.parseTintMode((-1), null), null);
        Assert.assertEquals(PromptUtils.parseTintMode(3, null), SRC_OVER);
        Assert.assertEquals(PromptUtils.parseTintMode(5, null), SRC_IN);
        Assert.assertEquals(PromptUtils.parseTintMode(9, null), SRC_ATOP);
        Assert.assertEquals(PromptUtils.parseTintMode(14, null), MULTIPLY);
        Assert.assertEquals(PromptUtils.parseTintMode(15, null), SCREEN);
        Assert.assertEquals(PromptUtils.parseTintMode(16, null), ADD);
    }

    @Test
    public void testSetTypeface() {
        TextPaint textPaint = new TextPaint();
        PromptUtils.setTypeface(textPaint, MONOSPACE, 0);
        Assert.assertEquals(MONOSPACE, textPaint.getTypeface());
        PromptUtils.setTypeface(textPaint, null, NORMAL);
        Assert.assertEquals(DEFAULT, textPaint.getTypeface());
        PromptUtils.setTypeface(textPaint, MONOSPACE, NORMAL);
        Assert.assertEquals(NORMAL, textPaint.getTypeface().getStyle());
        PromptUtils.setTypeface(textPaint, null, BOLD);
        Assert.assertEquals(BOLD, textPaint.getTypeface().getStyle());
        PromptUtils.setTypeface(textPaint, MONOSPACE, BOLD);
        Assert.assertEquals(BOLD, textPaint.getTypeface().getStyle());
        PromptUtils.setTypeface(textPaint, null, ITALIC);
        Assert.assertEquals(ITALIC, textPaint.getTypeface().getStyle());
        PromptUtils.setTypeface(textPaint, MONOSPACE, ITALIC);
        Assert.assertEquals(ITALIC, textPaint.getTypeface().getStyle());
        PromptUtils.setTypeface(textPaint, null, BOLD_ITALIC);
        Assert.assertEquals(BOLD_ITALIC, textPaint.getTypeface().getStyle());
        PromptUtils.setTypeface(textPaint, MONOSPACE, BOLD_ITALIC);
        Assert.assertEquals(BOLD_ITALIC, textPaint.getTypeface().getStyle());
        PromptUtils.setTypeface(textPaint, Typeface.defaultFromStyle(BOLD), BOLD_ITALIC);
        Assert.assertEquals(BOLD_ITALIC, textPaint.getTypeface().getStyle());
        PromptUtils.setTypeface(textPaint, Typeface.defaultFromStyle(BOLD), ITALIC);
        Assert.assertEquals(ITALIC, textPaint.getTypeface().getStyle());
        PromptUtils.setTypeface(textPaint, Typeface.defaultFromStyle(BOLD), BOLD);
        Assert.assertEquals(BOLD, textPaint.getTypeface().getStyle());
        PromptUtils.setTypeface(textPaint, Typeface.defaultFromStyle(ITALIC), ITALIC);
        Assert.assertEquals(ITALIC, textPaint.getTypeface().getStyle());
        PromptUtils.setTypeface(textPaint, Typeface.defaultFromStyle(ITALIC), BOLD);
        Assert.assertEquals(BOLD, textPaint.getTypeface().getStyle());
    }

    @Test
    public void testSetTypefaceFromAttrs() {
        Typeface typeface = PromptUtils.setTypefaceFromAttrs("Arial", 0, NORMAL);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(NORMAL, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs("Arial", 0, BOLD);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(BOLD, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs("Arial", 0, ITALIC);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(ITALIC, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs("Arial", 0, BOLD_ITALIC);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(BOLD_ITALIC, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs(null, 1, NORMAL);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(NORMAL, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs(null, 1, BOLD);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(BOLD, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs(null, 1, ITALIC);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(ITALIC, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs(null, 1, BOLD_ITALIC);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(BOLD_ITALIC, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs(null, 2, NORMAL);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(NORMAL, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs(null, 2, BOLD);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(BOLD, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs(null, 2, ITALIC);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(ITALIC, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs(null, 2, BOLD_ITALIC);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(BOLD_ITALIC, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs(null, 3, NORMAL);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(NORMAL, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs(null, 3, BOLD);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(BOLD, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs(null, 3, ITALIC);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(ITALIC, typeface.getStyle());
        typeface = PromptUtils.setTypefaceFromAttrs(null, 3, BOLD_ITALIC);
        Assert.assertNotNull(typeface);
        Assert.assertEquals(BOLD_ITALIC, typeface.getStyle());
    }

    @Test
    public void testScaleOriginIsNotCentre() {
        final RectF base = new RectF(0, 672, 841, 1508);
        final PointF origin = new PointF(540, 924);
        final RectF[] evenExpectedResults = new RectF[]{ new RectF(540.0F, 924.0F, 540.0F, 924.0F)// 0
        , new RectF(486.0F, 898.8F, 570.1F, 982.4F)// 0.1
        , new RectF(432.0F, 873.6F, 600.2F, 1040.8F)// 0.2
        , new RectF(378.0F, 848.4F, 630.3F, 1099.2F)// 0.3
        , new RectF(324.0F, 823.2F, 660.4F, 1157.6F)// 0.4
        , new RectF(270.0F, 798.0F, 690.5F, 1216.0F)// 0.5
        , new RectF(216.0F, 772.8F, 720.6F, 1274.4F)// 0.6
        , new RectF(162.0F, 747.6F, 750.7F, 1332.8F)// 0.7
        , new RectF(108.0F, 722.4F, 780.8F, 1391.2F)// 0.8
        , new RectF(54.0F, 697.2F, 810.9F, 1449.6F)// 0.9
        , new RectF(0.0F, 672.0F, 841.0F, 1508.0F)// 1
        , new RectF((-41.8F), 630.2F, 882.8F, 1549.8F)// 1.1
        , new RectF((-83.6F), 588.4F, 924.6F, 1591.6F)// 1.2
        , new RectF((-125.4F), 546.6F, 966.4F, 1633.4F)// 1.3
        , new RectF((-167.2F), 504.8F, 1008.2F, 1675.2F)// 1.4
        , new RectF((-209.0F), 463.0F, 1050.0F, 1717.0F)// 1.5
        , new RectF((-250.8F), 421.2F, 1091.8F, 1758.8F)// 1.6
        , new RectF((-292.6F), 379.4F, 1133.6F, 1800.6F)// 1.7
        , new RectF((-334.4F), 337.6F, 1175.4F, 1842.4F)// 1.8
        , new RectF((-376.2F), 295.8F, 1217.2F, 1884.2F)// 1.9
        , new RectF((-418.0F), 254.0F, 1259.0F, 1926.0F)// 2
         };
        for (int i = 0, count = evenExpectedResults.length; i < count; i++) {
            doScaleTest(origin, base, (((float) (i)) / 10), true, evenExpectedResults[i]);
        }
        final RectF[] expectedResults = new RectF[]{ new RectF(540.0F, 924.0F, 540.0F, 924.0F)// 0
        , new RectF(486.0F, 898.8F, 570.1F, 982.4F)// 0.1
        , new RectF(432.0F, 873.6F, 600.2F, 1040.8F)// 0.2
        , new RectF(378.0F, 848.4F, 630.3F, 1099.2F)// 0.3
        , new RectF(324.0F, 823.2F, 660.4F, 1157.6F)// 0.4
        , new RectF(270.0F, 798.0F, 690.5F, 1216.0F)// 0.5
        , new RectF(216.0F, 772.8F, 720.6F, 1274.4F)// 0.6
        , new RectF(162.0F, 747.6F, 750.7F, 1332.8F)// 0.7
        , new RectF(108.0F, 722.4F, 780.8F, 1391.2F)// 0.8
        , new RectF(54.0F, 697.2F, 810.9F, 1449.6F)// 0.9
        , new RectF(0.0F, 672.0F, 841.0F, 1508.0F)// 1
        , new RectF((-54.0F), 646.8F, 871.1F, 1566.4F)// 1.1
        , new RectF((-108.0F), 621.6F, 901.2F, 1624.8F)// 1.2
        , new RectF((-162.0F), 596.4F, 931.3F, 1683.2F)// 1.3
        , new RectF((-216.0F), 571.2F, 961.4F, 1741.6F)// 1.4
        , new RectF((-270.0F), 546.0F, 991.5F, 1800.0F)// 1.5
        , new RectF((-324.0F), 520.8F, 1021.6F, 1858.4F)// 1.6
        , new RectF((-378.0F), 495.6F, 1051.7F, 1916.8F)// 1.7
        , new RectF((-432.0F), 470.4F, 1081.8F, 1975.2F)// 1.8
        , new RectF((-486.0F), 445.2F, 1111.9F, 2033.6F)// 1.9
        , new RectF((-540.0F), 420.0F, 1142.0F, 2092.0F)// 2
         };
        for (int i = 0, count = expectedResults.length; i < count; i++) {
            doScaleTest(origin, base, (((float) (i)) / 10), false, expectedResults[i]);
        }
    }

    @Test
    public void testScaleOriginIsCentre() {
        final RectF base = new RectF(8, 1528, 508, 1752);
        final PointF origin = new PointF(258, 1640);
        final RectF[] evenExpectedResults = new RectF[]{ new RectF(258.0F, 1640.0F, 258.0F, 1640.0F)// 0
        , new RectF(233.0F, 1628.8F, 283.0F, 1651.2F)// 0.1
        , new RectF(208.0F, 1617.6F, 308.0F, 1662.4F)// 0.2
        , new RectF(183.0F, 1606.4F, 333.0F, 1673.6F)// 0.3
        , new RectF(158.0F, 1595.2F, 358.0F, 1684.8F)// 0.4
        , new RectF(133.0F, 1584.0F, 383.0F, 1696.0F)// 0.5
        , new RectF(108.0F, 1572.8F, 408.0F, 1707.2F)// 0.6
        , new RectF(83.0F, 1561.6F, 433.0F, 1718.4F)// 0.7
        , new RectF(58.0F, 1550.4F, 458.0F, 1729.6F)// 0.8
        , new RectF(33.0F, 1539.2F, 483.0F, 1740.8F)// 0.9
        , new RectF(8, 1528, 508, 1752)// 1
        , new RectF((-3.2F), 1516.8F, 519.2F, 1763.2F)// 1.1
        , new RectF((-14.4F), 1505.6F, 530.4F, 1774.4F)// 1.2
        , new RectF((-25.6F), 1494.4F, 541.6F, 1785.6F)// 1.3
        , new RectF((-36.8F), 1483.2F, 552.8F, 1796.8F)// 1.4
        , new RectF((-48.0F), 1472.0F, 564.0F, 1808.0F)// 1.5
        , new RectF((-59.2F), 1460.8F, 575.2F, 1819.2F)// 1.6
        , new RectF((-70.4F), 1449.6F, 586.4F, 1830.4F)// 1.7
        , new RectF((-81.6F), 1438.4F, 597.6F, 1841.6F)// 1.8
        , new RectF((-92.8F), 1427.2F, 608.8F, 1852.8F)// 1.9
        , new RectF((-104.0F), 1416.0F, 620.0F, 1864.0F)// 2
         };
        for (int i = 0, count = evenExpectedResults.length; i < count; i++) {
            doScaleTest(origin, base, (((float) (i)) / 10), true, evenExpectedResults[i]);
        }
        final RectF[] expectedResults = new RectF[]{ new RectF(258.0F, 1640.0F, 258.0F, 1640.0F)// 0
        , new RectF(233.0F, 1628.8F, 283.0F, 1651.2F)// 0.1
        , new RectF(208.0F, 1617.6F, 308.0F, 1662.4F)// 0.2
        , new RectF(183.0F, 1606.4F, 333.0F, 1673.6F)// 0.3
        , new RectF(158.0F, 1595.2F, 358.0F, 1684.8F)// 0.4
        , new RectF(133.0F, 1584.0F, 383.0F, 1696.0F)// 0.5
        , new RectF(108.0F, 1572.8F, 408.0F, 1707.2F)// 0.6
        , new RectF(83.0F, 1561.6F, 433.0F, 1718.4F)// 0.7
        , new RectF(58.0F, 1550.4F, 458.0F, 1729.6F)// 0.8
        , new RectF(33.0F, 1539.2F, 483.0F, 1740.8F)// 0.9
        , new RectF(8, 1528, 508, 1752)// 1
        , new RectF((-17.0F), 1516.8F, 533.0F, 1763.2F)// 1.1
        , new RectF((-42.0F), 1505.6F, 558.0F, 1774.4F)// 1.2
        , new RectF((-67.0F), 1494.4F, 583.0F, 1785.6F)// 1.3
        , new RectF((-92.0F), 1483.2F, 608.0F, 1796.8F)// 1.4
        , new RectF((-117.0F), 1472.0F, 633.0F, 1808.0F)// 1.5
        , new RectF((-142.0F), 1460.8F, 658.0F, 1819.2F)// 1.6
        , new RectF((-167.0F), 1449.6F, 683.0F, 1830.4F)// 1.7
        , new RectF((-192.0F), 1438.4F, 708.0F, 1841.6F)// 1.8
        , new RectF((-217.0F), 1427.2F, 733.0F, 1852.8F)// 1.9
        , new RectF((-242.0F), 1416.0F, 758.0F, 1864.0F)// 2
         };
        for (int i = 0, count = expectedResults.length; i < count; i++) {
            doScaleTest(origin, base, (((float) (i)) / 10), false, expectedResults[i]);
        }
    }

    @SuppressLint("RtlHardcoded")
    @Test
    public void testGetTextAlignment() {
        final Resources resources = Resources.getSystem();
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, START, null));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, LEFT, null));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, START, "abc"));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, LEFT, "abc"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, END, "abc"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, RIGHT, "abc"));
        Assert.assertEquals(ALIGN_CENTER, PromptUtils.getTextAlignment(resources, CENTER_HORIZONTAL, "abc"));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, START, "???"));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, LEFT, "???"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, END, "???"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, RIGHT, "???"));
        Assert.assertEquals(ALIGN_CENTER, PromptUtils.getTextAlignment(resources, CENTER_HORIZONTAL, "???"));
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", 16);
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, START, "abc"));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, LEFT, "abc"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, END, "abc"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, RIGHT, "abc"));
        Assert.assertEquals(ALIGN_CENTER, PromptUtils.getTextAlignment(resources, CENTER_HORIZONTAL, "abc"));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, START, "???"));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, LEFT, "???"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, END, "???"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, RIGHT, "???"));
        Assert.assertEquals(ALIGN_CENTER, PromptUtils.getTextAlignment(resources, CENTER_HORIZONTAL, "???"));
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("RtlHardcoded")
    @Test
    public void testGetTextAlignmentRtl() {
        final Resources resources = Mockito.mock(Resources.class);
        final Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(resources.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getLayoutDirection()).thenReturn(LAYOUT_DIRECTION_RTL);
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, START, "abc"));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, LEFT, "abc"));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, END, "abc"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, RIGHT, "abc"));
        Assert.assertEquals(ALIGN_CENTER, PromptUtils.getTextAlignment(resources, CENTER_HORIZONTAL, "abc"));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, START, "???"));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, LEFT, "???"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, END, "???"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, RIGHT, "???"));
        Assert.assertEquals(ALIGN_CENTER, PromptUtils.getTextAlignment(resources, CENTER_HORIZONTAL, "???"));
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", 16);
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, START, "abc"));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, LEFT, "abc"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, END, "abc"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, RIGHT, "abc"));
        Assert.assertEquals(ALIGN_CENTER, PromptUtils.getTextAlignment(resources, CENTER_HORIZONTAL, "abc"));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, START, "???"));
        Assert.assertEquals(ALIGN_NORMAL, PromptUtils.getTextAlignment(resources, LEFT, "???"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, END, "???"));
        Assert.assertEquals(ALIGN_OPPOSITE, PromptUtils.getTextAlignment(resources, RIGHT, "???"));
        Assert.assertEquals(ALIGN_CENTER, PromptUtils.getTextAlignment(resources, CENTER_HORIZONTAL, "???"));
    }

    @Test
    public void testIsRtlNullLayout() {
        Assert.assertFalse(PromptUtils.isRtlText(null, null));
    }

    @Test
    public void testIsRtlPreIceCreamSandwich() {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", HONEYCOMB_MR2);
        final Layout layout = Mockito.mock(Layout.class);
        Mockito.when(layout.getAlignment()).thenReturn(ALIGN_NORMAL);
        Assert.assertFalse(PromptUtils.isRtlText(layout, null));
    }

    @Test
    public void testIsRtlPreIceCreamSandwichOpposite() {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", HONEYCOMB_MR2);
        final Layout layout = Mockito.mock(Layout.class);
        Mockito.when(layout.getAlignment()).thenReturn(ALIGN_OPPOSITE);
        Assert.assertTrue(PromptUtils.isRtlText(layout, null));
    }

    @Test
    public void testIsRtlPreIceCreamSandwichCentre() {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", HONEYCOMB_MR2);
        final Layout layout = Mockito.mock(Layout.class);
        Mockito.when(layout.getAlignment()).thenReturn(ALIGN_CENTER);
        Assert.assertFalse(PromptUtils.isRtlText(layout, null));
    }

    @Test
    public void testIsRtlFirstCharacterRtl() {
        final Layout layout = Mockito.mock(Layout.class);
        Mockito.when(layout.isRtlCharAt(0)).thenReturn(true);
        Mockito.when(layout.getAlignment()).thenReturn(ALIGN_NORMAL);
        Assert.assertTrue(PromptUtils.isRtlText(layout, null));
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR1)
    @Test
    public void testIsRtlFirstCharacterNotRtl() {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", JELLY_BEAN_MR1);
        final Resources resources = Mockito.mock(Resources.class);
        final Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(resources.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getLayoutDirection()).thenReturn(LAYOUT_DIRECTION_LTR);
        final Layout layout = Mockito.mock(Layout.class);
        Mockito.when(layout.isRtlCharAt(0)).thenReturn(false);
        Mockito.when(layout.getAlignment()).thenReturn(ALIGN_NORMAL);
        Assert.assertFalse(PromptUtils.isRtlText(layout, resources));
    }

    @Test
    public void testIsRtlFirstCharacterRtlOpposite() {
        final Layout layout = Mockito.mock(Layout.class);
        Mockito.when(layout.isRtlCharAt(0)).thenReturn(true);
        Mockito.when(layout.getAlignment()).thenReturn(ALIGN_OPPOSITE);
        Assert.assertFalse(PromptUtils.isRtlText(layout, null));
    }

    @Test
    public void testIsRtlFirstCharacterNotRtlOpposite() {
        final Layout layout = Mockito.mock(Layout.class);
        Mockito.when(layout.isRtlCharAt(0)).thenReturn(false);
        Mockito.when(layout.getAlignment()).thenReturn(ALIGN_OPPOSITE);
        Assert.assertTrue(PromptUtils.isRtlText(layout, null));
    }

    @Test
    public void testIsRtlFirstCharacterNotRtlPreJellyBeanMR1() {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", JELLY_BEAN);
        final Layout layout = Mockito.mock(Layout.class);
        Mockito.when(layout.isRtlCharAt(0)).thenReturn(false);
        Mockito.when(layout.getAlignment()).thenReturn(ALIGN_NORMAL);
        Assert.assertFalse(PromptUtils.isRtlText(layout, null));
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR1)
    @Test
    public void testIsRtlFirstCharacterNotRtlOppositePreJellyBeanMR1() {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", JELLY_BEAN_MR1);
        final Resources resources = Mockito.mock(Resources.class);
        final Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(resources.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getLayoutDirection()).thenReturn(LAYOUT_DIRECTION_RTL);
        final Layout layout = Mockito.mock(Layout.class);
        Mockito.when(layout.isRtlCharAt(0)).thenReturn(false);
        Mockito.when(layout.getAlignment()).thenReturn(ALIGN_NORMAL);
        Assert.assertTrue(PromptUtils.isRtlText(layout, resources));
    }

    @Test
    public void testIsRtlFirstCharacterNotRtlNotOppositePreJellyBeanMR1() {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", JELLY_BEAN_MR1);
        final Layout layout = Mockito.mock(Layout.class);
        Mockito.when(layout.isRtlCharAt(0)).thenReturn(false);
        Mockito.when(layout.getAlignment()).thenReturn(ALIGN_CENTER);
        Assert.assertFalse(PromptUtils.isRtlText(layout, null));
    }

    @Test
    public void testIsPointInCircle() {
        Assert.assertTrue(PromptUtils.isPointInCircle(5, 5, new PointF(10, 10), 10));
    }

    @Test
    public void testIsPointInCircleOutside() {
        Assert.assertFalse(PromptUtils.isPointInCircle(1, 1, new PointF(10, 10), 10));
    }

    @Test
    public void testCalculateMaxTextWidth() {
        Layout layout = Mockito.mock(Layout.class);
        Mockito.when(layout.getLineCount()).thenReturn(3);
        Mockito.when(layout.getLineWidth(0)).thenReturn(10.0F);
        Mockito.when(layout.getLineWidth(1)).thenReturn(100.0F);
        Mockito.when(layout.getLineWidth(2)).thenReturn(50.0F);
        Assert.assertEquals(100.0F, PromptUtils.calculateMaxTextWidth(layout), 0);
    }

    @Test
    public void testCalculateMaxTextWidthNull() {
        Assert.assertEquals(0, PromptUtils.calculateMaxTextWidth(null), 0);
    }

    @Test
    public void testCalculateMaxWidthDefault() {
        Assert.assertEquals(260, PromptUtils.calculateMaxWidth(1000, new Rect(100, 0, 400, 0), 0, 20), 0);
    }

    @Test
    public void testCalculateMaxWidthDefaultWidth() {
        Assert.assertEquals(80, PromptUtils.calculateMaxWidth(0, null, 0, 0), 0);
    }

    @Test
    public void testCalculateMaxWidthNullBounds() {
        Assert.assertEquals(160, PromptUtils.calculateMaxWidth(1000, null, 200, 20), 0);
    }

    @Test
    public void testCreateStaticTextLayout() {
        final TextPaint paint = new TextPaint();
        final StaticLayout layout = PromptUtils.createStaticTextLayout("test", paint, 300, ALIGN_CENTER, 0.5F);
        Assert.assertNotNull(layout);
        Assert.assertEquals("test", layout.getText().toString());
        Assert.assertEquals(paint, layout.getPaint());
        Assert.assertEquals(ALIGN_CENTER, layout.getAlignment());
        Assert.assertEquals(300, layout.getWidth());
    }

    /* @Test
    @TargetApi(Build.VERSION_CODES.M)
    public void testCreateStaticTextLayoutMarshmallow()
    {
    ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", Build.VERSION_CODES.M);
    final TextPaint textPaint = new TextPaint();
    final StaticLayout layout = PromptUtils.createStaticTextLayout("test", textPaint, 300, Layout.Alignment.ALIGN_CENTER, 1);
    assertNotNull(layout);
    assertEquals(Layout.Alignment.ALIGN_CENTER, layout.getAlignment());
    assertEquals("test", layout.getText().toString());
    assertEquals(textPaint, layout.getPaint());
    assertEquals(300, layout.getWidth());
    }
     */
    @Test
    public void testContainsInset_Left() {
        Assert.assertFalse(PromptUtils.containsInset(new Rect(0, 0, 500, 500), 50, 20, 0));
    }

    @Test
    public void testContainsInset_Right() {
        Assert.assertFalse(PromptUtils.containsInset(new Rect(0, 0, 500, 500), 50, 460, 0));
    }

    @Test
    public void testContainsInset_Top() {
        Assert.assertFalse(PromptUtils.containsInset(new Rect(0, 0, 500, 500), 50, 60, 0));
    }

    @Test
    public void testContainsInset_Bottom() {
        Assert.assertFalse(PromptUtils.containsInset(new Rect(0, 0, 500, 500), 50, 60, 460));
    }

    @Test
    public void testContainsInset() {
        Assert.assertTrue(PromptUtils.containsInset(new Rect(0, 0, 500, 500), 50, 60, 60));
    }

    @Deprecated
    @Test
    public void testIsVersionAfterJellyBeanMR1() {
        Assert.assertTrue(PromptUtils.isVersionAfterJellyBeanMR1());
    }

    @Deprecated
    @Test
    public void testIsVersionBeforeJellyBeanMR1() {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", JELLY_BEAN);
        Assert.assertFalse(PromptUtils.isVersionAfterJellyBeanMR1());
    }
}

