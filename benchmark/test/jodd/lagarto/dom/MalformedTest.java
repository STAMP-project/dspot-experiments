/**
 * Copyright (c) 2003-present, Jodd Team (http://jodd.org)
 */
/**
 * All rights reserved.
 */
/**
 *
 */
/**
 * Redistribution and use in source and binary forms, with or without
 */
/**
 * modification, are permitted provided that the following conditions are met:
 */
/**
 *
 */
/**
 * 1. Redistributions of source code must retain the above copyright notice,
 */
/**
 * this list of conditions and the following disclaimer.
 */
/**
 *
 */
/**
 * 2. Redistributions in binary form must reproduce the above copyright
 */
/**
 * notice, this list of conditions and the following disclaimer in the
 */
/**
 * documentation and/or other materials provided with the distribution.
 */
/**
 *
 */
/**
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 */
/**
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 */
/**
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 */
/**
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 */
/**
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 */
/**
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 */
/**
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 */
/**
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 */
/**
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 */
/**
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 */
/**
 * POSSIBILITY OF SUCH DAMAGE.
 */
package jodd.lagarto.dom;


import java.io.IOException;
import jodd.util.StringUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class MalformedTest {
    protected String testDataRoot;

    @Test
    public void smtest() throws IOException {
        String html = read("smtest.html", false);
        LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
        lagartoDOMBuilder.enableHtmlPlusMode();
        lagartoDOMBuilder.enableDebug();
        Document doc = lagartoDOMBuilder.parse(html);
        html = html(doc);
        String out = read("smtest-out.html", true);
        // still not working
        out = StringUtil.remove(out, "<tbody>\n");
        out = StringUtil.remove(out, "</tbody>\n");
        html = StringUtil.replace(html, "<td>\nnotworking</td>", "<tr>\n<td>\nnotworking</td>\n</tr>");
        Assertions.assertEquals(out, html);
    }
}

