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


import BoxingConfig.DEFAULT_SELECTED_COUNT;
import BoxingConfig.Mode;
import BoxingConfig.Mode.MULTI_IMG;
import ImageMedia.IMAGE_TYPE.GIF;
import RuntimeEnvironment.application;
import android.content.ContentResolver;
import com.bilibili.boxing.model.BoxingManager;
import com.bilibili.boxing.model.callback.IAlbumTaskCallback;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.AlbumEntity;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing.model.entity.impl.VideoMedia;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author ChenSL
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, constants = BuildConfig.class)
public class PickerModuleTest {
    private BoxingManager mPickerManager;

    @Test
    public void testLoadImage() {
        Assert.assertNotNull(mPickerManager);
        mPickerManager.setBoxingConfig(new BoxingConfig(Mode.MULTI_IMG));
        BoxingConfig config = mPickerManager.getBoxingConfig();
        Assert.assertNotNull(config);
        Assert.assertEquals(config.getMode(), MULTI_IMG);
        ContentResolver cr = application.getContentResolver();
        Assert.assertNotNull(cr);
        mPickerManager.loadAlbum(cr, new IAlbumTaskCallback() {
            @Override
            public void postAlbumList(List<AlbumEntity> list) {
                Assert.assertNotNull(list);
            }
        });
    }

    @Test
    public void testBaseMedia() {
        BaseMedia media = new ImageMedia("233", "988");
        Assert.assertTrue(media.getId().equals("233"));
        Assert.assertTrue(media.getPath().equals("988"));
        media.setSize("99");
        Assert.assertTrue(((media.getSize()) == 99));
        media.setSize("&&");
        Assert.assertTrue(((media.getSize()) == 0));
        media.setSize("-1");
        Assert.assertTrue(((media.getSize()) == 0));
    }

    @Test
    public void testImageMedia() {
        ImageMedia imageMedia = new ImageMedia.Builder("233", "233").build();
        imageMedia.setPath("/");
        imageMedia.getThumbnailPath();
        String compressPath = imageMedia.getThumbnailPath();
        Assert.assertEquals(compressPath, "/");
        imageMedia.setCompressPath("111");
        String compressPath1 = imageMedia.getThumbnailPath();
        Assert.assertEquals(compressPath1, "/");
        imageMedia = new ImageMedia.Builder("233", "233").setThumbnailPath("999").build();
        String compressPath3 = imageMedia.getThumbnailPath();
        Assert.assertEquals(compressPath3, "233");
        Assert.assertEquals(imageMedia.getMimeType(), "image/jpeg");
        imageMedia.setImageType(GIF);
        Assert.assertEquals(imageMedia.getMimeType(), "image/gif");
    }

    @Test
    public void testVideoMedia() {
        VideoMedia videoMedia = new VideoMedia.Builder("233", "233").build();
        videoMedia.setDuration("asd");
        String result1 = videoMedia.formatTimeWithMin(0);
        Assert.assertEquals(result1, "00:00");
        String result2 = videoMedia.formatTimeWithMin(1000);
        Assert.assertEquals(result2, "00:01");
        String result3 = videoMedia.formatTimeWithMin((1000 * 36));
        Assert.assertEquals(result3, "00:36");
        String result4 = videoMedia.formatTimeWithMin((((1000 * 60) * 36) + (45 * 1000)));
        Assert.assertEquals(result4, "36:45");
        String result5 = videoMedia.formatTimeWithMin(((((1000 * 60) * 36) + (45 * 1000)) + 8500));
        Assert.assertEquals(result5, "36:53");
        String result6 = videoMedia.formatTimeWithMin(((long) (((((1000 * 60) * 102) + ((1000 * 60) * 7.2)) + (45 * 1000)) + 8500)));
        Assert.assertEquals(result6, "110:05");
        String duration = videoMedia.getDuration();
        Assert.assertEquals(duration, "0:00");
        videoMedia.setDuration("2160000");
        String duration1 = videoMedia.getDuration();
        Assert.assertEquals(duration1, "36:00");
    }

    @Test
    public void testSize() {
        VideoMedia videoMedia = new VideoMedia.Builder("233", "233").build();
        videoMedia.setSize("-1");
        String result = videoMedia.getSizeByUnit();
        Assert.assertEquals(result, "0K");
        videoMedia.setSize("0");
        result = videoMedia.getSizeByUnit();
        Assert.assertEquals(result, "0K");
        videoMedia.setSize("200");
        result = videoMedia.getSizeByUnit();
        Assert.assertEquals(result, "0.2K");
        videoMedia.setSize("1024");
        result = videoMedia.getSizeByUnit();
        Assert.assertEquals(result, "1.0K");
        videoMedia.setSize("1048576");
        result = videoMedia.getSizeByUnit();
        Assert.assertEquals(result, "1.0M");
        videoMedia.setSize("2048576");
        result = videoMedia.getSizeByUnit();
        Assert.assertEquals(result, "2.0M");
    }

    @Test
    public void testMaxCount() {
        mPickerManager.setBoxingConfig(withMaxCount(10));
        BoxingConfig config = mPickerManager.getBoxingConfig();
        int count = config.getMaxCount();
        Assert.assertEquals(count, 10);
        mPickerManager.setBoxingConfig(withMaxCount(0));
        config = mPickerManager.getBoxingConfig();
        count = config.getMaxCount();
        Assert.assertEquals(count, DEFAULT_SELECTED_COUNT);
    }
}

