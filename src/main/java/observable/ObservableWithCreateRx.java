/* */
package observable;


import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * But what if we need to cancel the asynchronous task in the RxJava 2 chain before it is finished?
 * This is so important for Android, as our Activity could get killed any time, and we want the RxJava 2 task in it,
 * to be cancel if the Activity ever got killed… So we definitely need a way.
 * <p><b>Disposable …</p></b>
 * The good news is, this is pretty simple here. It is using something call {@code .Disposable}
 * <p>
 * Whenever we create a RxJava 2 chain, and to it, a is return. e.g.{@code subscribe disposable}
 */

@Slf4j
public class ObservableWithCreateRx {

    public static void main(String[] args) throws InterruptedException {
        observableCreateFirstSample();
        observableCreateSecondSample();

    }

    static void observableCreateFirstSample() {
        Observable<String> source = Observable.create(emitter -> {
            emitter.onNext("Alpha");
            emitter.onNext("Beta");
            emitter.onNext("Gamma");
            emitter.onNext("Delta");
            emitter.onNext("Epsilon");
            emitter.onComplete();
        });
        final Disposable subscription = source.subscribe(
                s -> log.info("RECEIVED: {} ", s)
        );
        subscription.dispose();
    }


    static void observableCreateSecondSample() throws InterruptedException {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        ObservableOnSubscribe<String> handler = emitter -> {

            Future<Object> future = executor.schedule(() -> {
                emitter.onNext("Hello");
                emitter.onNext("World");
                emitter.onComplete();
                return null;
            }, 1, TimeUnit.SECONDS);

            emitter.setCancellable(() -> future.cancel(false));
        };

        Observable<String> observable = Observable.create(handler);

        observable.subscribe(
                log::info,
                Throwable::printStackTrace,
                () -> log.info("Done")
        );

        Thread.sleep(2000);
        executor.shutdown();
    }


}

