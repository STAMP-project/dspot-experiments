/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.index;


import IndexAnalysisSettingsAccessor.INDEX_ANALYSIS_SETTINGS_OBJUID;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.search.QueryBuilder;


/**
 * Represents a test class for {@link IndexAnalysisSettings}.
 */
public class IndexAnalysisSettingsTest {
    private static final String PROJECT_NAME = "foo-1-2-3";

    private static final long ANALYZER_GURU_VERSION = 3;

    private static final int TABSIZE = 17;

    private static final Map<String, Long> ANALYZER_VERSIONS = new HashMap<>();

    @Test
    public void shouldAffirmINDEX_ANALYSIS_SETTINGS_OBJUID() {
        String objuid = QueryBuilder.normalizeDirPath("58859C75-F941-42E5-8D1A-FAF71DDEBBA7");
        Assert.assertEquals("IndexAnalysisSettingsDao objuid", objuid, INDEX_ANALYSIS_SETTINGS_OBJUID);
    }

    @Test
    public void shouldRoundTripANullObject() throws IOException, ClassNotFoundException {
        IndexAnalysisSettings obj = new IndexAnalysisSettings();
        byte[] bin = obj.serialize();
        IndexAnalysisSettings res = IndexAnalysisSettings.deserialize(bin);
        Assert.assertNotNull(res);
        Assert.assertEquals("projectName", null, res.getProjectName());
        Assert.assertEquals("tabSize", null, res.getTabSize());
        Assert.assertEquals("analyzerGuruVersion", null, res.getAnalyzerGuruVersion());
    }

    @Test
    public void shouldRoundTripADefinedObject() throws IOException, ClassNotFoundException {
        IndexAnalysisSettings obj = new IndexAnalysisSettings();
        obj.setProjectName(IndexAnalysisSettingsTest.PROJECT_NAME);
        obj.setAnalyzerGuruVersion(IndexAnalysisSettingsTest.ANALYZER_GURU_VERSION);
        obj.setTabSize(IndexAnalysisSettingsTest.TABSIZE);
        byte[] bin = obj.serialize();
        IndexAnalysisSettings res = IndexAnalysisSettings.deserialize(bin);
        Assert.assertNotNull(res);
        Assert.assertEquals("projectName", IndexAnalysisSettingsTest.PROJECT_NAME, res.getProjectName());
        Assert.assertEquals("tabSize", IndexAnalysisSettingsTest.TABSIZE, ((int) (res.getTabSize())));
        Assert.assertEquals("analyzerGuruVersion", IndexAnalysisSettingsTest.ANALYZER_GURU_VERSION, ((long) (res.getAnalyzerGuruVersion())));
    }
}

