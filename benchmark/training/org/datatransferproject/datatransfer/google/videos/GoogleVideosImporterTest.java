/**
 * Copyright 2019 The Data Transfer Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datatransferproject.datatransfer.google.videos;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.datatransferproject.datatransfer.google.mediaModels.NewMediaItem;
import org.datatransferproject.datatransfer.google.mediaModels.NewMediaItemUpload;
import org.datatransferproject.spi.cloud.storage.JobStore;
import org.datatransferproject.transfer.ImageStreamProvider;
import org.datatransferproject.types.common.models.videos.VideoObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class GoogleVideosImporterTest {
    private String VIDEO_TITLE = "Model video title";

    private String VIDEO_DESCRIPTION = "Model video description";

    private String VIDEO_URI = "https://www.example.com/video.mp4";

    private String MP4_MEDIA_TYPE = "video/mp4";

    private String UPLOAD_TOKEN = "uploadToken";

    private UUID uuid = UUID.randomUUID();

    private GoogleVideosImporter googleVideosImporter;

    private GoogleVideosInterface googleVideosInterface;

    private ImageStreamProvider videoStreamProvider;

    private JobStore jobStore;

    private InputStream inputStream;

    @Test
    public void exportVideo() throws IOException {
        // Set up
        VideoObject videoModel = new VideoObject(VIDEO_TITLE, VIDEO_URI, VIDEO_DESCRIPTION, MP4_MEDIA_TYPE, null, null, false);
        // Run test
        googleVideosImporter.importSingleVideo(uuid, null, videoModel);
        // Check results
        Mockito.verify(googleVideosInterface).uploadVideoContent(inputStream, ("Copy of " + (VIDEO_TITLE)));
        Mockito.verify(videoStreamProvider).get(VIDEO_URI);
        ArgumentCaptor<NewMediaItemUpload> uploadArgumentCaptor = ArgumentCaptor.forClass(NewMediaItemUpload.class);
        Mockito.verify(googleVideosInterface).createVideo(uploadArgumentCaptor.capture());
        List<NewMediaItem> newMediaItems = uploadArgumentCaptor.getValue().getNewMediaItems();
        Assert.assertEquals(newMediaItems.size(), 1);
        NewMediaItem mediaItem = newMediaItems.get(0);
        Assert.assertEquals(mediaItem.getSimpleMediaItem().getUploadToken(), UPLOAD_TOKEN);
    }
}

