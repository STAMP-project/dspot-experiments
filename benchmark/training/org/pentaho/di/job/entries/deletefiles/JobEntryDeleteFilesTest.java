/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.job.entries.deletefiles;


import Const.EMPTY_STRING;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.job.Job;
import org.pentaho.di.trans.steps.named.cluster.NamedClusterEmbedManager;


public class JobEntryDeleteFilesTest {
    private final String PATH_TO_FILE = "path/to/file";

    private final String STRING_SPACES_ONLY = "   ";

    private JobEntryDeleteFiles jobEntry;

    private NamedClusterEmbedManager mockNamedClusterEmbedManager;

    @Test
    public void filesWithNoPath_AreNotProcessed_ArgsOfCurrentJob() throws Exception {
        jobEntry.setArguments(new String[]{ Const.EMPTY_STRING, STRING_SPACES_ONLY });
        jobEntry.setFilemasks(new String[]{ null, null });
        jobEntry.setArgFromPrevious(false);
        jobEntry.execute(new Result(), 0);
        Mockito.verify(jobEntry, Mockito.never()).processFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Job.class));
    }

    @Test
    public void filesWithPath_AreProcessed_ArgsOfCurrentJob() throws Exception {
        String[] args = new String[]{ PATH_TO_FILE };
        jobEntry.setArguments(args);
        jobEntry.setFilemasks(new String[]{ null, null });
        jobEntry.setArgFromPrevious(false);
        jobEntry.execute(new Result(), 0);
        Mockito.verify(jobEntry, Mockito.times(args.length)).processFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Job.class));
        Mockito.verify(mockNamedClusterEmbedManager).passEmbeddedMetastoreKey(ArgumentMatchers.anyObject(), ArgumentMatchers.anyString());
    }

    @Test
    public void filesWithNoPath_AreNotProcessed_ArgsOfPreviousMeta() throws Exception {
        jobEntry.setArgFromPrevious(true);
        Result prevMetaResult = new Result();
        List<RowMetaAndData> metaAndDataList = new ArrayList<>();
        metaAndDataList.add(constructRowMetaAndData(EMPTY_STRING, null));
        metaAndDataList.add(constructRowMetaAndData(STRING_SPACES_ONLY, null));
        prevMetaResult.setRows(metaAndDataList);
        jobEntry.execute(prevMetaResult, 0);
        Mockito.verify(jobEntry, Mockito.never()).processFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Job.class));
    }

    @Test
    public void filesPath_AreProcessed_ArgsOfPreviousMeta() throws Exception {
        jobEntry.setArgFromPrevious(true);
        Result prevMetaResult = new Result();
        List<RowMetaAndData> metaAndDataList = new ArrayList<>();
        metaAndDataList.add(constructRowMetaAndData(PATH_TO_FILE, null));
        prevMetaResult.setRows(metaAndDataList);
        jobEntry.execute(prevMetaResult, 0);
        Mockito.verify(jobEntry, Mockito.times(metaAndDataList.size())).processFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Job.class));
    }

    @Test
    public void filesPathVariables_AreProcessed_OnlyIfValueIsNotBlank() throws Exception {
        final String pathToFileBlankValue = "pathToFileBlankValue";
        final String pathToFileValidValue = "pathToFileValidValue";
        jobEntry.setVariable(pathToFileBlankValue, EMPTY_STRING);
        jobEntry.setVariable(pathToFileValidValue, PATH_TO_FILE);
        jobEntry.setArguments(new String[]{ asVariable(pathToFileBlankValue), asVariable(pathToFileValidValue) });
        jobEntry.setFilemasks(new String[]{ null, null });
        jobEntry.setArgFromPrevious(false);
        jobEntry.execute(new Result(), 0);
        Mockito.verify(jobEntry).processFile(ArgumentMatchers.eq(PATH_TO_FILE), ArgumentMatchers.anyString(), ArgumentMatchers.any(Job.class));
    }

    @Test
    public void specifyingTheSamePath_WithDifferentWildcards() throws Exception {
        final String fileExtensionTxt = ".txt";
        final String fileExtensionXml = ".xml";
        String[] args = new String[]{ PATH_TO_FILE, PATH_TO_FILE };
        jobEntry.setArguments(args);
        jobEntry.setFilemasks(new String[]{ fileExtensionTxt, fileExtensionXml });
        jobEntry.setArgFromPrevious(false);
        jobEntry.execute(new Result(), 0);
        Mockito.verify(jobEntry).processFile(ArgumentMatchers.eq(PATH_TO_FILE), ArgumentMatchers.eq(fileExtensionTxt), ArgumentMatchers.any(Job.class));
        Mockito.verify(jobEntry).processFile(ArgumentMatchers.eq(PATH_TO_FILE), ArgumentMatchers.eq(fileExtensionXml), ArgumentMatchers.any(Job.class));
    }
}

