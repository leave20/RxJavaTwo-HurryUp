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

### Usando `Observable.just()`

Antes de analizar un poco más el método subscribe(), tenga en cuenta que es probable
que no necesite usar `Observable.create()` con frecuencia.
Puede ser útil para conectarse a ciertas fuentes que no son reactivas,
y veremos esto en un par de lugares más adelante en este capítulo.
Pero, por lo general, utilizamos factories optimizadas para generar Observables para fuentes comunes.

```java
import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon");
        source.map(String::length).filter(i -> i >= 5)
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

También podemos usar `Observable.fromIterable()` para emitir los elementos de cualquier tipo Iterable, como una Lista.
Adicionalmente, llamará a onNext() para cada elemento y luego llamará a onComplete() después de que se complete la
iteración.
Es probable que utilice este factory con frecuencia, ya que los Iterables en Java son comunes y se pueden reactivar
fácilmente:

```java
import io.reactivex.Observable;

import java.util.Arrays;
import java.util.List;

public class Launcher {
    public static void main(String[] args) {
        List<String> items =
                Arrays.asList("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        Observable<String> source = Observable.fromIterable(items);
        source.map(String::length).filter(i -> i >= 5)
                .subscribe(s -> System.out.println("RECEIVED: " + s));
    }
}
```

## Cold and Hot Observables

Hay comportamientos sutiles en una relación entre un Observable y un Observador
dependiendo de cómo se implemente el Observable.
Una característica importante a tener en cuenta es el frío frente a los observables calientes,
que define cómo se comportan los observables cuando hay múltiples observadores.
Primero, cubriremos los Observables fríos.

### Cold Observables

Cold Observables es muy parecido a un CD de música que se puede reproducir para cada oyente,
por lo que cada persona puede escuchar todas las pistas en cualquier momento.
De la misma manera, los Observables fríos reproducirán las emisiones a cada Observador,
asegurando que todos los Observadores obtengan todos los datos.
La mayoría de los Observables controlados por datos son fríos, y esto incluye las fábricas Observable.just()
y Observable.fromIterable().

En el siguiente ejemplo, tenemos dos Observadores suscritos a un Observable.
El Observable primero reproducirá todas las emisiones al primer Observador y luego llamará a Complete().
Luego, reproducirá todas las emisiones nuevamente al segundo Observador y llamará a Complete().
Ambos reciben los mismos conjuntos de datos al obtener dos flujos separados cada uno,
lo cual es un comportamiento típico para un Observable frío:

```java
 import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        //first observer
        source.subscribe(s -> System.out.println("Observer 1 Received: " + s));
        //second observer
        source.subscribe(s -> System.out.println("Observer 2 Received: " + s));
    }
}
```

```
 Observer 1 Received: Alpha
 Observer 1 Received: Beta
 Observer 1 Received: Gamma
 Observer 1 Received: Delta
 Observer 1 Received: Epsilon
 Observer 2 Received: Alpha
 Observer 2 Received: Beta
 Observer 2 Received: Gamma
 Observer 2 Received: Delta
 Observer 2 Received: Epsilon
```

Incluso si el segundo Observador transforma sus emisiones con los operadores,
seguirá obteniendo su propio flujo de emisiones. El uso de operadores como map() y filter()
contra un Observable frío aún mantendrá la naturaleza fría de los Observables producidos:

```java
import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        //first observer
        source.subscribe(s -> System.out.println("Observer 1 Received: " + s));
        //second observer
        source.map(String::length)
                .filter(i -> i >= 5)
                .subscribe(s -> System.out.println("Observer 2 Received: " + s));
    }
}
```

```
 Observer 1 Received: Alpha
 Observer 1 Received: Beta
 Observer 1 Received: Gamma
 Observer 1 Received: Delta
 Observer 1 Received: Epsilon
 Observer 2 Received: 5
 Observer 2 Received: 5
 Observer 2 Received: 5
 Observer 2 Received: 7
```

Como se indicó anteriormente, las fuentes observables que emiten conjuntos de datos finitos suelen ser frías.
<p>
Aquí hay un ejemplo más real: RxJava-JDBC (https://github.com/davidmoten/rxjava-jdbc) de Dave Moten
le permite crear observables fríos construidos a partir de consultas de bases de datos SQL.
No nos desviaremos en esta biblioteca por mucho tiempo, pero si desea consultar una base de datos SQLite,
por ejemplo, incluya el controlador JDBC SQLite y las bibliotecas RxJava-JDBC en su proyecto.
A continuación, puede consultar una tabla de base de datos de forma reactiva,
como se muestra en el siguiente fragmento de código:

```java
import com.github.davidmoten.rx.jdbc.ConnectionProviderFromUrl;
import com.github.davidmoten.rx.jdbc.Database;
import rx.Observable;

import java.sql.Connection;

public class Launcher {
    public static void main(String[] args) {
        Connection conn =
                new ConnectionProviderFromUrl("jdbc:sqlite:/home/thomas/rexon_metals.db").get();
        Database db = Database.from(conn);
        Observable<String> customerNames =
                db.select("SELECT NAME FROM CUSTOMER")
                        .getAs(String.class);
        customerNames.subscribe(s -> System.out.println(s));
    }
}
```

### Hot Observables

Acabas de aprender sobre Observables fríos, que funciona como un CD de música.
Un Observable caliente es más como una estación de radio.
Transmite las mismas emisiones a todos los observadores al mismo tiempo.
Si un Observador se suscribe a un Observable caliente, recibe algunas emisiones,
y luego entra otro Observador, ese segundo Observador se habrá perdido esas emisiones.
Al igual que una estación de radio, si sintonizas demasiado tarde, te habrás perdido esa canción.

```java
import io.reactivex.Observable;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MyJavaFxApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ToggleButton toggleButton = new ToggleButton("TOGGLE ME");
        Label label = new Label();
        Observable<Boolean> selectedStates =
                valuesOf(toggleButton.selectedProperty());
        selectedStates.map(selected -> selected ? "DOWN" : "UP")
                .subscribe(label::setText);
        VBox vBox = new VBox(toggleButton, label);
        stage.setScene(new Scene(vBox));
        stage.show();
    }

    private static <T> Observable<T> valuesOf(final ObservableValue<T> fxObservable) {
        return Observable.create(observableEmitter -> {
            //emit initial state
            observableEmitter.onNext(fxObservable.getValue());
            //emit value changes uses a listener
            final ChangeListener<T> listener = (observableValue, prev,
                                                current) -> observableEmitter.onNext(current);
            fxObservable.addListener(listener);
        });
    }

}
```

#### ConnectableObservable

Una forma útil de Observable caliente es
conectableObservable. Tomará cualquier Observable, incluso si hace frío, y lo hará caliente
para que todas las emisiones se reproduzcan a todos los Observadores a la vez.
Para hacer esta conversión, simplemente necesita llamar a publish() en cualquier Observable,
y producirá un ConnectableObservable. Pero la suscripción aún no iniciará las emisiones.
Debe llamar a su método connect() para comenzar a disparar las emisiones.
Esto le permite configurar todos sus observadores de antemano.
Echa un vistazo al siguiente fragmento de código:

```java
import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;

public class Launcher {
    public static void main(String[] args) {
        ConnectableObservable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                        .publish();
        //Set up observer 1
        source.subscribe(s -> System.out.println("Observer 1: " + s));
        //Set up observer 2
        source.map(String::length)
                .subscribe(i -> System.out.println("Observer 2: " + i));
        //Fire!
        source.connect();
    }
}
```
```
 Observer 1: Alpha
 Observer 2: 5
 Observer 1: Beta
 Observer 2: 4
 Observer 1: Gamma
 Observer 2: 5
 Observer 1: Delta
 Observer 2: 5
 Observer 1: Epsilon
 Observer 2: 7
```