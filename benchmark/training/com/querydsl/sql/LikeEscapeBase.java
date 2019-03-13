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


import com.querydsl.sql.domain.QSurvey;
import org.junit.Assert;
import org.junit.Test;


public class LikeEscapeBase extends AbstractBaseTest {
    @Test
    public void like() {
        Assert.assertEquals(0, query().from(Constants.survey).where(Constants.survey.name.like("a!%")).fetchCount());
        Assert.assertEquals(0, query().from(Constants.survey).where(Constants.survey.name.like("a!_")).fetchCount());
        Assert.assertEquals(3, query().from(Constants.survey).where(Constants.survey.name.like("a%")).fetchCount());
        Assert.assertEquals(2, query().from(Constants.survey).where(Constants.survey.name.like("a_")).fetchCount());
        Assert.assertEquals(1, query().from(Constants.survey).where(Constants.survey.name.startsWith("a_")).fetchCount());
        Assert.assertEquals(1, query().from(Constants.survey).where(Constants.survey.name.startsWith("a%")).fetchCount());
    }

    @Test
    public void like_with_escape() {
        Assert.assertEquals(1, query().from(Constants.survey).where(Constants.survey.name.like("a!%", '!')).fetchCount());
        Assert.assertEquals(1, query().from(Constants.survey).where(Constants.survey.name.like("a!_", '!')).fetchCount());
        Assert.assertEquals(3, query().from(Constants.survey).where(Constants.survey.name.like("a%", '!')).fetchCount());
        Assert.assertEquals(2, query().from(Constants.survey).where(Constants.survey.name.like("a_", '!')).fetchCount());
    }

    @Test
    public void like_escaping_conclusion() {
        Assert.assertTrue("Escaped like construct must return more results", ((query().from(Constants.survey).where(Constants.survey.name.like("a!%")).fetchCount()) < (query().from(Constants.survey).where(Constants.survey.name.like("a!%", '!')).fetchCount())));
        Assert.assertTrue("Escaped like construct must return more results", ((query().from(Constants.survey).where(Constants.survey.name.like("a!_")).fetchCount()) < (query().from(Constants.survey).where(Constants.survey.name.like("a!_", '!')).fetchCount())));
    }
}

