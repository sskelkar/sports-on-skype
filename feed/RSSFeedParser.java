package feed;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class RSSFeedParser {
  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  private static final String ITEM = "item";
  private static final String PUB_DATE = "pubDate";
  private static final String GUID = "guid";

  private final URL url;

  public RSSFeedParser(String feedUrl) 
  {
    try 
    {
      this.url = new URL(feedUrl);
    } 
    catch (MalformedURLException e) 
    {
      throw new RuntimeException(e);
    }
  }

  public Feed readFeed() 
  {
    Feed feed = null;
    try 
    {
      boolean isFeedHeader = true;
      // Set header values intial to the empty string
      String description = "";
      String title = "";
      String pubdate = "";
      String guid = "";

      // First create a new XMLInputFactory
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      // Setup a new eventReader
      InputStream in = read();
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
      // Read the XML document
      while (eventReader.hasNext()) 
      {
        XMLEvent event = eventReader.nextEvent();
        if (event.isStartElement()) 
        {
          String localPart = event.asStartElement().getName().getLocalPart();
          if(localPart.equals(ITEM))
          {
            if (isFeedHeader) 
            {
              isFeedHeader = false;
              feed = new Feed(title, description, pubdate);
            }
            event = eventReader.nextEvent();
          }
          else if(localPart.equals(TITLE))
          {
            title = getCharacterData(event, eventReader);
          }
          else if(localPart.equals(DESCRIPTION))
          {
            description = getCharacterData(event, eventReader);
          }
          else if(localPart.equals(GUID))
          {          
            guid = getCharacterData(event, eventReader);
          }
          else if(localPart.equals(PUB_DATE))
          {
            pubdate = getCharacterData(event, eventReader);
          }
          
        } 
        else if (event.isEndElement()) 
        {
          if (event.asEndElement().getName().getLocalPart() == (ITEM)) 
          {
            FeedMessage message = new FeedMessage();
            message.setDescription(description);
            message.setGuid(guid);
            message.setTitle(title);
            feed.getMessages().add(message);
            event = eventReader.nextEvent();
            continue;
          }
        }
      }
    } catch (XMLStreamException e) {
      throw new RuntimeException(e);
    }
    return feed;
  }

  private String getCharacterData(XMLEvent event, XMLEventReader eventReader)
      throws XMLStreamException 
  {
    String result = "";
    event = eventReader.nextEvent();
    if (event instanceof Characters) 
    {
      result = event.asCharacters().getData();
    }
    return result;
  }

  private InputStream read() 
  {
    try 
    {
      return url.openStream();
    } 
    catch (IOException e) 
    {
      throw new RuntimeException(e);
    }
  }
} 