package vladimir.bojovic.pmf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ImageView loading = findViewById(R.id.imi_loading);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

        fadeIn.setFillAfter(true);
        fadeIn.setDuration(750);
        loading.startAnimation(fadeIn);

        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(1000);
        loading.startAnimation(fadeOut);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.crna));
        getWindow().setStatusBarColor(getResources().getColor(R.color.crna));

    }

    public void oglas (View view) {
        Intent i = new Intent(this, OglasnaTabla.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void oglas_faks (View view){
        Intent i = new Intent(this, OglasnaFaks.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void news (View view){
        Intent i = new Intent(this, Vesti.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void news_faks (View view){
        Intent i = new Intent(this, VestiFaks.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void cas (View view){
        Intent i = new Intent(this, RasporedCasova.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void info(View view){
        Toast.makeText(getApplicationContext(), "Aplikaciju napravio Vladimir Bojović\n" +
                "Broj indeksa: 29/2020", Toast.LENGTH_LONG).show();
    }

    public void bug(View view){
        Intent i = new Intent(this, Bug.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void settings(View view){
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void beleske(View view){
        Intent i = new Intent(this, Beleske.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void sajt(View view){
        Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://imi.pmf.kg.ac.rs/"));
        startActivity(Getintent);
    }

}