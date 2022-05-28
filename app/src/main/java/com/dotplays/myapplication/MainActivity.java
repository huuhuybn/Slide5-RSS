package com.dotplays.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    String link = "https://vnexpress.net/rss/tin-moi-nhat.rss";
    ListView lvList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvList = findViewById(R.id.lvList);
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                return getXMLFromVnExpress();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ArrayList<TinTuc> tinTucs = (ArrayList<TinTuc>) o;
                // viet cau lenh ListView vs Adapter
            }
        };
        asyncTask.execute();

    }

    public ArrayList<TinTuc> getXMLFromVnExpress() {
        ArrayList<TinTuc> tinTucs = new ArrayList<>();
        try {
            URL url = new URL(link);
            HttpURLConnection httpURLConnection =
                    (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
//            Scanner scanner = new Scanner(inputStream);
//            String data = "";
//            while (scanner.hasNext()) {
//                String line = scanner.nextLine();
//                data += line;
//            }
//            scanner.close();
//            Log.e("data", data);

            XmlPullParser pullParser = Xml.newPullParser();
            pullParser.setInput(inputStream, "utf-8");
            pullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            int eventType = pullParser.getEventType();
            TinTuc tinTuc = null;
            String text = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = pullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (name.equalsIgnoreCase("item")) {
                            tinTuc = new TinTuc();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = pullParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tinTuc!= null) {
                            if (name.equalsIgnoreCase("title")) tinTuc.setTitle(text);
                            else if (name.equalsIgnoreCase("description")) tinTuc.setDescription(text);
                            else if (name.equalsIgnoreCase("pubDate")) tinTuc.setPubDate(text);
                            else if (name.equalsIgnoreCase("link")) tinTuc.setLink(text);
                            else if (name.equalsIgnoreCase("item")) tinTucs.add(tinTuc);
                        }
                        break;
                }
                eventType = pullParser.next();
            }

            Log.e("ABC", "SIZE " + tinTucs.size());



        } catch (Exception e) {
            Log.e("AAA", e.toString());
        }

        return tinTucs;

    }
}