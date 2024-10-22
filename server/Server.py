import socket
import threading
import tkinter as tk
from tkinter import scrolledtext
import secrets
import string

#Para envio de email.
import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

#Clase para manejo de usuario para recuperación de contraseña
class Usuario:
    def send_password_reset_email(self, recipient_email, new_password):
        sender_email = 'intellihome.non.response@gmail.com'
        sender_password = 'dqzm rmmn hyjp ilqj'

        msg = MIMEMultipart()
        msg['From'] = sender_email
        msg['To'] = recipient_email
        msg['Subject'] = 'Recuperación de contraseña'

        message_body = f'Su nueva contraseña es: {new_password}'
        msg.attach(MIMEText(message_body, 'plain'))

        try:
            server = smtplib.SMTP('smtp.gmail.com', 587)
            server.starttls()
            server.login(sender_email, sender_password)
            server.sendmail(sender_email, recipient_email, msg.as_string())
            print(f'Correo enviado con éxito a {recipient_email}')
        except Exception as e:
            print(f'Error al enviar el correo: {e}')  # Imprimir el error
        finally:
            server.quit()

class ChatServer:
    def __init__(self, host="172.18.83.115", port=3535): #192.168.18.206
        self.matriz_Alquilador = [] 
        self.matriz_Propietario = []
        self.matriz_AmbasFunciones = [] 

        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind((host, port))
        self.server_socket.listen(5)
        self.clients = []
        self.agregar_usuario_a_matriz()


        # Hilo para manejar el servidor
        self.thread = threading.Thread(target=self.accept_connections)
        self.thread.start()
    def accept_connections(self):
        while True:
            client_socket, addr = self.server_socket.accept()
            self.clients.append(client_socket)
            threading.Thread(target=self.handle_client, args=(client_socket,)).start()

    def handle_client(self, client_socket):
        while True:
            try:
                message = client_socket.recv(1024).decode('utf-8')
                if message:
                    message = message.strip()

                    print("Llegó: "+message)

                    # Verificar el prefijo del mensaje
                    if message.startswith("CrearCuenta_"):  # Nuevo usuario
                        print(message + "SE CREARÁ ESTA CUENTAAAAAAA")
                        self.write_message_to_file(message[len("CrearCuenta_"):])

                    elif message.startswith("Login_"):
                        print(message)
                        self.buscar_login(message[len("Login_"):], client_socket)

                    elif message.startswith("Recuperacion_"):
                        print(message)
                        self.existe_correo(message[len("Recuperacion_"):], client_socket)
                        #self.recuperar_contraseña(self,"sebasboza.cr@gmail.com",None) 
                    else:
                        print("No llegó mensaje relevante")
            except Exception as e:
                print("error")
                break
        client_socket.close()
        self.clients.remove(client_socket)

    def broadcast(self, message, sender_socket):
        try:
            sender_socket.send(message.encode('utf-8'))
        except:
            sender_socket.close()
            self.clients.remove(sender_socket)

    #Envia mensaje a socket 
    def send_message_to_respond_request(self, client_socket, message):
        """Enviar una respuesta al cliente con el estado del inicio de sesión."""
        threading.Thread(target=self.broadcast, args=(message +"\n", client_socket)).start()
     # Enviar al cliente que hizo la solicitud

    #Escriba cuenta creada en .txt
    def write_message_to_file(self, message):
        """Escribir un mensaje en el archivo .txt indicado"""
        if message.startswith("Propietario_"):
            print(message)
            with open("server/Propietario.txt", "a") as file:  # Abrir en modo append
                file.write((message[len("Propietario_"):]) + "\n") 
                

        elif message.startswith("Alquilador_"):
            print(message)
            with open("server/Alquilador.txt", "a") as file:  # Abrir en modo append
                file.write((message[len("Alquilador_"):]) + "\n") 


        elif message.startswith("AmbasFunciones_"):
            print(message)
            with open("server/AmbasFunciones.txt", "a") as file:  # Abrir en modo append
                file.write((message[len("AmbasFunciones_"):]) + "\n") 


    def procesar_usuario(self, mensaje, Tipo):
        # Dividimos el mensaje en partes usando el delimitador '_'
        datos_usuario = mensaje.split('_')

        usuario = datos_usuario[0]  
        telefono = datos_usuario[1] 
        email = datos_usuario[2]    
        contrasena = datos_usuario[3]

        if Tipo == "Propietario":
            Iban = datos_usuario[4]
            usuario_fila = [usuario, telefono, email, contrasena, Iban]


        elif Tipo == "Alquilador":
            CardNum = datos_usuario[4]
            CardCVV = datos_usuario[5]
            CardHolder = datos_usuario[6]
            usuario_fila = [usuario, telefono, email, contrasena, CardNum, CardCVV, CardHolder]

        elif Tipo == "AmbasFunciones":
            Iban = datos_usuario[4]
            CardNum = datos_usuario[5]
            CardCVV = datos_usuario[6]
            CardHolder = datos_usuario[7]
            usuario_fila = [usuario, telefono, email, contrasena, Iban, CardNum, CardCVV, CardHolder]


        print(usuario_fila)

        return usuario_fila
    
    def revisar_contraseña(self, i, contra, client_socket, Tipo): 
        """Verifica si la contraseña proporcionada coincide con la almacenada."""
        if Tipo == "Propietario":
            mat = self.matriz_Propietario
            if mat[i][3] == contra:  # Verificar la contraseña
                DatosPorEnviar = mat[i][0] + "_Propietario"
                return self.send_message_to_respond_request(client_socket, DatosPorEnviar)
        elif Tipo == "Alquilador":
            mat = self.matriz_Alquilador
            if mat[i][3] == contra:  # Verificar la contraseña
                DatosPorEnviar = mat[i][0] + "_Alquilador"
                return self.send_message_to_respond_request(client_socket, DatosPorEnviar)

        elif Tipo == "AmbasFunciones":
            mat = self.matriz_AmbasFunciones
            if mat[i][3] == contra:  # Verificar la contraseña
                DatosPorEnviar = mat[i][0] + "_AmbasFunciones"
                return self.send_message_to_respond_request(client_socket, DatosPorEnviar)
        

        return self.send_message_to_respond_request(client_socket, "Fallo en contraseña")


    def agregar_usuario_a_matriz(self):
        """Leer usuarios del archivo y agregarlos a la matriz."""
        try:
            with open("server/Alquilador.txt", "r") as file:  # Abrir el archivo en modo lectura
                for line in file:
                    mensaje = line.strip()  # Limpiar espacios en blanco
                    usuario_fila = self.procesar_usuario(mensaje, "Alquilador")  # Procesar la línea
                    self.matriz_Alquilador.append(usuario_fila)  # Añadir el usuario a la matriz
            
            with open("server/Propietario.txt", "r") as file:  # Abrir el archivo en modo lectura
                for line in file:
                    mensaje = line.strip()  # Limpiar espacios en blanco
                    usuario_fila = self.procesar_usuario(mensaje, "Propietario")  # Procesar la línea
                    self.matriz_Propietario.append(usuario_fila)  # Añadir el usuario a la matriz

            with open("server/AmbasFunciones.txt", "r") as file:  # Abrir el archivo en modo lectura
                for line in file:
                    mensaje = line.strip()  # Limpiar espacios en blanco
                    usuario_fila = self.procesar_usuario(mensaje, "AmbasFunciones")  # Procesar la línea
                    self.matriz_AmbasFunciones.append(usuario_fila)  # Añadir el usuario a la matriz         


        except FileNotFoundError:
            print("El archivo no se encontró.")
        except Exception as e:
            print(f"Ocurrió un error al leer el archivo: {e}")

    def buscar_login(self, mensaje, client_socket): 
        """Buscar el usuario en la matriz y verificar la contraseña."""
        #Revisa matriz de Propietario
        mat = self.matriz_Propietario
        datos_login = mensaje.split("_")

        indicador = datos_login[0]  # Usuario, correo o teléfono
        contra = datos_login[1]      # Contraseña

        for i in range(len(mat)):  # Usar range(len(mat)) para revisar todas las filas
            if mat[i][0] == indicador:  # Verificar nombre de usuario
                return self.revisar_contraseña(i, contra, client_socket, "Propietario")  # Revisar contraseña
        
        for i in range(len(mat)):
            if mat[i][1] == indicador:  # Verificar teléfono
                return self.revisar_contraseña(i, contra, client_socket, "Propietario")  # Revisar contraseña
            
        for i in range(len(mat)):
            if mat[i][2] == indicador:  # Verificar correo
                return self.revisar_contraseña(i, contra, client_socket, "Propietario")  # Revisar contraseña

        #Revisa matriz de Alquilador
        mat = self.matriz_Alquilador
        datos_login = mensaje.split("_")

        indicador = datos_login[0]  # Usuario, correo o teléfono
        contra = datos_login[1]      # Contraseña

        for i in range(len(mat)):  # Usar range(len(mat)) para revisar todas las filas
            if mat[i][0] == indicador:  # Verificar nombre de usuario
                return self.revisar_contraseña(i, contra, client_socket, "Alquilador")  # Revisar contraseña
        
        for i in range(len(mat)):
            if mat[i][1] == indicador:  # Verificar teléfono
                return self.revisar_contraseña(i, contra, client_socket, "Alquilador")  # Revisar contraseña
            
        for i in range(len(mat)):
            if mat[i][2] == indicador:  # Verificar correo
                return self.revisar_contraseña(i, contra, client_socket, "Alquilador")  # Revisar contraseña

        #Revisa matriz de AmbasFunciones
        mat = self.matriz_AmbasFunciones
        datos_login = mensaje.split("_")

        indicador = datos_login[0]  # Usuario, correo o teléfono
        contra = datos_login[1]      # Contraseña

        for i in range(len(mat)):  # Usar range(len(mat)) para revisar todas las filas
            if mat[i][0] == indicador:  # Verificar nombre de usuario
                return self.revisar_contraseña(i, contra, client_socket, "AmbasFunciones")  # Revisar contraseña
        
        for i in range(len(mat)):
            if mat[i][1] == indicador:  # Verificar teléfono
                return self.revisar_contraseña(i, contra, client_socket, "AmbasFunciones")  # Revisar contraseña
            
        for i in range(len(mat)):
            if mat[i][2] == indicador:  # Verificar correo
                return self.revisar_contraseña(i, contra, client_socket, "AmbasFunciones")  # Revisar contraseña
        
        return self.send_message_to_respond_request(client_socket, "Fallo en usuario")


    def close_server(self):
        for client in self.clients:
            client.close()
        self.server_socket.close()
        self.root.destroy()

    def existe_correo(self, string, client_socket):
        mat = self.matriz_Propietario
        for i in range(len(mat)):
            if mat[i][2] == string:  # Verificar correo
                print("CORREO EXISTE EN MATRIZ PROPIETARIO")
                return self.recuperar_contraseña(string, "Propietario")
            
        mat = self.matriz_Alquilador
        for i in range(len(mat)):
            if mat[i][2] == string:  # Verificar correo
                print("CORREO EXISTE EN MATRIZ ALQUILADOR")
                return self.recuperar_contraseña(string, "Alquilador")
            
        mat = self.matriz_AmbasFunciones
        for i in range(len(mat)):
            if mat[i][2] == string:  # Verificar correo
                print("CORREO EXISTE EN MATRIZ AMBAS FUNCIONES")
                return self.recuperar_contraseña(string, "AmbasFunciones")
        
        print("NO SE ENCONTRÓ CORREO")
        return self.send_message_to_respond_request(client_socket, "Error, no se encontró el correo")

    def cambiar_Contraseña(self, correo, password, Tipo): 
        """Cambia la contraseña de un usuario en la matriz y actualiza el archivo."""

        if Tipo == "Propietario":
            mat = self.matriz_Propietario
            for i in range(len(mat)):
                if mat[i][2] == correo:  # Verificar correo
                    mat[i][3] = password  # Cambiar la contraseña
                    break
            self.CambiosATxt("Propietario")
        
        elif Tipo == "Alquilador":
            mat = self.matriz_Alquilador
            for i in range(len(mat)):
                if mat[i][2] == correo:  # Verificar correo
                    mat[i][3] = password  # Cambiar la contraseña
                    break
            self.CambiosATxt("Alquilador")
        
        elif Tipo == "AmbasFunciones":
            mat = self.matriz_AmbasFunciones
            for i in range(len(mat)):
                if mat[i][2] == correo:  # Verificar correo
                    mat[i][3] = password  # Cambiar la contraseña
                    break
            self.CambiosATxt("AmbasFunciones")

    
    def CambiosATxt(self, Tipo):
        if Tipo == "Propietario":
            mat = self.matriz_Propietario
            with open("server/Propietario.txt", "w") as file:  # Abrir en modo append
                for fila in mat:
                    message = "_".join(fila)  # Unir elementos de la fila con "_"
                    file.write(message + "\n")
        elif Tipo == "Alquilador":
            mat = self.matriz_Alquilador
            with open("server/Alquilador.txt", "w") as file:  # Abrir en modo append
                for fila in mat:
                    message = "_".join(fila)  # Unir elementos de la fila con "_"
                    file.write(message + "\n")

        elif Tipo == "AmbasFunciones":
            mat = self.matriz_AmbasFunciones
            with open("server/AmbasFunciones.txt", "w") as file:  # Abrir en modo append
                for fila in mat:
                    message = "_".join(fila)  # Unir elementos de la fila con "_"
                    file.write(message + "\n")

    def recuperar_contraseña(self, correo, Tipo):
        # Crear una instancia de Usuario y probar el envío
        usuario = Usuario()
        new_pass = generar_nueva_contraseña()  # Asegúrate de que esta función esté definida correctamente
        print(f"Nueva contraseña: {new_pass}")  # Imprimir la nueva contraseña
        usuario.send_password_reset_email(correo, new_pass)  # Enviar el correo
        self.cambiar_Contraseña(correo, new_pass, Tipo)  # Cambiar la contraseña en la matriz

def generar_nueva_contraseña():
    print("entro")
    while True:
        # Asegurar que se incluye al menos un carácter de cada tipo
        print("entro")
        password = [
            secrets.choice(letrasMayusculas),
            secrets.choice(letrasMinusculas),
            secrets.choice(digitos),
            secrets.choice(caracteresEspeciales)
        ]

        # Rellenar el resto de la contraseña con caracteres aleatorios del alfabeto completo
        alfabeto = letrasMayusculas + letrasMinusculas + digitos + caracteresEspeciales
        password += [secrets.choice(alfabeto) for _ in range(longitudContraseña - 4)]
        
        # Mezclar la contraseña para que no siempre aparezcan los caracteres especiales, números, etc. al principio
        secrets.SystemRandom().shuffle(password)
        
        # Convertir la lista en una cadena
        contraseña = ''.join(password)
        print(f"Contraseña generada: {contraseña}")  # Imprimir la contraseña generada

        # Verificar que la contraseña cumple con todos los requisitos
        if (any(char in letrasMayusculas for char in contraseña) and
            any(char in letrasMinusculas for char in contraseña) and
            any(char in digitos for char in contraseña) and
            any(char in caracteresEspeciales for char in contraseña)):
            return contraseña

# Definir parametros para la contraseña random
letrasMayusculas = string.ascii_uppercase  
letrasMinusculas = string.ascii_lowercase  
digitos = string.digits  
caracteresEspeciales = string.punctuation  
longitudContraseña = 8

if __name__ == "__main__":
    ChatServer()


#Flujo para Login: Se analiza que mensaje inicia con Login en handle_client, de ahí se va a buscar_login, donde crea 
#la matriz con base en los datos de usuario.txt, de ahí, si se encontró parámetro de usuario, se dirige a revisar_contraseña
#Que tiene contraseña e indice de ubicación de usuario indicado. Por último revisar_contraseña verifica contraseña correcta.
#Se envia mensaje.
