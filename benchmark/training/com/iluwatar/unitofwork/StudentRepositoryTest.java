/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Piyush Chaudhari
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.iluwatar.unitofwork;


import IUnitOfWork.DELETE;
import IUnitOfWork.INSERT;
import IUnitOfWork.MODIFY;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * tests {@link StudentRepository}
 */
@RunWith(MockitoJUnitRunner.class)
public class StudentRepositoryTest {
    private final Student student1 = new Student(1, "Ram", "street 9, cupertino");

    private final Student student2 = new Student(1, "Sham", "Z bridge, pune");

    private Map<String, List<Student>> context;

    @Mock
    private StudentDatabase studentDatabase;

    private StudentRepository studentRepository;

    @Test
    public void shouldSaveNewStudentWithoutWritingToDb() {
        studentRepository.registerNew(student1);
        studentRepository.registerNew(student2);
        Assert.assertEquals(2, context.get(INSERT).size());
        Mockito.verifyNoMoreInteractions(studentDatabase);
    }

    @Test
    public void shouldSaveDeletedStudentWithoutWritingToDb() {
        studentRepository.registerDeleted(student1);
        studentRepository.registerDeleted(student2);
        Assert.assertEquals(2, context.get(DELETE).size());
        Mockito.verifyNoMoreInteractions(studentDatabase);
    }

    @Test
    public void shouldSaveModifiedStudentWithoutWritingToDb() {
        studentRepository.registerModified(student1);
        studentRepository.registerModified(student2);
        Assert.assertEquals(2, context.get(MODIFY).size());
        Mockito.verifyNoMoreInteractions(studentDatabase);
    }

    @Test
    public void shouldSaveAllLocalChangesToDb() {
        context.put(INSERT, Collections.singletonList(student1));
        context.put(MODIFY, Collections.singletonList(student1));
        context.put(DELETE, Collections.singletonList(student1));
        studentRepository.commit();
        Mockito.verify(studentDatabase, Mockito.times(1)).insert(student1);
        Mockito.verify(studentDatabase, Mockito.times(1)).modify(student1);
        Mockito.verify(studentDatabase, Mockito.times(1)).delete(student1);
    }

    @Test
    public void shouldNotWriteToDbIfContextIsNull() {
        StudentRepository studentRepository = new StudentRepository(null, studentDatabase);
        studentRepository.commit();
        Mockito.verifyNoMoreInteractions(studentDatabase);
    }

    @Test
    public void shouldNotWriteToDbIfNothingToCommit() {
        StudentRepository studentRepository = new StudentRepository(new HashMap(), studentDatabase);
        studentRepository.commit();
        Mockito.verifyZeroInteractions(studentDatabase);
    }

    @Test
    public void shouldNotInsertToDbIfNoRegisteredStudentsToBeCommitted() {
        context.put(MODIFY, Collections.singletonList(student1));
        context.put(DELETE, Collections.singletonList(student1));
        studentRepository.commit();
        Mockito.verify(studentDatabase, Mockito.never()).insert(student1);
    }

    @Test
    public void shouldNotModifyToDbIfNotRegisteredStudentsToBeCommitted() {
        context.put(INSERT, Collections.singletonList(student1));
        context.put(DELETE, Collections.singletonList(student1));
        studentRepository.commit();
        Mockito.verify(studentDatabase, Mockito.never()).modify(student1);
    }

    @Test
    public void shouldNotDeleteFromDbIfNotRegisteredStudentsToBeCommitted() {
        context.put(INSERT, Collections.singletonList(student1));
        context.put(MODIFY, Collections.singletonList(student1));
        studentRepository.commit();
        Mockito.verify(studentDatabase, Mockito.never()).delete(student1);
    }
}

