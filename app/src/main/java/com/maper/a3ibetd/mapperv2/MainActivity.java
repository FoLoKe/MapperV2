
package com.maper.a3ibetd.mapperv2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.util.Log;
import android.widget.ViewFlipper;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.maper.a3ibetd.mapperv2.Objects.MapWallPoint;

import java.io.OutputStream;

public class MainActivity extends Activity
{
    private String fileName ="Map";
    MapPanel mapPanel;
    Context context;
    AlertDialog ad;
    // Состояние кнопки редактирования/сдвига
    public Boolean edit_move_Condition = false;
    String[] spinList = {"Стена","Дверь","Маршрут"};
    int[] spinIcons = {R.drawable.wall,R.drawable.door,R.drawable.way};
    int MIN_FLOOR = 0;
    int MAX_FLOOR = 10;
   /////////////////////////GOOGLE
   private GoogleSignInClient mGoogleSignInClient;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_SIGN_IN = 0;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        context = this;
        mapPanel=findViewById(R.id.canvasPanel);

        // Проверка доступа к памяти
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10001);

        // Кнопка сохранения
        Button saveButton =findViewById(R.id.saveButton);
        // Назначение действия
        saveButton.setOnClickListener
                (new OnClickListener()
                 {
                     @Override
                     public void onClick(View v)
                     {
                         // Диалоговое окно подтверждения
                         AlertDialog.Builder adb = new AlertDialog.Builder(context);
                         adb.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialogInterface, int i) {
                                 // Если "Нет", то просто закрываем
                                 dialogInterface.dismiss();
                             }
                         });
                         adb.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialogInterface, int i) {
                                 // Если "Да", то сохраняем
                                 saveFile(fileName,mapPanel.currentFloor);
                                 dialogInterface.dismiss();
                             }
                         });
                         adb.setMessage("Вы уверены, что хотите сохранить?");
                         adb.create().show();
                     }
                 }
                );

        // Кнопка сохранения на Google Drive
        Button gSaveButton = findViewById(R.id.saveToGoogle);
        gSaveButton.setOnClickListener(
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // Вход в аккаунт
                        signIn();
                    }
                }
        );

        // Кнопка добавления новой точки
        Button coordButton = findViewById(R.id.coordButton);
        coordButton.setOnClickListener
                (new OnClickListener()
                 {
                     @Override
                     public void onClick(View v)
                     {
                         // Функция добавления точки
                         setCoordClick(v);
                     }
                 }
                );

        // Кнопка загрузки карты
        Button loadButton =findViewById(R.id.loadButton);
        loadButton.setOnClickListener
                (new OnClickListener()
                 {
                     @Override
                     public void onClick(View v)
                     {
                         // Диалоговое окно подтверждения
                         AlertDialog.Builder adb = new AlertDialog.Builder(context);
                         adb.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialogInterface, int i) {
                                 // Если "Нет", то просто закрыть
                                 dialogInterface.dismiss();
                             }
                         });
                         adb.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialogInterface, int i) {
                                 // Если "Да", то открыть файл
                                 mapPanel.stringButtonPress=openFile(fileName,mapPanel.currentFloor);
                                 dialogInterface.dismiss();
                             }
                         });
                         adb.setMessage("Вы уверены, что хотите загрузить?");
                         adb.create().show();
                     }
                 }
                );
        // Кнопка отдаления
        Button zoomInButton =findViewById(R.id.plusButton);
        zoomInButton.setOnClickListener
                (new OnClickListener()
                 {
                     @Override
                     public void onClick(View v)
                     {
                        if (mapPanel.scaleFactor<mapPanel.maxScale)
                         mapPanel.scaleFactor+=0.25;
                     }
                 }
                );
        // Кнопка приближения
        Button zoomOutButton =findViewById(R.id.minusButton);
        zoomOutButton.setOnClickListener
                (new OnClickListener()
                 {
                     @Override
                     public void onClick(View v)
                     {
                          if (mapPanel.scaleFactor>0.5)
                         mapPanel.scaleFactor-=0.25;
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
        MyCustomAdapter adapter = new MyCustomAdapter(this, R.layout.row, spinList);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spin.setVisibility(View.VISIBLE);

        // Кнопка смены этажа вниз
        Button zoom_down_move = findViewById(R.id.downButton);
        // Устанавливаем действие по нажатию
        zoom_down_move.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mapPanel.currentFloor>MIN_FLOOR) {
                    saveFile(fileName+"_temp",mapPanel.currentFloor);
                    mapPanel.MapWallPoints.clear();
                    mapPanel.currentFloor -= 1;
                    mapPanel.stringButtonPress=openFile(fileName+"_temp",mapPanel.currentFloor);
                }
            }
        });

        Button zoom_up_move = findViewById(R.id.upButton);
        // Устанавливаем действие по нажатию
        zoom_up_move.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mapPanel.currentFloor<MAX_FLOOR) {
                    saveFile(fileName+"_temp",mapPanel.currentFloor);
                    mapPanel.MapWallPoints.clear();
                    mapPanel.currentFloor += 1;
                    mapPanel.stringButtonPress=openFile(fileName+"_temp",mapPanel.currentFloor);
                }
            }
        });
        // Установка размеров кнопок
        ButtonSize();

        // Переход в меню
        Button menuButton = findViewById(R.id.toMenu);
        // Привязываем аудиодорожку
        final MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.dank);

        // Устанавливаем действие по нажатию
        menuButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
            ((ViewFlipper)findViewById(R.id.mainFlipper)).setDisplayedChild(1);
        }
    });
        // Вернуться в режим редактирования
        Button redactorButton = findViewById(R.id.toRedactor);
        redactorButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewFlipper)findViewById(R.id.mainFlipper)).setDisplayedChild(0);
            }
        });
        // Показать создателей
        Button creatorsButton = findViewById(R.id.toCreators);
        // Через некоторое время открывает окно с создателями приложения
        final ScrollView sv=findViewById(R.id.scrollInCreators);
        creatorsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.start();
                long timeElapsed = 0;
                long startTime = System.nanoTime();
                while (timeElapsed < 11)
                    timeElapsed = (System.nanoTime() - startTime) / 1000000000;
                ((ViewFlipper) findViewById(R.id.mainFlipper)).setDisplayedChild(2);
            }
        });


        ImageView testButton = findViewById(R.id.debugFoloke);


        testButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Выйти из режима созерцания созидателей
        Button fromCreatorsButton = findViewById(R.id.fromCreators);
        fromCreatorsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.pause();
                ((ViewFlipper)findViewById(R.id.mainFlipper)).setDisplayedChild(1);
            }
        });
    }


    //////////////////////////////GOOGLE
    // Вход в аккаунт Google
    private void signIn() {
        Log.i(TAG, "Start sign in");
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }
    // Функция входа
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    // Обработка результатов активностей
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Если это - результат авторизации в GoogleDrive
            case REQUEST_CODE_SIGN_IN:
                Log.i(TAG, "Sign in request code");
                // Если успешно:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Signed in successfully.");
                    // Используется последний аккаунт
                    mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    // Build a drive resource client.
                    mDriveResourceClient =
                            Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    // Save
                    BitmapFactory.Options options=new BitmapFactory.Options();
                    options.inScaled=false;
                    // Создание папки
                    createFolder();
                }
                if (resultCode == RESULT_CANCELED){
                    AlertDialog.Builder adb = new AlertDialog.Builder(context);
                    adb.setMessage("Ошибка авторизации");
                    adb.create().show();
                }
                break;
        }
    }

    // Создание папки для файлов на GD
    private void createFolder() {
        // Получение даты и времени:
        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => "+c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = df.format(c.getTime());

        mDriveResourceClient
                .getRootFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(formattedDate)
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(true)
                            .build();
                    return mDriveResourceClient.createFolder(parentFolder, changeSet);
                })
                .addOnSuccessListener(this,
                        driveFolder -> {
                    writeFile(driveFolder);});
    }

    // Запись файла на диск
    public void writeFile(final DriveFolder driveid) {
        // Путь к файлу на устройстве
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File myDir = new File(root + "/CustomMapper");
        if (!myDir.exists()) {
            return;
        }
        // Отправка всех этажей
        for(int i=MIN_FLOOR;i<MAX_FLOOR;i++) {
            String fname = "Walls" + fileName + "Floor" + i + ".txt";
            File file = new File(myDir, fname);
            if (file.exists()) {

                final Task<DriveFolder> rootFolderTask = mDriveResourceClient.getRootFolder();
                final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
                Tasks.whenAll(rootFolderTask, createContentsTask).continueWithTask(task -> {
                    DriveFolder parent = driveid;
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();


                    try (Writer writer = new OutputStreamWriter(outputStream)) {


                        //InputStream inputStream = openFileInput(fileName);
                        FileInputStream inputStream = new FileInputStream(file);
                        InputStreamReader isr = new InputStreamReader(inputStream);
                        BufferedReader reader = new BufferedReader(isr);
                        String templine;
                        while ((templine = reader.readLine()) != null) {
                            writer.write(templine);
                            writer.write("\n");

                        }
                        inputStream.close();
                        reader.close();
                        isr.close();

                    } catch (Throwable e) {
                    }


                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(file.getName())
                            .setMimeType("text/plain")
                            .setStarred(true)
                            .build();

                    return mDriveResourceClient.createFile(parent, changeSet, contents);
                })
                        .addOnSuccessListener(this,
                                driveFile -> {

                                })
                        .addOnFailureListener(this, e -> {
                            Log.e(TAG, "Unable to create file", e);

                        });
            }
        }
    }


    // Изменение размеров кнопок, согласно размерам экрана
    private void ButtonSize(){
        // Перепись всех кнопок, подвергающихся экзекуции
        Button EM = findViewById(R.id.edit_move);
        Button LU = findViewById(R.id.upButton);
        Button LD = findViewById(R.id.downButton);
        Button ZP = findViewById(R.id.plusButton);
        Button ZM = findViewById(R.id.minusButton);
        Button SB = findViewById(R.id.saveButton);
        Button PB = findViewById(R.id.coordButton);
        Button LB = findViewById(R.id.loadButton);
        // Получение метрик дисплея (размеров)
        DisplayMetrics met = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(met);
        // Задание размеров кнопок
        int s;
        if(met.widthPixels<met.heightPixels)
            s = met.widthPixels/9;
        else
            s = met.heightPixels/9;
        EditButtonSize(EM,s,s);
        EditButtonSize(LU,s,s);
        EditButtonSize(LD,s,s);
        EditButtonSize(ZP,s,s);
        EditButtonSize(ZM,s,s);
        EditButtonSize(SB,s*2,s);
        EditButtonSize(PB,s*2,s);
        EditButtonSize(LB,s*2,s);
    }

    // Изменение размеров конкретной кнопки
    private void EditButtonSize(Button but, int w, int h){
        but.getLayoutParams().width=w;
        but.getLayoutParams().height=h;
    }

    // Функция открытия файла
    public String openFile(String fileName,int floorNumber) {
        String line="";
        try {
            // Получение пути
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            File myDir = new File(root + "/CustomMapper");
            if (!myDir.exists()) {
                return "no such directory";
            }
            // Получение имени файла
            String fname = "Walls"+fileName+"Floor"+floorNumber+".txt";
            File file = new File (myDir, fname);
            if (file.exists ())
            {
                FileInputStream inputStream = new FileInputStream(file);

                if (inputStream != null)
                {
                    InputStreamReader isr = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(isr);
                    String templine;

                    // Если файл не пуст
                    if ((templine = reader.readLine()) != null)
                    {
                        // Если строка содержит запись об этаже
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
                        // Пока есть строки
                        while((templine=reader.readLine())!=null)
                        {
                            // Преобразуем строку в число и получим индекс
                            int index=Integer.valueOf(templine);
                            // Получаем x и y
                            float x=Float.valueOf(reader.readLine());
                            float y=Float.valueOf(reader.readLine());
                            line+=x;
                            // Добавляем точку на карту
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
                            mapPanel.addMapWallPoint(index,x,y,tempMapWallPoints);
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


    // Функция сохранения карты
    public void saveFile(String fileName,int floorNumber) {
        try {
            // Получение пути
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            File myDir = new File(root + "/CustomMapper");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }

            // Имя файла
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
            // Сохранение индексов всех точек в буфер
            for(Map.Entry<Integer, MapWallPoint> entry : mapPanel.MapWallPoints.entrySet())
            {
                tempIndexes[i] = entry.getKey();
                i++;
            }
            Arrays.sort(tempIndexes);
            // Запись всех точек в файл
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

    // Всплывающее окно "4 строки"
   public void PointFunction(final int objectId){
        // Если в функцию передано неправильное значение
       if(!(mapPanel.MapWallPoints.containsKey(objectId)))
           return;
        final int id=objectId;
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        // Разметка всплывающего окна
        final View layout = LayoutInflater.from(this).inflate(R.layout.point_layout, null);
        adb.setView(layout);
        // Запретить закрывать нажатием во вне
        adb.setCancelable(false);
        // Назначение текста в заголовке
        TextView headline=layout.findViewById(R.id.headline);
        headline.setText("POINT #"+id);
        // Заполнение строки координат
        final EditText pointCoord=layout.findViewById(R.id.point_coord);
        pointCoord.setText(""+mapPanel.MapWallPoints.get(objectId).getWorldLocation().x+", "+mapPanel.MapWallPoints.get(objectId).getWorldLocation().y);
        // Заполнение поля "Соседи"
        final EditText neighbors = layout.findViewById(R.id.neighbors);
        String setNeigboursRow="";
        for(Map.Entry<Integer,MapWallPoint> entry: mapPanel.MapWallPoints.get(objectId).walls.entrySet())
        {
            setNeigboursRow+=entry.getKey()+", ";
        }
        neighbors.setText(setNeigboursRow);

        // Кнопка удаления точки
        Button del = layout.findViewById(R.id.ad_del);
        del.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Диалоговое окно подтверждения намерений
                AlertDialog.Builder ad1 = new AlertDialog.Builder(context);
                ad1.setMessage("Вы уверены, что хотите удалить?");
                ad1.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Точно удалить
                        mapPanel.removePoint(id);
                        // Закрытие всплывшего окна (которое большое)
                        ad.dismiss();
                    }
                });
                ad1.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Если "Нет", то просто закрыть
                        dialogInterface.cancel();
                    }
                });
                ad1.show();
            }
        });

        // Кнопка закрытия этого диалогового окна
        Button cancelButt = layout.findViewById(R.id.ad_cancel);
        cancelButt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
            }
        });

        // Кнопка сохранения изменений
        Button okButt = layout.findViewById(R.id.ad_ok);
        okButt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    // Получаем значения из полей:
                // Координаты
                String rowCoords=pointCoord.getText().toString();
                String[] tempCoordRows = rowCoords.split(", ");
                // Соседи
                String rowWalls = neighbors.getText().toString();
                String[] tempWallsRows = rowWalls.split(", ");
                    // Изменяем информацию о точке:
                // Переписываем соседей
                HashMap<Integer, MapWallPoint> tempMapWallPoints = new HashMap<>();
                if (rowWalls.length()>0)
                {
                    for (int i = 0; i < tempWallsRows.length; i++) {
                        try {
                            int test = Integer.parseInt(tempWallsRows[i]);
                        }
                        catch(NumberFormatException e)
                        {
                            return;
                        }
                        if (mapPanel.MapWallPoints.containsKey(Integer.valueOf(tempWallsRows[i]))) {
                            tempMapWallPoints.put(Integer.valueOf(tempWallsRows[i]), mapPanel.MapWallPoints.get(Integer.valueOf(tempWallsRows[i])));
                        } else {
                            return;
                        }
                    }

                }
                // Переписываем координаты
                float x=Float.valueOf(tempCoordRows[0]);
                float y=Float.valueOf(tempCoordRows[1]);
                // Сохраняем изменения
                mapPanel.changeWallPoint(objectId,x,y,tempMapWallPoints);
                // Закрыть всплывающее окно
                ad.dismiss();

            }
        });

        // Получение метрик дисплея (размеров)
        DisplayMetrics met = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(met);
        // Задание размеров кнопок
        int s;
        if(met.widthPixels<met.heightPixels)
            s = met.widthPixels/9;
        else
            s = met.heightPixels/9;
        // Изменение размеров кнопок
        EditButtonSize(okButt,2*s,s);
        EditButtonSize(cancelButt,2*s,s);
        EditButtonSize(del,2*s,s);
        // Создать и показать окно
        ad = adb.create();
        ad.show();
    }

    // Класс для выпадающего списка объектов (слева сверху)
    public class MyCustomAdapter extends ArrayAdapter<String> {
        public MyCustomAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
        public View getCustomView(int position, View convertView, ViewGroup parent) {
            // Используем отдельную разметку
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);
            // Указываем пары текст-картинка для каждой строки
            TextView label = (TextView) row.findViewById(R.id.spin_text);
            label.setText(spinList[position]);
            ImageView icon = (ImageView) row.findViewById(R.id.spin_icon);
            icon.setImageResource(spinIcons[position]);
            // Возвращаем объект
            return row;
        }
    }
}
