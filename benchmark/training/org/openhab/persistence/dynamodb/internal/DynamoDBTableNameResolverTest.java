/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.persistence.dynamodb.internal;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sami Salonen
 */
public class DynamoDBTableNameResolverTest {
    @Test
    public void testWithDynamoDBBigDecimalItem() {
        Assert.assertEquals("prefixbigdecimal", new DynamoDBTableNameResolver("prefix").fromItem(new DynamoDBBigDecimalItem()));
    }

    @Test
    public void testWithDynamoDBStringItem() {
        Assert.assertEquals("prefixstring", new DynamoDBTableNameResolver("prefix").fromItem(new DynamoDBStringItem()));
    }
}

