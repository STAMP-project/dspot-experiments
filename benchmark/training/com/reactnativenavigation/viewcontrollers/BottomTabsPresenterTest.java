package com.reactnativenavigation.viewcontrollers;


import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.parse.params.Colour;
import com.reactnativenavigation.presentation.BottomTabsPresenter;
import com.reactnativenavigation.views.BottomTabs;
import com.reactnativenavigation.views.Component;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


public class BottomTabsPresenterTest extends BaseTest {
    private List<ViewController> tabs;

    private BottomTabsPresenter uut;

    private BottomTabs bottomTabs;

    @Test
    public void mergeChildOptions_onlyDeclaredOptionsAreApplied() {
        // default options are not applies on merge
        Options defaultOptions = new Options();
        defaultOptions.bottomTabsOptions.visible = new Bool(false);
        uut.setDefaultOptions(defaultOptions);
        Options options = new Options();
        options.bottomTabsOptions.backgroundColor = new Colour(10);
        uut.mergeChildOptions(options, ((Component) (tabs.get(0).getView())));
        Mockito.verify(bottomTabs).setBackgroundColor(options.bottomTabsOptions.backgroundColor.get());
        Mockito.verifyNoMoreInteractions(bottomTabs);
    }
}

