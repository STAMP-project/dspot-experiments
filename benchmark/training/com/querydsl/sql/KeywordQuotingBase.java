/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team).
 *
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
 */
package com.querydsl.sql;


import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Assert;
import org.junit.Test;


public class KeywordQuotingBase extends AbstractBaseTest {
    private static class Quoting extends RelationalPathBase<KeywordQuotingBase.Quoting> {
        public static final KeywordQuotingBase.Quoting quoting = new KeywordQuotingBase.Quoting("quoting");

        public final StringPath from = createString("from");

        public final BooleanPath all = createBoolean("all");

        private Quoting(String path) {
            super(KeywordQuotingBase.Quoting.class, PathMetadataFactory.forVariable(path), "PUBLIC", "quoting");
            addMetadata();
        }

        public Quoting(PathMetadata metadata) {
            super(KeywordQuotingBase.Quoting.class, metadata, "PUBLIC", "quoting");
            addMetadata();
        }

        protected void addMetadata() {
            addMetadata(from, ColumnMetadata.named("from"));
            addMetadata(all, ColumnMetadata.named("all"));
        }
    }

    private final KeywordQuotingBase.Quoting quoting = KeywordQuotingBase.Quoting.quoting;

    @Test
    public void keywords() {
        KeywordQuotingBase.Quoting from = new KeywordQuotingBase.Quoting("from");
        Assert.assertEquals("from", query().from(as(from)).where(from.from.eq("from").and(from.all.isNotNull())).select(from.from).fetchFirst());
    }
}

