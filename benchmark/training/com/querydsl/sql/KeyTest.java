/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.sql;


import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.Test;


@SuppressWarnings("serial")
public class KeyTest {
    // @Table("USER")
    public static class QUser extends RelationalPathBase<KeyTest.QUser> {
        public final NumberPath<Integer> id = createNumber("id", Integer.class);

        public final NumberPath<Integer> department = createNumber("department", Integer.class);

        public final NumberPath<Integer> superiorId = createNumber("superiorId", Integer.class);

        public final PrimaryKey<KeyTest.QUser> idKey = createPrimaryKey(id);

        public final ForeignKey<KeyTest.QDepartment> departmentKey = createForeignKey(department, "ID");

        public final ForeignKey<KeyTest.QUser> superiorIdKey = createForeignKey(superiorId, "ID");

        public QUser(String path) {
            super(KeyTest.QUser.class, PathMetadataFactory.forVariable(path), "", "USER");
            addMetadata();
        }

        protected void addMetadata() {
            addMetadata(id, ColumnMetadata.named("ID"));
            addMetadata(department, ColumnMetadata.named("DEPARTMENT"));
            addMetadata(superiorId, ColumnMetadata.named("SUPERIOR_ID"));
        }
    }

    // @Table("DEPARTMENT")
    public static class QDepartment extends RelationalPathBase<KeyTest.QDepartment> {
        public final NumberPath<Integer> id = createNumber("id", Integer.class);

        public final NumberPath<Integer> company = createNumber("company", Integer.class);

        public final PrimaryKey<KeyTest.QDepartment> idKey = createPrimaryKey(id);

        public final ForeignKey<KeyTest.QCompany> companyKey = createForeignKey(company, "ID");

        public QDepartment(String path) {
            super(KeyTest.QDepartment.class, PathMetadataFactory.forVariable(path), "", "DEPARTMENT");
            addMetadata();
        }

        protected void addMetadata() {
            addMetadata(id, ColumnMetadata.named("ID"));
            addMetadata(company, ColumnMetadata.named("COMPANY"));
        }
    }

    // @Table("COMPANY")
    public static class QCompany extends RelationalPathBase<KeyTest.QCompany> {
        public final NumberPath<Integer> id = createNumber("id", Integer.class);

        public final PrimaryKey<KeyTest.QCompany> idKey = createPrimaryKey(id);

        public QCompany(String path) {
            super(KeyTest.QCompany.class, PathMetadataFactory.forVariable(path), "", "COMPANY");
            addMetadata();
        }

        protected void addMetadata() {
            addMetadata(id, ColumnMetadata.named("ID"));
        }
    }

    @Test
    public void test() {
        KeyTest.QUser user = new KeyTest.QUser("user");
        KeyTest.QUser user2 = new KeyTest.QUser("user2");
        KeyTest.QDepartment department = new KeyTest.QDepartment("department");
        KeyTest.QCompany company = new KeyTest.QCompany("company");
        // superiorId -> id
        SQLExpressions.selectOne().from(user).innerJoin(user.superiorIdKey, user2);
        // department -> id / company -> id
        SQLExpressions.selectOne().from(user).innerJoin(user.departmentKey, department).innerJoin(department.companyKey, company);
    }
}

