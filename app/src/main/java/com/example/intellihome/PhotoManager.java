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
import java.util.ArrayList;
import java.util.List;

public class PhotoManager {

    private final Activity activity;
    private Button btnProfilePhoto;
    private LinearLayout linearLayout;
    private List<Bitmap> photoUris; // Lista para almacenar las URIs de las fotos
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGE = 2;
    public static final int REQUEST_CAMERA_PERMISSION = 100;

    // Constructor con Button
    public PhotoManager(Activity activity, Button btnProfilePhoto) {
        this.activity = activity;
        this.btnProfilePhoto = btnProfilePhoto;
        this.photoUris = new ArrayList<>();
    }

    // Constructor con LinearLayout
    public PhotoManager(Activity activity, LinearLayout linearLayout) {
        this.activity = activity;
        this.linearLayout = linearLayout;
        this.photoUris = new ArrayList<>();
    }

    // Mostrar el diálogo de selección de fotos
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

    // Método para manejar la imagen de la cámara
    public void handleCameraImage(Intent data, boolean isForButton) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            if (isForButton) {
                setButtonBackgroundFromBitmap(imageBitmap);
            } else {
                Bitmap resizedBitmap = resizeBitmapByHeight(imageBitmap, 105);
                addImageToScrollView(resizedBitmap);
                ((PublicarCasaActivity) activity).agregarImagenALaLista(resizedBitmap); // Agrega la imagen a la lista
            }
        }
    }

    // Método para manejar la imagen de la galería
    public void handleGalleryImage(Intent data, boolean isForButton) {
        Uri selectedImage = data.getData();
        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImage);

            if (isForButton) {
                setButtonBackgroundFromBitmap(imageBitmap);
            } else {
                Bitmap resizedBitmap = resizeBitmapByHeight(imageBitmap, 105);
                addImageToScrollView(resizedBitmap);
                ((PublicarCasaActivity) activity).agregarImagenALaLista(resizedBitmap); // Agrega la imagen a la lista
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Añadir imagen al LinearLayout
    private void addImageToScrollView(Bitmap bitmap) {
        ImageView imageView = new ImageView(activity);

        imageView.setImageBitmap(bitmap);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (int) (110 * activity.getResources().getDisplayMetrics().density)
        );
        params.setMargins(
                (int) (7 * activity.getResources().getDisplayMetrics().density),
                0,
                (int) (7 * activity.getResources().getDisplayMetrics().density),
                0
        );
        imageView.setLayoutParams(params);
        linearLayout.addView(imageView);
        photoUris.add(bitmap);
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

    // Redimensionar Bitmap por altura
    private Bitmap resizeBitmapByHeight(Bitmap bitmap, int heightDp) {
        float density = activity.getResources().getDisplayMetrics().density;
        int heightPx = Math.round(heightDp * density);

        int widthPx = Math.round((float) bitmap.getWidth() * ((float) heightPx / (float) bitmap.getHeight()));
        return Bitmap.createScaledBitmap(bitmap, widthPx, heightPx, true);
    }

    // Manejar permisos solicitados
    public void handleRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == PICK_IMAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(activity, activity.getString(R.string.accesoimaRegisterActivity), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para obtener el número de fotos añadidas
    public int getPhotoCount() {
        // Devuelve el número de fotos almacenadas en photoUris
        return photoUris.size();
    }

}