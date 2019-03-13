package org.robolectric.android;


import ForRobolectricVersion.VERSION_3_0;
import ForRobolectricVersion.VERSION_3_1;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.apps.common.testing.accessibility.framework.integrations.AccessibilityViewCheckException;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.AccessibilityChecks;
import org.robolectric.annotation.AccessibilityChecks.ForRobolectricVersion;
import org.robolectric.util.TestRunnerWithManifest;


/**
 * Tests for accessibility checking. The checking relies on the Accessibility Test Framework for
 * Android, which has support-v4 as a dependency, so these tests are included where the presence
 * of that library is guaranteed.
 */
@RunWith(TestRunnerWithManifest.class)
public class AccessibilityUtilTest {
    private static final String DUPLICATE_STRING = "Duplicate";

    private TextView textViewWithClickableSpan;

    private LinearLayout parentLayout;

    private View labeledView;

    private View unlabeledView;

    @Test(expected = AccessibilityViewCheckException.class)
    public void checkUnlabeledView_shouldThrow() throws Exception {
        AccessibilityUtil.checkView(unlabeledView);
    }

    @Test
    public void checkOKView_shouldNotThrow() throws Exception {
        AccessibilityUtil.checkView(labeledView);
    }

    @Test
    public void default_viewWithSiblingIssue_shouldNotThrow() throws Exception {
        parentLayout.addView(unlabeledView);
        AccessibilityUtil.checkView(labeledView);
    }

    @Test(expected = AccessibilityViewCheckException.class)
    public void whenCheckingFromRoot_viewWithSiblingIssue_shouldThrow() throws Exception {
        parentLayout.addView(unlabeledView);
        AccessibilityUtil.setRunChecksFromRootView(true);
        AccessibilityUtil.checkView(labeledView);
    }

    @Test(expected = AccessibilityViewCheckException.class)
    @AccessibilityChecks
    public void whenAnnotationPresent_conditionalCheckRun() {
        AccessibilityUtil.checkViewIfCheckingEnabled(unlabeledView);
    }

    @Test
    public void whenAnnotationNotPresent_conditionalCheckNotRun() {
        AccessibilityUtil.checkViewIfCheckingEnabled(unlabeledView);
    }

    @Test(expected = AccessibilityViewCheckException.class)
    public void framework2pt0Error_byDefault_shouldThrow() throws Exception {
        AccessibilityUtil.checkView(textViewWithClickableSpan);
    }

    @Test
    public void framework2pt0Error_whenCheckingForRL3pt0_shouldNotThrow() throws Exception {
        AccessibilityUtil.setRunChecksForRobolectricVersion(VERSION_3_0);
        AccessibilityUtil.checkView(textViewWithClickableSpan);
    }

    @Test
    @AccessibilityChecks(forRobolectricVersion = ForRobolectricVersion.VERSION_3_0)
    public void framework2pt0Error_annotationForRL3pt0_shouldNotThrow() throws Exception {
        AccessibilityUtil.checkView(textViewWithClickableSpan);
    }

    @Test(expected = AccessibilityViewCheckException.class)
    @AccessibilityChecks(forRobolectricVersion = ForRobolectricVersion.VERSION_3_0)
    public void framework2pt0Error_codeForcesRL3pt1_shouldThrow() throws Exception {
        AccessibilityUtil.setRunChecksForRobolectricVersion(VERSION_3_1);
        AccessibilityUtil.checkView(textViewWithClickableSpan);
    }

    @Test
    public void whenSuppressingResults_shouldNotThrow() throws Exception {
        AccessibilityUtil.setSuppressingResultMatcher(Matchers.anything());
        AccessibilityUtil.checkView(unlabeledView);
    }

    @Test
    public void whenOnlyPrintingResults_shouldNotThrow() throws Exception {
        AccessibilityUtil.setThrowExceptionForErrors(false);
        AccessibilityUtil.checkView(unlabeledView);
    }

    @Test
    public void warningIssue_shouldNotThrow() throws Exception {
        labeledView.setContentDescription(AccessibilityUtilTest.DUPLICATE_STRING);
        parentLayout.setContentDescription(AccessibilityUtilTest.DUPLICATE_STRING);
        parentLayout.setClickable(true);
        AccessibilityUtil.checkView(parentLayout);
    }
}

