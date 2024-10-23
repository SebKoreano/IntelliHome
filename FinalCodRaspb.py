import network
import usocket as socket
import time
from machine import Pin

# Configuración de la red WiFi
nombrewifi = "Nexxt_DE4168"  # Nombre de la red WiFi (SSID)
contrasena = "Ams140568"     # Contraseña de la red WiFi

# Conexión a la red WiFi
wifi = network.WLAN(network.STA_IF)  # Configura el WiFi en modo estación
wifi.active(True)                    # Activa la interfaz WiFi
wifi.connect(nombrewifi, contrasena)  # Conecta a la red WiFi
while not wifi.isconnected():         # Espera hasta que se establezca la conexión
    time.sleep(0.1)                   # Pequeña pausa mientras espera la conexión
print("Conexión establecida:", wifi.ifconfig())  # Imprime la dirección IP de la Pico W

# Configuración de los pines de los LEDs
led_pins = {
    'Z': Pin(13, Pin.OUT),  # Configura el pin 13 para la letra 'Z'
    'X': Pin(14, Pin.OUT),  # Configura el pin 14 para la letra 'X'
    'C': Pin(15, Pin.OUT),  # Configura el pin 15 para la letra 'C'
    'V': Pin(16, Pin.OUT),  # Configura el pin 16 para la letra 'V'
    'B': Pin(17, Pin.OUT),  # Configura el pin 17 para la letra 'B'
    'N': Pin(18, Pin.OUT),  # Configura el pin 18 para la letra 'N'
    'M': Pin(19, Pin.OUT),  # Configura el pin 19 para la letra 'M'
}

# Estado inicial de los LEDs (todos apagados)
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
    """
    Cambia el estado del LED correspondiente a la letra recibida.
    """
    letter = letter.upper()  # Asegura que la letra esté en mayúsculas
    if letter in led_pins:   # Verifica que la letra tenga un LED asociado
        # Cambia el estado del LED (enciende/apaga)
        led_states[letter] = not led_states[letter]
        led_pins[letter].value(led_states[letter])  # Actualiza el estado del LED físico
        print(f"LED asociado a la letra {letter} {'encendido' if led_states[letter] else 'apagado'}")
    else:
        # Mensaje de error si la letra no tiene un LED asociado
        print(f"Letra {letter} no está asociada a ningún LED.")

# Configuración del socket para recibir conexiones
s = socket.socket()                # Crea un socket TCP/IP
s.bind(('0.0.0.0', 1234))          # Asocia el socket a todas las interfaces y al puerto 1234
s.listen(1)                        # El servidor escucha a una conexión entrante
print("Esperando conexiones...")   # Mensaje indicando que está esperando una conexión

conn, addr = s.accept()            # Acepta la conexión entrante
print("Conexión aceptada de:", addr)  # Imprime la dirección del cliente que se conectó

try:
    while True:
        # Recibe datos del cliente
        data = conn.recv(1024)  # Recibe hasta 1024 bytes de datos
        if not data:
            break  # Si no se reciben más datos, sale del bucle
        letra = data.decode('utf-8').strip()  # Decodifica el mensaje y elimina espacios en blanco
        print("Letra recibida:", letra)  # Imprime la letra recibida
        toggle_led(letra)  # Llama a la función para controlar el LED correspondiente
finally:
    conn.close()  # Cierra la conexión al finalizar
    