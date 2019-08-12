package com.example.apple.test;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.apple.test.models.Item;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener,AdapterView.OnItemSelectedListener {
    private RelativeLayout layoutResult;
    private LinearLayout layoutSearch;
    private  LinearLayout layoutShake;
    private  LinearLayout layoutLoading;
    MyRecyclerViewAdapter adapter;
    Spinner spinnerRating;
    EditText editTextSearch;
    SeekBar seekBarLimit;
    RecyclerView recyclerViewList;
    private String language;
    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;
    AppDatabase database;
    String rating;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    layoutSearch.setVisibility(LinearLayout.GONE);
                    layoutResult.setVisibility(LinearLayout.GONE);
                    layoutShake.setVisibility(LinearLayout.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    layoutSearch.setVisibility(LinearLayout.VISIBLE);
                    layoutResult.setVisibility(LinearLayout.GONE);
                    layoutShake.setVisibility(LinearLayout.GONE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        spinnerRating = (Spinner) findViewById(R.id.spinnerRating);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        seekBarLimit = (SeekBar) findViewById(R.id.seekBarLimit);
        layoutResult = (RelativeLayout) findViewById(R.id.layoutResult);
        layoutSearch = (LinearLayout) findViewById(R.id.layoutSearch);
        layoutShake = (LinearLayout) findViewById(R.id.layoutShake);
        layoutLoading = (LinearLayout) findViewById(R.id.layoutLoading);
        recyclerViewList = findViewById(R.id.recyclerViewList);

        ArrayAdapter<CharSequence> contentAdapter = ArrayAdapter.createFromResource(this,
                R.array.content_rating, android.R.layout.simple_spinner_item);
        contentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRating.setAdapter(contentAdapter);
        spinnerRating.setOnItemSelectedListener(this);

        /* Not needed, just to show its usage */
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("APIKey", "Vi0BzPAqqIYaYi8j3XbY2qhr4XeVGuQc");
        editor.commit();

        language = "all";
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        database = Room.databaseBuilder(this, AppDatabase.class, "gifDB")
                .allowMainThreadQueries()
                .build();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {
                String[] items = new String[]{"horse","car","cat","sheep","university","cow","evil","home","porn","tiger","moon","sun","galaxy"};
                Toast.makeText(HomeActivity.this, "Fetching awesome GIF", Toast.LENGTH_SHORT).show();
                int rnd = new Random().nextInt(items.length);
                new SearchDownloader(HomeActivity.this,items[rnd],1,"en","G").execute();
            }
        });

        recyclerViewList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, new ArrayList<ResponseObject>());
        adapter.setClickListener(this);
        recyclerViewList.setAdapter(adapter);
    }

    public void onRadioButtonLanguageClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radioButtonAll:
                if (checked)
                   language="all";
                break;
            case R.id.radioButtonArabic:
                if (checked)
                    language="ar";
                break;
            case R.id.radioButtonEnglish:
                if (checked)
                    language="en";
                break;
        }
    }

    public void shareItem(String id,String title){
        String url = "https://media.giphy.com/media/"+id+"/giphy.gif";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = title +" "+ url;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Magic Box");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share Now"));
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        switch(pos){
            case 0:
                rating = "G";
                break;
            case 1:
                rating = "PG";
                break;
            case 2:
                rating = "PG-13";
                break;
            case 3:
                rating = "R";
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    @Override
    public void onShareClick(String id,String title) {
        shareItem(id,title);
    }

    @Override
    public void onRatingChanged(String id,int rating) {
        ItemDAO itemDAO = database.getItemDAO();
        Item result = itemDAO.getItemById(id);
        Item item = new Item();
        item.setId(id);
        item.setRating(rating);
        if(result == null){
            Log.e("%%%%%","insert");
            itemDAO.insert(item);
        }else{
            Log.e("%%%%%","update");
            itemDAO.update(item);
        }
    }

    public void onSearch(View view){
        String search=editTextSearch.getText().toString();
        int limit=seekBarLimit.getProgress()*10;
        new SearchDownloader(this,search,limit,language,rating).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }


    private class SearchDownloader extends AsyncTask<Void,Void,List> {

        private Context activityReference;
        private String search;
        private int limit;
        private String language;
        private String rating;

        public SearchDownloader(Context context,String search,int limit,String language,String rating) {
            super();
            this.activityReference = context;
            this.search=search;
            this.limit=limit;
            this.language=language;
            this.rating=rating;
        }

        @Override
        protected List doInBackground(Void... voids) {
            JsonReader jsonReader;
            List<ResponseObject> list;
            list = new ArrayList<ResponseObject>();
            try {
                String APIKey = sharedPreferences.getString("APIKey","");
                String fullURL="https://api.giphy.com/v1/gifs/search?api_key="+APIKey;
                fullURL += "&q="+search;
                fullURL +="&offset=0";
                fullURL +="&limit="+limit;
                if(!language.equals("all"))
                fullURL +="&lang="+language;
                fullURL +="&rating="+rating;

                Log.e("#####",fullURL);

                URL url = new URL(fullURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                if (connection.getResponseCode() != 200) {
                    Log.e("#####","Unable to load data");
                    return list;
                }
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader =
                        new InputStreamReader(responseBody, "UTF-8");
                jsonReader = new JsonReader(responseBodyReader);

                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String key = jsonReader.nextName();
                    if (key.equals("data")) {
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            list.add(readElement(jsonReader));
                        }
                        jsonReader.endArray();
                    } else {
                        jsonReader.skipValue();
                    }
                }
                jsonReader.endObject();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("#####","Unable to connect");
            }
            return list;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layoutLoading.setVisibility(LinearLayout.VISIBLE);
        }

        @Override
        protected void onPostExecute(List result) {
            super.onPostExecute(result);
            layoutSearch.setVisibility(LinearLayout.GONE);
            layoutShake.setVisibility(LinearLayout.GONE);
            layoutResult.setVisibility(LinearLayout.VISIBLE);
            layoutLoading.setVisibility(LinearLayout.GONE);
            adapter.setElements(result);
            adapter.notifyDataSetChanged();
        }

        public ResponseObject readElement(JsonReader jsonReader) throws Exception {
            jsonReader.beginObject();
            String id="";
            String title="";
            String createdOn="";
            while (jsonReader.hasNext()){
                String name = jsonReader.nextName();
                if (name.equals("id")){
                    id = jsonReader.nextString();
                }
                else if (name.equals("title")){
                    title = jsonReader.nextString();
                }else if (name.equals("import_datetime")){
                    createdOn = jsonReader.nextString();
                }else{
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            ItemDAO itemDAO = database.getItemDAO();
            Item result = itemDAO.getItemById(id);
            int rating = (result!=null)? result.getRating():0;
            return new ResponseObject(id,title,createdOn,rating);
        }

    }

}
