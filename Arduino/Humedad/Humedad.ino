#include <DHT.h>

#define DHTPIN 4    // Asegúrate de que el pin esté correctamente conectado
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

void setup() {
  Serial.begin(9600);
  dht.begin();
  delay(2000);  // Espera de inicialización
}

void loop() {
  delay(2000);  // Tiempo entre lecturas

  float h = dht.readHumidity();

  if (isnan(h)) {
    Serial.println("Error al leer el sensor.");

  } else {
    Serial.print("Humedad: ");
    Serial.print(h);

  }
}
