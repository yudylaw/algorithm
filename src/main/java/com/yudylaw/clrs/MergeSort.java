package com.yudylaw.clrs;

import java.util.Arrays;

/**
 * 
 * @author yudy
 * 算法复杂度O(nlgn)
 * 递归算法，深度 lgn, 每次O(n)
 */
public class MergeSort {

	public static void main(String[] args) {
//		int[] leftArr = {7,8,10,23,100};//有序
//		int[] rightArr = {9,11,20,99,1299};//有序
		
		int[] numbres = {0,3,44,2,3,34,5,42,23,1001,1001,333};
		int[] result = mergeSort(numbres);
		
//		int[] numbres = {0,3,44,2,3,34};
//		int[] result = merge2(numbres, 0, 2, 5);
		
		for (int r : result) {
			System.out.println(r);
		}
	}
	
	/**
	 * 先分隔，再合并
	 * @param numbers
	 * @return
	 */
	public static int[] mergeSort(int[] numbers) {
		if (numbers.length == 1) {
			return numbers;
		}
		int left = numbers.length / 2;
		
		int[] leftArr = Arrays.copyOf(numbers, left);
		int[] rightArr = Arrays.copyOfRange(numbers, left, numbers.length);
		
		int[] leftResult = mergeSort(leftArr);
		int[] rightResult = mergeSort(rightArr);
		
		return merge(leftResult, rightResult);
	}
	
	//[l,m],[m+1,r] 已经排序好
	public static int[] merge2(int[] nums, int l, int m, int r) {
		if (r - l == 1) {
			return nums;
		}
		int[] leftArr = Arrays.copyOfRange(nums, l, m + 1);
		int[] rightArr = Arrays.copyOfRange(nums, m + 1, r + 1);
		//合并复杂度 O(n)
		int start = l;
		int end = r;
		l = 0;
		r = 0;
		for (int i=start;i <= end;i++) {
			
			if (l == leftArr.length || ( r < rightArr.length && leftArr[l] >= rightArr[r])) {
				nums[i] = rightArr[r];
				r++;
				continue;
			}
			
			if (r == rightArr.length || (l < leftArr.length && leftArr[l] < rightArr[r])) {
				nums[i] = leftArr[l];
				l++;
			}
			
		}
		return nums;
	}
	
	public static int[] merge(int[] leftArr, int[] rightArr) {
		int size = leftArr.length + rightArr.length;
		int[] result = new int[size];//存储空间
		int l = 0;
		int r = 0;
		//合并复杂度 O(n)
		for (int i=0;i < size;i++) {
			
			if (l == leftArr.length || ( r < rightArr.length && leftArr[l] >= rightArr[r])) {
				result[i] = rightArr[r];
				r++;
				continue;
			}
			
			if (r == rightArr.length || (l < leftArr.length && leftArr[l] < rightArr[r])) {
				result[i] = leftArr[l];
				l++;
			}
			
		}
		return result;
	}

}
