package com.example.android.camera2basic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

/**
 * Created by Edwin Kurniawan on 5/23/2016.
 */
public class UploadActivity extends Activity{

    private static final int PICK_IMAGE = 1000;
    private Button uplBtn, cntBtn, urlBtn;
    private ImageView imageView;
    private TextView countText;
    private String serverUrl = "";
    private AlertDialog.Builder alertBuilder;
    private SocketCommunication socketIo;
    private boolean communicationEstablished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);

        uplBtn = (Button) findViewById(R.id.uploadImgBtn);
        cntBtn = (Button) findViewById(R.id.countBtn);
        urlBtn = (Button) findViewById(R.id.urlButton);
        imageView = (ImageView) findViewById(R.id.uploaded_image);
        countText = (TextView) findViewById(R.id.countText);
//        addDialog();
        addListeners();
    }

    private void addDialog() {
        alertBuilder = new AlertDialog.Builder(UploadActivity.this);
        alertBuilder.setTitle("Input NGROK Server URL: ");

        final EditText input = new EditText(UploadActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alertBuilder.setView(input);
        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                serverUrl = input.getText().toString();
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    private void addListeners() {
        cntBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communicationEstablished == false && serverUrl != "") {
                    socketIo = new SocketCommunication(serverUrl);
                    communicationEstablished = true;
                } else {
                    return;
                }
                socketIo.sendMessage("countImg", "");
            }
        });
        uplBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
        urlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertBuilder = new AlertDialog.Builder(UploadActivity.this);
                alertBuilder.setTitle("Input NGROK Server URL: ");

                final EditText input = new EditText(UploadActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                alertBuilder.setView(input);
                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        serverUrl = input.getText().toString();
                    }
                });
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertBuilder.show();
            }
        });
    }

    private void resultDialog(String tag, boolean result) {
        alertBuilder = new AlertDialog.Builder(UploadActivity.this);
        if(tag == "upload" && result == true) {
            alertBuilder.setTitle("Success in Uploading Image to Server");
            alertBuilder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertBuilder.show();
        } else if(tag == "upload" && result == false) {
            alertBuilder.setTitle("Failed to Upload Image to Server");
            alertBuilder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertBuilder.show();
        } else
            return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE:
                if(resultCode == Activity.RESULT_OK && null != data){
                    Uri selectedImage = data.getData();
                    Log.d("Image URI", "" + selectedImage.toString());
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    if(picturePath != null) {
                        Bitmap bm = BitmapFactory.decodeFile(picturePath);
                        Bitmap resizedBm = Bitmap.createScaledBitmap(bm, 260, 360, false);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resizedBm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] b = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                        boolean result = socketIo.sendMessage("Image", encodedImage);
                        resultDialog("upload", result);
                        imageView.setImageBitmap(resizedBm);
                    }
                }
        }
    }

}
