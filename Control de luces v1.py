import network
import usocket as socket
import time
from machine import Pin

# Configuración de la red WiFi
nombrewifi = "Nexxt_DE4168"
contrasena = "Ams140568"

# Conexión a la red WiFi
wifi = network.WLAN(network.STA_IF)
wifi.active(True)
wifi.connect(nombrewifi, contrasena)
while not wifi.isconnected():
    time.sleep(0.1)
print("Conexión establecida:", wifi.ifconfig())

# Configuración de pines para los LEDs
led_pins = {
    'Z': Pin(13, Pin.OUT),
    'X': Pin(14, Pin.OUT),
    'C': Pin(15, Pin.OUT),
    'V': Pin(16, Pin.OUT),
    'B': Pin(17, Pin.OUT),
    'N': Pin(18, Pin.OUT),
    'M': Pin(19, Pin.OUT),
}

# Estado de los LEDs
led_states = {
    'Z': False,
    'X': False,
    'C': False,
    'V': False,
    'B': False,
    'N': False,
    'M': False,
}

def toggle_led(letter):
    if letter in led_pins:
        # Cambiar el estado del LED
        led_states[letter] = not led_states[letter]
        led_pins[letter].value(led_states[letter])  # Encender/apagar el LED
        print(f"LED asociado a la letra {letter} {'encendido' if led_states[letter] else 'apagado'}")

# Configuración del socket
s = socket.socket()
s.bind(('0.0.0.0', 1234))
s.listen(1)
print("Esperando conexiones...")

conn, addr = s.accept()  
print("Conexión aceptada de:", addr)

try:
    while True:
        data = conn.recv(1024)  # Recibe datos del cliente
        if not data:
            break  # Sale si no hay más datos
        letra = data.decode('utf-8').upper()  # Decodifica la letra recibida y la convierte a mayúsculas
        print("Letra recibida:", letra)
        toggle_led(letra)  # Llama a la función para encender/apagar el LED correspondiente
finally:
    conn.close()  # Cierra la conexión
