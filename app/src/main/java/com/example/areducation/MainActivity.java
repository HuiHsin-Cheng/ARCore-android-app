package com.example.areducation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaCas;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Renderer;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.DragGesture;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class MainActivity extends AppCompatActivity implements Scene.OnUpdateListener {

    int PERMISSION_REQUEST_CODE = 200;
    private ArSceneView arview;
    private com.google.ar.core.Session session;
    private boolean shouldConfigureSession=false;
    AnchorNode current_anchornode = new AnchorNode();
    TextView txt;
    ProgressBar progressBar;
    ImageButton btnVR;
    Button btnAR;
    String jsonString="";
    List idArray = new ArrayList();
    List imageNameArray = new ArrayList();
    List modelNameArray = new ArrayList();
    List textureNameArray = new ArrayList();
    ArrayList<ArrayList<String>> DocNameArray = new ArrayList<ArrayList<String>>();
    List imageLocationArray = new ArrayList();
    List modelLocationArray = new ArrayList();
    List textureLocationArray = new ArrayList();
    ArrayList<ArrayList<String>> DocLocationArray = new ArrayList<ArrayList<String>>();
    List GLBLocationArray = new ArrayList();
    List<Bitmap> bitmapsList= new ArrayList();
    List bitmapsListNo=  new ArrayList();
    AugmentedImageDatabase augmentedImageDatabase;
    AugmentedImageDatabase augmentedImageDatabase_backup;
    int delay=0;
    int currentNumber;
    Toast toast3;
    TextView textLoading;
    boolean loading=false;
    double percentage;


    //internet
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
    //internet

    //AR
    public void ARbuttonOnClick(View view) {
        WebView webview = (WebView) findViewById(R.id.web_view);
        webview.setVisibility(View.GONE);
//        Toast toast = Toast.makeText(this, "已进入AR模式", Toast.LENGTH_SHORT);
//        toast.show();
        btnAR=(Button)findViewById(R.id.button1);
        btnAR.setBackgroundColor(Color.argb(90,255,255,255));
        btnVR=(ImageButton)findViewById(R.id.button2);
        btnVR.setBackgroundColor(Color.rgb(255,255,255));

        if (loading) {
            textLoading.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    //VR
    public void VRbuttonOnClick(View view) {
        WebView webview = (WebView) findViewById(R.id.web_view);
        webview.setVisibility(View.VISIBLE);
//        Toast toast = Toast.makeText(this, "已进入VR模式", Toast.LENGTH_SHORT);
//        toast.show();
        btnVR=(ImageButton)findViewById(R.id.button2);
        btnVR.setBackgroundColor(Color.argb(90,1,1,1));
        btnAR=(Button)findViewById(R.id.button1);
        btnAR.setBackgroundColor(Color.rgb(255,255,255));

        textLoading.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

    }

    //Restart
    public void RestartButtonOnClick(View view) {
        if(!loading){
        if(go_or_not)
        {
            if(current_anchornode.getAnchor()!= null)
            {
                try {
                    go_or_not = false;
                    WebView webview = (WebView) findViewById(R.id.web_view);
                    webview.setVisibility(View.GONE);
                    webview.getSettings().setJavaScriptEnabled(true);
                    webview.setWebViewClient(new WebViewClient()); //不調用系統瀏覽器
                    //webview.loadUrl("https://www.baidu.com");
                    webview.loadUrl("https://ar.keshufang.com/vr?id= ");
                    System.out.println("reset mark");
                    System.out.println(current_anchornode.getAnchor());
//
                    current_anchornode.getAnchor().detach();

                    arview.getScene().removeChild(current_anchornode);
                    current_anchornode.setParent(null);
                    current_anchornode = new AnchorNode();

                    //hsin
                    TextView textView = (TextView) findViewById(R.id.titleNameText);
                    textView.setText(String.valueOf(0));

                    String[] initArray1 = {};
                    ListAdapter adapter1 = new ArrayAdapter<>(this,
                            android.R.layout.simple_list_item_1
                            , initArray1);

                    ListView listView = (ListView) findViewById(R.id.listView);
                    listView.setAdapter(adapter1);
                    //hsin

                    Config config=new Config(session);
                    config.setAugmentedImageDatabase(new AugmentedImageDatabase(session));
                    config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
                    config.setFocusMode(Config.FocusMode.AUTO);
                    session.configure(config);


                    Thread thread2 = new Thread(new Runnable() {
                        public void run() {


                            Config config1=new Config(session);

                            AugmentedImageDatabase new_augmentedImageDatabase=new AugmentedImageDatabase(session);
                            //把bitmapsList裡的圖片加到augmentedImageDatabase中
                            for(int i=0;i<bitmapsList.size();i++){
                                try{
                                    new_augmentedImageDatabase.addImage(bitmapsList.get(i).toString(),bitmapsList.get(i));

                                }catch (Exception e){}

                            }

                            config1.setAugmentedImageDatabase(new_augmentedImageDatabase);
                            config1.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
                            config1.setFocusMode(Config.FocusMode.AUTO);
                            session.configure(config1);



                        }
                    });

                    thread2.start();

//
//                System.out.println(arview.getScene().getChildren());
//                Node node = arview.getScene().getChildren().get(2);
//                System.out.println(arview.getScene().getChildren());






                    System.out.println(arview.getScene().getChildren());

                    textLoading.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast toast1 = Toast.makeText(this, "请扫描图片以载入模型", Toast.LENGTH_SHORT);
                    toast1.show();
                }catch (Exception e){
                    System.out.println(e);
                }

            }
            else{
                go_or_not = false;
                WebView webview = (WebView) findViewById(R.id.web_view);
                webview.setVisibility(View.GONE);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.setWebViewClient(new WebViewClient()); //不調用系統瀏覽器
                //webview.loadUrl("https://www.baidu.com");
                webview.loadUrl("https://ar.keshufang.com/vr?id=");

                arview.getScene().removeChild(current_anchornode);
                current_anchornode.setParent(null);
                current_anchornode = new AnchorNode();

                //hsin
                TextView textView = (TextView) findViewById(R.id.titleNameText);
                textView.setText(String.valueOf(0));

                String[] initArray1 = {};
                ListAdapter adapter1 = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1
                        , initArray1);

                ListView listView = (ListView) findViewById(R.id.listView);
                listView.setAdapter(adapter1);
                //hsin

                Config config=new Config(session);
                config.setAugmentedImageDatabase(new AugmentedImageDatabase(session));
                config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
                config.setFocusMode(Config.FocusMode.AUTO);
                session.configure(config);


                Thread thread2 = new Thread(new Runnable() {
                    public void run() {


                        Config config1=new Config(session);

                        AugmentedImageDatabase new_augmentedImageDatabase=new AugmentedImageDatabase(session);
                        //把bitmapsList裡的圖片加到augmentedImageDatabase中
                        for(int i=0;i<bitmapsList.size();i++){
                            try{
                                new_augmentedImageDatabase.addImage(bitmapsList.get(i).toString(),bitmapsList.get(i));

                            }catch (Exception e){}

                        }

                        config1.setAugmentedImageDatabase(new_augmentedImageDatabase);
                        config1.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
                        config1.setFocusMode(Config.FocusMode.AUTO);
                        session.configure(config1);



                    }
                });

                thread2.start();

//
//                System.out.println(arview.getScene().getChildren());
//                Node node = arview.getScene().getChildren().get(2);
//                System.out.println(arview.getScene().getChildren());






                System.out.println(arview.getScene().getChildren());

                textLoading.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                Toast toast1 = Toast.makeText(this, "请扫描图片以载入模型", Toast.LENGTH_SHORT);
                toast1.show();
            }
        }
        }



    }


    //Documents
    public void ShowDoc(View view) {
        ListView listView = (ListView) findViewById(R.id.listView);
        ImageButton btnfolder=(ImageButton) findViewById(R.id.button4);
        ImageButton btnreturn=(ImageButton) findViewById(R.id.button5);

        TextView textView = (TextView) findViewById(R.id.titleNameText);

        //Closing listView
        if (listView.getVisibility() == View.VISIBLE) {
            listView.setVisibility(View.GONE);
            btnfolder.setVisibility(View.VISIBLE);
            btnreturn.setVisibility(View.GONE);

            textView.setVisibility(View.VISIBLE);
        }
        //opening listView
        else {
            listView.setVisibility(View.VISIBLE);
            btnfolder.setVisibility(View.GONE);
            btnreturn.setVisibility(View.VISIBLE);

            textView.setVisibility(View.GONE);

        }
    }

    // Array of strings...
    String[] initArray = {};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //internet check
        if(isNetworkConnected()){
            System.out.println("internet connect");
        }
        if(!isNetworkConnected()){
            System.out.println("internet not connect");
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("未连接网路");
            alertDialog.setMessage("请连接网路以载入资源");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "关闭",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.show();
        }
        //internet check

        //camera check
        if (!checkPermission()) {
            AlertDialog alertDialogCam = new AlertDialog.Builder(MainActivity.this).create();
            alertDialogCam.setTitle("未取用相机权限");
            alertDialogCam.setMessage("请至装置的「设定」中允许课书房AR取用相机权限");
            alertDialogCam.setButton(AlertDialog.BUTTON_NEUTRAL, "关闭",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialogCam.show();

//            requestPermission();
//            System.out.println("PERMISSION_REQUEST_CODE:"+ PERMISSION_REQUEST_CODE);
        }
        //camera check


        //force to access internet
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //force to access internet

        //listView button set
        ImageButton btnreturn=(ImageButton) findViewById(R.id.button5);
        btnreturn.setVisibility(View.GONE);
        //listView button set

        btnAR=(Button)findViewById(R.id.button1);
        btnAR.setBackgroundColor(Color.argb(90,255,255,255));
        textLoading= (TextView)findViewById(R.id.textView);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

        //訪問內存許可
        //txt=(TextView) findViewById(R.id.textView2);
//        btn=(Button)findViewById(R.id.button2);
//        btn.setOnClickListener(new View.OnClickListener() {
//
//
//            @Override
//            public void onClick(View view) {
//               //cat
//                //txt.setText(jsonString);
//                //arview.getScene().onRemoveChild(cakeNode);
//            }
//        });

        //View
        arview=(ArSceneView)findViewById(R.id.arView);
        WebView webview = (WebView) findViewById(R.id.web_view);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient()); //不調用系統瀏覽器
        //webview.loadUrl("https://www.baidu.com");
        webview.loadUrl("https://ar.keshufang.com/vr?id=");
        ListAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1
                , initArray);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this,"點選第 "+(i +1) +" 個 \n內容："+DocLocationArray.get(currentNumber).get(i), Toast.LENGTH_SHORT).show();
            }
        });

        //Request Permission
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        setupSession();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this,"Permission need to display camera" ,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
        initSceneView();
        System.out.println("firstt");
        Toast toast = Toast.makeText(this, "请扫描图片以载入模型", Toast.LENGTH_SHORT);
        if(isNetworkConnected()&&checkPermission()){
            System.out.println("internet connect");
            toast.show();
        }



    }
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }
//
//    private void requestPermission() {
//
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.CAMERA},
//                PERMISSION_REQUEST_CODE);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
//
//                    // main logic
//                } else {
//                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                                != PackageManager.PERMISSION_GRANTED) {
//                            showMessageOKCancel("You need to allow access permissions",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                                requestPermission();
//                                            }
//                                        }
//                                    });
//                        }
//                    }
//                }
//                break;
//        }
//    }

//    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(MainActivity.this)
//                .setMessage(message)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
//    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }


    private void initSceneView() {
        try
        {
            Thread.currentThread().sleep(delay);//毫秒
            arview.getScene().addOnUpdateListener(this);
        }
        catch(Exception e){}

    }

    private void setupSession() {
        try
        {
            Thread.currentThread().sleep(delay);//毫秒
            if(session==null){
                try {
                    session=new Session(this);
                } catch (UnavailableArcoreNotInstalledException e) {
                    e.printStackTrace();
                }catch (UnavailableApkTooOldException e) {
                    e.printStackTrace();
                }catch (UnavailableSdkTooOldException e) {
                    e.printStackTrace();
                }catch (UnavailableDeviceNotCompatibleException e) {
                    e.printStackTrace();
                }
                shouldConfigureSession=true;
            }
            if(shouldConfigureSession){
                configSession();
                shouldConfigureSession=false;
                arview.setupSession(session);
            }
            try {
                session.resume();
                arview.resume();
            } catch (CameraNotAvailableException e) {
                e.printStackTrace();
                session=null;
                return;
            }
        }
        catch(Exception e){}

    }

    private List<Bitmap> loadImage() {
        try
        {
            try {
//======================================================================================//內建imageLocationArray1
                List imageLocationArray1 = new ArrayList();

                imageLocationArray1.add("https://s3.ap-southeast-1.amazonaws.com/ar.materials/1560147313975.rv.png");
                imageLocationArray1.add("https://s3.ap-southeast-1.amazonaws.com/ar.materials/1558493487754.1555729114846.4416DG02_CURRENT.01.jpg");
                imageLocationArray1.add("https://s3.ap-southeast-1.amazonaws.com/ar.materials/1560145004962.barri.png");
                imageLocationArray1.add("https://s3.ap-southeast-1.amazonaws.com/ar.materials/1561518345055.l11056-free-base-mesh-centaur--67384.jpg");
                imageLocationArray1.add("https://s3.ap-southeast-1.amazonaws.com/ar.materials/1562755487363.godfather-ii.jpeg");


//======================================================================================//內建imageLocationArray1
                GetimageLocationArray();
                //downloadFile4();
                System.out.println("currentNumber after: "+currentNumber);


                System.out.println("bee2: ");
                System.out.println(imageLocationArray);

                //將imageLocationArray1裡的所有圖片轉成bitmap存在bitmapsList中//******size()
                for(int i=0;i<imageLocationArray.size();i++){

                    File sd = Environment.getExternalStorageDirectory();
                    File file = new File(sd+"/"+imageNameArray.get(i).toString());

                    System.out.println(file.getPath().toString());
                    if(file.exists())
                    {
                        System.out.println("goodgoodOOOO");
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath().toString());

                        bitmapsList.add(bitmap);

                        System.out.println("imageimageLLLL");
                        System.out.println(imageLocationArray.get(i).toString());

                    }
                    else
                    {
                        System.out.println("download");
                        InputStream is=new URL(imageLocationArray.get(i).toString()).openStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        bitmapsList.add(bitmap);
                        File dest = new File(sd, imageNameArray.get(i).toString());
                        try {
                            FileOutputStream out = new FileOutputStream(dest);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(dest.getPath().toString());
                    }


                }
                //將imageLocationArray1裡的所有圖片轉成bitmap存在bitmapsList中 //******size()
                System.out.println("bitmapsList"+bitmapsList);
                return  bitmapsList;

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("loadImagebad");
                return  null;
            }


        }
        catch(Exception e){return  null;}

    }

    private void configSession() {
        try
        {
            Thread.currentThread().sleep(delay);//毫秒
            Config config=new Config(session);
            config.setFocusMode(Config.FocusMode.AUTO);
            if(!BuildDatabase(config)){
                Toast.makeText(this,"Error database",Toast.LENGTH_SHORT).show();
            }
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            //config.setAugmentedImageDatabase(imageDatabase);//dog
            session.configure(config);
        }
        catch(Exception e){}

    }

    private boolean BuildDatabase(Config config) {
        try
        {
            bitmapsList=loadImage();
            System.out.println("loadImageFuc"+bitmapsList);

//            System.out.println(bitmap);
//            if (bitmap==null)
//                return false;
            for(int i=0;i<11;i++){
                bitmapsListNo.add(i);
            }
            augmentedImageDatabase=new AugmentedImageDatabase(session);
            System.out.println("augmentedImageDatabase1"+augmentedImageDatabase);
            //把bitmapsList裡的圖片加到augmentedImageDatabase中
            for(int i=0;i<bitmapsList.size();i++){
                try {
                    augmentedImageDatabase.addImage(bitmapsList.get(i).toString(), bitmapsList.get(i));//samename
                    System.out.println("Add augmentedImageDatabase process:" + i);
                }catch(Exception e){
                    System.out.println("BadImageeeeee");
                }
            }
            //把bitmapsList裡的圖片加到augmentedImageDatabase中
            System.out.println("augmentedImageDatabase result:"+augmentedImageDatabase);
            config.setAugmentedImageDatabase(augmentedImageDatabase);

            return true;
        }
        catch(Exception e){
            System.out.println("augmentedImageDatabaseBad");
            return true;}

    }

    public Boolean Modelloading_finish_or_not = false;
    public Boolean go_or_not = false; // for render model or not
    public Boolean model_exist = false;

    @Override
    public void onUpdate(FrameTime frameTime) {

        try
        {

            Thread.currentThread().sleep(delay);//毫秒
            Frame frame=arview.getArFrame();

            Collection<AugmentedImage> updateAugmentedImg=frame.getUpdatedTrackables(AugmentedImage.class);

            System.out.println(frame.getCamera().getTrackingState());
            if(!go_or_not) {

                for (AugmentedImage image : updateAugmentedImg) {

                    System.out.println(image.getTrackingState());
                    if (image.getTrackingState() == TrackingState.TRACKING) {
                        for (int i = 0; i < bitmapsList.size(); i++) {
                            System.out.println(image.getName());
                            System.out.println(bitmapsList.get(i).toString());

                            if (image.getName().equals(bitmapsList.get(i).toString())) {  //samename
                                currentNumber = i;
                                System.out.println("currentNumber: " + currentNumber);



                                if(GLBLocationArray.get(currentNumber) != null)
                                {
                                    textLoading.setVisibility(View.VISIBLE);
                                    Modelloading_finish_or_not=false;
                                    loading=true;
                                    progressBar.setVisibility(View.VISIBLE);
//                                    toast3 = Toast.makeText(this, "模型加载中，请等候......", Toast.LENGTH_LONG);
//                                    toast3.show();

//                                    progressBar=(ProgressBar)findViewById(R.id.progressBar);
//                                    final int random=new Random().nextInt(26)+0;
//                                    progressBar.setProgress(random);

                                    //progressBar loading
                                    progressBar=(ProgressBar)findViewById(R.id.progressBar);
                                    // timer for seekbar
                                    final int oneMin = 1 * 60 * 300; // 1 minute in milli seconds
                                    progressBar.setProgress(0);
                                    percentage=new Random().nextFloat()*0.5+0.49;
                                    /** CountDownTimer starts with 1 minutes and every onTick is 1 second */
                                    new CountDownTimer(oneMin, 1000) {
                                        public void onTick(long millisUntilFinished) {


                                            //forward progress
                                            long finishedSeconds = oneMin - millisUntilFinished;
                                            int total = (int) (((float)finishedSeconds / (float)oneMin) * 100.0 * percentage);
                                            System.out.println("total: " +total);
                                            progressBar.setProgress(total);
                                        }

                                        public void onFinish() {
                                        }
                                    }.start();
                                    //progressBar loading

                                    WebView webview = (WebView) findViewById(R.id.web_view);
                                    webview.getSettings().setJavaScriptEnabled(true);
                                    webview.setWebViewClient(new WebViewClient());
                                    System.out.println("webview.loadUrl: " + "https://ar.keshufang.com/vr?id="+idArray.get(currentNumber));
                                    webview.loadUrl("https://ar.keshufang.com/vr?id="+idArray.get(currentNumber));

                                    ModelRenderable.builder()
                                            .setSource(this,

                                                    RenderableSource.builder().setSource(
                                                            this,
                                                            Uri.parse(GLBLocationArray.get(currentNumber).toString()),//modelLocationArray[currentselected]
                                                            RenderableSource.SourceType.GLB)
                                                            .build()
                                            )
                                            .setRegistryId(GLBLocationArray.get(currentNumber).toString())
                                            .build()
                                            .thenAccept(renderable -> onRenderableLoaded(renderable, image))
                                            .exceptionally(throwable -> {
                                                System.out.println(throwable);
//                                                Toast toast =
//                                                        Toast.makeText(this, "此教材有模型。", Toast.LENGTH_LONG);
//                                                toast.show();
                                                return null;
                                            });
                                }
                                else{

                                    Toast toast =
                                            Toast.makeText(this, "此教材尚无模型。", Toast.LENGTH_LONG);
                                    toast.show();
                                }



                                System.out.println("goooooo");
                                WebView webview = (WebView) findViewById(R.id.web_view);
                                webview.getSettings().setJavaScriptEnabled(true);
                                webview.setWebViewClient(new WebViewClient()); //不調用系統瀏覽器
                                webview.loadUrl("https://ar.keshufang.com/vr?id="+idArray.get(currentNumber));
                                System.out.println("hey:"+"https://ar.keshufang.com/vr?id="+idArray.get(currentNumber));
                                //webview.loadUrl("https://arteachingmaterial.herokuapp.com/vr?id="+idArray.get(currentNumber));
                                //System.out.println("https://ar.keshufang.com/vr?id="+idArray.get(currentNumber));


                                go_or_not = true;
//                                Config config=new Config(session);
//                                config.setAugmentedImageDatabase(new AugmentedImageDatabase(session));
//                                config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
//                                config.setFocusMode(Config.FocusMode.AUTO);
//                                session.configure(config);



                                TextView textView = (TextView) findViewById(R.id.titleNameText);
                                System.out.println(DocNameArray.get(currentNumber).toArray().length);
                                textView.setText(String.valueOf(DocNameArray.get(currentNumber).toArray().length));

                                ListAdapter adapter = new ArrayAdapter<>(this,
                                        android.R.layout.simple_list_item_1
                                        , DocNameArray.get(currentNumber));

                                ListView listView = (ListView) findViewById(R.id.listView);
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
                                        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                                        String temp = fileExt(DocLocationArray.get(currentNumber).get(i));



//                                        File sd = Environment.getExternalStorageDirectory();
//
//                                        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                                        Uri uri = Uri.parse(DocLocationArray.get(currentNumber).get(i));
//
//                                        DownloadManager.Request request = new DownloadManager.Request(uri);
//                                        request.setTitle("My File");
//                                        request.setDescription("Downloading");
//                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                                        request.setVisibleInDownloadsUi(false);
//                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,DocNameArray.get(currentNumber).get(i));
//
//                                        long r = downloadmanager.enqueue(request);
//
//                                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),DocNameArray.get(currentNumber).get(i));
//
//

                                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(DocLocationArray.get(currentNumber).get(i)));
                                        if(temp.contains("pdf"))
                                        {
                                            myIntent.setDataAndType(Uri.parse(DocLocationArray.get(currentNumber).get(i)), temp);

                                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                            startActivity(myIntent);
                                        }
                                        else
                                        {

                                            myIntent.setPackage("com.android.chrome");
                                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                            startActivity(myIntent);
                                        }


                                        //Toast.makeText(MainActivity.this,"點選第 "+(i +1) +" 個 \n內容："+DocLocationArray.get(currentNumber).get(i), Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        }
                    }

                }
            }
        }
        catch(Exception e){System.out.println(e);}

    }
    private String fileExt(String url) {
        String parts[]=url.split("\\.");

        String extension=parts[parts.length-1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }
    private void onRenderableLoaded(Renderable model, AugmentedImage image) {

//        AnchorNode cakeNode = new AnchorNode();
//        cakeNode.setRenderable(model);
//        cakeNode.setParent(arview.getScene());
//        cakeNode.setLocalPosition(new Vector3(0f, 0f, -1f));
//        System.out.println("checkk");
//        System.out.println(cakeNode);

        TransformationSystem ts = new TransformationSystem(getResources().getDisplayMetrics(), new FootprintSelectionVisualizer());


        TransformableNode transformableNode = new TransformableNode(ts);

//Connect transformableNode to anchorNode//

        transformableNode.setParent(arview.getScene());
        transformableNode.setRenderable(model);
        transformableNode.setLocalScale(new Vector3(0.1f, 0.1f, 0.1f));
        transformableNode.setWorldScale(new Vector3(0.1f, 0.1f, 0.1f));
        System.out.println(transformableNode.getLocalScale());

        transformableNode.getRotationController().setEnabled(true);
        transformableNode.getScaleController().setEnabled(true);
        transformableNode.getScaleController().setMinScale(0.06f);
        transformableNode.getScaleController().setMaxScale(2f);

        transformableNode.getTranslationController().setEnabled(true);

        current_anchornode = new AnchorNode();




        // make anchor in the center of the images
        current_anchornode.setAnchor(image.createAnchor(image.getCenterPose()));
        transformableNode.setParent(current_anchornode);

        arview.getScene().addChild(current_anchornode);

        transformableNode.select();

        arview.getScene().addOnPeekTouchListener(new Scene.OnPeekTouchListener() {
            @Override
            public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                ts.onTouch(hitTestResult, motionEvent);
            }
        });
        textLoading.setVisibility(View.GONE);
        Modelloading_finish_or_not=true;
        loading=false;
        progressBar.setVisibility(View.GONE);
        Toast toast2 = Toast.makeText(this, "模型加载完成！", Toast.LENGTH_LONG);
        toast2.show();
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(100);



    }



    @Override
    protected void onResume() {
        try
        {
            Thread.currentThread().sleep(delay);//毫秒
            super.onResume();


            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            setupSession();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(MainActivity.this,"Permission need to display camera" ,Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        }
                    }).check();
        }
        catch(Exception e){}

    }

    @Override
    protected void onPause() {
        try
        {
            Thread.currentThread().sleep(delay);//毫秒
            super.onPause();
            System.out.println("cool");
        }
        catch(Exception e){}

    }

    private void downloadFile3(int currentnumber){
        //下载路径，如果路径无效了，可换成你的下载路径
        //final String urlOBJ = "https://s3.ap-southeast-1.amazonaws.com/ar.materials/1561518345284.cent.obj";
        final String urlOBJ =modelLocationArray.get(currentnumber).toString();
        //final String urlMTL = "https://s3.ap-southeast-1.amazonaws.com/ar.materials/1561518353302.cent.mtl";
        final String urlMTL =textureLocationArray.get(currentnumber).toString();

        final long startTime = System.currentTimeMillis();
        Log.i("DOWNLOAD","startTime="+startTime);

        Request request = new Request.Builder().url(urlOBJ).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Log.i("DOWNLOAD","downloadFailed");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                Sink sink1 = null;
                BufferedSink bufferedSink = null;
                BufferedSink bufferedSink1 = null;
                //Context context = null;

                try {
                    String mSDCardPath= Environment.getExternalStorageDirectory().getAbsolutePath();
                    File dest = new File(mSDCardPath+"/Download",   urlOBJ.substring(urlOBJ.lastIndexOf("/") + 1));
                    //File dest = new File(context.getFilesDir(),   urlOBJ.substring(urlOBJ.lastIndexOf("/") + 1));
                    File dest1 = new File(mSDCardPath+"/Download",   urlMTL.substring(urlMTL.lastIndexOf("/") + 1));

                    sink = Okio.sink(dest);
                    sink1 = Okio.sink(dest1);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink1 = Okio.buffer(sink1);

                    bufferedSink.writeAll(response.body().source());
                    bufferedSink1.writeAll(response.body().source());

                    bufferedSink.close();
                    bufferedSink1.close();
                    Log.i("DOWNLOAD","downloadSuccess");
                    Log.i("DOWNLOAD","totalTime="+ (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("DOWNLOAD","downloadFile3 download failed");
                } finally {
                    if(bufferedSink != null){
                        bufferedSink.close();
                    }
                    if(bufferedSink1 != null){


                        bufferedSink1.close();
                    }

                }
            }
        });
    }

    private void downloadFile4(){
        //下载路径，如果路径无效了，可换成你的下载路径
        //final String urlOBJ = "https://s3.ap-southeast-1.amazonaws.com/ar.materials/1561518345284.cent.obj";
        final List urlOBJ =modelLocationArray;
        System.out.println("urlOBJ"+urlOBJ);
        //final String urlMTL = "https://s3.ap-southeast-1.amazonaws.com/ar.materials/1561518353302.cent.mtl";
        final List urlMTL =textureLocationArray;

        final long startTime = System.currentTimeMillis();
        Log.i("DOWNLOAD","startTime="+startTime);

        Request request = new Request.Builder().url(urlOBJ.get(1).toString()).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Log.i("DOWNLOAD","downloadFailed");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("urlOBJ1:"+urlOBJ);

                for(int j=0;j<urlOBJ.size();j++)
                {
                    Sink sink = null;
                    Sink sink1 = null;
                    BufferedSink bufferedSink = null;
                    BufferedSink bufferedSink1 = null;
                    //Context context = null;

                    try {
                        String mSDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                        File dest = new File(mSDCardPath + "/Download", urlOBJ.get(j).toString().substring(urlOBJ.lastIndexOf("/") + 1));
                        //File dest = new File(context.getFilesDir(),   urlOBJ.substring(urlOBJ.lastIndexOf("/") + 1));
                        //File dest1 = new File(mSDCardPath + "/Download", urlMTL.substring(urlMTL.lastIndexOf("/") + 1));
                        File dest1 = new File(mSDCardPath + "/Download", urlMTL.get(j).toString().substring(urlMTL.lastIndexOf("/") + 1));


                        sink = Okio.sink(dest);
                        sink1 = Okio.sink(dest1);
                        bufferedSink = Okio.buffer(sink);
                        bufferedSink1 = Okio.buffer(sink1);

                        bufferedSink.writeAll(response.body().source());
                        bufferedSink1.writeAll(response.body().source());

                        bufferedSink.close();
                        bufferedSink1.close();
                        Log.i("DOWNLOAD", "downloadSuccess");
                        Log.i("DOWNLOAD", "totalTime=" + (System.currentTimeMillis() - startTime));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("DOWNLOAD", "downloadFile4 download failed");
                    } finally {
                        if (bufferedSink != null) {
                            bufferedSink.close();
                        }
                        if (bufferedSink1 != null) {


                            bufferedSink1.close();
                        }

                    }

                }
            }
        });
    }


    public List GetimageLocationArray()throws Exception {
        //List imageLocationArray = new ArrayList();
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_0)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA)
                .build();
        //use Okhttp to get json from url
        OkHttpClient client = new OkHttpClient();
        client.newBuilder().protocols(Arrays.asList(Protocol.HTTP_1_1)).connectionSpecs(Collections.singletonList(spec));
        String url = "https://ar.keshufang.com/api/ar/arMaterials";
        Request request = new Request.Builder()

                .url(url)
                .build();

        Response response = client.newCall(request).execute();


        if (response.isSuccessful()) {
            String myResponse = response.body().string();
            System.out.println("testtt"+myResponse);
            jsonString = myResponse;
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });

            //處理json
            try {
                JSONArray json = new JSONArray(jsonString);
                // System.out.println("message : "+json.toString());

                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonObj = json.getJSONObject(i);

                    idArray.add(jsonObj.getString("_id"));
                    imageNameArray.add(jsonObj.getString("imageName"));
                    modelNameArray.add(jsonObj.getString("modelName"));
                    textureNameArray.add(jsonObj.getString("textureName"));
                    imageLocationArray.add(jsonObj.getString("imageLocation"));
                    modelLocationArray.add(jsonObj.getString("modelLocation"));
                    textureLocationArray.add(jsonObj.getString("textureLocation"));

//                                            if(jsonObj.getString("textureLocation")==null) textureLocationArray.add("null");
//                                            if(jsonObj.getString("textureLocation")!=null) textureLocationArray.add(jsonObj.getString("textureLocation"));


                    if(jsonObj.isNull("document_names"))

                        DocNameArray.add(null);
                    else {
                        ArrayList<String> list = new ArrayList<String>();
                        for (int x=0; x<jsonObj.getJSONArray("document_names").length(); x++) {
                            list.add( jsonObj.getJSONArray("document_names").get(x).toString());
                        }
                        DocNameArray.add(list);

                    }
                    if(jsonObj.isNull("document_locations"))
                        DocLocationArray.add(null);
                    else {
                        ArrayList<String> list = new ArrayList<String>();
                        for (int x=0; x<jsonObj.getJSONArray("document_locations").length(); x++) {

                            list.add( jsonObj.getJSONArray("document_locations").get(x).toString());
                        }
                        DocLocationArray.add(list);

                    }
                    if(jsonObj.isNull("TLGFmodelLocation"))
                        GLBLocationArray.add(null);
                    else {
                        GLBLocationArray.add(jsonObj.getString("TLGFmodelLocation"));

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("can not convert jsonString to jsonarray :\n");
            }
            System.out.println("idarray : " + idArray);
            System.out.println("imageNameArray : " + imageNameArray);
            System.out.println("modelNameArray : " + modelNameArray);
            System.out.println("textureNameArray : " + textureNameArray);
            System.out.println("imageLocationArray : " + imageLocationArray);
            System.out.println("modelLocationArray : " + modelLocationArray);
            System.out.println("textureLocationArray : " + textureLocationArray);
            System.out.println("DocNameArray : " + DocNameArray);
            System.out.println("DocLocationArray : " + DocLocationArray);

        }

            System.out.println("flower a: " + imageLocationArray);
            return imageLocationArray;
        }



}


