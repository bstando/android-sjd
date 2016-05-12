package lab.android.bartosz.sjd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SensorDataDbHelper sensorDataDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorDataDbHelper = new SensorDataDbHelper(getApplicationContext());
    }

    public void generateData(View view)
    {
        SensorData sensorData = new SensorData();
        sensorData.setTemperature((float) 12.0);
        sensorData.setHumidity((float) 33.0);
        sensorData.setDate(new Date());
        sensorDataDbHelper.insertData(sensorData);
    }

    public void retriveData(View view)
    {
        List<SensorData> list = sensorDataDbHelper.retriveAllData();
        ArrayAdapter<SensorData> adapter = new ArrayAdapter<SensorData>(this,android.R.layout.simple_list_item_1,list);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
