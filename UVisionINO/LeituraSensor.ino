/*
  Arquivo: LeituraSensor.ino
  Responsabilidade: Ler a voltagem e classificar de 0 a 11 conforme datasheet.
*/

int lerSensorUV(int pino) {
  long somaLeituras = 0;
  int quantidadeAmostras = 20; // Aumentei para 20 para ficar bem estável

  // 1. Média Móvel para estabilizar o sinal
  for (int i = 0; i < quantidadeAmostras; i++) {
    somaLeituras += analogRead(pino);
    delay(5);
  }
  float mediaAnalogica = somaLeituras / (float)quantidadeAmostras;

  // 2. Converter leitura do ESP32 (0-4095) para Milivolts (mV)
  // Fórmula: (Leitura / 4095) * 3300mV
  float voltagem_mV = (mediaAnalogica / 4095.0) * 3300.0;

  // 3. Comparação com a tabela da imagem (Vout em mV)
  // Usamos os valores da tabela como limites inferiores para cada índice.
  
  if (voltagem_mV < 50) return 0;
  else if (voltagem_mV < 227) return 0;  // Zona cinzenta, mantemos 0 até chegar perto de 227
  else if (voltagem_mV < 318) return 1;  // Entre 227 e 318
  else if (voltagem_mV < 408) return 2;  // Entre 318 e 408
  else if (voltagem_mV < 503) return 3;
  else if (voltagem_mV < 606) return 4;
  else if (voltagem_mV < 696) return 5;
  else if (voltagem_mV < 795) return 6;
  else if (voltagem_mV < 881) return 7;
  else if (voltagem_mV < 976) return 8;
  else if (voltagem_mV < 1079) return 9;
  else if (voltagem_mV < 1170) return 10;
  else return 11; // Qualquer coisa acima de 1170mV
}