/**
 * Copyright 2011-2015 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parceler.internal;


import android.os.Parcel;
import android.os.Parcelable;
import java.lang.reflect.InvocationTargetException;
import javax.inject.Inject;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.parceler.Parcels;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
@Bootstrap
public class ParcelableIntegrationTest {
    private static final String TEST_VALUE = "test value";

    @Inject
    private ASTClassFactory astClassFactory;

    @Inject
    private CodeGenerationUtil codeGenerationUtil;

    @Inject
    private ParcelableGenerator parcelableGenerator;

    @Inject
    private ParcelableAnalysis parcelableAnalysis;

    private Parcel parcel;

    private Class<Parcelable> parcelableClass;

    @Test
    public void testGeneratedParcelable() throws IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        ParcelTarget parcelTarget = new ParcelTarget();
        ParcelSecondTarget parcelSecondTarget = new ParcelSecondTarget();
        parcelTarget.setDoubleValue(Math.PI);
        parcelTarget.setStringValue(ParcelableIntegrationTest.TEST_VALUE);
        parcelTarget.setSecondTarget(parcelSecondTarget);
        parcelSecondTarget.setValue(ParcelableIntegrationTest.TEST_VALUE);
        Parcelable outputParcelable = parcelableClass.getConstructor(ParcelTarget.class).newInstance(parcelTarget);
        outputParcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Parcelable inputParcelable = ((Parcelable.Creator<Parcelable>) (parcelableClass.getField("CREATOR").get(null))).createFromParcel(parcel);
        ParcelTarget wrapped = Parcels.unwrap(inputParcelable);
        Assert.assertEquals(parcelTarget, wrapped);
    }
}

