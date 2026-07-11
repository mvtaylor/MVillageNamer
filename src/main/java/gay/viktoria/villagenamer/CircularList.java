package gay.viktoria.villagenamer;

import java.util.ArrayList;
import java.util.Collection;

public class CircularList<T> extends ArrayList<T> {
    private int index = 0;

    public CircularList(Collection<? extends T> items) {
        super(items);
    }

    public CircularList() {
        super();
    }

    public T getNext() throws IllegalAccessException {
        if (this.isEmpty()) throw new IllegalAccessException("Circular list is empty");
        T value = this.get(index);
        index = (index + 1) % this.size();
        return value;
    }
}
