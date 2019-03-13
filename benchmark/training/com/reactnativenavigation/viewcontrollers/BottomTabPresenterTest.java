package com.reactnativenavigation.viewcontrollers;


import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.presentation.BottomTabPresenter;
import com.reactnativenavigation.views.BottomTabs;
import com.reactnativenavigation.views.Component;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class BottomTabPresenterTest extends BaseTest {
    private Options tab1Options = createTab1Options();

    private Options tab2Options = createTab2Options();

    private BottomTabPresenter uut;

    private BottomTabs bottomTabs;

    private List<ViewController> tabs;

    private ViewController child3;

    @Test
    public void present() {
        uut.applyOptions();
        for (int i = 0; i < (tabs.size()); i++) {
            Mockito.verify(bottomTabs, Mockito.times(1)).setBadge(i, tabs.get(i).options.bottomTabOptions.badge.get(""));
            Mockito.verify(bottomTabs, Mockito.times(1)).setTitleInactiveColor(i, tabs.get(i).options.bottomTabOptions.textColor.get(null));
            Mockito.verify(bottomTabs, Mockito.times(1)).setTitleActiveColor(i, tabs.get(i).options.bottomTabOptions.selectedTextColor.get(null));
        }
    }

    @Test
    public void mergeChildOptions() {
        for (int i = 0; i < 2; i++) {
            Options options = tabs.get(i).options;
            uut.mergeChildOptions(options, ((Component) (tabs.get(i).getView())));
            Mockito.verify(bottomTabs, Mockito.times(1)).setBadge(i, options.bottomTabOptions.badge.get());
            Mockito.verify(bottomTabs, Mockito.times(1)).setIconActiveColor(ArgumentMatchers.eq(i), ArgumentMatchers.anyInt());
            Mockito.verify(bottomTabs, Mockito.times(1)).setIconInactiveColor(ArgumentMatchers.eq(i), ArgumentMatchers.anyInt());
        }
        Mockito.verifyNoMoreInteractions(bottomTabs);
    }

    @Test
    public void mergeChildOptions_onlySetsDefinedOptions() {
        uut.mergeChildOptions(child3.options, ((Component) (child3.getView())));
        Mockito.verify(bottomTabs, Mockito.times(0)).setBadge(ArgumentMatchers.eq(2), ArgumentMatchers.anyString());
        Mockito.verify(bottomTabs, Mockito.times(0)).setIconInactiveColor(ArgumentMatchers.eq(2), ArgumentMatchers.anyInt());
        Mockito.verify(bottomTabs, Mockito.times(0)).setIconActiveColor(ArgumentMatchers.eq(2), ArgumentMatchers.anyInt());
        Mockito.verifyNoMoreInteractions(bottomTabs);
    }
}

