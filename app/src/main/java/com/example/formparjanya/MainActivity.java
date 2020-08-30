package com.example.formparjanya;


import android.content.pm.PackageManager;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
/* ----------------------- */

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

/* ----------------------- */
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.os.Bundle;
//import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.amazonaws.AmazonServiceException;
//import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;


import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final int ACTIVITY_SELECT_IMAGE = 1234;
    private static final int REQUEST_CAMERA_CAPTURE = 1111;
    private static final int STORAGE_ACCESS_PERMISSION_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "Main Activity";

    //Variables
    private FileUriCallback fileUriCallback;
    private Uri[] fileUris = new Uri[10];
    Button camButton;
    Button camPhoto;
    Button upload;
    Button submit;
    Button nfcShare;
    Context context;
    ImageButton galPhoto;
    ImageButton imgButton;
    MainActivity mainActivity;
    File photoFile = null;
    String mCurrentPhotoPath = "";
    NfcAdapter nfcAdapter;
    NdefMessage mNdefMessage;
    EditText StudentId;

    private class FileUriCallback implements
            NfcAdapter.CreateBeamUrisCallback {
        public FileUriCallback() {

        }

        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            String transferFile = "sample.txt";
            File sdcard = Environment.getExternalStorageDirectory();
            File dir = new File(sdcard.getAbsolutePath() + "/text/");
            File requestFile = new File(dir, transferFile);
            requestFile.setReadable(true, false);
            Uri fileUri = Uri.fromFile(requestFile);
            if (fileUri != null) {
                fileUris[0] = fileUri;
                Toast.makeText(context, "Uri set", Toast.LENGTH_LONG);
            } else {
                Toast.makeText(context, "Uri not set", Toast.LENGTH_LONG);
                Log.e("My Activity", "No File URI available for file.");
            }
            return fileUris;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        StudentId = findViewById(R.id.StudentID);
        upload = findViewById(R.id.camPhoto);
        galPhoto = findViewById(R.id.galPhoto);
        camPhoto = findViewById(R.id.camPhoto);
        imgButton = findViewById(R.id.galPhoto);
        submit = findViewById(R.id.submit );
        nfcShare = findViewById(R.id.nfcShare);
        context = this;
        mainActivity = this;

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            //Toast.makeText(this, "NFC available", Toast.LENGTH_LONG).show();
        }
        else{
            //Toast.makeText(this, "NFC unavailable", Toast.LENGTH_LONG).show();
            finish();
        }

        if (nfcAdapter != null) {
            Toast.makeText(this, "NFC enabled", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This phone is not NFC enabled.", Toast.LENGTH_LONG).show();
        }

        nfcShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "onClick", Toast.LENGTH_LONG);
                fileUriCallback = new FileUriCallback();
                nfcAdapter.setBeamPushUrisCallback(fileUriCallback, mainActivity);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StudentId.getText().toString().isEmpty()) {
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (checkPermission()) {
                                File sdcard = Environment.getExternalStorageDirectory();
                                File dir = new File(sdcard.getAbsolutePath() + "/text/");
                                dir.mkdir();
                                File file = new File(dir, "sample.txt");
                                FileOutputStream os = null;
                                try {
                                    os = new FileOutputStream(file);
                                    os.write(StudentId.getText().toString().getBytes());
                                    os.close();
                                    Toast.makeText(context, "saved", Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                requestPermission(); // Code for permission
                            }

                        } else {
                            File sdcard = Environment.getExternalStorageDirectory();
                            File dir = new File(sdcard.getAbsolutePath() + "/text/");
                            dir.mkdir();
                            File file = new File(dir, "sample.txt");
                            FileOutputStream os = null;
                            try {
                                os = new FileOutputStream(file);
                                os.write(StudentId.getText().toString().getBytes());
                                os.close();
                                Toast.makeText(context, "saved", Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });


        // create an NDEF message with two records of plain text type
        mNdefMessage = new NdefMessage(
                new NdefRecord[] {
                        createNewTextRecord("First sample NDEF text record", Locale.ENGLISH, true),
                        createNewTextRecord("Second sample NDEF text record", Locale.ENGLISH, true) });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to create files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
            case STORAGE_ACCESS_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
                else Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public static NdefRecord createNewTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char)(utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte)status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    /*@Override
    public void onResume() {
        super.onResume();

        if (nfcAdapter != null)
            nfcAdapter.enableForegroundNdefPush(this, mNdefMessage);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (nfcAdapter != null)
            nfcAdapter.disableForegroundNdefPush(this);
    }*/

    @Override
    protected void onResume(){
        super.onResume();
        enableForegroundDispatchSystem();
    }

    private void enableForegroundDispatchSystem() {
        Intent nfcIntent = new Intent(this, MainActivity.class);
        nfcIntent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0);
        IntentFilter[] intentFilter = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);
    }

    @Override
    protected void onPause(){

        disableForegroundDispatchSystem();

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(nfcAdapter.EXTRA_TAG)){
            Toast.makeText(this, "NFC Intent", Toast.LENGTH_LONG).show();

            Tag tag = intent.getParcelableExtra(nfcAdapter.EXTRA_TAG);
            NdefMessage ndefMessage = createNdefMessage("My String Content");
            writeNdefMessage(tag, ndefMessage);
        }
    }


    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage){
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if(ndefFormatable == null){
                Toast.makeText(this, "tag is not ndef formatable", Toast.LENGTH_LONG).show();
            }
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
            Toast.makeText(this, "Tag written", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Log.i("formatTag", e.getMessage());
        }
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage){
        try {
            if(tag == null){
                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_LONG).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);
            if(ndef == null){
                formatTag(tag, ndefMessage);
            }
            else {
                ndef.connect();
                if(!ndef.isWritable()){
                    Toast.makeText(this, "Tag object is not writable", Toast.LENGTH_LONG).show();
                    ndef.close();
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this, "Tag written", Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            Log.i("writeNdefmsg", e.getMessage());
        }
    }

    private NdefRecord createTextRecord(String content){
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");
            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
        }catch (Exception e){
            Log.i("createRecord", e.getMessage());
        }
        return null;
    }

    private NdefMessage createNdefMessage(String content){
        NdefRecord ndefRecord = createTextRecord(content);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] {ndefRecord});
        return ndefMessage;
    }

    public void requestPermission(String PERMISSION_STRING) {
        if(ContextCompat.checkSelfPermission(context, PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(context, "Permission already granted!!", Toast.LENGTH_SHORT).show();
        else requestNewPermission(PERMISSION_STRING);
    }

    private void requestNewPermission(String PERMISSION_STRING) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, PERMISSION_STRING)) {
            new AlertDialog.Builder(this)
                    .setTitle("Title")
                    .setMessage("This permission is needed")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(mainActivity, new String[] {
                            PERMISSION_STRING
                    }, STORAGE_ACCESS_PERMISSION_CODE)).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {
                    PERMISSION_STRING
            }, STORAGE_ACCESS_PERMISSION_CODE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        galPhoto.setOnClickListener(v -> {
            Intent openImageFromGalleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(openImageFromGalleryIntent, ACTIVITY_SELECT_IMAGE);
            camButton = findViewById(R.id.camPhoto);
            camButton.setEnabled(false);
        });
        if(!hasCamera()){
            camPhoto.setEnabled(false);
        }

    }

    public boolean  hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    // Creating Image File: Creating a file with unique name

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );


        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Displaying Message

    private void displayMessage(Context context, String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    // Launch camera and save the image file and get its path

    public void launchCamera(View view){
        /*Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        File f = new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/Camera/temp.jpg");
        //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(takePictureIntent, REQUEST_CAMERA_CAPTURE);
        Log.i("FilePath",f.getAbsolutePath());*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        else
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                try {

                    photoFile = createImageFile();
                    displayMessage(getBaseContext(),photoFile.getAbsolutePath());
                    Log.i("Path of photoFile",photoFile.getAbsolutePath());

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.example.formparjanya.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_CAMERA_CAPTURE);
                    }
                } catch (Exception ex) {
                    // Error occurred while creating the File
                    displayMessage(getBaseContext(),ex.getMessage().toString());
                }


            }else
            {
                displayMessage(getBaseContext(),"Nullll");
            }
        }



    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case ACTIVITY_SELECT_IMAGE:
                if(resultCode == RESULT_OK && data != null){
                    Uri selectedImage = data.getData();
                    String path = data.getData().getPath();
                    Log.i(TAG, "onActivityResult: path: " + path);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    String newPath = "/storage/emulated/0/DCIM/Camera/IMG_20200701_122117.jpg";
                    File f = new File(filePath);
                    String imgPath = f.getAbsolutePath();
                    Log.i("File f", imgPath);
                    TextView imgname = findViewById(R.id.imgName);
                    imgname.setText(imgPath);
                    String KEY = "";
                    String SECRET = "";
                    String bucketName = "test-bucket-1bm";

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                    StrictMode.setThreadPolicy(policy);
                    BasicAWSCredentials awsCreds = new BasicAWSCredentials(KEY, SECRET);

                    AmazonS3Client s3Client = new AmazonS3Client(awsCreds);
                    InputStream is = null;
                    try {
                        is = new FileInputStream(f);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(is != null){
                        PutObjectRequest putRequest1 = new PutObjectRequest(bucketName, "image3.jpg", is, new ObjectMetadata());
                        PutObjectResult response1 = s3Client.putObject(putRequest1);
                    }
                    else{
                        Log.i("Error" , "is is null");
                    }

                }
                break;
            case REQUEST_CAMERA_CAPTURE:
                //if(resultCode == RESULT_OK){
                    /*Bitmap img = (Bitmap) data.getExtras().get("data");
                    //URI imgUri = (URI) data.getExtras().get("data");
                    ImageView capturedImg = (ImageView) findViewById(R.id.capturedImg);
                    capturedImg.setImageBitmap(img);




                    /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    InputStream is = new ByteArrayInputStream(baos.toByteArray());*/
                /*File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/");
                for (File temp : f.listFiles()) {
                    Log.i("FilePathI", temp.getName() + " :: " + temp.getAbsolutePath());
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        Log.i("FilePathFound",f.getAbsolutePath() + "/temp.jpg");
                        break;
                    }
                }

                Log.i("FilePathResult",f.getAbsolutePath());
                String path = "";
                try {
                    Bitmap bitmap = null;
                    //BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    //bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                      //      bitmapOptions);
                    //viewImage.setImageBitmap(bitmap);
                    path = android.os.Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                            + File.separator
                            + "Camera/";
                    Log.i("FilePathSaved1", "Image saved to : " + path);
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                        Log.i("FilePathSaved2", "Image saved to : " + path);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                File imgf = new File(path);

                    String KEY = "";
		    String SECRET = ""; 
                    String bucketName = "test-bucket-1bm";

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                    StrictMode.setThreadPolicy(policy);
                    BasicAWSCredentials awsCreds = new BasicAWSCredentials(KEY, SECRET);

                    AmazonS3Client s3Client = new AmazonS3Client(awsCreds);
                    //PutObjectRequest putRequest1 = new PutObjectRequest(bucketName, "image3.jpg", new File(imgUri));
                    //PutObjectResult response1 = s3Client.putObject(putRequest1);
                    InputStream is = null;
                    try {
                        is = new FileInputStream(imgf);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(is != null){
                        PutObjectRequest putRequest1 = new PutObjectRequest(bucketName, "image3.jpg", is, new ObjectMetadata());
                        PutObjectResult response1 = s3Client.putObject(putRequest1);

                    /*}
                    else{
                        Log.i("Error" , "is is null");
                    }*/



                //}
                Log.i("FilePathSaved", "mCurrentPhotoPath" + mCurrentPhotoPath + " photoFile path : " + photoFile.getAbsolutePath());
                break;
        }

    }
}

