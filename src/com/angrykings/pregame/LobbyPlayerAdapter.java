package com.angrykings.pregame;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angrykings.R;
import com.angrykings.pregame.LobbyPlayer;

import java.util.List;

public class LobbyPlayerAdapter extends BaseAdapter{

    private Activity activity;
    private List<LobbyPlayer> data;
    private static LayoutInflater inflater=null;

    public LobbyPlayerAdapter(Activity a, int list_row, List<LobbyPlayer> d){
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
            vi = inflater.inflate(R.layout.list_row, null);
        }
        TextView name = (TextView)vi.findViewById(R.id.spielername);

        final LobbyPlayer player = data.get(position);

        name.setText(player.name);

        vi.requestLayout();

        return vi;
    }

}
