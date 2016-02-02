package com.yudylaw.clrs;

/**
 * @author liuyu3@xiaomi.com
 * @since 2016年2月1日
 * 堆排序 (最大堆)
 * <p>节点的高度: 节点到叶子节点的边数</p>
 * <p>堆的高度,就是根节点的高度</p>
 * <p>堆是基于完全二叉树的, 所以高度为lgn, 堆结构的基本操作时间复杂度为O(lgn)</p>
 */

public class HeapSort {

    public static void main(String[] args) {
        int[] values = {11,1,5,7,10,28,100};
        
        buildMaxHeap(values);
        
        for (int value : values) {
            System.out.println(value);
        }
    }
    
    /**
     * <p>前提条件: index 左右子树都满足最大堆</p>
     * <p>maxHeap 确保添加 index 后,仍然是最大堆</p>
     * @param values
     * @param index
     * @return
     */
    public static int[] maxHeap(int[] values, int index) {
        int length = values.length;
        int left = index << 1;
        int right = left + 1;
        
        int maxIndex = index;//最大值的索引
        
        if (left <= length && values[left - 1] > values[maxIndex - 1]) {
            maxIndex = left;
        }
        if (right <= length && values[right -1] > values[maxIndex - 1]) {
            maxIndex = right;
        }
        if (maxIndex != index) {
            int tmp = values[index -1];
            values[index - 1] = values[maxIndex - 1];
            values[maxIndex - 1] = tmp;
            return maxHeap(values, maxIndex);
        }
        return values;
    }
    
    /**
     * [1, n/2] 都是非叶子节点
     * [n/2 + 1, n] 都是叶子节点,满足最大堆跟节点特性
     * @param values
     * @return
     */
    public static int[] buildMaxHeap(int[] values) {
        int size = values.length / 2;
        //树形结构,自底向上,越大越底层
        for (int i = size; i >= 1; i--) {
            values = maxHeap(values, i);
        }
        return values;
    }

}
