package disposing;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
@Slf4j
public class Principal {
    public static void main(String[] args) {
        Observable<Long> seconds =
                Observable.interval(1, TimeUnit.SECONDS);
        Disposable disposable =
                seconds.subscribe(l -> log.info("Received: " + l));
        //sleep 5 seconds
        sleep(5000);
        //dispose and stop emissions
        disposable.dispose();


    }
    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
