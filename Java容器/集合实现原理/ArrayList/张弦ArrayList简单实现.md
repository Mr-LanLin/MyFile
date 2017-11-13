# �����Ż�
**arrayList �ļ�ʵ�֣�д�� get��remove����**

```java

import java.util.Arrays;

/**
 * Title: <br>
 * Description: <br>
 * Copyright: Copyright (c) 2007<br>
 * Company: ����������Ϣ�������޹�˾<br>
 * @author zhangxian
 * @version 1.0
 * @date 2017��9��13��
 */
public class ArrayListDemo {
    
    private Object[] elements;
    
    private int size = 0;
    
    public static void main(String[] args) {
        ArrayListDemo demo = new ArrayListDemo();
        for(int i=0;i<3;i++){
            demo.add(i);
        }
        demo.remove(0);
        System.out.println(demo);
    }

    public ArrayListDemo() {
        elements = new Object[10];
    }
    
    public void add(Object o) {
        ensureCapacity(size+1);
        elements[size] = o;
        size ++;
    }
    
	public void get(int index){
		if(index < size){
			return elements[index];
		}
    }

    public void remove(int index){
        System.arraycopy(elements, index+1, elements, index, elements.length - index- 1);
        elements[elements.length-1] = null;
        size --;
    }
    
    public void ensureCapacity(int minCapacity){
        if(minCapacity > elements.length){
            Object[] temp2 = new  Object[minCapacity];
            System.arraycopy(elements, 0, temp2, 0, elements.length);
            elements = temp2;
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOf(elements, size));
    }
}


```