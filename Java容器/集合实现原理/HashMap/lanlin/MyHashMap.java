package util;

import java.util.LinkedList;

public class MyHashMap<K, V> {
    @SuppressWarnings("unchecked")
    LinkedList<MyEntry<K, V>>[] arr = new LinkedList[999];

    int size;

    public void put(K k, V v) {
        MyEntry<K, V> e = new MyEntry<K, V>(k, v);
        int a = k.hashCode() % 999;
        if (arr[a] == null) {
            LinkedList<MyEntry<K, V>> list = new LinkedList<MyEntry<K, V>>();
            list.add(e);
            arr[a] = list;
        } else {
            LinkedList<MyEntry<K, V>> list = arr[a];
            for (int i = 0; i < list.size(); i++) {
                MyEntry<K, V> entry = list.get(i);
                if (entry.getKey().equals(k)) {
                    entry.setValue(v);
                    return;
                }
            }
            arr[a].add(e);
        }
    }

    public V get(K k) {
        int a = k.hashCode() % 999;
        if (arr[a] != null) {
            LinkedList<MyEntry<K, V>> list = arr[a];
            for (MyEntry<K, V> myEntry : list) {
                if (myEntry.getKey().equals(k)) {
                    return myEntry.getValue();
                }
            }
        }
        return null;
    }
}
