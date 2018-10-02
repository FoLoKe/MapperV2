//package com.maper.a3ibetd.mapperv2;
package com.maper.a3ibetd.mapperv2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.BoringLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



public class MainActivity extends Activity
{
    private String fileName ="Map";
    MapPanel mapPanel;
    // Состояние кнопки редактирования/сдвига
    public Boolean edit_move_Condition = false;
    String[] spinList = {"Стена","Дверь","Маршрут"};
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        Button saveButton =findViewById(R.id.saveButton);
        mapPanel=findViewById(R.id.canvasPanel);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10001);

        saveButton.setOnClickListener
                (new OnClickListener()
                 {
                     @Override
                     public void onClick(View v)
                     {
                         saveFile(fileName,mapPanel.currentFloor);

                         //mapPanel.stringButtonPress="saved ";
                     }
                 }
                );
        Button coordButton = findViewById(R.id.coordButton);
        coordButton.setOnClickListener
                (new OnClickListener()
                 {
                     @Override
                     public void onClick(View v)
                     {
                         setCoordClick(v);
                     }
                 }
                );
        Button loadButton =findViewById(R.id.loadButton);
        loadButton.setOnClickListener
                (new OnClickListener()
                 {
                     @Override
                     public void onClick(View v)
                     {

                         mapPanel.stringButtonPress=openFile(fileName,mapPanel.currentFloor);
                     }
                 }
                );
        // Кнопка редактирования/сдвига
        Button edit_move = findViewById(R.id.edit_move);
        // Устанавливаем действие по нажатию
        edit_move.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EMClick(view);
            }
        });
        // Выпадающий список
        Spinner spin = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row, R.id.spin_text, spinList);
        spin.setAdapter(adapter);
        spin.setVisibility(View.VISIBLE);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
                //((TextView) adapterView.getChildAt(0)).setTextSize(20);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spin.setVisibility(View.VISIBLE);

        Button zoom_down_move = findViewById(R.id.downButton);
        // Устанавливаем действие по нажатию
        zoom_down_move.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mapPanel.scaleFactor+=0.25;
            }
        });

        Button zoom_up_move = findViewById(R.id.upButton);
        // Устанавливаем действие по нажатию
        zoom_up_move.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mapPanel.scaleFactor-=0.25;
            }
        });
        // Установка размеров кнопок
        ButtonSize();

    }

    private void ButtonSize(){
        // Перепись всех кнопк, подвергающихся экзекуции
        Button EM = findViewById(R.id.edit_move);
        Button LU = findViewById(R.id.upButton);
        Button LD = findViewById(R.id.downButton);
        // Получение метрик дисплея (размеров)
        DisplayMetrics met = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(met);
        // Задание размеров кнопок
        int s;
        if(met.widthPixels<met.heightPixels)
            s = met.widthPixels/9;
        else
            s = met.heightPixels/9;
        EM.getLayoutParams().width=s;
        EM.getLayoutParams().height=s;
        LU.getLayoutParams().width=s;
        LU.getLayoutParams().height=s;
        LD.getLayoutParams().width=s;
        LD.getLayoutParams().height=s;
    }
    public String openFile(String fileName,int floorNumber) {
        String line="";
        try {

            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            File myDir = new File(root + "/CustomMapper");
            if (!myDir.exists()) {
                return "no such directory";
            }

            String fname = "Walls"+fileName+"Floor"+floorNumber+".txt";
            File file = new File (myDir, fname);
            if (file.exists ())
            {
                //InputStream inputStream = openFileInput(fileName);
                FileInputStream inputStream = new FileInputStream(file);

                if (inputStream != null)
                {
                    InputStreamReader isr = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(isr);
                    String templine;

                    if ((templine = reader.readLine()) != null)
                    {
                        if (templine.contains("Floor"))
                        {
                            char tempFloor=templine.charAt(5);
                            mapPanel.currentFloor=Character.getNumericValue(tempFloor);
                            mapPanel.clearMap();
                        }
                        else
                        {
                            return "error: no floor";
                        }
                        while((templine=reader.readLine())!=null)
                        {

                            int index=Integer.valueOf(templine);
                            float x=Float.valueOf(reader.readLine());
                            float y=Float.valueOf(reader.readLine());
                            line+=x;
                            HashMap<Integer, MapWallPoint> tempMapWallPoints = new HashMap<>();
                            while(!((templine=reader.readLine()).contains("***")))
                            {

                                if (templine==null)
                                    return "error unexpected end of file"+templine;
                                if (mapPanel.MapWallPoints.containsKey(Integer.valueOf(templine))) {
                                    tempMapWallPoints.put(Integer.valueOf(templine), mapPanel.MapWallPoints.get(Integer.valueOf(templine)));
                                }
                                else
                                    {
                                    return "error no point";
                                    }
                            }
                            mapPanel.MapWallPoints.put(index,new MapWallPoint(Color.rgb(0,255,0),x,y,0,50,50,mapPanel.MapWallPoints.size(),tempMapWallPoints));
                          ///  tempMapWallPoints.clear();
                        }
                    }
                    inputStream.close();
                    reader.close();
                    isr.close();
                    return line;
                }
            }
            else
            {
                return "no such file";
            }
        } catch (Throwable t)
        {
            return "error:"+ t.getLocalizedMessage();
        }
        return line;
    }
    public void saveFile(String fileName,int floorNumber) {
        try {
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            File myDir = new File(root + "/CustomMapper");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }

            String fname = "Walls"+fileName+"Floor"+floorNumber+".txt";
            File file = new File (myDir, fname);
            if (file.exists ())
                file.delete ();

            FileOutputStream out = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(out);
            BufferedWriter writer = new BufferedWriter(osw);
            int[] tempIndexes=new int[mapPanel.MapWallPoints.size()];
            writer.write("Floor"+floorNumber);
            int i=0;
            for(Map.Entry<Integer, MapWallPoint> entry : mapPanel.MapWallPoints.entrySet())
            {
                tempIndexes[i] = entry.getKey();
                //MapWallPoint value = entry.getValue();
                //value.save(writer);
                i++;
            }
            Arrays.sort(tempIndexes);
            for(int j=0;j<i;j++)
            {

                mapPanel.MapWallPoints.get(tempIndexes[j]).save(writer);
            }
            writer.close();
            osw.close();
        } catch (Throwable t) {
            mapPanel.stringButtonPress=
                    "Exception: " + t.toString();
        }
    }

    // Функция для кнопки сдвига/редактирования
    private void EMClick(View v)
    {
        // Нужно для получения картинок
        Resources res = getResources();
        // Получение самой кнопки
        Button EM = (Button)v;
        // Заодно получаем выпадающий список
        Spinner spin = findViewById(R.id.spinner);
        // Инверсируем состояние кнопки
        edit_move_Condition=!edit_move_Condition;
        // Включаем сдвиг
        if(edit_move_Condition) {
            // Получаем картинку
            Drawable img = res.getDrawable(R.drawable.move);
            // Устанавливаем картинку
            EM.setBackground(img);
            // Изменяем состояние списка:
            spin.setVisibility(View.INVISIBLE);

            mapPanel.movable=true;
        }
        // Включаем редактирование
        else{
            // Получаем картинку
            Drawable img = res.getDrawable(R.drawable.edit);
            // Устанавливаем картинку
            EM.setBackground(img);
            // Изменяем состояние списка:
            spin.setVisibility(View.VISIBLE);
            mapPanel.movable=false;
        }
    }

    private void setCoordClick(View v)
    {
        // Создание всплывающего окна
        AlertDialog.Builder ad = new AlertDialog.Builder(this);

        // Разметка всплывающего окна
        final View layout = LayoutInflater.from(this).inflate(R.layout.layout_setcoord, null);
        ad.setView(layout);

        // Кнопка согласия
        ad.setCancelable(false);
        ad.setPositiveButton("Принять", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Здесь впиши, что хочешь на OK
                // Это строка, из которой нужен текст
                EditText ET = layout.findViewById(R.id.coord);
                String Row = ET.getText().toString();

                String[] tempRows = Row.split(", ");
                HashMap<Integer, MapWallPoint> tempMapWallPoints = new HashMap<>();
                if (Row.length()>0)
                {
                    for (int i = 0; i < tempRows.length; i++) {
                        int j=Integer.valueOf(tempRows[i]);
                        if (mapPanel.MapWallPoints.containsKey(Integer.valueOf(tempRows[i]))) {
                            tempMapWallPoints.put(Integer.valueOf(tempRows[i]), mapPanel.MapWallPoints.get(Integer.valueOf(tempRows[i])));
                        } else {
                            return;
                        }
                    }

                }
                mapPanel.putWallPoint(tempMapWallPoints);
        }
        });

        // Кнопка отмены
        ad.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Создать и показать окно
        ad.create().show();
    }
}
