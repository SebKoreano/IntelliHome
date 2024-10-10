package com.example.intellihome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

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
}

