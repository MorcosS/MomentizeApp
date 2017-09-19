package pharmacy.morcos.andrew.drpharmacy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class News_details extends AppCompatActivity {

    String text, image;
     ImageView full_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        ImageView detail_image = (ImageView) findViewById(R.id.imageView_News_Details);
        full_image = (ImageView) findViewById(R.id.imageView_News_fullImage);
        TextView news_text = (TextView) findViewById(R.id.textView_News_Details);

        full_image.setVisibility(View.GONE);
        full_image.setEnabled(false);

        Intent get_data = getIntent();
        text = get_data.getStringExtra("text");
        image = get_data.getStringExtra("pic");

        news_text.setText(text);

        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        detail_image.setImageBitmap(decodedByte);
        full_image.setImageBitmap(decodedByte);

        detail_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                full_image.setVisibility(View.VISIBLE);
                full_image.setEnabled(true);
            }
        });


    }

    @Override
    public void onBackPressed() {
        if(full_image.isEnabled()){
            full_image.setEnabled(false);
            full_image.setVisibility(View.GONE);

        }
        else {
            finish();
        }
    }
}
