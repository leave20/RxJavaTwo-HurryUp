package maybe;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Principal {
    public static void main(String[] args) {

        Maybe<String> source = Maybe.just("Alpha");
        source.subscribe(
                s -> log.info("onNext: {}", s),
                e -> log.error("onError: ", e),
                () -> log.info("onComplete"));

        Observable<String> sourceTwo = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        sourceTwo.firstElement().subscribe(
                s -> log.info("RECEIVED " + s),
                Throwable::printStackTrace,
                () -> log.info("Done!"));

    }
}
