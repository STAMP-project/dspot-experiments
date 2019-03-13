package com.reactnativenavigation.viewcontrollers;


import android.view.View;
import android.view.ViewGroup;
import com.reactnativenavigation.BaseTest;
import org.junit.Test;
import org.mockito.Mockito;


public class YellowBoxDelegateTest extends BaseTest {
    private YellowBoxDelegate uut;

    private YellowBoxHelper yellowBoxHelper;

    private View yellowBox;

    private ViewGroup parent;

    @Test
    public void onYellowBoxAdded_removedFromParent() {
        uut.onYellowBoxAdded(parent);
        assertThat(yellowBox.getParent()).isNull();
    }

    @Test
    public void onYellowBoxAdded_storesRefToYellowBoxAndParent() {
        uut.onYellowBoxAdded(parent);
        assertThat(uut.getYellowBoxes()).contains(yellowBox);
        assertThat(uut.getParent()).isEqualTo(parent);
    }

    @Test
    public void onReactViewDestroy_yellowBoxIsAddedBackToParent() {
        uut.onYellowBoxAdded(parent);
        uut.destroy();
        assertThat(yellowBox.getParent()).isEqualTo(parent);
    }

    @Test
    public void onChildViewAdded() {
        uut.onChildViewAdded(parent, yellowBox);
        Mockito.verify(yellowBoxHelper).isYellowBox(parent, yellowBox);
    }

    @Test
    public void onYellowBoxAdded_notHandledIfDelegateIsDestroyed() {
        uut.onYellowBoxAdded(parent);
        uut.destroy();
        uut.onYellowBoxAdded(parent);
        assertThat(yellowBox.getParent()).isEqualTo(parent);
    }
}

