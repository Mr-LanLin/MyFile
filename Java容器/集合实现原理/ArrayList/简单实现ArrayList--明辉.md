---
style: candy
---
简单实现ArrayList
===
```java
import java.util.ArrayList;   
/**
 * Created by mingh on 2017/9/6 0006. 
 */ 
public class MyArrayList<E> {

    private int capacity = 10;
    private int size = 0;
    private E[] values = null;   

	public MyArrayList () {
    values = (E[]) new Object[capacity];
 	}

    public MyArrayList (int capacity) {
        this.capacity = capacity;
  	values = (E[]) new Object[capacity];
  	}

    public void put (E e) {
        if (e == null) {
            throw new RuntimeException("This value should not be null.");
  		}
        if (size >= capacity) {
            enlargeCapacity();
  		}
        values [size] = e;
  		size ++;
  	}

    public E get (int index) {
        rangeCheck(index);
		return values[index];
  	}

    private void rangeCheck(int index) {
        if (index >= size) {
            throw new RuntimeException("The index:" + index + " is out of band.");
  		}
    }

    public void remove (int index) {
        rangeCheck(index);
		for (int i = index; i < size - 1; i++) {
	        values[i] = values[i + 1];
	  	}
	    values[size - 1] = null;
	  	size --;
 	}

    private void enlargeCapacity() {
        capacity *= 2;
  		E[] tmpValues = (E[]) new Object[capacity ];
  		System.arraycopy(values, 0, tmpValues, 0, size);
  		values = tmpValues;
  	}

    public String toString () {
        StringBuilder sb = new StringBuilder();
  		sb.append("[");
 		for (int i = 0; i < size; i++) {
            sb.append(values[i]).append(",");
  		}
        if (size > 0) {
            sb.deleteCharAt(sb.length() - 1);
  		}
        sb.append("]");
 		return sb.toString();
  	}

}
```