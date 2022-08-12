# Maybe

Maybe es como un Single, excepto que no permite que se produzca ninguna emisión (por lo tanto, Maybe).
MaybeObserver es muy parecido a un observador estándar, pero onNext() se llama onSuccess() en su lugar:

````java
public interface MaybeObserver<T> {

    void onSubscribe(Disposable d);

    void onSuccess(T value);

    void onError(Throwable e);

    void onComplete();
}

````

Un Maybe<T> solo emitirá 0 o 1 emission.
Pasará la posible emisión a onSuccess() y, en cualquier caso, llamará a onComplete() cuando termine.
Maybe.just() se puede usar para crear un Maybe que emita el elemento único.
Maybe.empty() generará un Maybe que no produzca ninguna emisión.

````java

import io.reactivex.Maybe;

public class Launcher {
    public static void main(String[] args) {
        // has emission
        Maybe<Integer> presentSource = Maybe.just(100);
        presentSource.subscribe(s -> System.out.println("Process 1 received:" + s),
                Throwable::printStackTrace,
                () -> System.out.println("Process 1 done!"));
        //no emission
        Maybe<Integer> emptySource = Maybe.empty();
        emptySource.subscribe(s -> System.out.println("Process 2 received:" + s),
                Throwable::printStackTrace,
                () -> System.out.println("Process 2 done!"));
    }
}
````

Ciertos operadores observables sobre los que aprenderemos más adelante producen un Maybe.
Un ejemplo es el operador firstElement(), que es similar a first(), pero devuelve un resultado vacío
si no se emite ningún elemento.

````java
import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        source.firstElement().subscribe(
                s -> System.out.println("RECEIVED " + s),
                Throwable::printStackTrace,
                () -> System.out.println("Done!"));
    }
}
````
