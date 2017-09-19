package pharmacy.morcos.andrew.drpharmacy.Data;

/**
 * Created by Andrew Samir on 6/7/2016.
 */
public class data_news {

    String pic_1;
    String text;
    String News_id;

    public String getNews_id() {
        return News_id;
    }

    public data_news(String pic_1, String text, String news_id) {

        this.pic_1 = pic_1;
        this.text = text;
        News_id = news_id;
    }

    public String getPic_1() {
        return pic_1;
    }
    public String getText() {
        return text;
    }

}
