package com.yudylaw.algorithm.tree;

import java.util.ArrayList;
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
    
    public Entry<Long,Long> remove(Long key) {
        return remove(rootNode, key);
    }
    
    /**
     * 1)btree在删除节点的时复杂一点，但是逻辑是，从上到下搜索，先看自己是不是叶子节点，如果是，数据直接删除（一开始就发现自己是叶子节点除非只有一个树根）
     * 2)如果不是，再看数据是不是在自己这个节点上。如果在自己身上，就检查这个节点的左右子节点哪个子节点的数据大于t-1,
     * 2.a)假设左边有多余的，按照这个递归算法，去删除左边子树的最大的节点，来顶替这个要删除的节点。(最大项上移)
     * 2.b)如果右边有多余的，就采用右边最小的。(最小项上移动)
     * 2.c)如果左右两个子树的树根都等于t-1，那么合并之，再删除。
     * 如果要的数据不在自己身上，就判断出会在哪个子节点上。
     * 如果这个子节点等于t-1，就看他的兄弟节点能不能借数据给他。如果不能借，就把这个子树合并。
     * 总之，无论如何，保证这个要删除的子树的沿路永远有可以借出的数据。用来确保树不变形。
     * @param node
     * @param key
     * @return
     */
    private Entry<Long,Long> remove(Node node, Long key) {
        // 该过程需要保证，对非根节点执行删除操作时，其关键字个数至少为t。
        assert node.size() >= T || node == rootNode;
        
        SearchResult result = node.searchKey(key);
        /*
         * 因为这是查找成功的情况，0 <= result.getIndex() <= (node.size() - 1)，
         * 因此(result.getIndex() + 1)不会溢出。
         */
        if(result.isExist()){
            // 1.如果关键字在节点node中，并且是叶节点，则直接删除。
            if(node.isLeaf()){
                return node.removeEntry(result.getIndex());
            }else{
                // 2.a 如果节点node中前于key的子节点包含至少t个项 (意味着删除一项,仍满足btree约束, 同时需要递归删除子节点)
                Node leftChildNode = node.getChildNode(result.getIndex());
                if(leftChildNode.size() >= T)
                {
                    // 使用leftChildNode中的最后一个项代替node中需要删除的项
                    node.removeEntry(result.getIndex());
                    //左节点最大项上移
                    node.insertEntry(result.getIndex(), leftChildNode.getEntry(leftChildNode.size() - 1));
                    // 递归删除左子节点中的最后一个项
                    return remove(leftChildNode, leftChildNode.getEntry(leftChildNode.size() - 1).getK());
                } else {
                    // 2.b 如果节点node中后于key的子节点包含至少t个关键字
                    Node rightChildNode = node.getChildNode(result.getIndex() + 1);
                    if(rightChildNode.size() >= T)
                    {
                        // 使用rightChildNode中的第一个项代替node中需要删除的项
                        node.removeEntry(result.getIndex());
                        node.insertEntry(result.getIndex(), rightChildNode.getEntry(0));//右边最小节点来替换
                        // 递归删除右子节点中的第一个项
                        return remove(rightChildNode, rightChildNode.getEntry(0).getK());
                    }
                    else // 2.c 前于key和后于key的子节点都只包含t-1个项
                    {
                        Entry<Long, Long> deletedEntry = node.removeEntry(result.getIndex());
                        node.removeChild(result.getIndex() + 1);
                        // 将node中与key关联的项和rightChildNode中的项合并进leftChildNode
                        leftChildNode.addEntry(deletedEntry);
                        for(int i = 0; i < rightChildNode.size(); ++ i)
                            leftChildNode.addEntry(rightChildNode.getEntry(i));
                        // 将rightChildNode中的子节点合并进leftChildNode，如果有的话
                        if(!rightChildNode.isLeaf())
                        {
                            for(int i = 0; i <= rightChildNode.size(); ++ i)
                                leftChildNode.addChild(rightChildNode.getChildNode(i));
                        }
                        //被删除项已经下移到左节点, 递归删除,知道叶子节点
                        return remove(leftChildNode, key);
                    }
                }
            }
        }else{
            /*
             * 因为这是查找失败的情况，0 <= result.getIndex() <= node.size()，
             * 因此(result.getIndex() + 1)会溢出。
             */
            if(node.isLeaf()) // 如果关键字不在节点node中，并且是叶节点，则什么都不做，因为该关键字不在该B树中
            {
                System.out.println("The key: " + key + " isn't in this BTree.");
                return null;
            }
            Node childNode = node.getChildNode(result.getIndex());//TODO ???
            if(childNode.size() >= T) // // 如果子节点有不少于t个项，则递归删除
                return remove(childNode, key);
            else // 3
            {
                //从兄弟节点中项 >= T 的借节点
                // 先查找右边的兄弟节点
                Node siblingNode = null;
                int siblingIndex = -1;
                if(result.getIndex() < node.size()) // 存在右兄弟节点
                {
                    if(node.getChildNode(result.getIndex() + 1).size() >= T)
                    {
                        siblingNode = node.getChildNode(result.getIndex() + 1);
                        siblingIndex = result.getIndex() + 1;
                    }
                }
                // 如果右边的兄弟节点不符合条件，则试试左边的兄弟节点
                if(siblingNode == null)
                {
                    if(result.getIndex() > 0) // 存在左兄弟节点
                    {
                        if(node.getChildNode(result.getIndex() - 1).size() >= T)
                        {
                            siblingNode = node.getChildNode(result.getIndex() - 1);
                            siblingIndex = result.getIndex() - 1;
                        }
                    }
                }
                // 3.a 有一个相邻兄弟节点至少包含t个项
                if(siblingNode != null)
                {
                    if(siblingIndex < result.getIndex()) // 左兄弟节点满足条件
                    {
                        childNode.insertEntry(0, node.getEntry(siblingIndex));
                        node.removeEntry(siblingIndex);
                        node.insertEntry(siblingIndex, siblingNode.getEntry(siblingNode.size() - 1));
                        siblingNode.removeEntry(siblingNode.size() - 1);
                        // 将左兄弟节点的最后一个孩子移到childNode
                        if(!siblingNode.isLeaf())
                        {
                            childNode.insertChild(0, siblingNode.getChildNode(siblingNode.size()));
                            siblingNode.removeChild(siblingNode.size());
                        }
                    }
                    else // 右兄弟节点满足条件
                    {
                        //TODO BUG 项顺序不对
                        childNode.insertEntry(childNode.size() - 1, node.getEntry(result.getIndex()));//上项下移
                        node.removeEntry(result.getIndex());
                        node.insertEntry(result.getIndex(), siblingNode.getEntry(0));//右兄弟上移
                        siblingNode.removeEntry(0);
                        // 将右兄弟节点的第一个孩子移到childNode
                        // childNode.insertChild(siblingNode.childAt(0), childNode.size() + 1);
                        if(!siblingNode.isLeaf())
                        {
                            childNode.addChild(siblingNode.getChildNode(0));
                            siblingNode.removeChild(0);
                        }
                    }
                    return remove(childNode, key);
                }
                else // 3.b 如果其相邻左右节点都包含t-1个项, 则合并左右子树
                {
                    if(result.getIndex() < node.size()) // 存在右兄弟，直接在后面追加
                    {
                        Node rightSiblingNode = node.getChildNode(result.getIndex() + 1);
                        childNode.addEntry(node.getEntry(result.getIndex()));//下移
                        node.removeEntry(result.getIndex());
                        node.removeChild(result.getIndex() + 1);
                        for(int i = 0; i < rightSiblingNode.size(); ++ i)
                            childNode.addEntry(rightSiblingNode.getEntry(i));
                        if(!rightSiblingNode.isLeaf())
                        {
                            for(int i = 0; i <= rightSiblingNode.size(); ++ i)
                                childNode.addChild(rightSiblingNode.getChildNode(i));
                        }
                    }
                    else // 存在左节点，在前面插入
                    {
                        Node leftSiblingNode = node.getChildNode(result.getIndex() - 1);
                        childNode.insertEntry(0, node.getEntry(result.getIndex() - 1));
                        node.removeEntry(result.getIndex() - 1);
                        node.removeChild(result.getIndex() - 1);
                        for(int i = leftSiblingNode.size() - 1; i >= 0; -- i)
                            childNode.insertEntry(0, leftSiblingNode.getEntry(i));
                        if(!leftSiblingNode.isLeaf())
                        {
                            for(int i = leftSiblingNode.size(); i >= 0; -- i)
                                childNode.insertChild(0, leftSiblingNode.getChildNode(i));
                        }
                    }
                    // 如果node是root并且node不包含任何项了
                    if(node == rootNode && node.size() == 0)
                        rootNode = childNode;
                    return remove(childNode, key);
                }
            }
        }
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
        parentNode.insertChild(index + 1, siblingNode);
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
        
        public boolean isEmpty() {
            return entries.isEmpty();
        }
        
        public int size() {
            return entries.size();
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
        
        public void removeChild(int index) {
            children.remove(index);
        }
        
        public void addChild(Node node) {
            assert children.size() <= MAX_POINT;
            children.add(node);
        }
        
        public Node getChildNode(int index) {
            return children.get(index);
        }
        
        public Entry<Long,Long> removeEntry(int index) {
            return entries.remove(index);
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
//        testDescInsert();
//        testAscInsert();
        testRemove();
    }

    private static void testRemove() {
        MyBTree tree = new MyBTree();
        tree.insert(0L, 10L);
        tree.insert(1L, 10L);
        tree.insert(2L, 10L);
        tree.insert(3L, 10L);//分裂到2层
        tree.remove(0L);
        tree.output();
    }
    
    private static void testDescInsert() {
        MyBTree tree = new MyBTree();
        tree.insert(9L, 10L);
        tree.insert(8L, 10L);
        tree.insert(7L, 10L);
        tree.insert(6L, 10L);//分裂到2层
        tree.insert(5L, 10L);
        tree.insert(4L, 10L);//分裂
//        //key乱序了
        tree.insert(3L, 10L);
        tree.insert(2L, 10L);//分裂
        tree.insert(1L, 10L);
        tree.insert(0L, 10L);
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
