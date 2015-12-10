package compfisica;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Mapa {

    private int zoom;
//    private String lat;
//    private String lon;

    public ImageIcon CriaMapa(String lat, String lon, int zoom) throws MalformedURLException, IOException {
//        this.lat = lat;
//        this.lon = lon;
        this.zoom = zoom;
        ImageIcon mapa = atualizaMapa(zoom, lat, lon);
        
        return mapa;
    }

    public ImageIcon atualizaMapa(int zoom, String lat, String lon) throws IOException {
        this.zoom = zoom;
        String tipo = "satellite";
        String chave = "AIzaSyB-67AcEfWmdoHh9IsSehjIduwcvH-e3WE";

        String endereco = "http://maps.googleapis.com/maps/api/staticmap?center=" + lat + "," + lon + "&zoom=" + zoom + "&size=700x640&maptype=" + tipo + "&key=" + chave + "&sensor=false&format=jpg";
        BufferedImage img = null;

        URL url = new URL(endereco);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        img = ImageIO.read(con.getInputStream());

        ImageIcon mapa = new ImageIcon();

        if (img != null) {
            mapa = new ImageIcon(img);
//            mapaLable.setIcon(mapa);
        }
        return mapa;
    }
}
