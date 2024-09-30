import socket
import threading
import tkinter as tk
from tkinter import scrolledtext

class ChatServer:
    def __init__(self, host='0.0.0.0', port=1717):
        
        self.matriz_usuarios = [] 
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind((host, port))
        self.server_socket.listen(5)
        self.clients = []

        # Configuración de la interfaz gráfica
        self.root = tk.Tk()
        self.root.title("Servidor de Chat")

        self.chat_display = scrolledtext.ScrolledText(self.root, state='disabled', width=50, height=20)
        self.chat_display.pack(pady=10)

        self.message_entry = tk.Entry(self.root, width=40)
        self.message_entry.pack(pady=5)

        self.send_button = tk.Button(self.root, text="Enviar")
        self.send_button.pack(pady=5)

        self.quit_button = tk.Button(self.root, text="Salir", command=self.close_server)
        self.quit_button.pack(pady=5)

        # Hilo para manejar el servidor
        self.thread = threading.Thread(target=self.accept_connections)
        self.thread.start()

        self.root.protocol("WM_DELETE_WINDOW", self.close_server)
        self.root.mainloop()

    def accept_connections(self):
        while True:
            client_socket, addr = self.server_socket.accept()
            self.clients.append(client_socket)
            self.chat_display.config(state='normal')
            self.chat_display.insert(tk.END, f"Conexión de {addr}\n")
            self.chat_display.config(state='disabled')
            threading.Thread(target=self.handle_client, args=(client_socket,)).start()

    def handle_client(self, client_socket):
        while True:
            try:
                message = client_socket.recv(1024).decode('utf-8')
                if message:
                    message = message.strip()

                    self.chat_display.config(state='normal')
                    self.chat_display.insert(tk.END, f"Mensaje recibido: {repr(message)}\n")  
                    self.chat_display.config(state='disabled')

                    # Verificar el prefijo del mensaje
                    if message.startswith("CrearCuenta_"):  # Nuevo usuario
                        self.chat_display.insert(tk.END, "Procesando nueva cuenta...\n")
                        self.write_message_to_file(message[len("CrearCuenta_"):])

                    elif message.startswith("Login_"):
                        self.chat_display.insert(tk.END, "Procesando login...\n")
                        self.buscar_login(message[len("Login_"):], client_socket)

                    elif message.startswith("Recuperación_"):
                        self.chat_display.insert(tk.END, "Procesando recuperación...\n")
                        self.handle_recuperacion(message[len("Recuperación_"):])

                    else:
                        self.chat_display.insert(tk.END, "No sirvió, pero llegó\n")
                        
                else:
                    self.chat_display.insert(tk.END, "No sirvió Lol, nisiquiera llegó\n")
            except Exception as e:
                self.chat_display.insert(tk.END, f"Error: {e}\n")
                break
        client_socket.close()
        self.clients.remove(client_socket)

    def broadcast(self, message, sender_socket):
        self.chat_display.config(state='normal')
        self.chat_display.insert(tk.END, f"Cliente: {message}\n")
        self.chat_display.config(state='disabled')

        try:
            sender_socket.send(message.encode('utf-8'))
        except:
            sender_socket.close()
            self.clients.remove(sender_socket)

    def send_message_to_respond_request(self, client_socket, message):
        """Enviar una respuesta al cliente con el estado del inicio de sesión."""
        threading.Thread(target=self.broadcast, args=(message +"\n", client_socket)).start()


     # Enviar al cliente que hizo la solicitud


    def write_message_to_file(self, message):
        """Escribir un mensaje en el archivo usuarios.txt."""
        with open("usuarios.txt", "a") as file:  # Abrir en modo append
            file.write(message + "\n") 


    def procesar_usuario(self, mensaje):
        # Dividimos el mensaje en partes usando el delimitador '_'
        datos_usuario = mensaje.split('_')
        
        # Asignamos cada parte a una variable sin omitir letras
        nombre = datos_usuario[0]
        apellido = datos_usuario[1]
        usuario = datos_usuario[2]  # No se omite el "USER"
        telefono = datos_usuario[3]  # No se omite el "TEL"
        email = datos_usuario[4]     # No se omite el "MAIL"
        contrasena = datos_usuario[5]  # Contraseña
        confirmar_contrasena = datos_usuario[6]  # Confirmación de contraseña
        codigo_recuperacion = datos_usuario[7] 

        # Creamos una fila con la información del usuario
        usuario_fila = [nombre, apellido, usuario, telefono, email, contrasena, confirmar_contrasena, codigo_recuperacion]

        # Insertar usuario_fila en el Text widget
        print(usuario_fila)

        return usuario_fila
    
    def revisar_contraseña(self, i, contra, client_socket): 
        """Verifica si la contraseña proporcionada coincide con la almacenada."""
        mat = self.matriz_usuarios
        if mat[i][5] == contra:  # Verificar la contraseña
            return self.send_message_to_respond_request(client_socket, "Se ingresó con éxito")
        return self.send_message_to_respond_request(client_socket, "Fallo en contraseña")

    def agregar_usuario_a_matriz(self):
        """Leer usuarios del archivo y agregarlos a la matriz."""
        try:
            with open("usuarios.txt", "r") as file:  # Abrir el archivo en modo lectura
                for line in file:
                    mensaje = line.strip()  # Limpiar espacios en blanco
                    usuario_fila = self.procesar_usuario(mensaje)  # Procesar la línea
                    self.matriz_usuarios.append(usuario_fila)  # Añadir el usuario a la matriz
        except FileNotFoundError:
            print("El archivo usuarios.txt no se encontró.")
        except Exception as e:
            print(f"Ocurrió un error al leer el archivo: {e}")

    def buscar_login(self, mensaje, client_socket): 
        """Buscar el usuario en la matriz y verificar la contraseña."""
        self.agregar_usuario_a_matriz()
        mat = self.matriz_usuarios
        datos_login = mensaje.split("_")

        indicador = datos_login[0]  # Usuario, correo o teléfono
        contra = datos_login[1]      # Contraseña

        for i in range(len(mat)):  # Usar range(len(mat)) para revisar todas las filas
            if mat[i][2] == indicador:  # Verificar nombre de usuario
                return self.revisar_contraseña(i, contra, client_socket)  # Revisar contraseña
        
        for i in range(len(mat)):
            if mat[i][3] == indicador:  # Verificar teléfono
                return self.revisar_contraseña(i, contra, client_socket)  # Revisar contraseña
            
        for i in range(len(mat)):
            if mat[i][4] == indicador:  # Verificar correo
                return self.revisar_contraseña(i, contra, client_socket)  # Revisar contraseña
        
        return self.send_message_to_respond_request(client_socket, "Fallo en usuario")
   
    def close_server(self):
        for client in self.clients:
            client.close()
        self.server_socket.close()
        self.root.destroy()

if __name__ == "__main__":
    ChatServer()


#Flujo para Login: Se analiza que mensaje inicia con Login en handle_client, de ahí se va a buscar_login, donde crea 
#la matriz con base en los datos de usuario.txt, de ahí, si se encontró parámetro de usuario, se dirige a revisar_contraseña
#Que tiene contraseña e indice de ubicación de usuario indicado. Por último revisar_contraseña verifica contraseña correcta.
#Se envia mensaje.
