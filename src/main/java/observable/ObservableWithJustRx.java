package observable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObservableWithJustRx {
    public static void main(String[] args) {

        observableJustFirstSample();
        observableJustSecondSample();
        observableJustThirdSample();
        observableJustFourthSample();
    }

    static void observableJustFirstSample() {
        Observable<String> source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        source.subscribe(
                log::info,
                Throwable::printStackTrace,
                () -> log.info("COMPLETED")
        );
    }

    static void observableJustSecondSample() {
        String greeting = "Hello world!";

        Observable<String> observable = Observable.just(greeting);

        observable.subscribe(log::info);

    }

    static void observableJustThirdSample() {
        Observable<Object> observable = Observable.just("1", "A", "3.2", "def");

        observable.subscribe(
                item->log.info("{}", item),
                Throwable::printStackTrace,
                () -> log.info("COMPLETED")
        );
    }

    static void observableJustFourthSample() {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon");
        Observer<Integer> myObserver = new Observer<>() {
            @Override
            public void onSubscribe(Disposable d) {
                //do nothing with Disposable, disregard for now
            }

            @Override
            public void onNext(@NonNull Integer value) {
                log.info("RECEIVED: " + value);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                log.info("Done!");
            }
        };
        source.map(String::length).filter(i -> i >= 5)
                .subscribe(myObserver);
    }

}
