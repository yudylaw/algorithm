package com.yudylaw.interview;

public class TestArrayMaxSum {

	public static void main(String[] args) {
		int[] a = { -1, 2, -4, 3, -4, 3, 2, -1, 2 };
		int max = maxSum(a);
		System.out.println("max=" + max);
	}

	/**
	 * 只遍历数组一遍，当从头到尾部遍历数组A， 遇到一个数有两种选择 （1）加入之前subArray （2）自己另起一个subArray
	 * 设状态S[i], 表示以A[i]结尾的最大连续子序列和，状态转移方程如下:
	 * S[i] = max(S[i-1] + A[i],A[i])
	 * @param arr
	 * @return
	 */
	public static int maxSum(int[] arr) {
		if (arr.length == 0) {
			return 0;
		}
		int max = arr[0];
		if (arr.length == 1) {
			return max;
		}
		for (int i = 1; i < arr.length; i++) {
			if (max + arr[i] > arr[i]) {
				max += arr[i];
			} else {
				max = arr[i];
			}
		}
		return max;
	}

}
