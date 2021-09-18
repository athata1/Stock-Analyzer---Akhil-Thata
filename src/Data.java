public class Data {
    private String[] date;
    private double closeValue;
    private int day;
    private int month;
    private int year;
    static double maxCloseValue = 0;
    public Data(String[] data)
    {
        date = data[0].split("-");
        year = Integer.parseInt(date[0]);
        month = Integer.parseInt(date[1]);
        day = Integer.parseInt(date[2]);
        closeValue = Double.parseDouble(data[4]);
        maxCloseValue = Math.max(maxCloseValue,closeValue);
    }
    public String[] getDate()
    {
        return date;
    }
    public double getCloseVal()
    {
        return closeValue;
    }
    public int getYear()
    {
        return year;
    }
    public int getMonth()
    {
        return month;
    }
    public int getDay()
    {
        return day;
    }
}
