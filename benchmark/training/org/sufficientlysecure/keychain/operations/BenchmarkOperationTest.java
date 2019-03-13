/**
 * Copyright (C) 2014 Vincent Breitmoser <v.breitmoser@mugenguild.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sufficientlysecure.keychain.operations;


import RuntimeEnvironment.application;
import java.io.PrintStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.daos.KeyWritableRepository;
import org.sufficientlysecure.keychain.service.BenchmarkInputParcel;


@RunWith(KeychainTestRunner.class)
public class BenchmarkOperationTest {
    static PrintStream oldShadowStream;

    @Test
    public void testBenchmark() throws Exception {
        BenchmarkOperation op = new BenchmarkOperation(RuntimeEnvironment.application, KeyWritableRepository.create(application), null);
        op.execute(BenchmarkInputParcel.newInstance(), null);
    }
}

