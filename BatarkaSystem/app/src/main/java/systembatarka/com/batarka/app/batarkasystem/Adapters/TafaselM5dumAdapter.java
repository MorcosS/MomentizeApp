package systembatarka.com.batarka.app.batarkasystem.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import systembatarka.com.batarka.app.batarkasystem.Data.M5dumDataShow;
import systembatarka.com.batarka.app.batarkasystem.R;

/**
 * Created by MorcosS on 8/3/16.
 */
public class TafaselM5dumAdapter extends BaseAdapter {
    ArrayList<M5dumDataShow> list;
    LayoutInflater inflater;
    Activity activity;

    public TafaselM5dumAdapter(ArrayList<M5dumDataShow> list, Activity activity) {
        inflater = activity.getLayoutInflater();
        this.list = list;
        this.activity = activity;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.m5dum_details_listview,null);
        ImageButton image1 = (ImageButton) view.findViewById(R.id.imageView2);
        ImageButton image2 = (ImageButton) view.findViewById(R.id.imageView3);
        TextView head1 = (TextView) view.findViewById(R.id.textView3);
        TextView head2 = (TextView) view.findViewById(R.id.textView4);
        head1.setText(list.get(i).getHead1());
        head2.setText(list.get(i).getHead2());

        image1.setImageResource(view.getContext().getResources().getIdentifier(list.get(i).getImg1(), "drawable", view.getContext().getPackageName()));
       if(!list.get(i).getImg1().equals(list.get(i).getImg2())) {
           int id = view.getContext().getResources().getIdentifier(list.get(i).getImg2(), "drawable", view.getContext().getPackageName());
           image2.setImageResource(id);
       }else{
           image2.setVisibility(View.GONE);
           image2.setVisibility(View.GONE);
       }
        switch(list.get(i).getHead2()){
            case "Address": image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q="+list.get(i).getHead1().split("\n")[0]);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    view.getContext().startActivity(mapIntent);
                }
            });break;
            case "Phone": image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + list.get(i).getHead1()));
                    if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
                        view.getContext().startActivity(intent);
                    }
                }
            });break;
            case "Mobile":;
            case "Father Mobile":;
            case "Mother Mobile": image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + list.get(i).getHead1()));
                    if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
                        view.getContext().startActivity(intent);
                    }
                }
            });
                image2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String number = list.get(i).getHead1();  // The number on which you want to send SMS
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
                    }
                });break;

            case "Date Of Birth": image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    cal.set(Integer.parseInt(list.get(i).getHead1().split("/")[0]),Integer.parseInt(list.get(i).getHead1().split("/")[1]),Integer.parseInt(list.get(i).getHead1().split("/")[2]));
                    intent.putExtra("beginTime", cal.getTimeInMillis());
                    intent.putExtra("allDay", true);

                   }
            });break;
            case "Points":image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
                image2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        }
                });break;
        }
        return view;
    }
}
