/**
 * This file is part of FNLP (formerly FudanNLP).
 *
 *  FNLP is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FNLP is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FudanNLP.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2009-2014 www.fnlp.org. All rights reserved.
 */
package org.fnlp.app.num;


import org.junit.Assert;
import org.junit.Test;


public class CNExpressionTest {
    CNExpression expr = new CNExpression();

    @Test
    public void test01() {
        int v;
        Loc loc = new Loc();
        for (int i = 0; i < 10; i++) {
            v = ((int) ((Math.random()) * 2147483647));
            loc.v = 0;
            Assert.assertTrue(((expr.chn2Num(expr.num2Chn(v), loc)) == v));
            System.out.println((((v + "\n") + (expr.num2Chn(v))) + "\n"));
        }
    }
}

