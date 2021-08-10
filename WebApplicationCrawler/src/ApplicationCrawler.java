import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.*;
import java.io.*;
import java.util.*;
public class ApplicationCrawler implements Runnable
{
    private static final int maxDepth = 100;
    private Thread thread;
    private String rootLink;
    private ArrayList<String> visitedLinks = new ArrayList<String>();
    private int id;
    List<String> foundURLs = new ArrayList<>();
    public ApplicationCrawler(String link, int botNumber)
    {
        System.out.println("ApplicationCrawler started");
        rootLink = link;
        id = botNumber;
        thread = new Thread(this);
        thread.start();
    }


    @Override
    public void run()
    {
        spider(1, rootLink);
    }

    private void  spider(int level, String url)
    {
        if(level <= maxDepth)
        {
            Document document = request(url);
            if(document != null)
            {

                for(Element link : document.select("a[href]"))
                {
                    String nextLink = link.absUrl("href");
                    if(!visitedLinks.contains(nextLink))
                    {
                        spider(level++, nextLink);
                    }
                }
            }
        }

        try
        {
            String fileName = "/home/arch/Desktop/crawlerOutput/" + id + ".txt";
            File outPutFile = new File(fileName);
            FileWriter writer = new FileWriter(outPutFile.getPath());
            for(String foundUrl: foundURLs)
            {
                writer.write(foundUrl + System.lineSeparator());
            }
            writer.close();
        }
        catch (IOException e)
        {
            System.out.println("An error occurred writing urls to output file");
            e.printStackTrace();
        }
    }

    private Document request(String url)
    {
        Connection connection = new HttpConnection();
        Document document = new Document(url);

        try
        {
            if(url.contains(rootLink))
            {
                if(url.contains("https://"))
                {
                    connection = Jsoup.connect(url).sslSocketFactory(socketFactory()).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0");
                }
                else
                {
                    connection = Jsoup.connect(url);
                }
                document = connection.get();

                if (connection.response().statusCode() == 200)
                {
                    foundURLs.add(url);
                    visitedLinks.add(url);
                    return document;
                }
            }
            return null;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public Thread getThread()
    {
        return  thread;
    }

    static private SSLSocketFactory socketFactory()
    {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager()
        {
            public java.security.cert.X509Certificate[] getAcceptedIssuers()
            {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) { }

            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
        }};

        try
        {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        }
        catch (NoSuchAlgorithmException | KeyManagementException e)
        {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }
}

