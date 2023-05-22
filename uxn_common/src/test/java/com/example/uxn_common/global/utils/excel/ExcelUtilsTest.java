package com.example.uxn_common.global.utils.excel;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
class ExcelUtilsTest {

    @Test
    void intFunctionTest(){
        ExcelUtils excelUtils = new ExcelUtils();
        int result = excelUtils.intFunction(12.345); // -> 캐스팅 해줘야 12 로 떨어짐 안해주면 12.0

        System.out.println(result);
    }

    @Test
    void medianFunctionTest(){
        ExcelUtils excelUtils = new ExcelUtils();
        double[] a;
//        a = new double[] {1,2,3,4};
////        Arrays.sort(a); // 크기 순으로 정렬
//        System.out.println(excelUtils.medianFunction(a)); // 2.5

        a = new double[] {4,3,1,2};

//        Arrays.sort(a);
//  MEDIAN 함수 (중간값)
//  MEDIAN 함수는 '중간값'을 계산합니다. 중간값이란 크기의 순서대로 정렬했을 때 가장 중앙에 위치하는 값을 말합니다. 다시 말해 나열된 숫자 중 절반은 중간값보다 작거나 같고 나머지 절반은 중간값보다 크거나 같게됩니다. -> 정렬을 해줘야 한다.
        System.out.println(excelUtils.medianFunction(a)); // 2.5 -> sort 해주지 않으면 2.0 나옴.

        a = new double[] {1,2,3,4,5};
//        Arrays.sort(a);
        System.out.println(excelUtils.medianFunction(a));

        a = new double[] {3,5,4,1,2}; // sort 안해주면 4 나옴.
//        Arrays.sort(a);
        System.out.println(excelUtils.medianFunction(a));

        //  =MEDIAN({1,2,5,8,100},{3,7,9,11,90})
        //  =MEDIAN({1,2,3,5,7,8,9,11,90,100})
        //  =7.5
        a = new double[] {1,2,3,5,7,8,9,11,90,100}; // 엑셀값 7.5
//        Arrays.sort(a);
        System.out.println(excelUtils.medianFunction(a)); // 7.5

        a = new double[] {3,2,5,3,7}; // 엑셀값 3
//        Arrays.sort(a);
        System.out.println(excelUtils.medianFunction(a)); //3.0 -> sort 안했을시 5

        a = new double[] {105,99,102};
        System.out.println(excelUtils.medianFunction(a));
    }

    @Test
    void roundFunctionTest(){
        ExcelUtils excelUtils = new ExcelUtils();
//        double dResult = excelUtils.roundFunction(1.44455555555,4); // 엑셀 값 1.4446
        double test = Math.round((99+102)/2.0);
        System.out.println(test);
        double dResult = excelUtils.roundFunction(test,0);
        System.out.println(dResult);
    }

    @Test
    void returnListRoundFunction(){
        ExcelUtils excelUtils = new ExcelUtils();
        ArrayList<Double> dResult = excelUtils.returnListRoundFunction(100.5,0); // 엑셀 값 1.4446
        System.out.println(dResult);

    }

    @Test
    void smallFunctionTest(){
        ExcelUtils excelUtils = new ExcelUtils();
        double[] a = new double[]{4,5,3,1,2};
        int[] b = new int[] {1,4}; // 기댓값 : 1,4 -> 1,4
        System.out.println(excelUtils.smallFunction(a,b)); // position : 1,2,3 -> [1,2,3]

    }

    @Test
    void smallListFunctionTest(){
        ExcelUtils excelUtils = new ExcelUtils();
        List<Long> diabetesLevelList = new ArrayList<>();
        diabetesLevelList.add(5L);
        diabetesLevelList.add(6L);
        diabetesLevelList.add(7L);
        diabetesLevelList.add(9L);
        diabetesLevelList.add(1L);
        int[] b = new int[] {2,3,5}; // 기댓값 : 5,6,9 -> 결과값 : 5,6,9
//        System.out.println(excelUtils.smallListFunction(diabetesLevelList, b));
    }

    @Test
    void medianListTest(){
        ExcelUtils excelUtils = new ExcelUtils();
        List<Long> level = new ArrayList<>();
        level.add(90L);
        level.add(80L);
        level.add(110L);
        level.add(70L);
//        excelUtils.medianListFunction(level);
    }

    @Test
    void integrationTest(){ // 엑셀 시트상에서 백분위 수 계산 -> 컨트롤러 통해서 만들어줘야 할 최종 코드.
        ExcelUtils excelUtils = new ExcelUtils();
        String test = "";
        double percentile = 0.05; // 파라미터로 하나의 값만 받아야 하는지 or 퍼센트 별로 다 계산해야 되는지

        double[] dataArray = new double[] {105, 99, 102, 101, 96, 98, 99, 102, 100, 101, 98, 98, 95, 99}; // 데이터를 입력 받아야 하나?
        double testNum = 0.7;

        int intResult = excelUtils.intFunction(testNum + 1);

        int[] positionArray = new int[] {intResult};

        ArrayList<Integer> smallFunctionResult = new ArrayList<>();


        if (percentile == 0.05) {
            test = "5%";
            smallFunctionResult = excelUtils.smallFunction(dataArray, positionArray); // 95
        } else if (percentile == 0.25) {
            test = "25%";
        } else if (percentile == 0.5) {
            test = "50%";
        } else if (percentile == 0.75) {
            test = "75%";
        } else if (percentile == 0.95) {
            test = "95%";
        }
        System.out.println(test);
        System.out.println(smallFunctionResult);



    }



}