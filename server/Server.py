import socket
import threading
import secrets
import string
import os
import serial
import time

# Twilio Connection Extensions
from twilio.rest import Client

# Para envio de email.
import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

# Clase para manejo de usuario para recuperación de contraseña
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
    def __init__(self, host="0.0.0.0", port=3535): #192.168.18.206
        self.matriz_Alquilador = [] 
        self.matriz_Propietario = []
        self.matriz_AmbasFunciones = [] 
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind((host, port))
        self.server_socket.listen(5)
        self.clients = []
        self.arduino = None
        self.agregar_usuario_a_matriz()

        # Conectarse con Arduino
        serialPort = 'COM3'  # Cambia 'COM3' al puerto correcto
        try:
            self.arduino = serial.Serial(serialPort, 600)
            time.sleep(2)  # Espera a que el puerto serial se inicie
            print('Conectado al Arduino en', serialPort)
        except serial.SerialException as e:
            print(f'Error al abrir puerto serial: {e}')

        # Hilo para manejar el servidor
        self.thread = threading.Thread(target=self.accept_connections)
        self.thread.start()

        # Hilo para leer mensajes de Arduino
        self.arduino_thread = threading.Thread(target=self.leerMensajeArduino)
        self.arduino_thread.start()

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
                    print("Llegó: " + message)

                    # Verificar el prefijo del mensaje
                    if message.startswith("CrearCuenta_"):  # Nuevo usuario
                        self.write_message_to_file(message[len("CrearCuenta_"):])

                    elif message.startswith("InformacionDeUsuario_"):
                        self.guardarInforDeUsuario(message[len("InformacionDeUsuario_"):])

                    elif message.startswith("ObtenerInformacionUsuario_"):
                        info = message[len("ObtenerInformacionUsuario_"):].split("_")
                        self.leerInformacionDeUsuario(info[0], info[1])

                    elif message.startswith("InformacionDeVivienda_"):
                        self.guardarInforDeVivienda(message[len("InformacionDeVivienda_"):])

                    elif message.startswith("ObtenerInformacionVivienda_"):
                        self.leerInformacionDeVivienda(message[len("ObtenerInformacionVivienda_"):], client_socket)

                    elif message.startswith("Login_"):
                        self.buscar_login(message[len("Login_"):], client_socket)

                    elif message.startswith("WhatsApp/"):
                        data = message.split("/")
                        self.WhatsAppMessage(data[1], data[2])

                    elif message.startswith("Recuperacion_"):
                        self.existe_correo(message[len("Recuperacion_"):], client_socket)

                    elif message.startswith("SERVO_"):  # Enviar comando al Arduino
                        self.envioMensajeArduino(message)
                    
                    elif message.startswith("LETRA_"):  # Enviar comando al Arduino
                        self.envioMensajeArduino(message)

                    else:
                        print("No llegó mensaje relevante")
                        
            except Exception as e:
                print("Error:", e)
                break
        client_socket.close()
        self.clients.remove(client_socket)


    def broadcast(self, message, sender_socket):
        try:
            sender_socket.send(message.encode('utf-8'))
        except:
            sender_socket.close()
            self.clients.remove(sender_socket)

    def envioMensajeArduino(self, message):
        """Enviar un mensaje al Arduino para mover el servo u otra acción."""
        if self.arduino is not None:
            try:
                self.arduino.write(message.encode("utf-8"))
            except serial.SerialException as e:
                print(f"Error al enviar mensaje al Arduino: {e}")

    def leerMensajeArduino(self):
        """Leer mensajes entrantes del Arduino y enviarlos a los clientes conectados."""
        while True:
            if self.arduino is not None:
                try:
                    if self.arduino.in_waiting > 0:
                        ino_message = self.arduino.readline().decode('utf-8').strip()
                        #print("Arduino dice:", ino_message)
                        for socket in self.clients:
                            self.send_message_to_respond_request(socket, ino_message)
                except serial.SerialException as e:
                    print(f"Error al leer mensaje del Arduino: {e}")
                    break
            else:
                print("Arduino no está conectado.")
                break

    def send_message_to_respond_request(self, client_socket, message):
        """Enviar una respuesta al cliente con el mensaje recibido del Arduino."""
        threading.Thread(target=self.broadcast, args=(message + "\n", client_socket)).start()

    def write_message_to_file(self, message):
        """Escribir un mensaje en el archivo .txt indicado"""
        if message.startswith("Propietario_"):
            with open("server/Usuarios/Propietario.txt", "a") as file:
                file.write(message[len("Propietario_"):] + "\n")

        elif message.startswith("Alquilador_"):
            with open("server/Usuarios/Alquilador.txt", "a") as file:
                file.write(message[len("Alquilador_"):] + "\n")

        elif message.startswith("AmbasFunciones_"):
            with open("server/Usuarios/AmbasFunciones.txt", "a") as file:
                file.write(message[len("AmbasFunciones_"):] + "\n")



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
            with open("server/Usuarios/Alquilador.txt", "r") as file:  # Abrir el archivo en modo lectura
                for line in file:
                    mensaje = line.strip()  # Limpiar espacios en blanco
                    usuario_fila = self.procesar_usuario(mensaje, "Alquilador")  # Procesar la línea
                    self.matriz_Alquilador.append(usuario_fila)  # Añadir el usuario a la matriz
            
            with open("server/Usuarios/Propietario.txt", "r") as file:  # Abrir el archivo en modo lectura
                for line in file:
                    mensaje = line.strip()  # Limpiar espacios en blanco
                    usuario_fila = self.procesar_usuario(mensaje, "Propietario")  # Procesar la línea
                    self.matriz_Propietario.append(usuario_fila)  # Añadir el usuario a la matriz

            with open("server/Usuarios/AmbasFunciones.txt", "r") as file:  # Abrir el archivo en modo lectura
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
            with open("server/Usuarios/Propietario.txt", "w") as file:  # Abrir en modo append
                for fila in mat:
                    message = "_".join(fila)  # Unir elementos de la fila con "_"
                    file.write(message + "\n")
        elif Tipo == "Alquilador":
            mat = self.matriz_Alquilador
            with open("server/Usuarios/Alquilador.txt", "w") as file:  # Abrir en modo append
                for fila in mat:
                    message = "_".join(fila)  # Unir elementos de la fila con "_"
                    file.write(message + "\n")

        elif Tipo == "AmbasFunciones":
            mat = self.matriz_AmbasFunciones
            with open("server/Usuarios/AmbasFunciones.txt", "w") as file:  # Abrir en modo append
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

    #De aquí en adelante estará la lógica para guardar y acceder a InformacionDeUsuarios
    def guardarInforDeUsuario(self, info_usuario,):
        # Buscar el nombre de usuario en la cadena de entrada
        username = None
        ruta='server/InformacionDeUsuarios/'
        for linea in info_usuario.splitlines():
            if linea.startswith("Username:"):
                username = linea.split(":", 1)[1].strip()
                break
            
        # Crear la ruta si no existe
        os.makedirs(ruta, exist_ok=True)
        
        # Definir el nombre del archivo usando el username extraído
        nombre_archivo = f"{username}.txt"
        ruta_archivo = os.path.join(ruta, nombre_archivo)
        
        # Guardar la información en el archivo
        with open(ruta_archivo, 'w') as archivo:
            archivo.write(info_usuario)

    def leerInformacionDeUsuario(self, informacionRequerida, userName):
        ruta_archivo = f'server/InformacionDeUsuarios/{userName}.txt'
        
        # Verificar si el archivo existe
        if not os.path.exists(ruta_archivo):
            print(f"El archivo {ruta_archivo} no existe.")
            return None
        
        # Leer el archivo y buscar la información requerida
        with open(ruta_archivo, 'r') as archivo:
            for linea in archivo:
                # Verificar si la línea contiene la información requerida
                if linea.startswith(f"{informacionRequerida}:"):
                    return self.send_message_to_respond_request(linea.split(":", 1)[1].strip())
        
        # Si no se encuentra la información requerida, retornar None
        print(f"No se encontró '{informacionRequerida}' en {ruta_archivo}.")
        return None

    #Aquí estarán las funciones para guardar y acceder a InformacionDeViviendas
    def guardarInforDeVivienda(self, info_usuario):
        print("AQUIIIIIIII")
        print(info_usuario)
        # Buscar el nombre de usuario en la cadena de entrada
        username = None
        ruta='server/InformacionDeVivienda/'
        for linea in info_usuario.splitlines():
            if linea.startswith("NombreDeVivienda:"):
                username = linea.split(":", 1)[1].strip()
                break
        print(info_usuario)
        # Crear la ruta si no existe
        os.makedirs(ruta, exist_ok=True)
        
        # Definir el nombre del archivo usando el username extraído
        nombre_archivo = f"{username}.txt"
        
        # Guardar la información en el archivo
        self.guardar_texto(ruta + nombre_archivo, info_usuario)

    def guardar_texto(self, direccion, texto):
        with open(direccion, 'w') as archivo:
            archivo.write(texto)
    def leerInformacionDeVivienda(self, nombreVivienda, socket):
        ruta_archivo = f'server/InformacionDeVivienda/{nombreVivienda}.txt'
        
        # Verificar si el archivo existe
        if not os.path.exists(ruta_archivo):
            print(f"El archivo {ruta_archivo} no existe.")
            return None

        resultados = []  # Inicializa la cadena que contendrá toda la información unida

        with open(ruta_archivo, 'r') as archivo:
            for linea in archivo:
                resultados.append(linea)
        
        # Unir todos los resultados encontrados con guiones bajos "_"
        if resultados:
            resultado_final = "_".join(resultados)
            return self.send_message_to_respond_request(resultado_final, socket)  # Devuelve la cadena unida

        # Si no se encuentra la información requerida, retornar None
        print(f"No se encontró información de {ruta_archivo}.")
        return None
    
    # Function to send WhatsApp Messages
    def WhatsAppMessage(self, phoneNumber, message):        

        account_sid =  '[ Twilio Account Sid ]'

        auth_token = '[ Token of Authorization ]'

        #:D

        client = Client(account_sid, auth_token)

        message_body = message
        message_from = 'whatsapp:+14155238886'
        message_to = f'whatsapp:{phoneNumber}' 

        whastapp_message = client.messages.create(
            body=message_body,
            from_=message_from,
            to=message_to
        )

        print(whastapp_message.sid)

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