package com.yudylaw.algorithm.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author liuyu3@xiaomi.com
 * @since 2015年6月15日
 */

public class MyBTree {

    public final static int T = 2;//树的度 t>=2,度越大,树越低
    //每个结点key数[t-1,2t-1]
    public final static int MIN_KEY = T -1;
    public final static int MAX_KEY = 2 * T -1;
    //子节点数[t,2t]
    public final static int MIN_POINT = T;
    public final static int MAX_POINT = 2 * T;
    //分裂约束:P1<=K1<=P2<=K2<=...<=Pn<=Kn<=Pn+1
    
    Node rootNode;
    
    KeyAscComparator COMP = new KeyAscComparator();
    
    class KeyAscComparator implements Comparator<Entry<Long, Long>> {

        public int compare(Entry<Long, Long> o1, Entry<Long, Long> o2) {
            if(o1 == null && o2 == null){
                return 0;
            }
            if(o1 == null || o1.getK() == null){
                return -1;
            }
            if(o2 == null || o2.getK() == null){
                return 1;
            }
            return o1.getK().compareTo(o2.getK());
        }
        
    }
    
    /**
     * 在B树中插入给定的键值对。
     * @param true，如果B树中不存在给定的项，否则false
     */
    public boolean insert(Long key, Long value) {
        //如果根节点满了，则分裂
        if(rootNode.isFull()){
            Node newNode = new Node();
            newNode.setLeaf(false);
            newNode.addChild(rootNode);
            splitNode(newNode, rootNode, 0);
            rootNode = newNode;
        }
        return insertNotFull(rootNode, new Entry<Long, Long>(key, value));
    }
    
    /**
     * 分裂一个满子节点<code>childNode</code>。
     * <p/>
     * 你需要保证给定的子节点是满节点。
     * 
     * @param parentNode - 父节点
     * @param childNode - 满子节点
     * @param index - 满子节点在父节点中的索引
     */
    private void splitNode(Node parentNode, Node childNode, int index) {
        assert childNode.isFull();
        Node siblingNode = new Node();
        //新节点与老节点在同一层
        siblingNode.setLeaf(childNode.isLeaf());
        //注：t - 1 项被提升
        // 将满子节点中索引为[t, 2t - 1]的(t - 1)个项插入新的节点中
        for (int i = 0; i < MIN_KEY; i++) {
            siblingNode.addEntry(childNode.getEntry(T + i));
        }
        // 提取满子节点中的中间项，其索引为(t - 1)
        Entry<Long,Long> entry = childNode.getEntry(T - 1);
        // 删除满子节点中索引为[t - 1, 2t - 1]的t个项（备注：从右侧开始删除，--i）
        for(int i = MAX_KEY - 1; i >= T - 1; -- i){
            childNode.entries.remove(i);
        }
        if(!childNode.isLeaf()){// 如果满子节点不是叶节点，则还需要处理其子节点
            // 将满子节点中索引为[t, 2t - 1]的t个子节点插入新的节点中
            for (int i = T; i <=MAX_KEY; i++) {
                siblingNode.addChild(childNode.getChildNode(i));
            }
            // 删除满子节点中索引为[t, 2t - 1]的t个子节点
            for(int i = MAX_KEY; i >=T; i--){
                childNode.children.remove(i);
            }
        }
        
        parentNode.insertEntry(index, entry);
        parentNode.insertChild(index, siblingNode);
    }
    
    /**
     * 在一个非满节点中插入给定的项。
     * <p>
     * 如果插入遇到满的节点，则分裂该节点
     * </p>
     * @param node - 非满节点
     * @param entry - 给定的项
     * @return true，如果B树中不存在给定的项，否则false
     */
    private boolean insertNotFull(Node node, Entry<Long, Long> entry) {
        assert !node.isFull();
        
        if(node.isLeaf()){ // 如果是叶子节点，直接插入
            return node.putEntry(entry);
        }else{
            SearchResult result = node.searchKey(entry.getK());
            //不支持重复key
            //TODO 支持更新
            if(result.isExist()){
                return false;
            }else{
                //P1<=K1<=P2....<=Pn<=Kn
                //由于key和children key是有序的，可以采用二分法查找可以插入的节点
                Node childNode = node.getChildNode(result.getIndex());
                if(childNode.isFull()){
                    //index 位置分裂，上移index节点
                    splitNode(node, childNode, result.getIndex());
                    /* 
                     * 如果给定entry的键大于分裂之后新生成项的键，则需要插入该新项的右边，否则左边。
                     */
                    if(compare(entry, node.getEntry(result.getIndex())) > 0){
                        childNode = node.getChildNode(result.getIndex() + 1);
                    }
                }
                //递归调用
                return insertNotFull(childNode, entry);
            }
        }
    }

    public int compare(Entry<Long, Long> newEntry, Entry<Long, Long> oldEntry) {
        return COMP.compare(newEntry, oldEntry);
    }
    
    /**
     * 在B树节点中搜索给定键值的返回结果。
     * <p/> 
     * 该结果有两部分组成。第一部分表示此次查找是否成功，
     * 如果查找成功，第二部分表示给定键值在B树节点中的位置，
     * 如果查找失败，第二部分表示给定键值应该插入的位置。
     */
    private static class SearchResult
    {
        private boolean exist;
        private int index;
        private Long value;
        
        public SearchResult(boolean exist, int index)
        {
            this.exist = exist;
            this.index = index;
        }
        
        public SearchResult(boolean exist, int index, Long value)
        {
            this(exist, index);
            this.value = value;
        }
        
        public boolean isExist()
        {
            return exist;
        }
        
        public int getIndex()
        {
            return index;
        }
        
    }
    
    /**
     * 一个简单的层次遍历B树实现，用于输出B树。
     */
    public void output()
    {
        Queue<Node> queue = new LinkedList<Node>();
        queue.offer(rootNode);
        while (!queue.isEmpty())
        {
            Node node = queue.poll();
            for (int i = 0; i < node.entries.size(); ++i) {
                System.out.print(node.getEntries().get(i) + " ");
            }
            System.out.println();
            if (!node.isLeaf())
            {
                for (int i = 0; i <= node.entries.size(); ++i) {
                    queue.offer(node.getChildren().get(i));
                }
            }
        }
    }
    
    class Node {
        
        private List<Entry<Long, Long>> entries;//size range [t-1]~[2t-1]
        private List<Node> children;//size range [t~2t]
        private boolean isLeaf;
        
        public Node() {
            this.entries = new ArrayList<Entry<Long,Long>>();
            this.children = new ArrayList<Node>();
            this.isLeaf = true;
        }

        public List<Entry<Long, Long>> getEntries() {
            return entries;
        }

        public void setEntries(List<Entry<Long, Long>> entries) {
            this.entries = entries;
        }

        public List<Node> getChildren() {
            return children;
        }

        public void setChildren(List<Node> children) {
            this.children = children;
        }

        public boolean isLeaf() {
            return isLeaf;
        }

        public void setLeaf(boolean isLeaf) {
            this.isLeaf = isLeaf;
        }
        
        public boolean isFull() {
            return entries.size() == MAX_KEY;
        }
        
        /**
         * 将给定的子节点插入到该节点中给定索引的位置。
         * <p>
         * 有序插入
         * </p> 
         * @param index - 子节点带插入的位置
         * @param child - 给定的子节点
         */
        public void insertChild(int index, Node node) {
            assert children.size() <= MAX_POINT;
            children.add(index, node);
        }
        
        public void addChild(Node node) {
            assert children.size() <= MAX_POINT;
            children.add(node);
        }
        
        public Node getChildNode(int index) {
            return children.get(index);
        }
        
        public Entry<Long,Long> getEntry(int index) {
            return entries.get(index);
        }
        
        public void addEntry(Entry<Long,Long> entry) {
            assert !isFull();
            entries.add(entry);
        }
        
        /**
         * 在该节点中给定索引的位置插入给定的项
         * <p>
         * 有序插入
         * </p> 
         * @param index - 给定的索引
         * @param entry 新项
         */
        public void insertEntry(int index, Entry<Long,Long> entry) {
            assert !isFull();
            entries.add(index, entry);
        }
        
        public boolean putEntry(Entry<Long,Long> entry) {
            SearchResult reuslt = searchKey(entry.getK());
            if(reuslt.isExist()){
                return false;
            }else{
                insertEntry(reuslt.getIndex(), entry);
            }
            return true;
        }
        
        /**
         * 在节点中查找给定的键。
         * 如果节点中存在给定的键，则返回一个<code>SearchResult</code>，
         * 标识此次查找成功，给定的键在节点中的索引和给定的键关联的值；
         * 如果不存在，则返回<code>SearchResult</code>，
         * 标识此次查找失败，给定的键应该插入的位置，该键的关联值为null。
         * <br/>
         * <p>
         * 如果查找失败，返回结果中的索引域为[0, {@link #size()}]
         * </p>
         * <p>
         * 如果查找成功，返回结果中的索引域为[0, {@link #size()} - 1]
         * <p/>
         * <p>
         * P[1] <= K[1] <= P[2] <= K[2] …..<= K[n] <= P[n+1]
         * <p>
         * 这是一个二分查找算法，可以保证时间复杂度为O(log(t))。
         * </p>
         * @param key - 给定的键值
         * @return - 查找结果
         */
        public SearchResult searchKey(Long key)
        {
            int low = 0;
            int high = entries.size() - 1;
            int mid = 0;
            while(low <= high)
            {
                mid = (low + high) / 2; //1/2=0,3/2=1
                Entry<Long, Long> entry = entries.get(mid);
                if(entry.getK().compareTo(key) == 0)
                    break;
                else if(entry.getK().compareTo(key) > 0)
                    high = mid - 1;
                else
                    low = mid + 1;
            }
            boolean result = false;
            int index = 0;
            Long value = null;
            if(low <= high) // 说明查找成功
            {
                result = true;
                index = mid; // index表示元素所在的位置
                value = entries.get(index).getV();
            }
            else
            {
                result = false;
                index = low; // index表示元素应该插入的位置
            }
            return new SearchResult(result, index, value);
        }
        
    }
    
    public MyBTree() {
        this.rootNode = new Node();
    }
    
    /**
     * key/value
     */
    public class Entry<K,V> {
        
        K k;
        V v;
        
        public Entry(K k, V v) {
            this.k = k;
            this.v = v;
        }

        public K getK() {
            return k;
        }

        public void setK(K k) {
            this.k = k;
        }

        public V getV() {
            return v;
        }

        public void setV(V v) {
            this.v = v;
        }
        
        @Override
        public String toString() {
            return "[" + k + ":" + v + "]";
        }
        
    }
    
    public static void main(String[] args) {
        //B-Tree 不同插入顺序，结点位置不同，但是遵循平衡树的特征
        testDescInsert();
//        testAscInsert();
    }

    private static void testDescInsert() {
        MyBTree tree = new MyBTree();
        tree.insert(9L, 10L);
        tree.insert(8L, 10L);
        tree.insert(7L, 10L);
        tree.insert(6L, 10L);//分裂到2层
//        tree.insert(5L, 10L);
//        tree.insert(4L, 10L);//分裂
//        //key乱序了
//        tree.insert(3L, 10L);
//        tree.insert(2L, 10L);//分裂
//        tree.insert(1L, 10L);
//        tree.insert(0L, 10L);
        tree.output();
    }
    
    private static void testAscInsert() {
        MyBTree tree = new MyBTree();
        tree.insert(0L, 10L);
        tree.insert(1L, 10L);
        tree.insert(2L, 10L);
        tree.insert(3L, 10L);//分裂到2层
        tree.insert(4L, 10L);
        tree.insert(5L, 10L);//分裂
        tree.insert(6L, 10L);
        tree.insert(7L, 10L);//分裂
        tree.insert(8L, 10L);//分裂到3层
        tree.insert(9L, 10L);//分裂
        tree.output();
    }
    
}
