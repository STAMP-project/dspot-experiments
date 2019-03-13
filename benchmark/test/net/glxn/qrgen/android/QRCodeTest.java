package net.glxn.qrgen.android;


import EncodeHintType.CHARACTER_SET;
import EncodeHintType.ERROR_CORRECTION;
import ErrorCorrectionLevel.L;
import ImageType.GIF;
import ImageType.JPG;
import ImageType.PNG;
import android.graphics.Bitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import net.glxn.qrgen.core.exception.QRGenerationException;
import net.glxn.qrgen.core.scheme.VCard;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = { QRGenShadowBitmap.class })
public class QRCodeTest {
    @Test
    public void shouldCreateBitmapWithDefaults() throws Exception {
        Bitmap bmp = QRCode.from("www.example.org").bitmap();
        Assert.assertNotNull(bmp);
    }

    @Test
    public void shouldGetFileAsBitmapWithDefaults() throws Exception {
        File file = QRCode.from("www.example.org").to(ImageType.BMP).file();
        Assert.assertNotNull(file);
    }

    @Test
    public void shouldGetFileFromVCardWithDefaults() throws Exception {
        VCard johnDoe = new VCard("John Doe").setName("John Doe").setEmail("john.doe@example.org").setAddress("John Doe Street 1, 5678 Berlin").setTitle("Mister").setCompany("John Doe Inc.").setPhoneNumber("1234").setWebsite("www.example.org");
        File file = QRCode.from(johnDoe).file();
        Assert.assertNotNull(file);
    }

    @Test
    public void shouldGetFileFromTextWithDefaults() throws Exception {
        File file = QRCode.from("Hello World").file();
        Assert.assertNotNull(file);
    }

    @Test
    public void shouldGetFileWithNameFromTextWithDefaults() throws Exception {
        File file = QRCode.from("Hello World").file("Hello World");
        Assert.assertNotNull(file);
        Assert.assertTrue(file.getName().startsWith("Hello World"));
    }

    @Test
    public void shouldGetSTREAMFromTextWithDefaults() throws Exception {
        ByteArrayOutputStream stream = QRCode.from("Hello World").stream();
        Assert.assertNotNull(stream);
    }

    @Test
    public void shouldHandleLargeString() throws Exception {
        int length = 2950;
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = 'a';
        }
        String text = new String(chars);
        Assert.assertEquals(length, text.length());
        File file = QRCode.from(text).to(PNG).file();
        Assert.assertNotNull(file);
    }

    @Test
    public void shouldGetFileWithNameFromTextWithImageTypeOverrides() throws Exception {
        File jpg = QRCode.from("Hello World").to(JPG).file("Hello World");
        Assert.assertNotNull(jpg);
        Assert.assertTrue(jpg.getName().startsWith("Hello World"));
        File gif = QRCode.from("Hello World").to(GIF).file("Hello World");
        Assert.assertNotNull(gif);
        Assert.assertTrue(gif.getName().startsWith("Hello World"));
    }

    @Test
    public void shouldGetStreamFromText() throws Exception {
        ByteArrayOutputStream stream = QRCode.from("Hello World").to(PNG).stream();
        Assert.assertNotNull(stream);
        File tempFile = File.createTempFile("test", ".tmp");
        long lengthBefore = tempFile.length();
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        stream.writeTo(fileOutputStream);
        Assert.assertTrue((lengthBefore < (tempFile.length())));
    }

    @Test
    public void shouldWriteToSuppliedStream() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        QRCode.from("Hello World").writeTo(stream);
        Assert.assertNotNull(stream);
        File tempFile = File.createTempFile("test", ".tmp");
        long lengthBefore = tempFile.length();
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        stream.writeTo(fileOutputStream);
        Assert.assertTrue((lengthBefore < (tempFile.length())));
    }

    @Test
    public void shouldBeAbleToOverrideDimensionsToFile() throws Exception {
        long defaultSize = QRCode.from("Hello World").to(PNG).file().length();
        long defaultSize2 = QRCode.from("Hello World").to(PNG).file().length();
        File file = QRCode.from("Hello World").to(PNG).withSize(250, 250).file();
        Assert.assertNotNull(file);
        Assert.assertTrue((defaultSize == defaultSize2));
        // This seems not to work due to some limitations of robolectric, the genrated file on disk is also broken
        // Assert.assertTrue(defaultSize < file.length());
    }

    @Test
    public void shouldBeAbleToOverrideDimensionsToFileWithName() throws Exception {
        long defaultSize = QRCode.from("Hello World").to(PNG).file("Hello World").length();
        long defaultSize2 = QRCode.from("Hello World").to(PNG).file("Hello World").length();
        File file = QRCode.from("Hello World").to(PNG).withSize(250, 250).file("Hello World");
        Assert.assertNotNull(file);
        Assert.assertTrue((defaultSize == defaultSize2));
        // This seems not to work due to some limitations of robolectric, the genrated file on disk is also broken
        // Assert.assertTrue(defaultSize < file.length());
        Assert.assertTrue(file.getName().startsWith("Hello World"));
    }

    @Test
    public void shouldBeAbleToSupplyEncodingHint() throws Exception {
        String expected = "UTF-8";
        final Object[] capture = new Object[1];
        try {
            final QRCode from = QRCode.from("Jour f?ri?");
            from.setQrWriter(writerWithCapture(capture));
            from.to(PNG).withCharset(expected).stream();
        } catch (QRGenerationException ignored) {
        }
        assertCapturedHint(expected, capture, CHARACTER_SET);
    }

    @Test
    public void shouldBeAbleToSupplyErrorCorrectionHint() throws Exception {
        ErrorCorrectionLevel expected = ErrorCorrectionLevel.L;
        final Object[] capture = new Object[1];
        try {
            final QRCode from = QRCode.from("Jour f?ri?");
            from.setQrWriter(writerWithCapture(capture));
            from.to(PNG).withErrorCorrection(L).stream();
        } catch (QRGenerationException ignored) {
        }
        assertCapturedHint(expected, capture, ERROR_CORRECTION);
    }

    @Test
    public void shouldBeAbleToSupplyAnyHint() throws Exception {
        String expected = "a hint";
        EncodeHintType[] hintTypes = EncodeHintType.values();
        for (EncodeHintType type : hintTypes) {
            final Object[] capture = new Object[1];
            try {
                final QRCode from = QRCode.from("Jour f?ri?");
                from.setQrWriter(writerWithCapture(capture));
                from.to(PNG).withHint(type, expected).stream();
            } catch (QRGenerationException ignored) {
            }
            assertCapturedHint(expected, capture, type);
        }
    }
}

