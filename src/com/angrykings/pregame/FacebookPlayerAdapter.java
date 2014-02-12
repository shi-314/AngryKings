package com.angrykings.pregame;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angrykings.R;

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
        LinearLayout winlose = (LinearLayout)vi.findViewById(R.id.win_lose);
        LinearLayout win = (LinearLayout)vi.findViewById(R.id.win);
        LinearLayout lose = (LinearLayout)vi.findViewById(R.id.lose);

        FacebookPlayer player = data.get(position);

        Log.d("Lobbyplayer:    " , player.win);

        name.setText(player.name);
        Integer summe = Integer.parseInt(player.win) + Integer.parseInt(player.lose);
        Integer max = winlose.getLayoutParams().width;
        Log.d("WinLose Breite:    ", max.toString());
        win.getLayoutParams().width = Integer.parseInt(player.win) * max / (summe + 1);
        lose.getLayoutParams().width = Integer.parseInt(player.lose) * max / (summe + 1);
        win.requestLayout();
        lose.requestLayout();
        vi.requestLayout();

        return vi;
    }
}
