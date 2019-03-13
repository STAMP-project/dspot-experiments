package com.nextcloud.client.preferences;


import SharedPreferences.Editor;
import android.content.Context;
import android.content.SharedPreferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TestPreferenceManager {
    @Mock
    private Context testContext;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private Editor editor;

    private PreferenceManager appPreferences;

    @Test
    public void removeLegacyPreferences() {
        appPreferences.removeLegacyPreferences();
        InOrder inOrder = Mockito.inOrder(editor);
        inOrder.verify(editor).remove("instant_uploading");
        inOrder.verify(editor).remove("instant_video_uploading");
        inOrder.verify(editor).remove("instant_upload_path");
        inOrder.verify(editor).remove("instant_upload_path_use_subfolders");
        inOrder.verify(editor).remove("instant_upload_on_wifi");
        inOrder.verify(editor).remove("instant_upload_on_charging");
        inOrder.verify(editor).remove("instant_video_upload_path");
        inOrder.verify(editor).remove("instant_video_upload_path_use_subfolders");
        inOrder.verify(editor).remove("instant_video_upload_on_wifi");
        inOrder.verify(editor).remove("instant_video_uploading");
        inOrder.verify(editor).remove("instant_video_upload_on_charging");
        inOrder.verify(editor).remove("prefs_instant_behaviour");
        inOrder.verify(editor).apply();
    }
}

