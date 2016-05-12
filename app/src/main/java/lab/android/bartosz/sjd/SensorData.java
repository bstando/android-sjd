package lab.android.bartosz.sjd;


import java.util.Date;

public class SensorData {
    private long id;
    private float temperature;
    private float humidity;
    private Date date;

    public SensorData() {
    }

    public long getId() {
        return id;
    }

    public float getTemperature()
    {
        return temperature;
    }

    public float getHumidity()
    {
        return humidity;
    }

    public Date getDate()
    {
        return date;
    }

    public void setId(long value)
    {
        this.id = value;
    }

    public void setTemperature(float value)
    {
        this.temperature = value;
    }

    public void setHumidity(float value)
    {
        this.humidity = value;
    }

    public void setDate(Date value)
    {
        this.date = value;
    }

    @Override
    public String toString()
    {
        return "Date: " + date.toString() + ", Temperature: " + temperature + ", Humidity: " + humidity;
    }
}
