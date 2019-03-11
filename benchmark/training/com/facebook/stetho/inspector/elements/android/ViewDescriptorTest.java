package com.facebook.stetho.inspector.elements.android;


import Build.VERSION_CODES;
import android.app.Activity;
import android.widget.CheckBox;
import android.widget.TextView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(emulateSdk = VERSION_CODES.JELLY_BEAN)
@RunWith(RobolectricTestRunner.class)
public class ViewDescriptorTest {
    private final MethodInvoker mMethodInvoker = Mockito.mock(MethodInvoker.class);

    private final ViewDescriptor mDescriptor = new ViewDescriptor(mMethodInvoker);

    private final Activity mActivity = Robolectric.setupActivity(Activity.class);

    private final TextView mTextView = new TextView(mActivity);

    private final CheckBox mCheckBox = new CheckBox(mActivity);

    @Test
    public void testSetAttributeAsTextWithSetText() {
        mDescriptor.setAttributesAsText(mTextView, "text=\"Hello World\"");
        Mockito.verify(mMethodInvoker).invoke(mTextView, "setText", "Hello World");
    }

    @Test
    public void testSetAttributeAsTextWithSetId() {
        mDescriptor.setAttributesAsText(mTextView, "id=\"2\"");
        Mockito.verify(mMethodInvoker).invoke(mTextView, "setId", "2");
    }

    @Test
    public void testSetAttributeAsTextWithSetChecked() {
        mDescriptor.setAttributesAsText(mCheckBox, "checked=\"true\"");
        Mockito.verify(mMethodInvoker).invoke(mCheckBox, "setChecked", "true");
    }

    @Test
    public void testSetMultipleAttributesAsText() {
        mDescriptor.setAttributesAsText(mTextView, "id=\"2\" text=\"Hello World\"");
        Mockito.verify(mMethodInvoker).invoke(mTextView, "setId", "2");
        Mockito.verify(mMethodInvoker).invoke(mTextView, "setText", "Hello World");
    }

    @Test
    public void testSetAttributeAsTextIgnoreInvalidFormat() {
        mDescriptor.setAttributesAsText(mTextView, "garbage");
        Mockito.verify(mMethodInvoker, Mockito.never()).invoke(ArgumentMatchers.anyObject(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }
}

