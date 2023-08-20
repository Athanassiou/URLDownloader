import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class URLDownloader {


    public static List<String> extractImageSrcList(String htmlFilePath)
    {   List<String> imageSrcList = new ArrayList<>();
        try
        {   File input = new File(htmlFilePath);
            Document doc = Jsoup.parse(input, "UTF-8");
            Elements imgTags = doc.select("img[src]");
            for (Element imgTag : imgTags) {
                String src = imgTag.attr("src");
                imageSrcList.add("https://www.eventartworks.de/"+src);
            }
        }
        catch (IOException e) { e.printStackTrace();  }
        return imageSrcList;
    }
    public static void MkDir (String directoryPath)
    {   Path directory = Paths.get(directoryPath);
        try
        {   Files.createDirectory(directory);
            System.out.println("Verzeichnis erfolgreich erstellt.");
        }
        catch (IOException e)
        {   System.out.println("Verzeichnis konnte nicht erstellt werden: " + e.getMessage()); }
    }

    public static void ImageDownloader(String imageUrl, String outputPath)
    {   try
        {   URL url = new URL(imageUrl);
            InputStream in = url.openStream();
            FileOutputStream out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1)
            {   out.write(buffer, 0, bytesRead);
            }
            in.close();
            out.close();
            System.out.println("Bild wurde erfolgreich heruntergeladen und in " + outputPath + " gespeichert.");
        }
        catch (IOException e) {  e.printStackTrace(); }
    }


    public static void getURL(String urlString, String outputPath) {

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));

                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }

                reader.close();
                writer.close();

                System.out.println("URL-Inhalt wurde erfolgreich heruntergeladen und in " + outputPath + " gespeichert.");
            } else {
                System.out.println("Fehler: HTTP-Response-Code " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static String getlastToken(String s)
    {   String[] res =s.split("/");
        return res[res.length-1];
    }
    public static void main(String[] args) {

        String url ;
        String OutputPath;
        String OutImg;
        String WorkDir;
        for(int i=1950;i<=2023;i++)
        {   url        = "https://www.eventartworks.de/gallery.php?theme=f1&year=" + i;
            OutputPath = "/Users/akis/Desktop/F1-Poster/output-"+i+".html";
            WorkDir    = "/Users/akis/Desktop/F1-Poster/"+i;
            getURL( url, OutputPath);
            MkDir(WorkDir);
            List<String> imageSrcList = extractImageSrcList(OutputPath);
            System.out.println("Gefundene Bildquellen:");
            for (String src : imageSrcList)
            {   System.out.println(src);
                OutImg=WorkDir+"/"+getlastToken(src);
                ImageDownloader(src,OutImg);
            }
        }
    }
}