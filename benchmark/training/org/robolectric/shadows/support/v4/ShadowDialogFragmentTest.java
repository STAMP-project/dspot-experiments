package org.robolectric.shadows.support.v4;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.util.TestRunnerWithManifest;

import static org.robolectric.R.id.title;
import static org.robolectric.R.layout.dialog_fragment;


@RunWith(TestRunnerWithManifest.class)
public class ShadowDialogFragmentTest {
    private FragmentActivity activity;

    private ShadowDialogFragmentTest.TestDialogFragment dialogFragment;

    private FragmentManager fragmentManager;

    @Test
    public void show_shouldCallLifecycleMethods() throws Exception {
        dialogFragment.show(fragmentManager, "this is a tag");
        assertThat(dialogFragment.transcript).containsExactly("onAttach", "onCreate", "onCreateDialog", "onCreateView", "onViewCreated", "onActivityCreated", "onStart", "onResume");
        Assert.assertNotNull(getActivity());
        Assert.assertSame(activity, dialogFragment.onAttachActivity);
    }

    @Test
    public void show_whenPassedATransaction_shouldCallShowWithManager() throws Exception {
        dialogFragment.show(fragmentManager.beginTransaction(), "this is a tag");
        assertThat(dialogFragment.transcript).containsExactly("onAttach", "onCreate", "onCreateDialog", "onCreateView", "onViewCreated", "onActivityCreated", "onStart", "onResume");
        Assert.assertNotNull(getActivity());
        Assert.assertSame(activity, dialogFragment.onAttachActivity);
    }

    @Test
    public void show_shouldShowDialogThatWasReturnedFromOnCreateDialog_whenOnCreateDialogReturnsADialog() throws Exception {
        Dialog dialogFromOnCreateDialog = new Dialog(activity);
        dialogFragment.returnThisDialogFromOnCreateDialog(dialogFromOnCreateDialog);
        dialogFragment.show(fragmentManager, "this is a tag");
        Dialog dialog = ShadowDialog.getLatestDialog();
        Assert.assertSame(dialogFromOnCreateDialog, dialog);
        Assert.assertSame(dialogFromOnCreateDialog, getDialog());
        Assert.assertSame(dialogFragment, fragmentManager.findFragmentByTag("this is a tag"));
    }

    @Test
    public void show_shouldShowDialogThatWasAutomaticallyCreated_whenOnCreateDialogReturnsNull() throws Exception {
        dialogFragment.show(fragmentManager, "this is a tag");
        Dialog dialog = ShadowDialog.getLatestDialog();
        Assert.assertNotNull(dialog);
        Assert.assertSame(dialog, getDialog());
        Assert.assertNotNull(dialog.findViewById(title));
        Assert.assertSame(dialogFragment, fragmentManager.findFragmentByTag("this is a tag"));
    }

    @Test
    public void removeUsingTransaction_shouldDismissTheDialog() throws Exception {
        dialogFragment.show(fragmentManager, null);
        FragmentTransaction t = fragmentManager.beginTransaction();
        t.remove(dialogFragment);
        t.commit();
        Dialog dialog = ShadowDialog.getLatestDialog();
        Assert.assertFalse(dialog.isShowing());
        Assert.assertTrue(ShadowDialogFragmentTest.shadowOf(dialog).hasBeenDismissed());
    }

    public static class TestDialogFragment extends DialogFragment {
        final List<String> transcript = new ArrayList<>();

        Activity onAttachActivity;

        private Dialog returnThisDialogFromOnCreateDialog;

        @Override
        public void onAttach(Activity activity) {
            transcript.add("onAttach");
            onAttachActivity = activity;
            super.onAttach(activity);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            transcript.add("onCreate");
            super.onCreate(savedInstanceState);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            transcript.add("onCreateDialog");
            return (returnThisDialogFromOnCreateDialog) == null ? super.onCreateDialog(savedInstanceState) : returnThisDialogFromOnCreateDialog;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            transcript.add("onCreateView");
            return inflater.inflate(dialog_fragment, null);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            transcript.add("onViewCreated");
            super.onViewCreated(view, savedInstanceState);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            transcript.add("onActivityCreated");
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onStart() {
            transcript.add("onStart");
            super.onStart();
        }

        @Override
        public void onResume() {
            transcript.add("onResume");
            super.onResume();
        }

        public void returnThisDialogFromOnCreateDialog(Dialog dialog) {
            returnThisDialogFromOnCreateDialog = dialog;
        }
    }
}

