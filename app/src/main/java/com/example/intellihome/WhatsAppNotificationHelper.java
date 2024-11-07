    package com.example.intellihome;

    import static androidx.core.content.ContextCompat.startActivity;

    import android.content.Context;
    import android.content.Intent;
    import android.net.Uri;
    import android.widget.Toast;

    import java.io.PrintWriter;
    import java.net.Socket;
    import java.util.Scanner;

    public class WhatsAppNotificationHelper {

        private static Socket socket;
        private static PrintWriter out;
        private static Scanner in;

        public static void allowTwilioMessageWhatsApp(Context context) {
            String twilioTestNumber = "+1 415 523 8886";
            String code = "join wrong-said";

            try {
                String url = "https://api.whatsapp.com/send?phone=" + twilioTestNumber + "&text=" + Uri.encode(code);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.setPackage("com.whatsapp");

                context.startActivity(intent);

            } catch (Exception e) {
                Toast.makeText(context, "!Error : A Failure Occur...", Toast.LENGTH_SHORT).show();
            }

            // (usage e.g.) <WhatsAppNotificationHelper.allowTwilioMessageWhatsApp(this);>
        }

        public static void sendWhatsAppMessageViaServer(String host, int port, String phoneNumber, String message) {
            new Thread(() -> {
                try {
                    socket = new Socket(host, port);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new Scanner(socket.getInputStream());

                    String formattedMessage = "WhatsApp/" + phoneNumber + "/" + message;
                    out.println(formattedMessage);

                    out.close();
                    in.close();
                    socket.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
