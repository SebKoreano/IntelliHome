import serial
import time

# Configura el puerto serial (asegúrate de que sea el correcto para tu Arduino)
arduino_port = 'COM8'
baud_rate = 9600

# Inicia la comunicación serial
ser = serial.Serial(arduino_port, baud_rate)
time.sleep(2)  # Esperar un momento para que el puerto esté listo

try:
    while True:
        # Enviar un mensaje al Arduino
        message_to_send = "Mensaje desde Python"
        ser.write((message_to_send + '\n').encode('utf-8'))
        print("Python envió:", message_to_send)
        
        # Esperar y leer la respuesta del Arduino
        if ser.in_waiting > 0:
            response = ser.readline().decode('utf-8').strip()
            print("Python recibió:", response)
        
        time.sleep(2)  # Esperar antes de enviar otro mensaje

except KeyboardInterrupt:
    print("Cerrando comunicación.")
finally:
    ser.close()  # Cerrar el puerto serial al finalizar
