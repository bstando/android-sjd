package lab.android.bartosz.sjd;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    protected SensorService sensorService;
    protected boolean bounded = false;

    SensorDataDbHelper sensorDataDbHelper;
    NsdHelper nsdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorDataDbHelper = new SensorDataDbHelper(getApplicationContext());
        nsdHelper = new NsdHelper(getApplicationContext());
        nsdHelper.initializeNsd();

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Intent intent = new Intent(this,SensorService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(bounded)
        {
            unbindService(connection);
            bounded = false;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SensorService.LocalBinder binder = (SensorService.LocalBinder) service;
            sensorService = binder.getService();
            bounded=true;
            sensorService.setHelpers(nsdHelper,sensorDataDbHelper);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //sensorService.stopSearching();
            bounded = false;
        }
    };

    public void generateData(View view)
    {
        SensorData sensorData = new SensorData();
        sensorData.setTemperature((float) 12.0);
        sensorData.setHumidity((float) 33.0);
        sensorData.setDate(new Date());
        sensorService.insertToDatabase(sensorData);
    }

    public void retrieveData(View view)
    {
        sensorService.startSearching();
        List<SensorData> list = sensorService.getFromDatabase();
        ArrayAdapter<SensorData> adapter = new ArrayAdapter<SensorData>(this,android.R.layout.simple_list_item_1,list);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        Map<InetAddress,Integer> map = sensorService.getClients();
        Set<InetAddress> set = map.keySet();

        for(InetAddress inetAddress :set)
        {
            Log.d("MAIN",inetAddress.toString()+" "+map.get(inetAddress));

            //SensorData sensorData = sensorService.runSomeTest(inetAddress,map.get(inetAddress));
            //SensorData sensorData = sensorService.getDataFromDevice(inetAddress,map.get(inetAddress));
            SensorData sensorData = sensorService.getDataFromSensor(inetAddress,map.get(inetAddress));
            /*SensorData sensorData = new SensorData();
            InetAddress address = inetAddress;
            Integer port = map.get(inetAddress);
            DatagramSocket dout = null;
            try {
                dout = new DatagramSocket(port);
                JSONObject json = new JSONObject();
                json.put("id", 2);
                String message = json.toString();
                Log.d("SENSOR:",message);
                DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), address, port);
                dout.send(dp);
                byte[] bMsg = new byte[256];
                DatagramPacket datagramPacket = new DatagramPacket(bMsg,bMsg.length);
                dout.receive(datagramPacket);
                Log.d("TASK:",new String(bMsg));
                Log.d("LENGTH:",String.valueOf(bMsg.length));
                String jsonString = new String(bMsg,0,datagramPacket.getLength());
                JSONObject retJson = new JSONObject(jsonString);
                sensorData.setDate(new Date());
                sensorData.setTemperature((float)retJson.getDouble("temperature"));
                sensorData.setHumidity((float)retJson.getDouble("humidity"));
                dout.close();

            } catch (SocketException ex) {
                Log.e("ERROR:", ex.getMessage());
            } catch (IOException e)
            {
                Log.e("ERROR:", e.getMessage());
            } catch (JSONException ex)
            {
                Log.e("ERROR:", ex.getMessage());
            }                finally {
                if (dout != null) {
                    dout.close();
                }
            }*/
            //Toast.makeText(getApplicationContext(),sensorData.toString(),Toast.LENGTH_LONG).show();

        }


    }
}
