package com.reactnativenavigation.viewcontrollers.button;


import View.LAYOUT_DIRECTION_LTR;
import android.content.Context;
import android.graphics.drawable.Drawable;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.utils.Functions.Func1;
import com.reactnativenavigation.utils.ImageLoader;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class NavigationIconResolverTest extends BaseTest {
    private static final String ICON_URI = "someIconUri";

    private NavigationIconResolver uut;

    private ImageLoader imageLoader;

    private Context context;

    @Test
    public void create_iconButton() {
        @SuppressWarnings("Convert2Lambda")
        Func1<Drawable> onSuccess = Mockito.spy(new Func1<Drawable>() {
            @Override
            public void run(Drawable icon) {
            }
        });
        uut.resolve(iconButton(), LAYOUT_DIRECTION_LTR, onSuccess);
        Mockito.verify(imageLoader).loadIcon(ArgumentMatchers.eq(context), ArgumentMatchers.eq(NavigationIconResolverTest.ICON_URI), ArgumentMatchers.any());
        Mockito.verify(onSuccess).run(ArgumentMatchers.any(Drawable.class));
    }

    @Test
    public void create_backButton() {
        @SuppressWarnings("Convert2Lambda")
        Func1<Drawable> onSuccess = Mockito.spy(new Func1<Drawable>() {
            @Override
            public void run(Drawable param) {
            }
        });
        uut.resolve(backButton(), LAYOUT_DIRECTION_LTR, onSuccess);
        Mockito.verifyZeroInteractions(imageLoader);
        Mockito.verify(onSuccess).run(ArgumentMatchers.any());
    }
}

