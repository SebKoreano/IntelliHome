import socket
import threading

# Dirección IP y puerto de la Raspberry Pi Pico
ip_pico = "192.168.0.117"  # Cambia esta IP si es necesario (es la IP de la Raspberry Pi Pico en la red)
puerto_pico = 1234         # Puerto en la Raspberry Pi Pico que está escuchando las conexiones

class ChatServer:
    """
    Esta clase implementa un servidor de chat que se comunica con clientes y
    también con la Raspberry Pi Pico para el control de LEDs.
    """
    def __init__(self, host='0.0.0.0', port=6969):
        # Configura el servidor para escuchar conexiones entrantes en la IP y puerto especificados
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  # Crea un socket TCP/IP
        self.server_socket.bind((host, port))  # Asocia el socket a todas las interfaces y el puerto 6969
        self.server_socket.listen(5)           # Permite hasta 5 conexiones pendientes
        self.clients = []                      # Lista para almacenar los clientes conectados
        print(f"Servidor iniciado en {host}:{port}")  # Mensaje para indicar que el servidor está activo

        # Conectar al socket de la Raspberry Pi Pico
        self.pico_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  # Crea un socket TCP/IP para la Pico
        self.pico_socket.connect((ip_pico, puerto_pico))  # Conecta al socket de la Pico en la IP y puerto especificados

    def accept_connections(self):
        """
        Método principal del servidor que acepta nuevas conexiones de clientes
        y crea un nuevo hilo para manejar cada cliente conectado.
        """
        while True:
            client_socket, addr = self.server_socket.accept()  # Acepta una nueva conexión de cliente
            print(f"Conexión establecida desde {addr}")        # Imprime la dirección del cliente que se conectó
            self.clients.append(client_socket)                 # Agrega el socket del cliente a la lista de clientes
            # Crea y comienza un nuevo hilo para manejar la comunicación con este cliente
            threading.Thread(target=self.handle_client, args=(client_socket,)).start()

    def handle_client(self, client_socket):
        """
        Este método maneja la comunicación con un cliente específico. Se ejecuta
        en un hilo separado para cada cliente conectado.
        """
        while True:
            try:
                message = client_socket.recv(1024).decode('utf-8')  # Recibe un mensaje del cliente (hasta 1024 bytes)
                if message:
                    print(f"Mensaje recibido: {message}")  # Imprime el mensaje recibido desde el cliente
                    self.broadcast(message, client_socket)  # Envía el mensaje a todos los demás clientes conectados
                    self.send_to_pico(message)  # Envía el mensaje a la Raspberry Pi Pico para el control de LEDs
                else:
                    break  # Si no se recibe ningún mensaje (el cliente cerró la conexión), sale del bucle
            except Exception as e:
                print(f"Error al recibir el mensaje: {e}")  # Captura cualquier error al recibir el mensaje
                break  # Rompe el bucle en caso de error
        client_socket.close()  # Cierra el socket del cliente
        self.clients.remove(client_socket)  # Remueve el cliente de la lista de clientes conectados
        print("Cliente desconectado.")  # Mensaje indicando que un cliente se ha desconectado

    def send_to_pico(self, message):
        """
        Este método envía el mensaje recibido de un cliente a la Raspberry Pi Pico.
        """
        try:
            self.pico_socket.send(message.encode('utf-8'))  # Envía el mensaje a la Pico en formato UTF-8
            print(f"Mensaje enviado a la Pico: {message}")  # Imprime confirmación de envío a la Pico
        except Exception as e:
            print(f"Error al enviar mensaje a la Pico: {e}")  # Captura cualquier error al intentar enviar a la Pico

    def broadcast(self, message, sender_socket):
        """
        Envía el mensaje a todos los clientes conectados excepto al cliente que lo envió.
        """
        for client in self.clients:
            if client != sender_socket:  # Evita enviar el mensaje de vuelta al remitente
                try:
                    print(f"Enviando: {message} a un cliente")  # Imprime un mensaje antes de enviarlo a un cliente
                    client.send(message.encode('utf-8'))  # Envía el mensaje al cliente
                except Exception as e:
                    print(f"Error al enviar mensaje a un cliente: {e}")  # Captura errores al enviar mensajes
                    client.close()  # Cierra el socket del cliente si ocurre un error
                    self.clients.remove(client)  # Remueve el cliente de la lista si falla

if __name__ == "__main__":
    # Inicia el servidor de chat cuando se ejecuta el script
    server = ChatServer()  # Crea una instancia del servidor de chat
    server.accept_connections()  # Comienza a aceptar conexiones de clientes
