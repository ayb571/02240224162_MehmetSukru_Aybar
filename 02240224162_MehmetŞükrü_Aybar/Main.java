import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;

public class Main {

    private static String path = "ornek.html";
    private static JFrame mainFrame;
    private static JPanel mainPanel;
    private static JTextArea sonTextArea = null;

    public static void main(String[] args) {
        String html = dosyaOku(path);

        if (html == null || html.length() == 0) {
            System.out.println("Dosya bulunamadi veya bos!");
            return;
        }

        htmlAyristirVeCiz(html);
    }

    public static String altMetin(String s, int bas, int son) {
        String sonuc = "";
        for (int i = bas; i < son && i < s.length(); i++) {
            sonuc += s.charAt(i);
        }
        return sonuc;
    }

    public static boolean boslukMu(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    public static String boslukTemizle(String s) {
        int bas = 0;
        int son = s.length() - 1;

        while (bas <= son && boslukMu(s.charAt(bas))) {
            bas++;
        }

        while (son >= bas && boslukMu(s.charAt(son))) {
            son--;
        }

        return altMetin(s, bas, son + 1);
    }

    public static boolean kucukHarfElesirMi(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        }

        for (int i = 0; i < s1.length(); i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);

            if (c1 >= 'A' && c1 <= 'Z')
                c1 += 32;
            if (c2 >= 'A' && c2 <= 'Z')
                c2 += 32;

            if (c1 != c2)
                return false;
        }

        return true;
    }

    public static boolean ileBasliyorMu(String metin, String onEk) {
        if (metin.length() < onEk.length())
            return false;

        for (int i = 0; i < onEk.length(); i++) {
            char c1 = metin.charAt(i);
            char c2 = onEk.charAt(i);

            if (c1 >= 'A' && c1 <= 'Z')
                c1 += 32;
            if (c2 >= 'A' && c2 <= 'Z')
                c2 += 32;

            if (c1 != c2)
                return false;
        }

        return true;
    }

    public static String dosyaOku(String dosyaYolu) {
        String icerik = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(dosyaYolu))) {
            int karakter;

            while ((karakter = reader.read()) != -1) {
                icerik += (char) karakter;
            }
        } catch (IOException e) {
            System.out.println("Dosya okuma hatasi: " + e.getMessage());
            return null;
        }

        return icerik;
    }

    public static void htmlAyristirVeCiz(String html) {
        mainFrame = new JFrame("HTML Sayfasi");
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        mainFrame.add(new JScrollPane(mainPanel));

        String aktifEtiket = "";
        String metinTamponu = "";
        int i = 0;

        while (i < html.length()) {
            char c = html.charAt(i);

            if (c == '<') {
                if (metinTamponu.length() > 0) {
                    metinEkle(aktifEtiket, metinTamponu);
                    metinTamponu = "";
                }

                int bitis = i + 1;

                while (bitis < html.length() && html.charAt(bitis) != '>') {
                    bitis++;
                }

                if (bitis >= html.length())
                    break;

                String icerik = boslukTemizle(altMetin(html, i + 1, bitis));

                boolean kendiKapanir = false;

                if (icerik.length() > 0 && icerik.charAt(icerik.length() - 1) == '/') {
                    kendiKapanir = true;
                    icerik = boslukTemizle(altMetin(icerik, 0, icerik.length() - 1));
                }

                String etiket = "";
                int j = 0;

                while (j < icerik.length() && !boslukMu(icerik.charAt(j)) && icerik.charAt(j) != '/') {
                    etiket += icerik.charAt(j);
                    j++;
                }

                if (etiket.length() > 0) {
                    if (etiket.charAt(0) == '/') {
                        String kapanan = yalinEtiketKontrol(etiket);

                        if (kucukHarfElesirMi(kapanan, "textarea")) {
                            sonTextArea = null;
                        }

                        aktifEtiket = "";
                    } else {
                        etiketIsle(etiket);

                        if (!kendiKapanir
                                && !kucukHarfElesirMi(etiket, "input")
                                && !kucukHarfElesirMi(etiket, "br")) {
                            aktifEtiket = etiket;
                        } else {
                            aktifEtiket = "";
                        }
                    }
                }

                i = bitis;
            } else {
                metinTamponu += c;
            }

            i++;
        }

        if (metinTamponu.length() > 0) {
            metinEkle(aktifEtiket, metinTamponu);
        }

        mainFrame.setSize(500, 750);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    public static String yalinEtiketKontrol(String etiket) {
        if (etiket.length() > 0 && etiket.charAt(0) == '/') {
            return altMetin(etiket, 1, etiket.length());
        }
        return etiket;
    }

    public static void etiketIsle(String etiket) {
        if (ileBasliyorMu(etiket, "!DOCTYPE") ||
                kucukHarfElesirMi(etiket, "html") ||
                kucukHarfElesirMi(etiket, "head") ||
                kucukHarfElesirMi(etiket, "body"))
            return;

        if (mainPanel == null)
            return;

        if (kucukHarfElesirMi(etiket, "br")) {
            mainPanel.add(Box.createVerticalStrut(12));
        } else if (kucukHarfElesirMi(etiket, "input")) {
            JTextField alan = new JTextField();
            alan.setMaximumSize(new Dimension(280, 28));
            alan.setPreferredSize(new Dimension(280, 28));
            alan.setAlignmentX(Component.LEFT_ALIGNMENT);
            mainPanel.add(alan);
            mainPanel.add(Box.createVerticalStrut(8));
        } else if (kucukHarfElesirMi(etiket, "textarea")) {
            sonTextArea = new JTextArea(3, 20);
            sonTextArea.setLineWrap(true);
            sonTextArea.setWrapStyleWord(true);

            JScrollPane kaydirma = new JScrollPane(sonTextArea);
            kaydirma.setMaximumSize(new Dimension(280, 80));
            kaydirma.setPreferredSize(new Dimension(280, 80));
            kaydirma.setAlignmentX(Component.LEFT_ALIGNMENT);

            mainPanel.add(kaydirma);
            mainPanel.add(Box.createVerticalStrut(8));
        }
    }

    public static void metinEkle(String etiket, String metin) {
        if (mainPanel == null)
            return;

        if (kucukHarfElesirMi(etiket, "textarea") && sonTextArea != null) {
            sonTextArea.append(metin);
            return;
        }

        String temiz = boslukTemizle(metin);
        if (temiz.length() == 0)
            return;

        if (kucukHarfElesirMi(etiket, "title")) {
            mainFrame.setTitle(temiz);
            return;
        }

        JLabel label = null;

        if (kucukHarfElesirMi(etiket, "h1")) {
            label = new JLabel(temiz);
            label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        } else if (kucukHarfElesirMi(etiket, "h2")) {
            label = new JLabel(temiz);
            label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        } else if (kucukHarfElesirMi(etiket, "h3")) {
            label = new JLabel(temiz);
            label.setFont(new Font("Segoe UI", Font.BOLD, 17));
        } else if (kucukHarfElesirMi(etiket, "h4")) {
            label = new JLabel(temiz);
            label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        } else if (kucukHarfElesirMi(etiket, "h5")) {
            label = new JLabel(temiz);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        } else if (kucukHarfElesirMi(etiket, "h6")) {
            label = new JLabel(temiz);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        } else if (kucukHarfElesirMi(etiket, "p")
                || kucukHarfElesirMi(etiket, "span")
                || kucukHarfElesirMi(etiket, "label")) {
            label = new JLabel(temiz);
        } else if (kucukHarfElesirMi(etiket, "strong")) {
            label = new JLabel(temiz);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        } else if (kucukHarfElesirMi(etiket, "em")) {
            label = new JLabel(temiz);
            label.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        } else if (kucukHarfElesirMi(etiket, "a")) {
            label = new JLabel("<html><u>" + temiz + "</u></html>");
            label.setForeground(new Color(30, 100, 220));
        } else if (kucukHarfElesirMi(etiket, "button")) {
            JButton button = new JButton(temiz);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            mainPanel.add(button);
            mainPanel.add(Box.createVerticalStrut(8));
            return;
        }

        if (label != null) {
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            mainPanel.add(label);
            mainPanel.add(Box.createVerticalStrut(5));
        }
    }
}
