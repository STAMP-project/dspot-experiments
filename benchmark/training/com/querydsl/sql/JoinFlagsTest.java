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


import JoinFlag.Position.BEFORE_CONDITION;
import JoinFlag.Position.BEFORE_TARGET;
import JoinFlag.Position.END;
import JoinFlag.Position.OVERRIDE;
import JoinFlag.Position.START;
import com.querydsl.sql.domain.QSurvey;
import java.sql.Connection;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class JoinFlagsTest {
    private Connection connection = EasyMock.createMock(Connection.class);

    private QSurvey s1;

    private QSurvey s2;

    private QSurvey s3;

    private QSurvey s4;

    private QSurvey s5;

    private QSurvey s6;

    private SQLQuery query;

    @SuppressWarnings("unchecked")
    @Test
    public void joinFlags_beforeCondition() {
        query.innerJoin(s2).on(eq(s2));
        query.addJoinFlag(" a ", BEFORE_CONDITION);
        Assert.assertEquals(("from SURVEY s\n" + ("inner join SURVEY s2 a \n" + "on s.ID = s2.ID")), query.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void joinFlags_beforeTarget() {
        query.innerJoin(s3).on(eq(s3));
        query.addJoinFlag(" b ", BEFORE_TARGET);
        Assert.assertEquals(("from SURVEY s\n" + ("inner join  b SURVEY s3\n" + "on s.ID = s3.ID")), query.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void joinFlags_end() {
        query.innerJoin(s4).on(eq(s4));
        query.addJoinFlag(" c ", END);
        Assert.assertEquals(("from SURVEY s\n" + ("inner join SURVEY s4\n" + "on s.ID = s4.ID c")), query.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void joinFlags_override() {
        query.innerJoin(s5).on(eq(s5));
        query.addJoinFlag(" d ", OVERRIDE);
        Assert.assertEquals(("from SURVEY s d SURVEY s5\n" + "on s.ID = s5.ID"), query.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void joinFlags_start() {
        query.innerJoin(s6).on(eq(s6));
        query.addJoinFlag(" e ", START);
        Assert.assertEquals(("from SURVEY s e \n" + ("inner join SURVEY s6\n" + "on s.ID = s6.ID")), query.toString());
    }
}

