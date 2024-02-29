package com.wkd.dev;

import java.util.*;

public class BinarySearch {
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        List<Integer> numbers = new ArrayList<>();
        Set<Integer> duplicatedChk = new HashSet<>();
        Random random = new Random();

        for(int i = 0; i < 5000; i++) {
            int ranNum = random.nextInt(1, 10001);

            if(duplicatedChk.contains(ranNum)) continue;

            numbers.add(ranNum);
            duplicatedChk.add(ranNum);
        }

        int[] arr = numbers.stream().mapToInt(Integer::intValue).toArray();
        Arrays.sort(arr);
        System.out.println("Arrays.toString(arr) = " + Arrays.toString(arr));

        int findIdx = binarySearch(arr, 0, arr.length-1, 156);

        int[] arr2 = {1, 3, 5, 7, 9};

        int idx = Arrays.binarySearch(arr2, 4);
        boolean isPresent = true;

        if(idx < 0) {
            idx = Math.abs(idx + 1);
            isPresent = false;
        }
        System.out.println("isPresent? " + (isPresent ? "YES" : "NO"));
        System.out.println("idx = " + idx);

        System.out.println("findIdx = " + findIdx);
        System.out.println("Arrays.binarySearch(arr, 51) = " + Arrays.binarySearch(arr, 20));

        long endTime = System.currentTimeMillis();
        System.out.println("소요시간은? " + (float)(endTime - startTime)/1000 + "s");
    }

    private static int binarySearch(int[] arr, int start, int end, int target) {

        if(start > end) return -1;

        int mid = start + (end-start) / 2;

        if(arr[mid] == target) {
            return mid;
        }
        if(arr[mid] > target) {
            return binarySearch(arr, start, mid - 1, target);
        } else {
            return binarySearch(arr, mid + 1, end, target);
        }
    }

}
