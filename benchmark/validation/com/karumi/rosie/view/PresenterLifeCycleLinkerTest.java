/**
 * The MIT License (MIT) Copyright (c) 2014 karumi Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions: The above copyright notice and this permission
 * notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE
 * IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.karumi.rosie.view;


import RosiePresenter.View;
import com.karumi.rosie.doubles.AnyClassWithAPrivatePresenterAnnotated;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PresenterLifeCycleLinkerTest {
    @Mock
    RosiePresenter anyPresenter1;

    @Mock
    RosiePresenter anyPresenter2;

    @Mock
    View anyView;

    @Test
    public void shouldInitializePresentersLifecycleOnInitializeLifecycleIsCalled() {
        PresenterLifeCycleLinker presenterLifeCycleLinker = givenAPresenterLifecycleLinker();
        Object source = givenAnyClassWithAnAnnotatedPresenter(anyPresenter1);
        presenterLifeCycleLinker.initializeLifeCycle(source, anyView);
        Mockito.verify(anyPresenter1).initialize();
    }

    @Test
    public void shouldCallAllRegisteredPresentersAreCalledWhenInitializeIsCalled() {
        PresenterLifeCycleLinker presenterLifeCycleLinker = givenAPresenterLifecycleLinker(anyPresenter1, anyPresenter2);
        presenterLifeCycleLinker.initializePresenters();
        Mockito.verify(anyPresenter1).initialize();
        Mockito.verify(anyPresenter2).initialize();
    }

    @Test
    public void shouldCallAllRegisteredPresentersAreCalledWhenUpdateIsCalled() {
        PresenterLifeCycleLinker presenterLifeCycleLinker = givenAPresenterLifecycleLinker(anyPresenter1, anyPresenter2);
        presenterLifeCycleLinker.updatePresenters(anyView);
        Mockito.verify(anyPresenter1).update();
        Mockito.verify(anyPresenter2).update();
    }

    @Test
    public void shouldCallAllRegisteredPresentersAreCalledWhenDestroyIsCalled() {
        PresenterLifeCycleLinker presenterLifeCycleLinker = givenAPresenterLifecycleLinker(anyPresenter1, anyPresenter2);
        presenterLifeCycleLinker.destroyPresenters();
        Mockito.verify(anyPresenter1).destroy();
        Mockito.verify(anyPresenter2).destroy();
    }

    @Test
    public void shouldCallAllRegisteredPresentersAreCalledWhenPauseIsCalled() {
        PresenterLifeCycleLinker presenterLifeCycleLinker = givenAPresenterLifecycleLinker(anyPresenter1, anyPresenter2);
        presenterLifeCycleLinker.pausePresenters();
        Mockito.verify(anyPresenter1).pause();
        Mockito.verify(anyPresenter2).pause();
    }

    @Test
    public void shouldUpdateThePresentersViewWhenUpdateIsCalled() {
        RosiePresenter presenter = givenAnyPresenter();
        PresenterLifeCycleLinker presenterLifeCycleLinker = givenAPresenterLifecycleLinker(presenter);
        presenterLifeCycleLinker.updatePresenters(anyView);
        Assert.assertEquals(anyView, presenter.getView());
    }

    @Test
    public void shouldUpdateThePresentersViewWhenSetViewIsCalled() {
        RosiePresenter presenter = givenAnyPresenter();
        PresenterLifeCycleLinker presenterLifeCycleLinker = givenAPresenterLifecycleLinker(presenter);
        presenterLifeCycleLinker.setView(anyView);
        Assert.assertEquals(anyView, presenter.getView());
    }

    @Test
    public void shouldResetPresenterViewOnPause() {
        RosiePresenter presenter = givenAnyPresenter();
        PresenterLifeCycleLinker presenterLifeCycleLinker = givenAPresenterLifecycleLinker(presenter);
        presenterLifeCycleLinker.updatePresenters(anyView);
        presenterLifeCycleLinker.pausePresenters();
        Assert.assertNotEquals(anyView, presenter.getView());
    }

    @Test(expected = PresenterNotAccessibleException.class)
    public void shouldThrowExceptionIfTheAnnotatedPresenterVisibilityIsPrivate() {
        AnyClassWithAPrivatePresenterAnnotated source = new AnyClassWithAPrivatePresenterAnnotated();
        PresenterLifeCycleLinker presenterLifeCycleLinker = givenAPresenterLifecycleLinker();
        presenterLifeCycleLinker.initializeLifeCycle(source, anyView);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullSourcesToInitializeTheLifeCycle() {
        PresenterLifeCycleLinker presenterLifeCycleLinker = givenAPresenterLifecycleLinker();
        presenterLifeCycleLinker.initializeLifeCycle(null, anyView);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullViewsToInitializeTheLifeCycle() {
        PresenterLifeCycleLinker presenterLifeCycleLinker = givenAPresenterLifecycleLinker();
        Object source = givenAnyClassWithAnAnnotatedPresenter(anyPresenter1);
        presenterLifeCycleLinker.initializeLifeCycle(source, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullViewsToConfigureThePresenters() {
        PresenterLifeCycleLinker presenterLifeCycleLinker = givenAPresenterLifecycleLinker();
        presenterLifeCycleLinker.setView(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullPresentersToRegister() {
        PresenterLifeCycleLinker presenterLifeCycleLinker = givenAPresenterLifecycleLinker();
        presenterLifeCycleLinker.registerPresenter(null);
    }
}

