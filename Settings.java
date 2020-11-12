package vladimir.bojovic.pmf;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Settings extends AppCompatActivity {

    String id;
    boolean tranzicija = false, action_tranzicija = false;
    boolean stanje = false;
    ArrayList<String> arrSmerLink = new ArrayList<>();
    boolean info_checked = false;
    boolean math_checked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RasporedCasova r = new RasporedCasova();
        if (r.settings)
            r.settings = false;

        this.setTheme(R.style.Settings);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.settings));
        getWindow().setStatusBarColor(getResources().getColor(R.color.settings));

        setContentView(R.layout.settings);
        action();

        final SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        final RadioButton informatika_radio = findViewById(R.id.informatika_radio);
        RadioButton matematika_radio = findViewById(R.id.matematika_radio);
        final RadioGroup radio_group = findViewById(R.id.smer_group);
        final RadioGroup grupa_group = findViewById(R.id.grupa_group);


        informatika_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!info_checked) {
                    info_checked = true;
                    math_checked = false;
                    editor.putString("id", "informatika_toolbox");
                    editor.apply();
                    radio_group.removeAllViews();
                    grupa_group.removeAllViews();
                    stanje = false;

                    action_tranzicija = true;
                    ImageButton loading = findViewById(R.id.settings_loading);
                    AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                    loading.startAnimation(fadeIn);
                    fadeIn.setDuration(250);
                    action();
                }
            }
        });
        matematika_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!math_checked) {
                    math_checked = true;
                    info_checked = false;
                    editor.putString("id", "matematika_toolbox");
                    editor.apply();
                    radio_group.removeAllViews();
                    grupa_group.removeAllViews();
                    stanje = false;

                    action_tranzicija = true;
                    ImageButton loading = findViewById(R.id.settings_loading);
                    AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                    loading.startAnimation(fadeIn);
                    fadeIn.setDuration(250);
                    action();
                }
            }
        });

        ImageView info = findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "I grupa - studenti sa brojem indeksa oblika 4k\n" +
                        "II grupa - studenti sa brojem indeksa oblika 4k+1\n" +
                        "III grupa - studenti sa brojem indeksa oblika 4k+2\n" +
                        "IV grupa - studenti sa brojem indeksa oblika 4k+3\n" +
                        "\n" +
                        "* - studenti sa neparnim brojem indeksa\n" +
                        "** - studenti sa parnim brojem indeksa", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String radio_name;

    public void action() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                final ArrayList<String> arrSmerovi = new ArrayList<>();

                final SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = preferences.edit();
                if (preferences.getString("id", "").isEmpty()) {
                    editor.putString("id", "");
                    editor.apply();
                }

                final RadioButton informatika_radio = findViewById(R.id.informatika_radio);
                final RadioButton matematika_radio = findViewById(R.id.matematika_radio);

                String checked = preferences.getString("id", "");
                if (!checked.isEmpty()) {
                    if (checked.equals("informatika_toolbox")) {informatika_radio.setChecked(true); info_checked = true; editor.putString("id", "informatika_toolbox");}
                    else {matematika_radio.setChecked(true); math_checked = true; editor.putString("id", "matematika_toolbox");}
                } else {informatika_radio.setChecked(true); info_checked = true; editor.putString("id", "informatika_toolbox");}

                id = preferences.getString("id", "informatika_toolbox");
                if (id.isEmpty())
                    id = "informatika_toolbox";
                final Document doc;
                try {
                    doc = Jsoup.connect("https://imi.pmf.kg.ac.rs/raspored-casova").get(); // imaju posebni fajlovi u sources od sajta

                    arrSmerovi.addAll(doc.getElementById(id).getElementsByTag("label").eachText());
                    arrSmerovi.remove(0);
                    arrSmerovi.remove(arrSmerovi.get(arrSmerovi.size() - 1));

                    arrSmerLink.clear();
                    arrSmerLink.addAll(doc.getElementById(id).getElementsByTag("a").eachAttr("id"));
                    try {
                        arrSmerLink.remove("pdf_info_mat");
                    } catch (Exception e) {
                        arrSmerLink.remove("pdf_info_inf");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        int index = 0;
                        for (int i = 0; i < arrSmerovi.size(); i++) {
                            final RadioButton radio = new RadioButton(Settings.this);
                            final RadioGroup r_group = findViewById(R.id.smer_group);

                            radio.setText(arrSmerovi.get(i));
                            radio.setId(index);

                            if ((arrSmerovi.get(i) + (informatika_radio.isChecked() ? " info" : " matis")).equals(preferences.getString("check", ""))) {
                                radio.setChecked(true);
                                radio_name = radio.getText().toString();
                                group(link(radio.getId()));
                            }

                            //noinspection deprecation
                            radio.setTextColor(getResources().getColor(R.color.crna));
                            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                            radio.setLayoutParams(params);
                            radio.setTextSize(18);

                            radio.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    editor.putString("check", radio.getText().toString() + (informatika_radio.isChecked() ? " info" : " matis"));
                                    editor.apply();
                                    stanje = false;
                                    radio_name = radio.getText().toString();
                                    final RadioGroup g_group = findViewById(R.id.grupa_group);


                                    group(link(r_group.getCheckedRadioButtonId()));
                                    r_group.removeAllViews();
                                    radio_name = null;

                                    action_tranzicija = true;
                                    ImageButton loading = findViewById(R.id.settings_loading);
                                    AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                                    loading.startAnimation(fadeIn);
                                    fadeIn.setDuration(250);
                                    g_group.removeAllViews();
                                    action();
                                }
                            });

                            r_group.addView(radio);

                            index += 1;

                            ImageButton loading = findViewById(R.id.settings_loading);
                            if (action_tranzicija) {
                                AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                                loading.startAnimation(fadeOut);
                                fadeOut.setStartOffset(250);
                                fadeOut.setDuration(250);
                                fadeOut.setFillAfter(true);
                                action_tranzicija = false;
                            }

                            if (!tranzicija) {
                                AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                                loading.startAnimation(fadeOut);
                                fadeOut.setStartOffset(500);
                                fadeOut.setDuration(500);
                                fadeOut.setFillAfter(true);
                                tranzicija = true;
                            }

                            if (!stanje) { // ovde za grupu
                                stanje = true;
                                group(link(r_group.getCheckedRadioButtonId()));
                            }
                            if ((arrSmerovi.get(i) + (informatika_radio.isChecked() ? " matis" : " info")).equals(preferences.getString("check", ""))) {
                                final RadioGroup g_group = findViewById(R.id.grupa_group);
                                g_group.removeAllViews();
                            }
                        }

                    }

                });
            }
        }).start();
    }

    public String link(int index) {

        String radio_text;
        if (index < 0)
            radio_text = "";
        else
            radio_text = arrSmerLink.get(index);

        radio_text = radio_text.replace("inf", "Inf");
        radio_text = radio_text.replace("mat", "Mat");
        radio_text = radio_text.replace("godina", "god");
        radio_text = radio_text.replace("Master", "Mas");
        if (radio_text.contains("I_") || radio_text.contains("V_")) {
            if (radio_text.contains("I_god") && !radio_text.contains("II_god")) {
                radio_text = radio_text.replaceAll("_", "");
                radio_text += "Let15";
            } else {
                radio_text = radio_text.replaceFirst("_", "");
                radio_text = radio_text.replaceFirst("_", "");
                radio_text = radio_text.replaceFirst("_", "Let15_");
            }
        } else {
            radio_text = radio_text.replaceFirst("_", "");
            if (radio_text.contains("_"))
                radio_text = radio_text.replaceFirst("_", "Let15_");
            else
                radio_text += "Let15";
        }

        radio_text = "https://imi.pmf.kg.ac.rs/json_datasets/" + radio_text + ".js";

        final SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString("check_id", radio_text);
        editor.apply();

        return radio_text;
    }

    ArrayList<String> each_group = new ArrayList<>();
    String full_text;

    public void group(final String link) {

        if (!link.equals("https://imi.pmf.kg.ac.rs/json_datasets/Let15.js")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document doc = Jsoup.connect(link).ignoreContentType(true).get(); // imaju posebni fajlovi u sources od sajta
                        full_text = doc.getAllElements().text();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String[] split = full_text.split("\\},");

                            each_group.addAll(Arrays.asList(split));

                            for (int i = 0; i < each_group.size(); i++) {
                                //each_group.set(i, each_group.get(i).substring(each_group.get(i).indexOf("'") + 1));
                                //each_group.set(i, each_group.get(i).substring(0, each_group.get(i).indexOf("'")));  ovo dvoje je za predmete
                                try {
                                    each_group.set(i, each_group.get(i).substring(each_group.get(i).indexOf("'") + 1));
                                    each_group.set(i, each_group.get(i).substring(0, each_group.get(i).indexOf("'")));
                                    each_group.set(i, each_group.get(i).substring(0, each_group.get(i).indexOf(")")));
                                    each_group.set(i, each_group.get(i).substring(each_group.get(i).indexOf("(") + 1));
                                } catch (Exception e) {
                                    each_group.set(i, "");
                                }

                            }
                            removeDuplicates(each_group);

                            for (int i = 0; i < each_group.size(); i++) {
                                try {
                                    each_group.set(i, each_group.get(i).replace(" grupa", ""));
                                } catch (Exception ignored) {
                                }
                            }
                            if (each_group.contains("V") && !each_group.contains("IV"))
                                each_group.remove("V");
                            Collections.sort(each_group);
                            if (each_group.get(0).equals(""))
                                each_group.remove(0);

                            try {
                                final SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
                                final SharedPreferences.Editor editor = preferences.edit();
                                final RadioButton informatika_radio = findViewById(R.id.informatika_radio);
                                final RadioGroup g_group = findViewById(R.id.grupa_group);
                                final RadioGroup z_group = findViewById(R.id.zvezda_group);
                                g_group.removeAllViews();
                                z_group.removeAllViews();
                                for (int j = 0; j < each_group.size(); j++) {
                                    final RadioButton g_radio = new RadioButton(Settings.this);

                                    g_radio.setText(each_group.get(j));
                                    g_radio.setId(View.generateViewId());

                                    //noinspection deprecation
                                    g_radio.setTextColor(getResources().getColor(R.color.crna));
                                    g_radio.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                                    g_radio.setTextSize(18);

                                    g_radio.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String upis = "check_g";
                                            if (g_radio.getText().toString().contains("*") || g_radio.getText().toString().contains("B I"))
                                                upis = "check_z";

                                            editor.putString(upis, g_radio.getText().toString() + (informatika_radio.isChecked() ? " info" : " matis") + " " + radio_name);
                                            editor.apply();
                                            if ((informatika_radio.isChecked() ? !each_group.contains("*") : !each_group.contains("B I"))){
                                                editor.remove("check_z");
                                                editor.apply();
                                            }
                                        }
                                    });

                                    if (!each_group.get(j).contains("*") && !each_group.get(j).contains("B I")) {
                                        if ((each_group.get(j) + (informatika_radio.isChecked() ? " info" : " matis") + " " + radio_name).equals(preferences.getString("check_g", "")))
                                            g_radio.setChecked(true);
                                        g_group.addView(g_radio);
                                    }
                                    else {
                                        if ((each_group.get(j) + (informatika_radio.isChecked() ? " info" : " matis") + " " + radio_name).equals(preferences.getString("check_z", "")))
                                            g_radio.setChecked(true);
                                        z_group.addView(g_radio);
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    });
                }
            }).start();
        }
    }

    public static <T> void removeDuplicates(ArrayList<T> list) {

        Set<T> set = new LinkedHashSet<>(list);
        list.clear();
        list.addAll(set);
    }
}
