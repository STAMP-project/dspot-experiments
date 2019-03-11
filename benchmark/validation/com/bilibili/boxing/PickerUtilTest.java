/**
 * Copyright (C) 2017 Bilibili
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bilibili.boxing;


import Activity.RESULT_OK;
import BoxingFileHelper.DEFAULT_SUB_DIR;
import CameraPickerHelper.REQ_CODE_CAMERA;
import Environment.DIRECTORY_DCIM;
import Environment.MEDIA_MOUNTED;
import android.os.Environment;
import android.text.TextUtils;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing.utils.BoxingFileHelper;
import com.bilibili.boxing.utils.CameraPickerHelper;
import com.bilibili.boxing.utils.CompressTask;
import com.bilibili.boxing.utils.ImageCompressor;
import java.io.File;
import java.util.concurrent.ExecutionException;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author ChenSL
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
@PrepareOnlyThisForTest(Environment.class)
@Config(sdk = 21, constants = BuildConfig.class)
public class PickerUtilTest {
    @Mock
    private CameraPickerHelper mHelper;

    @Captor
    private ArgumentCaptor<CameraPickerHelper.Callback> mCaptor;

    @Test
    public void testCompressTask() {
        ImageCompressor illegalCompressor = new ImageCompressor(new File("///"));
        ImageMedia media = new ImageMedia("123", "44");
        ImageCompressor compressor = new ImageCompressor(new File("src/main/res/"));
        ImageMedia media1 = new ImageMedia("1223", "../boxing/boxing-impl/src/main/res/drawable-hdpi/ic_boxing_broken_image.png");
        media1.setSize("233");
        boolean result1 = CompressTask.compress(null, null, 0);
        TestCase.assertTrue((!result1));
        result1 = CompressTask.compress(null, media, 0);
        TestCase.assertTrue((!result1));
        result1 = CompressTask.compress(illegalCompressor, media, 0);
        TestCase.assertTrue((!result1));
        result1 = CompressTask.compress(illegalCompressor, media, 1000);
        TestCase.assertTrue((!result1));
        result1 = CompressTask.compress(compressor, media1, 1000);
        TestCase.assertTrue(result1);
    }

    @Test
    public void testFileHelper() throws InterruptedException, ExecutionException {
        boolean nullFile = BoxingFileHelper.createFile(null);
        TestCase.assertTrue((!nullFile));
        boolean hasFile = BoxingFileHelper.createFile("/");
        TestCase.assertTrue(hasFile);
    }

    @Test
    public void testCacheDir() {
        String nullFile = BoxingFileHelper.getCacheDir(null);
        TestCase.assertTrue((nullFile == null));
    }

    @Test
    public void testGetExternalDCIM() {
        PowerMockito.mockStatic(Environment.class);
        String file = BoxingFileHelper.getExternalDCIM(DEFAULT_SUB_DIR);
        Assert.assertNull(file);
        PowerMockito.when(Environment.getExternalStorageState()).thenReturn(MEDIA_MOUNTED);
        file = BoxingFileHelper.getExternalDCIM(DEFAULT_SUB_DIR);
        Assert.assertNull(file);
        PowerMockito.when(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM)).thenReturn(new File("DCIM"));
        file = BoxingFileHelper.getExternalDCIM(DEFAULT_SUB_DIR);
        Assert.assertNotNull(file);
    }

    @Test
    public void testCameraHelper() {
        CameraPickerHelper helper = new CameraPickerHelper(null);
        boolean fail = helper.onActivityResult(0, 0);
        Assert.assertFalse(fail);
        fail = helper.onActivityResult(REQ_CODE_CAMERA, 0);
        Assert.assertFalse(fail);
        fail = helper.onActivityResult(0, RESULT_OK);
        Assert.assertFalse(fail);
        boolean suc = helper.onActivityResult(REQ_CODE_CAMERA, RESULT_OK);
        Assert.assertTrue(suc);
    }
}

