import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;

class Offer{
    String Year;
    String Hersteller;
    String Modell;
    String Preis;

    public Offer(String Year, String Hersteller, String Modell, String Preis) {
        this.Year = Year;
        this.Hersteller = Hersteller;
        this.Modell = Modell;
        this.Preis = Preis;
    }

    public String getYear() {
        return Year;
    }
    public String getHersteller() {
        return Hersteller;
    }
    public String getModell() {
        return Modell;
    }
    public String getPreis() {
        return Preis;
    }
    @Override
    public String toString() {
        return Year + " " + Hersteller + " " + Modell + " " + Preis;
    }
}


public class URLDownloader {
    public static String[] Stopwords={"Australian",
            "Chinese",
            "Bahrain",
            "Russian",
            "Spanish",
            "Monaco",
            "Canadian",
            "Azerbaijan",
            "Austrian",
            "British",
            "Hungarian",
            "Belgian",
            "Italian",
            "Singapore",
            "Malaysian",
            "Japanese",
            "United_States",
            "Mexican",
            "Brazilian",
            "Abu Dhabi"

    };

    public static List<String> extractImageSrcList_0(String htmlFilePath) {
        List<String> imageSrcList = new ArrayList<>();
        try {
            File input = new File(htmlFilePath);
            Document doc = Jsoup.parse(input, "UTF-8");
            Elements imgTags = doc.select("img[src]");
            for (Element img : imgTags) {
                String l = img.attr("src");
                imageSrcList.add(l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageSrcList;
    }

    public static class myData {
        String href;
        String title;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static List<myData> extractImageSrcList(String htmlFilePath) {
        List<myData> imageSrcList = new ArrayList<myData>();
        try {
            File input = new File(htmlFilePath);
            Document doc = Jsoup.parse(input, "UTF-8");
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String l = link.attr("href");
                String t = link.attr("title");
                myData d = new myData();
                if (l.endsWith(".jpg") && !(skip(t))) {
                    //System.out.println("l:" + l + "\t out:" + out);
                    d.setTitle(formatTitle(t));
                    d.setHref(l);
                    imageSrcList.add(d);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageSrcList;
    }

    private static boolean skip(String t) {
        String[] toSkip = {"Sports Cars", "Sportscars", "Other Races", "1000km", "Le Mans", "Touring Cars", "VIP", "Girls", "Fans", "Woman", "Pits"};
        for (String s : toSkip)
            if (t.contains(s))
                return true;
        return false;
    }

    private static String formatTitle(String t) {

        String s1, s2;
        String[] toDelete = {", A1", ", A2", ", A3", ", A4", ", Formula 1", ", Galerie-Motiv", ", Hi-res", ", Cigarette",
                ", Cigar", ", Pits", ", Girls", ", Helmet", ", Drivers Women", ", Topshot", ", Celebrating", ", TGC2", ", Photographers",
                ", Pits", ", Fire", ", Accident", ", Portrait", ", Pit Board", ", Track", ", Joy", ", Goggles", ", Technical", ", Side", ", Snapshot",
                ", TGC1", ", A6", ", Speed Blur", ", Paddock", ", Pits", ", Fans", ", A5", ", TGC3", ", A0", ", Above", ", Cockpit", ", Rain", ", Panaroma",
                ", Zoom", ", Three Cars", ", Thoughtful", ", Two Cars", ", Rear", ", Press", ", Many Cars", ", Many", ", Steering Wheel", ", TGC5", ", TGC6", "A1"
        };

        String[] phrases = t.split("<i>");
        if (phrases.length != 2)
            return "error";
        s1 = phrases[1];

        for (String s : toDelete)
            s1 = s1.replace(s, "");

        s2 = s1.replace("</i>", "");
        return s2.replace(", ", "_");
    }

    public static void MkDir(String directoryPath) {
        Path directory = Paths.get(directoryPath);
        try {
            Files.createDirectory(directory);
            System.out.println("Verzeichnis erfolgreich erstellt.");
        } catch (IOException e) {
            System.out.println("Verzeichnis konnte nicht erstellt werden: " + e.getMessage());
        }
    }

    public static boolean onlyDigits(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) < '0' || str.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }


    public static void ImageDownloader(String imageUrl, String outputPath) {
        try {
            int i;
            File f = new File(outputPath);
            while (f.exists()) {
                String[] token = outputPath.split(".jpg");
                String s = getLastToken(token[0]);
                if (onlyDigits(s)) {
                    i = Integer.valueOf(s) + 1;
                    String out = outputPath.replace("_" + s, "_" + i);
                    outputPath = out;
                } else {
                    outputPath = token[0] + "_" + 1 + ".jpg";
                }
                f = new File(outputPath);
            }
            URL url = new URL(imageUrl);
            InputStream in = url.openStream();
            FileOutputStream out = new FileOutputStream(outputPath);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            in.close();
            out.close();
            System.out.println("Bild wurde erfolgreich heruntergeladen und in " + outputPath + " gespeichert.");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static String getlastToken(String s) {
        String[] res = s.split("/");
        return res[res.length - 1];
    }

    public static String getLastToken(String s) {
        String[] res = s.split("_");
        return res[res.length - 1];
    }

    public static String makeFileName(String name, String Year) {
        String[] pieces = name.split("_");
        if (!pieces[0].equals(Year)) {
            return Year + "_" + name.replace("_" + Year, "");
        }
        return name;

    }

    static boolean renameFile(String path, String alt, String neu) {
        Path source = Paths.get(path + alt);

        try {
            Files.move(source, source.resolveSibling(neu));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void Schlegelmilch() {

        String url;
        String OutputPath;
        String OutImg;
        String WorkDir;

        String[] Token0 = {"2007-01-melbourne", "2007-02-sepang", "2007-03-sakhir",
                "2007-04-barcelona", "2007-05-monaco", "2007-06-montreal", "2007-07-indianapolis",
                "2007-09-silverstone", "2007-10-nuerburgring",
                "2007-11-budapest", "2007-12-istanbul", "2007-13-monza", "2007-14-spa-francorchamps", "2007-16-shanghai"};

        String[] Token1 = {"2008-01-melbourne", "2008-02-sepang", "2008-03-sakhir",
                "2008-04-barcelona", "2008-05-istanbul", "2008-06-monaco", "2008-07-montreal", "2008-08-magny-cours",
                "2008-09-silverstone", "2008-10-hockenheim", "2008-11-budapest", "2008-12-valencia",
                "2008-13-spa-francorchamps", "2008-14-monza", "2008-15-singapur", "2008-16-mountfuji", "2008-17-shanghai", "2008-18-saopaulo"};

        String[] Token2 = {"2009-01-melbourne", "2009-02-sepang", "2009-03-shanghai", "2009-04-sakhir",
                "2009-05-barcelona", "2009-06-monaco", "2009-07-istanbul", "2009-08-silverstone",
                "2009-09-nuerburgring", "2009-10-budapest", "2009-11-valencia",
                "2009-12-spa-francorchamps", "2009-13-monza", "2009-14-singapur", "2009-15-suzuka", "2009-16-saopaulo", "2009-17-abudhabi"};

        String[] Token3 = {"2010-01-sakhir", "2010-02-melbourne", "2010-03-sepang", "2010-04-shanghai",
                "2010-05-barcelona", "2010-06-monaco", "2010-07-istanbul", "2010-08-montreal", "2010-09-valencia", "2010-10-silverstone",
                "2010-11-hockenheim", "2010-12-budapest", "2010-13-spa-francorchamps", "2010-14-monza", "2010-15-singapur",
                "2010-16-suzuka", "2010-17-yeongam", "2010-18-saopaulo", "2010-19-abudhabi"};

        String[] Token5 = {"2011-01-melbourne", "2011-02-sepang", "2011-03-shanghai", "2011-04-istanbul",
                "2011-05-barcelona", "2011-06-monaco", "2011-07-montreal", "2011-08-valencia", "2011-09-silverstone",
                "2011-10-nuerburgring", "2011-11-budapest", "2011-12-spa-francorchamps", "2011-13-monza", "2011-14-singapur",
                "2011-15-suzuka", "2011-16-yeongam", "2011-17-neudehli", "2011-18-abudhabi", "2011-19-saopaulo"};

        String[] Token = {"2009-01-melbourne"};
        int i = 2009;
        //for(int i=1990;i<=1999;i++)
        for (int j = 0; j < Token.length; j++) {   // url= https://www.eventartworks.de/gallery.php?theme=f1&year=" + i;
            // OutputPath = "/Users/eathanassiou/Desktop/F1-Poster/output-"+i+".html";
            // WorkDir    = "/Users/eathanassiou/Desktop/F1-Poster/"+i;
            // url        = "https://www.schlegelmilch.com/racing-years/nggallery/2000s/"+i+"/";

            String token = Token[j];
            //OutputPath = "/Users/eathanassiou/Desktop/Schlegelmilch/"+i+"/"+token+".html";
            // url=        "https://www.schlegelmilch.com/racing-years/nggallery/"+i+"/"+token+"/";
            // WorkDir    = "/Users/eathanassiou/Desktop/Schlegelmilch/"+i+"/"+token;

            //OutputPath = "/Users/eathanassiou/Desktop/Schlegelmilch/"+i+".html";
            //url=        "https://www.schlegelmilch.com/racing-years/nggallery/1990s/"+i+"/";
            //WorkDir    = "/Users/eathanassiou/Desktop/Schlegelmilch/"+i;
            OutputPath = "/Users/eathanassiou/Cahier/1.html";
            WorkDir = "/Users/eathanassiou/Desktop/1";
            //getURL( url, OutputPath);
            MkDir(WorkDir);
            List<myData> imageSrcList = extractImageSrcList(OutputPath);
            System.out.println("Gefundene Bildquellen:");
            for (myData src : imageSrcList) {   //System.out.println(src);
                OutImg = WorkDir + "/" + makeFileName(getlastToken(src.getTitle()), String.valueOf(i)) + ".jpg";
                ImageDownloader(src.getHref(), OutImg);
            }


        }
    }



    public static List<String> extractImageSrcListCahier(String htmlFilePath) {
        List<String> imageSrcList = new ArrayList<>();
        try {
            File input = new File(htmlFilePath);
            Document doc = Jsoup.parse(input, "UTF-8");
            Elements imgTags = doc.select("img[src]");
            for (Element img : imgTags) {
                String l = img.attr("src");
                imageSrcList.add(l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageSrcList;
    }

    public static String getLastToken2(String s) {
        String[] res = s.split("/");
        return res[res.length - 1];
    }

    public static String removeExtension(String fname) {
        int pos = fname.lastIndexOf('_');
        if (pos > -1)
            return fname.substring(0, pos);
        else
            return fname;
    }

    public static void Cahier() {
        String Event = "Australia";
        String Year = "1993";
        String myURL = "https://www.f1-photo.com/Search/Year:" + Year + "%20Event:" + Event + "%20/P1x16";
        String OutputPath = "/Users/eathanassiou/Cahier/" + Year + "_" + Event + ".html";
        String WorkDir = "/Users/eathanassiou/Cahier/" + Year + "_" + Event;
        String URLCore = "https://www.f1-photo.com";
        getURL(myURL, OutputPath);

        MkDir(WorkDir);
        List<String> imageSrcList = extractImageSrcListCahier(OutputPath);
        for (String i : imageSrcList) {
            if (i.startsWith("/Ressources/Images/"))
                continue;
            System.out.println(i);
            String Name = removeExtension(i) + ".jpg";
            String NameNeu = getLastToken2(Name);
            String OutImg = WorkDir + "/" + NameNeu;
            String ImageURL = URLCore + Name;
            System.out.println(" ImageURL :" + ImageURL + "\nOutImg :" + OutImg);
            ImageDownloader(ImageURL, OutImg);
        }

    }

    public static List<String> extractImageSrcListAutosport(String htmlFilePath) {
        List<String> imageSrcList = new ArrayList<>();
        try {
            File input = new File(htmlFilePath);
            Document doc = Jsoup.parse(input, "UTF-8");
            Elements imgTags = doc.select("img[src]");
            for (Element img : imgTags) {
                String l = img.attr("src");
                imageSrcList.add(l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageSrcList;
    }

    public static String makeCaps(String name) {
        if (name.equals(""))
            return "";
        String firstLetter = name.substring(0, 1);

        String remainingLetters = name.substring(1, name.length());
        firstLetter = firstLetter.toUpperCase();        // change the first letter to uppercase

        return firstLetter + remainingLetters;          // join the two substrings
    }

    public static String buildPNewName(String name, String year) {
        String[] phrase = removeJPGExtension(name).split("-");
        String Neu = "";
        for (String p : phrase) {
            Neu = Neu + makeCaps(p) + "_";
        }
        String result = Neu.substring(0, Neu.length() - 1);

        return replacements(result, year) + ".jpg";
    }

    private static String replacements(String result,String year) {

        String r1, r2;
        r1 = result.replace("Fw", "FW");
        r2 = r1.replace("Mclaren", "McLaren");
        r1 = r2.replace("_Di_", "_di ");
        r2 = r1.replace("Force_India", "Force India");
        r1 = r2.replace("_Scuderia", "");
        r2 = r1.replace("Toro_Rosso", "Toro Rosso");
        r1 = r2.replace("Jean_Eric", "Jean-Eric");
        r2 = r1.replace("Red_Bull", "Red Bull");
        r1 = r2.replace("Van_Der_", "van der ");
        r2 = r1.replace("_F1", "");
        r1 = r2.replace("_Amg", "");
        r2 = r1.replace("Alphatauri", "Alpha Tauri");
        r1 = r2.replace("Alpha_Tauri", "Alpha Tauri");
        r2 = r1.replace("Racing_Point", "Racing Point");
        r1 = r2.replace("_Sahara", "");
        r2 = r1.replace("Racing_", "");
        r1 = r2.replace("_Team", "");
        r2 = r1.replace("F1_", "");
        r1 = r2.replace("_Gp", "");
        r2 = r1.replace("_"+year, "");
        r1 = r2.replace("Racing.", ".");
        return Delswords(r1.replace("__","_"));
    }

    private static String Delswords(String r2) {
        String s = r2;
        for(String r:Stopwords)
            s=s.replace(r,"");
        return s;
    }

    public static String removeJPGExtension(String fname) {
        int pos = fname.lastIndexOf('.');
        if (pos > -1)
            return fname.substring(0, pos);
        else
            return fname;
    }

    static boolean renameFileSimple(String path, String alt, String neu) {

        Path target = Paths.get(path + neu);
        Path source = Paths.get(path + alt);
        try {
            Files.move(source, target);
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> listOfFilenames(String PATH) {
        File folder = new File(PATH);
        File[] listOfFiles = folder.listFiles();
        List<String> tmpList = new ArrayList<>();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile() && !listOfFile.isHidden()) {
                tmpList.add(listOfFile.getName());
            }
        }
        return tmpList;
    }

    public static void renameEverything(String directoryPath, String searchString, String replacementString) {
        List<String> listOfFiles = listOfFilenames(directoryPath);
        for (String f : listOfFiles) {
            String oldName = f;
            String newName = f.replace(searchString, replacementString);
            if (!newName.equals(oldName)) {
                if (!renameFileSimple(directoryPath + "/", oldName, newName))
                    System.out.println("Error renaming: " + directoryPath + "/" + oldName);
            }
        }
    }






    public static void main(String[] args) {
           //einzelne Seiten
        String[] Event = {
                "Hungary_Budapest","Hungary_Budapest"
        };
        Integer[] Year=new Integer[Event.length];
        for(int i=0;i<Event.length;i++)
            Year[i]  =2024;

        String[] myURL =
                {
                        "https://www.autosport.com/f1/photos/?filters%5Bchampionship%5D%5B%5D=4660&filters%5Bevent%5D%5B%5D=277433&filters%5Brace_type%5D%5B%5D=54",
                        "https://www.autosport.com/f1/photos/?filters%5Bchampionship%5D%5B0%5D=4660&filters%5Bchampionship%5D%5B1%5D=4660&filters%5Bevent%5D%5B0%5D=277433&filters%5Bevent%5D%5B1%5D=277433&filters%5Brace_type%5D%5B0%5D=54&filters%5Brace_type%5D%5B1%5D=54&entity_type=photo&_=1722449787721&p=2"
     };


        /*
        String[] Event = {
                "Australia_Melbourne",
                "Bahrain_Sakhir",
                "China_Shanghai",
                "Azerbaijan_Baku",
                "Spain_Barcelona",
                "Monaco_Monte-Carlo",
                "Canada_Montreal",
                "France_Magny Cours",
                "Austria_Spielberg",
                "Great Britain_Silverstone",
                "Germany_Hockenheim",
                "Hungary_Budapest",
                "Belgium_Spa-Francorchamps",
                "Italy_Monza",
                "Singapore_Marina Bay",
                "Russia_Sotschi",
                 "Japan_Suzuka",
                "Mexico_Mexico-City",
                "USA_Austin",
                "Brazil_Sao Paulo",
                "Abu Dhabi_Yas Marina Circuit"  // Malaysia_Sepang
                };
        Integer[] EventNr ={

        };

        String [] myURL = new String[Event.length];
        Integer [] Year = new Integer[Event.length];
        int EventNumber = 261049;
        // https://www.autosport.com/f1/photos/?filters%5Bchampionship%5D%5B%5D=2098&filters%5Bevent%5D%5B%5D=261049&filters%5Brace_type%5D%5B%5D=54
        for(int i=0;i<Event.length;i++) {
            EventNumber= (EventNumber+i);
            myURL[i] = "https://www.autosport.com/f1/photos/?filters%5Bchampionship%5D%5B%5D=2098&filters%5Bevent%5D%5B%5D="+EventNr[i]+"&filters%5Brace_type%5D%5B%5D=54";
            Year[i]  =2019;
        }
*/
        for(int j=0;j<Event.length;j++)
        {

            String OutputPath = "/Users/eathanassiou/Autosport/" + Year[j] + "_" + Event[j] + ".html";
            String WorkDir = "/Users/eathanassiou/Autosport/" + Year[j] + "_" + Event[j];
            String URLCore = "https://www.autosport.com";
            getURL(myURL[j], OutputPath);
            int k=0;
           MkDir(WorkDir);
            List<String> imageSrcList = extractImageSrcListAutosport(OutputPath);
            for (String i : imageSrcList) {
                System.out.println(i);
                if(!i.endsWith(".jpg"))
                    continue;

                String NameNeu = getLastToken2(i);
                String OutImg = WorkDir + "/" + Year[j] + "_" + Event[j] + "_" + buildPNewName(NameNeu, String.valueOf(Year[j]));
                String ImageURL = i.replace("s300", "s1200");

                System.out.println(k++ +"\t:ImageURL :" + ImageURL + "\nOutImg :" + OutImg);
                ImageDownloader(ImageURL, OutImg.replace("__","_"));
            }
    }
    }
}