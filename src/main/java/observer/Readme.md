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
Simplemente, deje su implementación vacía hasta que lo discutamos al final de este capítulo:

