package ru.nsu.fit.BusinessLogic.CallBack;

public class Notification {
    Wiretap wiretap = null;

    public void setWiretap(Wiretap wiretap) {
        this.wiretap=wiretap;
    }

    public void notifyWiretap() {
        if (wiretap!=null) {
            wiretap.update();
        }
    }

}
