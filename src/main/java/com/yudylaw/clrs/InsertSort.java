
package com.yudylaw.clrs;

/**
 * @author liuyu3@xiaomi.com
 * @since 2015年12月28日
 * 算法复杂度O(N^2)
 */

public class InsertSort {

    public static void main(String[] args) {
        int[] arr = { 0, 23, 42, 10, 232, 2002, 3, 2 };

        arr = sort(arr);
        
        for (int i : arr) {
            System.out.println(i);
        }
    }

    public static int[] sort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int j = i;
            //swap with sorted numbers
            while (j > 0 && arr[j - 1] > arr[j]) {
                int temp = arr[j - 1];
                arr[j - 1] = arr[j];
                arr[j] = temp;
                j--;
            }
        }
        return arr;
    }

}
