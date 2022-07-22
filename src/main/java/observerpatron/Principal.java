package observerpatron;



public class Principal {
    public static void main(String[] args) {
// A Acelerator --> B (Motor)
//motor observa-> acelerador, observador y el sujeto (Observable)
//motor es observador
//acelerador es observado(sujeto)

        Motor motorV8 = new Motor();
        Acelerator acelerador = new Acelerator();

        acelerador.enlazarObserver(motorV8);
        acelerador.pisarAcelator();

    }
}
