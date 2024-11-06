#include <Servo.h>

Servo servoMotor;
int angle = 90;  // Ángulo inicial

void setup() {
  servoMotor.attach(9);       // Conecta el servomotor al pin digital 9
  servoMotor.write(angle);    // Inicia el servo en la posición media (90 grados)
  Serial.begin(9600);         // Inicia la comunicación serial a 9600 baudios
}

void loop() {
  if (Serial.available() > 0) {   // Verifica si hay datos disponibles en el puerto serial
    char receivedChar = Serial.read();  // Lee el carácter recibido
    
    if (receivedChar == '1') {
      angle = 180;               // Define el ángulo para moverse a 180 grados
      servoMotor.write(angle);   // Mueve el servomotor a 180 grados
    } 
    else if (receivedChar == '0') {
      angle = 0;                 // Define el ángulo para moverse a 0 grados
      servoMotor.write(angle);   // Mueve el servomotor a 0 grados
    }
    
    delay(500);  // Espera medio segundo para darle tiempo al servo a moverse
  }
}
