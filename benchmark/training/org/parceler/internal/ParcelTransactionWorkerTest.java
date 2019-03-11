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


import javax.inject.Provider;
import org.androidtransfuse.adapter.ASTAnnotation;
import org.androidtransfuse.adapter.ASTType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author John Ericksen
 */
public class ParcelTransactionWorkerTest {
    private ParcelTransactionWorker parcelTransaction;

    private ParcelableAnalysis mockAnalysis;

    private ParcelableGenerator mockGenerator;

    private ParcelableDescriptor mockDescriptor;

    private Provider inputProvider;

    private ASTType input;

    private ASTAnnotation mockASTAnnotation;

    @Test
    public void test() {
        Mockito.when(inputProvider.get()).thenReturn(input);
        Mockito.when(input.getASTAnnotation(ArgumentMatchers.any(Class.class))).thenReturn(mockASTAnnotation);
        Mockito.when(mockAnalysis.analyze(input, mockASTAnnotation)).thenReturn(mockDescriptor);
        Assert.assertFalse(parcelTransaction.isComplete());
        parcelTransaction.run(inputProvider);
        Mockito.verify(mockGenerator).generateParcelable(input, mockDescriptor);
        Assert.assertTrue(parcelTransaction.isComplete());
    }
}

