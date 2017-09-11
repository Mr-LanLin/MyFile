package util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * MyArrayList.
 * @author lanlin
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class MyArrayList<E> implements Iterable<E> {
    /** 装元素的数组. */
    private Object[] elementData;

    /** 集合的大小. */
    private int size;

    public MyArrayList() {
        this(10);
    }

    public MyArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new RuntimeException("容量不能小于0");
        }
        elementData = new Object[initialCapacity];
    }

    /**
     * 获取集合大小.
     * @return
     */
    public int size() {
        return size;
    }

    /**
     * 是否为空.
     * @return
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 添加元素.
     * @param e
     * @return
     */
    public void add(E e) {
        expansionCapacity(size);
        elementData[size++] = e;
    }

    /**
     * 在制定位置添加元素.
     * @param e
     * @return
     */
    public void add(int index, E e) {
        //检查下表
        rangeCheck(index);
        //扩容
        expansionCapacity(size);
        int moveNum = size - index;
        System.arraycopy(elementData, index, elementData, index + 1, moveNum);
        elementData[index] = e;
    }

    /**
     * 扩容
     */
    private void expansionCapacity(int size) {
        if (size >= elementData.length) {
            Object[] newElementDat = new Object[(size * 3 >> 1) + 1];
            System.arraycopy(elementData, 0, newElementDat, 0, size);
            elementData = newElementDat;
        }
    }

    /**
     * 获取元素.
     * @param index
     * @return
     */
    public E get(int index) {
        rangeCheck(index);
        return (E) elementData[index];
    }

    /**
     * 删除指定位置的元素.
     * @param index
     * @return
     */
    public E remove(int index) {
        rangeCheck(index);
        E e = (E) elementData[index];
        int moveNum = size - index - 1;
        if (moveNum > 0) {
            System.arraycopy(elementData, index + 1, elementData, index,
                moveNum);
        }
        elementData[--size] = null;
        return e;
    }

    /**
     * 删除指定元素.
     * @param e
     */
    public boolean remove(E e) {
        if (null == e) {
            for (int i = 0; i < size; i++) {
                if (null == elementData[i]) {
                    remove(i);
                    return true;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (e.equals(elementData[i])) {
                    remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 设置指定位置的元素.
     * @param index
     * @param e
     */
    public E set(int index, E e) {
        rangeCheck(index);
        E oldElement = (E) elementData[index];
        elementData[index] = e;
        return oldElement;
    }

    /**
     * 检查下表是否越界.
     * @param index
     */
    private void rangeCheck(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("下标越界");
        }
    }

    /**
     * 批量添加元素.
     * @param c
     */
    public void addAll(Collection<E> c) {
        int addlength = c.size();
        Object[] addArray = c.toArray();
        expansionCapacity(addlength + size);
        System.arraycopy(addArray, 0, elementData, size, addlength);
        size += addlength;
    }

    /**
     * to Array.
     * @return
     */
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    /**
     * 是否包含某元素.
     * @param o
     * @return
     */
    public boolean contains(Object o) {
        return indexOf(o) > -1;
    }

    /**
     * 查找元素第一次出现的位置.
     * @param o
     * @return
     */
    public int indexOf(Object o) {
        if (null == o) {
            for (int i = 0; i < size; i++) {
                if (null == elementData[i]) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 获取迭代器.
     * @return
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            int nextIndex = 0;

            @Override
            public boolean hasNext() {
                return nextIndex < size;
            }

            @Override
            public E next() {
                rangeCheck(nextIndex);
                return get(nextIndex++);
            }

            @Override
            public void remove() {
                rangeCheck(nextIndex);
                MyArrayList.this.remove(nextIndex--);
            }
        };
    }

}
