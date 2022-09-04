package com.snail.loadimagetoserver;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/** Main class of launched activity */
public class MainActivity extends AppCompatActivity {

    private ImageView avatarImage;
    private Uri       uri;

    /**onCreate method of MainActivity
     *
     * @param savedInstanceState a reference to a Bundle object that is passed into the onCreate method of every Android Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uri = null;

        avatarImage  = findViewById(R.id.imageViewAvatar);
        Button bLoad = findViewById(R.id.buttonLoad);

        avatarImage.setOnClickListener(this::openFileDialog);
        bLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadImageToServer();
            }
        });
    }

    private void LoadImageToServer() {
        if (uri == null) {
            Toast.makeText(this, "Выберите изображение", Toast.LENGTH_LONG).show();
            return;
        }


    }

    /** Browser for select avatar */
    ActivityResultLauncher<Intent> selectActivityRes = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        uri = data.getData();
                        avatarImage.setImageURI(uri);
                    }
                }
            }
    );

    /**Method to open explorer for select avatar image
     *
     * @param view Button select avatar
     */
    public void openFileDialog(View view) {
        Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        data.setType("image/*");
        data = Intent.createChooser(data, "Choose an avatar");
        selectActivityRes.launch(data);
    }
}