package com.yudylaw.clrs;

/**
 * 
 * @author yudy
 * 算法复杂度O(N^2)
 */
public class SelectionSort {

	public static void main(String[] args) {

		int arr[] = { 10, 8, 2, 0, 12, 323, 232 };
		int[] arr2 = sort(arr);
		for (int i = 0; i < arr2.length; i++) {
			System.out.println(arr2[i]);
		}
	}

	public static int[] sort(int[] arr) {
		int minIndex = 0;
		for (int i = 0; i < arr.length - 1; i++) {
			minIndex = i;
			for (int j = i + 1; j < arr.length; j++) {
				if (arr[j] < arr[minIndex]) {
					minIndex = j;
				}
			}
			// swap
			if (arr[i] != arr[minIndex]) {
				int min = arr[minIndex];
				arr[minIndex] = arr[i];
				arr[i] = min;
			}
		}
		return arr;
	}

}
