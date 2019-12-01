package codeReader

import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * 基于红黑树（Red-Black tree）的 NavigableMap 实现。该映射根据其键的自然顺序进行排序，
 * 或者根据创建映射时提供的Comparator 进行排序，具体取决于使用的构造方法。
 *
 * 此实现为 containsKey、get、put 和 remove 操作提供受保证的 log(n) 时间开销。
 * 这些算法是 Cormen、Leiserson和 Rivest 的 Introduction to Algorithms 中的算法的改编。
 *
 * 注意，如果要正确实现 Map 接口，则有序映射所保持的顺序（无论是否明确提供了比较器）都
 * 必须与 equals 一致。（关于与 equals 一致的精确定义，请参阅 Comparable 或 Comparator）。
 * 这是因为 Map 接口是按照 equals 操作定义的，但有序映射使用它的compareTo（或 compare）方法
 * 对所有键进行比较，因此从有序映射的观点来看，此方法认为相等的两个键就是相等的。
 * 即使排序与 equals不一致，有序映射的行为仍然是 定义良好的，只不过没有遵守 Map 接口的常规协定。
 *
 * 注意，此实现不是同步的。如果多个线程同时访问一个映射，并且其中至少一个线程从结构上
 * 修改了该映射，则其必须外部同步。（结构上的修改是指添加或删除一个或多个映射关系的操作
 * ；仅改变与现有键关联的值不是结构上的修改。）这一般是通过对自然封装该映射的对象执行同步操作
 * 来完成的。如果不存在这样的对象，则应该使用Collections.synchronizedSortedMap
 * 方法来“包装”该映射。最好在创建时完成这一操作，以防止对映射进行意外的不同步访问，如下所示：
 *
 * SortedMap m = Collections.synchronizedSortedMap(new
 * TreeMap(...));collection（由此类所有的“collection 视图方法”返回）的 iterator 方法返回的
 * 迭代器都是快速失败的：在迭代器创建之后，如果从结构上对映射进行修改，
 * 除非通过迭代器自身的 remove 方法，否则在其他任何时间以任何方式进行修改都将导致迭代器抛出
 * ConcurrentModificationException。因此，对于并发的修改，迭代器很快就完全失败，
 * 而不会冒着在将来不确定的时间发生不确定行为的风险。
 *
 * 注意，迭代器的快速失败行为无法得到保证，一般来说，当存在不同步的并发修改时，
 * 不可能作出任何肯定的保证。快速失败迭代器尽最大努力抛出ConcurrentModificationException。
 * 因此，编写依赖于此异常的程序的做法是错误的，正确做法是：迭代器的快速失败行为应该仅用于检测
 * bug。
 *
 * 此类及其视图中的方法返回的所有 Map.Entry 对都表示生成它们时的映射关系的快照。它们
 * 不支持 Entry.setValue方法。（不过要注意的是，使用 put 更改相关映射中的映射关系是有可能的。）
 *
 * 此类是 Java Collections Framework 的成员。
 */
public class TreeMap<K, V> extends AbstractMap<K, V> implements
        NavigableMap<K, V>, Cloneable, java.io.Serializable {

    /*
     *（1）每个节点或者是黑色，或者是红色。
     *（2）根节点是黑色。
     *（3）每个叶子节点（NIL）是黑色。 [注意：这里叶子节点，是指为空(NIL或NULL)的叶子节点！]
     *（4）如果一个节点是红色的，。则它的子节点必须是黑色的
     *（5）从一个节点到该节点的子孙节点的所有路径上包含相同数目的黑节点。
     */
    /**
     * 比较器用于维护树映射中的顺序，如果使用键的自然顺序，则为null。
     * @serial
     */
    private final Comparator<? super K> comparator;
    /**
     * 红黑树根节点
     */
    private transient Entry<K,V> root;

    /**
     * 树中的条目数
     */
    private transient int size = 0;

    /**
     * 树的结构修改的数量。
     */
    private transient int modCount = 0;

    /**
     * 使用键的自然顺序构造一个新的、空的树映射。
     * 插入该映射的所有键都必须实现 Comparable 接口。
     * 另外，所有这些键都必须是可互相比较的：
     * 对于映射中的任意两个键 k1 和 k2，执行 k1.compareTo(k2) 都不得抛出 ClassCastException。
     * 如果用户试图将违反此约束的键添加到映射中（例如，用户试图将字符串键添加到键为整数的映射中），
     * 则 put(Object key, Object value) 调用将抛出 ClassCastException。
     */
    public TreeMap() {
        comparator = null;
    }

    /**
     * 构造一个新的、空的树映射，该映射根据给定比较器进行排序。
     * 插入该映射的所有键都必须由给定比较器进行相互比较：对于映射中的任意两个键 k1 和 k2，
     * 执行 comparator.compare(k1, k2) 都不得抛出 ClassCastException。如果用户试图将违反此约束的键放入映射中，
     * 则 put(Object key, Object value) 调用将抛出 ClassCastException。 
     * @param comparator	将用来对此映射进行排序的比较器。如果该参数为 null，则将使用键的自然顺序。
     */
    public TreeMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    /**
     * 构造一个与给定映射具有相同映射关系的新的树映射，该映射根据其键的自然顺序 进行排序。
     * 插入此新映射的所有键都必须实现 Comparable 接口。另外，所有这些键都必须是可互相比较的：
     * 对于映射中的任意两个键 k1 和 k2，执行 k1.compareTo(k2) 都不得抛出 ClassCastException。
     * 此方法的运行时间为 n*log(n)。 
     * @param m	其映射关系将存放在此映射中的映射
     */
    public TreeMap(Map<? extends K, ? extends V> m) {
        comparator = null;
        putAll(m);
    }

    /**
     * 构造一个与指定有序映射具有相同映射关系和相同排序顺序的新的树映射。此方法是以线性时间运行的。
     * @param m	有序映射，其映射关系将存放在此映射中，并且其比较器将用来对此映射进行排序 
     */
    public TreeMap(SortedMap<K, ? extends V> m) {
        comparator = m.comparator();
        try {
            // 基于排序数据的线性时间树构建算法。
            buildFromSorted(m.size(), m.entrySet().iterator(), null, null);
        } catch (java.io.IOException cannotHappen) {
        } catch (ClassNotFoundException cannotHappen) {
        }
    }

    /**
     * 返回此映射中的键-值映射关系数。
     * 覆写：类 AbstractMap<K,V> 中的 size
     */
    public int size() {
        return size;
    }

    /**
     * 如果此映射包含指定键的映射关系，则返回 true。
     * 覆写：AbstractMap<K,V> 中的 containsKey
     * @param key	测试是否存在于此映射中的键
     * @return	如果此映射包含指定键的映射关系，则返回 true 
     */
    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    /**
     * 如果此映射为指定值映射一个或多个键，则返回 true。更确切地讲，
     * 当且仅当此映射包含至少一个到值 v 的映射关系，
     * 并且该值满足 (value==null ? v==null :value.equals(v)) 时，返回 true。
     * 对于大部分实现而言，此操作需要的时间可能与映射的大小呈线性关系。
     * 覆写：类 AbstractMap<K,V> 中的 containsValue
     * @param value	将测试其是否存在于此映射中的值
     * @return	如果存在到 value 的映射关系，则返回 true；否则返回 false
     */
    public boolean containsValue(Object value) {
        for (Entry<K,V> e = getFirstEntry(); e != null; e = successor(e))
            if (valEquals(value, e.value)) // 测试两个值是否相等。
                return true;
        return false;
    }

    /**
     * 返回指定键所映射的值，如果对于该键而言，此映射不包含任何映射关系，则返回 null。 
     * 更确切地讲，如果此映射包含从键 k 到值 v 的映射关系，根据该映射的排序 key 比较起来等于 k，
     * 那么此方法将返回 v；否则返回 null。（最多只能有一个这样的映射关系。） 
     * 返回 null 值并不一定 表明映射不包含该键的映射关系；也可能此映射将该键显式地映射为 null。
     * 可以使用 containsKey 操作来区分这两种情况。
     * 覆写：类 AbstractMap<K,V> 中的 get
     * @param key	要返回其关联值的键 
     * @return	指定键所映射的值；如果此映射不包含该键的映射关系，则返回 null
     */
    public V get(Object key) {
        Entry<K,V> p = getEntry(key);
        return (p==null ? null : p.value);
    }

    /**
     * 返回对此映射中的键进行排序的比较器；如果此映射使用键的自然顺序，则返回 null。 
     * 覆写：接口 SortedMap<K,V> 中的 comparator
     * @return 用来对此映射中的键进行排序的比较器；如果此映射使用键的自然顺序，则返回 null
     */
    @Override
    public Comparator<? super K> comparator() {
        return comparator;
    }

    /**
     * 返回此映射中当前第一个（最低）键。
     * 覆写：接口 SortedMap<K,V> 中的 firstKey
     * @return	此映射中当前第一个（最低）键
     */
    @Override
    public K firstKey() {
        return key(getFirstEntry());
    }

    /**
     * 返回映射中当前最后一个（最高）键。
     * 覆写：接口 SortedMap<K,V> 中的 lastKey
     * @return	此映射中当前最后一个（最高）键
     */
    @Override
    public K lastKey() {
        return key(getLastEntry());
    }

    /**
     * 将指定映射中的所有映射关系复制到此映射中。
     * 这些映射关系将替换此映射所有当前为指定映射的所有键所包含的映射关系。
     * 覆写：类 AbstractMap<K,V> 中的 putAll
     * @param map	将存储在此映射中的映射关系
     */
    public void putAll(Map<? extends K, ? extends V> map) {
        int mapSize = map.size();
        if (size==0 && mapSize!=0 && map instanceof SortedMap) {
            Comparator<?> c = ((SortedMap<?,?>)map).comparator(); // map的比较器
            if (c == comparator || (c != null && c.equals(comparator))) {
                ++modCount;
                try {
                    // 基于排序数据的线性时间树构建算法。
                    buildFromSorted(mapSize, map.entrySet().iterator(),
                            null, null);
                } catch (java.io.IOException cannotHappen) {
                } catch (ClassNotFoundException cannotHappen) {
                }
                return;
            }
        }
        super.putAll(map);
    }

    /**
     * 返回与指定键对应的项
     * @param key	要返回其关联值的键 
     * @return 返回与指定键对应的项
     */
    final Entry<K,V> getEntry(Object key) {
        // 为了提高性能，加载（Offload）基于比较器的版本
        if (comparator != null) // 使用比较器的getEntry版本，返回与指定键对应的项
            return getEntryUsingComparator(key);
        if (key == null)	//  如果指定键为 null 并且此映射使用自然顺序
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key; // 使用自然顺序比较器
        Entry<K,V> p = root; // 父节点
        while (p != null) {
            int cmp = k.compareTo(p.key);
            if (cmp < 0)	// 左子节点
                p = p.left;
            else if (cmp > 0) // 右子节点
                p = p.right;
            else
                return p;
        }
        return null;
    }

    /**
     * 使用比较器的getEntry版本。
     * 从getEntry中分离出来以获得性能。
     * (对于不太依赖于比较器性能的大多数方法，这不值得这样做，但在这里是值得的。)
     * @param key	要返回其关联值的键 
     * @return 返回与指定键对应的项
     */
    final Entry<K,V> getEntryUsingComparator(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            Entry<K,V> p = root; // 父节点
            while (p != null) {
                int cmp = cpr.compare(k, p.key);
                if (cmp < 0) // 左子节点
                    p = p.left;
                else if (cmp > 0)	// 右子节点
                    p = p.right;
                else
                    return p;
            }
        }
        return null;
    }

    /**
     * 获取与指定键对应的项;
     * 如果没有这样的条目存在,返回的条目键大于指定的键;
     * 如果不存在这样的条目(即，树中最大的键小于指定的键)，返回null。
     * @param key	要返回其关联值的键 
     * @return 返回与指定键对应的项
     */
    final Entry<K,V> getCeilingEntry(K key) {
        Entry<K,V> p = root;	// 父节点
        while (p != null) {
            int cmp = compare(key, p.key);
            if (cmp < 0) {
                if (p.left != null)	// 左子节点
                    p = p.left;
                else
                    return p;
            } else if (cmp > 0) {
                if (p.right != null) {	// 右子节点
                    p = p.right;
                } else {	// 父节点
                    Entry<K,V> parent = p.parent;
                    Entry<K,V> ch = p;
                    while (parent != null && ch == parent.right) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            } else
                return p;
        }
        return null;
    }

    /**
     * 获取与指定键对应的项;
     * 如果没有这样的条目存在,返回条目的最大键少于指定的键;
     * 如果不存在这样的条目，则返回null。
     * @param key	要返回其关联值的键 
     * @return 返回与指定键对应的项
     */
    final Entry<K,V> getFloorEntry(K key) {
        Entry<K,V> p = root;
        while (p != null) {
            int cmp = compare(key, p.key);
            if (cmp > 0) {
                if (p.right != null)	// 右子节点
                    p = p.right;
                else
                    return p;
            } else if (cmp < 0) {
                if (p.left != null) {	// 左子节点
                    p = p.left;
                } else {	//父节点
                    Entry<K,V> parent = p.parent;
                    Entry<K,V> ch = p;
                    while (parent != null && ch == parent.left) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            } else
                return p;

        }
        return null;
    }

    /**
     * 获取大于指定键的最小键的项;
     * 如果没有这样的条目存在,返回的条目键大于指定的键;
     * 如果不存在这样的条目，则返回null。
     * @param key	要返回其关联值的键 
     * @return 返回与指定键对应的项
     */
    final Entry<K,V> getHigherEntry(K key) {
        Entry<K,V> p = root;	// 根节点
        while (p != null) {
            int cmp = compare(key, p.key);
            if (cmp < 0) {	// 左子节点
                if (p.left != null)
                    p = p.left;
                else
                    return p;
            } else {
                if (p.right != null) {	// 右子节点
                    p = p.right;
                } else {	// 父节点
                    Entry<K,V> parent = p.parent;
                    Entry<K,V> ch = p;
                    while (parent != null && ch == parent.right) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            }
        }
        return null;
    }

    /**
     * 返回小于指定键的最大键的项;
     * 如果不存在这样的条目(即，则树中最小的键值大于指定的键值)，返回null。
     * @param key	要返回其关联值的键 
     * @return 返回与指定键对应的项
     */
    final Entry<K,V> getLowerEntry(K key) {
        Entry<K,V> p = root;	// 根节点
        while (p != null) {
            int cmp = compare(key, p.key);
            if (cmp > 0) {
                if (p.right != null)	// 右子节点
                    p = p.right;
                else
                    return p;
            } else {
                if (p.left != null) {	// 左子节点
                    p = p.left;
                } else {	// 父节点
                    Entry<K,V> parent = p.parent;
                    Entry<K,V> ch = p;
                    while (parent != null && ch == parent.left) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            }
        }
        return null;
    }

    /**
     * 将指定值与此映射中的指定键进行关联。如果该映射以前包含此键的映射关系，那么将替换旧值。
     * 覆写：类 AbstractMap<K,V> 中的 put
     * @param key	要与指定值关联的键
     * @param value	要与指定键关联的值 
     * @return	与 key 关联的先前值；如果没有针对 key 的映射关系，则返回 null。
     * 		    （返回 null 还可能表示该映射以前将 null 与 key 关联。） 
     */
    public V put(K key, V value) {
        Entry<K,V> t = root;
        if (t == null) {	// 根节点为空，直接设置为根节点
            compare(key, key); // 类型(可能为空)检查

            root = new Entry<>(key, value, null);
            size = 1;
            modCount++;
            return null;
        }
        int cmp;
        Entry<K,V> parent;
        // split comparator and comparable paths
        Comparator<? super K> cpr = comparator;	// 获取比较器
        if (cpr != null) {	// 使用指定的比较器
            do {
                parent = t;
                cmp = cpr.compare(key, t.key);
                if (cmp < 0)	// 左子节点
                    t = t.left;
                else if (cmp > 0)	// 右子节点
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        else {	// 使用自然顺序比较器
            if (key == null)	// 如果指定键为 null 并且此映射使用自然顺序，或者其比较器不允许使用 null 键
                throw new NullPointerException();
            @SuppressWarnings("unchecked")
            Comparable<? super K> k = (Comparable<? super K>) key;
            do {
                parent = t;
                cmp = k.compareTo(t.key);
                if (cmp < 0)	// 左子节点
                    t = t.left;
                else if (cmp > 0)	// 右节点
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        Entry<K,V> e = new Entry<>(key, value, parent);
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
        fixAfterInsertion(e); // 插入节点e
        size++;
        modCount++;
        return null;
    }

    /**
     * 如果此 TreeMap 中存在该键的映射关系，则将其删除。 
     * 覆写：类 AbstractMap<K,V> 中的 remove
     * @param key	将为其移除映射关系的键 
     * @return	返回与 key 关联的先前值，如果没有针对 key 的映射关系，则返回 null。
     * 			（返回 null 还可能表示该映射以前将 null 与 key 关联。） 
     */
    public V remove(Object key) {
        Entry<K,V> p = getEntry(key);
        if (p == null)
            return null;

        V oldValue = p.value;
        deleteEntry(p);	// 删除节点p，然后重新平衡树。
        return oldValue;
    }

    /**
     * 从此映射中移除所有映射关系。在此调用返回之后，映射将为空。
     * 覆写：类 AbstractMap<K,V> 中的 clear
     */
    public void clear() {
        modCount++;
        size = 0;
        root = null;
    }

    /**
     * 返回此 TreeMap 实例的浅表副本。（键和值本身不被复制。） 
     * 覆写：类 AbstractMap<K,V> 中的 clone
     * @return	此映射的浅表副本
     */
    public Object clone() {
        TreeMap<?,?> clone;
        try {
            clone = (TreeMap<?,?>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }

        // 克隆到“原始”状态(比较器除外)
        clone.root = null;
        clone.size = 0;
        clone.modCount = 0;
        clone.entrySet = null;
        clone.navigableKeySet = null;
        clone.descendingMap = null;

        // 初始化克隆
        try {
            clone.buildFromSorted(size, entrySet().iterator(), null, null);
        } catch (java.io.IOException cannotHappen) {
        } catch (ClassNotFoundException cannotHappen) {
        }

        return clone;
    }

    // NavigableMap API方法

    /**
     * 返回一个与此映射中的最小键关联的键-值映射关系；如果映射为空，则返回 null。 
     * 覆写：接口 NavigableMap<K,V> 中的 firstEntry
     * @return	带有最小键的条目；如果此映射为空，则返回 null
     */
    @Override
    public java.util.Map.Entry<K, V> firstEntry() {
        return exportEntry(getFirstEntry());
    }

    /**
     * 返回与此映射中的最大键关联的键-值映射关系；如果映射为空，则返回 null。
     * 覆写：接口 NavigableMap<K,V> 中的 lastEntry
     * @return	带有最大键的条目；如果此映射为空，则返回 null
     */
    @Override
    public java.util.Map.Entry<K, V> lastEntry() {
        return exportEntry(getLastEntry());
    }
    /**
     * 移除并返回与此映射中的最小键关联的键-值映射关系；如果映射为空，则返回 null。
     * 覆写：接口 NavigableMap<K,V> 中的 pollFirstEntry
     * @return	此映射中被移除的第一个条目；如果此映射为空，则返回 null
     */
    @Override
    public java.util.Map.Entry<K, V> pollFirstEntry() {
        Entry<K,V> p = getFirstEntry();	// 返回树映射中的最后一个条目
        Map.Entry<K,V> result = exportEntry(p);
        if (p != null)
            deleteEntry(p);	// 删除节点p，然后重新平衡树。
        return result;
    }

    /**
     * 移除并返回与此映射中的最大键关联的键-值映射关系；如果映射为空，则返回 null。
     * 覆写：接口 NavigableMap<K,V> 中的 pollLastEntry
     * @return	此映射中被移除的最后一个条目；如果此映射为空，则返回 null
     */
    @Override
    public java.util.Map.Entry<K, V> pollLastEntry() {
        Entry<K,V> p = getLastEntry();	// 返回树映射中的最后一个条目
        Map.Entry<K,V> result = exportEntry(p);
        if (p != null)
            deleteEntry(p);	// 删除节点p，然后重新平衡树。
        return result;
    }

    /**
     * 返回一个键-值映射关系，它与严格小于给定键的最大键关联；如果不存在这样的键，则返回 null。
     * 覆写：接口 NavigableMap<K,V> 中的 lowerEntry
     * @param key	键
     * @return	最大键小于 key 的条目；如果不存在这样的键，则返回 null
     */
    @Override
    public java.util.Map.Entry<K, V> lowerEntry(K key) {
        return exportEntry(getLowerEntry(key));
    }

    /**
     * 返回严格小于给定键的最大键；如果不存在这样的键，则返回 null。
     * 覆写：接口 NavigableMap<K,V> 中的 lowerKey
     * @param key	键
     * @return	小于 key 的最大键；如果不存在这样的键，则返回 null
     */
    @Override
    public K lowerKey(K key) {
        return keyOrNull(getLowerEntry(key));
    }

    /**
     * 返回一个键-值映射关系，它与小于等于给定键的最大键关联；
     * 如果不存在这样的键，则返回 null。
     * 覆写：接口 NavigableMap<K,V> 中的 floorEntry
     * @param key	键
     * @return	最大键小于等于 key 的条目；如果不存在这样的键，则返回 null
     */
    @Override
    public java.util.Map.Entry<K, V> floorEntry(K key) {
        return exportEntry(getFloorEntry(key));
    }

    /**
     * 返回小于等于给定键的最大键；如果不存在这样的键，则返回 null。
     * 覆写：接口 NavigableMap<K,V> 中的 floorKey
     * @param key	键
     * @return	小于等于 key 的最大键；如果不存在这样的键，则返回 null
     */
    @Override
    public K floorKey(K key) {
        return keyOrNull(getFloorEntry(key));
    }

    /**
     * 返回一个键-值映射关系，它与大于等于给定键的最小键关联；如果不存在这样的键，则返回 null。
     * 覆写：接口 NavigableMap<K,V> 中的 ceilingEntry
     * @param key	键
     * @return	最小键大于等于 key 的条目；如果不存在这样的键，则返回 null
     */
    @Override
    public java.util.Map.Entry<K, V> ceilingEntry(K key) {
        return exportEntry(getCeilingEntry(key));
    }

    /**
     * 返回大于等于给定键的最小键；如果不存在这样的键，则返回 null。
     * 覆写：接口 NavigableMap<K,V> 中的 ceilingKey
     * @param key	键
     * @return	大于等于 key 的最小键；如果不存在这样的键，则返回 null
     */
    @Override
    public K ceilingKey(K key) {
        return keyOrNull(getCeilingEntry(key));
    }

    /**
     * 返回一个键-值映射关系，它与严格大于给定键的最小键关联；
     * 如果不存在这样的键，则返回 null。
     * 覆写：接口 NavigableMap<K,V> 中的 higherEntry
     * @param key	键
     * @return	最小键大于 key 的条目；如果不存在这样的键，则返回 null
     */
    @Override
    public java.util.Map.Entry<K, V> higherEntry(K key) {
        return exportEntry(getHigherEntry(key));
    }

    /**
     * 返回严格大于给定键的最小键；如果不存在这样的键，则返回 null。
     * 覆写：接口 NavigableMap<K,V> 中的 higherKey
     * @param key	键
     * @return	大于 key 的最小键；如果不存在这样的键，则返回 null
     */
    @Override
    public K higherKey(K key) {
        return keyOrNull(getHigherEntry(key));
    }
    // 视图

    /**
     * 在第一次请求此视图时，初始化为包含条目集视图实例的字段。
     * 视图是无状态的，因此没有理由创建多个视图。
     */
    private transient EntrySet entrySet;
    private transient KeySet<K> navigableKeySet;
    private transient NavigableMap<K,V> descendingMap;

    /**
     * 返回此映射包含的键的 Set 视图。
     * set 的迭代器将按升序返回这些键。该 set 受映射支持，
     * 所以对映射的更改可在此 set 中反映出来，反之亦然。
     * 如果对该 set 进行迭代的同时修改了映射（通过迭代器自己的 remove 操作除外），
     * 则迭代结果是不确定的。此 set 支持元素移除，
     * 通过 Iterator.remove、Set.remove、removeAll、retainAll 和 clear 操作，
     * 可从映射中移除相应的映射关系。它不支持 add 或 addAll 操作。
     * 覆写：类 AbstractMap<K,V> 中的 keySet
     * @return	此映射中包含的键的 set 视图
     */
    public Set<K> keySet() {
        return navigableKeySet();
    }

    /**
     * 返回此映射中所包含键的 NavigableSet 视图。set 的迭代器按升序返回键。
     * set 受映射支持，因此对映射的更改将反映在 set 中，反之亦然。
     * 如果正在对 set 进行迭代的同时修改了映射（通过迭代器自己的 remove 操作除外），
     * 则迭代结果是不确定的。set 支持元素移除，即通过 Iterator.remove、Set.remove、
     * removeAll、retainAll 和 clear 操作从映射中移除相应的映射关系。它不支持 add 或 addAll 操作。
     * 覆写：接口 NavigableMap<K,V> 中的 navigableKeySet
     * @return	此映射中键的可导航 set 视图
     */
    @Override
    public NavigableSet<K> navigableKeySet() {
        KeySet<K> nks = navigableKeySet;
        return (nks != null) ? nks : (navigableKeySet = new KeySet<>(this));
    }

    /**
     * 返回此映射中所包含键的逆序 NavigableSet 视图。set 的迭代器按降序返回键。
     * set 受映射支持，因此对映射的更改将反映在 set 中，反之亦然。
     * 如果正在对 set 进行迭代的同时修改了映射（通过迭代器自己的 remove 操作除外），
     * 则迭代结果是不确定的。set 支持元素移除，即通过 Iterator.remove、Set.remove、
     * removeAll、retainAll 和 clear 操作从映射中移除相应的映射关系。它不支持 add 或 addAll 操作。
     * 覆写：接口 NavigableMap<K,V> 中的 descendingKeySet
     * @return	此映射中键的逆序可导航 set 视图
     */
    @Override
    public NavigableSet<K> descendingKeySet() {
        return descendingMap().navigableKeySet();
    }

    /**
     * 返回此映射包含的值的 Collection 视图。
     * 该 collection 的迭代器将按相关键的升序返回这些值。该 collection 受映射支持，
     * 所以对映射的更改可在此 collection 中反映出来，反之亦然。
     * 如果对该 collection 进行迭代的同时修改了映射（通过迭代器自己的 remove 操作除外），
     * 则迭代结果是不确定的。该 collection 支持元素的移除，通过 Iterator.remove、
     * Collection.remove、removeAll、retainAll 和 clear 操作，可从映射中移除相应的映射关系。
     * 它不支持 add 或 addAll 操作。
     * 覆写：类 AbstractMap<K,V> 中的 values
     * @return	此映射中包含的值的 collection 视图
     */
    public Collection<V> values() {
        Collection<V> vs = values;
        if (vs == null) {
            vs = new Values();
            values = vs;
        }
        return vs;
    }

    /**
     * 返回此映射中包含的映射关系的 Set 视图。该 set 的迭代器将按升序返回这些条目。
     * 该 set 受映射支持，所以对映射的更改可在此 set 中反映出来，反之亦然。
     * 如果对该 set 进行迭代的同时修改了映射（通过迭代器自己的 remove 操作，
     * 或者通过在迭代器返回的映射条目上执行 setValue 操作除外），则迭代结果是不确定的。
     * 此 set 支持元素移除，通过 Iterator.remove、Set.remove、removeAll、retainAll 
     * 和 clear 操作，可从映射中移除相应的映射关系。它不支持 add 或 addAll 操作。 
     * 覆写：类 AbstractMap<K,V> 中的 entrySet
     * @return	此映射中包含的映射关系的 set 视图
     */
    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        EntrySet es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
    }

    /**
     * 返回此映射中所包含映射关系的逆序视图。降序映射受此映射的支持，
     * 因此对映射的更改将反映在降序映射中，反之亦然。
     * 如果在对任一映射的 collection 视图进行迭代的同时修改了任一映射
     * （通过迭代器自己的 remove 操作除外），则迭代结果是不确定的。 
     * 返回映射的顺序等于 Collections.reverseOrder(comparator())。
     * 表达式 m.descendingMap().descendingMap() 返回的 m 视图基本等于 m。 
     * 覆写：接口 NavigableMap<K,V> 中的 descendingMap
     * @return	此映射的逆序视图
     */
    @Override
    public NavigableMap<K, V> descendingMap() {
        NavigableMap<K, V> km = descendingMap;
        return (km != null) ? km :
                (descendingMap = new DescendingSubMap<>(this,
                        true, null, true,
                        true, null, true));
    }

    /**
     * 返回此映射的部分视图，其键的范围从 fromKey 到 toKey。
     * 如果 fromKey 和 toKey 相等，则返回的映射为空，除非 fromExclusive 和 toExclusive 都为 true。
     * 返回的映射受此映射支持，因此返回映射中的更改将反映在此映射中，反之亦然。
     * 返回的映射支持此映射支持的所有可选映射操作。 如果试图在返回映射的范围之外
     * 插入一个键，或者构造一个任一端点位于其范围之外的子映射，则返回的映射将抛出 IllegalArgumentException。
     * 覆写：接口 NavigableMap<K,V> 中的 subMap
     * @param fromKey	返回映射中键的低端点
     * @param fromInclusive	如果低端点要包含在返回的视图中，则为 true
     * @param toKey	返回映射中键的高端点
     * @param toInclusive	如果高端点要包含在返回的视图中，则为 true
     * @return	此映射的部分视图，其键范围从 fromKey 到 toKey
     */
    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey,
                                     boolean toInclusive) {
        return new AscendingSubMap<>(this,
                false, fromKey, fromInclusive,
                false, toKey,   toInclusive);
    }

    /**
     * 返回此映射的部分视图，其键小于（或等于，如果 inclusive 为 true）toKey。
     * 返回的映射受此映射支持，因此返回映射中的更改将反映在此映射中，反之亦然。
     * 返回的映射支持此映射支持的所有可选映射操作。 如果试图在返回映射的范围之外
     * 插入一个键，则返回的映射将抛出 IllegalArgumentException。
     * 覆写：接口 NavigableMap<K,V> 中的 headMap
     * @param toKey	返回映射中键的高端点
     * @param inclusive	如果高端点要包含在返回的视图中，则为 true
     * @return	此映射的部分视图，其键小于（或等于，如果 inclusive 为 true）toKey
     */
    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return new AscendingSubMap<>(this,
                true,  null,  true,
                false, toKey, inclusive);
    }

    /**
     * 返回此映射的部分视图，其键大于（或等于，如果 inclusive 为 true）fromKey。
     * 返回的映射受此映射支持，因此返回映射中的更改将反映在此映射中，反之亦然。
     * 返回的映射支持此映射支持的所有可选映射操作。 如果试图在返回映射的范围之外
     * 插入一个键，则返回的映射将抛出 IllegalArgumentException。
     * 覆写：接口 NavigableMap<K,V> 中的 tailMap
     * @param fromKey	返回映射中键的低端点
     * @param inclusive	如果低端点要包含在返回的视图中，则为 true
     * @return	此映射的部分视图，其键大于（或等于，如果 inclusive 为 true）fromKey
     */
    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return new AscendingSubMap<>(this,
                false, fromKey, inclusive,
                true,  null,    true);
    }

    /**
     * 返回此映射的部分视图，其键值的范围从 fromKey（包括）到 toKey（不包括）。
     * （如果 fromKey 和 toKey 相等，则返回映射为空。）返回的映射受此映射支持，
     * 所以在返回映射中的更改将反映在此映射中，反之亦然。返回的映射支持此映射支持的所有可选映射操作。
     * 如果试图在返回映射的范围之外插入键，则返回的映射将抛出 IllegalArgumentException。
     * 等效于 subMap(fromKey, true, toKey, false)。
     * 覆写：接口 NavigableMap<K,V> 中的 subMap
     * @param fromKey	返回映射中键的低端点（包括）
     * @param toKey	返回映射中键的高端点（不包括）
     * @return	此映射的部分视图，其键值的范围从 fromKey（包括）到 toKey（不包括）
     */
    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return subMap(fromKey, true, toKey, false);
    }

    /**
     * 返回此映射的部分视图，其键值严格小于 toKey。
     * 返回的映射受此映射支持，所以在返回映射中的更改将反映在映射中，反之亦然。
     * 返回的映射支持此映射支持的所有可选映射操作。
     * 如果试图在返回映射的范围之外插入键，则返回的映射将抛出 IllegalArgumentException。
     * 等效于 headMap(toKey, false)。
     * 覆写：接口 NavigableMap<K,V> 中的 headMap
     * @param toKey	返回映射中键的高端点（不包括）
     * @return	此映射的部分视图，该映射的键严格小于 toKey
     */
    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return headMap(toKey, false);
    }

    /**
     * 返回此映射的部分视图，其键大于等于 fromKey。
     * 返回的映射受此映射支持，所以在返回映射中的更改将反映在映射中，反之亦然。
     * 返回的映射支持此映射支持的所有可选映射操作。
     * 如果试图在返回映射的范围之外插入键，则返回的映射将抛出 IllegalArgumentException。
     * 等效于 tailMap(fromKey, true)。
     * 覆写：接口 NavigableMap<K,V> 中的 tailMap
     * @param fromKey	返回映射中键的低端点（包括）
     * @return	此映射的部分视图，其键大于等于 fromKey
     */
    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return tailMap(fromKey, true);
    }

    /**
     * 仅当当前映射到指定值时，才替换指定键的项。
     * @implSpec
     * 对于此映射，默认实现相当于:
     *  if (map.containsKey(key) && Objects.equals(map.get(key), value)) {
     *  	map.put(key, newValue);
     *  	return true;
     *  } else {
     *  	return false;
     *  }
     * 如果oldValue为空，默认实现不会为不支持空值的映射抛出NullPointerException，
     * 除非newValue也是空的。
     * 默认实现不保证此方法的同步或原子性属性。
     * 任何提供原子性保证的实现都必须覆盖此方法并记录其并发性属性。
     * 覆写：接口 Map<K,V> 中的 replace
     * @param key	与指定值相关联的键
     * @param oldValue	期望与指定键关联的值
     * @param newValue	与指定键相关联的值
     * @return 	如果值被替换，则为true
     */
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        Entry<K,V> p = getEntry(key);
        if (p!=null && Objects.equals(oldValue, p.value)) {
            p.value = newValue;
            return true;
        }
        return false;
    }

    /**
     * 仅当指定键的项当前映射到某个值时，才替换该项。
     * @implSpec
     * 对于此映射，默认实现相当于:
     * if (map.containsKey(key)) {
     *     return map.put(key, value);
     * } else {
     *     return null;
     * }
     * 默认实现不保证此方法的同步或原子性属性。
     * 任何提供原子性保证的实现都必须覆盖此方法并记录其并发性属性。
     * 覆写：接口 Map<K,V> 中的 replace
     * @param key	与指定值相关联的键 
     * @param value	与指定的键相关联的值
     * @return 与指定键关联的前一个值，如果没有键的映射，则为空。
     * 			(如果实现支持null值，null返回还可以指示以前将null与键关联的映射。)
     */
    @Override
    public V replace(K key, V value) {
        Entry<K,V> p = getEntry(key); // 返回与key对应的项
        if (p!=null) {
            V oldValue = p.value;
            p.value = value;
            return oldValue;
        }
        return null;
    }

    /**
     * 为映射中的每个条目执行给定的操作，直到处理完所有条目或操作引发异常为止。
     * 除非实现类另有指定，否则操作将按照条目集迭代的顺序执行(如果指定了迭代顺序)。
     * 该操作引发的异常将传递给调用者。
     * @implSpec
     * 对于此映射，默认实现相当于:
     * for (Map.Entry<K, V> entry : map.entrySet())
     *     action.accept(entry.getKey(), entry.getValue());
     * }
     * 默认实现不保证此方法的同步或原子性属性。
     * 任何提供原子性保证的实现都必须覆盖此方法并记录其并发性属性。
     * 覆写：接口 Map<K,V> 中的 forEach
     * @param action 对每个条目执行的操作
     */
    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);	// 非空校验
        int expectedModCount = modCount;
        for (Entry<K, V> e = getFirstEntry(); e != null; e = successor(e)) {
            action.accept(e.key, e.value);	// 执行操作

            if (expectedModCount != modCount) { // 如果在迭代过程中发现一个条目被删除
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * 将每个条目的值替换为对该条目调用给定函数的结果，
     * 直到所有条目都被处理或函数抛出异常。函数抛出的异常将传递给调用者。
     * @implSpec
     * 对于此映射，默认实现相当于:
     * for (Map.Entry<K, V> entry : map.entrySet())
     *     entry.setValue(function.apply(entry.getKey(), entry.getValue()));
     * }
     * 默认实现不保证此方法的同步或原子性属性。
     * 任何提供原子性保证的实现都必须覆盖此方法并记录其并发性属性。
     * 覆写：接口 Map<K,V> 中的 replaceAll
     * @param function 应用于每个条目的函数
     */
    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);	// 非空校验
        int expectedModCount = modCount;

        for (Entry<K, V> e = getFirstEntry(); e != null; e = successor(e)) {
            e.value = function.apply(e.key, e.value);	// 执行函数

            if (expectedModCount != modCount) { // 如果在迭代过程中发现一个条目被删除
                throw new ConcurrentModificationException();
            }
        }
    }

    //视图类支持
    class Values extends AbstractCollection<V> {
        /**
         * 返回值的迭代器。
         * @return	值的迭代器
         */
        public Iterator<V> iterator() {
            return new ValueIterator(getFirstEntry());
        }
        /**
         * 返回TreeMap的键-值映射关系数。
         * @return
         */
        public int size() {
            return TreeMap.this.size();
        }
        /**
         * 如果此映射为指定值映射一个或多个键，则返回 true。
         * @param o	将测试其是否存在于此映射中的值
         * @return	如果存在到 value 的映射关系，则返回 true；否则返回 false
         */
        public boolean contains(Object o) {
            return TreeMap.this.containsValue(o);
        }

        /**
         * 根据指定值移除节点
         * @param o	需要移除的指定值
         * @return	移除成功返回true；否则返回false
         */
        public boolean remove(Object o) {
            for (Entry<K,V> e = getFirstEntry(); e != null; e = successor(e)) {
                if (valEquals(e.getValue(), o)) { // 测试e节点的值和o是否相等。
                    deleteEntry(e); // 删除节点e，然后重新平衡树。
                    return true;
                }
            }
            return false;
        }

        /**
         * 从此映射中移除所有映射关系。
         */
        public void clear() {
            TreeMap.this.clear();
        }

        /**
         * 返回一个值的spliterator。
         */
        public Spliterator<V> spliterator() {
            return new ValueSpliterator<K,V>(TreeMap.this, null, null, 0, -1, 0);
        }
    }

    class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        /**
         * 返回节点的迭代器。
         * @return
         */
        public Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator(getFirstEntry());
        }

        /**
         * 如果此映射为指定值映射一个或多个键，则返回 true。
         * @param o	将测试其是否存在于此映射中的值
         * @return	如果存在到 value 的映射关系，则返回 true；否则返回 false
         */
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            Object value = entry.getValue();
            Entry<K,V> p = getEntry(entry.getKey()); // 返回与entry.getKey()对应的项
            return p != null && valEquals(p.getValue(), value);
        }
        /**
         * 移除指定的节点
         * @param o	需要移除的指定节点
         * @return	移除成功返回true；否则返回false
         */
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            Object value = entry.getValue();
            Entry<K,V> p = getEntry(entry.getKey());// 返回与entry.getKey()对应的项
            if (p != null && valEquals(p.getValue(), value)) {
                deleteEntry(p); // 删除节点p，然后重新平衡树。
                return true;
            }
            return false;
        }
        /**
         * 返回TreeMap的键-值映射关系数。
         * @return
         */
        public int size() {
            return TreeMap.this.size();
        }
        /**
         * 从此映射中移除所有映射关系。
         */
        public void clear() {
            TreeMap.this.clear();
        }
        /**
         * 返回一个节点的spliterator。
         */
        public Spliterator<Map.Entry<K,V>> spliterator() {
            return new EntrySpliterator<K,V>(TreeMap.this, null, null, 0, -1, 0);
        }
    }

    /*
     * 与Values 和EntrySet不同，KeySet类是静态的，它委托给NavigableMap以允许子映射使用，
     * 这就克服了需要对以下迭代器方法进行类型测试的缺点，
     * 这些方法在main类和子映射类中定义得很合适。
     */

    /**
     * 返回键的迭代器
     * @return	键的迭代器
     */
    Iterator<K> keyIterator() {
        return new KeyIterator(getFirstEntry());
    }

    /**
     * 返回键的降序迭代器
     * @return	键的降序迭代器
     */
    Iterator<K> descendingKeyIterator() {
        return new DescendingKeyIterator(getLastEntry());
    }

    static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
        private final NavigableMap<E, ?> m;
        KeySet(NavigableMap<E,?> map) { m = map; }
        /**
         * 返回键的迭代器
         * @return
         */
        public Iterator<E> iterator() {
            if (m instanceof TreeMap)
                return ((TreeMap<E,?>)m).keyIterator();
            else
                return ((TreeMap.NavigableSubMap<E,?>)m).keyIterator();
        }
        /**
         * 返回键的降序迭代器
         * @return
         */
        public Iterator<E> descendingIterator() {
            if (m instanceof TreeMap)
                return ((TreeMap<E,?>)m).descendingKeyIterator();
            else
                return ((TreeMap.NavigableSubMap<E,?>)m).descendingKeyIterator();
        }

        public int size() { return m.size(); }
        public boolean isEmpty() { return m.isEmpty(); }
        public boolean contains(Object o) { return m.containsKey(o); }
        public void clear() { m.clear(); }
        public E lower(E e) { return m.lowerKey(e); }
        public E floor(E e) { return m.floorKey(e); }
        public E ceiling(E e) { return m.ceilingKey(e); }
        public E higher(E e) { return m.higherKey(e); }
        public E first() { return m.firstKey(); }
        public E last() { return m.lastKey(); }
        public Comparator<? super E> comparator() { return m.comparator(); }
        public E pollFirst() {
            Map.Entry<E,?> e = m.pollFirstEntry();
            return (e == null) ? null : e.getKey();
        }
        public E pollLast() {
            Map.Entry<E,?> e = m.pollLastEntry();
            return (e == null) ? null : e.getKey();
        }
        public boolean remove(Object o) {
            int oldSize = size();
            m.remove(o);
            return size() != oldSize;
        }
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive,
                                      E toElement,   boolean toInclusive) {
            return new KeySet<>(m.subMap(fromElement, fromInclusive,
                    toElement,   toInclusive));
        }
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            return new KeySet<>(m.headMap(toElement, inclusive));
        }
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            return new KeySet<>(m.tailMap(fromElement, inclusive));
        }
        public SortedSet<E> subSet(E fromElement, E toElement) {
            return subSet(fromElement, true, toElement, false);
        }
        public SortedSet<E> headSet(E toElement) {
            return headSet(toElement, false);
        }
        public SortedSet<E> tailSet(E fromElement) {
            return tailSet(fromElement, true);
        }
        public NavigableSet<E> descendingSet() {
            return new KeySet<>(m.descendingMap());
        }

        public Spliterator<E> spliterator() {
            return keySpliteratorFor(m);
        }
    }

    /**
     * TreeMap迭代器的基类
     * @param <T>
     */
    abstract class PrivateEntryIterator<T> implements Iterator<T> {
        Entry<K,V> next;
        Entry<K,V> lastReturned;
        int expectedModCount;

        PrivateEntryIterator(Entry<K,V> first) {
            expectedModCount = modCount;
            lastReturned = null;
            next = first;
        }
        /**
         * 如果仍有元素可以迭代，则返回 true。（换句话说，如果 next 返回了元素而不是抛出异常，则返回 true）。 
         * @return	如果有下一个节点，则返回true
         */
        public final boolean hasNext() {
            return next != null;
        }

        /**
         * 返回下一个节点
         * @return 下一个节点
         */
        final Entry<K,V> nextEntry() {
            Entry<K,V> e = next;	// e为下一个节点
            if (e == null)
                throw new NoSuchElementException();
            if (modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                throw new ConcurrentModificationException();
            next = successor(e); // 返回e的继承项
            lastReturned = e;
            return e;
        }

        /**
         * 返回上一个节点
         * @return	上一个节点
         */
        final Entry<K,V> prevEntry() {
            Entry<K,V> e = next;	// e为下一个节点
            if (e == null)
                throw new NoSuchElementException();
            if (modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                throw new ConcurrentModificationException();
            next = predecessor(e); // 返回指定项的前身
            lastReturned = e;
            return e;
        }

        /**
         * 移除迭代器返回的最后一个原色
         */
        public void remove() {
            if (lastReturned == null) // 如果尚未调用 next 方法，或者在上一次调用 next 方法之后已经调用了 remove 方法。
                throw new IllegalStateException();
            if (modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                throw new ConcurrentModificationException();
            // 删除的条目将由它们的后继条目替换
            if (lastReturned.left != null && lastReturned.right != null)
                next = lastReturned;
            deleteEntry(lastReturned); // 删除节点p，然后重新平衡树。
            expectedModCount = modCount;
            lastReturned = null;
        }
    }

    /**
     *	节点的迭代器
     */
    final class EntryIterator extends PrivateEntryIterator<Map.Entry<K,V>> {
        EntryIterator(Entry<K,V> first) {
            super(first);
        }
        /**
         * 返回下一个节点
         * @return	下一个节点
         */
        public Map.Entry<K,V> next() {
            return nextEntry();
        }
    }

    /**
     *	值的迭代器
     */
    final class ValueIterator extends PrivateEntryIterator<V> {
        ValueIterator(Entry<K,V> first) {
            super(first);
        }
        /**
         * 返回下一个节点的值
         * @return	下一个节点的值
         */
        public V next() {
            return nextEntry().value;
        }
    }

    /**
     * 键的迭代器
     *
     */
    final class KeyIterator extends PrivateEntryIterator<K> {
        KeyIterator(Entry<K,V> first) {
            super(first);
        }
        /**
         * 返回下一个节点的键
         * @return	下一个节点的键
         */
        public K next() {
            return nextEntry().key;
        }
    }

    final class DescendingKeyIterator extends PrivateEntryIterator<K> {
        DescendingKeyIterator(Entry<K,V> first) {
            super(first);
        }
        /**
         * 返回上一个节点的键
         * @return	上一个节点的键
         */
        public K next() {
            return prevEntry().key;
        }
        /**
         * 移除迭代器返回的最后一个元素
         */
        public void remove() {
            if (lastReturned == null)	// 如果尚未调用 next 方法，或者在上一次调用 next 方法之后已经调用了 remove 方法。
                throw new IllegalStateException();
            if (modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                throw new ConcurrentModificationException();
            deleteEntry(lastReturned); // 删除节点lastReturned，然后重新平衡树。
            lastReturned = null;
            expectedModCount = modCount;
        }
    }

    //小工具
    /**
     * 使用此树映射的正确比较方法比较两个键。
     * @param k1
     * @param k2
     * @return
     */
    @SuppressWarnings("unchecked")
    final int compare(Object k1, Object k2) {
        return comparator==null ? ((Comparable<? super K>)k1).compareTo((K)k2)
                : comparator.compare((K)k1, (K)k2);
    }

    /**
     * 测试两个值是否相等。与o1.equals(o2)的不同之处在于它正确地处理了null o1。
     * @param o1
     * @param o2
     * @return
     */
    static final boolean valEquals(Object o1, Object o2) {
        return (o1==null ? o2==null : o1.equals(o2));
    }

    /**
     * 返回SimpleImmutableEntry for entry，如果为空则返回null
     * @param e	项
     * @return
     */
    static <K,V> Map.Entry<K,V> exportEntry(TreeMap.Entry<K,V> e) {
        return (e == null) ? null :
                new AbstractMap.SimpleImmutableEntry<>(e);
    }

    /**
     * 返回条目的键值，如果为空则返回null
     * @param e	项
     * @return
     */
    static <K,V> K keyOrNull(TreeMap.Entry<K,V> e) {
        return (e == null) ? null : e.key;
    }

    /**
     * 返回与指定项对应的键。
     * @param e	项
     * @return
     */
    static <K> K key(Entry<K,?> e) {
        if (e==null) // 如果条目为空
            throw new NoSuchElementException();
        return e.key;
    }
    // 子映射
    /**
     * 虚值作为无界子映射迭代器的不可匹配栅栏键
     */
    private static final Object UNBOUNDED = new Object();

    abstract static class NavigableSubMap<K,V> extends AbstractMap<K,V>
            implements NavigableMap<K,V>, java.io.Serializable {
        private static final long serialVersionUID = -2102997345730753016L;
        /**
         * The backing map.
         */
        final TreeMap<K,V> m;

        /**
         * 端点表示为三元组(fromStart, lo, loinclusion)和(toEnd, hi, hiinclusion)。
         * 如果fromStart为真，则低(绝对)界限是支持映射的起点，其他值将被忽略。
         * 否则，如果loinclusion为真，则lo为包容性界，否则lo为排他性界。上界也是一样。
         */
        final K lo, hi;
        final boolean fromStart, toEnd;
        final boolean loInclusive, hiInclusive;

        NavigableSubMap(TreeMap<K,V> m,
                        boolean fromStart, K lo, boolean loInclusive,
                        boolean toEnd,     K hi, boolean hiInclusive) {
            if (!fromStart && !toEnd) {
                if (m.compare(lo, hi) > 0)
                    throw new IllegalArgumentException("fromKey > toKey");
            } else {
                if (!fromStart) // type check
                    m.compare(lo, lo);
                if (!toEnd)
                    m.compare(hi, hi);
            }

            this.m = m;
            this.fromStart = fromStart;
            this.lo = lo;
            this.loInclusive = loInclusive;
            this.toEnd = toEnd;
            this.hi = hi;
            this.hiInclusive = hiInclusive;
        }

        // 内部工具

        /**
         * 判断指定键是否小于lo
         * @param key	指定键
         * @return
         */
        final boolean tooLow(Object key) {
            if (!fromStart) {
                int c = m.compare(key, lo);
                if (c < 0 || (c == 0 && !loInclusive))
                    return true;
            }
            return false;
        }
        /**
         * 判断指定键是否大于hi
         * @param key	指定键
         * @return
         */
        final boolean tooHigh(Object key) {
            if (!toEnd) {
                int c = m.compare(key, hi);
                if (c > 0 || (c == 0 && !hiInclusive))
                    return true;
            }
            return false;
        }
        /**
         * 判断指定键是否在范围内
         * @param key	指定键
         * @return
         */
        final boolean inRange(Object key) {
            return !tooLow(key) && !tooHigh(key);
        }

        final boolean inClosedRange(Object key) {
            return (fromStart || m.compare(key, lo) >= 0)
                    && (toEnd || m.compare(hi, key) >= 0);
        }

        final boolean inRange(Object key, boolean inclusive) {
            return inclusive ? inRange(key) : inClosedRange(key);
        }

        /*
         * 关系操作的绝对版本。
         * 子类使用类似名称的“子(sub)”版本映射到这些类，“子(sub)”版本反转降序映射的意义
         */

        final TreeMap.Entry<K,V> absLowest() {
            TreeMap.Entry<K,V> e =
                    (fromStart ?  m.getFirstEntry() :
                            (loInclusive ? m.getCeilingEntry(lo) :
                                    m.getHigherEntry(lo)));
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absHighest() {
            TreeMap.Entry<K,V> e =
                    (toEnd ?  m.getLastEntry() :
                            (hiInclusive ?  m.getFloorEntry(hi) :
                                    m.getLowerEntry(hi)));
            return (e == null || tooLow(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absCeiling(K key) {
            if (tooLow(key))
                return absLowest();
            TreeMap.Entry<K,V> e = m.getCeilingEntry(key);
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absHigher(K key) {
            if (tooLow(key))
                return absLowest();
            TreeMap.Entry<K,V> e = m.getHigherEntry(key);
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absFloor(K key) {
            if (tooHigh(key))
                return absHighest();
            TreeMap.Entry<K,V> e = m.getFloorEntry(key);
            return (e == null || tooLow(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absLower(K key) {
            if (tooHigh(key))
                return absHighest();
            TreeMap.Entry<K,V> e = m.getLowerEntry(key);
            return (e == null || tooLow(e.key)) ? null : e;
        }

        /** 返回用于升序遍历的绝对高栅栏 */
        final TreeMap.Entry<K,V> absHighFence() {
            return (toEnd ? null : (hiInclusive ?
                    m.getHigherEntry(hi) :
                    m.getCeilingEntry(hi)));
        }

        /** 返回绝对低栅栏进行下行遍历 */
        final TreeMap.Entry<K,V> absLowFence() {
            return (fromStart ? null : (loInclusive ?
                    m.getLowerEntry(lo) :
                    m.getFloorEntry(lo)));
        }

        // 升序类和降序类中定义的抽象方法
        // 这些将传递到适当的绝对版本

        abstract TreeMap.Entry<K,V> subLowest();
        abstract TreeMap.Entry<K,V> subHighest();
        abstract TreeMap.Entry<K,V> subCeiling(K key);
        abstract TreeMap.Entry<K,V> subHigher(K key);
        abstract TreeMap.Entry<K,V> subFloor(K key);
        abstract TreeMap.Entry<K,V> subLower(K key);

        /** 从这个子映射的角度返回升序迭代器 */
        abstract Iterator<K> keyIterator();

        abstract Spliterator<K> keySpliterator();

        /** 从该子映射的角度返回降序迭代器 */
        abstract Iterator<K> descendingKeyIterator();

        // public methods

        public boolean isEmpty() {
            return (fromStart && toEnd) ? m.isEmpty() : entrySet().isEmpty();
        }

        public int size() {
            return (fromStart && toEnd) ? m.size() : entrySet().size();
        }

        public final boolean containsKey(Object key) {
            return inRange(key) && m.containsKey(key);
        }

        public final V put(K key, V value) {
            if (!inRange(key))
                throw new IllegalArgumentException("key out of range");
            return m.put(key, value);
        }

        public final V get(Object key) {
            return !inRange(key) ? null :  m.get(key);
        }

        public final V remove(Object key) {
            return !inRange(key) ? null : m.remove(key);
        }

        public final Map.Entry<K,V> ceilingEntry(K key) {
            return exportEntry(subCeiling(key));
        }

        public final K ceilingKey(K key) {
            return keyOrNull(subCeiling(key));
        }

        public final Map.Entry<K,V> higherEntry(K key) {
            return exportEntry(subHigher(key));
        }

        public final K higherKey(K key) {
            return keyOrNull(subHigher(key));
        }

        public final Map.Entry<K,V> floorEntry(K key) {
            return exportEntry(subFloor(key));
        }

        public final K floorKey(K key) {
            return keyOrNull(subFloor(key));
        }

        public final Map.Entry<K,V> lowerEntry(K key) {
            return exportEntry(subLower(key));
        }

        public final K lowerKey(K key) {
            return keyOrNull(subLower(key));
        }

        public final K firstKey() {
            return key(subLowest());
        }

        public final K lastKey() {
            return key(subHighest());
        }

        public final Map.Entry<K,V> firstEntry() {
            return exportEntry(subLowest());
        }

        public final Map.Entry<K,V> lastEntry() {
            return exportEntry(subHighest());
        }

        public final Map.Entry<K,V> pollFirstEntry() {
            TreeMap.Entry<K,V> e = subLowest();
            Map.Entry<K,V> result = exportEntry(e);
            if (e != null)
                m.deleteEntry(e);
            return result;
        }

        public final Map.Entry<K,V> pollLastEntry() {
            TreeMap.Entry<K,V> e = subHighest();
            Map.Entry<K,V> result = exportEntry(e);
            if (e != null)
                m.deleteEntry(e);
            return result;
        }

        // 视图
        transient NavigableMap<K,V> descendingMapView;
        transient EntrySetView entrySetView;
        transient KeySet<K> navigableKeySetView;

        public final NavigableSet<K> navigableKeySet() {
            KeySet<K> nksv = navigableKeySetView;
            return (nksv != null) ? nksv :
                    (navigableKeySetView = new TreeMap.KeySet<>(this));
        }

        public final Set<K> keySet() {
            return navigableKeySet();
        }

        public NavigableSet<K> descendingKeySet() {
            return descendingMap().navigableKeySet();
        }

        public final SortedMap<K,V> subMap(K fromKey, K toKey) {
            return subMap(fromKey, true, toKey, false);
        }

        public final SortedMap<K,V> headMap(K toKey) {
            return headMap(toKey, false);
        }

        public final SortedMap<K,V> tailMap(K fromKey) {
            return tailMap(fromKey, true);
        }

        // View classes

        abstract class EntrySetView extends AbstractSet<Map.Entry<K,V>> {
            private transient int size = -1, sizeModCount;

            public int size() {
                if (fromStart && toEnd)
                    return m.size();
                if (size == -1 || sizeModCount != m.modCount) {
                    sizeModCount = m.modCount;
                    size = 0;
                    Iterator<?> i = iterator();
                    while (i.hasNext()) {
                        size++;
                        i.next();
                    }
                }
                return size;
            }

            public boolean isEmpty() {
                TreeMap.Entry<K,V> n = absLowest();
                return n == null || tooHigh(n.key);
            }

            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
                Object key = entry.getKey();
                if (!inRange(key))
                    return false;
                TreeMap.Entry<?,?> node = m.getEntry(key);
                return node != null &&
                        valEquals(node.getValue(), entry.getValue());
            }

            public boolean remove(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
                Object key = entry.getKey();
                if (!inRange(key))
                    return false;
                TreeMap.Entry<K,V> node = m.getEntry(key);
                if (node!=null && valEquals(node.getValue(),
                        entry.getValue())) {
                    m.deleteEntry(node);
                    return true;
                }
                return false;
            }
        }

        /**
         * 子映射的迭代器
         */
        abstract class SubMapIterator<T> implements Iterator<T> {
            TreeMap.Entry<K,V> lastReturned;
            TreeMap.Entry<K,V> next;
            final Object fenceKey;
            int expectedModCount;

            SubMapIterator(TreeMap.Entry<K,V> first,
                           TreeMap.Entry<K,V> fence) {
                expectedModCount = m.modCount;
                lastReturned = null;
                next = first;
                fenceKey = fence == null ? UNBOUNDED : fence.key;
            }

            public final boolean hasNext() {
                return next != null && next.key != fenceKey;
            }

            final TreeMap.Entry<K,V> nextEntry() {
                TreeMap.Entry<K,V> e = next;
                if (e == null || e.key == fenceKey)
                    throw new NoSuchElementException();
                if (m.modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                    throw new ConcurrentModificationException();
                next = successor(e);
                lastReturned = e;
                return e;
            }

            final TreeMap.Entry<K,V> prevEntry() {
                TreeMap.Entry<K,V> e = next;
                if (e == null || e.key == fenceKey)
                    throw new NoSuchElementException();
                if (m.modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                    throw new ConcurrentModificationException();
                next = predecessor(e);
                lastReturned = e;
                return e;
            }

            final void removeAscending() {
                if (lastReturned == null)
                    throw new IllegalStateException();
                if (m.modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                    throw new ConcurrentModificationException();
                // 删除的条目将由它们的后继条目替换
                if (lastReturned.left != null && lastReturned.right != null)
                    next = lastReturned;
                m.deleteEntry(lastReturned);
                lastReturned = null;
                expectedModCount = m.modCount;
            }

            final void removeDescending() {
                if (lastReturned == null)
                    throw new IllegalStateException();
                if (m.modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                    throw new ConcurrentModificationException();
                m.deleteEntry(lastReturned);
                lastReturned = null;
                expectedModCount = m.modCount;
            }

        }

        final class SubMapEntryIterator extends SubMapIterator<Map.Entry<K,V>> {
            SubMapEntryIterator(TreeMap.Entry<K,V> first,
                                TreeMap.Entry<K,V> fence) {
                super(first, fence);
            }
            public Map.Entry<K,V> next() {
                return nextEntry();
            }
            public void remove() {
                removeAscending();
            }
        }

        final class DescendingSubMapEntryIterator extends SubMapIterator<Map.Entry<K,V>> {
            DescendingSubMapEntryIterator(TreeMap.Entry<K,V> last,
                                          TreeMap.Entry<K,V> fence) {
                super(last, fence);
            }

            public Map.Entry<K,V> next() {
                return prevEntry();
            }
            public void remove() {
                removeDescending();
            }
        }

        // 最小Spliterator作为KeySpliterator备份
        final class SubMapKeyIterator extends SubMapIterator<K>
                implements Spliterator<K> {
            SubMapKeyIterator(TreeMap.Entry<K,V> first,
                              TreeMap.Entry<K,V> fence) {
                super(first, fence);
            }
            public K next() {
                return nextEntry().key;
            }
            public void remove() {
                removeAscending();
            }
            public Spliterator<K> trySplit() {
                return null;
            }
            public void forEachRemaining(Consumer<? super K> action) {
                while (hasNext())
                    action.accept(next());
            }
            public boolean tryAdvance(Consumer<? super K> action) {
                if (hasNext()) {
                    action.accept(next());
                    return true;
                }
                return false;
            }
            public long estimateSize() {
                return Long.MAX_VALUE;
            }
            public int characteristics() {
                return Spliterator.DISTINCT | Spliterator.ORDERED |
                        Spliterator.SORTED;
            }
            public final Comparator<? super K>  getComparator() {
                return NavigableSubMap.this.comparator();
            }
        }

        final class DescendingSubMapKeyIterator extends SubMapIterator<K>
                implements Spliterator<K> {
            DescendingSubMapKeyIterator(TreeMap.Entry<K,V> last,
                                        TreeMap.Entry<K,V> fence) {
                super(last, fence);
            }
            public K next() {
                return prevEntry().key;
            }
            public void remove() {
                removeDescending();
            }
            public Spliterator<K> trySplit() {
                return null;
            }
            public void forEachRemaining(Consumer<? super K> action) {
                while (hasNext())
                    action.accept(next());
            }
            public boolean tryAdvance(Consumer<? super K> action) {
                if (hasNext()) {
                    action.accept(next());
                    return true;
                }
                return false;
            }
            public long estimateSize() {
                return Long.MAX_VALUE;
            }
            public int characteristics() {
                return Spliterator.DISTINCT | Spliterator.ORDERED;
            }
        }
    }

    static final class AscendingSubMap<K,V> extends NavigableSubMap<K,V> {
        private static final long serialVersionUID = 912986545866124060L;

        AscendingSubMap(TreeMap<K,V> m,
                        boolean fromStart, K lo, boolean loInclusive,
                        boolean toEnd,     K hi, boolean hiInclusive) {
            super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
        }

        public Comparator<? super K> comparator() {
            return m.comparator();
        }

        public NavigableMap<K,V> subMap(K fromKey, boolean fromInclusive,
                                        K toKey,   boolean toInclusive) {
            if (!inRange(fromKey, fromInclusive))
                throw new IllegalArgumentException("fromKey out of range");
            if (!inRange(toKey, toInclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new AscendingSubMap<>(m,
                    false, fromKey, fromInclusive,
                    false, toKey,   toInclusive);
        }

        public NavigableMap<K,V> headMap(K toKey, boolean inclusive) {
            if (!inRange(toKey, inclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new AscendingSubMap<>(m,
                    fromStart, lo,    loInclusive,
                    false,     toKey, inclusive);
        }

        public NavigableMap<K,V> tailMap(K fromKey, boolean inclusive) {
            if (!inRange(fromKey, inclusive))
                throw new IllegalArgumentException("fromKey out of range");
            return new AscendingSubMap<>(m,
                    false, fromKey, inclusive,
                    toEnd, hi,      hiInclusive);
        }

        public NavigableMap<K,V> descendingMap() {
            NavigableMap<K,V> mv = descendingMapView;
            return (mv != null) ? mv :
                    (descendingMapView =
                            new DescendingSubMap<>(m,
                                    fromStart, lo, loInclusive,
                                    toEnd,     hi, hiInclusive));
        }

        Iterator<K> keyIterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        Spliterator<K> keySpliterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        Iterator<K> descendingKeyIterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        final class AscendingEntrySetView extends EntrySetView {
            public Iterator<Map.Entry<K,V>> iterator() {
                return new SubMapEntryIterator(absLowest(), absHighFence());
            }
        }

        public Set<Map.Entry<K,V>> entrySet() {
            EntrySetView es = entrySetView;
            return (es != null) ? es : (entrySetView = new AscendingEntrySetView());
        }

        TreeMap.Entry<K,V> subLowest()       { return absLowest(); }
        TreeMap.Entry<K,V> subHighest()      { return absHighest(); }
        TreeMap.Entry<K,V> subCeiling(K key) { return absCeiling(key); }
        TreeMap.Entry<K,V> subHigher(K key)  { return absHigher(key); }
        TreeMap.Entry<K,V> subFloor(K key)   { return absFloor(key); }
        TreeMap.Entry<K,V> subLower(K key)   { return absLower(key); }
    }

    static final class DescendingSubMap<K,V>  extends NavigableSubMap<K,V> {
        private static final long serialVersionUID = 912986545866120460L;
        DescendingSubMap(TreeMap<K,V> m,
                         boolean fromStart, K lo, boolean loInclusive,
                         boolean toEnd,     K hi, boolean hiInclusive) {
            super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
        }

        private final Comparator<? super K> reverseComparator =
                Collections.reverseOrder(m.comparator);

        public Comparator<? super K> comparator() {
            return reverseComparator;
        }

        public NavigableMap<K,V> subMap(K fromKey, boolean fromInclusive,
                                        K toKey,   boolean toInclusive) {
            if (!inRange(fromKey, fromInclusive))
                throw new IllegalArgumentException("fromKey out of range");
            if (!inRange(toKey, toInclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new DescendingSubMap<>(m,
                    false, toKey,   toInclusive,
                    false, fromKey, fromInclusive);
        }

        public NavigableMap<K,V> headMap(K toKey, boolean inclusive) {
            if (!inRange(toKey, inclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new DescendingSubMap<>(m,
                    false, toKey, inclusive,
                    toEnd, hi,    hiInclusive);
        }

        public NavigableMap<K,V> tailMap(K fromKey, boolean inclusive) {
            if (!inRange(fromKey, inclusive))
                throw new IllegalArgumentException("fromKey out of range");
            return new DescendingSubMap<>(m,
                    fromStart, lo, loInclusive,
                    false, fromKey, inclusive);
        }

        public NavigableMap<K,V> descendingMap() {
            NavigableMap<K,V> mv = descendingMapView;
            return (mv != null) ? mv :
                    (descendingMapView =
                            new AscendingSubMap<>(m,
                                    fromStart, lo, loInclusive,
                                    toEnd,     hi, hiInclusive));
        }

        Iterator<K> keyIterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        Spliterator<K> keySpliterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        Iterator<K> descendingKeyIterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        final class DescendingEntrySetView extends EntrySetView {
            public Iterator<Map.Entry<K,V>> iterator() {
                return new DescendingSubMapEntryIterator(absHighest(), absLowFence());
            }
        }

        public Set<Map.Entry<K,V>> entrySet() {
            EntrySetView es = entrySetView;
            return (es != null) ? es : (entrySetView = new DescendingEntrySetView());
        }

        TreeMap.Entry<K,V> subLowest()       { return absHighest(); }
        TreeMap.Entry<K,V> subHighest()      { return absLowest(); }
        TreeMap.Entry<K,V> subCeiling(K key) { return absFloor(key); }
        TreeMap.Entry<K,V> subHigher(K key)  { return absLower(key); }
        TreeMap.Entry<K,V> subFloor(K key)   { return absCeiling(key); }
        TreeMap.Entry<K,V> subLower(K key)   { return absHigher(key); }
    }

    /**
     * 这个类的存在仅仅是为了与以前不支持NavigableMap的TreeMap版本的序列化兼容性。
     * 它将一个旧版本的SubMap转换为新版本AscendingSubMap。
     * 这个类永远不会被使用。
     */
    private class SubMap extends AbstractMap<K,V>
            implements SortedMap<K,V>, java.io.Serializable {
        private static final long serialVersionUID = -6520786458950516097L;
        private boolean fromStart = false, toEnd = false;
        private K fromKey, toKey;
        private Object readResolve() {
            return new AscendingSubMap<>(TreeMap.this,
                    fromStart, fromKey, true,
                    toEnd, toKey, false);
        }
        public Set<Map.Entry<K,V>> entrySet() { throw new InternalError(); }
        public K lastKey() { throw new InternalError(); }
        public K firstKey() { throw new InternalError(); }
        public SortedMap<K,V> subMap(K fromKey, K toKey) { throw new InternalError(); }
        public SortedMap<K,V> headMap(K toKey) { throw new InternalError(); }
        public SortedMap<K,V> tailMap(K fromKey) { throw new InternalError(); }
        public Comparator<? super K> comparator() { throw new InternalError(); }
    }

    // 红黑力学
    private static final boolean RED   = false;
    private static final boolean BLACK = true;

    /**
     * 树中的节点。Doubles 用作将键值对传递回用户的方法(请参阅Map.Entry)。
     * @param <K>
     * @param <V>
     */
    static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;
        Entry<K,V> left;
        Entry<K,V> right;
        Entry<K,V> parent;
        boolean color = BLACK;

        /**
         * 使用给定的键、值和父元素创建一个新单元格，并使用空子链接和黑色。
         */
        Entry(K key, V value, Entry<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        /**
         * 返回键。
         *
         * @return 键
         */
        public K getKey() {
            return key;
        }

        /**
         * 返回与键关联的值。
         *
         * @return 与键关联的值
         */
        public V getValue() {
            return value;
        }

        /**
         * 用给定值替换当前与键关联的值。
         *
         * @return 调用此方法之前与键关联的值
         */
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;

            return valEquals(key,e.getKey()) && valEquals(value,e.getValue());
        }

        public int hashCode() {
            int keyHash = (key==null ? 0 : key.hashCode());
            int valueHash = (value==null ? 0 : value.hashCode());
            return keyHash ^ valueHash;
        }

        public String toString() {
            return key + "=" + value;
        }
    }

    /**
     * 返回树映射中的第一个条目(根据树映射的键排序函数)。如果树映射为空，则返回null。
     * @return 树映射中的第一个条目(根据树映射的键排序函数)
     */
    final Entry<K,V> getFirstEntry() {
        Entry<K,V> p = root;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }
    /**
     * 返回树映射中的最后一个条目(根据树映射的键排序函数)。如果树映射为空，则返回null。
     * @return 树映射中的最后一个条目(根据树映射的键排序函数)
     */
    final Entry<K,V> getLastEntry() {
        Entry<K,V> p = root;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }

    /**
     * 返回指定项的继承项，如果没有继承项，则返回null。
     * @param t	指定项
     * @return 指定项的继承项
     */
    static <K,V> TreeMap.Entry<K,V> successor(Entry<K,V> t) {
        if (t == null)
            return null;
        else if (t.right != null) {
            Entry<K,V> p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        } else {
            Entry<K,V> p = t.parent;
            Entry<K,V> ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * 返回指定项的前身，如果没有，则返回null。
     * @param t	指定项
     * @return 指定项的前身
     */
    static <K,V> Entry<K,V> predecessor(Entry<K,V> t) {
        if (t == null)
            return null;
        else if (t.left != null) {
            Entry<K,V> p = t.left;
            while (p.right != null)
                p = p.right;
            return p;
        } else {
            Entry<K,V> p = t.parent;
            Entry<K,V> ch = t;
            while (p != null && ch == p.left) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * 平衡操作。
     * 插入和删除期间的重新平衡实现与CLR版本略有不同。
     * 我们使用一组正确处理null的访问器，而不是使用伪nilnodes。
     * 在主要算法中，它们用于避免围绕空性(nullness)检查的混乱。
     */
    /**
     * 返回节点的颜色
     * @param p	节点
     * @return 节点颜色
     */
    private static <K,V> boolean colorOf(Entry<K,V> p) {
        return (p == null ? BLACK : p.color);
    }

    /**
     * 返回当前节点的父节点
     * @param p	当前节点
     * @return 	父节点
     */
    private static <K,V> Entry<K,V> parentOf(Entry<K,V> p) {
        return (p == null ? null: p.parent);
    }

    /**
     * 为该节点添色
     * @param p	节点
     * @param c 颜色
     */
    private static <K,V> void setColor(Entry<K,V> p, boolean c) {
        if (p != null)
            p.color = c;
    }

    /**
     * 返回左子节点
     * @param p	节点
     * @return 左子节点
     */
    private static <K,V> Entry<K,V> leftOf(Entry<K,V> p) {
        return (p == null) ? null: p.left;
    }

    /**
     * 返回右子节点
     * @param p	节点
     * @return 	右子节点
     */
    private static <K,V> Entry<K,V> rightOf(Entry<K,V> p) {
        return (p == null) ? null: p.right;
    }

    /** From CLR */
    /**
     * 向左旋转节点
     * P为父节点，S为孩子节点。左旋操作后，S节点代替P节点的位置，
     * P节点成为S节点的左孩子，S节点的左孩子成为P节点的右孩子。
     * @param p 节点
     */
    private void rotateLeft(Entry<K,V> p) {
        if (p != null) {
            Entry<K,V> r = p.right; // 右子节点
            p.right = r.left;	// p节点的右子节点为r的左子节点
            if (r.left != null) // r左子节点非空，r左子节点的父节点为p节点
                r.left.parent = p;
            r.parent = p.parent;	// r父节点等于p父节点
            if (p.parent == null)	// p父节点为空，根节点等于r节点
                root = r;
            else if (p.parent.left == p)	// p父节点的左子节点等于p节点，p父节点的左子节点等于r节点
                p.parent.left = r;
            else
                p.parent.right = r;	//	p的父节点的右子节点等于r节点
            r.left = p;	// r的左子节点等于p
            p.parent = r;	// p的父节点等于r
        }
    }

    /** From CLR */
    /**
     * 向右旋转节点。
     * P为父节点，l为孩子节点。右旋操作后，l节点代替P节点的位置，
     * P节点成为l节点的右孩子，l节点的右孩子成为P节点的左孩子。
     * @param p 节点
     */
    private void rotateRight(Entry<K,V> p) {
        if (p != null) {
            Entry<K,V> l = p.left;	// 左子节点
            p.left = l.right;	// p的左子节点等于l的右子节点
            if (l.right != null) l.right.parent = p;	// l的右子节点非空，l的右子节点的父节点为p
            l.parent = p.parent;	// l的父节点等于p的父节点
            if (p.parent == null)	// p的父节点为空，根节点等于l节点
                root = l;
            else if (p.parent.right == p)	// p的父节点的右子节点等于p节点，p的父节点的右子节点等于l
                p.parent.right = l;	// p的父节点的右子节点等于l
            else p.parent.left = l;
            l.right = p;	// l的右子节点等于p
            p.parent = l;	// p的父节点等于l
        }
    }

    /** From CLR */
    /**
     * 修复后插入
     * 新插入节点为红色
     * 一、父节点是黑色，直接插入不需要重构
     * 二、父节点是红色 （基准节点不是根节点）
     * @param x	需要插入的节点
     */
    private void fixAfterInsertion(Entry<K,V> x) {
        x.color = RED;	// x节点为红色

        while (x != null && x != root && x.parent.color == RED) {	// x节点非空且x不是根节点且x的父节点的颜色为红色
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {	// 父节点是祖父节点的左子节点
                Entry<K,V> y = rightOf(parentOf(parentOf(x)));	// y等于父节点的右子节点
                if (colorOf(y) == RED) {	// y为红色
                    setColor(parentOf(x), BLACK);	// 父节点为黑色
                    setColor(y, BLACK);	// y为黑色
                    setColor(parentOf(parentOf(x)), RED); // 祖父节点为红色
                    x = parentOf(parentOf(x));	// x为祖父节点
                } else { // y为黑色
                    if (x == rightOf(parentOf(x))) { // x等于父节点的右子节点
                        x = parentOf(x);	// x等于父节点
                        rotateLeft(x);	// x节点进行左旋操作
                    }
                    setColor(parentOf(x), BLACK);	// 父节点为黑色
                    setColor(parentOf(parentOf(x)), RED);	// 祖父为红色
                    rotateRight(parentOf(parentOf(x)));	// 祖父节点进行右旋操作
                }
            } else {	// 父节点是祖父节点的右子节点
                Entry<K,V> y = leftOf(parentOf(parentOf(x)));	// y等于祖父节点的左子节点
                if (colorOf(y) == RED) {	// y为红色
                    setColor(parentOf(x), BLACK);	// 父节点为黑色
                    setColor(y, BLACK);	// y为黑色
                    setColor(parentOf(parentOf(x)), RED);	// 祖父节点为红色
                    x = parentOf(parentOf(x));	// x为祖父节点
                } else {	// y为黑色
                    if (x == leftOf(parentOf(x))) {	// x为父节点的左子节点
                        x = parentOf(x);	// x为父节点
                        rotateRight(x);	// x节点进行优选操作
                    }
                    setColor(parentOf(x), BLACK);	// 父节点为黑色
                    setColor(parentOf(parentOf(x)), RED);	// 祖父节点为红色
                    rotateLeft(parentOf(parentOf(x)));	// 祖父节点进行右旋操作
                }
            }
        }
        root.color = BLACK;	// 根节点为黑色
    }

    /**
     * 删除节点p，然后重新平衡树。
     * @param p 需要删除的节点
     */
    private void deleteEntry(Entry<K,V> p) {
        modCount++;
        size--;

        // 如果严格内部，将后继元素复制到p，然后让p指向后继元素。
        if (p.left != null && p.right != null) {
            Entry<K,V> s = successor(p);
            p.key = s.key;
            p.value = s.value;
            p = s;
        } // p节点有两个子结点

        // 开始修正替换节点,如果它存在。
        Entry<K,V> replacement = (p.left != null ? p.left : p.right);

        if (replacement != null) {
            // 链接替换到父链接
            replacement.parent = p.parent;
            if (p.parent == null)
                root = replacement;
            else if (p == p.parent.left)
                p.parent.left  = replacement;
            else
                p.parent.right = replacement;

            // 空出链接，以便fixAfterDeletion可以使用它们。
            p.left = p.right = p.parent = null;

            // 修复更换
            if (p.color == BLACK)
                fixAfterDeletion(replacement);
        } else if (p.parent == null) { // 如果我们是唯一的节点，返回。
            root = null;
        } else { //  没有孩子。使用self作为幻影替换和解除链接。
            if (p.color == BLACK)
                fixAfterDeletion(p);

            if (p.parent != null) {
                if (p == p.parent.left)
                    p.parent.left = null;
                else if (p == p.parent.right)
                    p.parent.right = null;
                p.parent = null;
            }
        }
    }

    /** From CLR */
    /**
     * 修复更换
     * @param x	需要更换的节点
     */
    private void fixAfterDeletion(Entry<K,V> x) {
        while (x != root && colorOf(x) == BLACK) {	// x非根节点且x节点为黑色
            if (x == leftOf(parentOf(x))) {	// x节点为父节点的左子节点
                Entry<K,V> sib = rightOf(parentOf(x));	// sib为父节点的右子节点

                if (colorOf(sib) == RED) {	// sib为红色
                    setColor(sib, BLACK);	// sib为黑色
                    setColor(parentOf(x), RED);	// 父节点为红色
                    rotateLeft(parentOf(x));	// 父节点左旋处理
                    sib = rightOf(parentOf(x));	// sib为父节点的右子节点
                }

                if (colorOf(leftOf(sib))  == BLACK &&
                        colorOf(rightOf(sib)) == BLACK) {	// sib左子节点为黑色&&sib右子节点为黑色
                    setColor(sib, RED);	// sib为红色
                    x = parentOf(x);	// x为父节点
                } else {
                    if (colorOf(rightOf(sib)) == BLACK) {	// sib右子节点为黑色
                        setColor(leftOf(sib), BLACK);	// sib的左子节点为黑色
                        setColor(sib, RED);	// sib为红色
                        rotateRight(sib);	// sib节点进行右旋处理
                        sib = rightOf(parentOf(x));	// sib为父节点的右子节点
                    }
                    setColor(sib, colorOf(parentOf(x)));	// sib节点的颜色等于父节点的颜色
                    setColor(parentOf(x), BLACK);		// 父节点为黑色
                    setColor(rightOf(sib), BLACK);	// sib右子节点为黑色
                    rotateLeft(parentOf(x));	// 父节点进行左旋
                    x = root;	// x为根节点
                }
            } else { // 对称的
                Entry<K,V> sib = leftOf(parentOf(x));	// sib为父节点的左子节点

                if (colorOf(sib) == RED) {	// sib节点为红色
                    setColor(sib, BLACK);	// sib节点颜色等于黑色
                    setColor(parentOf(x), RED);	// 父节点为红色
                    rotateRight(parentOf(x));	// 父节点进行右旋处理
                    sib = leftOf(parentOf(x));	// sib为父节点的左子节点
                }

                if (colorOf(rightOf(sib)) == BLACK &&
                        colorOf(leftOf(sib)) == BLACK) {	// sib的右子节点为黑色&&sib左子节点为黑色
                    setColor(sib, RED);	// sib为红色
                    x = parentOf(x);	// x为父节点
                } else {
                    if (colorOf(leftOf(sib)) == BLACK) {	// sib左子节点为黑色
                        setColor(rightOf(sib), BLACK);	// sib的右子节点为黑色
                        setColor(sib, RED);	// sib为红色
                        rotateLeft(sib);	// sib进行左旋
                        sib = leftOf(parentOf(x));	// sib为父节点的左子节点
                    }
                    setColor(sib, colorOf(parentOf(x)));	//sib颜色等于父节点的颜色
                    setColor(parentOf(x), BLACK);	// 父节点为黑色
                    setColor(leftOf(sib), BLACK);	// sib左子节点为黑色
                    rotateRight(parentOf(x));	// 父节点进行右旋处理
                    x = root;	// x为根节点
                }
            }
        }

        setColor(x, BLACK);	// x为黑色
    }

    private static final long serialVersionUID = 919286545866124006L;

    /**
     * 将TreeMap实例的状态保存到流中(即,序列化)。
     * @serialData	TreeMap的size(键值映射的数量)被发出(int)，
     * 然后是由TreeMap表示的每个键值映射的键(对象)和值(对象)。
     * 键值映射按键顺序发出(由树映射的比较器决定，如果树映射没有比较器，则由键的自然顺序决定)。
     *
     * @param s
     */
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        // 写出比较器和任何隐藏的东西
        s.defaultWriteObject();

        // 写出大小(映射的数量)
        s.writeInt(size);

        // 写出键和值(交替)
        for (Iterator<Map.Entry<K,V>> i = entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<K,V> e = i.next();
            s.writeObject(e.getKey());
            s.writeObject(e.getValue());
        }
    }

    /**
     * 从流(即,反序列化)。
     * @param s
     */
    private void readObject(final java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // 读取比较器和任何隐藏的东西
        s.defaultReadObject();

        // 读取大小
        int size = s.readInt();

        buildFromSorted(size, null, s, null);
    }

    /**
     * 只打算从TreeSet.readObject调用
     * @param size
     * @param s
     * @param defaultVal
     */
    void readTreeSet(int size, java.io.ObjectInputStream s, V defaultVal)
            throws java.io.IOException, ClassNotFoundException {
        buildFromSorted(size, null, s, defaultVal);
    }

    /**
     * 只打算从TreeSet.addAll调用
     * @param set
     * @param defaultVal
     */
    void addAllForTreeSet(SortedSet<? extends K> set, V defaultVal) {
        try {
            buildFromSorted(set.size(), set.iterator(), null, defaultVal);
        } catch (java.io.IOException cannotHappen) {
        } catch (ClassNotFoundException cannotHappen) {
        }
    }

    /**
     * 基于排序数据的线性时间树构建算法。
     * 可以接受来自迭代器或流的键和/或值。这导致了太多的参数，但似乎比其他选择更好。
     * 该方法接受的四种格式是:
     * 	  1) Map.Entries的迭代器。    (it != null, defaultVal == null).
     *    2) 键的迭代器。        	    (it != null, defaultVal != null).
     *    3) 一串交替序列化的键和值。(it == null, defaultVal == null).
     *    4) 一连串的序列化的键.     (it == null, defaultVal != null).
     * 假设在调用此方法之前已经设置了TreeMap的比较器。
     * @param size	从迭代器或流中读取的键数(或键值对)
     * @param it	如果非空，则从该迭代器读取的条目或键创建新条目。
     * @param str	如果非空，则从键创建新条目，并可能以序列化形式从该流读取值。
     * 				其中一个和str应该是非空的。
     * @param defaultVal	如果非null，则此默认值用于映射中的每个值。
     * 						如果为空，则从迭代器或流读取每个值，如上所述。
     */
    private void buildFromSorted(int size, Iterator<?> it,
                                 java.io.ObjectInputStream str,
                                 V defaultVal)
            throws  java.io.IOException, ClassNotFoundException {
        this.size = size;
        root = buildFromSorted(0, 0, size-1, computeRedLevel(size),
                it, str, defaultVal);
    }

    /**
     * 递归“helper方法”，它执行前一个方法的实际工作。同名参数具有相同的定义。
     * 下面记录了其他参数。假设在调用此方法之前已经设置了树映射的比较器和大小字段。
     * (它忽略了这两个字段。)
     * @param level	当前树的级别。初始调用应该是0。
     * @param lo	这个子树的第一个元素索引。初始值应该是0。
     * @param hi	这个子树的最后一个元素索引。初始值应为size-1。
     * @param redLevel	节点应该是红色的级别。对于这种大小的树，必须与计算机级别相同。
     * @param it	如果非空，则从该迭代器读取的条目或键创建新条目。
     * @param str	如果非空，则从键创建新条目，并可能以序列化形式从该流读取值。
     * 				其中一个和str应该是非空的。
     * @param defaultVal	如果非null，则此默认值用于映射中的每个值。
     * 						如果为空，则从迭代器或流读取每个值，如上所述。
     * @return
     */
    @SuppressWarnings("unchecked")
    private final Entry<K,V> buildFromSorted(int level, int lo, int hi,
                                             int redLevel,
                                             Iterator<?> it,
                                             java.io.ObjectInputStream str,
                                             V defaultVal)
            throws  java.io.IOException, ClassNotFoundException {
        /*
         * 策略:根是middlemost元素。
         * 为了得到它，我们必须先递归地构造整个左子树，以便获取它的所有元素。
         * 然后我们可以继续使用右子树。
         *
         * lo和hi参数是当前子树从迭代器或流中提取的最小和最大索引。
         * 它们实际上没有索引，我们只是按顺序进行，确保按相应的顺序提取项。
         */
        /*
         * 计算索引
         */
        if (hi < lo) return null;  // 最后一个元素索引小于第一个元素索引

        int mid = (lo + hi) >>> 1; // 取中间的索引值，即父节点的索引值

        Entry<K,V> left  = null;
        if (lo < mid)	// 第一个元素索引小于中国索引值
            left = buildFromSorted(level+1, lo, mid - 1, redLevel,
                    it, str, defaultVal); // 左节点处理

        /*
         * 从迭代器或流中提取键和/或值
         */
        K key;
        V value;
        if (it != null) {	// 迭代器非空，从迭代器中读取键
            if (defaultVal==null) {	// 默认值为空
                Map.Entry<?,?> entry = (Map.Entry<?,?>)it.next();
                key = (K)entry.getKey();
                value = (V)entry.getValue();
            } else {	// 默认值非空
                key = (K)it.next();
                value = defaultVal;
            }
        } else { // use stream 迭代器为空，从流中读取键
            key = (K) str.readObject();
            value = (defaultVal != null ? defaultVal : (V) str.readObject());
        }
        /*
         * 创建条目
         */
        Entry<K,V> middle =  new Entry<>(key, value, null);

        // 非完全底部水平红色的颜色节点
        if (level == redLevel)	// 当前树的级别等于红色节点，中间索引节点为红色
            middle.color = RED;

        if (left != null) {	// 左节点非空，中间节点的左子节点为left
            middle.left = left;
            left.parent = middle;
        }

        if (mid < hi) {	// 中间索引值小于最后一个元素索引
            Entry<K,V> right = buildFromSorted(level+1, mid+1, hi, redLevel,
                    it, str, defaultVal); // 右节点处理
            middle.right = right;	// 中间节点的右子节点是right
            right.parent = middle;
        }

        return middle;	// 返回中间节点，即构造的条目
    }

    /**
     * 找到将所有节点分配为黑色的级别。
     * 这是buildTree生成的完整二叉树的最后一个“完整”级别。
     * 其余的节点是红色的。(这就构成了一组“漂亮的”颜色分配，用于将来的插入。)
     * 这个级别数是通过查找到达零点节点所需的分划数来计算的。
     * (答案是~lg(N)，但在任何情况下都必须通过相同的快速O(lg(N))循环来计算。)
     * @param sz
     * @return
     */
    private static int computeRedLevel(int sz) {
        int level = 0;
        for (int m = sz - 1; m >= 0; m = m / 2 - 1)
            level++;
        return level;
    }

    /**
     * 目前，我们只支持基于spliterator的完整映射版本(以降序形式)，
     * 否则依赖于默认值，因为子映射的大小估计将控制成本。
     * 检查关键视图所需的类型测试不是很好，但是可以避免破坏现有的类结构。
     * 如果返回null，调用者必须使用普通的默认spliterators。
     * @param m
     * @return 键的spliterator
     */
    static <K> Spliterator<K> keySpliteratorFor(NavigableMap<K,?> m) {
        if (m instanceof TreeMap) { // 如果是	TreeMap，返回TreeMap.keySpliterator
            @SuppressWarnings("unchecked")
            TreeMap<K,Object> t = (TreeMap<K,Object>) m;
            return t.keySpliterator();
        }
        if (m instanceof DescendingSubMap) { // 如果是DescendingSubMap，返回DescendingSubMap.keySpliterator
            @SuppressWarnings("unchecked")
            DescendingSubMap<K,?> dm = (DescendingSubMap<K,?>) m;
            TreeMap<K,?> tm = dm.m;
            if (dm == tm.descendingMap) {
                @SuppressWarnings("unchecked")
                TreeMap<K,Object> t = (TreeMap<K,Object>) tm;
                return t.descendingKeySpliterator();
            }
        }
        @SuppressWarnings("unchecked") // 否则返回NavigableSubMap.keySpliterator
                NavigableSubMap<K,?> sm = (NavigableSubMap<K,?>) m;
        return sm.keySpliterator();
    }

    /**
     * 返回键的Spliterator
     * @return	键的Spliterator
     */
    final Spliterator<K> keySpliterator() {
        return new KeySpliterator<K,V>(this, null, null, 0, -1, 0);
    }

    /**
     * 返回键的降序Spliterator
     * @return	键的降序Spliterator
     */
    final Spliterator<K> descendingKeySpliterator() {
        return new DescendingKeySpliterator<K,V>(this, null, null, 0, -2, 0);
    }

    /**
     * spliterators的基类。
     * 迭代从一个给定的原点开始，一直到但不包括一个给定的fence(或null for end)。
     * 在顶层，对于升序情况，第一个分割使用根作为左栅栏/右原点。
     * 从这里开始，右拆分用它的左子元素替换当前的栅栏，同时也充当了spli -off spliterator的原点。
     * 左手是对称的。降序版本将原点放在末尾，并反转升序拆分规则。
     * 这个基类对于方向性，或者顶级的spliterator是否覆盖整个树，都是非关键的。
     * 这意味着实际的分割机制位于子类中。
     * 一些子类trySplit方法是相同的(除了返回类型)，但是不能很好地分解。
     *
     * 目前，子类版本只存在于完整的映射中(包括通过其后代映射的降序键)。
     * 其他方法是可能的，但目前不值得使用，因为子映射需要O(n)计算来确定大小，
     * 这在很大程度上限制了使用自定义spliterator与默认机制的潜在加速。
     *
     * 为了启动初始化，外部构造函数使用负大小估计值:-1表示上升，-2表示下降。
     * @param <K>
     * @param <V>
     */
    static class TreeMapSpliterator<K,V> {
        final TreeMap<K,V> tree;
        TreeMap.Entry<K,V> current; // traverser; 初始范围内的第一个节点
        TreeMap.Entry<K,V> fence;   // 最后一个过去的，或null
        int side;                   // 0: top, -1: is a left split, +1: right
        int est;                    // 大小估计(只适用于顶层)
        int expectedModCount;       // for CME checks

        TreeMapSpliterator(TreeMap<K,V> tree,
                           TreeMap.Entry<K,V> origin, TreeMap.Entry<K,V> fence,
                           int side, int est, int expectedModCount) {
            this.tree = tree;
            this.current = origin;
            this.fence = fence;
            this.side = side;
            this.est = est;
            this.expectedModCount = expectedModCount;
        }

        /**
         * 当前映射的条目数，默认为0
         * @return
         */
        final int getEstimate() { // force initialization
            int s; TreeMap<K,V> t;
            if ((s = est) < 0) {
                if ((t = tree) != null) {
                    current = (s == -1) ? t.getFirstEntry() : t.getLastEntry(); // 获取条目
                    s = est = t.size;	// tree中的条目数
                    expectedModCount = t.modCount;
                }
                else
                    s = est = 0;
            }
            return s;
        }
        /**
         * 返回当前映射的条目数，默认为0
         * @return 当前映射的条目数
         */
        public final long estimateSize() {
            return (long)getEstimate();
        }
    }

    static final class KeySpliterator<K,V>
            extends TreeMapSpliterator<K,V>
            implements Spliterator<K> {
        KeySpliterator(TreeMap<K,V> tree,
                       TreeMap.Entry<K,V> origin, TreeMap.Entry<K,V> fence,
                       int side, int est, int expectedModCount) {
            super(tree, origin, fence, side, est, expectedModCount);
        }
        /**
         * 如果这个spliterator可以被分区，那么返回一个包含元素的spliterator，
         * 当这个方法返回时，这个spliterator将不包含元素。
         *
         * 如果这个Spliterator是有序的，返回的Spliterator必须包含元素的严格前缀。
         *
         * 除非这个Spliterator包含无穷多个元素，否则对trySplit()的重复调用最终必须返回null。
         * 在非空返回:
         * 1、在分割之前为estimateSize()报告的值，在分割之后，
         * 必须大于或等于该值和返回的Spliterator的estimateSize();
         * 2、如果这个Spliterator消失了，那么这个Spliterator在分裂前的estimateSize()
         * 必须等于这个Spliterator和分裂后返回的Spliterator的estimateSize()的和。
         *
         * 这种方法可能会因为任何原因返回null，包括空、遍历开始后无法分割、数据结构约束和效率考虑。
         *
         * @apiNote
         * 一个理想的trySplit方法有效地(不需要遍历)将其元素精确地分成两半，允许平衡的并行计算。
         * 许多偏离这一理想的做法仍然非常有效;例如，只近似地拆分一个近似平衡的树，
         * 或者对于叶子节点可能包含一个或两个元素的树，不能进一步拆分这些节点。
         * 然而，平衡上的较大偏差和/或效率过低的trySplit机制通常会导致较差的并行性能。
         * 覆写：Spliterator<T> 中的 trySplit
         * @return 一个包含部分元素的Spliterator，
         * 			如果这个Spliterator不能被分割，则为null
         */
        public KeySpliterator<K,V> trySplit() {
            if (est < 0)
                getEstimate(); // force initialization
            int d = side;
            TreeMap.Entry<K,V> e = current, f = fence,
                    s = ((e == null || e == f) ? null :      // empty
                            (d == 0)              ? tree.root : // was top
                                    (d >  0)              ? e.right :   // was right
                                            (d <  0 && f != null) ? f.left :    // was left
                                                    null);
            if (s != null && s != e && s != f &&
                    tree.compare(e.key, s.key) < 0) {        // e not already past s
                side = 1;
                return new KeySpliterator<>
                        (tree, e, current = s, -1, est >>>= 1, expectedModCount);
            }
            return null;
        }
        /**
         * 为当前线程中的每个剩余元素依次执行给定的操作，直到处理完所有元素或操作引发异常为止。
         * 如果这个Spliterator是有序的，则按相遇顺序执行操作。该操作引发的异常将传递给调用者。
         * 覆写：Spliterator<T> 中的 forEachRemaining
         * @param action 动作
         */
        public void forEachRemaining(Consumer<? super K> action) {
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            TreeMap.Entry<K,V> f = fence, e, p, pl;
            if ((e = current) != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e.key);
                    if ((p = e.right) != null) {
                        while ((pl = p.left) != null)
                            p = pl;
                    }
                    else {
                        while ((p = e.parent) != null && e == p.right)
                            e = p;
                    }
                } while ((e = p) != null && e != f);
                if (tree.modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                    throw new ConcurrentModificationException();
            }
        }
        /**
         * 如果剩余元素存在，则对其执行给定的操作，返回true;否则返回false。
         * 如果这个Spliterator是有序的，那么将按相遇顺序对下一个元素执行操作。
         * 该操作引发的异常将传递给调用者。
         * 覆写：Spliterator<T> 中的 tryAdvance
         * @param action	动作
         * @return 如果在进入此方法时不存在其他元素，则为false，否则为true。
         */
        public boolean tryAdvance(Consumer<? super K> action) {
            TreeMap.Entry<K,V> e;
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            if ((e = current) == null || e == fence)
                return false;
            current = successor(e);
            action.accept(e.key);
            if (tree.modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                throw new ConcurrentModificationException();
            return true;
        }
        /**
         * 返回该Spliterator及其元素的一组特征。结果被表示为来自
         * ORDERED, DISTINCT, SORTED, SIZED,NONNULL, IMMUTABLE, CONCURRENT,SUBSIZED。
         * 在调用trySplit之前或之间，对给定spliterator上的
         * characters()的重复调用应该总是返回相同的结果。
         *
         * 如果Spliterator报告一组不一致的特征(从单个调用或跨多个调用返回的特征)，
         * 则不能保证使用该Spliterator进行任何计算。
         * 覆写：Spliterator<T> 中的 characteristics
         * @return 特征的表示
         */
        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) |
                    Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.ORDERED;
        }

        /**
         * 如果这个Spliterator的源代码是由比较器排序的，则返回该比较器。
         * 如果源按可比较的自然顺序排序，则返回null。
         * 否则，如果源没有排序，则抛出IllegalStateException。
         * 覆写：Spliterator<T> 中的 getComparator
         * @return 一个比较器，如果元素按自然顺序排序，则为空。
         */
        public final Comparator<? super K>  getComparator() {
            return tree.comparator;
        }

    }

    static final class DescendingKeySpliterator<K,V>
            extends TreeMapSpliterator<K,V>
            implements Spliterator<K> {
        DescendingKeySpliterator(TreeMap<K,V> tree,
                                 TreeMap.Entry<K,V> origin, TreeMap.Entry<K,V> fence,
                                 int side, int est, int expectedModCount) {
            super(tree, origin, fence, side, est, expectedModCount);
        }
        /**
         * 如果这个spliterator可以被分区，那么返回一个包含元素的spliterator，
         * 当这个方法返回时，这个spliterator将不包含元素。
         *
         * 如果这个Spliterator是有序的，返回的Spliterator必须包含元素的严格前缀。
         *
         * 除非这个Spliterator包含无穷多个元素，否则对trySplit()的重复调用最终必须返回null。
         * 在非空返回:
         * 1、在分割之前为estimateSize()报告的值，在分割之后，
         * 必须大于或等于该值和返回的Spliterator的estimateSize();
         * 2、如果这个Spliterator消失了，那么这个Spliterator在分裂前的estimateSize()
         * 必须等于这个Spliterator和分裂后返回的Spliterator的estimateSize()的和。
         *
         * 这种方法可能会因为任何原因返回null，包括空、遍历开始后无法分割、数据结构约束和效率考虑。
         *
         * @apiNote
         * 一个理想的trySplit方法有效地(不需要遍历)将其元素精确地分成两半，允许平衡的并行计算。
         * 许多偏离这一理想的做法仍然非常有效;例如，只近似地拆分一个近似平衡的树，
         * 或者对于叶子节点可能包含一个或两个元素的树，不能进一步拆分这些节点。
         * 然而，平衡上的较大偏差和/或效率过低的trySplit机制通常会导致较差的并行性能。
         * 覆写：Spliterator<T> 中的 trySplit
         * @return 一个包含部分元素的Spliterator，
         * 			如果这个Spliterator不能被分割，则为null
         */
        public DescendingKeySpliterator<K,V> trySplit() {
            if (est < 0)
                getEstimate(); // force initialization
            int d = side;
            TreeMap.Entry<K,V> e = current, f = fence,
                    s = ((e == null || e == f) ? null :      // empty
                            (d == 0)              ? tree.root : // was top
                                    (d <  0)              ? e.left :    // was left
                                            (d >  0 && f != null) ? f.right :   // was right
                                                    null);
            if (s != null && s != e && s != f &&
                    tree.compare(e.key, s.key) > 0) {       // e not already past s
                side = 1;
                return new DescendingKeySpliterator<>
                        (tree, e, current = s, -1, est >>>= 1, expectedModCount);
            }
            return null;
        }
        /**
         * 为当前线程中的每个剩余元素依次执行给定的操作，直到处理完所有元素或操作引发异常为止。
         * 如果这个Spliterator是有序的，则按相遇顺序执行操作。该操作引发的异常将传递给调用者。
         * 覆写：Spliterator<T> 中的 forEachRemaining
         * @param action 动作
         */
        public void forEachRemaining(Consumer<? super K> action) {
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            TreeMap.Entry<K,V> f = fence, e, p, pr;
            if ((e = current) != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e.key);
                    if ((p = e.left) != null) {
                        while ((pr = p.right) != null)
                            p = pr;
                    }
                    else {
                        while ((p = e.parent) != null && e == p.left)
                            e = p;
                    }
                } while ((e = p) != null && e != f);
                if (tree.modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                    throw new ConcurrentModificationException();
            }
        }
        /**
         * 如果剩余元素存在，则对其执行给定的操作，返回true;否则返回false。
         * 如果这个Spliterator是有序的，那么将按相遇顺序对下一个元素执行操作。
         * 该操作引发的异常将传递给调用者。
         * 覆写：Spliterator<T> 中的 tryAdvance
         * @param action	动作
         * @return 如果在进入此方法时不存在其他元素，则为false，否则为true。
         */
        public boolean tryAdvance(Consumer<? super K> action) {
            TreeMap.Entry<K,V> e;
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            if ((e = current) == null || e == fence)
                return false;
            current = predecessor(e);
            action.accept(e.key);
            if (tree.modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                throw new ConcurrentModificationException();
            return true;
        }
        /**
         * 返回该Spliterator及其元素的一组特征。结果被表示为来自
         * ORDERED, DISTINCT, SORTED, SIZED,NONNULL, IMMUTABLE, CONCURRENT,SUBSIZED。
         * 在调用trySplit之前或之间，对给定spliterator上的
         * characters()的重复调用应该总是返回相同的结果。
         *
         * 如果Spliterator报告一组不一致的特征(从单个调用或跨多个调用返回的特征)，
         * 则不能保证使用该Spliterator进行任何计算。
         * 覆写：Spliterator<T> 中的 characteristics
         * @return 特征的表示
         */
        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) |
                    Spliterator.DISTINCT | Spliterator.ORDERED;
        }
    }

    static final class ValueSpliterator<K,V>
            extends TreeMapSpliterator<K,V>
            implements Spliterator<V> {
        ValueSpliterzator(TreeMap<K,V> tree,
                          TreeMap.Entry<K,V> origin, TreeMap.Entry<K,V> fence,
                          int side, int est, int expectedModCount) {
            super(tree, origin, fence, side, est, expectedModCount);
        }
        /**
         * 如果这个spliterator可以被分区，那么返回一个包含元素的spliterator，
         * 当这个方法返回时，这个spliterator将不包含元素。
         *
         * 如果这个Spliterator是有序的，返回的Spliterator必须包含元素的严格前缀。
         *
         * 除非这个Spliterator包含无穷多个元素，否则对trySplit()的重复调用最终必须返回null。
         * 在非空返回:
         * 1、在分割之前为estimateSize()报告的值，在分割之后，
         * 必须大于或等于该值和返回的Spliterator的estimateSize();
         * 2、如果这个Spliterator消失了，那么这个Spliterator在分裂前的estimateSize()
         * 必须等于这个Spliterator和分裂后返回的Spliterator的estimateSize()的和。
         *
         * 这种方法可能会因为任何原因返回null，包括空、遍历开始后无法分割、数据结构约束和效率考虑。
         *
         * @apiNote
         * 一个理想的trySplit方法有效地(不需要遍历)将其元素精确地分成两半，允许平衡的并行计算。
         * 许多偏离这一理想的做法仍然非常有效;例如，只近似地拆分一个近似平衡的树，
         * 或者对于叶子节点可能包含一个或两个元素的树，不能进一步拆分这些节点。
         * 然而，平衡上的较大偏差和/或效率过低的trySplit机制通常会导致较差的并行性能。
         * 覆写：Spliterator<T> 中的 trySplit
         * @return 一个包含部分元素的Spliterator，
         * 			如果这个Spliterator不能被分割，则为null
         */
        public ValueSpliterator<K,V> trySplit() {
            if (est < 0)
                getEstimate(); // force initialization
            int d = side;
            TreeMap.Entry<K,V> e = current, f = fence,
                    s = ((e == null || e == f) ? null :      // empty
                            (d == 0)              ? tree.root : // was top
                                    (d >  0)              ? e.right :   // was right
                                            (d <  0 && f != null) ? f.left :    // was left
                                                    null);
            if (s != null && s != e && s != f &&
                    tree.compare(e.key, s.key) < 0) {        // e not already past s
                side = 1;
                return new ValueSpliterator<>
                        (tree, e, current = s, -1, est >>>= 1, expectedModCount);
            }
            return null;
        }
        /**
         * 为当前线程中的每个剩余元素依次执行给定的操作，直到处理完所有元素或操作引发异常为止。
         * 如果这个Spliterator是有序的，则按相遇顺序执行操作。该操作引发的异常将传递给调用者。
         * 覆写：Spliterator<T> 中的 forEachRemaining
         * @param action 动作
         */
        public void forEachRemaining(Consumer<? super V> action) {
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            TreeMap.Entry<K,V> f = fence, e, p, pl;
            if ((e = current) != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e.value);
                    if ((p = e.right) != null) {
                        while ((pl = p.left) != null)
                            p = pl;
                    }
                    else {
                        while ((p = e.parent) != null && e == p.right)
                            e = p;
                    }
                } while ((e = p) != null && e != f);
                if (tree.modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                    throw new ConcurrentModificationException();
            }
        }
        /**
         * 如果剩余元素存在，则对其执行给定的操作，返回true;否则返回false。
         * 如果这个Spliterator是有序的，那么将按相遇顺序对下一个元素执行操作。
         * 该操作引发的异常将传递给调用者。
         * 覆写：Spliterator<T> 中的 tryAdvance
         * @param action	动作
         * @return 如果在进入此方法时不存在其他元素，则为false，否则为true。
         */
        public boolean tryAdvance(Consumer<? super V> action) {
            TreeMap.Entry<K,V> e;
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            if ((e = current) == null || e == fence)
                return false;
            current = successor(e);
            action.accept(e.value);
            if (tree.modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                throw new ConcurrentModificationException();
            return true;
        }
        /**
         * 返回该Spliterator及其元素的一组特征。结果被表示为来自
         * ORDERED, DISTINCT, SORTED, SIZED,NONNULL, IMMUTABLE, CONCURRENT,SUBSIZED。
         * 在调用trySplit之前或之间，对给定spliterator上的
         * characters()的重复调用应该总是返回相同的结果。
         *
         * 如果Spliterator报告一组不一致的特征(从单个调用或跨多个调用返回的特征)，
         * 则不能保证使用该Spliterator进行任何计算。
         * 覆写：Spliterator<T> 中的 characteristics
         * @return 特征的表示
         */
        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) | Spliterator.ORDERED;
        }
    }

    static final class EntrySpliterator<K,V>
            extends TreeMapSpliterator<K,V>
            implements Spliterator<Map.Entry<K,V>> {
        EntrySpliterator(TreeMap<K,V> tree,
                         TreeMap.Entry<K,V> origin, TreeMap.Entry<K,V> fence,
                         int side, int est, int expectedModCount) {
            super(tree, origin, fence, side, est, expectedModCount);
        }

        /**
         * 如果这个spliterator可以被分区，那么返回一个包含元素的spliterator，
         * 当这个方法返回时，这个spliterator将不包含元素。
         *
         * 如果这个Spliterator是有序的，返回的Spliterator必须包含元素的严格前缀。
         *
         * 除非这个Spliterator包含无穷多个元素，否则对trySplit()的重复调用最终必须返回null。
         * 在非空返回:
         * 1、在分割之前为estimateSize()报告的值，在分割之后，
         * 必须大于或等于该值和返回的Spliterator的estimateSize();
         * 2、如果这个Spliterator消失了，那么这个Spliterator在分裂前的estimateSize()
         * 必须等于这个Spliterator和分裂后返回的Spliterator的estimateSize()的和。
         *
         * 这种方法可能会因为任何原因返回null，包括空、遍历开始后无法分割、数据结构约束和效率考虑。
         *
         * @apiNote
         * 一个理想的trySplit方法有效地(不需要遍历)将其元素精确地分成两半，允许平衡的并行计算。
         * 许多偏离这一理想的做法仍然非常有效;例如，只近似地拆分一个近似平衡的树，
         * 或者对于叶子节点可能包含一个或两个元素的树，不能进一步拆分这些节点。
         * 然而，平衡上的较大偏差和/或效率过低的trySplit机制通常会导致较差的并行性能。
         * 覆写：Spliterator<T> 中的 trySplit
         * @return 一个包含部分元素的Spliterator，
         * 			如果这个Spliterator不能被分割，则为null
         */
        public EntrySpliterator<K,V> trySplit() {
            if (est < 0)
                getEstimate(); // force initialization
            int d = side;
            TreeMap.Entry<K,V> e = current, f = fence,
                    s = ((e == null || e == f) ? null :      // empty
                            (d == 0)              ? tree.root : // was top
                                    (d >  0)              ? e.right :   // was right
                                            (d <  0 && f != null) ? f.left :    // was left
                                                    null);
            if (s != null && s != e && s != f &&
                    tree.compare(e.key, s.key) < 0) {        // e还没有经过s
                side = 1;
                return new EntrySpliterator<>
                        (tree, e, current = s, -1, est >>>= 1, expectedModCount);
            }
            return null;
        }

        /**
         * 为当前线程中的每个剩余元素依次执行给定的操作，直到处理完所有元素或操作引发异常为止。
         * 如果这个Spliterator是有序的，则按相遇顺序执行操作。该操作引发的异常将传递给调用者。
         * 覆写：Spliterator<T> 中的 forEachRemaining
         * @param action 动作
         */
        public void forEachRemaining(Consumer<? super Map.Entry<K, V>> action) {
            if (action == null) // 如果指定的操作为空
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            TreeMap.Entry<K,V> f = fence, e, p, pl;
            if ((e = current) != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e);
                    if ((p = e.right) != null) {
                        while ((pl = p.left) != null)
                            p = pl;
                    }
                    else {
                        while ((p = e.parent) != null && e == p.right)
                            e = p;
                    }
                } while ((e = p) != null && e != f);
                if (tree.modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                    throw new ConcurrentModificationException();
            }
        }

        /**
         * 如果剩余元素存在，则对其执行给定的操作，返回true;否则返回false。
         * 如果这个Spliterator是有序的，那么将按相遇顺序对下一个元素执行操作。
         * 该操作引发的异常将传递给调用者。
         * 覆写：Spliterator<T> 中的 tryAdvance
         * @param action	动作
         * @return 如果在进入此方法时不存在其他元素，则为false，否则为true。
         */
        public boolean tryAdvance(Consumer<? super Map.Entry<K,V>> action) {
            TreeMap.Entry<K,V> e;
            if (action == null) // 如果指定的操作为空
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            if ((e = current) == null || e == fence)
                return false;
            current = successor(e); // 返回指定项的继承项，
            action.accept(e);
            if (tree.modCount != expectedModCount)// 如果在迭代过程中发现一个条目被删除
                throw new ConcurrentModificationException();
            return true;
        }

        /**
         * 返回该Spliterator及其元素的一组特征。结果被表示为来自
         * ORDERED, DISTINCT, SORTED, SIZED,NONNULL, IMMUTABLE, CONCURRENT,SUBSIZED。
         * 在调用trySplit之前或之间，对给定spliterator上的
         * characters()的重复调用应该总是返回相同的结果。
         *
         * 如果Spliterator报告一组不一致的特征(从单个调用或跨多个调用返回的特征)，
         * 则不能保证使用该Spliterator进行任何计算。
         * 覆写：Spliterator<T> 中的 characteristics
         * @return 特征的表示
         */
        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) |
                    Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.ORDERED;
        }

        /**
         * 如果这个Spliterator的源代码是由比较器排序的，则返回该比较器。
         * 如果源按可比较的自然顺序排序，则返回null。
         * 否则，如果源没有排序，则抛出IllegalStateException。
         * 覆写：Spliterator<T> 中的 getComparator
         * @return 一个比较器，如果元素按自然顺序排序，则为空。
         */
        @Override
        public Comparator<Map.Entry<K, V>> getComparator() {
            // 调整或创建一个基于键的比较器
            if (tree.comparator != null) {
                return Map.Entry.comparingByKey(tree.comparator);
            }
            else {
                return (Comparator<Map.Entry<K, V>> & Serializable) (e1, e2) -> {
                    @SuppressWarnings("unchecked")
                    Comparable<? super K> k1 = (Comparable<? super K>) e1.getKey();
                    return k1.compareTo(e2.getKey());
                };
            }
        }
    }
}