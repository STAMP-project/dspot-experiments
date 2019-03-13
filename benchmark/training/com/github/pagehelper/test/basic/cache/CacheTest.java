/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.pagehelper.test.basic.cache;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.mapper.CountryMapper;
import com.github.pagehelper.model.Country;
import com.github.pagehelper.util.MybatisHelper;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;


/**
 * ???ms??????
 */
public class CacheTest {
    /**
     * ??Mapper????????PageHelper.startPage??????????Mapper????
     */
    @Test
    public void testMapperWithStartPage() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        CountryMapper countryMapper = sqlSession.getMapper(CountryMapper.class);
        try {
            // ???1??10??????????count
            PageHelper.startPage(1, 10);
            List<Country> list = countryMapper.selectGreterThanId(10);
            Assert.assertEquals(10, list.size());
            // ???1??10??????????count
            PageHelper.startPage(2, 10);
            list = countryMapper.selectGreterThanIdAndNotEquelContryname(10, "china");
            Assert.assertEquals(10, list.size());
            // ???1??10??????????count
            PageHelper.startPage(3, 10);
            list = countryMapper.selectGreterThanIdAndNotEquelContryname(10, "china");
            Assert.assertEquals(10, list.size());
            // ???1??10??????????count
            PageHelper.startPage(4, 10);
            list = countryMapper.selectGreterThanIdAndNotEquelContryname(10, "china");
            Assert.assertEquals(10, list.size());
            // ???1??10??????????count
            PageHelper.startPage(5, 10);
            list = countryMapper.selectGreterThanIdAndNotEquelContryname(10, "china");
            Assert.assertEquals(10, list.size());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??Mapper????????PageHelper.startPage??????????Mapper????
     */
    @Test
    public void testThreads() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        sqlSession.close();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread thread1 = new Thread(new CacheTest.CacheThread());
        Thread thread2 = new Thread(new CacheTest.CacheThread());
        Thread thread3 = new Thread(new CacheTest.CacheThread());
        Thread thread4 = new Thread(new CacheTest.CacheThread());
        Thread thread5 = new Thread(new CacheTest.CacheThread());
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class CacheThread implements Runnable {
        private CacheThread() {
        }

        public void run() {
            SqlSession sqlSession = MybatisHelper.getSqlSession();
            System.out.println(((Thread.currentThread().getId()) + "????..."));
            CountryMapper countryMapper = sqlSession.getMapper(CountryMapper.class);
            // ???1??10??????????count
            PageHelper.startPage(1, 10);
            List<Country> list = countryMapper.selectGreterThanIdAndNotEquelContryname(10, "china");
            Assert.assertEquals(10, list.size());
            // ???2??10??????????count
            PageHelper.startPage(2, 10);
            list = countryMapper.selectGreterThanIdAndNotEquelContryname(10, "china");
            Assert.assertEquals(10, list.size());
            sqlSession.close();
        }
    }
}

