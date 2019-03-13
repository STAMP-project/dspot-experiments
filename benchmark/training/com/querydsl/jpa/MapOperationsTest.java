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
package com.querydsl.jpa;


import org.junit.Assert;
import org.junit.Test;


public class MapOperationsTest extends AbstractQueryTest {
    @Test
    public void map_with_groupBy() {
        Assert.assertEquals("select show_acts_0\nfrom Show show\n  left join show.acts as show_acts_0 on key(show_acts_0) = ?1\ngroup by show_acts_0", new com.querydsl.jpa.impl.JPAQuery<Void>().from(Constants.show).select(Constants.show.acts.get("A")).groupBy(Constants.show.acts.get("A")).toString());
    }
}

