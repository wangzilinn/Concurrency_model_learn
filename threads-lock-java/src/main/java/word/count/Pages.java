package word.count;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
/***
 * Excerpted from "Seven Concurrency Models in Seven Weeks",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/pb7con for more book information.
 ***/
class Pages implements Iterable<Page> {

  private final int maxPages;
  private final String fileName;

  public Pages(int maxPages, String fileName) {
    this.maxPages = maxPages;
    this.fileName = fileName;
  }

  private class PageIterator implements Iterator<Page> {

    private XMLEventReader reader;
    private int remainingPages;

    public PageIterator() throws Exception {
      remainingPages = maxPages;
      reader = XMLInputFactory.newInstance().createXMLEventReader(new FileInputStream(fileName));
    }

    public boolean hasNext() { return remainingPages > 0; }

    public Page next() {
      try {
        XMLEvent event;
        String title = "";
        StringBuilder text = new StringBuilder();
        while (true) {
          event = reader.nextEvent();
          if (event.isStartElement()) {
            if (event.asStartElement().getName().getLocalPart().equals("c")) {
              while (true) {
                // v
                event = reader.nextEvent();
                if (event.isStartElement()) {
                    text.append(reader.getElementText());
                } else if (event.isEndElement()) {
                  if (event.asEndElement().getName().getLocalPart().equals("c")) {
                    --remainingPages;
                    return new Page(title, text.toString());
                  }
                }
              }
            }
          }
        }
      } catch (Exception ignored) {}
      throw new NoSuchElementException();
    }

    public void remove() { throw new UnsupportedOperationException(); }
  }

  public Iterator<Page> iterator() {
    try {
      return new PageIterator();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
