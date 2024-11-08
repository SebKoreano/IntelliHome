#include <Servo.h> // Para servo
#include <DHT.h> // Para sensor de humedad

#define SERVO_PIN 9
#define SENSOR_FLAME_PIN 12
#define MOVE_SENSOR_PIN 3

//Definicion de pin y creacion deobjeto DHT para poder leer humedad.
#define DHTPIN 4    
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);


// Para servo
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

// Para detector de llama
// booleans para identificar fuego
bool flameSensor;
bool fire;


void setup() {
  Serial.begin(9600); // Iniciar puerto serial a 9600 baud

  // Inicio servo
  myServo.attach(SERVO_PIN);
  myServo.write(servoPos);

  // Sensor humedad modo salida
  pinMode(SENSOR_FLAME_PIN, INPUT);

  // Inicia modo lectura de datos de humedad
  dht.begin();

  // Sensor de movimiento en modo salida
  pinMode(MOVE_SENSOR_PIN, INPUT);
}

void loop() {
  serverMessage = Serial.readStringUntil('\n'); // Leer strings del puerto serial hasta encontrar '\n', esto solo es necesario para servo.
  flameSensor = digitalRead(SENSOR_FLAME_PIN); // Lee el valor de fuego.
  int switchState = digitalRead(MOVE_SENSOR_PIN);

  // Condiciones para ver fuego.
  if (flameSensor && !fire)
  {
    Serial.println("Llama detectada!");
    Serial.write("F1"); //F1 indica fuego prendido
    fire = true;
  }

  if (!flameSensor && fire)
  {
    Serial.println("No hay llama cerca.");
    Serial.write("F2"); //F2 indica fuego fue apagado
    fire = false;
  }


  //Esto verifica si llego un mensaje con indicaciones para mover servo
  if (serverMessage.startsWith("SERVO_")) {
    // Descomponer el mensaje en las partes que me interesan 
    delimiterIndex = serverMessage.indexOf(delimiter); // Obtener la posición del delimitador
    key = serverMessage.substring(0, delimiterIndex); // Obtener la clave
    value = serverMessage.substring(delimiterIndex + 1); // Obtener el valor

    // Procesamiento del mensaje 
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

  // Caso para analizar humedad
  // Lectura de sensor de humedad esto se mide en porcentaje en relacion con superficie de mdicion
  float h = dht.readHumidity();

  if (isnan(h)) {
    Serial.println("Error al leer el sensor.");
  } else {
    Serial.println(h);
    String mensaje = "Humedad:" + String(h); 
    Serial.write(mensaje.c_str()); // enviar valor de humedad
  }

  


  // Caso para leer sensor de inclinacion
  if (switchState == HIGH) {
    Serial.println("No hay inclinación (LOW)");
  } else {
    Serial.println("Inclinación detectada (HIGH)");
    Serial.write('T'); //Indicador de temblor
  }

  delay(2000);
}