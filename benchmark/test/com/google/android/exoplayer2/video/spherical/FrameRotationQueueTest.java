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


import android.opengl.Matrix;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests {@link FrameRotationQueue}.
 */
@RunWith(RobolectricTestRunner.class)
public class FrameRotationQueueTest {
    private FrameRotationQueue frameRotationQueue;

    private float[] rotationMatrix;

    @Test
    public void testGetRotationMatrixReturnsNull_whenEmpty() throws Exception {
        assertThat(frameRotationQueue.pollRotationMatrix(rotationMatrix, 0)).isFalse();
    }

    @Test
    public void testGetRotationMatrixReturnsNotNull_whenNotEmpty() throws Exception {
        frameRotationQueue.setRotation(0, new float[]{ 1, 2, 3 });
        assertThat(frameRotationQueue.pollRotationMatrix(rotationMatrix, 0)).isTrue();
        assertThat(rotationMatrix).hasLength(16);
    }

    @Test
    public void testConvertsAngleAxisToRotationMatrix() throws Exception {
        /* angleRadian= */
        /* x= */
        /* y= */
        /* z= */
        doTestAngleAxisToRotationMatrix(0, 1, 0, 0);
        frameRotationQueue.reset();
        /* angleRadian= */
        /* x= */
        /* y= */
        /* z= */
        doTestAngleAxisToRotationMatrix(1, 1, 0, 0);
        frameRotationQueue.reset();
        /* angleRadian= */
        /* x= */
        /* y= */
        /* z= */
        doTestAngleAxisToRotationMatrix(1, 0, 0, 1);
        // Don't reset frameRotationQueue as we use recenter matrix from previous calls.
        /* angleRadian= */
        /* x= */
        /* y= */
        /* z= */
        doTestAngleAxisToRotationMatrix((-1), 0, 1, 0);
        /* angleRadian= */
        /* x= */
        /* y= */
        /* z= */
        doTestAngleAxisToRotationMatrix(1, 1, 1, 1);
    }

    @Test
    public void testRecentering_justYaw() throws Exception {
        float[] actualMatrix = /* angleRadian= */
        /* x= */
        /* y= */
        /* z= */
        getRotationMatrixFromAngleAxis(((float) (Math.PI)), 0, 1, 0);
        float[] expectedMatrix = new float[16];
        Matrix.setIdentityM(expectedMatrix, 0);
        FrameRotationQueueTest.assertEquals(actualMatrix, expectedMatrix);
    }

    @Test
    public void testRecentering_yawAndPitch() throws Exception {
        float[] matrix = /* angleRadian= */
        /* x= */
        /* y= */
        /* z= */
        getRotationMatrixFromAngleAxis(((float) (Math.PI)), 1, 1, 0);
        /* xr= */
        /* yr= */
        /* zr= */
        /* x= */
        /* y= */
        /* z= */
        FrameRotationQueueTest.assertMultiplication(0, 0, 1, matrix, 0, 0, 1);
    }

    @Test
    public void testRecentering_yawAndPitch2() throws Exception {
        float[] matrix = /* angleRadian= */
        /* x= */
        /* y= */
        /* z= */
        getRotationMatrixFromAngleAxis((((float) (Math.PI)) / 2), 1, 1, 0);
        float sqrt2 = ((float) (Math.sqrt(2)));
        /* xr= */
        /* yr= */
        /* zr= */
        /* x= */
        /* y= */
        /* z= */
        FrameRotationQueueTest.assertMultiplication(sqrt2, 0, 0, matrix, 1, (-1), 0);
    }

    @Test
    public void testRecentering_yawAndPitchAndRoll() throws Exception {
        float[] matrix = /* angleRadian= */
        /* x= */
        /* y= */
        /* z= */
        getRotationMatrixFromAngleAxis(((((float) (Math.PI)) * 2) / 3), 1, 1, 1);
        /* xr= */
        /* yr= */
        /* zr= */
        /* x= */
        /* y= */
        /* z= */
        FrameRotationQueueTest.assertMultiplication(0, 0, 1, matrix, 0, 0, 1);
    }
}

