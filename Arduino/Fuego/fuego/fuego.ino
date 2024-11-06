#define SENSOR_PIN 12

bool flameSensor;
bool fire;

void setup() {
  Serial.begin(9600);
  pinMode(SENSOR_PIN, INPUT);
}

void loop() {
  flameSensor = digitalRead(SENSOR_PIN);

  if (flameSensor && !fire)
  {
    Serial.println("Llama detectada!");
    fire = true;
  }

  if (!flameSensor && fire)
  {
    Serial.println("Llama apagada");
    fire = false;
  }

  delay(200);
} 
