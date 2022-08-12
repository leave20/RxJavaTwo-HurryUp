# Completable

Completable se refiere simplemente a la ejecución de una acción, pero no recibe ninguna emisión.
Lógicamente, no tiene onNext() ni onSuccess() para recibir emisiones, pero sí onError() y onComplete():

````java
interface CompletableObserver<T> {
    void onSubscribe(Disposable d);

    void onComplete();

    void onError(Throwable error);
}
````

Completable es algo que probablemente no usará con frecuencia.
Puede construir uno rápidamente llamando a Completable.complete() o Completable.fromRunnable().
El primero llamará inmediatamente a onComplete() sin hacer nada, mientras que fromRunnable()
ejecutará la acción especificada antes de llamar a onComplete():

````java

import io.reactivex.Completable;

public class Launcher {
    public static void main(String[] args) {
        Completable.fromRunnable(() -> runProcess())
                .subscribe(() -> System.out.println("Done!"));
    }

    public static void runProcess() {
        //run process here
    }
}
````

