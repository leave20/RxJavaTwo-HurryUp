# ¿Que es un Observable?

El Observable es un iterador que puede estar compuesto por varios elementos.
(llamados emisiones) a través de una serie de operadores hasta que finalmente llega a un Observador final,
que consume dichos elementos. Cubriremos varias formas de crear un Observable,
pero primero, profundicemos en cómo funciona un Observable a través de sus llamadas onNext(),
onCompleted() y onError().

## ¿Como trabaja un Observable?

Antes de hacer cualquier otra cosa, debemos estudiar cómo un Observable pasa secuencialmente
los elementos de una cadena a un Observador. Al más alto nivel, un Observable funciona pasando tres tipos de eventos:

- onNext(): Esto pasa cada elemento de uno en uno desde el Observable de origen hasta el Observador.
- onComplete(): esto comunica un evento de finalización hasta el observador, lo que indica que no se producirán más
  llamadas onNext().
- onError(): esto comunica un error en la cadena al observador, donde el observador normalmente define cómo manejarlo.

A menos que se use un operador retry() para interceptar el error, la cadena Observable generalmente termina y
no se producirán más emisiones.

### Usando `Observable.create()`

El método `create()` es una función que devuelve un Observable.

```
public static Observable<T> create(ObservableOnSubscribe<T> onSubscribe)
```

Comencemos con la creación de un Observable fuente usando Observable.create().
Relativamente hablando, un Observable de fuente es un Observable desde donde se originan las emisiones
y es el punto de partida de nuestra cadena de Observables.
El factory `Observable.create()` nos permite generar un Observable proporcionando una lambda que recibe un emisor
Observable.
Podemos llamar al método onNext() del emisor Observable para pasar las emisiones (una a la vez) en la cadena,
así como a onComplete() para señalar la finalización y comunicar que no habrá más elementos.
Estas llamadas a onNext() pasarán estos elementos por la cadena hacia el Observer,
donde imprimirá cada elemento, como se muestra en el siguiente fragmento de código.

```java
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Launcher {
    public static void main(String[] args) {
        Observable<String> source = Observable.create(emitter -> {
            emitter.onNext("Alpha");
            emitter.onNext("Beta");
            emitter.onNext("Gamma");
            emitter.onNext("Delta");
            emitter.onNext("Epsilon");
            emitter.onComplete();
        });
        source.subscribe(s -> log.info("RECEIVED: {} ", s));
    }
}
```

```
 RECEIVED: Alpha
 RECEIVED: Beta
 RECEIVED: Gamma
 RECEIVED: Delta
 RECEIVED: Epsilon
```

#### onNext()

El método onNext() es una forma de entregar cada elemento, comenzando con Alpha, al siguiente paso de la cadena.
En este ejemplo, el siguiente paso es el Observer, que imprime el elemento usando la lambda
`s -> System.out.println("RECEIVED: " + s)`.
Este lambda se invoca en la llamada onNext() de Observer, y veremos Observer más de cerca en un momento.

#### onComplete()

El método onComplete() se usa para comunicarle al observador que no van a llegar más elementos.
De hecho, los observables pueden ser infinitos, y si este es el caso, nunca se llamará al evento onComplete().
Técnicamente, una fuente podría dejar de emitir llamadas onNext() y nunca llamar a onComplete().
Sin embargo, esto probablemente sería un mal diseño si la fuente ya no planea enviar emisiones.

#### onError()

Aunque es poco probable que este ejemplo en particular arroje un error,
podemos detectar los errores que pueden ocurrir dentro de nuestro bloque Observable.create()
y emitirlos a través de onError(). De esta manera, el error puede ser empujado hacia arriba en la cadena
y manejado por el observador. Este observador particular que hemos configurado no maneja excepciones,
pero puede hacerlo que, como se muestra aquí:

```java
import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable<String> source = Observable.create(emitter -> {
            try {
                emitter.onNext("Alpha");
                emitter.onNext("Beta");
                emitter.onNext("Gamma");
                emitter.onNext("Delta");
                emitter.onNext("Epsilon");
                emitter.onComplete();
            } catch (Throwable e) {
                emitter.onError(e);
            }
        });
        source.subscribe(s -> System.out.println("RECEIVED: " + s),
                Throwable::printStackTrace);
    }
}
```

Tenga en cuenta que onNext(), onComplete() y onError() no necesariamente envían directamente al observador final.
También pueden empujar a un operador que actúa como el siguiente paso en la cadena.
En el siguiente código, derivamos nuevos Observables con los operadores map() y filter(),
que actuarán entre el Observable de origen y el Observer final imprimiendo los elementos:

```java
import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable<String> source = Observable.create(emitter -> {

            try {
                emitter.onNext("Alpha");
                emitter.onNext("Beta");
                emitter.onNext("Gamma");
                emitter.onNext("Delta");
                emitter.onNext("Epsilon");
                emitter.onComplete();
            } catch (Throwable e) {
                emitter.onError(e);
            }
        });
        Observable<Integer> lengths = source.map(String::length);
        Observable<Integer> filtered = lengths.filter(i -> i >= 5);
        filtered.subscribe(s -> System.out.println("RECEIVED: " + s));
    }
}
```

```
 RECEIVED: 5
 RECEIVED: 5
 RECEIVED: 5
 RECEIVED: 7
```

Con los operadores map() y filter() entre el Observable de origen y el Observer, onNext()
entregará cada elemento al operador map(). Internamente, actuará como un observador intermediario
y convertirá cada cadena a su lengths(). Esto, a su vez, llamará a onNext() en filter() para pasar ese entero,
y la condición lambda i -> i >= 5 suprimirá las emisiones que no tengan al menos cinco caracteres de longitud.
Finalmente, el operador filter() llamará a onNext() para entregar cada elemento al observador final donde se imprimirán.

### Nota

Es fundamental tener en cuenta que el operador map() generará un nuevo `Observable<Integer>`
derivado del `Observable<String>` original. El filter() también devolverá un `Observable<Integer>`,
pero ignorará las emisiones que no cumplan con los criterios. Dado que los operadores como map() y filter()
generan nuevos Observables (que internamente usan implementaciones de Observer para recibir emisiones),
podemos encadenar todos nuestros Observables devueltos con el siguiente operador en lugar de guardarlos
innecesariamente en una variable intermedia.

```java
 import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable<String> source = Observable.create(emitter -> {
            try {
                emitter.onNext("Alpha");
                emitter.onNext("Beta");
                emitter.onNext("Gamma");
                emitter.onNext("Delta");
                emitter.onNext("Epsilon");
                emitter.onComplete();
            } catch (Throwable e) {
                emitter.onError(e);
            }
        });
        source.map(String::length)
                .filter(i -> i >= 5)
                .subscribe(s -> System.out.println("RECEIVED: " + s));
    }
}
```
```
 RECEIVED: 5
 RECEIVED: 5
 RECEIVED: 5
 RECEIVED: 7
```
