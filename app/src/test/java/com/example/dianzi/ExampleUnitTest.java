package com.example.dianzi;

import org.junit.Test;

import com.example.dianzi.common.SubsetSum;

import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void testSubsetSum() {
       // int arr[] = {1, 2, 3, 4, 5};
        int arr[] = {

                160401,
                146286,
                148221,
                136869,
                140672,
                145204,
                136398,
                131564,
                134912,
                124196,
                143594,
                146985,
                128232,
                124474,
                123360,
                126140,
                130662,
                96746,
                131150,
                130011,
                127734,
                125434
};
        int n = arr.length;
        int sum = 877653;
        SubsetSum subsetSum = new SubsetSum();
        ArrayList<ArrayList<Integer>> result = subsetSum.findAllSubsets(arr, sum);
        for(ArrayList<Integer> path : result) {
            System.out.println(path);
        }
    }

    @Test
    public void ocr() {
        System.out.println("ocr init start");
        try {

            TessBaseAPI tessBaseAPI = new TessBaseAPI();
            tessBaseAPI.init("/data/data/com.example.dianzi/files", "chi_sim");
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("ocr init done");
    }
}