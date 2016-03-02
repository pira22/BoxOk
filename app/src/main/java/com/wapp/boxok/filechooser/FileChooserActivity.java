package com.wapp.boxok.filechooser;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wapp.boxok.R;
import com.wapp.boxok.SplashActivity;
import com.wapp.boxok.filechooser.adapters.FileArrayAdapter;
import com.wapp.boxok.filechooser.adapters.ListAdapter;
import com.wapp.boxok.model.Advertise;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;

public class FileChooserActivity extends Activity {

    private File currentFolder;
    private FileArrayAdapter fileArrayListAdapter;
    private FileFilter fileFilter;
    private File fileSelected;
    private ArrayList<String> extensions;
    ListView listview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        listview= (ListView) findViewById(R.id.list_file);
        /*Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getStringArrayList(Constants.KEY_FILTER_FILES_EXTENSIONS) != null) {
                extensions = extras.getStringArrayList(Constants.KEY_FILTER_FILES_EXTENSIONS);
                fileFilter = new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return ((pathname.isDirectory()) || (pathname.getName()
                                .contains(".") ? extensions.contains(pathname
                                .getName().substring(
                                        pathname.getName().lastIndexOf(".")))
                                : false));
                    }
                };
            }
        }*/
        //currentFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        currentFolder = new File("/mnt/sda/sda1");
        //Log.i("FILE CHOOSER", currentFolder.getAbsolutePath());
        //Log.i("root", Environment.getRootDirectory().getAbsolutePath());
        //addAdvertising(fileSelected.getAbsolutePath());
        Toast.makeText(getApplicationContext(), currentFolder.getParentFile().getPath(), Toast.LENGTH_LONG).show();
        //File f = new File();
        fill(currentFolder);
    }


/*
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Toast.makeText(getApplicationContext(),"key down", Toast.LENGTH_SHORT).show();

       // if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
        Toast.makeText(getApplicationContext(),"key pressed", Toast.LENGTH_SHORT).show();

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((!currentFolder.getName().equals(
                    Environment.getExternalStorageDirectory().getName()))
                    && (currentFolder.getParentFile() != null)) {
                currentFolder = currentFolder.getParentFile();
                fill(currentFolder);
            } else {

                setResult(Activity.RESULT_CANCELED);
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
*/
    private void fill(File f) {
        File[] folders = null;
        if (fileFilter != null)
            folders = f.listFiles(fileFilter);
        else
            folders = f.listFiles();

        this.setTitle(getString(R.string.currentDir) + ": " + f.getName());
        List<FileInfo> dirs = new ArrayList<FileInfo>();
        List<FileInfo> files = new ArrayList<FileInfo>();
        try {
            for (File file : folders) {
                if (file.isDirectory() && !file.isHidden())
                    //si es un directorio en el data se ponemos la contante folder
                    dirs.add(new FileInfo(file.getName(),
                            Constants.FOLDER, file.getAbsolutePath(),
                            true, false));
                else {
                    if (!file.isHidden())
                        files.add(new FileInfo(file.getName(),
                                getString(R.string.fileSize) + ": "
                                        + file.length(),
                                file.getAbsolutePath(), false, false));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(dirs);
        Collections.sort(files);
        dirs.addAll(files);
        if (!f.getName().equalsIgnoreCase(
                Environment.getExternalStorageDirectory().getName())) {
            if (f.getParentFile() != null)
                //si es un directorio padre en el data se ponemos la contante adeacuada
                dirs.add(0, new FileInfo("..",
                        Constants.PARENT_FOLDER, f.getParent(),
                        false, true));
        }
        fileArrayListAdapter = new FileArrayAdapter(FileChooserActivity.this,
                R.layout.file_row, dirs);
        //this.setListAdapter(fileArrayListAdapter);

        listview.setAdapter(fileArrayListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInfo fileDescriptor = fileArrayListAdapter.getItem(position);
                if (fileDescriptor.isFolder() || fileDescriptor.isParent()) {
                    currentFolder = new File(fileDescriptor.getPath());
                    Log.i("FILE CHOOSER", fileDescriptor.getPath());
                    fill(currentFolder);
                }else {

                    fileSelected = new File(fileDescriptor.getPath());
                    Intent intent = new Intent();
                    intent.putExtra(Constants.KEY_FILE_SELECTED,
                            fileSelected.getAbsolutePath());
                    setResult(Activity.RESULT_OK, intent);
                    Log.i("FILE CHOOSER", fileSelected.getAbsolutePath());
                    Toast.makeText(getApplicationContext(), fileSelected.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                    finish();
                }
                //Log.d("FF",fileDescriptor.toString());
                //Toast.makeText(FileChooserActivity.this, "You Clicked at "+fileDescriptor.getName(), Toast.LENGTH_SHORT).show();
            }
        });



    }

   // @Override
    protected void onListItemClick( View v, int position, long id) {
       // super.onListItemClick(l, v, position, id);
        FileInfo fileDescriptor = fileArrayListAdapter.getItem(position);

        if (fileDescriptor.isFolder() || fileDescriptor.isParent()) {
            currentFolder = new File(fileDescriptor.getPath());
            fill(currentFolder);
        } else {

            fileSelected = new File(fileDescriptor.getPath());
            Intent intent = new Intent();
            intent.putExtra(Constants.KEY_FILE_SELECTED,
                    fileSelected.getAbsolutePath());
            setResult(Activity.RESULT_OK, intent);
            Log.i("FILE CHOOSER", fileSelected.getAbsolutePath());
            //addAdvertising(fileSelected.getAbsolutePath());
            Toast.makeText(getApplicationContext(), fileSelected.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            //Intent returnIntent = new Intent();
            //returnIntent.putExtra("result",fileSelected.getAbsolutePath());
            //setResult(RESULT_OK,returnIntent);

            finish();
        }
    }

    boolean addAdvertising(final String path){
        // Obtain a Realm instance
        Realm realm = Realm.getInstance(this);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Advertise ad = realm.createObject(Advertise.class);
                ad.setPath(path);
            }
        });
        return true;
    }



}