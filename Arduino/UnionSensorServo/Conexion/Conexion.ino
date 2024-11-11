#include <Servo.h> // Para el servo
#include <DHT.h>   // Para el sensor de humedad

#define SERVO_PIN 9
#define SENSOR_FLAME_PIN 12
#define MOVE_SENSOR_PIN 3

// Definición de pin y creación del objeto DHT para poder leer humedad.
#define DHTPIN 4    
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

// Controlador del servo
Servo myServo;
int servoPos = 90; // Posición inicial del servo

// Declaración de la variable del mensaje recibido
String serverMessage;
String key, value;
char delimiter = '_';
int delimiterIndex;

// Variables para el sensor de llama y movimiento
bool flameSensor, fire;

void setup() {
  Serial.begin(9600); // Iniciar puerto serial a 9600 baud

  // Inicializar el servo
  myServo.attach(SERVO_PIN);
  myServo.write(servoPos);

  // Configurar los pines del sensor de llama y movimiento
  pinMode(SENSOR_FLAME_PIN, INPUT);
  pinMode(MOVE_SENSOR_PIN, INPUT);

  // Iniciar el sensor de humedad
  dht.begin();
}

void loop() {
  // Leer mensaje desde el servidor para el control del servo
  serverMessage = Serial.readStringUntil('\n'); 
  if (serverMessage.startsWith("SERVO_")) {
    delimiterIndex = serverMessage.indexOf(delimiter);
    key = serverMessage.substring(0, delimiterIndex);
    value = serverMessage.substring(delimiterIndex + 1);

    if (key == "SERVO") {
      int targetPos = value.toInt();
      if (targetPos >= 0 && targetPos <= 180) {
        while (servoPos != targetPos) {
          servoPos += (targetPos > servoPos) ? 1 : -1;
          myServo.write(servoPos);
          delay(15);
        }
      }
    }
  }

  // Verificar sensor de llama
  flameSensor = digitalRead(SENSOR_FLAME_PIN);
  if (flameSensor && !fire) {
    Serial.println("Llama detectada!");
    Serial.println("F1"); // F1 indica fuego detectado
    fire = true;
  } else if (!flameSensor && fire) {
    Serial.println("No hay llama cerca.");
    Serial.println("F2"); // F2 indica que ya no hay fuego
    fire = false;
  }

  // Leer y enviar humedad
  float humedad = dht.readHumidity();
  if (!isnan(humedad)) {
    Serial.print("Humedad:");
    Serial.println(humedad, 2); // Enviar con dos decimales
  } else {
    Serial.println("Error al leer el sensor de humedad.");
  }

  // Verificar inclinación (sensor de movimiento)
  int switchState = digitalRead(MOVE_SENSOR_PIN);
  if (switchState == HIGH) {
    Serial.println("Inclinación:No detectada");
  } else {
   
    Serial.println("T"); // T indica inclinación detectada
  }

  // Pequeño retraso entre cada envío de datos
  delay(2000);
}
