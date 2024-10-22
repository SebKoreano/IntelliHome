package com.example.intellihome;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class PhotoManager {

    private final Activity activity;
    private Button btnProfilePhoto;
    private LinearLayout linearLayout;

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE = 2;
    public static final int REQUEST_CAMERA_PERMISSION = 100;

    // Constructor con Button
    public PhotoManager(Activity activity, Button btnProfilePhoto) {
        this.activity = activity;
        this.btnProfilePhoto = btnProfilePhoto;
    }

    // Constructor con LinearLayout
    public PhotoManager(Activity activity, LinearLayout linearLayout) {
        this.activity = activity;
        this.linearLayout = linearLayout;
    }

    // Método para mostrar el diálogo y gestionar la selección de fotos
    public void showPhotoSelectionDialog(DialogInterface.OnClickListener listener) {
        String[] options = {
                activity.getString(R.string.tomarfotoRegisterActivity),
                activity.getString(R.string.selectgaleRegisterActivity)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.selectimaRegisterActivity))
                .setItems(options, listener)
                .show();
    }

    // Método para abrir la cámara
    public void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // Método para abrir la galería
    public void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(pickPhotoIntent, PICK_IMAGE);
    }

    // Método para manejar la imagen tomada con la cámara
    public void handleCameraImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Redimensionar la imagen a 110dp de altura (ancho proporcional)
            Bitmap resizedBitmap = resizeBitmapByHeight(imageBitmap, 105);
            addImageToScrollView(resizedBitmap);
        }
    }

    // Método para manejar la imagen seleccionada de la galería
    public void handleGalleryImage(Intent data) {
        Uri selectedImage = data.getData();
        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImage);

            // Redimensionar la imagen a 110dp de altura (ancho proporcional)
            Bitmap resizedBitmap = resizeBitmapByHeight(imageBitmap, 105);
            addImageToScrollView(resizedBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para añadir la imagen al HorizontalScrollView
    private void addImageToScrollView(Bitmap bitmap) {
        // Crear un ImageView nuevo
        ImageView imageView = new ImageView(activity);

        // Establecer la imagen redimensionada como contenido del ImageView
        imageView.setImageBitmap(bitmap);

        // Configurar las dimensiones del ImageView (wrap_content para el ancho, 110dp para la altura)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // Mantener el ancho original
                (int) (110 * activity.getResources().getDisplayMetrics().density) // 110dp en altura
        );

        // Agregar margen de 7dp a la izquierda y a la derecha
        params.setMargins(
                (int) (7 * activity.getResources().getDisplayMetrics().density), // margen izquierda
                0, // sin margen arriba
                (int) (7 * activity.getResources().getDisplayMetrics().density), // margen derecha
                0  // sin margen abajo
        );

        // Asignar los parámetros de diseño al ImageView
        imageView.setLayoutParams(params);

        // Añadir el ImageView al LinearLayout
        linearLayout.addView(imageView);
    }


    // Establecer el fondo del botón con un Bitmap circular
    private void setButtonBackgroundFromBitmap(Bitmap bitmap) {
        Bitmap circularBitmap = getCircularBitmap(bitmap);
        Drawable drawableImage = new BitmapDrawable(activity.getResources(), circularBitmap);
        btnProfilePhoto.setBackground(drawableImage);
        btnProfilePhoto.setBackgroundTintList(null);
        btnProfilePhoto.getLayoutParams().width = btnProfilePhoto.getLayoutParams().height;
        btnProfilePhoto.requestLayout();
    }

    // Recortar el Bitmap a una forma circular
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, width);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFF000000);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // Método para insertar una imagen en el LinearLayout
    private void insertImageIntoLinearLayout(Bitmap bitmap) {
        ImageView imageView = new ImageView(activity);
        imageView.setImageBitmap(bitmap);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        linearLayout.addView(imageView);
    }

    // Manejar los permisos solicitados
    public void handleRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == PICK_IMAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(activity, activity.getString(R.string.accesoimaRegisterActivity), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para redimensionar el Bitmap a 100x100 dp
    private Bitmap resizeBitmap(Bitmap bitmap, int widthDp, int heightDp) {
        // Convertir dp a píxeles
        float density = activity.getResources().getDisplayMetrics().density;
        int widthPx = Math.round(widthDp * density);
        int heightPx = Math.round(heightDp * density);

        return Bitmap.createScaledBitmap(bitmap, widthPx, heightPx, true);
    }

    // Método para redimensionar el Bitmap a una altura específica (110dp de altura, ancho proporcional)
    private Bitmap resizeBitmapByHeight(Bitmap bitmap, int heightDp) {
        // Convertir dp a píxeles
        float density = activity.getResources().getDisplayMetrics().density;
        int heightPx = Math.round(heightDp * density);

        // Calcular el ancho proporcionalmente
        int widthPx = Math.round((float) bitmap.getWidth() * ((float) heightPx / (float) bitmap.getHeight()));

        return Bitmap.createScaledBitmap(bitmap, widthPx, heightPx, true);
    }

}

