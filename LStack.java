import java.util.Iterator;


public class LStack<T> implements IStack<T> {
  class Link {
    private T datum;
    private Link next;

    public Link(T datum, Link next) {
      this.datum = datum;
      this.next = next;
    }

    public Link(T datum) {
      this(datum, null);
    }

    public T getDatum() {
      return datum;
    }

    public Link getNext() {
      return next;
    }
  }

  private Link top;
  private int size;

  public LStack() {
    top = null;
    size = 0;
  }

  public void push(T newItem) {
    Link l = new Link(newItem, top);
    top = l;
    size++;
  }

  public T pop() {
    if (isEmpty()) {
      throw new EmptyStackException();
    }
    Link l = top;
    top = l.getNext();
    size--;
    T res = l.getDatum();
    l = null;
    return res;
  }

  public T peek() {
    if (isEmpty()) {
      throw new EmptyStackException();
    }
    return top.getDatum();
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return top == null;
  }

  public void clear() {
    top = null;
    size = 0;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    Link l = top;
    for (Link nav = top; nav != null; nav = nav.getNext()) {
      sb.append(nav.getDatum() + (nav.getNext() == null ? "" : ", "));
    }
    sb.append("]");
    return sb.toString();
  }

  public Iterator<T> iterator() {
    return new LStackIterator();
  }

  class LStackIterator implements Iterator<T> {
    Link current;
    public LStackIterator() {
      current = top;
    }

    public T next() {
      T out = current.getDatum();
      current = current.getNext();
      return out;
    }

    public boolean hasNext() {
      return current != null;
    }
  }

  public static void main(String[] args) {
    LStack<Character> ls = new LStack<>();
    for (char c = 'a'; c < 'g'; c++) {
      ls.push(c);
    }
    System.out.println(ls);
    System.out.println(ls.pop());
    System.out.println(ls.size());
    System.out.println(ls);
    for (Character c : ls) {
      System.out.println(c);
    }
    LStack<Character> els = new LStack<>();
    System.out.println(els);
  }
}
