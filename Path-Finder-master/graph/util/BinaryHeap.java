package util;

import java.util.Arrays;
import java.util.Comparator;

public class BinaryHeap<E> {

	Comparator<E> comparator;
	Object[] array = new Object[8];
	int size;

	public BinaryHeap(Comparator<E> c) {
		this.comparator = c;
	}

	public void clear() {
		Arrays.fill(array, 0, array.length, null);
		this.size = 0;
	}

	public int size() {
		return this.size;
	}

	private void ensureCapacity() {
		if (array.length <= size) {
			int newsize = array.length;
			while ((newsize = newsize*2) <= this.size);
			array = Arrays.copyOf(array, newsize);
		}
	}

	public void add(E e) {
		size++;
		ensureCapacity();
		array[size] = e;
		bubbleUp(size);
	}

	@SuppressWarnings("unchecked")
	public E remove() {
		if (size > 0) {
			Object obj = array[1];
			array[1] = array[size];
			size--;
			siftDown(1);
			return (E) obj;
		}
		return null;
	}

	public void remove(E e) {
		int index = indexOf(e);
		if (index != -1) {
			if (index == size) {
				array[size] = null;
				size--;
			}
			else {
				array[index] = array[size];
				size--;
				siftDown(index);
			}
		}
	}

	public boolean contains(E e) {
		return indexOf(e) != -1;
	}

	private int indexOf(E e) {
		for (int i = 0; i < array.length; i++) {
			if (e == array[i]) {
				return i;
			}
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	private void bubbleUp(int i) {
		while (i > 1) {
			if (comparator.compare((E)array[i], (E)array[i/2]) >= 0) 
				break;
			Object obj = array[i];
			array[i] = array[i/2];
			array[i/2] = obj;
			i /= 2;
		}
	}

	@SuppressWarnings("unchecked")
	private void siftDown(int i) {
		int i1 = i;
		while (true) {
			int i2 = i1;
			if (i2*2+1 <= size) {
				if (comparator.compare((E)array[i2], (E)array[i2*2]) > 0) {
					i1 = i2*2;
				}
				if (comparator.compare((E)array[i1], (E)array[i2*2+1]) > 0) {
					i1 = i2*2+1;
				}
			}
			else if (i2*2 <= size) {
				if (comparator.compare((E)array[i1], (E)array[i2*2]) > 0) {
					i1 = i2*2;
				}		    
			}
			if (i2 != i1) {
				Object obj = array[i2];
				array[i2] = array[i1];
				array[i1] = obj;
			}
			else 
				break;
		}
	}

}
