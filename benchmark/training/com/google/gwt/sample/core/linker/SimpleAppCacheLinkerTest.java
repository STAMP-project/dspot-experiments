/**
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.sample.core.linker;


import TreeLogger.NULL;
import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.SelectionProperty;
import com.google.gwt.core.ext.linker.SyntheticArtifact;
import java.util.SortedSet;
import java.util.TreeSet;
import junit.framework.TestCase;


/**
 * Tests {@link SimpleAppCacheLinker}.
 */
public class SimpleAppCacheLinkerTest extends TestCase {
    /**
     * A {@code SimpleAppCacheLinker} mocking the addition of a static entry.
     */
    public static class OneStaticFileAppCacheLinker extends SimpleAppCacheLinker {
        @Override
        protected String[] otherCachedFiles() {
            return new String[]{ "aStaticFile" };
        }
    }

    private static class MockLinkerContext implements LinkerContext {
        public SortedSet<ConfigurationProperty> getConfigurationProperties() {
            return new TreeSet<ConfigurationProperty>();
        }

        public String getModuleFunctionName() {
            return null;
        }

        public long getModuleLastModified() {
            return 0;
        }

        public String getModuleName() {
            return null;
        }

        public SortedSet<SelectionProperty> getProperties() {
            return new TreeSet<SelectionProperty>();
        }

        public boolean isOutputCompact() {
            return true;
        }

        public String optimizeJavaScript(TreeLogger logger, String jsProgram) {
            return jsProgram;
        }
    }

    private ArtifactSet artifacts;

    private TreeLogger logger;

    public void testAddCachableArtifacts() throws UnableToCompleteException {
        SimpleAppCacheLinker linker = new SimpleAppCacheLinker();
        // Some cacheable artifact
        artifacts.add(new SyntheticArtifact(SimpleAppCacheLinker.class, "foo.bar", new byte[0]));
        ArtifactSet result = linker.link(logger, new SimpleAppCacheLinkerTest.MockLinkerContext(), artifacts, false);
        TestCase.assertEquals(3, result.size());
        assertHasOneManifest(result);
        TestCase.assertTrue(getManifestContents(result).contains("foo.bar"));
    }

    public void testAddStaticFiles() throws UnableToCompleteException {
        SimpleAppCacheLinker linker = new SimpleAppCacheLinkerTest.OneStaticFileAppCacheLinker();
        ArtifactSet result = linker.link(logger, new SimpleAppCacheLinkerTest.MockLinkerContext(), artifacts, false);
        TestCase.assertEquals(2, result.size());
        assertHasOneManifest(result);
        TestCase.assertTrue(getManifestContents(result).contains("aStaticFile"));
    }

    public void testEmptyManifestDevMode() throws UnableToCompleteException {
        // No SelectionInformation artifact
        artifacts = new ArtifactSet();
        SimpleAppCacheLinker linker = new SimpleAppCacheLinker();
        // Some cacheable artifact
        artifacts.add(new SyntheticArtifact(SimpleAppCacheLinker.class, "foo.bar", new byte[0]));
        ArtifactSet result = linker.link(logger, new SimpleAppCacheLinkerTest.MockLinkerContext(), artifacts, false);
        assertHasOneManifest(result);
        TestCase.assertFalse(getManifestContents(result).contains("foo.bar"));
    }

    public void testManifestOnlyOnLastPass() throws UnableToCompleteException {
        SimpleAppCacheLinker linker = new SimpleAppCacheLinker();
        ArtifactSet result = linker.link(logger, new SimpleAppCacheLinkerTest.MockLinkerContext(), artifacts, true);
        TestCase.assertEquals(artifacts, result);
        result = linker.link(logger, new SimpleAppCacheLinkerTest.MockLinkerContext(), artifacts, false);
        TestCase.assertEquals(2, result.size());
        assertHasOneManifest(result);
    }

    public void testNoNonCachableArtifacts() throws UnableToCompleteException {
        SimpleAppCacheLinker linker = new SimpleAppCacheLinker();
        // Some non-cacheable artifacts
        artifacts.add(new SyntheticArtifact(SimpleAppCacheLinker.class, "soycReport.baz", new byte[0]));
        artifacts.add(new SyntheticArtifact(SimpleAppCacheLinker.class, "foo.symbolMap", new byte[0]));
        artifacts.add(new SyntheticArtifact(SimpleAppCacheLinker.class, "foo.xml.gz", new byte[0]));
        artifacts.add(new SyntheticArtifact(SimpleAppCacheLinker.class, "foo.rpc.log", new byte[0]));
        artifacts.add(new SyntheticArtifact(SimpleAppCacheLinker.class, "foo.gwt.rpc", new byte[0]));
        artifacts.add(new SyntheticArtifact(SimpleAppCacheLinker.class, "rpcPolicyManifest.bar", new byte[0]));
        ArtifactSet result = linker.link(NULL, new SimpleAppCacheLinkerTest.MockLinkerContext(), artifacts, false);
        TestCase.assertEquals(8, result.size());
        assertHasOneManifest(result);
        TestCase.assertFalse(getManifestContents(result).contains("soycReport"));
        TestCase.assertFalse(getManifestContents(result).contains("symbolMap"));
        TestCase.assertFalse(getManifestContents(result).contains("xml.gz"));
        TestCase.assertFalse(getManifestContents(result).contains("rpc.log"));
        TestCase.assertFalse(getManifestContents(result).contains("gwt.rpc"));
        TestCase.assertFalse(getManifestContents(result).contains("rpcPolicyManifest"));
    }
}

