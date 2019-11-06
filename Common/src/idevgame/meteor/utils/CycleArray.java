package idevgame.meteor.utils;

import java.util.ArrayList;
import java.util.List;

/** 
 * 鏉烆喖娲栭弫鎵矋闂嗗棗鎮�
 * 閻€劋绨弸鍕紦娑擄拷娑擃亜娴愮�规艾銇囩亸蹇撹嫙娑撴柨鎯婇悳顖欏▏閻€劎娈戦弫鎵矋閵嗭拷
 * 鐠囥儱顔愰崳銊ф畱size()閻€劋绨懢宄板絿閺堫亜褰傞柅浣界箖閻ㄥ嫭鏆熼幑顔炬畱婢堆冪毈閵嗗倻鏁ゆ禍搴″灲閺傤厽妲搁崥锔芥箒閺堫亜褰傞柅浣界箖閻ㄥ嫭鏆熼幑锟�
 * @author son 
 * @version 2016楠烇拷4閺堬拷20閺冿拷 
 * 
 */
public class CycleArray<E> implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//閺佹壆绮�
	private Object[] elementData; 
	//閺佹壆绮嶆径褍鐨�
	private int size;
	//婢х偛濮為弫鐗堝祦閻ㄥ嫭鐖堕弽锟�
	private int cursor = -1;
	//閺屻儴顕楅弫鐗堝祦閻ㄥ嫭鐖堕弽锟�
	private int selectCursor = -1;
	
	private long updateDate;
	
	public long getUpdateDate() {
		return updateDate;
	}
	
	/**
	 * 閸掓繂顫愰崠鏍枂閸ョ偞鏆熺紒锟�
	 * @param initialCapacity 閺佹壆绮嶆径褍鐨�
	 */
	public CycleArray(int initialCapacity) {
		if(initialCapacity <= 0) {
			throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
		}
		elementData = new Object[initialCapacity];
		
		updateDate = System.currentTimeMillis();
	}
	
	/**
	 * 閸氬本顒炴晶鐐插鐎电钖�
	 * @param e
	 * @return
	 */
	public synchronized void synAdd(E e) {
			if(size < elementData.length) {
				size++;
			}
			cursor++;
			if(cursor >= size) {
				cursor = 0;
			}
			
			elementData[cursor] = e;
			
			updateDate = System.currentTimeMillis();
	}
	
	/**
	 * 婢х偛濮炵�电钖�
	 * @param e
	 */
	public void add(E e) {
		if(size < elementData.length) {
			size++;
		}
		cursor++;
		if(cursor >= size) {
			cursor = 0;
		}
		
		elementData[cursor] = e;
		
		updateDate = System.currentTimeMillis();
	}
	
	public int size() {
		if(cursor - selectCursor < 0) {
			return cursor - selectCursor + size;
		}
		
		return cursor - selectCursor;
	}
	
	@SuppressWarnings("unchecked")
	public E get(int index) {
		if(elementData.length < index) {
			throw new IndexOutOfBoundsException("Index: " + index  + ", Size: " + size);
		}
		
		return (E)elementData[index];
	}
	
	/**
	 * 閹跺﹤顔愰崳銊ュ敶閻ㄥ嫭鏆熼幑顔藉瘻鏉堟挸鍙嗘い鍝勭碍鏉堟挸鍤�
	 * @return
	 */
	public List<E> getSequenceList() {
		List<E> list = new ArrayList<>(size);
		for(int i = cursor +1; i <= cursor + size; i++) {
			
			list.add((E)elementData[i >= size ? i - size : i]);
		}
		
		return list;
	}
	
	/**
	 * 閼惧嘲褰囬張锟介弬鎵畱num閺夆剝鏆熼幑锟�
	 * @param num
	 * @return
	 */
	public List<E> getNewData(int num) {
		if(num <= 0) {
			return null;
		}
		List<E> newData = getNewData();
		int size2 = newData.size();
		if(size2 > num) {
			return newData.subList(size2 - num, size2);
		} 
		
		return newData;
	}
	
	/**
	 * 閼惧嘲褰囬弬鏉垮閸忋儱顔愰崳銊ф畱閺佺増宓侀獮鎯扮箲閸ョ�昳st
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<E> getNewData() {
		List<E> list = new ArrayList<>();
		while(selectCursor != cursor) {
			selectCursor++;
			
			if(selectCursor >= size) {
				selectCursor -= size;
			}
			Object[] elementData = CycleArray.this.elementData;
			list.add((E) elementData[selectCursor]);
		}
		
		return list;
	}
	
	public String toString() {
		String k = "[";
		for (int i = 0; i < size; i++) {
			k += elementData[i] + ",";
		}
		return k + "]";
	}
	
//	 /**
//     * An optimized version of AbstractList.Itr
//     */
//    private class Itr implements Iterator<E> {
//        
//		@Override
//		public boolean hasNext() {
//			if(selectCursor == cursor) {
//				return false;
//			}
//			return true;
//		}
//		
//		@SuppressWarnings("unchecked")
//		@Override
//		public E next() {
//			selectCursor++;
//			
//			if(selectCursor >= size) {
//				selectCursor -= size;
//			}
//			Object[] elementData = CycleArray.this.elementData;
//			return (E) elementData[selectCursor];
//		}
//    }	
    
    public static void main(String[] args) {
		CycleArray<Integer> cycleArray = new CycleArray<>(15);
		for(int i = 1; i <= 8; i++) {
			cycleArray.add(i);
		}
		
		System.out.println(cycleArray.toString());
		List<Integer> newData1 = cycleArray.getNewData();
		List<Integer> newData = cycleArray.getSequenceList();
		for (Integer integer : newData1) {
			System.out.print(integer + ",");
		}
		System.out.println();
		for (Integer integer : newData) {
			System.out.print(integer + ",");
		}
		for(int i = 1; i <= 10; i++) {
			cycleArray.add(i + 10);
		}
		System.out.println();
		List<Integer> newData2 = cycleArray.getNewData();
		for (Integer integer : newData2) {
			System.out.print(integer + ",");
		}
		
		System.out.println();
//		List<Integer> newData1 = cycleArray.getNewData(15);
//		for (Integer integer : newData1) {
//			System.out.print(integer + ",");
//		}
	}
}
