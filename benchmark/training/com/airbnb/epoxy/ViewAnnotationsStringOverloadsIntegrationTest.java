package com.airbnb.epoxy;


import R.plurals;
import R.plurals.plural_test_string;
import R.string;
import R.string.string_with_args;
import R.string.string_with_no_args;
import RuntimeEnvironment.application;
import ViewWithAnnotationsForIntegrationTest.DEFAULT_STRING;
import android.content.res.Resources;
import com.airbnb.epoxy.integrationtest.BuildConfig;
import com.airbnb.epoxy.integrationtest.ViewWithAnnotationsForIntegrationTest;
import com.airbnb.epoxy.integrationtest.ViewWithAnnotationsForIntegrationTestModel_;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ViewAnnotationsStringOverloadsIntegrationTest {
    private ControllerLifecycleHelper lifecycleHelper;

    @Test
    public void normalCharSequenceIsSet() {
        String text = "hello world";
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredText(text);
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertEquals(text, view.requiredText);
    }

    @Test
    public void getStringOffModel() {
        String text = "hello world!";
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredText(text);
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertEquals(model.getRequiredText(view.getContext()), view.requiredText);
    }

    @Test
    public void stringResIsSet() {
        int stringWithNoArgs = string.string_with_no_args;
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredText(stringWithNoArgs);
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertEquals(view.getContext().getText(stringWithNoArgs), view.requiredText);
    }

    @Test
    public void stringResWithArgsIsSet() {
        int stringWithNoArgs = string.string_with_args;
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredText(stringWithNoArgs, 3);
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertEquals(view.getContext().getString(stringWithNoArgs, 3), view.requiredText);
    }

    @Test
    public void quantityStringIsSet() {
        int pluralString = plurals.plural_test_string;
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredTextQuantityRes(pluralString, 1);
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertEquals(view.getContext().getResources().getQuantityString(pluralString, 1), view.requiredText);
    }

    @Test
    public void quantityStringWithArgsIsSet() {
        int pluralString = plurals.plural_test_string_with_args;
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredTextQuantityRes(pluralString, 1, 3);
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertEquals(view.getContext().getResources().getQuantityString(pluralString, 1, 3), view.requiredText);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiredTextThrowsWhenSetWithNull() {
        new ViewWithAnnotationsForIntegrationTestModel_().requiredText(null);
    }

    @Test(expected = IllegalStateException.class)
    public void requiredTextThrowsWhenNotSet() {
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_();
        bind(model);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiredTextThrowsOnBadStringRes() {
        new ViewWithAnnotationsForIntegrationTestModel_().requiredText(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiredTextThrowsOnBadStringResWithArgs() {
        new ViewWithAnnotationsForIntegrationTestModel_().requiredText(0, "args");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiredTextThrowsOnBadQuantityString() {
        new ViewWithAnnotationsForIntegrationTestModel_().requiredTextQuantityRes(0, 23, "args");
    }

    @Test
    public void nullableTextSetsNullWhenNotSet() {
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required");
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertNull(view.nullableText);
    }

    @Test
    public void nullableTextAllowsNull() {
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").nullableText(null);
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertNull(view.nullableText);
    }

    @Test
    public void nullableTextAllowsZeroStringRes() {
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").nullableText(0);
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertNull(view.nullableText);
    }

    @Test
    public void nullableTextAllowsZeroQuantityRes() {
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").nullableTextQuantityRes(0, 1);
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertNull(view.nullableText);
    }

    @Test
    public void defaultStringValueSetIfNothingElseIsSet() {
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required");
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertEquals(DEFAULT_STRING, view.textWithDefault);
        Assert.assertEquals(DEFAULT_STRING, view.nullableTextWithDefault);
    }

    @Test
    public void stringOverridesDefault() {
        String text = "hello world";
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").textWithDefault(text).nullableTextWithDefault(text);
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertEquals(text, view.nullableTextWithDefault);
        Assert.assertEquals(text, view.textWithDefault);
    }

    @Test
    public void nullableStringOverridesDefaultWithNull() {
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").nullableTextWithDefault(null);
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertEquals(null, view.nullableTextWithDefault);
    }

    @Test
    public void zeroStringResSetsDefault() {
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").textWithDefault(0).nullableTextWithDefault(0);
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertEquals(DEFAULT_STRING, view.textWithDefault);
        Assert.assertEquals(DEFAULT_STRING, view.nullableTextWithDefault);
    }

    @Test
    public void zeroQuantityStringResSetsDefault() {
        ViewWithAnnotationsForIntegrationTestModel_ model = new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").textWithDefaultQuantityRes(0, 1).nullableTextWithDefaultQuantityRes(0, 1);
        ViewWithAnnotationsForIntegrationTest view = bind(model);
        Assert.assertEquals(DEFAULT_STRING, view.textWithDefault);
        Assert.assertEquals(DEFAULT_STRING, view.nullableTextWithDefault);
    }

    @Test
    public void stringOverloadsResetEachOther() {
        Resources r = application.getResources();
        ViewWithAnnotationsForIntegrationTest view = bind(new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").nullableText(string_with_no_args).nullableText("test"));
        Assert.assertEquals("test", view.nullableText);
        view = bind(new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").nullableText("test").nullableText(string_with_no_args));
        Assert.assertEquals(r.getString(string_with_no_args), view.nullableText);
        view = bind(new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").nullableText(string_with_no_args).nullableTextQuantityRes(plural_test_string, 1));
        Assert.assertEquals(r.getQuantityString(plural_test_string, 1), view.nullableText);
        view = bind(new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").nullableText(string_with_no_args).nullableTextQuantityRes(0, 1));
        Assert.assertNull(view.nullableText);
        view = bind(new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").nullableTextQuantityRes(plural_test_string, 1).nullableText(string_with_args, 2));
        Assert.assertEquals(r.getString(string_with_args, 2), view.nullableText);
        view = bind(new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").nullableText(0).nullableText(string_with_args, 2));
        Assert.assertEquals(r.getString(string_with_args, 2), view.nullableText);
        view = bind(new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").nullableText(0).nullableText(string_with_args, 2));
        Assert.assertEquals(r.getString(string_with_args, 2), view.nullableText);
        view = bind(new ViewWithAnnotationsForIntegrationTestModel_().requiredText("required").nullableText(string_with_args, 2).nullableText(0));
        Assert.assertNull(view.nullableText);
    }
}

