void setup() {
  Serial.begin(9600);  // Iniciar la comunicación serial
  while (!Serial) {    // Esperar a que se establezca la conexión
    ; 
  }
}

void loop() {
  if (Serial.available() > 0) {
    // Leer el mensaje recibido
    String receivedMessage = Serial.readStringUntil('\n');
    Serial.print("Arduino recibió: ");
    Serial.println(receivedMessage);  // Responder con el mensaje recibido
    
    // Enviar una respuesta de prueba al mensaje recibido
    Serial.println("Respuesta de Arduino: Mensaje recibido");
  }

  delay(1000);  // Esperar un momento antes de leer de nuevo
}
