package edge_decimation;

import java.util.ArrayList;

public class MyPriorityQueue<T extends Comparable<T>> {

    protected ArrayList<T> ll = new ArrayList<T>(100_000);

    public MyPriorityQueue() {
    }

    public int size() {
        return ll.size();
    }

    public void add(T p) {

        int left = 0;
        int right = ll.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            int cmp = ll.get(mid).compareTo(p);
            if (cmp == 0) {
                ll.add(mid, p);
                return;
            } else if ( cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        ll.add(left, p);
    }

    public T remove() {
        return ll.remove(0);
    }

    public T remove(T p) {
        int idx = binarySearch(p);

        if(idx>=0) {
            int i=0;
            // search down and up for an exact match, compare to can be anywhere inbetween...
            T t = ll.get(idx+i);
            while( t.compareTo(p) == 0) {
                if( t == p )
                    return ll.remove(idx+i);
                else
                    i++;
                t = ll.get(idx+i);
            }
                
            i=0;
            t = ll.get(idx-i);
            while( t.compareTo(p) == 0) {
                if( t == p )
                    return ll.remove(idx+i);
                else
                    i--;
                t = ll.get(idx+i);
            }
            
        }

        throw new RuntimeException("sdasd");

   }

    public int binarySearch(T p) {
        int left = 0;
        int right = ll.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = ll.get(mid).compareTo(p);
            if (cmp == 0) {
                return mid;
            } else if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    public int indexOf_(Object o) {
        if (o != null) {
            for (int i = 0; i < ll.size(); i++) {
                if (o.equals(ll.get(i)))
                    return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return ll.toString();
    }

}
