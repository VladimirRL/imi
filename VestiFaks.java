package vladimir.bojovic.pmf;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;

public class VestiFaks extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTheme(R.style.VestiFaks);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.vesti_faks));
        getWindow().setStatusBarColor(getResources().getColor(R.color.vesti_faks));

        setContentView(R.layout.vesti_faks);

        final LinearLayout layout = findViewById(R.id.linear_vesti_faks);
        final ArrayList<String> arrHtml = new ArrayList<>();
        final int[] signal_za_tranziciju = {0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://www.pmf.kg.ac.rs/index.php/vesti").get();
                    Elements text_elements = doc.getElementsByClass("vesti_post");
                    for (Element text : text_elements) {
                        String html = text.outerHtml();
                        String datum_script = html.substring(html.indexOf("var datum = moment("), html.indexOf("</script>"));
                        String datum = datum_script.substring(datum_script.indexOf("\""), datum_script.indexOf(","));
                        datum = datum.replaceAll("\"", "");

                        String[] datum_split = datum.split("-");
                        datum = datum_split[1] + "." + datum_split[0] + "." + datum_split[2] + ".";

                        html = html.replace(html.substring(html.indexOf("<script>"), html.indexOf("</script>") + 9), "<p>" + datum + "</p>");

                        arrHtml.add(html);

                        signal_za_tranziciju[0] += 1;


                    }

                }catch(Exception e){e.printStackTrace();}

                runOnUiThread(new Runnable() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void run() {
                        for (int i = 0; i < arrHtml.size(); i++) {

                            String html = arrHtml.get(i);

                            final WebView web = new WebView(VestiFaks.this);

                            final String mimeType = "text/html";
                            final String encoding = "UTF-8";
                            html = html.replaceAll("×<", "");

                            html = "<html><head>"
                                    + "<style type=\"text/css\">body{color:" + "#424242" + ";} a{color:#FF5353; text-decoration:none; font-weight:bold;}"
                                    + "</style></head>"
                                    + "<body>" + html + "</body></html>";

                            String slika_novi_link = "", slika_link = "";
                            if (!html.contains("src=\"https://www.pmf.kg.ac.rs/")) {
                                while (html.contains("src=\"p")) {
                                    slika_link = html.substring(html.indexOf("src=\"pub/") + 5);
                                    slika_novi_link = "https://www.pmf.kg.ac.rs/" + slika_link;
                                    html = html.replace(slika_link, slika_novi_link);
                                }
                            }

                            if (html.contains("<a href=\"")) {
                                if (!html.contains("<a href=\"https://www.pmf.kg.ac.rs/"))
                                    html = html.replaceAll("<a href=\"", "<a href=\"https://www.pmf.kg.ac.rs/");
                            }
                            if (html.contains("src=\"pub"))
                                html = html.replaceAll("src=\"", "src=\"https://www.pmf.kg.ac.rs/");


                            web.loadDataWithBaseURL("", html, mimeType,
                                    encoding, "");

                            web.setBackgroundColor(getResources().getColor(R.color.tamno_crvena));
                            web.getSettings().setDefaultFontSize(16);

                            layout.addView(web);

                            signal_za_tranziciju[0] -= 1;

                            if (signal_za_tranziciju[0] == 0) {
                                ImageButton loading = findViewById(R.id.vesti_faks_loading);

                                AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                                loading.startAnimation(fadeOut);
                                fadeOut.setDuration(500);
                                fadeOut.setFillAfter(true);
                                fadeOut.setStartOffset(500);

                            }
                            web.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });
                        }
                    }
                });

            }

        }).start();
    }
}
