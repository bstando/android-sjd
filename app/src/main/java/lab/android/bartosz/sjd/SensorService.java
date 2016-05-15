package lab.android.bartosz.sjd;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SensorService extends Service {
    private Handler handler = new Handler();
    private SensorDataDbHelper sensorDataDbHelper;
    private NsdHelper nsdHelper;
    private boolean searching = false;

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        SensorService getService() {
            return SensorService.this;
        }
    }

    public void setHelpers(NsdHelper nsdHelper1, SensorDataDbHelper sensorDataDbHelper1) {
        this.nsdHelper = nsdHelper1;
        this.sensorDataDbHelper = sensorDataDbHelper1;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public List<SensorData> getFromDatabase() {
        return sensorDataDbHelper.retriveAllData();
    }

    public void insertToDatabase(SensorData data) {
        sensorDataDbHelper.insertData(data);
    }

    public void startSearching() {
        if (!searching) {
            nsdHelper.discoverServices();
            searching = true;
        }
    }

    public void stopSearching() {
        if (searching) {
            nsdHelper.stopDiscovery();
            searching = false;
        }
    }

    public Map<InetAddress, Integer> getClients() {
        return nsdHelper.getAvailableConnections();
    }


    private class DownloadData extends AsyncTask<Pair<InetAddress,Integer>, Void,SensorData>
    {
        private SensorData data;
        protected DownloadData(SensorData sensorData)
        {
            data = sensorData;
        }

        @Override
        protected SensorData doInBackground(Pair<InetAddress,Integer>... pairs)
        {
            InetAddress address = pairs[0].first;
            Integer port = pairs[0].second;
            SensorData sensorData = new SensorData();
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
            }

            data = sensorData;
            Log.d("TASK",sensorData.toString());
            return sensorData;
        }

        @Override
        protected void onPostExecute(SensorData sensorData)
        {
            sensorDataDbHelper.insertData(sensorData);
            Toast.makeText(getApplicationContext(),sensorData.toString(),Toast.LENGTH_LONG).show();
        }

    }
    
    public SensorData getDataFromDevice(InetAddress address, int port)
    {
        SensorData sensorData = new SensorData();
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket(port);
            JSONObject json = new JSONObject();
            json.put("id", 2);
            String message = json.toString();
            Log.d("SENSOR:", message);
            DatagramPacket sendDatagramPacket = new DatagramPacket(message.getBytes(), message.length(), address, port);
            datagramSocket.send(sendDatagramPacket);
            byte[] bMsg = new byte[256];
            DatagramPacket reciveDatagramPacket = new DatagramPacket(bMsg, bMsg.length);
            datagramSocket.receive(reciveDatagramPacket);
            Log.d("TASK:", new String(bMsg));
            Log.d("LENGTH:", String.valueOf(bMsg.length));
            String jsonString = new String(bMsg, 0, reciveDatagramPacket.getLength());
            JSONObject retJson = new JSONObject(jsonString);
            sensorData.setDate(new Date());
            sensorData.setTemperature((float) retJson.getDouble("temperature"));
            sensorData.setHumidity((float) retJson.getDouble("humidity"));
            datagramSocket.close();
            insertToDatabase(sensorData);

        } catch (SocketException ex) {
            Log.e("ERROR:", ex.getMessage());
        } catch (IOException e) {
            Log.e("ERROR:", e.getMessage());
        } catch (JSONException ex) {
            Log.e("ERROR:", ex.getMessage());
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }
        return sensorData;
        
    }

    public SensorData getDataFromSensor(InetAddress address, int port) {
        SensorData sensorData = new SensorData();
        DownloadData data = new DownloadData(sensorData);
        data.execute(new Pair<InetAddress, Integer>(address,port));
        sensorData = data.data;
        return sensorData;
        //return new DownloadData().doInBackground();

    }

    public SensorData runSomeTest(final InetAddress address, final int port)
    {
        final SensorData sensorData = new SensorData();
        handler.post(new Runnable() {
            @Override
            public void run() {
                DatagramSocket dout = null;
                try {
                    dout = new DatagramSocket(port);
                    JSONObject json = new JSONObject();
                    json.put("id", 2);
                    String message = json.toString();
                    Log.d("SENSOR:", message);
                    DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), address, port);
                    dout.send(dp);
                    byte[] bMsg = new byte[256];
                    DatagramPacket datagramPacket = new DatagramPacket(bMsg, bMsg.length);
                    dout.receive(datagramPacket);
                    Log.d("TASK:", new String(bMsg));
                    Log.d("LENGTH:", String.valueOf(bMsg.length));
                    String jsonString = new String(bMsg, 0, datagramPacket.getLength());
                    JSONObject retJson = new JSONObject(jsonString);
                    sensorData.setDate(new Date());
                    sensorData.setTemperature((float) retJson.getDouble("temperature"));
                    sensorData.setHumidity((float) retJson.getDouble("humidity"));
                    dout.close();
                    insertToDatabase(sensorData);

                } catch (SocketException ex) {
                    Log.e("ERROR:", ex.getMessage());
                } catch (IOException e) {
                    Log.e("ERROR:", e.getMessage());
                } catch (JSONException ex) {
                    Log.e("ERROR:", ex.getMessage());
                } finally {
                    if (dout != null) {
                        dout.close();
                    }
                }
            }
        });
        return sensorData;
    }


}
