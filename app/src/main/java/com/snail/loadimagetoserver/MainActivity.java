package com.snail.loadimagetoserver;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        bLoad.setOnClickListener(view -> LoadImageToServer());
    }

    /**Check selection image
     *
     */
    private void LoadImageToServer() {
        if (uri == null) {
            Toast.makeText(this, "Выберите изображение", Toast.LENGTH_LONG).show();
            return;
        }

        uploadFile(uri);
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

    /** Upload file to server with retrofit
     *
     * @param fileUri file Uri
     */
    private void uploadFile(Uri fileUri) {
        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);

        File file = null;
        try {
            file = FileUtil.from(MainActivity.this,fileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (file == null) {
            return;
        }

        RequestBody requestFile = RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        String descriptionString = "description";
        RequestBody description = RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        Call<ResponseBody> call = service.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.v("Upload", "success");
                Toast.makeText(getApplicationContext(), "Загрузка завершена", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("Upload error:", t.getMessage());
                Toast.makeText(getApplicationContext(), "Ошибка при загрузке", Toast.LENGTH_LONG).show();
            }
        });
    }
}