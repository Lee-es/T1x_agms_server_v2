package com.example.uxn_common.global.utils.excel;

import java.util.*;


public class ExcelUtils {

    public int intFunction(double number){
        return (int)Math.floor(number); // 소수점 없이 내림.
    }

    public double medianFunction(double[] array){ // 받을때 크기 순으로 이미 정렬된 배열을 받아야 함.

        Arrays.sort(array); // 혹시 모르니 여기서 sorting 하기로 한다.
//        double[] b = array; // sort 해주면 순서대로 정렬 된다.
        if(array.length == 0){
            return Double.NaN; // 빈 배영은 에러 반환 (NaN은 숫자가 아니라는 뜻)
        }
        int center = array.length/2;

        if(array.length % 2 == 1){ // 요소 갯수가 홀수면.
            return array[center]; // 홀수 개수인 배열에서는 중간 요소를 그대로 반환
        }else {

            return (array[center -1] + array[center]) / 2.0; // 짝수 개 요소는, 중간 두 수의 평균 반환.
        }
    }

    public Double medianListFunction(List<Double> list){
        Collections.sort(list); // 리스트 순서대로 정렬

        if (list.isEmpty()){
            return null;
        }
        int center =  list.size()/2;
        if (list.size() % 2 == 1){
            return list.get(center);
        }else{
            return (list.get(center -1 ) + list.get(center))/ 2;
        }
    }

    public double roundListFunction(List<Long> dNumber, int position){ // 실수, 소수점 자리 지정 -> 현재 음수는 지정 못함.
        return Double.parseDouble(
                String.format("%."+Integer.toString(position) + "f", dNumber)
        );
    }

    public ArrayList<Double> returnListRoundFunction(double dNumber, int position){ // 실수, 소수점 자리 지정 -> 현재 음수는 지정 못함.
        ArrayList<Double> arrayList = new ArrayList<>();
         var value = Double.parseDouble(
                String.format("%."+Integer.toString(position) + "f", dNumber)
        ); // value = 1.4446

        arrayList.add(value);
        return arrayList;
    }

    public Set<Double> returnSetRoundFunction(double dNumber, int position){ // 실수, 소수점 자리 지정 -> 현재 음수는 지정 못함.
        Set<Double> arrayList = new HashSet<>();
        var value = Double.parseDouble(
                String.format("%."+Integer.toString(position) + "f", dNumber)
        ); // value = 1.4446

        arrayList.add(value);
        return arrayList;
    }

    public double roundFunction(double dNumber, int position){ // 실수, 소수점 자리 지정 -> 현재 음수는 지정 못함.
        return Double.parseDouble(
                String.format("%."+Integer.toString(position) + "f", dNumber)
        );
    }


    public ArrayList<Integer> smallFunction(double[] array, int[] positionArray){ // 범위 내에서 k 번째로 작은 값을 반환 = SMALL ( 범위, 순번(k) )

        ArrayList<Integer> arrayList = new ArrayList<>(); // 배열 push 를 해주기 위한.

        Arrays.sort(array); // 순서대로 정렬 -> 정렬 안해도 되지만 순서대로 정렬을 해놔야 코드가 안더러워 질것 같다.
        Arrays.sort(positionArray); // 순서대로 정렬(정렬을 해줘야 위치 찾기가 편하다.) {1,2} -> 첫번째 두번째로 작은 수 출력 -> 위에서 array sorting 을 했기 떄문에 0번 , 1번 index 출력하면 됨.

        for(int x : positionArray){
            arrayList.add((int) array[x-1]);
        }
        return arrayList;
    }

    public ArrayList<Double> smallListFunction(List<Double>list, int[] positionArray){
        Collections.sort(list);
        ArrayList<Double> arrayList = new ArrayList<>();
        Arrays.sort(positionArray);
        for (int x : positionArray){
            if (x == 0){
                arrayList.add(list.get(x));
            }else {
                arrayList.add(list.get(x-1));
            }
        }
        return arrayList;
    }

    public Set<Double> smallSetFunction(List<Double>list, int[] positionArray){
        Collections.sort(list);
        Set<Double> arrayList = new HashSet<>();
        Arrays.sort(positionArray);
        for (int x : positionArray){
            arrayList.add(list.get(x-1));
        }
        return arrayList;
    }
}
