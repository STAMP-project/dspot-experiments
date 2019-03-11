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


import BoxingConfig.Mode;
import PickerContract.Presenter;
import PickerContract.View;
import android.content.ContentResolver;
import android.text.TextUtils;
import com.bilibili.boxing.model.BoxingManager;
import com.bilibili.boxing.model.callback.IAlbumTaskCallback;
import com.bilibili.boxing.model.callback.IMediaTaskCallback;
import com.bilibili.boxing.model.entity.AlbumEntity;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing.model.entity.impl.VideoMedia;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author ChenSL
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
@PrepareOnlyThisForTest(BoxingManager.class)
public class PickerPresenterTest {
    private Presenter mPresenter;

    @Mock
    private View mView;

    @Mock
    private BoxingManager mPickerManager;

    @Captor
    private ArgumentCaptor<IMediaTaskCallback> mLoadMediaCallback;

    @Captor
    private ArgumentCaptor<IAlbumTaskCallback> mAlbumTaskCallback;

    @Test
    public void loadVideo() {
        Mockito.when(mPickerManager.getBoxingConfig()).thenReturn(new com.bilibili.boxing.model.config.BoxingConfig(Mode.VIDEO));
        mPresenter.loadMedias(1, "");
        Mockito.verify(mPickerManager).loadMedia(ArgumentMatchers.any(ContentResolver.class), ArgumentCaptor.forClass(Integer.class).capture(), ArgumentCaptor.forClass(String.class).capture(), mLoadMediaCallback.capture());
        List<VideoMedia> videoMedias = new ArrayList<>();
        videoMedias.add(new VideoMedia.Builder("1", "test1").build());
        videoMedias.add(new VideoMedia.Builder("2", "test2").build());
        videoMedias.add(new VideoMedia.Builder("3", "test3").build());
        mLoadMediaCallback.getValue().postMedia(videoMedias, 3);
        ArgumentCaptor<List> showVideoCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(mView).showMedia(showVideoCaptor.capture(), ArgumentMatchers.anyInt());
        System.out.print(("load video count:" + (showVideoCaptor.getValue().size())));
        Assert.assertTrue(((showVideoCaptor.getValue().size()) == 3));
        Assert.assertTrue(showVideoCaptor.getValue().get(0).equals(videoMedias.get(0)));
        Assert.assertTrue(showVideoCaptor.getValue().get(1).equals(videoMedias.get(1)));
    }

    @Test
    public void loadImages() {
        Mockito.when(mPickerManager.getBoxingConfig()).thenReturn(new com.bilibili.boxing.model.config.BoxingConfig(Mode.MULTI_IMG));
        mPresenter.loadMedias(0, "");
        Mockito.verify(mPickerManager).loadMedia(ArgumentMatchers.any(ContentResolver.class), ArgumentCaptor.forClass(Integer.class).capture(), ArgumentCaptor.forClass(String.class).capture(), mLoadMediaCallback.capture());
        List<ImageMedia> imageMedias = new ArrayList<>();
        imageMedias.add(new ImageMedia.Builder("1", "test1").build());
        imageMedias.add(new ImageMedia.Builder("2", "test2").build());
        imageMedias.add(new ImageMedia.Builder("3", "test3").build());
        imageMedias.add(new ImageMedia.Builder("4", "test4").build());
        mLoadMediaCallback.getValue().postMedia(imageMedias, 4);
        ArgumentCaptor<List> showVideoCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(mView).showMedia(showVideoCaptor.capture(), ArgumentMatchers.anyInt());
        System.out.print(("load video count:" + (showVideoCaptor.getValue().size())));
        Assert.assertTrue(((showVideoCaptor.getValue().size()) == 4));
        Assert.assertTrue(showVideoCaptor.getValue().get(0).equals(imageMedias.get(0)));
        Assert.assertTrue(showVideoCaptor.getValue().get(1).equals(imageMedias.get(1)));
    }

    @Test
    public void loadAlbum() {
        mPresenter.loadAlbums();
        Mockito.verify(mPickerManager).loadAlbum(ArgumentMatchers.any(ContentResolver.class), mAlbumTaskCallback.capture());
        List<AlbumEntity> albums = new ArrayList<>();
        albums.add(AlbumEntity.createDefaultAlbum());
        mAlbumTaskCallback.getValue().postAlbumList(albums);
        ArgumentCaptor<List> showVideoCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(mView).showAlbum(showVideoCaptor.capture());
        System.out.print(("load album count:" + (showVideoCaptor.getValue().size())));
        Assert.assertTrue(((showVideoCaptor.getValue().size()) == 1));
    }

    @Test
    public void checkSelectedMedia() {
        List<BaseMedia> allMedias = new ArrayList<>();
        allMedias.add(new ImageMedia.Builder("1", "test1").setSelected(true).build());
        allMedias.add(new ImageMedia.Builder("2", "test2").setSelected(false).build());
        allMedias.add(new ImageMedia.Builder("3", "test3").build());
        List<BaseMedia> selectedMedias = new ArrayList<>();
        selectedMedias.add(new ImageMedia.Builder("2", "test2").setSelected(true).build());
        mPresenter.checkSelectedMedia(allMedias, selectedMedias);
        ImageMedia imageMedia0 = ((ImageMedia) (allMedias.get(0)));
        ImageMedia imageMedia1 = ((ImageMedia) (allMedias.get(1)));
        ImageMedia imageMedia2 = ((ImageMedia) (allMedias.get(2)));
        Assert.assertTrue((!(imageMedia0.isSelected())));
        Assert.assertTrue(imageMedia1.isSelected());
        Assert.assertTrue((!(imageMedia2.isSelected())));
        mPresenter.checkSelectedMedia(null, null);
    }

    @Test
    public void loadNextPage() {
        mPresenter.onLoadNextPage();
        Mockito.verify(mPickerManager).loadMedia(ArgumentMatchers.any(ContentResolver.class), ArgumentCaptor.forClass(Integer.class).capture(), ArgumentCaptor.forClass(String.class).capture(), mLoadMediaCallback.capture());
    }
}

