package com.angrykings.pregame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angrykings.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.util.List;

public class FacebookPlayerAdapter extends BaseAdapter{

    private Activity activity;
    private List<FacebookPlayer> data;
    private static LayoutInflater inflater=null;

    public FacebookPlayerAdapter(Activity a, int list_row, List<FacebookPlayer> d){
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null){
            vi = inflater.inflate(R.layout.list_row_facebook, null);
        }

        ImageView fb = (ImageView)vi.findViewById(R.id.facebookpicture);
        TextView name = (TextView)vi.findViewById(R.id.spielername);

        FacebookPlayer player = data.get(position);

        Log.d("Lobbyplayer:    " , player.win);

        name.setText(player.name);

        vi.requestLayout();

        fb.setImageBitmap(circularCrop(((BitmapDrawable)fb.getDrawable()).getBitmap()));

        String profilePicture = "http://graph.facebook.com/" + data.get(position).fbID + "/picture";
        ImageLoader.getInstance().displayImage(profilePicture, fb, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                ImageView iv = (ImageView) view;
                iv.setImageBitmap(circularCrop(bitmap));
             }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

        return vi;
    }

    public static Bitmap circularCrop(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;

    }
}
