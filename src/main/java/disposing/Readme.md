# Disposing

Cuando se suscribe a un Observable para recibir emisiones, se crea un flujo para procesar estas emisiones
a través de la cadena Observable.
Por supuesto, esto consume recursos.
Cuando hayamos terminado, queremos disponer de estos recursos para que puedan ser recogidos por el garbage collector.
Afortunadamente, los Observables finitos que llaman a onComplete() generalmente se desharán de sí mismos
de manera segura cuando terminen.

Pero si está trabajando con Observables infinitos o de larga ejecución, es probable que se encuentre
con situaciones en las que desee detener explícitamente las emisiones y disponer de todo lo asociado a esa suscripción.
De hecho, no puede confiar en que el recolector de elementos no utilizados se encargue de las suscripciones activas
que ya no necesita, y es necesaria una eliminación explícita para evitar fugas de memoria.

El Disposable es un vínculo entre un Observable y un Observador activo, y puede llamar a su método dispose()
para detener las emisiones y deshacerse de todos los recursos usados para ese Observador.
También tiene un método isDisposed(), que indica si ya se ha eliminado:

````java
package io.reactivex.disposables;

public interface Disposable {
    void dispose();

    boolean isDisposed();
}

````

Cuando proporciona lambdas onNext(), onComplete() y/o onError() como argumentos para el método subscribe(),
en realidad devolverá un Desechable. Puede usar esto para detener las emisiones en cualquier momento llamando
a su método dispose().
Por ejemplo, podemos dejar de recibir emisiones de un Observable.interval() después de cinco segundos:

````java
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.TimeUnit;

public class Launcher {
    public static void main(String[] args) {
        Observable<Long> seconds =
                Observable.interval(1, TimeUnit.SECONDS);
        Disposable disposable =
                seconds.subscribe(l -> System.out.println("Received: " + l));
        //sleep 5 seconds
        sleep(5000);
        //dispose and stop emissions
        disposable.dispose();
        //sleep 5 seconds to prove
        //there are no more emissions
        sleep(5000);
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

````