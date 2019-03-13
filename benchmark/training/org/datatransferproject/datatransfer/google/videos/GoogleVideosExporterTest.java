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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.datatransferproject.spi.cloud.storage.JobStore;
import org.datatransferproject.spi.transfer.provider.ExportResult;
import org.datatransferproject.spi.transfer.types.ContinuationData;
import org.datatransferproject.types.common.StringPaginationToken;
import org.datatransferproject.types.common.models.videos.VideoObject;
import org.datatransferproject.types.common.models.videos.VideosContainerResource;
import org.junit.Test;
import org.mockito.Mockito;


public class GoogleVideosExporterTest {
    private String VIDEO_URI = "video uri";

    private String VIDEO_ID = "video id";

    private String VIDEO_TOKEN = "video_token";

    private UUID uuid = UUID.randomUUID();

    private GoogleVideosExporter googleVideosExporter;

    private JobStore jobStore;

    private GoogleVideosInterface videosInterface;

    private MediaItemSearchResponse mediaItemSearchResponse;

    private AlbumListResponse albumListResponse;

    @Test
    public void exportSingleVideo() throws IOException {
        Mockito.when(albumListResponse.getNextPageToken()).thenReturn(null);
        GoogleMediaItem mediaItem = setUpSingleVideo(VIDEO_URI, VIDEO_ID);
        Mockito.when(mediaItemSearchResponse.getMediaItems()).thenReturn(new GoogleMediaItem[]{ mediaItem });
        Mockito.when(mediaItemSearchResponse.getNextPageToken()).thenReturn(VIDEO_TOKEN);
        // Run test
        ExportResult<VideosContainerResource> result = googleVideosExporter.exportVideos(null, Optional.empty());
        // Verify correct methods were called
        Mockito.verify(videosInterface).listVideoItems(Optional.empty());
        Mockito.verify(mediaItemSearchResponse).getMediaItems();
        // Check pagination
        ContinuationData continuationData = result.getContinuationData();
        StringPaginationToken paginationToken = ((StringPaginationToken) (continuationData.getPaginationData()));
        assertThat(paginationToken.getToken()).isEqualTo(VIDEO_TOKEN);
        // Check videos field of container
        Collection<VideoObject> actualVideos = result.getExportedData().getVideos();
        URI video_uri_object = null;
        try {
            video_uri_object = new URI(((VIDEO_URI) + "=dv"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        assertThat(actualVideos.stream().map(VideoObject::getContentUrl).collect(Collectors.toList())).containsExactly(video_uri_object);
        // Since albums are not supported atm, this should be null
        assertThat(actualVideos.stream().map(VideoObject::getAlbumId).collect(Collectors.toList())).containsExactly(((Object) (null)));
    }
}

