/*
  Arquivo: ConexaoBT.ino
  Responsabilidade: Enviar o número inteiro via Bluetooth.
*/

#include "BluetoothSerial.h"

BluetoothSerial SerialBT;

void iniciarBluetooth(String nomeDispositivo) {
  if(!SerialBT.begin(nomeDispositivo)) {
    Serial.println("Erro ao iniciar Bluetooth!");
  } else {
    Serial.println("Bluetooth Pronto!");
  }
}

void enviarDadosBluetooth(int valorUV) {
  if (SerialBT.hasClient()) {
    // Envia apenas o número puro. Ex: "5"
    // Se quiser adicionar texto antes, descomente a linha abaixo:
    // SerialBT.print("Indice: ");
    
    SerialBT.println(valorUV); 
  }
}