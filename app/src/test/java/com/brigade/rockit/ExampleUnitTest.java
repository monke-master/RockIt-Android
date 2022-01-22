package com.brigade.rockit;

import org.junit.Test;

import static org.junit.Assert.*;

import com.brigade.rockit.database.TimeManager;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    TimeManager manager = new TimeManager();

    @Test
    public void test_day_dif_1() {
        assertEquals(2, manager.getDayDiff("15.05.2018/23:24", "17.05.2018/23:24"));
    }

    @Test
    public void test_day_dif_2() {
        assertEquals(367, manager.getDayDiff("15.05.2018/23:24", "17.05.2019/23:24"));
    }
    @Test
    public void test_day_dif_3() {
        assertEquals(59, manager.getDayDiff("15.07.2018/23:24", "17.05.2018/23:24"));
    }


}