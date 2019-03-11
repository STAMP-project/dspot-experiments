package com.reactnativenavigation.viewcontrollers;


import com.reactnativenavigation.Activity;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.utils.Button;
import com.reactnativenavigation.utils.TitleBar;
import java.util.ArrayList;
import org.junit.Test;
import org.mockito.Mockito;


public class TitleBarTest extends BaseTest {
    private TitleBar uut;

    private Button leftButton;

    private Button textButton;

    private Button customButton;

    private Activity activity;

    @Test
    public void setButton_setsTextButton() {
        uut.setRightButtons(rightButtons(textButton));
        uut.setLeftButtons(leftButton(leftButton));
        assertThat(uut.getMenu().getItem(0).getTitle()).isEqualTo(textButton.text.get());
    }

    @Test
    public void setButton_setsCustomButton() {
        uut.setLeftButtons(leftButton(leftButton));
        uut.setRightButtons(rightButtons(customButton));
        ReactView btnView = ((ReactView) (uut.getMenu().getItem(0).getActionView()));
        assertThat(btnView.getComponentName()).isEqualTo(customButton.component.name.get());
    }

    @Test
    public void setRightButtons_emptyButtonsListClearsRightButtons() {
        uut.setLeftButtons(new ArrayList());
        uut.setRightButtons(rightButtons(customButton, textButton));
        uut.setLeftButtons(new ArrayList());
        uut.setRightButtons(new ArrayList());
        assertThat(uut.getMenu().size()).isEqualTo(0);
    }

    @Test
    public void setLeftButtons_emptyButtonsListClearsLeftButton() {
        uut.setLeftButtons(leftButton(leftButton));
        uut.setRightButtons(rightButtons(customButton));
        assertThat(uut.getNavigationIcon()).isNotNull();
        uut.setLeftButtons(new ArrayList());
        uut.setRightButtons(rightButtons(textButton));
        assertThat(uut.getNavigationIcon()).isNull();
    }

    @Test
    public void setRightButtons_buttonsAreAddedInReverseOrderToMatchOrderOnIOs() {
        uut.setLeftButtons(new ArrayList());
        uut.setRightButtons(rightButtons(textButton, customButton));
        assertThat(uut.getMenu().getItem(1).getTitle()).isEqualTo(textButton.text.get());
    }

    @Test
    public void setComponent_addsComponentToTitleBar() {
        View component = new View(activity);
        uut.setComponent(component);
        Mockito.verify(uut).addView(component);
    }

    @Test
    public void addView_overflowIsEnabledForButtonsContainer() {
        ActionMenuView buttonsContainer = Mockito.mock(ActionMenuView.class);
        uut.addView(buttonsContainer);
        Mockito.verify(buttonsContainer).setClipChildren(false);
    }

    @Test
    public void clear() {
        View title = new View(activity);
        uut.setComponent(title);
        Mockito.verify(uut).addView(title);
        uut.clear();
        assertThat(uut.getTitle()).isNullOrEmpty();
        assertThat(uut.getMenu().size()).isZero();
        assertThat(uut.getNavigationIcon()).isNull();
        Mockito.verify(uut).removeView(title);
    }
}

