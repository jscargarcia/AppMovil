#include <SPI.h>
#include <MFRC522.h>
#include <Adafruit_NeoPixel.h>
#include <Servo.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

// ===== WIFI =====
const char* ssid = "dulan";
const char* password = "dulan12345";

// ===== ENDPOINTS =====
const String url_validar       = "http://35.168.148.150/validarUID.php";
const String url_evento        = "http://35.168.148.150/registrarEvento.php";
const String url_estadoBarrera = "http://35.168.148.150/estadoBarrera.php";
const String url_barrera       = "http://35.168.148.150/barrera.php";

// ===== PINS =====
#define RST_PIN   D3
#define SS_PIN    D8
#define LED_PIN   D1
#define SERVO_PIN D2
#define NUM_LEDS  16

// ===== OBJECTS =====
MFRC522 mfrc522(SS_PIN, RST_PIN);
Adafruit_NeoPixel ring(NUM_LEDS, LED_PIN, NEO_GRB + NEO_KHZ800);
Servo servoMotor;

// ===== VARIABLES =====
String estadoActualBarrera = "CERRADA";
unsigned long ultimaConsulta = 0;
const unsigned long intervaloConsulta = 1000; // Consultar cada 1 segundo

// ===== FUNCIONES AUXILIARES =====
void setRingColor(uint8_t r, uint8_t g, uint8_t b) {
  for (int i = 0; i < NUM_LEDS; i++) {
    ring.setPixelColor(i, ring.Color(r, g, b));
  }
  ring.show();
}

void conectarWiFi() {
  if (WiFi.status() == WL_CONNECTED) return;

  Serial.println("\nConectando a WiFi...");
  WiFi.begin(ssid, password);

  int intentos = 0;
  while (WiFi.status() != WL_CONNECTED && intentos < 20) {
    delay(500);
    Serial.print(".");
    intentos++;
  }

  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\n✔ WiFi conectado");
    Serial.print("IP: ");
    Serial.println(WiFi.localIP());
  } else {
    Serial.println("\n✖ No se pudo conectar a WiFi");
  }
}

void abrirSuave() {
  Serial.println("🔓 Abriendo barrera...");
  for (int pos = 0; pos <= 90; pos += 3) {
    servoMotor.write(pos);
    delay(15);
  }
  estadoActualBarrera = "ABIERTA";
}

void cerrarSuave() {
  Serial.println("🔒 Cerrando barrera...");
  for (int pos = 90; pos >= 0; pos -= 3) {
    servoMotor.write(pos);
    delay(15);
  }
  estadoActualBarrera = "CERRADA";
}

bool validarConAPI(const String& uid) {
  if (WiFi.status() != WL_CONNECTED) return false;

  HTTPClient http;
  WiFiClient client;
  http.begin(client, url_validar);
  http.addHeader("Content-Type", "application/x-www-form-urlencoded");
  String postData = "uid=" + uid;

  int httpCode = http.POST(postData);
  if (httpCode <= 0) { 
    Serial.println("Error en validación HTTP: " + String(httpCode));
    http.end(); 
    return false; 
  }

  String response = http.getString();
  http.end();

  Serial.println("Respuesta validación: " + response);

  StaticJsonDocument<256> doc;
  DeserializationError error = deserializeJson(doc, response);
  if (error) {
    Serial.println("Error JSON: " + String(error.c_str()));
    return false;
  }

  return doc["permitido"] | false;
}

// ===== CONSULTA ESTADO APP =====
String consultarEstadoAPP() {
  if (WiFi.status() != WL_CONNECTED) return "NADA";

  HTTPClient http;
  WiFiClient client;
  String url = url_estadoBarrera + "?id_departamento=1";
  http.begin(client, url);
  http.setTimeout(5000); // Timeout de 5 segundos
  int httpCode = http.GET();

  if (httpCode != 200) { 
    Serial.println("Error consultando estado: " + String(httpCode));
    http.end(); 
    return "NADA"; 
  }

  String response = http.getString();
  http.end();

  Serial.println("Respuesta estado: " + response);

  // Parsear JSON
  StaticJsonDocument<256> doc;
  DeserializationError error = deserializeJson(doc, response);
  if (error) {
    Serial.println("Error JSON estado: " + String(error.c_str()));
    return "NADA";
  }

  String comando = doc["comando"] | "NADA";
  comando.toUpperCase();
  
  Serial.println("Comando recibido: " + comando);
  return comando;
}

// ===== REGISTRAR EVENTO =====
void registrarEvento(const String& tipo, const String& resultado, int id_sensor = 1, int id_usuario = 0, int id_departamento = 1) {
  if (WiFi.status() != WL_CONNECTED) return;

  HTTPClient http;
  WiFiClient client;
  http.begin(client, url_evento);
  http.addHeader("Content-Type", "application/x-www-form-urlencoded");

  String postData = "id_sensor=" + String(id_sensor) +
                    "&id_usuario=" + String(id_usuario) +
                    "&id_departamento=" + String(id_departamento) +
                    "&tipo_evento=" + tipo +
                    "&resultado=" + resultado;

  int httpCode = http.POST(postData);
  Serial.println("Evento registrado. HTTP: " + String(httpCode));
  http.end();
}

// ===== SETUP =====
void setup() {
  Serial.begin(115200);
  delay(1000);
  Serial.println("\n\n=== SISTEMA INICIANDO ===");

  conectarWiFi();

  ring.begin();
  ring.setBrightness(50);
  setRingColor(0, 0, 0);

  SPI.begin();
  mfrc522.PCD_Init();

  servoMotor.attach(SERVO_PIN);
  servoMotor.write(0);

  Serial.println("✅ Sistema iniciado correctamente");
  Serial.println("Esperando comandos...\n");
}

// ===== LOOP PRINCIPAL =====
void loop() {
  // Reconectar WiFi si es necesario
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("⚠ WiFi desconectado, reconectando...");
    conectarWiFi();
    delay(2000);
    return;
  }

  // 1️⃣ Revisar comandos desde la APP (cada segundo)
  unsigned long ahora = millis();
  if (ahora - ultimaConsulta >= intervaloConsulta) {
    ultimaConsulta = ahora;
    
    String comando = consultarEstadoAPP();
    
    if (comando == "ABRIR" && estadoActualBarrera != "ABIERTA") {
      Serial.println("\n📱 APP SOLICITÓ: ABRIR BARRERA");
      setRingColor(0, 255, 0); // Verde
      abrirSuave();
      setRingColor(0, 0, 0);
      registrarEvento("APERTURA_MANUAL_APP", "PERMITIDO");
      
    } else if (comando == "CERRAR" && estadoActualBarrera != "CERRADA") {
      Serial.println("\n📱 APP SOLICITÓ: CERRAR BARRERA");
      setRingColor(255, 255, 0); // Amarillo
      cerrarSuave();
      setRingColor(0, 0, 0);
      registrarEvento("CIERRE_MANUAL_APP", "PERMITIDO");
    }
  }

  // 2️⃣ Lectura de RFID
  if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
    String uid = "";
    for (byte i = 0; i < mfrc522.uid.size; i++) {
      if (mfrc522.uid.uidByte[i] < 0x10) uid += "0";
      uid += String(mfrc522.uid.uidByte[i], HEX);
    }
    uid.toUpperCase();
    Serial.println("\n🔖 UID detectado: " + uid);

    bool acceso = validarConAPI(uid);
    if (acceso) {
      Serial.println("✅ Acceso PERMITIDO");
      setRingColor(0, 255, 0); // Verde
      abrirSuave();
      delay(6000); // Mantener abierta 6 segundos
      cerrarSuave();
      setRingColor(0, 0, 0);
      registrarEvento("APERTURA_RFID", "PERMITIDO");
      
    } else {
      Serial.println("❌ Acceso DENEGADO");
      setRingColor(255, 0, 0); // Rojo
      delay(2000);
      setRingColor(0, 0, 0);
      registrarEvento("APERTURA_RFID", "DENEGADO");
    }

    mfrc522.PICC_HaltA();
    mfrc522.PCD_StopCrypto1();
    delay(1000); // Anti-rebote
  }

  delay(100); // Pequeña pausa para no saturar
}
