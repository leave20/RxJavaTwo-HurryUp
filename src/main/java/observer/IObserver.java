/* */
package observer;

import io.reactivex.disposables.Disposable;

/**
 * Este es un ejemplo de un observer que se puede usar para observar un observable.
 */
public interface IObserver<T> {
    void onSubscribe(Disposable disposable);
    void onNext(T t);
    void onError(Throwable throwable);
    void onComplete();



}
