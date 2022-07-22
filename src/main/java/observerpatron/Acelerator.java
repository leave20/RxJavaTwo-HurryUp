package observerpatron;


import java.util.ArrayList;

public class Acelerator implements Observable{

    private final ArrayList<Observer> observers;

    public Acelerator() {
        observers=new ArrayList<>();
    }
    public void pisarAcelator() {
        //subir la potencia del motor
        notification();
    }
    public void enlazarObserver(Observer observer) {
        observers.add(observer);
    }
    @Override
    public void notification() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
