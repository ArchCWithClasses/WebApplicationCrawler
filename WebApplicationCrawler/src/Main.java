import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        System.out.print("Please enter the number of urls you would like to scan: ");
        String[] urlArray = new String [sc.nextInt()];
        //consuming the <enter> from input above
        sc.nextLine();
        for (int i = 0; i < urlArray.length; i++)
        {
            System.out.println("Please enter url: ");
            urlArray[i] = sc.nextLine();
        }
        System.out.println(urlArray.length);


        ArrayList<ApplicationCrawler> spiderBots = new ArrayList<>();

        for(int j = 0; j < urlArray.length; j++)
        {
            spiderBots.add(new ApplicationCrawler(urlArray[j],j + 1));
        }

        for(ApplicationCrawler crawler : spiderBots)
        {
            try
            {
                crawler.getThread().join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }
}