# Single

Single<T> es esencialmente un Observable<T> que solo emitirá un elemento. Funciona como un Observable,
pero está limitado solo a operadores que tienen sentido para una sola emisión.
También tiene su propia interfaz SingleObserver.

```java
 interface SingleObserver<T> {
    void onSubscribe(Disposable d);

    void onSuccess(T value);

    void onError(Throwable error);
}
```

onSuccess() esencialmente consolida onNext() y onComplete() en un solo evento que acepta la emisión.
Cuando llamas a subscribe() contra un Single, proporcionas las lambdas para onSuccess() así como un onError() opcional:

```java
import io.reactivex.Single;

public class Launcher {
    public static void main(String[] args) {
        Single.just("Hello")
                .map(String::length)
                .subscribe(System.out::println,
                        Throwable::printStackTrace);
    }
}
```

Ciertos operadores observables de RxJava generarán un Single, como veremos en el próximo capítulo.
Por ejemplo, el operador first() devolverá un Single, ya que ese operador está relacionado lógicamente con un solo
elemento.
Sin embargo, acepta un valor predeterminado como parámetro (que especifiqué como Nil en el siguiente ejemplo) si el
Observable sale vacío:

```java
import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma");
        source.first("Nil") //returns a Single
                .subscribe(System.out::println);
    }
}
```
```
Alpha
```

El Single debe tener una emisión, y debería preferirlo si solo tiene una emisión para proporcionar. 
Esto significa que en lugar de usar Observable.just("Alpha"), debe intentar usar Single.just("Alpha") en su lugar. 
Hay operadores en Single que le permitirán convertirlo en un Observable cuando sea necesario, como toObservable().