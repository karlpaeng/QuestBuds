package app.questbuds.notifs;

public class TimeInterval {
    int hrStart, minStart, hrEnd, minEnd;

    public TimeInterval() {
    }

    public TimeInterval(int hrStart, int minStart, int hrEnd, int minEnd) {
        this.hrStart = hrStart;
        this.minStart = minStart;
        this.hrEnd = hrEnd;
        this.minEnd = minEnd;
    }

    @Override
    public String toString() {
        return "TimeInterval{" +
                "hrStart=" + hrStart +
                ", minStart=" + minStart +
                ", hrEnd=" + hrEnd +
                ", minEnd=" + minEnd +
                '}';
    }
}
