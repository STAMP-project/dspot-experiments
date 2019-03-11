package org.robolectric.shadows;


import EditorInfo.IME_ACTION_GO;
import Gravity.CENTER;
import Linkify.ALL;
import TextView.BufferType.EDITABLE;
import TextView.BufferType.SPANNABLE;
import TypedValue.COMPLEX_UNIT_DIP;
import TypedValue.COMPLEX_UNIT_PX;
import TypedValue.COMPLEX_UNIT_SP;
import android.R.color.white;
import android.R.id.selectAll;
import android.R.style.TextAppearance_Small;
import android.app.Activity;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.R;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadow.api.Shadow;

import static org.robolectric.R.color.grey42;
import static org.robolectric.R.drawable.an_image;
import static org.robolectric.R.drawable.an_other_image;
import static org.robolectric.R.drawable.fourth_image;
import static org.robolectric.R.drawable.l0_red;
import static org.robolectric.R.drawable.l1_orange;
import static org.robolectric.R.drawable.l2_yellow;
import static org.robolectric.R.drawable.l3_green;
import static org.robolectric.R.drawable.third_image;
import static org.robolectric.R.id.black_text_view;
import static org.robolectric.R.id.black_text_view_hint;
import static org.robolectric.R.id.grey_text_view;
import static org.robolectric.R.id.grey_text_view_hint;
import static org.robolectric.R.id.white_text_view;
import static org.robolectric.R.id.white_text_view_hint;
import static org.robolectric.R.layout.text_views;
import static org.robolectric.R.layout.text_views_hints;
import static org.robolectric.R.string.hello;


@RunWith(AndroidJUnit4.class)
public class ShadowTextViewTest {
    private static final String INITIAL_TEXT = "initial text";

    private static final String NEW_TEXT = "new text";

    private TextView textView;

    private ActivityController<Activity> activityController;

    @Test
    public void shouldTriggerTheImeListener() {
        ShadowTextViewTest.TestOnEditorActionListener actionListener = new ShadowTextViewTest.TestOnEditorActionListener();
        textView.setOnEditorActionListener(actionListener);
        textView.onEditorAction(IME_ACTION_GO);
        assertThat(actionListener.textView).isSameAs(textView);
        assertThat(actionListener.sentImeId).isEqualTo(IME_ACTION_GO);
    }

    @Test
    public void shouldCreateGetterForEditorActionListener() {
        ShadowTextViewTest.TestOnEditorActionListener actionListener = new ShadowTextViewTest.TestOnEditorActionListener();
        textView.setOnEditorActionListener(actionListener);
        assertThat(Shadows.shadowOf(textView).getOnEditorActionListener()).isSameAs(actionListener);
    }

    @Test
    public void testGetUrls() throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        textView.setAutoLinkMask(ALL);
        textView.setText("here\'s some text http://google.com/\nblah\thttp://another.com/123?456 blah");
        assertThat(urlStringsFrom(textView.getUrls())).isEqualTo(Arrays.asList("http://google.com", "http://another.com/123?456"));
    }

    @Test
    public void testGetGravity() throws Exception {
        assertThat(textView.getGravity()).isNotEqualTo(CENTER);
        textView.setGravity(CENTER);
        assertThat(textView.getGravity()).isEqualTo(CENTER);
    }

    @Test
    public void testMovementMethod() {
        MovementMethod movement = new ArrowKeyMovementMethod();
        Assert.assertNull(textView.getMovementMethod());
        textView.setMovementMethod(movement);
        assertThat(textView.getMovementMethod()).isSameAs(movement);
    }

    @Test
    public void testLinksClickable() {
        assertThat(textView.getLinksClickable()).isTrue();
        textView.setLinksClickable(false);
        assertThat(textView.getLinksClickable()).isFalse();
        textView.setLinksClickable(true);
        assertThat(textView.getLinksClickable()).isTrue();
    }

    @Test
    public void testGetTextAppearanceId() throws Exception {
        textView.setTextAppearance(ApplicationProvider.getApplicationContext(), TextAppearance_Small);
        assertThat(Shadows.shadowOf(textView).getTextAppearanceId()).isEqualTo(TextAppearance_Small);
    }

    @Test
    public void shouldSetTextAndTextColorWhileInflatingXmlLayout() throws Exception {
        Activity activity = activityController.get();
        activity.setContentView(text_views);
        TextView black = ((TextView) (activity.findViewById(black_text_view)));
        assertThat(black.getText().toString()).isEqualTo("Black Text");
        assertThat(black.getCurrentTextColor()).isEqualTo(-16777216);
        TextView white = ((TextView) (activity.findViewById(white_text_view)));
        assertThat(white.getText().toString()).isEqualTo("White Text");
        assertThat(white.getCurrentTextColor()).isEqualTo(activity.getResources().getColor(white));
        TextView grey = ((TextView) (activity.findViewById(grey_text_view)));
        assertThat(grey.getText().toString()).isEqualTo("Grey Text");
        assertThat(grey.getCurrentTextColor()).isEqualTo(activity.getResources().getColor(grey42));
    }

    @Test
    public void shouldSetHintAndHintColorWhileInflatingXmlLayout() throws Exception {
        Activity activity = activityController.get();
        activity.setContentView(text_views_hints);
        TextView black = ((TextView) (activity.findViewById(black_text_view_hint)));
        assertThat(black.getHint().toString()).isEqualTo("Black Hint");
        assertThat(black.getCurrentHintTextColor()).isEqualTo(-16777216);
        TextView white = ((TextView) (activity.findViewById(white_text_view_hint)));
        assertThat(white.getHint().toString()).isEqualTo("White Hint");
        assertThat(white.getCurrentHintTextColor()).isEqualTo(activity.getResources().getColor(white));
        TextView grey = ((TextView) (activity.findViewById(grey_text_view_hint)));
        assertThat(grey.getHint().toString()).isEqualTo("Grey Hint");
        assertThat(grey.getCurrentHintTextColor()).isEqualTo(activity.getResources().getColor(grey42));
    }

    @Test
    public void shouldNotHaveTransformationMethodByDefault() {
        assertThat(textView.getTransformationMethod()).isNull();
    }

    @Test
    public void shouldAllowSettingATransformationMethod() {
        textView.setTransformationMethod(PasswordTransformationMethod.getInstance());
        assertThat(textView.getTransformationMethod()).isInstanceOf(PasswordTransformationMethod.class);
    }

    @Test
    public void testGetInputType() throws Exception {
        assertThat(textView.getInputType()).isNotEqualTo(((InputType.TYPE_CLASS_TEXT) | (InputType.TYPE_TEXT_VARIATION_PASSWORD)));
        textView.setInputType(((InputType.TYPE_CLASS_TEXT) | (InputType.TYPE_TEXT_VARIATION_PASSWORD)));
        assertThat(textView.getInputType()).isEqualTo(((InputType.TYPE_CLASS_TEXT) | (InputType.TYPE_TEXT_VARIATION_PASSWORD)));
    }

    @Test
    public void givenATextViewWithATextWatcherAdded_WhenSettingTextWithTextResourceId_ShouldNotifyTextWatcher() {
        ShadowTextViewTest.MockTextWatcher mockTextWatcher = new ShadowTextViewTest.MockTextWatcher();
        textView.addTextChangedListener(mockTextWatcher);
        textView.setText(hello);
        assertEachTextWatcherEventWasInvoked(mockTextWatcher);
    }

    @Test
    public void givenATextViewWithATextWatcherAdded_WhenSettingTextWithCharSequence_ShouldNotifyTextWatcher() {
        ShadowTextViewTest.MockTextWatcher mockTextWatcher = new ShadowTextViewTest.MockTextWatcher();
        textView.addTextChangedListener(mockTextWatcher);
        textView.setText("text");
        assertEachTextWatcherEventWasInvoked(mockTextWatcher);
    }

    @Test
    public void givenATextViewWithATextWatcherAdded_WhenSettingNullText_ShouldNotifyTextWatcher() {
        ShadowTextViewTest.MockTextWatcher mockTextWatcher = new ShadowTextViewTest.MockTextWatcher();
        textView.addTextChangedListener(mockTextWatcher);
        textView.setText(null);
        assertEachTextWatcherEventWasInvoked(mockTextWatcher);
    }

    @Test
    public void givenATextViewWithMultipleTextWatchersAdded_WhenSettingText_ShouldNotifyEachTextWatcher() {
        List<ShadowTextViewTest.MockTextWatcher> mockTextWatchers = anyNumberOfTextWatchers();
        for (ShadowTextViewTest.MockTextWatcher textWatcher : mockTextWatchers) {
            textView.addTextChangedListener(textWatcher);
        }
        textView.setText("text");
        for (ShadowTextViewTest.MockTextWatcher textWatcher : mockTextWatchers) {
            assertEachTextWatcherEventWasInvoked(textWatcher);
        }
    }

    @Test
    public void whenSettingText_ShouldFireBeforeTextChangedWithCorrectArguments() {
        textView.setText(ShadowTextViewTest.INITIAL_TEXT);
        TextWatcher mockTextWatcher = Mockito.mock(TextWatcher.class);
        textView.addTextChangedListener(mockTextWatcher);
        textView.setText(ShadowTextViewTest.NEW_TEXT);
        Mockito.verify(mockTextWatcher).beforeTextChanged(ShadowTextViewTest.INITIAL_TEXT, 0, ShadowTextViewTest.INITIAL_TEXT.length(), ShadowTextViewTest.NEW_TEXT.length());
    }

    @Test
    public void whenSettingText_ShouldFireOnTextChangedWithCorrectArguments() {
        textView.setText(ShadowTextViewTest.INITIAL_TEXT);
        TextWatcher mockTextWatcher = Mockito.mock(TextWatcher.class);
        textView.addTextChangedListener(mockTextWatcher);
        textView.setText(ShadowTextViewTest.NEW_TEXT);
        ArgumentCaptor<SpannableStringBuilder> builderCaptor = ArgumentCaptor.forClass(SpannableStringBuilder.class);
        Mockito.verify(mockTextWatcher).onTextChanged(builderCaptor.capture(), ArgumentMatchers.eq(0), ArgumentMatchers.eq(ShadowTextViewTest.INITIAL_TEXT.length()), ArgumentMatchers.eq(ShadowTextViewTest.NEW_TEXT.length()));
        assertThat(builderCaptor.getValue().toString()).isEqualTo(ShadowTextViewTest.NEW_TEXT);
    }

    @Test
    public void whenSettingText_ShouldFireAfterTextChangedWithCorrectArgument() {
        ShadowTextViewTest.MockTextWatcher mockTextWatcher = new ShadowTextViewTest.MockTextWatcher();
        textView.addTextChangedListener(mockTextWatcher);
        textView.setText(ShadowTextViewTest.NEW_TEXT);
        assertThat(mockTextWatcher.afterTextChangeArgument.toString()).isEqualTo(ShadowTextViewTest.NEW_TEXT);
    }

    @Test
    public void whenAppendingText_ShouldAppendNewTextAfterOldOne() {
        textView.setText(ShadowTextViewTest.INITIAL_TEXT);
        textView.append(ShadowTextViewTest.NEW_TEXT);
        assertThat(textView.getText().toString()).isEqualTo(((ShadowTextViewTest.INITIAL_TEXT) + (ShadowTextViewTest.NEW_TEXT)));
    }

    @Test
    public void whenAppendingText_ShouldFireBeforeTextChangedWithCorrectArguments() {
        textView.setText(ShadowTextViewTest.INITIAL_TEXT);
        TextWatcher mockTextWatcher = Mockito.mock(TextWatcher.class);
        textView.addTextChangedListener(mockTextWatcher);
        textView.append(ShadowTextViewTest.NEW_TEXT);
        Mockito.verify(mockTextWatcher).beforeTextChanged(ArgumentMatchers.eq(ShadowTextViewTest.INITIAL_TEXT), ArgumentMatchers.eq(0), ArgumentMatchers.eq(ShadowTextViewTest.INITIAL_TEXT.length()), ArgumentMatchers.eq(ShadowTextViewTest.INITIAL_TEXT.length()));
    }

    @Test
    public void whenAppendingText_ShouldFireOnTextChangedWithCorrectArguments() {
        textView.setText(ShadowTextViewTest.INITIAL_TEXT);
        TextWatcher mockTextWatcher = Mockito.mock(TextWatcher.class);
        textView.addTextChangedListener(mockTextWatcher);
        textView.append(ShadowTextViewTest.NEW_TEXT);
        ArgumentCaptor<SpannableStringBuilder> builderCaptor = ArgumentCaptor.forClass(SpannableStringBuilder.class);
        Mockito.verify(mockTextWatcher).onTextChanged(builderCaptor.capture(), ArgumentMatchers.eq(0), ArgumentMatchers.eq(ShadowTextViewTest.INITIAL_TEXT.length()), ArgumentMatchers.eq(ShadowTextViewTest.INITIAL_TEXT.length()));
        assertThat(builderCaptor.getValue().toString()).isEqualTo(((ShadowTextViewTest.INITIAL_TEXT) + (ShadowTextViewTest.NEW_TEXT)));
    }

    @Test
    public void whenAppendingText_ShouldFireAfterTextChangedWithCorrectArgument() {
        textView.setText(ShadowTextViewTest.INITIAL_TEXT);
        ShadowTextViewTest.MockTextWatcher mockTextWatcher = new ShadowTextViewTest.MockTextWatcher();
        textView.addTextChangedListener(mockTextWatcher);
        textView.append(ShadowTextViewTest.NEW_TEXT);
        assertThat(mockTextWatcher.afterTextChangeArgument.toString()).isEqualTo(((ShadowTextViewTest.INITIAL_TEXT) + (ShadowTextViewTest.NEW_TEXT)));
    }

    @Test
    public void removeTextChangedListener_shouldRemoveTheListener() throws Exception {
        ShadowTextViewTest.MockTextWatcher watcher = new ShadowTextViewTest.MockTextWatcher();
        textView.addTextChangedListener(watcher);
        Assert.assertTrue(Shadows.shadowOf(textView).getWatchers().contains(watcher));
        textView.removeTextChangedListener(watcher);
        Assert.assertFalse(Shadows.shadowOf(textView).getWatchers().contains(watcher));
    }

    @Test
    public void getPaint_returnsMeasureTextEnabledObject() throws Exception {
        assertThat(textView.getPaint().measureText("12345")).isEqualTo(5.0F);
    }

    @Test
    public void append_whenSelectionIsAtTheEnd_shouldKeepSelectionAtTheEnd() throws Exception {
        textView.setText("1", EDITABLE);
        Selection.setSelection(textView.getEditableText(), 0, 0);
        textView.append("2");
        Assert.assertEquals(0, textView.getSelectionEnd());
        Assert.assertEquals(0, textView.getSelectionStart());
        Selection.setSelection(textView.getEditableText(), 2, 2);
        textView.append("3");
        Assert.assertEquals(3, textView.getSelectionEnd());
        Assert.assertEquals(3, textView.getSelectionStart());
    }

    @Test
    public void append_whenSelectionReachesToEnd_shouldExtendSelectionToTheEnd() throws Exception {
        textView.setText("12", EDITABLE);
        Selection.setSelection(textView.getEditableText(), 0, 2);
        textView.append("3");
        Assert.assertEquals(3, textView.getSelectionEnd());
        Assert.assertEquals(0, textView.getSelectionStart());
    }

    @Test
    public void testSetCompountDrawablesWithIntrinsicBounds_int_shouldCreateDrawablesWithResourceIds() throws Exception {
        textView.setCompoundDrawablesWithIntrinsicBounds(an_image, an_other_image, third_image, fourth_image);
        Assert.assertEquals(an_image, Shadows.shadowOf(textView.getCompoundDrawables()[0]).getCreatedFromResId());
        Assert.assertEquals(an_other_image, Shadows.shadowOf(textView.getCompoundDrawables()[1]).getCreatedFromResId());
        Assert.assertEquals(third_image, Shadows.shadowOf(textView.getCompoundDrawables()[2]).getCreatedFromResId());
        Assert.assertEquals(fourth_image, Shadows.shadowOf(textView.getCompoundDrawables()[3]).getCreatedFromResId());
    }

    @Test
    public void testSetCompountDrawablesWithIntrinsicBounds_int_shouldNotCreateDrawablesForZero() throws Exception {
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        Assert.assertNull(textView.getCompoundDrawables()[0]);
        Assert.assertNull(textView.getCompoundDrawables()[1]);
        Assert.assertNull(textView.getCompoundDrawables()[2]);
        Assert.assertNull(textView.getCompoundDrawables()[3]);
    }

    @Test
    public void canSetAndGetTypeface() throws Exception {
        Typeface typeface = Shadow.newInstanceOf(Typeface.class);
        textView.setTypeface(typeface);
        Assert.assertSame(typeface, textView.getTypeface());
    }

    @Test
    public void onTouchEvent_shouldCallMovementMethodOnTouchEventWithSetMotionEvent() throws Exception {
        ShadowTextViewTest.TestMovementMethod testMovementMethod = new ShadowTextViewTest.TestMovementMethod();
        textView.setMovementMethod(testMovementMethod);
        textView.setLayoutParams(new FrameLayout.LayoutParams(100, 100));
        textView.measure(100, 100);
        MotionEvent event = MotionEvent.obtain(0, 0, 0, 0, 0, 0);
        textView.dispatchTouchEvent(event);
        Assert.assertEquals(testMovementMethod.event, event);
    }

    @Test
    public void testGetError() {
        Assert.assertNull(textView.getError());
        CharSequence error = "myError";
        textView.setError(error);
        Assert.assertEquals(error, textView.getError());
    }

    @Test
    public void canSetAndGetInputFilters() throws Exception {
        final InputFilter[] expectedFilters = new InputFilter[]{ new InputFilter.LengthFilter(1) };
        textView.setFilters(expectedFilters);
        assertThat(textView.getFilters()).isSameAs(expectedFilters);
    }

    @Test
    public void testHasSelectionReturnsTrue() {
        textView.setText("1", SPANNABLE);
        textView.onTextContextMenuItem(selectAll);
        Assert.assertTrue(textView.hasSelection());
    }

    @Test
    public void testHasSelectionReturnsFalse() {
        textView.setText("1", SPANNABLE);
        Assert.assertFalse(textView.hasSelection());
    }

    @Test
    public void whenSettingTextToNull_WatchersSeeEmptyString() {
        TextWatcher mockTextWatcher = Mockito.mock(TextWatcher.class);
        textView.addTextChangedListener(mockTextWatcher);
        textView.setText(null);
        ArgumentCaptor<SpannableStringBuilder> builderCaptor = ArgumentCaptor.forClass(SpannableStringBuilder.class);
        Mockito.verify(mockTextWatcher).onTextChanged(builderCaptor.capture(), ArgumentMatchers.eq(0), ArgumentMatchers.eq(0), ArgumentMatchers.eq(0));
        assertThat(builderCaptor.getValue().toString()).isEmpty();
    }

    @Test
    public void getPaint_returnsNonNull() {
        Assert.assertNotNull(textView.getPaint());
    }

    @Test
    public void testNoArgAppend() {
        textView.setText("a");
        textView.append("b");
        assertThat(textView.getText().toString()).isEqualTo("ab");
    }

    @Test
    public void setTextSize_shouldHandleDips() throws Exception {
        ApplicationProvider.getApplicationContext().getResources().getDisplayMetrics().density = 1.5F;
        textView.setTextSize(COMPLEX_UNIT_DIP, 10);
        assertThat(textView.getTextSize()).isEqualTo(15.0F);
        textView.setTextSize(COMPLEX_UNIT_DIP, 20);
        assertThat(textView.getTextSize()).isEqualTo(30.0F);
    }

    @Test
    public void setTextSize_shouldHandleSp() throws Exception {
        textView.setTextSize(COMPLEX_UNIT_SP, 10);
        assertThat(textView.getTextSize()).isEqualTo(10.0F);
        ApplicationProvider.getApplicationContext().getResources().getDisplayMetrics().scaledDensity = 1.5F;
        textView.setTextSize(COMPLEX_UNIT_SP, 10);
        assertThat(textView.getTextSize()).isEqualTo(15.0F);
    }

    @Test
    public void setTextSize_shouldHandlePixels() throws Exception {
        ApplicationProvider.getApplicationContext().getResources().getDisplayMetrics().density = 1.5F;
        textView.setTextSize(COMPLEX_UNIT_PX, 10);
        assertThat(textView.getTextSize()).isEqualTo(10.0F);
        textView.setTextSize(COMPLEX_UNIT_PX, 20);
        assertThat(textView.getTextSize()).isEqualTo(20.0F);
    }

    @Test
    public void getPaintFlagsAndSetPaintFlags_shouldWork() {
        assertThat(textView.getPaintFlags()).isEqualTo(0);
        textView.setPaintFlags(100);
        assertThat(textView.getPaintFlags()).isEqualTo(100);
    }

    @Test
    public void setCompoundDrawablesWithIntrinsicBounds_setsValues() {
        textView.setCompoundDrawablesWithIntrinsicBounds(l0_red, l1_orange, l2_yellow, l3_green);
        assertThat(Shadows.shadowOf(textView).getCompoundDrawablesWithIntrinsicBoundsLeft()).isEqualTo(l0_red);
        assertThat(Shadows.shadowOf(textView).getCompoundDrawablesWithIntrinsicBoundsTop()).isEqualTo(l1_orange);
        assertThat(Shadows.shadowOf(textView).getCompoundDrawablesWithIntrinsicBoundsRight()).isEqualTo(l2_yellow);
        assertThat(Shadows.shadowOf(textView).getCompoundDrawablesWithIntrinsicBoundsBottom()).isEqualTo(l3_green);
    }

    private static class TestOnEditorActionListener implements TextView.OnEditorActionListener {
        private TextView textView;

        private int sentImeId;

        @Override
        public boolean onEditorAction(TextView textView, int sentImeId, KeyEvent keyEvent) {
            this.textView = textView;
            this.sentImeId = sentImeId;
            return false;
        }
    }

    private static class MockTextWatcher implements TextWatcher {
        List<String> methodsCalled = new ArrayList<>();

        Editable afterTextChangeArgument;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            methodsCalled.add("beforeTextChanged");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            methodsCalled.add("onTextChanged");
        }

        @Override
        public void afterTextChanged(Editable s) {
            methodsCalled.add("afterTextChanged");
            afterTextChangeArgument = s;
        }
    }

    private static class TestMovementMethod implements MovementMethod {
        public MotionEvent event;

        public boolean touchEventWasCalled;

        @Override
        public void initialize(TextView widget, Spannable text) {
        }

        @Override
        public boolean onKeyDown(TextView widget, Spannable text, int keyCode, KeyEvent event) {
            return false;
        }

        @Override
        public boolean onKeyUp(TextView widget, Spannable text, int keyCode, KeyEvent event) {
            return false;
        }

        @Override
        public boolean onKeyOther(TextView view, Spannable text, KeyEvent event) {
            return false;
        }

        @Override
        public void onTakeFocus(TextView widget, Spannable text, int direction) {
        }

        @Override
        public boolean onTrackballEvent(TextView widget, Spannable text, MotionEvent event) {
            return false;
        }

        @Override
        public boolean onTouchEvent(TextView widget, Spannable text, MotionEvent event) {
            this.event = event;
            touchEventWasCalled = true;
            return false;
        }

        @Override
        public boolean canSelectArbitrarily() {
            return false;
        }

        @Override
        public boolean onGenericMotionEvent(TextView widget, Spannable text, MotionEvent event) {
            return false;
        }
    }
}

