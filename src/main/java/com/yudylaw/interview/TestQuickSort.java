package com.yudylaw.interview;

public class TestQuickSort {

	public static void main(String[] args) {
		int[] a = { 1, 2, -1, 310, 1, 9, 10 };
		search(a, 0, a.length - 1);
		for (int i = 0; i < a.length; i++) {
			System.out.println("" + a[i]);
		}
	}

	public static void search(int[] array, int begin, int end) {
		if (begin >= end) {
			return;
		}
		int left = begin, right = end;
		int base = array[begin];
		int swap;
		// j--,i++,m是参考值
		while (right > left) {
			// 从右边找比base小的数
			while (array[right] >= base && right > left) {
				right--;
			}
			// 从左边找比base大的数
			while (array[left] <= base && right > left) {
				left++;
			}
			if (right > left) {
				// 找到则交换
				swap = array[right];
				array[right] = array[left];
				array[left] = swap;
			}
		}
		// 基准数归位, base >= array[left]
		array[begin] = array[left];
		array[left] = base;

		search(array, begin, left);
		search(array, left + 1, end);
	}

}
