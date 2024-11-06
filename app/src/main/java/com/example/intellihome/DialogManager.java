package com.example.intellihome;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DialogManager {

    private final Context context;

    public DialogManager(Context context) {
        this.context = context;
    }

    // Mostrar los términos y condiciones en un diálogo
    public void showTermsAndConditionsDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_terms_and_conditions, null);
        dialogBuilder.setView(dialogView);

        AlertDialog dialog = dialogBuilder.create();

        // Inicializar los elementos del layout
        Button btnContinue = dialogView.findViewById(R.id.btnContinue);

        // Botón de continuar cierra el diálogo
        btnContinue.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Mostrar los requerimientos de la contraseña
    public void showPasswordRequirementsDialog() {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.requecontraRegisterActivity))
                .setMessage(context.getString(R.string.contradebecontRegisterActivity) + "\n" +
                        context.getString(R.string.mayusculacontraRegisterActivity) + "\n" +
                        context.getString(R.string.minusculacontraRegisterActivity) + "\n" +
                        context.getString(R.string.numcontraRegisterActivity) + "\n" +
                        context.getString(R.string.simbocontraRegisterActivity) + "\n" +
                        context.getString(R.string.mincaractcontraRegisterActivity))
                .setPositiveButton("OK", null)
                .show();
    }

    // Mostrar un diálogo para elegir entre tomar una foto o seleccionar de la galería
    public void showPhotoSelectionDialog(DialogInterface.OnClickListener listener) {
        String[] options = {context.getString(R.string.tomarfotoRegisterActivity), context.getString(R.string.selectgaleRegisterActivity)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.selectimaRegisterActivity))
                .setItems(options, listener)
                .show();
    }

    public static class PhotoManager {

        private final Activity activity;
        private final Button btnProfilePhoto;

        public static final int REQUEST_IMAGE_CAPTURE = 1;
        public static final int PICK_IMAGE = 2;
        public static final int REQUEST_CAMERA_PERMISSION = 100;

        public PhotoManager(Activity activity, Button btnProfilePhoto) {
            this.activity = activity;
            this.btnProfilePhoto = btnProfilePhoto;
        }

        // Mostrar un diálogo para tomar la foto o seleccionar de la galería
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
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA)
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
                setButtonBackgroundFromBitmap(imageBitmap);
            }
        }

        // Método para manejar la imagen seleccionada de la galería
        public void handleGalleryImage(Intent data) {
            Uri selectedImage = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImage);
                setButtonBackgroundFromBitmap(imageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    }
}

