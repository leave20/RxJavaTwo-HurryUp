# Operadores de transformación

## map()

Para un Observable<T> dado, el operador map() transformará una emisión T en una emisión R
utilizando la Function<T, R> lambda proporcionada.
Ya hemos usado este operador muchas veces, convirtiendo cadenas en longitudes.
Aquí hay un nuevo ejemplo: podemos tomar cadenas de fecha sin procesar y usar el operador map()
para convertir cada una en una emisión de LocalDate, como se muestra en el siguiente fragmento de código:

````java

import io.reactivex.Observable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Launcher {
    public static void main(String[] args) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/ yyyy");
        Observable.just("1/3/2016", "5/9/2016", "10/12/2016")
                .map(s -> LocalDate.parse(s, dtf))
                .subscribe(i -> System.out.println("RECEIVED: " + i));
    }
}
````

````
RECEIVED: 2016-01-03
RECEIVED: 2016-05-09
RECEIVED: 2016-10-12
````

## cast()

Un operador simple similar a un map para convertir cada emisión a un tipo diferente es cast().
Si queremos tomar Observable<String> y lanzar cada emisión a un objeto (y devolver un Observable<Object>),
podríamos usar el operador map() de esta manera:

```java
import io.reactivex.Observable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Launcher {
    public static void main(String[] args) {
        Observable<Object> items=
                Observable.just("Alpha","Beta","Gamma").map(s->(Object)s);
    }
}

```
