package jp.kutsuna.epub;

import lombok.Data;

@Data
public class BookInformation {

    private String book_id;
    private String publisher;
    private String creator;
    private String title;
    private String language;
    private String tocContent;
    private String bookContent;
    private String pageProgressionDirection;
}
