from machine import Pin
import time

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

# Simulación de entrada por teclado
while True:
    letter = input("Ingresa una letra (Z, X, C, V, B, N, M): ").upper()
    if letter in led_pins:
        toggle_led(letter)
    else:
        print("Letra no válida, intenta nuevamente.")
    time.sleep(0.1)
