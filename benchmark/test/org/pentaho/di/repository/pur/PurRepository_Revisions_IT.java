/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
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
package org.pentaho.di.repository.pur;


import org.junit.Test;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.TransMeta;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class PurRepository_Revisions_IT extends PurRepositoryTestBase {
    public PurRepository_Revisions_IT(Boolean lazyRepo) {
        super(lazyRepo);
    }

    @Test
    public void onlyRevision_DataAndCommentAreSaved_Trans() throws Exception {
        testOnlyRevision_DateAndCommentAreSaved(new TransMeta());
    }

    @Test
    public void onlyRevision_DataAndCommentAreSaved_Job() throws Exception {
        testOnlyRevision_DateAndCommentAreSaved(new JobMeta());
    }

    @Test
    public void onlyRevision_DataAndCommentAreNull_Trans() throws Exception {
        testOnlyRevision_DateAndCommentAreNull(new TransMeta());
    }

    @Test
    public void onlyRevision_DataAndCommentAreNull_Job() throws Exception {
        testOnlyRevision_DateAndCommentAreNull(new JobMeta());
    }

    @Test
    public void twoRevisions_DataAndCommentAreSaved_Trans() throws Exception {
        testTwoRevisions_DateAndCommentAreSaved(new TransMeta());
    }

    @Test
    public void twoRevisions_DataAndCommentAreSaved_Job() throws Exception {
        testTwoRevisions_DateAndCommentAreSaved(new JobMeta());
    }
}

