package br.scaylart.malbile.reader.model.mangaeden;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MangaListJson {
    @SerializedName("manga")
    @Getter @Setter private ArrayList<MangaJson> mangas;// = new ArrayList<MangaEden>();

    public class MangaJson {
        @SerializedName("i")
        @Getter @Setter private String id;

        @SerializedName("t")
        @Getter @Setter private String title;

        @SerializedName("im")
        @Getter @Setter private String image;

        @SerializedName("ld")
        @Getter @Setter private String lastDate;

        @SerializedName("s")
        @Getter @Setter private int status;

        @SerializedName("h")
        @Getter @Setter private int hits;
    }

    /**
     * Example of a chapter array element:
     * [
     * 5, # <-- chapter's number
     * 1275542373.0, # <-- chapter's date
     * "5", # <-- chapter's title
     * "4e711cb0c09225616d037cc2" # <-- chapter's ID (chapter.id in the next section)
     * ]
     */
    @SerializedName("chapters")
    @Getter @Setter private List<List<Object>> chapters;// = new ArrayList<List<Object>>();

    /**
     * Example of a page chapter array element:
     * [
     * 32, # <-- page number
     * "87/87b1ac3ddea96bb953342829c3f74ac9f9409a4591aff77b5cf6c308.jpg", # <-- image
     * 570, # <-- width
     * 570 # <-- height
     * ]
     */
    @SerializedName("images")
    @Getter @Setter private List<List<Object>> pages;//  = new ArrayList<List<Object>>();
}
