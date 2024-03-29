package com.example.isongs;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listSongs;
    String[] items = {"Song 1", "Song 2", "Song 3"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listSongs = findViewById(R.id.listSongs);
        requestPermissions();
    }
    private void requestPermissions(){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getSongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }) .check();
    }
    private ArrayList<File> findSong(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
        for (File singlefile : files) {
            if (singlefile.isDirectory() && !singlefile.isHidden()) {
                arrayList.addAll(findSong(singlefile));
            } else {
                if (singlefile.getName().endsWith(".mp3")) {
                    arrayList.add(singlefile);
                }
            }
        }
        }
        return arrayList;
    }
    private void getSongs(){

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Handle the case when external storage is not available
            return;
        }

        final ArrayList<File> songs = findSong(Environment.getExternalStorageDirectory());
        items = new String[songs.size()];

        for (int i=0; i<songs.size(); i++){
            items[i] = songs.get(i).getName().replace(".mp3","");
        }
        ArrayAdapter<String> songsAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,items);
        listSongs.setAdapter(songsAdapter);

        listSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String song = listSongs.getItemAtPosition(position).toString();
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                        .putExtra("songs", songs)
                        .putExtra("song", song)
                        .putExtra("position", position));
            }
        });
    }
}