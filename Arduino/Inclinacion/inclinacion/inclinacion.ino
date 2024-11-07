// Define el pin del switch de inclinación
const int switchPin = 4;

void setup() {
  // Configura el pin del switch como entrada
  pinMode(switchPin, INPUT);

  // Inicia la comunicación serial a 9600 baudios
  Serial.begin(9600);
}

void loop() {
  // Lee el estado del switch de inclinación
  int switchState = digitalRead(switchPin);

  // Imprime el estado del switch en el Monitor Serie
  if (switchState == HIGH) {
    Serial.println("No hay inclinación (LOW)");
  } else {
    Serial.println("Inclinación detectada (HIGH)");
  }

  // Espera un momento para evitar lecturas demasiado rápidas
  delay(500);
}