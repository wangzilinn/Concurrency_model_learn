package word.count;

/***
 * Excerpted from "Seven Concurrency Models in Seven Weeks",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/pb7con for more book information.
 ***/
class Page {
    private final String title;
    private final String text;
    private boolean finalPage = false;

    public Page(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public static Page finalPage() {
        Page page = new Page("", "");
        page.finalPage = true;
        return page;
    }

    public boolean isFinal() {
        return finalPage;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }


}
