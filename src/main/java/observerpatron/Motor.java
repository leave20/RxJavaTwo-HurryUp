package observerpatron;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Motor implements Observer{

    public Motor() {
        log.info("Motor creado");
    }

    @Override
    public void update() {
        log.info("Subir la potencia, subir velocidad/revolución");
    }
}
