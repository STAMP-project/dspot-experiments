package com.octo.android.sample.ui;


import R.id.button_main;
import R.id.textview_hello;
import R.string.app_name;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.TextView;
import com.octo.android.sample.model.Computer;
import org.boundbox.BoundBox;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class MyActivityTest {
    @Test
    public void shouldHaveApplicationName() throws Exception {
        // given
        HelloAndroidActivity activityUnderTest = Robolectric.buildActivity(HelloAndroidActivity.class).create().get();
        // when
        // then
        String appName = activityUnderTest.getResources().getString(app_name);
        Assert.assertThat(appName, CoreMatchers.equalTo("sonar-android-sample"));
    }

    @Test
    public void shouldNotUseNullComputer() throws Exception {
        // given
        HelloAndroidActivity activityUnderTest = Robolectric.buildActivity(HelloAndroidActivity.class).create().get();
        activityUnderTest.setComputer(null);
        // when
        Button button = ((Button) (activityUnderTest.findViewById(button_main)));
        button.performClick();
        // then
        TextView textViewHello = ((TextView) (activityUnderTest.findViewById(textview_hello)));
        String textViewHelloString = textViewHello.getText().toString();
        Assert.assertThat(textViewHelloString, CoreMatchers.equalTo("-"));
    }

    @Test
    public void shouldUseDummyComputer() throws Exception {
        final int EXPECTED_RESULT = 42;
        // given
        HelloAndroidActivity activityUnderTest = Robolectric.buildActivity(HelloAndroidActivity.class).create().get();
        // when
        Button button = ((Button) (activityUnderTest.findViewById(button_main)));
        button.performClick();
        // then
        TextView textViewHello = ((TextView) (activityUnderTest.findViewById(textview_hello)));
        String textViewHelloString = textViewHello.getText().toString();
        Assert.assertThat(textViewHelloString, CoreMatchers.equalTo(String.valueOf(EXPECTED_RESULT)));
    }

    @Test
    public void shouldUseCustomComputerUsingEasyMock() throws Exception {
        final int EXPECTED_RESULT = 1;
        // given
        HelloAndroidActivity activityUnderTest = Robolectric.buildActivity(HelloAndroidActivity.class).create().get();
        Computer mockComputer = EasyMock.createMock(Computer.class);
        EasyMock.expect(mockComputer.getResult()).andReturn(EXPECTED_RESULT);
        activityUnderTest.setComputer(mockComputer);
        EasyMock.replay(mockComputer);
        // when
        Button button = ((Button) (activityUnderTest.findViewById(button_main)));
        button.performClick();
        // then
        EasyMock.verify(mockComputer);
        TextView textViewHello = ((TextView) (activityUnderTest.findViewById(textview_hello)));
        String textViewHelloString = textViewHello.getText().toString();
        Assert.assertThat(textViewHelloString, CoreMatchers.equalTo(String.valueOf(EXPECTED_RESULT)));
    }

    @Test
    public void shouldUseCustomComputerUsingMockito() throws Exception {
        final int EXPECTED_RESULT = 1;
        // given
        HelloAndroidActivity activityUnderTest = Robolectric.buildActivity(HelloAndroidActivity.class).create().get();
        Computer mockComputer = Mockito.mock(Computer.class);
        Mockito.when(mockComputer.getResult()).thenReturn(EXPECTED_RESULT);
        activityUnderTest.setComputer(mockComputer);
        // when
        Button button = ((Button) (activityUnderTest.findViewById(button_main)));
        button.performClick();
        // then
        Mockito.verify(mockComputer, Mockito.times(1)).getResult();
        TextView textViewHello = ((TextView) (activityUnderTest.findViewById(textview_hello)));
        String textViewHelloString = textViewHello.getText().toString();
        Assert.assertThat(textViewHelloString, CoreMatchers.equalTo(String.valueOf(EXPECTED_RESULT)));
    }

    @BoundBox(boundClass = HelloAndroidActivity.class, maxSuperClass = FragmentActivity.class)
    @Test
    public void shouldUseCustomComputerUsingMockitoAndBoundBox() throws Exception {
        final int EXPECTED_RESULT = 1;
        // given
        HelloAndroidActivity activityUnderTest = Robolectric.buildActivity(HelloAndroidActivity.class).create().get();
        BoundBoxOfHelloAndroidActivity boundBoxOfHelloAndroidActivity = new BoundBoxOfHelloAndroidActivity(activityUnderTest);
        Computer mockComputer = Mockito.mock(Computer.class);
        Mockito.when(mockComputer.getResult()).thenReturn(EXPECTED_RESULT);
        boundBoxOfHelloAndroidActivity.setComputer(mockComputer);
        // when
        boundBoxOfHelloAndroidActivity.boundBox_getButton().performClick();
        // then
        Mockito.verify(mockComputer, Mockito.times(1)).getResult();
        String textViewHelloString = boundBoxOfHelloAndroidActivity.boundBox_getTextView().getText().toString();
        Assert.assertThat(textViewHelloString, CoreMatchers.equalTo(String.valueOf(EXPECTED_RESULT)));
    }
}

