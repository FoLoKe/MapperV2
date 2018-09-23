//package com.maper.a3ibetd.mapperv2;
package com.maper.a3ibetd.mapperv2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;



public class MainActivity extends Activity
{
    private String fileName ="Map";
    MapPanel mapPanel;
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

    }
    public String /*HashMap<Integer,MapWallPoint> */openFile(String fileName,int floorNumber) {
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
                    //boolean firstRow=true;
                    String templine="";

                    if ((templine = reader.readLine()) != null)
                    {
                        if (templine.contains("Floor"))
                        {
                            char tempFloor=templine.charAt(5);
                            mapPanel.currentFloor=Character.getNumericValue(tempFloor);
                            //line+=tempFloor;
                            mapPanel.clearMap();
                        }
                        else
                        {
                            return "error: no floor";
                        }
                        while((templine=reader.readLine())!=null)
                        {

                            int index=Integer.valueOf(templine);
                            float x=Float.valueOf(templine=reader.readLine());
                            float y=Float.valueOf(templine=reader.readLine());
                            line+=x;
                            HashMap<Integer, MapWallPoint> tempMapWallPoints = new HashMap<>();
                            while(!((templine=reader.readLine()).contains("***")))
                            {

                                if (templine==null)
                                    return "error unexpected end of file"+templine;
                                //line+=templine+"_";
                                if (mapPanel.MapWallPoints.containsKey(Integer.valueOf(templine))) {

                                    tempMapWallPoints.put(Integer.valueOf(templine), mapPanel.MapWallPoints.get(Integer.valueOf(templine)));
                                }
                                else
                                    {
                                    return "error no point";
                                }


                            }
                            mapPanel.MapWallPoints.put(index,new MapWallPoint(Color.rgb(0,255,0),x,y,0,50,50,mapPanel.MapWallPoints.size(),tempMapWallPoints));
                            tempMapWallPoints.clear();
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
        } catch (Throwable t) {

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

            writer.write("Floor"+floorNumber);
            for(Map.Entry<Integer, MapWallPoint> entry : mapPanel.MapWallPoints.entrySet())
            {
                Integer key = entry.getKey();
                MapWallPoint value = entry.getValue();
                value.save(writer);
                //writer.write("***");
					/*writer.newLine();
					writer.write(Integer.toString(value.myIndex));
					writer.newLine();
					writer.write(Float.toString(value.location.x));
					writer.newLine();
					writer.write(Float.toString(value.location.y));
					for(Map.Entry<Integer, MapWallPoint> entryTwo : value.walls.entrySet())
					{
						Integer keyTwo = entryTwo.getKey();
						MapWallPoint valueTwo = entryTwo.getValue();
						writer.newLine();
						writer.write(Integer.toString(keyTwo));
					}

				writer.newLine();
					writer.write("***");*/
                //writer.newLine();
                //mapPanel.stringButtonPress=root+""+getFileStreamPath(fname);
            }
            writer.close();
            osw.close();
        } catch (Throwable t) {
            mapPanel.stringButtonPress=
                    "Exception: " + t.toString();
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
                boolean errorCheck = true;
                //while(!(Row.isEmpty())&&errorCheck)
                //{
                //if(Row.contains(","))
                //{

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
