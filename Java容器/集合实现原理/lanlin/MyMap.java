package util;

/**
 * MyMap
 * @author lanlin
 * @version 1.0
 * @param <K>
 * @param <V>
 */
public class MyMap<K, V> {
    @SuppressWarnings("unchecked")
    MyEntry<K, V>[] entries = new MyEntry[999];

    int size;

    public void put(K key, V value) {
        MyEntry<K, V> e = new MyEntry<K, V>(key, value);
        for (int i = 0; i < size; i++) {
            if (entries[i].getKey().equals(key)) {
                entries[i].setValue(value);
                return;
            }
        }
        entries[size++] = e;
    }

    public V get(K k) {
        for (int i = 0; i < size; i++) {
            if (entries[i].getKey().equals(k)) {
                return entries[i].getValue();
            }
        }
        return null;
    }

    public boolean containsKey(K k) {
        for (int i = 0; i < size; i++) {
            if (entries[i].getKey().equals(k)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsValue(V v) {
        for (int i = 0; i < size; i++) {
            if (entries[i].getValue().equals(v)) {
                return true;
            }
        }
        return false;
    }
}

class MyEntry<K, V> {

    private K key;

    private V value;

    private MyEntry<K, V> next;

    private int hashCode;

    public MyEntry(K key, V value) {
        super();
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public MyEntry<K, V> getNext() {
        return next;
    }

    public void setNext(MyEntry<K, V> next) {
        this.next = next;
    }

    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

}