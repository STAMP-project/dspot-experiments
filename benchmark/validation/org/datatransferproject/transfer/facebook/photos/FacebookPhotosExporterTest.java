/**
 * Copyright 2018 The Data Transfer Project Authors.
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
package org.datatransferproject.transfer.facebook.photos;


import ExportResult.ResultType.CONTINUE;
import ExportResult.ResultType.END;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.datatransferproject.spi.transfer.provider.ExportResult;
import org.datatransferproject.types.common.ExportInformation;
import org.datatransferproject.types.common.models.IdOnlyContainerResource;
import org.datatransferproject.types.common.models.photos.PhotoAlbum;
import org.datatransferproject.types.common.models.photos.PhotoModel;
import org.datatransferproject.types.common.models.photos.PhotosContainerResource;
import org.datatransferproject.types.transfer.auth.TokensAndUrlAuthData;
import org.junit.Assert;
import org.junit.Test;


public class FacebookPhotosExporterTest {
    private static final String ALBUM_NAME = "My Album";

    private static final String ALBUM_ID = "1946595";

    private static final String ALBUM_DESCRIPTION = "This is a test album";

    private static final String PHOTO_ID = "937644721";

    private static final String PHOTO_SOURCE = "https://www.example.com/photo.jpg";

    private static final String PHOTO_NAME = "Example photo";

    private FacebookPhotosExporter facebookPhotosExporter;

    private UUID uuid = UUID.randomUUID();

    @Test
    public void testExportAlbum() {
        ExportResult<PhotosContainerResource> result = facebookPhotosExporter.export(uuid, new TokensAndUrlAuthData("accessToken", null, null), Optional.empty());
        Assert.assertEquals(CONTINUE, result.getType());
        PhotosContainerResource exportedData = result.getExportedData();
        Assert.assertEquals(1, exportedData.getAlbums().size());
        Assert.assertEquals(new PhotoAlbum(FacebookPhotosExporterTest.ALBUM_ID, FacebookPhotosExporterTest.ALBUM_NAME, FacebookPhotosExporterTest.ALBUM_DESCRIPTION), exportedData.getAlbums().toArray()[0]);
        Assert.assertNull(result.getContinuationData().getPaginationData());
        assertThat(result.getContinuationData().getContainerResources()).contains(new IdOnlyContainerResource(FacebookPhotosExporterTest.ALBUM_ID));
    }

    @Test
    public void testExportPhoto() {
        ExportResult<PhotosContainerResource> result = facebookPhotosExporter.export(uuid, new TokensAndUrlAuthData("accessToken", null, null), Optional.of(new ExportInformation(null, new IdOnlyContainerResource(FacebookPhotosExporterTest.ALBUM_ID))));
        Assert.assertEquals(END, result.getType());
        PhotosContainerResource exportedData = result.getExportedData();
        Assert.assertEquals(1, exportedData.getPhotos().size());
        Assert.assertEquals(new PhotoModel(((FacebookPhotosExporterTest.PHOTO_ID) + ".jpg"), FacebookPhotosExporterTest.PHOTO_SOURCE, FacebookPhotosExporterTest.PHOTO_NAME, "image/jpg", FacebookPhotosExporterTest.PHOTO_ID, FacebookPhotosExporterTest.ALBUM_ID, false), exportedData.getPhotos().toArray()[0]);
    }

    @Test
    public void testSpecifiedAlbums() {
        ExportResult<PhotosContainerResource> result = facebookPhotosExporter.export(uuid, new TokensAndUrlAuthData("accessToken", null, null), Optional.of(new ExportInformation(new org.datatransferproject.types.common.StringPaginationToken(FacebookPhotosExporter.PHOTO_TOKEN_PREFIX), new PhotosContainerResource(Lists.newArrayList(new PhotoAlbum(FacebookPhotosExporterTest.ALBUM_ID, FacebookPhotosExporterTest.ALBUM_NAME, FacebookPhotosExporterTest.ALBUM_DESCRIPTION)), new ArrayList()))));
        Assert.assertEquals(CONTINUE, result.getType());
        PhotosContainerResource exportedData = result.getExportedData();
        Assert.assertEquals(1, exportedData.getAlbums().size());
        Assert.assertEquals(new PhotoAlbum(FacebookPhotosExporterTest.ALBUM_ID, FacebookPhotosExporterTest.ALBUM_NAME, FacebookPhotosExporterTest.ALBUM_DESCRIPTION), exportedData.getAlbums().toArray()[0]);
        Assert.assertNull(result.getContinuationData().getPaginationData());
        assertThat(result.getContinuationData().getContainerResources()).contains(new IdOnlyContainerResource(FacebookPhotosExporterTest.ALBUM_ID));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalExport() {
        facebookPhotosExporter.export(uuid, new TokensAndUrlAuthData("accessToken", null, null), Optional.of(new ExportInformation(new org.datatransferproject.types.common.StringPaginationToken(FacebookPhotosExporter.PHOTO_TOKEN_PREFIX), null)));
    }
}

