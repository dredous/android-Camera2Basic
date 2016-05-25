package com.example.android.camera2basic;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Edwin Kurniawan on 5/23/2016.
 */
public class CameraScreenActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }
}
