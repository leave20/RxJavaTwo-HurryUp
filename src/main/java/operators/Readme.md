# Operadores de Suppression

## filer()

El operador filter() acepta Predicate<T> para un Observable<T> determinado.
Esto significa que le proporciona una lambda que califica cada emisión asignándola a un valor booleano,
y las emisiones con `false` no avanzarán.

Por ejemplo, puede usar filter() para permitir solo emisiones de cadenas que no tengan cinco caracteres de longitud:

````java
import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                .filter(s -> s.length() != 5);
        subscribe(s -> System.out.println("RECEIVED: " + s));
    }
}
````

````
RECEIVED: Beta
RECEIVED: Epsilon
````

## take()

El operador take() tiene dos sobrecargas. Uno tomará un número específico de emisiones y luego llamará a onComplete()
después de que las capture todas.
También dispondrá de la totalidad de la suscripción para que no se produzcan más emisiones.

Por ejemplo, take(3) emitirá las primeras tres emisiones y luego llamará al evento onComplete():

````java

import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                .take(3)
                .subscribe(s -> System.out.println("RECEIVED: " + s));
    }
}
````

````
 RECEIVED: Alpha
 RECEIVED: Beta
 RECEIVED: Gamma
````

La otra sobrecarga tomará emisiones dentro de un período de tiempo específico y luego llamará a onComplete().
Por supuesto, nuestro Observable frío aquí emitirá tan rápido que serviría como un mal ejemplo para este caso.
Tal vez un mejor ejemplo sería usar una función Observable.interval(). Emitamos cada 300 milisegundos, pero emitamos()
por solo 2 segundos en el siguiente fragmento de código:

````java
import io.reactivex.Observable;

import java.util.concurrent.TimeUnit;

public class Launcher {
    public static void main(String[] args) {
        Observable.interval(300, TimeUnit.MILLISECONDS)
                .take(2, TimeUnit.SECONDS)
                .subscribe(i -> System.out.println("RECEIVED: " + i));
        sleep(5000);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
````

````
RECEIVED: 0
RECEIVED: 1
RECEIVED: 2
RECEIVED: 3
RECEIVED: 4
RECEIVED: 5
````

## skip()

El operador skip() hace lo contrario del operador take(). Ignorará el número especificado de emisiones y
luego emitirá las siguientes. Si quisiera omitir las primeras 90 emisiones de un Observable,
podría usar este operador, como se muestra en el siguiente fragmento de código:

````java
import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable.range(1, 100)
                .skip(90)
                .subscribe(i -> System.out.println("RECEIVED: " + i));
    }
}
````

````
RECEIVED: 91
RECEIVED: 92
RECEIVED: 93
RECEIVED: 94
RECEIVED: 95
RECEIVED: 96
RECEIVED: 97
RECEIVED: 98
RECEIVED: 99
RECEIVED: 100
````

## takeWhile() & skipWhile()

Otra variante del operador take() es el operador takeWhile(), que toma
emisiones mientras que una condición derivada de cada emisión es verdadera.
El siguiente ejemplo seguirá tomando emisiones mientras las emisiones sean inferiores a 5.
En el momento en que encuentre una que no lo sea, llamará a la función onComplete() y la eliminará:

````java
import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable.range(1, 100)
                .takeWhile(i -> i < 5)
                .subscribe(i -> System.out.println("RECEIVED: " + i));
    }
}
````

````
RECEIVED: 1
RECEIVED: 2
RECEIVED: 3
RECEIVED: 4

````

Al igual que la función takeWhile(), hay una función skipWhile().
Seguirá omitiendo emisiones mientras califican con una condición.
En el momento en que esa condición ya no califique, las emisiones comenzarán a pasar.
En el siguiente código, omitimos las emisiones siempre que sean menores o iguales a 95.
En el momento en que se encuentra una emisión que no cumple con esta condición, permitirá todas las
emisiones posteriores en el futuro:

````java
import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable.range(1, 100)
                .skipWhile(i -> i <= 95)
                .subscribe(i -> System.out.println("RECEIVED: " + i));
    }
}
````

````
RECEIVED: 96
RECEIVED: 97
RECEIVED: 98
RECEIVED: 99
RECEIVED: 100
````

## distinct()

El operador distinct() emitirá cada emisión única, pero suprimirá cualquier duplicado que siga.
La igualdad se basa en la implementación hashCode()/equals() de los objetos emitidos.
Si quisiéramos emitir las distintas longitudes de una secuencia de cadenas, podría hacerse de la siguiente manera:

````java

import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon")
                .map(String::length)
                .distinct()
                .subscribe(i -> System.out.println("RECEIVED: " + i));
    }
}

````

````
RECEIVED: 5
RECEIVED: 4
RECEIVED: 7
````

### Nota

Tenga en cuenta que si tiene un espectro amplio y diverso de valores únicos, distint() puede usar un poco de memoria.
Imagine que cada suscripción da como resultado un HashSet que realiza un seguimiento de los valores únicos capturados
previamente.
También puede agregar un argumento lambda que asigna cada emisión a una clave utilizada para la lógica de igualdad.
Esto permite que las emisiones, pero no la clave, avancen mientras se usa la clave para una lógica distinta.
Por ejemplo, podemos eliminar la longitud de cada cadena y usarla como unicidad, pero emitiendo las cadenas
en lugar de sus longitudes:

````java

import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon")
                .distinct(String::length)
                .subscribe(i -> System.out.println("RECEIVED: " + i));
    }
}
````

````
RECEIVED: Alpha
RECEIVED: Beta
RECEIVED: Epsilon
````

## distinctUntilChanged()

La función ignorará las emisiones consecutivas duplicadas. Es una forma útil de ignorar las repeticiones hasta que
cambien.
Si el mismo valor se emite repetidamente, todos los duplicados se ignorarán hasta que se emita un nuevo valor.
Los duplicados del siguiente valor se ignorarán hasta que vuelva a cambiar, y así sucesivamente.
Observe el resultado del siguiente código para ver este comportamiento en acción:

````java

import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable.just(1, 1, 1, 2, 2, 3, 3, 2, 1, 1)
                .distinctUntilChanged()
                .subscribe(i -> System.out.println("RECEIVED: " + i));
    }
}
````

````
RECEIVED: 1
RECEIVED: 2
RECEIVED: 3
RECEIVED: 2
RECEIVED: 1
````

## elementAt()

Puede obtener una emisión específica por su índice especificado por Long, comenzando en 0.
Después de encontrar y emitir ese elemento, se llamará a onComplete() y se eliminará la suscripción.
Si desea que la cuarta emisión provenga de un Observable, puede hacerlo como se muestra en el siguiente fragmento de
código:

````java

import io.reactivex.Observable;

public class Launcher {
    public static void main(String[] args) {
        Observable.just("Alpha", "Beta", "Zeta", "Eta", "Gamma",
                        "Delta")
                .elementAt(3)
                .subscribe(i -> System.out.println("RECEIVED: " + i));
    }
}
````
````
RECEIVED: Eta
````