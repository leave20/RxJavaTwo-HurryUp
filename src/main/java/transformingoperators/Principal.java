package transformingoperators;



import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class Principal {
    public static void main(String[] args) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");
        Observable.just("1/3/2016", "5/9/2016", "10/12/2016")
                .map(s -> LocalDate.parse(s, dtf))
                .subscribe(i -> log.info("RECEIVED: " + i));
    }




//    DateTimeFormatter dtftwo = DateTimeFormatter.ofPattern("M/d
//                    / yyyy");
//            Observable.just("1/3/2016", "5/9/2016", "10/12/2016")
//                    .map(s -> LocalDate.parse(s, dtf))
//                    .subscribe(i -> System.out.println("RECEIVED: " + i));
}
