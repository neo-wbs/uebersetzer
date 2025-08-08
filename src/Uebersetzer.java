import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class Uebersetzer extends JFrame {
    private JTextField txtText = new JTextField("", 20);
    private JComboBox cbSprachen = new JComboBox<>(
            new String[]{"en","fr","es","it","pt","ru","ja","zh","pl"}
    );
    private JLabel lblUebersetzt = new JLabel("Übersetzung ...");
    private JButton btnUebersetze = new JButton("Übersetze");
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public Uebersetzer() {
        super("Übersetzer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        add(new Label("Eure Eingabe"));
        add(txtText);
        add(new Label("Zielsprache"));
        add(cbSprachen);
        add(btnUebersetze);
        add(lblUebersetzt);

        btnUebersetze.addActionListener((e) -> translateAsync());
        setSize(600, 150);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void translateAsync() {
        String quelltext = txtText.getText().trim();
        String zielsprache = (String) cbSprachen.getSelectedItem();
        if(quelltext.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Etwas eingeben !!!");
            return;
        }
        //Erwartet Consumer: 1 Parameter, keine Rückgabe
        fetchTranslation(quelltext, zielsprache)
                .thenAccept(translation -> SwingUtilities.invokeLater(() ->
                    lblUebersetzt.setText(translation)))
                .exceptionally(ex -> {
                    SwingUtilities.invokeLater(() ->
                        lblUebersetzt.setText(ex.getMessage()));
                        return null;
                    });
    }

    //Gibt einen zukünftigen (Future) String zurück
    //URLEncoder.encode wandelt Sonderzeichen, Leerzeichen etc. in URL sichere Codes um
    //Beispiel:
    //Leerzeichen -> %20
    //Ich & Du -> Ich+%26+Du
    private CompletableFuture<String> fetchTranslation(String quelltext, String zielsprache) {
        //Supplier: Formuliere Lambda, der keinen Parameter hat, aber Rückgabewert (hier String)
        return CompletableFuture.supplyAsync(() -> {
            //Aufgabe in Supplier-Form
            try {
                String url = "https://api.mymemory.translated.net/get"
                        + "?q=" + URLEncoder.encode(quelltext, StandardCharsets.UTF_8)
                        + "&langpair=de|" + zielsprache;
                String json = fetchAPI(url);
                JSONObject root = new JSONObject(json);
                JSONObject responseData = root.getJSONObject("responseData");
                return responseData.getString("translatedText");
            } catch (Exception ex) {
                return "Übersetzung schiefgelaufen";
            }
        }, executor)
        .orTimeout(10, TimeUnit.SECONDS);
    }

    //Daten abholen von API-Adresse
    private String fetchAPI(String url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET"); // POST, PUT ...
        conn.setConnectTimeout(5000); //Zeit Verbindung aufzubauen
        conn.setReadTimeout(10000); //Zeit Daten abzufragen
        try(BufferedReader reader = new BufferedReader(
              new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
        )) {
            StringBuilder sb = new StringBuilder();
            String zeile;
            //Lese Zeile aus InputStream -> Weise zeile zu -> solange zeile != null
            while ((zeile=reader.readLine()) != null) {
                sb.append(zeile);
            }
            return sb.toString();
        }
    }

    @Override
    public void dispose() {
        executor.shutdownNow();
        super.dispose();
    }

    private String info() {
        return "Neues Feature zu Infos Übersetzer";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Uebersetzer::new);
    }
}
