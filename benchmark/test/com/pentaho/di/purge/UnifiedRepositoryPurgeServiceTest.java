/**
 * !
 * Copyright 2010 - 2018 Hitachi Vantara.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pentaho.di.purge;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryElementInterface;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.VersionSummary;


/**
 * Created by tkafalas 7/14/14.
 */
public class UnifiedRepositoryPurgeServiceTest {
    private static final String[][] versionData = new String[][]{ new String[]{ "100", "1", "01/01/2000", "Bugs Bunny", "original", "1.0" }, new String[]{ "101", "1", "01/01/2002", "Bugs Bunny", "1st change", "1.1" }, new String[]{ "102", "1", "01/01/2004", "Micky Mouse", "2nd change", "1.2" }, new String[]{ "103", "1", "01/01/2006", "Micky Mouse", "3rd change", "1.3" }, new String[]{ "104", "1", "01/01/2008", "Micky Mouse", "4th change", "1.4" }, new String[]{ "105", "1", "01/01/2010", "Donald Duck", "5th change", "1.5" }, new String[]{ "200", "2", "01/01/2001", "Donald Duck", "original", "1.0" }, new String[]{ "201", "2", "01/01/2003", "Fred Flintstone", "1st change", "1.1" }, new String[]{ "202", "2", "01/01/2005", "Fred Flintstone", "2nd change", "1.2" }, new String[]{ "203", "2", "01/01/2013", "Barny Rubble", "3rd change", "1.3" } };

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    private static final String treeResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + (((("<repositoryFileTreeDto><children><children><file><folder>false</folder><hidden>false</hidden><id>1</id><locked>false</locked><name>file1.ktr</name><ownerType>-1</ownerType><path>/home/joe/file1.ktr</path><versionId>1.5</versionId><versioned>true</versioned></file></children>" + "<children><children><file><folder>false</folder><hidden>false</hidden><id>2</id><locked>false</locked><name>file2.ktr</name><ownerType>-1</ownerType><path>/home/joe/newdirTest/file2.ktr</path><versionId>1.3</versionId><versioned>true</versioned></file></children>") + "</children><file><folder>true</folder><hidden>false</hidden><id>homejoessn</id><locked>false</locked><name>joe</name><ownerType>-1</ownerType><path>/home/joe</path><versioned>false</versioned></file></children>") + "<file><folder>true</folder><hidden>false</hidden><id>homessn</id><locked>false</locked><name>home</name><ownerType>-1</ownerType><path>/home</path><versioned>false</versioned></file>") + "</repositoryFileTreeDto>");

    private static RepositoryElementInterface element1;

    static {
        // Setup a mocked RepositoryElementInterface so alternate methods can be called for maximum code coverage
        UnifiedRepositoryPurgeServiceTest.element1 = Mockito.mock(RepositoryElementInterface.class);
        ObjectId mockObjectId1 = Mockito.mock(ObjectId.class);
        Mockito.when(mockObjectId1.getId()).thenReturn("1");
        Mockito.when(UnifiedRepositoryPurgeServiceTest.element1.getObjectId()).thenReturn(mockObjectId1);
    }

    @Test
    public void deleteAllVersionsTest() throws KettleException {
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        final HashMap<String, List<VersionSummary>> versionListMap = processVersionMap(mockRepo);
        UnifiedRepositoryPurgeService purgeService = new UnifiedRepositoryPurgeService(mockRepo);
        String fileId = "1";
        purgeService.deleteAllVersions(UnifiedRepositoryPurgeServiceTest.element1);
        UnifiedRepositoryPurgeServiceTest.verifyAllVersionsDeleted(versionListMap, mockRepo, "1");
        Mockito.verify(mockRepo, Mockito.never()).deleteFileAtVersion(ArgumentMatchers.eq("2"), ArgumentMatchers.anyString());
    }

    @Test
    public void deleteVersionTest() throws KettleException {
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        final HashMap<String, List<VersionSummary>> versionListMap = processVersionMap(mockRepo);
        UnifiedRepositoryPurgeService purgeService = new UnifiedRepositoryPurgeService(mockRepo);
        String fileId = "1";
        String versionId = "103";
        purgeService.deleteVersion(UnifiedRepositoryPurgeServiceTest.element1, versionId);
        Mockito.verify(mockRepo, Mockito.times(1)).deleteFileAtVersion(fileId, versionId);
        Mockito.verify(mockRepo, Mockito.never()).deleteFileAtVersion(ArgumentMatchers.eq("2"), ArgumentMatchers.anyString());
    }

    @Test
    public void keepNumberOfVersions0Test() throws KettleException {
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        final HashMap<String, List<VersionSummary>> versionListMap = processVersionMap(mockRepo);
        UnifiedRepositoryPurgeService purgeService = new UnifiedRepositoryPurgeService(mockRepo);
        String fileId = "1";
        int versionCount = 0;
        purgeService.keepNumberOfVersions(UnifiedRepositoryPurgeServiceTest.element1, versionCount);
        UnifiedRepositoryPurgeServiceTest.verifyVersionCountDeletion(versionListMap, mockRepo, fileId, versionCount);
        Mockito.verify(mockRepo, Mockito.never()).deleteFileAtVersion(ArgumentMatchers.eq("2"), ArgumentMatchers.anyString());
    }

    @Test
    public void keepNumberOfVersionsTest() throws KettleException {
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        final HashMap<String, List<VersionSummary>> versionListMap = processVersionMap(mockRepo);
        UnifiedRepositoryPurgeService purgeService = new UnifiedRepositoryPurgeService(mockRepo);
        String fileId = "1";
        int versionCount = 2;
        purgeService.keepNumberOfVersions(UnifiedRepositoryPurgeServiceTest.element1, versionCount);
        UnifiedRepositoryPurgeServiceTest.verifyVersionCountDeletion(versionListMap, mockRepo, fileId, versionCount);
        Mockito.verify(mockRepo, Mockito.never()).deleteFileAtVersion(ArgumentMatchers.eq("2"), ArgumentMatchers.anyString());
    }

    @Test
    public void deleteVersionsBeforeDate() throws KettleException {
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        final HashMap<String, List<VersionSummary>> versionListMap = processVersionMap(mockRepo);
        UnifiedRepositoryPurgeService purgeService = new UnifiedRepositoryPurgeService(mockRepo);
        String fileId = "1";
        Date beforeDate = UnifiedRepositoryPurgeServiceTest.getDate("01/02/2006");
        purgeService.deleteVersionsBeforeDate(UnifiedRepositoryPurgeServiceTest.element1, beforeDate);
        UnifiedRepositoryPurgeServiceTest.verifyDateBeforeDeletion(versionListMap, mockRepo, fileId, beforeDate);
        Mockito.verify(mockRepo, Mockito.never()).deleteFileAtVersion(ArgumentMatchers.eq("2"), ArgumentMatchers.anyString());
    }

    @Test
    public void doPurgeUtilPurgeFileTest() throws PurgeDeletionException {
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        final HashMap<String, List<VersionSummary>> versionListMap = processVersionMap(mockRepo);
        UnifiedRepositoryPurgeService purgeService = UnifiedRepositoryPurgeServiceTest.getPurgeService(mockRepo);
        PurgeUtilitySpecification spec = new PurgeUtilitySpecification();
        spec.purgeFiles = true;
        spec.setPath("/");
        purgeService.doDeleteRevisions(spec);
        UnifiedRepositoryPurgeServiceTest.verifyAllVersionsDeleted(versionListMap, mockRepo, "1");
        UnifiedRepositoryPurgeServiceTest.verifyAllVersionsDeleted(versionListMap, mockRepo, "2");
        Mockito.verify(UnifiedRepositoryPurgeService.getRepoWs(), Mockito.times(1)).deleteFileWithPermanentFlag(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(true), ArgumentMatchers.anyString());
        Mockito.verify(UnifiedRepositoryPurgeService.getRepoWs(), Mockito.times(1)).deleteFileWithPermanentFlag(ArgumentMatchers.eq("2"), ArgumentMatchers.eq(true), ArgumentMatchers.anyString());
    }

    @Test
    public void doPurgeUtilVersionCountTest() throws PurgeDeletionException {
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        final HashMap<String, List<VersionSummary>> versionListMap = processVersionMap(mockRepo);
        UnifiedRepositoryPurgeService purgeService = UnifiedRepositoryPurgeServiceTest.getPurgeService(mockRepo);
        PurgeUtilitySpecification spec = new PurgeUtilitySpecification();
        spec.setVersionCount(3);
        spec.setPath("/");
        purgeService.doDeleteRevisions(spec);
        UnifiedRepositoryPurgeServiceTest.verifyVersionCountDeletion(versionListMap, mockRepo, "1", spec.getVersionCount());
        UnifiedRepositoryPurgeServiceTest.verifyVersionCountDeletion(versionListMap, mockRepo, "2", spec.getVersionCount());
    }

    @Test
    public void doPurgeUtilDateBeforeTest() throws PurgeDeletionException {
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        final HashMap<String, List<VersionSummary>> versionListMap = processVersionMap(mockRepo);
        UnifiedRepositoryPurgeService purgeService = UnifiedRepositoryPurgeServiceTest.getPurgeService(mockRepo);
        PurgeUtilitySpecification spec = new PurgeUtilitySpecification();
        spec.setBeforeDate(UnifiedRepositoryPurgeServiceTest.getDate("01/02/2006"));
        spec.setPath("/");
        purgeService.doDeleteRevisions(spec);
        UnifiedRepositoryPurgeServiceTest.verifyDateBeforeDeletion(versionListMap, mockRepo, "1", spec.getBeforeDate());
        UnifiedRepositoryPurgeServiceTest.verifyDateBeforeDeletion(versionListMap, mockRepo, "2", spec.getBeforeDate());
    }

    @Test
    public void doPurgeUtilSharedObjectsTest() throws PurgeDeletionException {
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        final HashMap<String, List<VersionSummary>> versionListMap = processVersionMap(mockRepo);
        UnifiedRepositoryPurgeService purgeService = UnifiedRepositoryPurgeServiceTest.getPurgeService(mockRepo);
        PurgeUtilitySpecification spec = new PurgeUtilitySpecification();
        spec.purgeFiles = true;
        spec.setSharedObjects(true);
        purgeService.doDeleteRevisions(spec);
        // Since each tree call delivers the same mock tree, we expect the files to get deleted once per folder.
        String fileId = "1";
        String fileLastRevision = "105";
        List<VersionSummary> list = versionListMap.get(fileId);
        for (VersionSummary sum : list) {
            final int expectedTimes;
            if (!(fileLastRevision.equals(sum.getId()))) {
                expectedTimes = 4;
            } else {
                expectedTimes = 0;
            }
            Mockito.verify(mockRepo, Mockito.times(expectedTimes)).deleteFileAtVersion(fileId, sum.getId());
            Mockito.verify(UnifiedRepositoryPurgeService.getRepoWs(), Mockito.times(4)).deleteFileWithPermanentFlag(ArgumentMatchers.eq(fileId), ArgumentMatchers.eq(true), ArgumentMatchers.anyString());
        }
    }
}

