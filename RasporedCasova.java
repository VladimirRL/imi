package vladimir.bojovic.pmf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class RasporedCasova extends AppCompatActivity {

    String grupa, grupa_zvezda;
    ArrayList<String> ponedeljak = new ArrayList<>();
    ArrayList<String> utorak = new ArrayList<>();
    ArrayList<String> sreda = new ArrayList<>();
    ArrayList<String> cetvrtak = new ArrayList<>();
    ArrayList<String> petak = new ArrayList<>();
    ArrayList<String> predmet_sorted = new ArrayList<>();
    boolean tranzicija = false, ima_raspored = false;
    public boolean settings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTheme(R.style.Raspored);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.casovi));
        getWindow().setStatusBarColor(getResources().getColor(R.color.casovi));

        setContentView(R.layout.raspored_casova);

        final SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor editor = preferences.edit();

        settings = false;
        if (preferences.getString("id", "").isEmpty() || preferences.getString("check", "").isEmpty()) {
            settings = true;
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            Toast.makeText(getApplicationContext(), "Podesite studije, smer i grupu u Settings", Toast.LENGTH_LONG).show();
        }
        if (!settings) {
            try {
                grupa = preferences.getString("check_g", "");
                grupa = grupa.substring(0, grupa.indexOf(grupa.contains("info") ? " info" : " matis")); // grupa info/matis smer
            } catch (Exception e) {
            }
            try {
                grupa_zvezda = preferences.getString("check_z", "");
                grupa_zvezda = grupa_zvezda.substring(0, grupa_zvezda.indexOf(grupa_zvezda.contains("info") ? " info" : " matis")); // grupa info/matis smer
            } catch (Exception e) {
            }
            final String link = preferences.getString("check_id", "");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String link_edit = null;
                        try {
                            link_edit = link.substring(link.indexOf("datasets/") + 9);
                        } catch (Exception e) {
                        }
                        link_edit = "var " + link_edit.substring(0, link_edit.indexOf("."));

                        if (!preferences.getString("raspored_js", "").isEmpty() && preferences.getString("raspored_js", "").startsWith(link_edit))
                            ima_raspored = true;

                        Document doc;
                        String[] split;
                        if (!ima_raspored) {
                            doc = Jsoup.connect(link).ignoreContentType(true).get(); // imaju posebni fajlovi u sources od sajta - https://imi.pmf.kg.ac.rs/json_datasets
                            editor.putString("raspored_js", doc.text());
                            editor.apply();
                            split = doc.text().split("\\},");
                        } else {
                            String js = preferences.getString("raspored_js", "");
                            split = js.split("\\},");
                        }

                        ArrayList<String> predmeti_po_grupama = new ArrayList<>();

                        for (int i = 0; i < split.length; i++) {
                            if (split[i].contains("(" + grupa + (split[i].contains("grupa)") ? " grupa)" : ")"))
                                    || split[i].contains("(" + grupa_zvezda + (split[i].contains("grupa)") ? " grupa)" : ")"))
                                    || !split[i].contains(")'"))
                                predmeti_po_grupama.add(split[i]);
                        }

                        ArrayList<String> toBeSorted = new ArrayList<>();

                        for (int i = 0; i < predmeti_po_grupama.size(); i++) {
                            String str = predmeti_po_grupama.get(i).substring(predmeti_po_grupama.get(i).indexOf("'2015") + 1);
                            str = str.substring(0, str.indexOf("',"));
                            toBeSorted.add(str);
                        }
                        toBeSorted.sort(new AgeSorter());

                        for (int i = 0; i < toBeSorted.size(); i++) {
                            for (int j = 0; j < predmeti_po_grupama.size(); j++) {
                                if (predmeti_po_grupama.get(j).contains(toBeSorted.get(i)))
                                    predmet_sorted.add(predmeti_po_grupama.get(j));
                            }
                        }

                        for (int i = 0; i < predmet_sorted.size(); i++) {
                            if (predmet_sorted.get(i).contains("2015-04-20"))
                                ponedeljak.add(predmet_sorted.get(i));
                            else if (predmet_sorted.get(i).contains("2015-04-21"))
                                utorak.add(predmet_sorted.get(i));
                            else if (predmet_sorted.get(i).contains("2015-04-22"))
                                sreda.add(predmet_sorted.get(i));
                            else if (predmet_sorted.get(i).contains("2015-04-23"))
                                cetvrtak.add(predmet_sorted.get(i));
                            else if (predmet_sorted.get(i).contains("2015-04-24"))
                                petak.add(predmet_sorted.get(i));
                        }

                    } catch (IOException e) {
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ImageButton loading = findViewById(R.id.raspored_loading);
                            loading.bringToFront();

                            for (int i = 0; i < ponedeljak.size(); i++)
                                raspored(i, R.id.pon_layout, ponedeljak);

                            for (int i = 0; i < utorak.size(); i++)
                                raspored(i, R.id.uto_layout, utorak);

                            for (int i = 0; i < sreda.size(); i++)
                                raspored(i, R.id.sre_layout, sreda);

                            for (int i = 0; i < cetvrtak.size(); i++)
                                raspored(i, R.id.cet_layout, cetvrtak);

                            for (int i = 0; i < petak.size(); i++)
                                raspored(i, R.id.pet_layout, petak);


                            if (!tranzicija) {
                                tranzicija = true;

                                loading.bringToFront();
                                AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                                loading.startAnimation(fadeOut);
                                fadeOut.setDuration(500);
                                fadeOut.setFillAfter(true);
                                fadeOut.setStartOffset(500);
                            }

                        }
                    });

                }

            }).start();
        }

    }

    public void raspored(int i, int layout_id, ArrayList<String> dan){

        TextView text = new TextView(RasporedCasova.this);
        LinearLayout raspored_layout = findViewById(layout_id);

        String predmet = dan.get(i);
        try {
            predmet = predmet.substring(predmet.indexOf("title: '") + 8, predmet.indexOf(")' ,") + 1); // Teorijske osnove informatike (* grupa)
        }catch(Exception e){predmet = predmet.substring(predmet.indexOf("title: '") + 8, predmet.indexOf("' ,start"));}

        String start = dan.get(i);
        start = start.substring(start.indexOf("start: '") + 8, start.indexOf("', end"));
        start = start.substring(start.indexOf("T") + 1, start.length() - 3);

        String end = dan.get(i);
        end = end.substring(end.indexOf("end: '") + 6, end.indexOf("', backgroundColor:"));
        end = end.substring(end.indexOf("T") + 1, end.length() - 3);

        String time = start + " - " + end;

        String room = dan.get(i);
        room = room.substring(room.indexOf("description: \"") + 14);
        room = room.substring(0, room.indexOf(predmet));
        if (room.equals("sala Svečana sala "))
            room = "Svečana sala";

        String bg_color = dan.get(i);
        bg_color = bg_color.substring(bg_color.indexOf("backgroundColor: '") + 18, bg_color.indexOf(";', borderColor:"));

        text.setTextColor(getResources().getColor(R.color.crna));
        text.setBackgroundColor(Color.parseColor(bg_color));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 2, 2, 3);

        text.setLayoutParams(params);
        try {
            text.setText(Html.fromHtml(time + "<br>" + "<b>" + predmet.split("\\(")[0] + "<br>(" + predmet.split(" \\(")[1] + "</b>" + "<br>" + room));
        }catch(Exception e){text.setText(Html.fromHtml(time + "<br>" + "<b>" + predmet + "</b>" + "<br>" + room));}

        raspored_layout.addView(text);

    }

}

class AgeSorter implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        return o1.compareTo(o2);
    }
}
