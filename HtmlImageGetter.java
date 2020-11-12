package vladimir.bojovic.pmf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class HtmlImageGetter implements Html.ImageGetter {
    private Context mContext;
    private View mHtmlView;

    public HtmlImageGetter(Context context, TextView view) {
        this.mContext = context;
        this.mHtmlView = view;
    }

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable drawable = new LevelListDrawable();
        Drawable empty = mContext.getResources().getDrawable(R.drawable.imi_logo);
        drawable.addLevel(0, 0, empty);
        drawable.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
        new HtmlImageLoad().execute(source, drawable);
        return drawable;
    }

    class HtmlImageLoad extends AsyncTask<Object, Void, Bitmap> {
        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                TextView htmlView = ((TextView) mHtmlView);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                htmlView.setLayoutParams(params1);
                BitmapDrawable drawable = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, drawable);
                int height = (htmlView.getWidth() * bitmap.getHeight()) / bitmap.getWidth(); // textview width : bitmap width = height : bitmap height
                mDrawable.setBounds(0, 0, htmlView.getWidth(), height);
                mDrawable.setLevel(1);
                /*htmlView.invalidate();
                if (htmlView.getParent() != null) {
                    htmlView.getParent().requestLayout();
                }*/


                    CharSequence textChar = htmlView.getText();
                    htmlView.setText(textChar);
            }
        }
    }
}