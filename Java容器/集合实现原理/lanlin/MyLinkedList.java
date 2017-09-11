package util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * MyLinkedList.
 * @author lanlin
 * @version 1.0
 * @param <E>
 */
public class MyLinkedList<E> implements Iterable<E> {
    /** 前一个元素. */
    private Node<E> first;

    /** 后一个元素. */
    private Node<E> last;

    /** 集合大小. */
    private int size;

    /**
     * 添加元素.
     * @param e
     */
    public void add(E e) {
        Node<E> node = new Node<E>();
        if (first == null) {
            node.setPrevious(null);
            node.setNext(null);
            node.setElement(e);
            first = node;
            last = node;
        } else {
            node.setPrevious(last);
            node.setElement(e);
            node.setNext(null);
            last.setNext(node);
            last = node;
        }
        size++;
    }

    /**
     * 获取集合大小.
     * @return
     */
    public int size() {
        return size;
    }

    /**
     * 检查index.
     * @param index
     */
    private void rangCheck(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("下标越界");
        }
    }

    /**
     * 获取指定索引的元素.
     * @param index
     * @return
     */
    public E get(int index) {
        rangCheck(index);
        Node<E> temp = getNode(index);
        if (temp != null) {
            return temp.getElement();
        }
        return null;
    }

    /**
     * 移除指定索引位置的元素.
     * @param index
     */
    public void remove(int index) {
        Node<E> temp = getNode(index);
        if (temp != null) {
            Node<E> up = temp.getPrevious();
            Node<E> down = temp.getNext();
            if (up == null) {
                first = down;
            } else {
                up.setNext(down);
            }

            if (down == null) {
                last = up;
            } else {
                down.setPrevious(up);
            }
            size--;
        }
    }

    /**
     * 移除指定元素.
     * @param o
     */
    public void remove(Object o) {
        if (null == o) {
            for (int i = 0; i < size; i++) {
                if (null == get(i)) {
                    remove(i);
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(get(i))) {
                    remove(i);
                }
            }
        }
    }

    /**
     * 获取制定索引位置的结点.
     * @param index
     * @return
     */
    private Node<E> getNode(int index) {
        Node<E> temp = null;
        if (first != null) {
            temp = first;
            for (int i = 0; i < index; i++) {
                temp = temp.getNext();
            }
        }
        return temp;
    }

    /**
     * 在指定索引位置添加元素.
     * @param index
     * @param e
     */
    public void add(int index, E e) {
        Node<E> temp = getNode(index);
        Node<E> newNode = new Node<>();
        newNode.setElement(e);
        if (temp != null) {
            Node<E> up = temp.getPrevious();
            up.setNext(newNode);
            newNode.setPrevious(up);
            newNode.setNext(temp);
            temp.setPrevious(newNode);
            size++;
        }
    }

    /**
     * 设置第一个元素.
     * @param index
     * @param e
     */
    public void add2First(E e) {
        Node<E> temp = first;
        Node<E> newNode = new Node<>();
        newNode.setElement(e);
        first = newNode;
        if (null == temp) {
            last = newNode;
        } else {
            temp.setPrevious(newNode);
            newNode.setNext(temp);
        }
        size++;
    }

    /**
     * 设置最后一个元素.
     * @param index
     * @param e
     */
    public void add2Last(E e) {
        Node<E> temp = last;
        Node<E> newNode = new Node<>();
        newNode.setElement(e);
        last = newNode;
        if (null == temp) {
            last = newNode;
        } else {
            newNode.setPrevious(temp);
            temp.setNext(newNode);
        }
        size++;
    }

    /**
     * 获取第一个元素.
     * @return
     */
    public E getFirst() {
        return first == null ? null : first.getElement();
    }

    /**
     * 获取最后一个元素.
     * @return
     */
    public E getLast() {
        return last == null ? null : last.getElement();
    }

    /**
     * 获取迭代器.
     * @return
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> lastReturned;

            private Node<E> next = first;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("没有下一个了");
                }
                lastReturned = next;
                next = lastReturned.getNext();
                return lastReturned.getElement();
            }

            @Override
            public void remove() {
                if (null == lastReturned) {
                    throw new IllegalStateException("请先调用next()");
                }
                MyLinkedList.this.remove(lastReturned.getElement());
            }
        };
    }
}
