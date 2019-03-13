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
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.RepositoryTestBase;
import org.pentaho.di.trans.TransMeta;
import org.w3c.dom.Node;


public class PurRepository_SharedObjects_Links_IT extends PurRepositoryIT {
    public PurRepository_SharedObjects_Links_IT(Boolean lazyRepo) {
        super(lazyRepo);
    }

    private interface GenericMeta {
        public AbstractMeta createFilled() throws Exception;

        public void loadFromXml(Node xmlNode) throws Exception;

        public AbstractMeta createEmpty();
    }

    @Test
    public void testReadSharedObjects_Trans() throws Exception {
        testReadSharedObjects(new PurRepository_SharedObjects_Links_IT.GenericMeta() {
            private TransMeta meta;

            @Override
            public void loadFromXml(Node xmlNode) throws Exception {
                meta.loadXML(xmlNode, repository, true, null);
            }

            @Override
            public AbstractMeta createFilled() throws Exception {
                meta = createTransMeta(RepositoryTestBase.EXP_DBMETA_NAME);
                return meta;
            }

            @Override
            public AbstractMeta createEmpty() {
                meta = new TransMeta();
                return meta;
            }
        });
    }

    @Test
    public void testReadSharedObjects_Job() throws Exception {
        testReadSharedObjects(new PurRepository_SharedObjects_Links_IT.GenericMeta() {
            private JobMeta meta;

            @Override
            public void loadFromXml(Node xmlNode) throws Exception {
                meta.loadXML(xmlNode, repository, null);
            }

            @Override
            public AbstractMeta createFilled() throws Exception {
                meta = createJobMeta(RepositoryTestBase.EXP_DBMETA_NAME);
                return meta;
            }

            @Override
            public AbstractMeta createEmpty() {
                meta = new JobMeta();
                return meta;
            }
        });
    }
}

