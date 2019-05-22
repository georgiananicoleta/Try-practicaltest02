package ro.pub.cs.systems.eim.practicaltest02.model;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;

public class TimeInformation {

    private String time;
    private String date;

    public TimeInformation() {
        this.time = null;
        this.date = null;
    }

    public TimeInformation(
            String time,
            String date) {
        this.time = time;
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return Constants.TIME+ ": " + time + "\n\r" +
                Constants.DATE + ": " + date ;
    }

}