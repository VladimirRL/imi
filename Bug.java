package vladimir.bojovic.pmf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Bug extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.Bug);
        setContentView(R.layout.bug);
        ImageView loading = findViewById(R.id.bug_loading);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(1000);
        loading.startAnimation(fadeOut);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.bug));
        getWindow().setStatusBarColor(getResources().getColor(R.color.bug));

        ImageButton send = findViewById(R.id.bug_confirm);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        action();
    }

    @SuppressLint("IntentReset")
    public void sendEmail() {

        EditText subject = findViewById(R.id.naslov);
        EditText tekst = findViewById(R.id.mail_tekst);

        if (!subject.getText().toString().isEmpty() && !tekst.getText().toString().isEmpty()) {

            Intent i = new Intent(Intent.ACTION_SEND);

            i.setData(Uri.parse("mailto:"));
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"vladimirbojovic2002@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
            i.putExtra(Intent.EXTRA_TEXT, tekst.getText().toString());
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
                finish();
            } catch (Exception e) {
            }
        }
    }

    public void action(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                final TextView tv = findViewById(R.id.bugs);                
                try {
                    doc = Jsoup.connect("https://pastebin.com/raw/UcPDEr7C").get();                    
                } catch (IOException e) {
                }
                final Document finalDoc = doc;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(finalDoc.wholeText());
                    }
                });
            }
        }).start();
    }
}
