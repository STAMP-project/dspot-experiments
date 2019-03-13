/**
 * Copyright ? 2010-2017 Nokia
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
package org.jsonschema2pojo.util;


import org.junit.Assert;
import org.junit.Test;


public class InflectorTest {
    @Test
    public void testSingularize() {
        Assert.assertThat(Inflector.getInstance().singularize("dwarves"), is("dwarf"));
        Assert.assertThat(Inflector.getInstance().singularize("curves"), is("curve"));
        Assert.assertThat(Inflector.getInstance().singularize("halves"), is("half"));
        Assert.assertThat(Inflector.getInstance().singularize("vertices"), is("vertex"));
        Assert.assertThat(Inflector.getInstance().singularize("proofs"), is("proof"));
        Assert.assertThat(Inflector.getInstance().singularize("moths"), is("moth"));
        Assert.assertThat(Inflector.getInstance().singularize("houses"), is("house"));
        Assert.assertThat(Inflector.getInstance().singularize("rooves"), is("roof"));
        Assert.assertThat(Inflector.getInstance().singularize("elves"), is("elf"));
        Assert.assertThat(Inflector.getInstance().singularize("baths"), is("bath"));
        Assert.assertThat(Inflector.getInstance().singularize("leaves"), is("leaf"));
        Assert.assertThat(Inflector.getInstance().singularize("calves"), is("calf"));
        Assert.assertThat(Inflector.getInstance().singularize("lives"), is("life"));
        Assert.assertThat(Inflector.getInstance().singularize("knives"), is("knife"));
        Assert.assertThat(Inflector.getInstance().singularize("addresses"), is("address"));
        Assert.assertThat(Inflector.getInstance().singularize("mattresses"), is("mattress"));
        Assert.assertThat(Inflector.getInstance().singularize("databases"), is("database"));
        Assert.assertThat(Inflector.getInstance().singularize("bison"), is("bison"));
        Assert.assertThat(Inflector.getInstance().singularize("buffalo"), is("buffalo"));
        Assert.assertThat(Inflector.getInstance().singularize("deer"), is("deer"));
        Assert.assertThat(Inflector.getInstance().singularize("fish"), is("fish"));
        Assert.assertThat(Inflector.getInstance().singularize("sheep"), is("sheep"));
        Assert.assertThat(Inflector.getInstance().singularize("squid"), is("squid"));
        Assert.assertThat(Inflector.getInstance().singularize("mattress"), is("mattress"));
        Assert.assertThat(Inflector.getInstance().singularize("address"), is("address"));
        Assert.assertThat(Inflector.getInstance().singularize("men"), is("man"));
        Assert.assertThat(Inflector.getInstance().singularize("women"), is("woman"));
        Assert.assertThat(Inflector.getInstance().singularize("specimen"), is("specimen"));
        Assert.assertThat(Inflector.getInstance().singularize("children"), is("child"));
        Assert.assertThat(Inflector.getInstance().singularize("s"), is("s"));
        Assert.assertThat(Inflector.getInstance().singularize("status"), is("status"));
        Assert.assertThat(Inflector.getInstance().singularize("statuses"), is("status"));
        Assert.assertThat(Inflector.getInstance().singularize("LineItemTaxes"), is("LineItemTax"));
        Assert.assertThat(Inflector.getInstance().singularize("WidgetList"), is("Widget"));
    }

    @Test
    public void testPluralize() {
        Assert.assertThat(Inflector.getInstance().pluralize("mattress"), is("mattresses"));
        Assert.assertThat(Inflector.getInstance().pluralize("address"), is("addresses"));
    }
}

