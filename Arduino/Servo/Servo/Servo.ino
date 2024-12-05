#include <Servo.h>

#define SERVO_PIN 9
//Controlador de servo
Servo myServo;

//Posicion servo
int servoPos = 0;

// Declaración de la variable del mensaje recibido
String serverMessage;
// Clave
String key;
// Valor
String value;
// Delimitador
char delimiter = '_';
// Posición del delimitador
int delimiterIndex;

void setup() {
  Serial.begin(9600); // Iniciar puerto serial a 9600 baud
  myServo.attach(SERVO_PIN);
  myServo.write(servoPos);
}

void loop() {
  serverMessage = Serial.readStringUntil('\n'); // Leer strings del puerto serial hasta encontrar '\n'

  /* Descomponer el mensaje en las partes que me interesan */
  delimiterIndex = serverMessage.indexOf(delimiter); // Obtener la posición del delimitador
  key = serverMessage.substring(0, delimiterIndex); // Obtener la clave
  value = serverMessage.substring(delimiterIndex + 1); // Obtener el valor

  /* Procesamiento del mensaje */
  if (key == "SERVO") { // Mover el servo a una posición entre 0 y 180 grados
    if (180 >= value.toInt() && value.toInt() >= 0) { // Límite para las posiciones que puede tomar el servo
      if (value.toInt() < servoPos) {
        while (servoPos > value.toInt()) {
          servoPos--;
          myServo.write(servoPos);
          delay(15);
        }
      } else if (value.toInt() > servoPos) {
        while (servoPos < value.toInt()) {
          servoPos++;
          myServo.write(servoPos);
          delay(15);
        }
      }
    }
  }
}