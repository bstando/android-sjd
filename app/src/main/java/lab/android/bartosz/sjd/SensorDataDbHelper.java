package lab.android.bartosz.sjd;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

public class SensorDataDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SensorData.db";

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static final String FLOAT_TYPE = " FLOAT";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SensorDataContract.SensorDataCol.TABLE_NAME + " ("
            + SensorDataContract.SensorDataCol._ID + " INTEGER PRIMARY KEY,"
            + SensorDataContract.SensorDataCol.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP
            + SensorDataContract.SensorDataCol.COLUMN_NAME_TEMPERATURE + FLOAT_TYPE + COMMA_SEP
            + SensorDataContract.SensorDataCol.COLUMN_NAME_HUMIDITY  + FLOAT_TYPE
            + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SensorDataContract.SensorDataCol.TABLE_NAME;


    public SensorDataDbHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertData(SensorData data)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SensorDataContract.SensorDataCol.COLUMN_NAME_DATE,dateFormat.format(data.getDate()));
        values.put(SensorDataContract.SensorDataCol.COLUMN_NAME_HUMIDITY,data.getHumidity());
        values.put(SensorDataContract.SensorDataCol.COLUMN_NAME_TEMPERATURE,data.getTemperature());
        db.insert(SensorDataContract.SensorDataCol.TABLE_NAME, null,values);
    }

    public List<SensorData> retriveAllData()
    {

        List<SensorData> sensorDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                SensorDataContract.SensorDataCol._ID,
                SensorDataContract.SensorDataCol.COLUMN_NAME_DATE,
                SensorDataContract.SensorDataCol.COLUMN_NAME_TEMPERATURE,
                SensorDataContract.SensorDataCol.COLUMN_NAME_HUMIDITY
        };

        String sortOrder =
                SensorDataContract.SensorDataCol.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = db.query(SensorDataContract.SensorDataCol.TABLE_NAME,projection,null,null,null,null,sortOrder);
        while (cursor.moveToNext())
        {
            SensorData sensorData = new SensorData();
            sensorData.setId(cursor.getLong(0));
            try {
                sensorData.setDate(dateFormat.parse(cursor.getString(1)));
            } catch (ParseException ex)
            {
                sensorData.setDate(null);
            }
            sensorData.setHumidity(cursor.getFloat(2));
            sensorData.setTemperature(cursor.getFloat(3));
            sensorDataList.add(sensorData);

        }
        cursor.close();
        return sensorDataList;

    }

}
