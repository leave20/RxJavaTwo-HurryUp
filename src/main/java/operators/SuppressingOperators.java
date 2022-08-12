package operators;

import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@Slf4j
public class SuppressingOperators{

    public static void main(String[] args) throws InterruptedException {
        Observable.interval(300, TimeUnit.MILLISECONDS)
                .take(2, TimeUnit.SECONDS)
                .subscribe(i -> log.info("RECEIVED: " + i));
        sleep(5000);
    }



}
