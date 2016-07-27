//package monaja;

import java.util.function.Function; // header stuff MUST go above the first class



public class HelloWorld
{
  static Wrap<Integer> inc(Integer x) {
    return Wrap.of(x + 1);
  }

  public static void main(String[] args)
  {
    System.out.println("Testing monads");
    System.out.println(Wrap.of("HELLO!").unwrap());

    System.out.println(Wrap
      .of("HELLO")
      .map(i ->  i+", WORLD!").unwrap());

    Wrap<Integer> a = Wrap.of(1);           
    Wrap<Integer> b = a.map(i -> i + 9);    
    Wrap<Integer> c = b.map(i -> i * 11);   
    a.map(i -> i * 10).map(i -> i + 11);   

    Wrap<Integer> k = Wrap.of(1);     
    k.flatMap(HelloWorld::inc); 
    Wrap<Integer> kres = k.flatMap(HelloWorld::inc).flatMap(HelloWorld::inc);

    Wrap<Integer> add1 = Wrap.of(5),
                  add2 = Wrap.of(10);

    Wrap res12 = add1.flatMap(a1 -> add2.map(b1 -> a1 + b1));

    System.out.println(res12.unwrap());

    System.out.println("a: "+a.unwrap());
    System.out.println("b: "+b.unwrap());
    System.out.println("c: "+c.unwrap());
    System.out.println("kres: "+kres.unwrap());
  }
}


interface Monad<T> {
  Monad<T> of(T value);
  <R> Monad<R> flatMap(Function<T, Monad<R>> mapper);
}

class Wrap<T> {
  private final T value;

  private Wrap(T value) { this.value = value; }

  public static <T> Wrap<T> of(T value) {
    return new Wrap<>(value);
  }

  public T unwrap() {
    return value;
  }

  public <R> Wrap<R> map(Function<T, R> mapper) {
    return Wrap.of(mapper.apply(value));
  }

  public <R> Wrap<R> flatMap(Function<T, Wrap<R>> mapper) {
    return mapper.apply(value);
  }
}
