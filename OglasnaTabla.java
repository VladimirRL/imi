package vladimir.bojovic.pmf;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;

public class OglasnaTabla extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTheme(R.style.OglasnaTheme);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.oglas_crvena));
        getWindow().setStatusBarColor(getResources().getColor(R.color.oglas_crvena));

        setContentView(R.layout.oglasna_tabla);

        final LinearLayout layout = findViewById(R.id.linear);
        final ArrayList<String> arrTitle  = new ArrayList<>();
        final ArrayList<String> arrDate = new ArrayList<>();
        final ArrayList<String> arrAuthor = new ArrayList<>();
        final ArrayList<String> arrText = new ArrayList<>();
        final int[] signal_za_tranziciju = {0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://imi.pmf.kg.ac.rs/oglasna-tabla").get();

                    arrTitle.addAll(doc.getElementsByClass("lead").eachText());

                    String modal = "";

                    Elements title = doc.select(".naslov_oglasa");
                    for (Element element : title){                             
                        String[] split = element.outerHtml().split("data-reveal-id=");
                        modal = split[1].substring(1, split[1].indexOf(">") - 1); // modal_12345
                        String html = doc.getElementById(modal).outerHtml();

                        html = html.substring(html.lastIndexOf("datum;") + 34); // promeniti u 24 ako mora preko dole ovog sto je u comment
                        html = html.substring(0, html.lastIndexOf("</a>") + 1);
                        arrText.add(html);

                        signal_za_tranziciju[0] += 1;

                    }
                    Elements date = doc.select(".datum_oglasa");
                    for (Element element : date)
                        arrDate.add(element.text());


                    Elements author = doc.select(".autor_oglasa");
                    for (Element element : author)
                        arrAuthor.add(element.text());


                } catch (IOException e) {e.printStackTrace();}

                runOnUiThread(new Runnable() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void run() {
                        for (int i = 0; i < arrText.size(); i++) {

                            final TextView title = new TextView(OglasnaTabla.this);

                            title.setBackground(getResources().getDrawable(R.drawable.border_oglasna_title));
                            title.setTextSize(18);
                            title.setTextColor(Color.BLACK);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            params.setMargins(5, 15, 5, 2);
                            title.setLayoutParams(params);

                            title.setText(Html.fromHtml("<b>" + arrTitle.get(i) + "</b>" + "<br>" + arrDate.get(i) + "<br>" + arrAuthor.get(i)));

                            String html = arrText.get(i);

                            final WebView web = new WebView(OglasnaTabla.this);

                            final String mimeType = "text/html";
                            final String encoding = "UTF-8";
                            html = html.replaceAll("×<", "");

                            html = "<html><head>"
                                    + "<style type=\"text/css\">body{color:" + "#424242" + ";} a{color:#FF5353; text-decoration:none; font-weight:bold;}"
                                    + "</style></head>"
                                    + "<body>" + html + "</body></html>";

                            String slika_novi_link = "", slika_link = "";
                            if (!html.contains("<img src=\"http://www.pmf.kg.ac.rs/")) {
                                while (html.contains("<img src=\"p")) {
                                    slika_link = html.substring(html.indexOf("<img src=\"pub/") + 10);
                                    slika_novi_link = "http://www.pmf.kg.ac.rs/" + slika_link;
                                    html = html.replace(slika_link, slika_novi_link);
                                }
                            }

                            if (html.contains("<a href=\"")) {
                                if (!html.contains("<a href=\"https://imi.pmf.kg.ac.rs/"))
                                    html = html.replaceAll("<a href=\"", "<a href =\"https://imi.pmf.kg.ac.rs/");
                            }

                            if (html.contains("http://imi.pmf.kg.ac.rs/") && !html.contains("logopmf")) {
                                web.getSettings().setSupportZoom(true);
                                web.getSettings().setBuiltInZoomControls(true);
                                web.getSettings().setLoadWithOverviewMode(true);
                                web.getSettings().setUseWideViewPort(true);
                            }

                            web.loadDataWithBaseURL("", html, mimeType,
                                    encoding, "");

                            web.setVisibility(View.GONE);
                            web.getSettings().setDefaultFontSize(16);

                            title.setId(View.generateViewId());
                            web.setId(title.getId() + 1);

                            layout.addView(title);
                            layout.addView(web);

                            title.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    WebView web = findViewById(title.getId() + 1);
                                    if (web.getVisibility() == View.GONE)
                                        web.setVisibility(View.VISIBLE);
                                    else
                                        web.setVisibility(View.GONE);
                                }
                            });

                            signal_za_tranziciju[0] -= 1;

                            if (signal_za_tranziciju[0] == 0){
                                ImageButton loading = findViewById(R.id.oglasna_loading);

                                AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f);
                                loading.startAnimation(fadeOut);
                                fadeOut.setDuration(500);
                                fadeOut.setFillAfter(true);
                                fadeOut.setStartOffset(500);
                            }

                        }
                    }
                });

            }

        }).start();
    }
}
