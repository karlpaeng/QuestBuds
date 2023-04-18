package app.questbuds.notifs;

public class ModelNotifs {
    public String notifId, notifText;
    public int hourNotif, minNotif;

    public ModelNotifs() {
    }

    public ModelNotifs(String notifId, String notifText, int hourNotif, int minNotif) {
        this.notifId = notifId;
        this.notifText = notifText;
        this.hourNotif = hourNotif;
        this.minNotif = minNotif;
    }

    @Override
    public String toString() {
        return "ModelNotifs{" +
                "notifId='" + notifId + '\'' +
                ", notifText='" + notifText + '\'' +
                ", hourNotif=" + hourNotif +
                ", minNotif=" + minNotif +
                '}';
    }
}
