package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a result from the SNAPI.
 */
public class ArticleResult {
    String title;
    @SerializedName("published_at")
    String publishedAt;
    ArticleAuthor[] authors;
    String summary;
    @SerializedName("image_url")
    String imageUrl;

} // ArticleResult
