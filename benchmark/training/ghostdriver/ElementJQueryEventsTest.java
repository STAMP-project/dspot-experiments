/**
 * This file is part of the GhostDriver by Ivan De Marino <http://ivandemarino.me>.
 *
 * Copyright (c) 2012-2014, Ivan De Marino <http://ivandemarino.me>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ghostdriver;


import ghostdriver.server.HttpRequestCallback;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


@RunWith(Parameterized.class)
public class ElementJQueryEventsTest extends BaseTestWithServer {
    private String mJqueryVersion;

    public ElementJQueryEventsTest(String jQueryVersion) {
        mJqueryVersion = jQueryVersion;
    }

    @Test
    public void shouldBeAbleToClickAndEventsBubbleUpUsingJquery() {
        final String buttonId = "clickme";
        server.setHttpHandler("GET", new HttpRequestCallback() {
            @Override
            public void call(HttpServletRequest req, HttpServletResponse res) throws IOException {
                res.getOutputStream().println((((((((((((((((((((("<html>\n" + ("<head>\n" + "<script src=\"//ajax.googleapis.com/ajax/libs/jquery/")) + (mJqueryVersion)) + "/jquery.min.js\"></script>\n") + "<script type=\"text/javascript\">\n") + "   var clicked = false;") + "   $(document).ready(function() {") + "       $('#") + buttonId) + "').bind('click', function(e) {") + "           clicked = true;") + "       });") + "   });\n") + "</script>\n") + "</head>\n") + "<body>\n") + "    <a href='#' id='") + buttonId) + "\'>click me</a>\n") + "</body>\n") + "</html>"));
            }
        });
        WebDriver d = getDriver();
        d.get(server.getBaseUrl());
        // Click on the link inside the page
        d.findElement(By.id(buttonId)).click();
        // Check element was clicked as expected
        Assert.assertTrue(((Boolean) (executeScript("return clicked;"))));
    }
}

