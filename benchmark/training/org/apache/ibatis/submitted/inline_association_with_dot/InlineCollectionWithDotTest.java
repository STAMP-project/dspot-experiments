/**
 * Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.submitted.inline_association_with_dot;


import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;


public class InlineCollectionWithDotTest {
    private SqlSession sqlSession;

    /* Load an element with an element with and element with a value. Expect that this is
    possible bij using an inline 'association' map.
     */
    @Test
    public void selectElementValueInContainerUsingInline() throws Exception {
        openSession("inline");
        Element myElement = sqlSession.getMapper(ElementMapperUsingInline.class).selectElement();
        Assert.assertEquals("value", myElement.getElement().getElement().getValue());
    }

    /* Load an element with an element with and element with a value. Expect that this is
    possible bij using an sub-'association' map.
     */
    @Test
    public void selectElementValueInContainerUsingSubMap() throws Exception {
        openSession("submap");
        Element myElement = sqlSession.getMapper(ElementMapperUsingSubMap.class).selectElement();
        Assert.assertEquals("value", myElement.getElement().getElement().getValue());
    }
}

