package completable;

import io.reactivex.Completable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Principal {
    public static void main(String[] args) {
        Completable.fromRunnable(Principal::runProcess)
                .subscribe(() -> log.info("Done!"));
    }

    public static void runProcess() {
        //run process here
        log.info("Processing...");

    }
}
