package com.library.example.cifar10.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.library.example.cifar10.adapters.OptionAdapter;
import com.library.example.cifar10.R;
import com.library.example.cifar10.beans.ResponseOption;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.library.example.cifar10.messagepack.ParamUnpacker;
import com.library.example.cifar10.network.CNNdroid;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

public class MainActivity extends Activity {

    private ListView listView;
    private OptionAdapter adapter;

    private ImageView img;
    private Button btnMakePhoto;
    private Button btnTakePhoto;

    private RenderScript myRenderScript;
    private boolean condition = false;
    private CNNdroid myConv = null;
    private String[] labels;
    private long loadTime;

    private final int PHOTO_TAKE = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        img = (ImageView) findViewById(R.id.imageView);
        listView = (ListView) findViewById(R.id.listView);
        btnMakePhoto = (Button) findViewById(R.id.btnMakePhoto);
        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        myRenderScript = RenderScript.create(this);

        readLabels();

        new prepareModel().execute(myRenderScript);
    }

    private class prepareModel extends AsyncTask<RenderScript, Void, CNNdroid> {

        ProgressDialog progDailog;
        protected void onPreExecute ()
        {
            btnMakePhoto.setVisibility(View.GONE);
            btnTakePhoto.setVisibility(View.GONE);
            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Please Wait...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        @Override
        protected CNNdroid doInBackground(RenderScript... params) {
            loadTime = System.currentTimeMillis();
            try {
                myConv = new CNNdroid(myRenderScript, "/Removable/MicroSD/data_model/def.txt");
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadTime = System.currentTimeMillis() - loadTime;
            return myConv;
        }
        protected void onPostExecute(CNNdroid result) {
            btnMakePhoto.setVisibility(View.VISIBLE);
            btnTakePhoto.setVisibility(View.VISIBLE);
            condition = true;
            progDailog.dismiss();
        }
    }

    private ArrayList<ResponseOption> accuracy(float[] input_matrix, String[] labels, int topk) {
        String result = "";
        int[] max_num = {-1, -1, -1, -1, -1};
        float[] max = new float[topk];
        for (int k = 0; k < topk ; ++k) {
            for (int i = 0; i < 10; ++i) {
                if (input_matrix[i] > max[k]) {
                    boolean newVal = true;
                    for (int j = 0; j < topk; ++j)
                        if (i == max_num[j])
                            newVal = false;
                    if (newVal) {
                        max[k] = input_matrix[i];
                        max_num[k] = i;
                    }
                }
            }
        }

        ArrayList<ResponseOption> resultAnswer = new ArrayList<>();
        for (int i = 0 ; i < topk ; i++) {
            resultAnswer.add(new ResponseOption(labels[max_num[i]], max[i] * 100));
        }

        return resultAnswer;
    }

    public void takePhoto(View view) {
        Crop.pickImage(this);
    }

    public void makePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(new File("/sdcard/flashCropped.png"));
        takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, PHOTO_TAKE);
        }
    }

    private void algorithm(Bitmap bmp) {
        float[][][][] inputBatch = new float[1][3][32][32];

        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp, 200, 200, true);
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, 32, 32, false);
        img.setImageBitmap(bmp1);

        ParamUnpacker pu = new ParamUnpacker();
        float[][][] mean = (float[][][]) pu.unpackerFunction("/Removable/MicroSD/data_model/mean.msg", float[][][].class);

        for (int j = 0; j < 32; ++j)
            for (int k = 0; k < 32; ++k) {
                int color = bmp2.getPixel(j, k);
                inputBatch[0][0][k][j] = (float) (blue(color)) - mean[0][j][k];
                inputBatch[0][1][k][j] = (float) (green(color)) - mean[1][j][k];
                inputBatch[0][2][k][j] = (float) (red(color)) - mean[2][j][k];
            }

        float[][] output = (float[][]) myConv.compute(inputBatch);

        adapter = new OptionAdapter(this, accuracy(output[0], labels, 3));
        listView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == PHOTO_TAKE) {
             Crop.pickImage(this);
         } else if(requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
             beginCrop(data.getData());
         } else if (requestCode == Crop.REQUEST_CROP) {
             handleCrop(resultCode, data);
         }
    }

    private void beginCrop(Uri source) {
        Log.d("TAG", getCacheDir().toString());
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                //Bitmap bmp = (Bitmap) result.getExtras().get("data");
                //img.setImageBitmap(bmp);
                algorithm(bmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void readLabels() {
        /*String m_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        Log.d("TAG", m_path);
        labels = new String[1000];
        File f = new File("/Removable/MicroSD/data_model/labels.txt");
        Scanner s = null;
        int iter = 0;

        try {
            s = new Scanner(f);
            while (s.hasNextLine()) {
                String str = s.nextLine();
                labels[iter++] = str;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        labels = new String[] {"airplane", "automobile", "bird", "cat", "deer", "dog", "frog", "horse", "ship", "truck"};
    }

}
