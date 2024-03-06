package com.example.greenhousecontrol;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemClickListener,
        View.OnClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_CODE_LOC = 1;

    private static final int REQ_ENABLE_BT  = 10;
    public static final int BT_BOUNDED      = 21;
    public static final int BT_SEARCH       = 22;

    public static final int LED_RED         = 30;
    public static final int LED_GREEN       = 31;

    private ConstraintLayout connectLayout;
    private FrameLayout frameMessage;
    private LinearLayout frameControls;
    private Button btnDisconnect;
    private Switch switchEnableBt;
    private Button btnEnableSearch;
    private ProgressBar pbProgress;
    private ListView listBtDevices;
    ImageView backFromBtConnection;

    private ConstraintLayout mainMenu;
    private Button personalSettings;
    private Button bdSettings;
    private Button btConnection;
    private Button getData;

    private ConstraintLayout bdLayout;
    private ImageView sqaButton;
    private ImageView cucButton;
    private ImageView pepButton;
    private ImageView tomButton;
    ImageView backFromChoiceVeg;

    private ConstraintLayout getDataLayout;
    private Button getTempDay;
    private Button getTempNight;
    private Button getHumidity;
    private ImageView backFromStats;



    private ConstraintLayout choiceLayout;
        ConstraintLayout listPlants;
     ListView listView;
     ImageView backFromListPlants;
        ConstraintLayout readyPlant;
    TextView bdNamePlant;
    TextView bdType;
    TextView bdTempDay;
    TextView bdTempNight;
    TextView bdHumidity;
    TextView bdFrequency;
    Button bdSaveChanges;
    ImageView bdPicture;
    ImageView backFromReadyPlantChoice;

    ConstraintLayout personalLayout;
    TextView inTempDay;
    TextView inTempNight;
    TextView inHumidity;
    TextView inFrequency;
    Button inSaveChanges;
    ImageView backFromPersonalLayout;




    private BluetoothAdapter bluetoothAdapter;
    private BtListAdapter listAdapter;
    private ArrayList<BluetoothDevice> bluetoothDevices;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    Intent intent;
    ArrayAdapter<String> adapter;
    Cursor cursor;

    List<String> list;
    List<Integer> idList;
    List<Integer> groupList;
    String type;

    String tempDay;
    String tempNight;
    String humidity;
    String frequency;
    String inputData;

    GraphView graphView;

    String str = "";
    int count = -1;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectLayout = findViewById(R.id.bt_settings_layout);
        frameMessage = findViewById(R.id.frame_message);
        frameControls = findViewById(R.id.frame_control);
        switchEnableBt = findViewById(R.id.switch_enable_bt);
        btnEnableSearch = findViewById(R.id.btn_enable_search);
        pbProgress = findViewById(R.id.pb_progress);
        listBtDevices = findViewById(R.id.lv_bt_device);
        btnDisconnect = findViewById(R.id.btn_disconnect);
        backFromBtConnection = findViewById(R.id.back_from_bt_connection);

        backFromBtConnection.setOnClickListener(this);

        mainMenu = findViewById(R.id.main_menu);
        btConnection = findViewById(R.id.bt_connection);
        personalSettings = findViewById(R.id.bt_personal_settings);
        bdSettings = findViewById(R.id.bt_finished_plants);
        getData = findViewById(R.id.get_data);

        btConnection.setOnClickListener(this);
        personalSettings.setOnClickListener(this);
        bdSettings.setOnClickListener(this);
        getData.setOnClickListener(this);

        personalLayout = findViewById(R.id.personal_settings);
        inTempDay = findViewById(R.id.inTemperatureDay);
        inTempNight = findViewById(R.id.inTemperatureNight);
        inHumidity = findViewById(R.id.inHumidity);
        inFrequency = findViewById(R.id.inFreqency);
        inSaveChanges = findViewById(R.id.bt_save_settings);
        backFromPersonalLayout = findViewById(R.id.back_from_personal_settings);

        backFromPersonalLayout.setOnClickListener(this);
        inSaveChanges.setOnClickListener(this);

        bdLayout = findViewById(R.id.choise_veg);
        tomButton = findViewById(R.id.tom_choice);
        cucButton = findViewById(R.id.cuc_choice);
        pepButton = findViewById(R.id.pep_choice);
        sqaButton = findViewById(R.id.sqa_choice);
        backFromChoiceVeg = findViewById(R.id.back_from_choice_veg);

        sqaButton.setOnClickListener(this);
        cucButton.setOnClickListener(this);
        pepButton.setOnClickListener(this);
        tomButton.setOnClickListener(this);
        backFromChoiceVeg.setOnClickListener(this);

        getDataLayout = findViewById(R.id.get_data_layout);
        getTempDay = findViewById(R.id.getTempData);
        getTempNight = findViewById(R.id.getTempNight);
        getHumidity = findViewById(R.id.getHumidity);
        backFromStats = findViewById(R.id.backFromStats);


        backFromStats.setOnClickListener(this);


        choiceLayout = findViewById(R.id.choice_activity);
        listPlants = findViewById(R.id.list_plants);
        listView = findViewById(R.id.list_view);
        backFromListPlants = findViewById(R.id.back_from_list_plants);
        readyPlant = findViewById(R.id.ready_plant_choise);
        bdNamePlant = findViewById(R.id.plant_name);
        bdType = findViewById(R.id.text_type);
        bdTempDay = findViewById(R.id.temp_day);
        bdTempNight = findViewById(R.id.temp_night);
        bdHumidity = findViewById(R.id.humidity);
        bdFrequency = findViewById(R.id.frequency_text);
        bdSaveChanges = findViewById(R.id.save_changes);
        bdPicture = findViewById(R.id.imageView);
        backFromReadyPlantChoice = findViewById(R.id.back_from_ready_plant_choice);

        bdSaveChanges.setOnClickListener(this);
        backFromReadyPlantChoice.setOnClickListener(this);
        backFromListPlants.setOnClickListener(this);

         graphView = (GraphView) findViewById(R.id.graphView);






        showMainMenu();

        switchEnableBt.setOnCheckedChangeListener(this);

        btnEnableSearch.setOnClickListener(this);
        listBtDevices.setOnItemClickListener(this);

        btnDisconnect.setOnClickListener(this);

        bluetoothDevices = new ArrayList<>();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        dbHelper = new DatabaseHelper(this);
        dbHelper.create_db();
        db = dbHelper.open();


        list = new ArrayList<>();
        idList = new ArrayList<>();
        groupList = new ArrayList<>();


        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCreate: " + getString(R.string.bluetooth_not_supported));
            finish();
        }

        if (bluetoothAdapter.isEnabled()) {
            showFrameControls();
            switchEnableBt.setChecked(true);
            setListAdapter(BT_BOUNDED);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int i = Integer.parseInt(String.valueOf(id));
                cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME + " where "
                        + DatabaseHelper.ID + " = ?", new String[]{String.valueOf(idList.get(i))});
                while (cursor.moveToNext()) {
                    if (cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.GROUP_T)) == 1) {
                        bdType.setText("Вид: помидор");
                        bdPicture.setImageResource(R.drawable.tom_choice);
                    } else if (cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.GROUP_T)) == 2) {
                        bdType.setText("Вид: огурец");
                        bdPicture.setImageResource(R.drawable.cuc_choise);
                    }
                    if (cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.GROUP_T)) == 3) {
                        bdType.setText("Вид: болгарский перец");
                        bdPicture.setImageResource(R.drawable.pep_choice);
                    }
                    if (cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.GROUP_T)) == 4) {
                        bdType.setText("Вид: кабачок");
                        bdPicture.setImageResource(R.drawable.sqa_choice);
                    }
                    bdNamePlant.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.NAME)));
                    tempDay = String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.TEMPERATURE_DAY)));
                    tempNight = String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.TEMPERATURE_NIGHT)));
                    humidity = String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.HUMIDITY)));
                    frequency = String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.FREQUENCY)));
                    bdTempDay.setText("Температура днём: " + tempDay);
                    bdTempNight.setText("Температура ночью: " + tempNight);
                    bdHumidity.setText("Влажность: " + humidity);
                    bdFrequency.setText("Периодичность полива: " + frequency);
                }
                listPlants.setVisibility(View.GONE);
                readyPlant.setVisibility(View.VISIBLE);


                cursor.close();

            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);

        if (connectThread != null) {
            connectThread.cancel();
        }

        if (connectedThread != null) {
            connectedThread.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btConnection)){
            showConnectLayout();
            if (bluetoothAdapter.isEnabled()){
                showFrameControls();
            } else{
                showFrameMessage();
            }

        } else if (v.equals(btnEnableSearch)) {
            enableSearch();
        } else if (v.equals(btnDisconnect)) {
            // TODO отключение от устройства
            if (connectedThread != null) {
                connectedThread.cancel();
            }

            if (connectThread != null) {
                connectThread.cancel();
            }

            showFrameControls();
        } else if (v.equals(bdSettings)){
            showBdLayout();
        } else if (v.equals(personalSettings)){
            showPersonalLayout();
        } else if (v.equals(tomButton)){
            showChoiceLayout();
            showListPlants();
            showListPlants("tom");
            type = "tom";
        } else if (v.equals(cucButton)){
            showChoiceLayout();
            showListPlants();
            showListPlants("cuc");
            type = "cuc";
        } else if (v.equals(pepButton)){
            showChoiceLayout();
            showListPlants();
            showListPlants("pep");
            type = "pep";
        } else if (v.equals(sqaButton)){
            showChoiceLayout();
            showListPlants();
            showListPlants("sqa");
            type = "sqa";
        } else if (v.equals(inSaveChanges)){
            String tempNight = inTempNight.getText().toString();
            String tempDay = inTempDay.getText().toString();
            String humidity = inHumidity.getText().toString();
            String frequency = inFrequency.getText().toString();

            String []mas = new String[]{tempDay+"!",tempNight+"#",humidity+"%",frequency+"*"};
            if(connectedThread!=null) {
                connectedThread.write(tempDay + "!");
                connectedThread.write(tempNight + "#");
                connectedThread.write(humidity + "%");
                connectedThread.write(frequency + "&");
            } else if (connectedThread==null){
                Toast.makeText(this, "Подключитесь к устройству",Toast.LENGTH_SHORT).show();
            }
            System.out.println("sent"+humidity);
        } else if (v.equals(bdSaveChanges)){
            if (connectedThread!=null){
                connectedThread.write( tempDay+"!");
                connectedThread.write( tempNight+"#");
                connectedThread.write( humidity+"%");
                connectedThread.write( frequency+"&");

            } else if (connectedThread==null){
                Toast.makeText(this, "Подключитесь к устройству",Toast.LENGTH_SHORT).show();
            }
        } else if (v.equals(backFromChoiceVeg)||v.equals(backFromPersonalLayout)){
            showMainMenu();
        } else if (v.equals(backFromListPlants)){
            showBdLayout();
            idList.clear();
            list.clear();
        } else if(v.equals(backFromReadyPlantChoice)){
            showListPlants();
        } else if (v.equals(backFromBtConnection)){
            showMainMenu();
        } else if (v.equals(getData)){
            showGetData();
        } else if (v.equals(getTempDay)){

            String command = "@";
            connectedThread.write(command);
            Toast.makeText(this,inputData,Toast.LENGTH_SHORT).show();
        } else if (v.equals(getTempNight)){

            String command = "@";
            connectedThread.write(command);
            Toast.makeText(this,inputData,Toast.LENGTH_SHORT).show();
        } else if (v.equals(getHumidity)){

            String command = "!";
            connectedThread.write(command);

            Toast.makeText(this,inputData,Toast.LENGTH_SHORT).show();
        } else if (v.equals(backFromStats)){
            showMainMenu();
        }

    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.equals(listBtDevices)) {
            BluetoothDevice device = bluetoothDevices.get(position);
            if (device != null) {
                connectThread = new ConnectThread(device);
                connectThread.start();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(switchEnableBt)) {
            enableBt(isChecked);

            if (!isChecked) {
                showFrameMessage();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ENABLE_BT) {
            if (resultCode == RESULT_OK && bluetoothAdapter.isEnabled()) {
                showFrameControls();
                setListAdapter(BT_BOUNDED);
            } else if (resultCode == RESULT_CANCELED) {
                enableBt(true);
            }
        }
    }

    private void showFrameMessage() {
        frameMessage.setVisibility(View.VISIBLE);
        frameControls.setVisibility(View.GONE);
    }

    private void showFrameControls() {
        frameMessage.setVisibility(View.GONE);
        frameControls.setVisibility(View.VISIBLE);
    }


    private void showListPlants(String key){


        if(key.equals("tom")){
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME+" where "+
                    DatabaseHelper.GROUP_T+ " = 1",null);
            while (userCursor.moveToNext()){
                list.add(userCursor.getString(userCursor.getColumnIndexOrThrow(DatabaseHelper.NAME)));
                idList.add(userCursor.getInt(userCursor.getColumnIndexOrThrow(DatabaseHelper.ID)));
                groupList.add(userCursor.getInt(userCursor.getColumnIndexOrThrow(DatabaseHelper.GROUP_T)));
            }

            adapter = new ArrayAdapter<>(this,R.layout.list_item2,list);
            listView.setAdapter(adapter);
            userCursor.close();
        } else if (key.equals("cuc")){
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME+" where "+
                    DatabaseHelper.GROUP_T+ " = 2",null);
            while (userCursor.moveToNext()){
                list.add(userCursor.getString(userCursor.getColumnIndexOrThrow(DatabaseHelper.NAME)));
                idList.add(userCursor.getInt(userCursor.getColumnIndexOrThrow(DatabaseHelper.ID)));
                groupList.add(userCursor.getInt(userCursor.getColumnIndexOrThrow(DatabaseHelper.GROUP_T)));
            }
            System.out.println(idList.get(0));
            adapter = new ArrayAdapter<>(this,R.layout.list_item2,list);
            listView.setAdapter(adapter);

            userCursor.close();
        } else if(key.equals("pep")){
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME+" where "+
                    DatabaseHelper.GROUP_T+ " = 3",null);
            while (userCursor.moveToNext()){
                list.add(userCursor.getString(userCursor.getColumnIndexOrThrow(DatabaseHelper.NAME)));
                idList.add(userCursor.getInt(userCursor.getColumnIndexOrThrow(DatabaseHelper.ID)));
                groupList.add(userCursor.getInt(userCursor.getColumnIndexOrThrow(DatabaseHelper.GROUP_T)));
            }

            adapter = new ArrayAdapter<>(this,R.layout.list_item2,list);
            listView.setAdapter(adapter);
            userCursor.close();
        } else if (key.equals("sqa")){
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME+" where "+
                    DatabaseHelper.GROUP_T+ " = 4",null);
            while (userCursor.moveToNext()){
                list.add(userCursor.getString(userCursor.getColumnIndexOrThrow(DatabaseHelper.NAME)));
                idList.add(userCursor.getInt(userCursor.getColumnIndexOrThrow(DatabaseHelper.ID)));
                groupList.add(userCursor.getInt(userCursor.getColumnIndexOrThrow(DatabaseHelper.GROUP_T)));
            }
            adapter = new ArrayAdapter<>(this,R.layout.list_item2,list);
            listView.setAdapter(adapter);
            userCursor.close();
        }

    }
    private void showListPlants(){
        listPlants.setVisibility(View.VISIBLE);
        readyPlant.setVisibility(View.GONE);
    }
    private void showReadyPlant(){
        listPlants.setVisibility(View.GONE);
        readyPlant.setVisibility(View.VISIBLE);
    }

    private void showStats(){

        String[] strMas = str.split(" ");
        int[] intMas = new int[strMas.length];


        for (int i = 0;i<strMas.length;i++){
            intMas[i] = Integer.parseInt(strMas[i].trim());
            System.out.println(i + " "+intMas[i]);
        }

        DataPoint[] dataPoint = new DataPoint[strMas.length];

        for (int i = 0;i<strMas.length;i++){
            dataPoint[i] = new DataPoint(i,intMas[i]);
        }

    }





    private void showMainMenu(){
        mainMenu.setVisibility(View.VISIBLE);
        bdLayout.setVisibility(View.GONE);
        choiceLayout.setVisibility(View.GONE);
        personalLayout.setVisibility(View.GONE);
        connectLayout.setVisibility(View.GONE);
        getDataLayout.setVisibility(View.GONE);
    }
    private void showConnectLayout(){
        mainMenu.setVisibility(View.GONE);
        bdLayout.setVisibility(View.GONE);
        choiceLayout.setVisibility(View.GONE);
        personalLayout.setVisibility(View.GONE);
        connectLayout.setVisibility(View.VISIBLE);
        getDataLayout.setVisibility(View.GONE);
    }
    private void showBdLayout(){
        mainMenu.setVisibility(View.GONE);
        bdLayout.setVisibility(View.VISIBLE);
        choiceLayout.setVisibility(View.GONE);
        personalLayout.setVisibility(View.GONE);
        connectLayout.setVisibility(View.GONE);
        getDataLayout.setVisibility(View.GONE);
    }
    private void showPersonalLayout(){
        mainMenu.setVisibility(View.GONE);
        bdLayout.setVisibility(View.GONE);
        choiceLayout.setVisibility(View.GONE);
        personalLayout.setVisibility(View.VISIBLE);
        connectLayout.setVisibility(View.GONE);
        getDataLayout.setVisibility(View.GONE);
    }
    private void showChoiceLayout(){
        mainMenu.setVisibility(View.GONE);
        bdLayout.setVisibility(View.GONE);
        choiceLayout.setVisibility(View.VISIBLE);
        personalLayout.setVisibility(View.GONE);
        connectLayout.setVisibility(View.GONE);
        getDataLayout.setVisibility(View.GONE);
    }
    private void showGetData(){
        mainMenu.setVisibility(View.GONE);
        bdLayout.setVisibility(View.GONE);
        choiceLayout.setVisibility(View.GONE);
        personalLayout.setVisibility(View.GONE);
        connectLayout.setVisibility(View.GONE);
        getDataLayout.setVisibility(View.VISIBLE);
    }








    private void enableBt(boolean flag) throws SecurityException{
        if (flag) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQ_ENABLE_BT);
        } else {
            bluetoothAdapter.disable();
        }
    }

    private void setListAdapter(int type) {

        bluetoothDevices.clear();
        int iconType = R.drawable.ic_bluetooth_bounded_device;

        switch (type) {
            case BT_BOUNDED:
                bluetoothDevices = getBoundedBtDevices();
                iconType = R.drawable.ic_bluetooth_bounded_device;
                break;
            case BT_SEARCH:
                iconType = R.drawable.ic_bluetooth_search_device;
                break;
        }
        listAdapter = new BtListAdapter(this, bluetoothDevices, iconType);
        listBtDevices.setAdapter(listAdapter);
    }

    private ArrayList<BluetoothDevice> getBoundedBtDevices()throws SecurityException {
        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> tmpArrayList = new ArrayList<>();
        if (deviceSet.size() > 0) {
            for (BluetoothDevice device: deviceSet) {
                if (device==null) {
                    tmpArrayList.add(device);
                }
            }
        }

        return tmpArrayList;
    }


    private void enableSearch() throws SecurityException{
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        } else {
            accessLocationPermission();
            bluetoothAdapter.startDiscovery();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    btnEnableSearch.setText(R.string.stop_search);
                    pbProgress.setVisibility(View.VISIBLE);
                    setListAdapter(BT_SEARCH);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    btnEnableSearch.setText(R.string.start_search);
                    pbProgress.setVisibility(View.GONE);
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (!bluetoothDevices.contains(device)) {
                        bluetoothDevices.add(device);
                        listAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    /**
     * Запрос на разрешение данных о местоположении (для Marshmallow 6.0)
     */
    private void accessLocationPermission() {
        int accessCoarseLocation = this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineLocation   = this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listRequestPermission = new ArrayList<String>();

        if (accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listRequestPermission.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listRequestPermission.isEmpty()) {
            String[] strRequestPermission = listRequestPermission.toArray(new String[listRequestPermission.size()]);
            this.requestPermissions(strRequestPermission, REQUEST_CODE_LOC);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_LOC:

                if (grantResults.length > 0) {
                    for (int gr : grantResults) {
                        // Check if request is granted or not
                        if (gr != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    //TODO - Add your code here to start Discovery
                }
                break;
            default:
                return;
        }
    }



    private class ConnectThread extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private boolean success = false;

        public ConnectThread(BluetoothDevice device) {
            try {
                Method m = device.getClass().getMethod("createRfcommSocket",new Class[] { int.class });
                bluetoothSocket = (BluetoothSocket)m.invoke(device, Integer.valueOf(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() throws SecurityException{
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Идёт подключение",Toast.LENGTH_SHORT).show();
                    }
                });

                cancel();
            }

            if (success) {
                connectedThread = new ConnectedThread(bluetoothSocket);
                connectedThread.start();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Bluetooth-подключение прошло удачно!",Toast.LENGTH_SHORT).show();
                        showMainMenu();
                    }
                });
            }
        }

        public boolean isConnect() {
            return bluetoothSocket.isConnected();
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread  extends  Thread {

        private final InputStream inputStream;
        private final OutputStream outputStream;

        private boolean isConnected = false;

        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.inputStream = inputStream;
            this.outputStream = outputStream;
            isConnected = true;
        }

        @Override
        public void run() {
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            final StringBuffer buffer = new StringBuffer();
            final StringBuffer sbConsole = new StringBuffer();

            final ScrollingMovementMethod movementMethod = new ScrollingMovementMethod();

            while (isConnected){
                try {
                    int bytes = bis.read();
                    buffer.append((char)bytes);
                    int eof = buffer.indexOf("\r\n");
                    if (eof>0){
                        sbConsole.append(buffer.toString());
                        buffer.delete(0,buffer.length());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                inputData = sbConsole.toString();


                            }
                        });
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            try{
                bis.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }


        public void write(String command) {
            byte[] bytes = command.getBytes();
            if (outputStream != null) {
                try {
                    outputStream.write(bytes);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            try {
                isConnected = false;
                inputStream.close();
                outputStream.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void enableLed(int led, boolean state) {
        if (connectedThread != null && connectThread.isConnect()) {
            String command = "";

            switch (led) {
                case LED_RED:
                    command = (state) ? "red on#" : "red off#";
                    break;
                case LED_GREEN:
                    command = (state) ? "green on#" : "green off#";
                    break;
            }

            connectedThread.write(command);
        }
    }

}
