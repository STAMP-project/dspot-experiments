/**
 * -
 * #%L
 * rapidoid-networking
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.rapidoid.net.mime;


import MediaType.BMP;
import MediaType.CSS_UTF_8;
import MediaType.GIF;
import MediaType.HTML_UTF_8;
import MediaType.JAVASCRIPT_UTF8;
import MediaType.JPEG;
import MediaType.JSON;
import MediaType.PDF;
import MediaType.PLAIN_TEXT_UTF_8;
import MediaType.PNG;
import MediaType.SVG;
import MediaType.SWF;
import MediaType.XHTML_XML_UTF8;
import MediaType.ZIP;
import org.junit.jupiter.api.Test;
import org.rapidoid.NetTestCommons;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.MediaType;


@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class MediaTypeTest extends NetTestCommons {
    @Test
    public void testCommonMediaTypes() {
        eq(new String(PLAIN_TEXT_UTF_8.getBytes()), "text/plain; charset=utf-8");
        eq(MediaType.getByFileExtension("txt"), PLAIN_TEXT_UTF_8);
        eq(new String(HTML_UTF_8.getBytes()), "text/html; charset=utf-8");
        eq(MediaType.getByFileExtension("html"), HTML_UTF_8);
        eq(new String(XHTML_XML_UTF8.getBytes()), "application/xhtml+xml; charset=utf-8");
        eq(MediaType.getByFileExtension("xhtml"), XHTML_XML_UTF8);
        eq(new String(CSS_UTF_8.getBytes()), "text/css; charset=utf-8");
        eq(MediaType.getByFileExtension("css"), CSS_UTF_8);
        eq(new String(JSON.getBytes()), "application/json");
        eq(MediaType.getByFileExtension("json"), JSON);
        eq(new String(JAVASCRIPT_UTF8.getBytes()), "application/javascript; charset=utf-8");
        eq(MediaType.getByFileExtension("js"), JAVASCRIPT_UTF8);
        eq(new String(PDF.getBytes()), "application/pdf");
        eq(MediaType.getByFileExtension("pdf"), PDF);
        eq(new String(ZIP.getBytes()), "application/zip");
        eq(MediaType.getByFileExtension("zip"), ZIP);
        eq(new String(SWF.getBytes()), "application/x-shockwave-flash");
        eq(MediaType.getByFileExtension("swf"), SWF);
        eq(new String(JPEG.getBytes()), "image/jpeg");
        eq(MediaType.getByFileExtension("jpg"), JPEG);
        eq(MediaType.getByFileExtension("jpeg"), JPEG);
        eq(new String(PNG.getBytes()), "image/png");
        eq(MediaType.getByFileExtension("png"), PNG);
        eq(new String(GIF.getBytes()), "image/gif");
        eq(MediaType.getByFileExtension("gif"), GIF);
        eq(new String(BMP.getBytes()), "image/bmp");
        eq(MediaType.getByFileExtension("bmp"), BMP);
        eq(new String(SVG.getBytes()), "image/svg+xml; charset=utf-8");
        eq(MediaType.getByFileExtension("svg"), SVG);
    }

    @Test
    public void testCustomMediaType() {
        String[] attrss = new String[]{ "abc=xy" };
        MediaType myType = MediaType.create("text/some", attrss, "some");
        eq(new String(myType.getBytes()), "text/some; abc=xy");
        eq(MediaType.getByFileName("abc.some"), myType);
        eq(MediaType.getByFileName(".some"), myType);
        eq(MediaType.getByFileName("some"), MediaType.DEFAULT);
        eq(MediaType.getByFileName("someX"), MediaType.DEFAULT);
    }

    @Test
    public void testCustomMediaType2() {
        String[] attrss = new String[]{ "a=1", "b=abc" };
        MediaType myType = MediaType.create("app/my", attrss, "my1", "my2");
        eq(new String(myType.getBytes()), "app/my; a=1; b=abc");
        eq(MediaType.getByFileExtension("my1"), myType);
        eq(MediaType.getByFileExtension("my2"), myType);
        eq(MediaType.getByFileExtension("myX"), null);
    }
}

