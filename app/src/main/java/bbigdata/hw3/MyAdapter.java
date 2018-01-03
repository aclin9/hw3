package bbigdata.hw3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hp on 2017/12/27.
 */

public class MyAdapter extends BaseAdapter {

    private ArrayList<Data> Data;
    private int view;
    private Context context;

    MyAdapter(ArrayList<Data> Data_, int view_, Context context_){
        Data = Data_;
        view = view_;
        context = context_;
    }

    @Override
    public int getCount() {
        return Data.size();
    }

    @Override
    public Object getItem(int position) {
        return Data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = LayoutInflater.from(context).inflate(this.view, parent, false);
        TextView tv1 = (TextView) rowView.findViewById(R.id.tv1);
        TextView tv2 = (TextView) rowView.findViewById(R.id.tv2);
        TextView tv3 = (TextView) rowView.findViewById(R.id.tv3);
        tv1.setText(Data.get(position).SiteName);
        tv2.setText(Data.get(position).Temp);
        tv3.setText(Data.get(position).Date);
        return rowView;
    }



}
