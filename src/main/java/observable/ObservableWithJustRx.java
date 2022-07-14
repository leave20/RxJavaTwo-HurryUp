package observable;

import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObservableWithJustRx {
    public static void main(String[] args) {

        observableJustFirstSample();
        observableJustSecondSample();
        observableJustThirdSample();
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

}
