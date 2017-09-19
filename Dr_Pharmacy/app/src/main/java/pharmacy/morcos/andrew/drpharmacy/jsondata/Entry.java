
package pharmacy.morcos.andrew.drpharmacy.jsondata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Entry {

    @SerializedName("id")
    @Expose
    private Id_ id;
    @SerializedName("updated")
    @Expose
    private Updated_ updated;
    @SerializedName("category")
    @Expose
    private List<Category_> category = new ArrayList<Category_>();
    @SerializedName("title")
    @Expose
    private Title_ title;
    @SerializedName("content")
    @Expose
    private Content content;
    @SerializedName("link")
    @Expose
    private List<Link_> link = new ArrayList<Link_>();
    @SerializedName("gsx$timestamp")
    @Expose
    private Gsx$timestamp gsx$timestamp;
    @SerializedName("gsx$id")
    @Expose
    private Gsx$id gsx$id;
    @SerializedName("gsx$pic1")
    @Expose
    private Gsx$pic1 gsx$pic1;
    @SerializedName("gsx$pic2")
    @Expose
    private Gsx$pic2 gsx$pic2;
    @SerializedName("gsx$text")
    @Expose
    private Gsx$text gsx$text;

    /**
     * 
     * @return
     *     The id
     */
    public Id_ getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Id_ id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The updated
     */
    public Updated_ getUpdated() {
        return updated;
    }

    /**
     * 
     * @param updated
     *     The updated
     */
    public void setUpdated(Updated_ updated) {
        this.updated = updated;
    }

    /**
     * 
     * @return
     *     The category
     */
    public List<Category_> getCategory() {
        return category;
    }

    /**
     * 
     * @param category
     *     The category
     */
    public void setCategory(List<Category_> category) {
        this.category = category;
    }

    /**
     * 
     * @return
     *     The title
     */
    public Title_ getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(Title_ title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The content
     */
    public Content getContent() {
        return content;
    }

    /**
     * 
     * @param content
     *     The content
     */
    public void setContent(Content content) {
        this.content = content;
    }

    /**
     * 
     * @return
     *     The link
     */
    public List<Link_> getLink() {
        return link;
    }

    /**
     * 
     * @param link
     *     The link
     */
    public void setLink(List<Link_> link) {
        this.link = link;
    }

    /**
     * 
     * @return
     *     The gsx$timestamp
     */
    public Gsx$timestamp getGsx$timestamp() {
        return gsx$timestamp;
    }

    /**
     * 
     * @param gsx$timestamp
     *     The gsx$timestamp
     */
    public void setGsx$timestamp(Gsx$timestamp gsx$timestamp) {
        this.gsx$timestamp = gsx$timestamp;
    }

    /**
     * 
     * @return
     *     The gsx$id
     */
    public Gsx$id getGsx$id() {
        return gsx$id;
    }

    /**
     * 
     * @param gsx$id
     *     The gsx$id
     */
    public void setGsx$id(Gsx$id gsx$id) {
        this.gsx$id = gsx$id;
    }

    /**
     * 
     * @return
     *     The gsx$pic1
     */
    public Gsx$pic1 getGsx$pic1() {
        return gsx$pic1;
    }

    /**
     * 
     * @param gsx$pic1
     *     The gsx$pic1
     */
    public void setGsx$pic1(Gsx$pic1 gsx$pic1) {
        this.gsx$pic1 = gsx$pic1;
    }

    /**
     * 
     * @return
     *     The gsx$pic2
     */
    public Gsx$pic2 getGsx$pic2() {
        return gsx$pic2;
    }

    /**
     * 
     * @param gsx$pic2
     *     The gsx$pic2
     */
    public void setGsx$pic2(Gsx$pic2 gsx$pic2) {
        this.gsx$pic2 = gsx$pic2;
    }

    /**
     * 
     * @return
     *     The gsx$text
     */
    public Gsx$text getGsx$text() {
        return gsx$text;
    }

    /**
     * 
     * @param gsx$text
     *     The gsx$text
     */
    public void setGsx$text(Gsx$text gsx$text) {
        this.gsx$text = gsx$text;
    }

}
