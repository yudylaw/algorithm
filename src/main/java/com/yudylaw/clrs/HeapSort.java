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
        int[] values = {0,11,1,5,7,10,28,100,-1,-1,-1};
        
        buildMaxHeap(values);
        
        int heapSize = 8;
//        int max = extractMax(values, heapSize);
//        heapSort(values, heapSize);

        insert(values, heapSize, 99);
        
        for (int value : values) {
            System.out.println(value);
        }
    }
    
    /**
     * <p>前提条件: index 左右子树都满足最大堆</p>
     * <p>maxHeap 确保添加 index 后,仍然是最大堆</p>
     * @param values
     * @param index 堆节点序号,从1开始
     * @param heapSize 堆大小 <= values.length
     * @return
     */
    public static void maxHeap(int[] values, int index, int heapSize) {
        if (heapSize > values.length) {
            throw new IllegalArgumentException("heapSize cannot bigger than length of values");
        }
        int left = index << 1;
        int right = left + 1;
        
        int maxIndex = index;//最大值的索引
        
        if (left <= heapSize && values[left - 1] > values[maxIndex - 1]) {
            maxIndex = left;
        }
        if (right <= heapSize && values[right -1] > values[maxIndex - 1]) {
            maxIndex = right;
        }
        if (maxIndex != index) {
            int tmp = values[index -1];
            values[index - 1] = values[maxIndex - 1];
            values[maxIndex - 1] = tmp;
            maxHeap(values, maxIndex, heapSize);
        }
    }
    
    /**
     * [1, n/2] 都是非叶子节点
     * [n/2 + 1, n] 都是叶子节点,满足最大堆跟节点特性
     * @param values
     * @return
     */
    public static void buildMaxHeap(int[] values) {
        int size = values.length / 2;
        //树形结构,自底向上,越大越底层
        for (int i = size; i >= 1; i--) {
            maxHeap(values, i, values.length);
        }
    }
    
    public static void heapSort(int[] values, int heapSize) {
        //构建最大堆
        buildMaxHeap(values);
        while (heapSize > 1) {
            //移动堆根节点
            int max = values[0];
            values[0] = values[heapSize - 1];
            values[heapSize - 1] = max;
            heapSize--;
            //维持最大堆
            maxHeap(values, 1, heapSize);
        }
    }
    
    
    /**
     * priority queue insert
     * 
     * @param values
     * @param heapSize
     * @param val
     */
    public static void insert(int[] values, int heapSize, int val) {
        if (heapSize >= values.length) {
            throw new IllegalArgumentException("heapSize cannot bigger than length of values");
        }
        //添加最小值,再 increase to val
        values[heapSize] = Integer.MIN_VALUE;
        increaseKey(values, heapSize + 1, heapSize + 1, val);
    }
    
    /**
     * priority queue extract max
     * 
     * @param values
     * @return
     */
    public static int extractMax(int[] values, int heapSize) {
        int max = values[0];
        //把小节点放作为根
        values[0] = values[heapSize - 1];
        //新的最大堆
        maxHeap(values, 1, heapSize - 1);
        return max;
    }
    
    /**
     * priority queue increase key
     * <p>前提: val > values[index-1]</p>
     * @param values
     * @param heapSize
     * @param index
     * @param val
     */
    public static void increaseKey(int[] values, int heapSize, int index, int val) {
        if (val <= values[index-1]) {
            throw new IllegalArgumentException("illeal arguments");
        }
        if (heapSize > values.length || index > heapSize) {
            throw new IllegalArgumentException("illeal arguments");
        }
        
        values[index -1] = val;
        
        while (index > 1) {
            int parent = index >> 1;
            if (values[index -1] > values[parent -1]) {
                //swap index with parent
                int tmp = values[parent -1];
                values[parent -1] = values[index -1];
                values[index -1] = tmp;
                index = parent;
            } else {
                break;
            }
        }

    }

}
