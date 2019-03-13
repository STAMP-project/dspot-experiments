/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2016 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.remote;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.tools.StringTools;
import org.languagetool.tools.Tools;


public class RemoteLanguageToolTest {
    @Test
    public void testResultParsing() throws IOException {
        RemoteLanguageTool lt = new RemoteLanguageToolTest.FakeRemoteLanguageTool("response.json");
        RemoteResult result1 = lt.check("some text, reply is hard-coded anyway", "en");
        runAsserts(result1);
        CheckConfiguration config = new CheckConfigurationBuilder().build();
        RemoteResult result2 = lt.check("some text, reply is hard-coded anyway", config);
        runAsserts(result2);
        RemoteLanguageTool lt2 = new RemoteLanguageToolTest.FakeRemoteLanguageTool("response-with-url.json");
        RemoteResult result3 = lt2.check("some text, reply is hard-coded anyway", config);
        Assert.assertThat(result3.getMatches().get(0).getUrl().get(), Is.is("https://fake.org/foo"));
    }

    private static class FakeRemoteLanguageTool extends RemoteLanguageTool {
        private final String jsonFile;

        FakeRemoteLanguageTool(String jsonFile) throws MalformedURLException {
            super(new URL("http://fake"));
            this.jsonFile = jsonFile;
        }

        @Override
        HttpURLConnection getConnection(byte[] postData, URL url) {
            URL fakeUrl = Tools.getUrl("https://fake");
            return new HttpURLConnection(fakeUrl) {
                @Override
                public void disconnect() {
                }

                @Override
                public boolean usingProxy() {
                    return false;
                }

                @Override
                public void connect() throws IOException {
                }

                @Override
                public int getResponseCode() {
                    return HttpURLConnection.HTTP_OK;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    String response = StringTools.readStream(RemoteLanguageToolTest.class.getResourceAsStream(("/org/languagetool/remote/" + (jsonFile))), "utf-8");
                    return new ByteArrayInputStream(response.getBytes());
                }
            };
        }
    }
}

