/**
 * Copyright (C) 2018 The Android Open Source Project
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
package com.google.android.exoplayer2.video.spherical;


import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link ProjectionDecoder}.
 */
@RunWith(RobolectricTestRunner.class)
public final class ProjectionDecoderTest {
    private static final byte[] PROJ_DATA = Util.getBytesFromHexString(("0000008D70726F6A0000008579746D7000000000ABA158D672617720000000716D65736800000006BF800000" + (("3F8000003F0000003F2AAAAB000000003EAAAAAB000000100024200104022430010421034020400123" + "1020401013020010102222001001003100200010320010000000010000000000240084009066080420") + "9020108421002410860214C1200660")));

    private static final int MSHP_OFFSET = 16;

    private static final int VERTEX_COUNT = 36;

    private static final float[] FIRST_VERTEX = new float[]{ -1.0F, -1.0F, 1.0F };

    private static final float[] LAST_VERTEX = new float[]{ 1.0F, -1.0F, -1.0F };

    private static final float[] FIRST_UV = new float[]{ 0.5F, 1.0F };

    private static final float[] LAST_UV = new float[]{ 1.0F, 1.0F };

    @Test
    public void testDecodeProj() {
        ProjectionDecoderTest.testDecoding(ProjectionDecoderTest.PROJ_DATA);
    }

    @Test
    public void testDecodeMshp() {
        ProjectionDecoderTest.testDecoding(Arrays.copyOfRange(ProjectionDecoderTest.PROJ_DATA, ProjectionDecoderTest.MSHP_OFFSET, ProjectionDecoderTest.PROJ_DATA.length));
    }
}

