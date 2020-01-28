package ca.bcit.ham_makasoff;

import java.util.Date;

public class BloodPressure {

    private String id;
    private String userID;
    private double systolic;
    private double diastolic;
    private Date date;
    private Date time;
    private String condition;

    public BloodPressure() {}

    public BloodPressure(String id, String userID, double systolic, double diastolic, Date date, Date time) {
        this.id = id;
        this.userID = userID;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.date = date;
        this.time = time;
        this.condition = calculateCondition();
    }

    private String calculateCondition() {
        if (systolic < 120 && diastolic < 80) {
            return "Normal";
        } else if (systolic >= 120 && systolic < 130 && diastolic < 80) {
            return "Elevated";
        } else if ((systolic >= 130 && systolic < 140) || (diastolic >= 80 && diastolic < 90)) {
            return "High blood pressure (stage 1)";
        } else if (systolic >= 180 || diastolic >= 120) {
            return "Hypertensive crisis";
        } else if (systolic >= 140 || diastolic >= 90) {
            return "High blood pressure (stage 2)";
        }
        return null;
    }

    public String getUserID() { return userID; }

    public void setUserID(String userID) { this.userID = userID; }

    public double getSystolic() { return systolic; }

    public void setSystolic(double systolic) { this.systolic = systolic; }

    public double getDiastolic() { return diastolic; }

    public void setDiastolic(double diastolic) { this.diastolic = diastolic; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public Date getTime() { return time; }

    public void setTime(Date time) { this.time = time; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getCondition() { return condition; }

    public void setCondition(String condition) { this.condition = condition; }
}
