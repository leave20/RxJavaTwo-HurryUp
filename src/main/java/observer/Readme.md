# La Interface Observer

Los métodos onNext(), onComplete() y onError() en realidad definen el tipo Observer,
una interfaz abstracta implementada en RxJava para comunicar estos eventos.
Esta es la definición de Observer en RxJava que se muestra en el fragmento de código.
No se preocupe por onSubscribe() por ahora, ya que lo cubriremos al final de este capítulo.
Simplemente, traiga su atención a los otros tres métodos:

```java
package io.reactivex;

import io.reactivex.disposables.Disposable;

public interface Observer<T> {
    void onSubscribe(Disposable d);

    void onNext(T value);

    void onError(Throwable e);

    void onComplete();
}
```

Los observadores y los observables fuente son algo relativos. En un contexto, una fuente Observable es donde comienza
su cadena Observable y donde se originan las emisiones.
En nuestros ejemplos anteriores, podría decir que el Observable devuelto por nuestro método Observable.create()
u Observable.just() es el Observable de origen. Pero para el operador filter(),
el Observable devuelto por el operador map() es la fuente. No tiene idea de dónde se originan las emisiones,
y solo sabe que está recibiendo emisiones del operador inmediatamente aguas arriba, que provienen de map().

Por el contrario, cada Observable devuelto por un operador es internamente un Observador que recibe, transforma
y retransmite emisiones al siguiente Observador aguas abajo. No sabe si el siguiente observador es otro operador
o el último observador al final de la cadena. Cuando hablamos del Observador, a menudo nos referimos al Observador
final al final de la cadena Observable que consume las emisiones. Pero cada operador, como map() y filter(),
también implementa Observer internamente.

## Implementación y suscripción a un observador

Cuando llama al método subscribe() en un Observable, se usa un Observer para
consumir estos tres eventos implementando sus métodos.
En lugar de especificar argumentos lambda como lo hacíamos antes,
podemos implementar un Observer y pasar una instancia del mismo al método subscribe().
No te preocupes por onSubscribe() en este momento.

```java
 import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class Launcher {
    public static void main(String[] args) {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon");
        Observer<Integer> myObserver = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                //do nothing with Disposable, disregard for now
            }

            @Override
            public void onNext(Integer value) {
                System.out.println("RECEIVED: " + value);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Done!");
            }
        };
        source.map(String::length).filter(i -> i >= 5)
                .subscribe(myObserver);
    }
}

```

```
 RECEIVED: 5
 RECEIVED: 5
 RECEIVED: 5
 RECEIVED: 7
 Done!
```

Creamos rápidamente un Observer<Integer> que sirve como nuestro Observer, y recibirá emisiones de longitud entera.
Nuestro observador recibe emisiones al final de una cadena observable y sirve como punto final donde se consumen
las emisiones. Por consumidos, esto significa que llegan al final del proceso donde se escriben en una base de datos,
un archivo de texto, una respuesta del servidor, se muestran en una interfaz de usuario, o (en este caso)
simplemente impreso en la consola.

## Taquigrafía de un Observer con Lambdas

Implementar un observador es un poco detallado y engorroso. Afortunadamente, el método subscribe() está sobrecargado
para aceptar argumentos lambda para nuestros tres eventos.
Esto es probablemente lo que querremos usar en la mayoría de los casos, y podemos especificar tres parámetros lambda
separados por comas: onNext lambda, onError lambda y onComplete lambda.
Para nuestro ejemplo anterior, podemos consolidar nuestras implementaciones de tres métodos usando estas tres lambdas:

```
        Consumer<Integer> onNext = i -> System.out.println("RECEIVED: " + i);
        Action onComplete = () -> System.out.println("Done!");
        Consumer<Throwable> onError = Throwable::printStackTrace;
```

Podemos pasar estas tres lambdas como argumentos al método subscribe(), y usará
ellos para implementar un observador para nosotros. Esto es mucho más conciso y requiere mucho menos
código repetitivo:

```java
import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon");
        source.map(String::length).filter(i -> i >= 5)
                .subscribe(i -> System.out.println("RECEIVED: " + i),
                        Throwable::printStackTrace,
                        () -> System.out.println("Done!"));
    }
}
```

```
 RECEIVED: 5
 RECEIVED: 5
 RECEIVED: 5
 RECEIVED: 7
 Done!
```

Tenga en cuenta que hay otras sobrecargas para subscribe().
Puede omitir onComplete() y solo implementar onNext() y onError().
Esto ya no realizará ninguna acción para onComplete(), pero probablemente habrá casos en los que no necesite uno

```java
import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon");
        source.map(String::length).filter(i -> i >= 5)
                .subscribe(i -> System.out.println("RECEIVED: " + i),
                        Throwable::printStackTrace);
    }
}

```

Como ha visto en ejemplos anteriores, incluso puede omitir onError y simplemente especificar onNext:

```java
import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon");
        source.map(String::length).filter(i -> i >= 5)
                .subscribe(i -> System.out.println("RECEIVED: " + i));
    }
}
```



