package ashes;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import com.skype.ContactList;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.SkypeException;

import feed.*;

public class AshesScoreUpdater
{
  private Set<String> _friends;
  private List<Friend> _subscribers;
  private RSSFeedParser _parser;

  public AshesScoreUpdater(RSSFeedParser parser)
  {
    _friends = new HashSet<String>();
    _subscribers = new ArrayList<Friend>();
    _parser = parser;
    try
    {
      setReceivers();
    }
    catch (SkypeException e)
    {
      e.printStackTrace();
    }
  }

  private void sendScore(String score)
  {
    String message = score;

    message += "\n#AshesBaby! (rock)";

    for(Friend fr : _subscribers)
    {
        try
        {
          fr.send(message);
        }
        catch (SkypeException e)
        {
          e.printStackTrace();
        }
    }
  }

  public void start()
  {
    Feed feed;
    try
    {
      while(true)
      {
        feed = _parser.readFeed();

        for (FeedMessage message : feed.getMessages())
        {
          sendScore(message.getTitle());
        }

        Thread.sleep(1000*60*20);
      }
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  private void setReceivers() throws SkypeException
  {
    ContactList list = Skype.getContactList();
    Friend friends[] = list.getAllFriends();

    setFriends();
    for(Friend fr: friends)
    {
      if(_friends.contains(fr.getId()))
      {
        _subscribers.add(fr);
        _friends.remove(fr.getId());
      }
    }
  }

  private void setFriends()
  {
    _friends.add("<skype id>");
  }

  public static void main(String[]a)
  {
    String yahooPipeUrl = "http://pipes.yahoo.com/pipes/pipe.run?_id=8b9fae21555ff6151e118926f67aef91&_render=rss";
    RSSFeedParser parser = new RSSFeedParser(yahooPipeUrl);
    AshesScoreUpdater updater =  new AshesScoreUpdater(parser);

    updater.start();
  }
}
