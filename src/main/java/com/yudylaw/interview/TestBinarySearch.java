package com.yudylaw.interview;

public class TestBinarySearch {

	public static void main(String[] args) {
		int[] a = { 1, 2, 3, 4, 5, 6, 7 };
		int index = -1;
		for (int i = 0; i < 10; i++) {
			index = search(a, i);
			System.out.println("search value:" + i + ", found index=" + index);
		}
	}

	/**
	 * 假设升序
	 * 
	 * @param arr
	 * @param value
	 * @return
	 */
	public static int search(int[] arr, int value) {
		if (arr.length == 0) {
			return -1;
		}
		int start = 0;
		int end = arr.length - 1;
		int m = 0;
		while (end >= start) {
			m = (start + end) / 2;
			if (arr[m] > value) {
				end = m - 1;
			} else if (arr[m] < value) {
				start = m + 1;
			} else if (arr[m] == value) {
				return m;
			}
		}
		return -1;
	}

}
