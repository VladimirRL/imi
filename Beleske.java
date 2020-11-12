package vladimir.bojovic.pmf;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import java.util.Calendar;

public class Beleske extends AppCompatActivity {

    boolean clicked = false, edit = false, add_clicked = false, stanje = false;
    LinearLayout layout_edit, layout;
    int id = 0;
    boolean reset = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTheme(R.style.Beleske);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.beleske));
        getWindow().setStatusBarColor(getResources().getColor(R.color.beleske));

        setContentView(R.layout.beleske);

        ImageButton loading = findViewById(R.id.beleske_loading);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        loading.startAnimation(fadeOut);
        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(500);

        action(); // kad se napravi treci posle 2, layout za unos mu je pojeban - fix ID nekako

    }
    @SuppressLint("ResourceType")
    public void action() {
        final LinearLayout linear = findViewById(R.id.beleske_linear);

        final SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        if (preferences.getInt("broj_predmeta", 0) > 0){
            int n = preferences.getInt("broj_predmeta", 0);
            for (int i = 1; i <= n; i++){
                if (!preferences.getString("button_name" + i, "").isEmpty()) {
                    final Button button = new Button(Beleske.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    params.setMargins(0, 50, 0, 0);
                    button.setLayoutParams(params);
                    button.setBackground(getResources().getDrawable(R.drawable.border_beleske_svetlija));
                    button.setTextSize(24F);

                    layout = new LinearLayout(Beleske.this);
                    layout_edit = new LinearLayout(Beleske.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout_edit.setOrientation(LinearLayout.VERTICAL);

                    button.setId(i);
                    layout.setId(button.getId() + 1);
                    layout_edit.setId(button.getId() + 2);

                    button.setText(preferences.getString("button_name" + button.getId(), ""));
                    editor.putString("layout_edit_broj", preferences.getString("layout_edit_broj", "") + layout_edit.getId() + "/divider/\n");
                    editor.apply();

                    button.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint({"ResourceType", "SetTextI18n"})
                        @Override
                        public void onClick(View view) {
                            final SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
                            clicked = true;
                            if (button.getId() == id) {
                                if (!stanje) {
                                    LinearLayout layout_edit = findViewById(button.getId() + 2);
                                    layout_edit.removeAllViews();
                                }
                            } else {
                                String[] split = preferences.getString("layout_edit_broj", "").split("/divider/");
                                for (String s : split) {
                                    try {
                                        LinearLayout layout_edit = findViewById(Integer.parseInt(s.replaceAll("\\s+", "")));
                                        layout_edit.removeAllViews();
                                    } catch (Exception ignored) {
                                    }
                                    stanje = true;
                                }
                            }

                            id = button.getId();
                            final EditText ime_predmeta = new EditText(Beleske.this);
                            ime_predmeta.setHint(button.getText());

                            View tanka_linija = new View(Beleske.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            params.setMargins(0, 30, 0, 15);
                            params.height = 4;
                            tanka_linija.setBackgroundColor(getResources().getColor(R.color.crna));
                            tanka_linija.setLayoutParams(params);

                            ispit = new EditText(Beleske.this);
                            ispit.setHint("Kolokvijum/ispit");

                            broj_bodova = new EditText(Beleske.this);
                            broj_bodova.setHint("Broj bodova");
                            broj_bodova.setInputType(InputType.TYPE_CLASS_NUMBER);

                            select_date = new Button(Beleske.this);
                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                            final DatePickerDialog datePicker = new DatePickerDialog(Beleske.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
                                    select_date.setText(day + "/" + (month + 1) + "/" + year);
                                }
                            }, year, month, dayOfMonth);

                            select_date.setBackgroundColor(getResources().getColor(R.color.beleskeSvetlija));
                            select_date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            select_date.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    datePicker.show();
                                }
                            });

                            final Button confirm = new Button(Beleske.this);
                            confirm.setBackground(getResources().getDrawable(R.drawable.confirm));
                            final LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            confirm.setLayoutParams(params1);

                            confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!ime_predmeta.getText().toString().isEmpty())
                                        button.setText(ime_predmeta.getText());

                                    stanje = true;
                                    final SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
                                    final SharedPreferences.Editor editor = preferences.edit();
                                    boolean add_to_layout = true;
                                    final Button ispit_button = new Button(Beleske.this);
                                    ispit_button.setBackground(getResources().getDrawable(R.drawable.border_beleske_svetlija2));
                                    if (!ispit.getText().toString().isEmpty() && !broj_bodova.getText().toString().isEmpty())
                                        ispit_button.setText(Html.fromHtml("<b>" + ispit.getText() + "</b> <br>Broj bodova: " + broj_bodova.getText() + "<br>Datum: " + select_date.getText()));
                                    else if (!ime_predmeta.getText().toString().isEmpty()){
                                        editor.putString("button_name"  + button.getId(), ime_predmeta.getText().toString());
                                        editor.apply();
                                        add_to_layout = false;
                                    }
                                    else add_to_layout = false;

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                    ispit_button.setLayoutParams(params);
                                    ispit_button.setTextSize(9 * getResources().getDisplayMetrics().density);

                                    if (add_to_layout) {
                                        try {
                                            layout.addView(ispit_button);
                                            layout_edit.removeAllViews();

                                            brojac = preferences.getInt("desc" + button.getId(), 0) + 1;
                                            editor.putString("desc_name" + button.getId() + " " + brojac, ispit.getText().toString());
                                            editor.putString("desc_point" + button.getId() + " " + brojac, broj_bodova.getText().toString());
                                            editor.putString("desc_date" + button.getId() + " " + brojac, select_date.getText().toString());
                                            editor.putInt("desc" + button.getId(), brojac);
                                            editor.apply();

                                            LinearLayout linear = findViewById(R.id.beleske_linear);
                                            linear.removeAllViews();
                                            action();

                                        } catch (Exception ignored) {
                                        }
                                    } else layout_edit.removeAllViews();
                                }
                            });
                            if (stanje) {
                                layout_edit = findViewById(button.getId() + 2);
                                layout_edit.addView(ime_predmeta);
                                layout_edit.addView(tanka_linija);
                                layout_edit.addView(ispit);
                                layout_edit.addView(broj_bodova);
                                layout_edit.addView(select_date);
                                layout_edit.addView(confirm);
                                stanje = false;
                            } else {
                                stanje = true;
                                LinearLayout linear = findViewById(R.id.beleske_linear);
                                linear.removeAllViews();
                                action();
                            }
                        }
                    });

                    final int finalI = i;
                    button.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            linear.removeView(button);
                            LinearLayout l = findViewById(button.getId() + 1);
                            LinearLayout l1 = findViewById(button.getId() + 2);
                            l.removeAllViews();
                            l1.removeAllViews();
                            editor.remove("button_name" + button.getId());
                            editor.remove("desc_name" + button.getId() + " " + finalI);
                            editor.remove("desc_point" + button.getId() + " " + finalI);
                            editor.remove("desc_date" + button.getId() + " " + finalI);
                            editor.apply();
                            return true;
                        }
                    });

                    linear.addView(button);
                    linear.addView(layout);
                    linear.addView(layout_edit);

                    brojac = preferences.getInt("desc" + button.getId(), 0);

                    for (int j = 1; j <= brojac; j++) {

                        boolean add_to_layout = true;

                        final Button ispit_button = new Button(Beleske.this);
                        ispit_button.setBackground(getResources().getDrawable(R.drawable.border_beleske_svetlija2));

                        ispit_button.setTextSize(9 * getResources().getDisplayMetrics().density);

                        if (!preferences.getString("desc_name" + button.getId() + " " + j, "").isEmpty()) {
                            ispit_button.setText(Html.fromHtml("<b>" + preferences.getString("desc_name" + button.getId() + " " + j, "")
                                    + "</b> <br>Broj bodova: " + preferences.getString("desc_point" + button.getId() + " " + j, "")
                                    + "<br>Datum: " + preferences.getString("desc_date" + button.getId() + " " + j, "")));
                        } else add_to_layout = false;

                        if (add_to_layout) {
                            try {
                                layout.addView(ispit_button);
                                layout_edit.removeAllViews();

                            } catch (Exception ignored) {
                            }
                        } else layout_edit.removeAllViews();

                        final int finalJ = j;
                        ispit_button.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                layout.removeView(ispit_button);

                                editor.remove("desc_name" + button.getId() + " " + finalJ);
                                editor.remove("desc_point" + button.getId() + " " + finalJ);
                                editor.remove("desc_date" + button.getId() + " " + finalJ);
                                editor.apply();

                                linear.removeAllViews();
                                action();

                                return true;
                            }
                        });
                    }

                }
            }

        }
        final EditText predmet = findViewById(R.id.naziv_predmeta);
        final ImageButton add = findViewById(R.id.add_predmet);
        final ImageButton confirm = findViewById(R.id.confirm);
        final ConstraintLayout const_predmet = findViewById(R.id.predmet_naziv);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!add_clicked) {
                    ConstraintLayout constraintLayout = findViewById(R.id.beleske);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.beleske_scroll, ConstraintSet.TOP, R.id.guideline14, ConstraintSet.TOP);
                    constraintSet.applyTo(constraintLayout);

                    const_predmet.setVisibility(View.VISIBLE);

                    add_clicked = true;
                }else {
                    hide(const_predmet);
                    add_clicked = false;
                }

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                add_clicked = false;
                final Button button = new Button(Beleske.this);
                layout = new LinearLayout(Beleske.this);
                layout_edit = new LinearLayout(Beleske.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout_edit.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.setMargins(15, 50, 15, 0);
                button.setLayoutParams(params);

                button.setBackground(getResources().getDrawable(R.drawable.border_beleske_svetlija));
                button.setText(predmet.getText());
                button.setTextSize(24F);

                button.setId(View.generateViewId());
                layout.setId(button.getId() + 1);
                layout_edit.setId(button.getId() + 2);

                editor.putInt("broj_predmeta", button.getId());
                editor.putString("button_name" + button.getId(), button.getText().toString());
                editor.apply();

                hide(const_predmet);

                predmet.setText("");

                linear.removeAllViews();
                reset = true;
                action();

            }
        });

        ConstraintLayout full_layout = findViewById(R.id.beleske);
        full_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit) {
                    layout_edit.removeAllViews();
                    edit = false;
                }
                if (add_clicked) {
                    hide(const_predmet);
                    add_clicked = false;
                }
                if(clicked){
                    layout_edit.removeAllViews();
                    clicked = false;
                }
            }
        });

        if (reset) {
            reset = false;
            linear.removeAllViews();
            action();
        }
    }

    public void hide(ConstraintLayout predmet){
        ConstraintLayout constraintLayout = findViewById(R.id.beleske);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.beleske_scroll, ConstraintSet.TOP, R.id.guideline10, ConstraintSet.TOP);
        constraintSet.applyTo(constraintLayout);

        predmet.setVisibility(View.GONE);
    }

    int brojac = 0;
    EditText ispit, broj_bodova;
    Button select_date;


}
