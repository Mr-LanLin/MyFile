package util;

/**
 * Node.
 * 结点
 * @author lanlin
 * @version 1.0
 * @param <E>
 */
public class Node<E> {
    private Node<E> previous;

    private E element;

    private Node<E> next;

    public Node<E> getPrevious() {
        return previous;
    }

    public Node(Node<E> previous, E element, Node<E> next) {
        super();
        this.previous = previous;
        this.element = element;
        this.next = next;
    }

    public E getElement() {
        return element;
    }

    public void setElement(E element) {
        this.element = element;
    }

    public Node<E> getNext() {
        return next;
    }

    public void setNext(Node<E> next) {
        this.next = next;
    }

    public void setPrevious(Node<E> previous) {
        this.previous = previous;
    }

    public Node() {
        super();
    }

}
