package com.diragi.leveldesigner;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindViews({ R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine,
            R.id.twoone, R.id.twotwo, R.id.twothree, R.id.twofour, R.id.twofive, R.id.twosix, R.id.twoseven, R.id.twoeight, R.id.twonine,
            R.id.thone, R.id.thtwo, R.id.ththree, R.id.thfour, R.id.thfive, R.id.thsix, R.id.thseven, R.id.theight, R.id.thnine,
            R.id.fourone, R.id.fourtwo, R.id.fourthree, R.id.fourfour, R.id.fourfive, R.id.foursix, R.id.fourseven, R.id.foureight, R.id.fournine,
            R.id.fiveone, R.id.fivetwo, R.id.fivethree, R.id.fivefour, R.id.fivefive, R.id.fivesix, R.id.fiveseven, R.id.fiveeight, R.id.fivenine,
            R.id.sixone, R.id.sixtwo, R.id.sixthree, R.id.sixfour, R.id.sixfive, R.id.sixsix, R.id.sixseven, R.id.sixeight, R.id.sixnine,
            R.id.sevone, R.id.sevtwo, R.id.sevthree, R.id.sevfour, R.id.sevfive, R.id.sevsix, R.id.sevseven, R.id.seveight, R.id.sevnine,
            R.id.eightone, R.id.eighttwo, R.id.eightthree, R.id.eightfour, R.id.eightfive, R.id.eightsix, R.id.eightseven, R.id.eighteight, R.id.eightnine,
            R.id.nineone, R.id.ninetwo, R.id.ninethree, R.id.ninefour, R.id.ninefive, R.id.ninesix, R.id.nineseven, R.id.nineeight, R.id.ninenine})
    List<CheckBox> boxes;

    @BindView(R.id.title) EditText title;
    @BindView(R.id.turns) EditText turns;
    @BindView(R.id.score) EditText score;

    int twodee[][] = new int[9][9];

    private FirebaseAuth auth;
    private Boolean toggle = true;
    private Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();

        auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "logged in anonymously" +task.isSuccessful());
                    }
                });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                make2D();
            }
        });
    }

    private void make2D() {
        int res = 0;
        for (int i = 0; i < 81; i++) {
            if (boxes.get(i).isChecked()) res = 1;
            if (!boxes.get(i).isChecked()) res = 0;
            if (i < 9) {
                //Row one
                twodee[0][i] = res;
            } else if (9 < i && i < 18) {
                //Row two
                twodee[1][i%9] = res;
            } else if (18 < i && i < 27) {
                //Row three
                twodee[2][i%9] = res;
            } else if (27 < i && i < 36) {
                //Row four
                twodee[3][i%9] = res;
            } else if (36 < i && i < 45) {
                //Row five
                twodee[4][i%9] = res;
            } else if (45 < i && i < 54) {
                //Row six
                twodee[5][i%9] = res;
            } else if (54 < i && i < 63) {
                //Row seven
                twodee[6][i%9] = res;
            } else if (63 < i && i < 72) {
                //Row eight
                twodee[7][i%9] = res;
            } else if (72 < i && i < 81) {
                //Row nine
                twodee[8][i%9] = res;
            }
        }
        gson = new Gson();
        String twodeeSerialized = gson.toJson(twodee);
        saveFile(twodeeSerialized, title.getText().toString());
    }

    private void toggleAll(Boolean b) {
        for (int i = 0; i < 81; i++) {
            boxes.get(i).setChecked(b);
        }
    }


    private void saveFile(String array, String name) {
        String filename = name;
        name += ".json";
        File file = new File(getApplicationContext().getFilesDir(), name);
        FileOutputStream outputStream;
        Boolean success = false;

        try {
            outputStream = openFileOutput(name, Context.MODE_PRIVATE);
            String scoreSerialized = gson.toJson(score.getText().toString());
            String turnsSerialized = gson.toJson(turns.getText().toString());
            outputStream.write(array.getBytes());
            outputStream.write(scoreSerialized.getBytes());
            outputStream.write(turnsSerialized.getBytes());
            outputStream.close();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (success) {
            sendToFB(file, name);
            Log.d("TAG0", file.getAbsolutePath());
        }
    }

    private void sendToFB(File file, String name) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://project-1677111012594498785.appspot.com");
        storageReference.child(name).putFile(Uri.fromFile(file));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            toggleAll(toggle);
            toggle = !toggle;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
