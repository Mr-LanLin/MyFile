#### HashMap

 HashMap是基于哈希表的Map接口的一个实现。它提供所有可选的映射操作，并允许使用null值和null键，不保证映射存储的顺序。HashMap实际上是一个“链表散列”的数据结构，即数组和链表的结合体。

```java

hashmap的put方法
public V put(K key, V value) {
    if (table == EMPTY_TABLE) {
        inflateTable(threshold);
    }
    if (key == null)
        return putForNullKey(value);当key为null时存储键值对数据
    int hash = hash(key);计算key的哈希值
    int i = indexFor(hash, table.length);获取key在hash表数组结构中的索引
    for (EntryK,V e = table[i]; e != null; e = e.next) {
        Object k;
        if (e.hash == hash && ((k = e.key) == key  key.equals(k))) {如果key已经存在则直接替换该key对应的值
            V oldValue = e.value;
            e.value = value;
            e.recordAccess(this);
            return oldValue;
        }
    }

    modCount++;
    addEntry(hash, key, value, i);创建一个新的键值对，并放入hash数组中
    return null;
}


private V putForNullKey(V value) {
    for (EntryK,V e = table[0]; e != null; e = e.next) {
        if (e.key == null) {
            V oldValue = e.value;
            e.value = value;
            e.recordAccess(this);
            return oldValue;
        }
    }
    modCount++;
    addEntry(0, null, value, 0);
    return null;
}


final int hash(Object k) {哈希算法
    int h = hashSeed;
    if (0 != h && k instanceof String) {
        return sun.misc.Hashing.stringHash32((String) k);
    }

    h ^= k.hashCode();

     This function ensures that hashCodes that differ only by
     constant multiples at each bit position have a bounded
     number of collisions (approximately 8 at default load factor).
    h ^= (h  20) ^ (h  12);
    return h ^ (h  7) ^ (h  4);
}


static int indexFor(int h, int length) {
     assert Integer.bitCount(length) == 1  length must be a non-zero power of 2;
    return h & (length-1);
}

void addEntry(int hash, K key, V value, int bucketIndex) {
    if ((size = threshold) && (null != table[bucketIndex])) {
        resize(2  table.length);
        hash = (null != key)  hash(key)  0;
        bucketIndex = indexFor(hash, table.length);
    }

    createEntry(hash, key, value, bucketIndex);
}


```

```java 
hashmap的get方法
public V get(Object key) {
    if (key == null)
        return getForNullKey();获取key为null的值
    EntryK,V entry = getEntry(key);根据key获取键值对
    return null == entry  null  entry.getValue();
}

private V getForNullKey() {
    if (size == 0) {
        return null;
    }
    for (EntryK,V e = table[0]; e != null; e = e.next) {
        if (e.key == null)
            return e.value;
    }
    return null;
}
    
final EntryK,V getEntry(Object key) {
    if (size == 0) {
        return null;
    }

    int hash = (key == null)  0  hash(key);
    for (EntryK,V e = table[indexFor(hash, table.length)];
         e != null;
         e = e.next) {根据key计算hash值，然后根据hash值获取哈希数组的索引值
        Object k;
        if (e.hash == hash &&
            ((k = e.key) == key  (key != null && key.equals(k))))遍历哈希链表获取键值对
            return e;
    }
    return null;
}
```
 ps：本人才疏学浅，理解不够深刻！