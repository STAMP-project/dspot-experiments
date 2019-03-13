package org.robolectric.shadows;


import AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS;
import AccessibilityNodeInfo.ACTION_CLICK;
import AccessibilityNodeInfo.ACTION_LONG_CLICK;
import AccessibilityNodeInfo.ACTION_PASTE;
import AccessibilityNodeInfo.ACTION_SET_SELECTION;
import AccessibilityNodeInfo.CREATOR;
import android.graphics.Rect;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Parcel;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowAccessibilityNodeInfoTest {
    private AccessibilityNodeInfo node;

    private ShadowAccessibilityNodeInfo shadow;

    @Test
    public void shouldHaveObtainedNode() {
        assertThat(ShadowAccessibilityNodeInfo.areThereUnrecycledNodes(false)).isEqualTo(true);
    }

    @Test
    public void shouldHaveZeroBounds() {
        Rect outBounds = new Rect();
        node.getBoundsInParent(outBounds);
        assertThat(outBounds.left).isEqualTo(0);
    }

    @Test
    public void shouldHaveClonedCorrectly() {
        node.setAccessibilityFocused(true);
        node.setBoundsInParent(new Rect(0, 0, 100, 100));
        node.setContentDescription("test");
        AccessibilityNodeInfo anotherNode = AccessibilityNodeInfo.obtain(node);
        assertThat(anotherNode).isEqualTo(node);
        assertThat(anotherNode.getContentDescription().toString()).isEqualTo("test");
    }

    @Test
    public void shouldWriteAndReadFromParcelCorrectly() {
        Parcel p = Parcel.obtain();
        node.setContentDescription("test");
        node.writeToParcel(p, 0);
        p.setDataPosition(0);
        AccessibilityNodeInfo anotherNode = CREATOR.createFromParcel(p);
        assertThat(node).isEqualTo(anotherNode);
        node.setContentDescription(null);
    }

    @Test
    public void shouldNotHaveInfiniteLoopWithSameLoopedChildren() {
        node = AccessibilityNodeInfo.obtain();
        AccessibilityNodeInfo child = AccessibilityNodeInfo.obtain();
        Shadows.shadowOf(node).addChild(child);
        Shadows.shadowOf(child).addChild(node);
        AccessibilityNodeInfo anotherNode = AccessibilityNodeInfo.obtain(node);
        assertThat(node).isEqualTo(anotherNode);
    }

    @Test
    public void shouldNotHaveInfiniteLoopWithDifferentLoopedChildren() {
        node = AccessibilityNodeInfo.obtain();
        shadow = Shadows.shadowOf(node);
        AccessibilityNodeInfo child1 = AccessibilityNodeInfo.obtain();
        shadow.addChild(child1);
        ShadowAccessibilityNodeInfo child1Shadow = Shadows.shadowOf(child1);
        child1Shadow.addChild(node);
        AccessibilityNodeInfo anotherNode = ShadowAccessibilityNodeInfo.obtain();
        AccessibilityNodeInfo child2 = ShadowAccessibilityNodeInfo.obtain();
        child2.setText("test");
        ShadowAccessibilityNodeInfo child2Shadow = Shadows.shadowOf(child2);
        ShadowAccessibilityNodeInfo anotherNodeShadow = Shadows.shadowOf(anotherNode);
        anotherNodeShadow.addChild(child2);
        child2Shadow.addChild(anotherNode);
        assertThat(node).isNotEqualTo(anotherNode);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void shouldRecordFlagsProperly() {
        node = AccessibilityNodeInfo.obtain();
        node.setClickable(false);
        shadow = Shadows.shadowOf(node);
        shadow.setPasteable(false);
        assertThat(shadow.isClickable()).isEqualTo(false);
        assertThat(shadow.isPasteable()).isEqualTo(false);
        node.setText("Test");
        shadow.setTextSelectionSetable(true);
        shadow.addAction(ACTION_SET_SELECTION);
        node.setTextSelection(0, 1);
        assertThat(shadow.getActions()).isEqualTo(ACTION_SET_SELECTION);
        assertThat(shadow.getTextSelectionStart()).isEqualTo(0);
        assertThat(shadow.getTextSelectionEnd()).isEqualTo(1);
        AccessibilityWindowInfo window = ShadowAccessibilityWindowInfo.obtain();
        shadow.setAccessibilityWindowInfo(window);
        assertThat(node.getWindow()).isEqualTo(window);
        shadow.setAccessibilityWindowInfo(null);
        // Remove action was added in API 21
        node.removeAction(AccessibilityAction.ACTION_SET_SELECTION);
        shadow.setPasteable(true);
        shadow.setTextSelectionSetable(false);
        node.addAction(ACTION_PASTE);
        assertThat(shadow.getActions()).isEqualTo(ACTION_PASTE);
        node.setClickable(true);
        assertThat(shadow.isClickable()).isEqualTo(true);
        node.setClickable(false);
        shadow.setPasteable(false);
        node.removeAction(ACTION_PASTE);
        node.addAction(ACTION_CLEAR_ACCESSIBILITY_FOCUS);
        assertThat(shadow.getActions()).isEqualTo(ACTION_CLEAR_ACCESSIBILITY_FOCUS);
        node.removeAction(ACTION_CLEAR_ACCESSIBILITY_FOCUS);
    }

    @Test
    public void shouldRecordActionsPerformed() {
        node.setClickable(true);
        node.addAction(ACTION_CLICK);
        shadow = Shadows.shadowOf(node);
        shadow.setOnPerformActionListener(new ShadowAccessibilityNodeInfo.OnPerformActionListener() {
            @Override
            public boolean onPerformAccessibilityAction(int action, Bundle arguments) {
                if (action == (AccessibilityNodeInfo.ACTION_CLICK)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        boolean clickResult = node.performAction(ACTION_CLICK);
        assertThat(clickResult).isEqualTo(true);
        assertThat(shadow.getPerformedActions().isEmpty()).isEqualTo(false);
        assertThat(shadow.getPerformedActions().get(0)).isEqualTo(ACTION_CLICK);
        boolean longClickResult = node.performAction(ACTION_LONG_CLICK);
        assertThat(longClickResult).isEqualTo(false);
        assertThat(shadow.getPerformedActions().size()).isEqualTo(2);
        assertThat(shadow.getPerformedActions().get(1)).isEqualTo(ACTION_LONG_CLICK);
    }

    @Test
    public void equalsTest_unrelatedNodesAreUnequal() {
        AccessibilityNodeInfo nodeA = AccessibilityNodeInfo.obtain();
        AccessibilityNodeInfo nodeB = AccessibilityNodeInfo.obtain();
        Shadows.shadowOf(nodeA).setText("test");
        Shadows.shadowOf(nodeB).setText("test");
        assertThat(nodeA).isNotEqualTo(nodeB);
    }

    @Test
    public void equalsTest_nodesFromTheSameViewAreEqual() {
        View view = new View(ApplicationProvider.getApplicationContext());
        AccessibilityNodeInfo nodeA = AccessibilityNodeInfo.obtain(view);
        AccessibilityNodeInfo nodeB = AccessibilityNodeInfo.obtain(view);
        Shadows.shadowOf(nodeA).setText("tomato");
        Shadows.shadowOf(nodeB).setText("tomatoe");
        assertThat(nodeA).isEqualTo(nodeB);
    }

    @Test
    public void equalsTest_nodesFromDifferentViewsAreNotEqual() {
        View viewA = new View(ApplicationProvider.getApplicationContext());
        View viewB = new View(ApplicationProvider.getApplicationContext());
        AccessibilityNodeInfo nodeA = AccessibilityNodeInfo.obtain(viewA);
        AccessibilityNodeInfo nodeB = AccessibilityNodeInfo.obtain(viewB);
        Shadows.shadowOf(nodeA).setText("test");
        Shadows.shadowOf(nodeB).setText("test");
        assertThat(nodeA).isNotEqualTo(nodeB);
    }

    @Test
    public void equalsTest_nodeIsEqualToItsClone_evenWhenModified() {
        node = AccessibilityNodeInfo.obtain();
        AccessibilityNodeInfo clone = AccessibilityNodeInfo.obtain(node);
        Shadows.shadowOf(clone).setText("test");
        assertThat(node).isEqualTo(clone);
    }
}

