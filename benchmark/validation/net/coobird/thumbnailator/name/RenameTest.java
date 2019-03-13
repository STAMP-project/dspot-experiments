package net.coobird.thumbnailator.name;


import Rename.NO_CHANGE;
import Rename.PREFIX_DOT_THUMBNAIL;
import Rename.PREFIX_HYPHEN_THUMBNAIL;
import Rename.SUFFIX_DOT_THUMBNAIL;
import Rename.SUFFIX_HYPHEN_THUMBNAIL;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.builders.ThumbnailParameterBuilder;
import org.junit.Assert;
import org.junit.Test;


public class RenameTest {
    @Test
    public void renameNoChange_NameGiven_ParamNull() {
        // given
        String name = "filename";
        ThumbnailParameter param = null;
        // when
        String filename = NO_CHANGE.apply(name, param);
        // then
        Assert.assertEquals("filename", filename);
    }

    @Test
    public void renameNoChange_NameGiven_ParamGiven() {
        // given
        String name = "filename";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = NO_CHANGE.apply(name, param);
        // then
        Assert.assertEquals("filename", filename);
    }

    @Test
    public void renamePrefixDotThumbnail_NameGiven_ParamNull() {
        // given
        String name = "filename";
        ThumbnailParameter param = null;
        // when
        String filename = PREFIX_DOT_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("thumbnail.filename", filename);
    }

    @Test
    public void renamePrefixDotThumbnail_NameGiven_ParamGiven() {
        // given
        String name = "filename";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = PREFIX_DOT_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("thumbnail.filename", filename);
    }

    @Test
    public void renamePrefixHyphenThumbnail_NameGiven_ParamNull() {
        // given
        String name = "filename";
        ThumbnailParameter param = null;
        // when
        String filename = PREFIX_HYPHEN_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("thumbnail-filename", filename);
    }

    @Test
    public void renamePrefixHyphenThumbnail_NameGiven_ParamGiven() {
        // given
        String name = "filename";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = PREFIX_HYPHEN_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("thumbnail-filename", filename);
    }

    @Test
    public void renameSuffixDotThumbnail_NameGiven_ParamNull() {
        // given
        String name = "filename";
        ThumbnailParameter param = null;
        // when
        String filename = SUFFIX_DOT_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("filename.thumbnail", filename);
    }

    @Test
    public void renameSuffixDotThumbnail_NameGiven_ParamGiven() {
        // given
        String name = "filename";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = SUFFIX_DOT_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("filename.thumbnail", filename);
    }

    @Test
    public void renameSuffixHyphenThumbnail_NameGiven_ParamNull() {
        // given
        String name = "filename";
        ThumbnailParameter param = null;
        // when
        String filename = SUFFIX_HYPHEN_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("filename-thumbnail", filename);
    }

    @Test
    public void renameSuffixHyphenThumbnail_NameGiven_ParamGiven() {
        // given
        String name = "filename";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = SUFFIX_HYPHEN_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("filename-thumbnail", filename);
    }

    @Test
    public void renameNoChange_NameGiven_ParamNull_WithExtension() {
        // given
        String name = "filename.jpg";
        ThumbnailParameter param = null;
        // when
        String filename = NO_CHANGE.apply(name, param);
        // then
        Assert.assertEquals("filename.jpg", filename);
    }

    @Test
    public void renameNoChange_NameGiven_ParamGiven_WithExtension() {
        // given
        String name = "filename.jpg";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = NO_CHANGE.apply(name, param);
        // then
        Assert.assertEquals("filename.jpg", filename);
    }

    @Test
    public void renamePrefixDotThumbnail_NameGiven_ParamNull_WithExtension() {
        // given
        String name = "filename.jpg";
        ThumbnailParameter param = null;
        // when
        String filename = PREFIX_DOT_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("thumbnail.filename.jpg", filename);
    }

    @Test
    public void renamePrefixDotThumbnail_NameGiven_ParamGiven_WithExtension() {
        // given
        String name = "filename.jpg";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = PREFIX_DOT_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("thumbnail.filename.jpg", filename);
    }

    @Test
    public void renamePrefixHyphenThumbnail_NameGiven_ParamNull_WithExtension() {
        // given
        String name = "filename.jpg";
        ThumbnailParameter param = null;
        // when
        String filename = PREFIX_HYPHEN_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("thumbnail-filename.jpg", filename);
    }

    @Test
    public void renamePrefixHyphenThumbnail_NameGiven_ParamGiven_WithExtension() {
        // given
        String name = "filename.jpg";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = PREFIX_HYPHEN_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("thumbnail-filename.jpg", filename);
    }

    @Test
    public void renameSuffixDotThumbnail_NameGiven_ParamNull_WithExtension() {
        // given
        String name = "filename.jpg";
        ThumbnailParameter param = null;
        // when
        String filename = SUFFIX_DOT_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("filename.thumbnail.jpg", filename);
    }

    @Test
    public void renameSuffixDotThumbnail_NameGiven_ParamGiven_WithExtension() {
        // given
        String name = "filename.jpg";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = SUFFIX_DOT_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("filename.thumbnail.jpg", filename);
    }

    @Test
    public void renameSuffixHyphenThumbnail_NameGiven_ParamNull_WithExtension() {
        // given
        String name = "filename.jpg";
        ThumbnailParameter param = null;
        // when
        String filename = SUFFIX_HYPHEN_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("filename-thumbnail.jpg", filename);
    }

    @Test
    public void renameSuffixHyphenThumbnail_NameGiven_ParamGiven_WithExtension() {
        // given
        String name = "filename.jpg";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = SUFFIX_HYPHEN_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("filename-thumbnail.jpg", filename);
    }

    @Test
    public void renameNoChange_NameGiven_ParamNull_WithMultipleDots() {
        // given
        String name = "filename.middle.jpg";
        ThumbnailParameter param = null;
        // when
        String filename = NO_CHANGE.apply(name, param);
        // then
        Assert.assertEquals("filename.middle.jpg", filename);
    }

    @Test
    public void renameNoChange_NameGiven_ParamGiven_WithMultipleDots() {
        // given
        String name = "filename.middle.jpg";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = NO_CHANGE.apply(name, param);
        // then
        Assert.assertEquals("filename.middle.jpg", filename);
    }

    @Test
    public void renamePrefixDotThumbnail_NameGiven_ParamNull_WithMultipleDots() {
        // given
        String name = "filename.middle.jpg";
        ThumbnailParameter param = null;
        // when
        String filename = PREFIX_DOT_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("thumbnail.filename.middle.jpg", filename);
    }

    @Test
    public void renamePrefixDotThumbnail_NameGiven_ParamGiven_WithMultipleDots() {
        // given
        String name = "filename.middle.jpg";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = PREFIX_DOT_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("thumbnail.filename.middle.jpg", filename);
    }

    @Test
    public void renamePrefixHyphenThumbnail_NameGiven_ParamNull_WithMultipleDots() {
        // given
        String name = "filename.middle.jpg";
        ThumbnailParameter param = null;
        // when
        String filename = PREFIX_HYPHEN_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("thumbnail-filename.middle.jpg", filename);
    }

    @Test
    public void renamePrefixHyphenThumbnail_NameGiven_ParamGiven_WithMultipleDots() {
        // given
        String name = "filename.middle.jpg";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = PREFIX_HYPHEN_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("thumbnail-filename.middle.jpg", filename);
    }

    @Test
    public void renameSuffixDotThumbnail_NameGiven_ParamNull_WithMultipleDots() {
        // given
        String name = "filename.middle.jpg";
        ThumbnailParameter param = null;
        // when
        String filename = SUFFIX_DOT_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("filename.middle.thumbnail.jpg", filename);
    }

    @Test
    public void renameSuffixDotThumbnail_NameGiven_ParamGiven_WithMultipleDots() {
        // given
        String name = "filename.middle.jpg";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = SUFFIX_DOT_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("filename.middle.thumbnail.jpg", filename);
    }

    @Test
    public void renameSuffixHyphenThumbnail_NameGiven_ParamNull_WithMultipleDots() {
        // given
        String name = "filename.middle.jpg";
        ThumbnailParameter param = null;
        // when
        String filename = SUFFIX_HYPHEN_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("filename.middle-thumbnail.jpg", filename);
    }

    @Test
    public void renameSuffixHyphenThumbnail_NameGiven_ParamGiven_WithMultipleDots() {
        // given
        String name = "filename.middle.jpg";
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(1.0).build();
        // when
        String filename = SUFFIX_HYPHEN_THUMBNAIL.apply(name, param);
        // then
        Assert.assertEquals("filename.middle-thumbnail.jpg", filename);
    }
}

