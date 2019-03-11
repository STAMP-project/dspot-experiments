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
package com.querydsl.collections;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static QCat.cat;


public class CollDeleteClauseTest {
    @Test
    public void execute() {
        QCat cat = cat;
        List<Cat> cats = new ArrayList<Cat>(Arrays.asList(new Cat("Ann"), new Cat("Bob"), new Cat("John"), new Cat("Carl")));
        CollDeleteClause<Cat> deleteClause = new CollDeleteClause<Cat>(cat, cats);
        deleteClause.where(cat.name.eq("Bob"));
        Assert.assertEquals(1, deleteClause.execute());
        Assert.assertEquals(3, cats.size());
        Assert.assertEquals("Ann", cats.get(0).getName());
        Assert.assertEquals("John", cats.get(1).getName());
        Assert.assertEquals("Carl", cats.get(2).getName());
    }
}

