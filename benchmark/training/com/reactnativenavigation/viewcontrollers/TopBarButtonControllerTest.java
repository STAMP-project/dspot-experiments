package com.reactnativenavigation.viewcontrollers;


import Color.BLACK;
import Color.LTGRAY;
import Color.RED;
import Typeface.MONOSPACE;
import android.graphics.Color;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.parse.params.Button;
import com.reactnativenavigation.parse.params.Number;
import com.reactnativenavigation.utils.ButtonPresenter;
import com.reactnativenavigation.viewcontrollers.stack.StackController;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@SuppressWarnings("MagicNumber")
public class TopBarButtonControllerTest extends BaseTest {
    private TitleBarButtonController uut;

    private StackController stackController;

    private Button button;

    private ButtonPresenter optionsPresenter;

    @Test
    public void buttonDoesNotClearStackOptionsOnAppear() {
        setReactComponentButton();
        uut.ensureViewIsCreated();
        uut.onViewAppeared();
        Mockito.verify(stackController, Mockito.times(0)).clearOptions();
    }

    @Test
    public void setIconColor_enabled() {
        setIconButton(true);
        uut.addToMenu(getTitleBar(), 0);
        assertThat(getTitleBar().getMenu().size()).isOne();
        Mockito.verify(optionsPresenter, Mockito.times(1)).tint(ArgumentMatchers.any(), ArgumentMatchers.eq(RED));
    }

    @Test
    public void setIconColor_disabled() {
        setIconButton(false);
        uut.addToMenu(getTitleBar(), 0);
        Mockito.verify(optionsPresenter, Mockito.times(1)).tint(ArgumentMatchers.any(), ArgumentMatchers.eq(LTGRAY));
    }

    @Test
    public void setIconColor_disabledColor() {
        setIconButton(false);
        button.disabledColor = new com.reactnativenavigation.parse.params.Colour(Color.BLACK);
        uut.addToMenu(getTitleBar(), 0);
        Mockito.verify(optionsPresenter, Mockito.times(1)).tint(ArgumentMatchers.any(), ArgumentMatchers.eq(BLACK));
    }

    @Test
    public void disableIconTint() {
        setIconButton(false);
        button.disableIconTint = new Bool(true);
        uut.addToMenu(getTitleBar(), 0);
        Mockito.verify(optionsPresenter, Mockito.times(0)).tint(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }

    @Test
    public void fontFamily() {
        setTextButton();
        uut.addToMenu(getTitleBar(), 0);
        Mockito.verify(optionsPresenter, Mockito.times(1)).setTypeFace(MONOSPACE);
    }

    @Test
    public void fontSize() {
        setTextButton();
        uut.addToMenu(getTitleBar(), 0);
        Mockito.verify(optionsPresenter, Mockito.times(0)).setFontSize(getTitleBar().getMenu().getItem(0));
        clearMenu();
        button.fontSize = new Number(10);
        uut.addToMenu(getTitleBar(), 0);
        Mockito.verify(optionsPresenter, Mockito.times(1)).setFontSize(getTitleBar().getMenu().getItem(0));
    }

    @Test
    public void textColor_enabled() {
        setTextButton();
        button.enabled = new Bool(false);
        uut.addToMenu(getTitleBar(), 0);
        dispatchPreDraw(getTitleBar());
        Mockito.verify(optionsPresenter, Mockito.times(0)).setEnabledColor(ArgumentMatchers.any());
        clearMenu();
        button.enabled = new Bool(true);
        button.color = new com.reactnativenavigation.parse.params.Colour(Color.RED);
        uut.addToMenu(getTitleBar(), 0);
        dispatchPreDraw(getTitleBar());
        Mockito.verify(optionsPresenter, Mockito.times(1)).setEnabledColor(ArgumentMatchers.any());
    }

    @Test
    public void textColor_disabled() {
        setTextButton();
        button.enabled = new Bool(false);
        uut.addToMenu(getTitleBar(), 0);
        dispatchPreDraw(getTitleBar());
        Mockito.verify(optionsPresenter, Mockito.times(1)).setDisabledColor(ArgumentMatchers.any(), ArgumentMatchers.eq(LTGRAY));
        clearMenu();
        button.disabledColor = new com.reactnativenavigation.parse.params.Colour(Color.BLACK);
        uut.addToMenu(getTitleBar(), 0);
        dispatchPreDraw(getTitleBar());
        Mockito.verify(optionsPresenter, Mockito.times(1)).setDisabledColor(ArgumentMatchers.any(), ArgumentMatchers.eq(BLACK));
    }
}

