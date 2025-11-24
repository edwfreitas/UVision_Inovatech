#define PINO_SENSOR_UV 36 // GPIO 36 (VP)

// Variável global para armazenar o índice UV (agora é um número inteiro)
int indiceUV = 0;

void setup() {
  Serial.begin(115200);
  
  // Inicia o Bluetooth (Verifique o nome no arquivo ConexaoBT)
  iniciarBluetooth("UVision_SmartBand");
  
  pinMode(PINO_SENSOR_UV, INPUT);
  Serial.println("Sistema UVision Iniciado...");
}

void loop() {
  // 1. Realiza a leitura e obtém o número inteiro (0 a 11)
  indiceUV = lerSensorUV(PINO_SENSOR_UV);

  // 2. Envia o número via Bluetooth
  enviarDadosBluetooth(indiceUV);

  // Debug no PC
  Serial.print("Indice UV Identificado: ");
  Serial.println(indiceUV);

  delay(1000);
}
