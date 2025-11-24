# UVision: Pulseira Inteligente para Monitoramento de Radiação UV

> Um projeto de pulseira inteligente voltada ao monitoramento de radiação ultravioleta (UV) e prevenção do câncer de pele.

![[INSERIR IMAGEM DA PULSEIRA AQUI]](https://via.placeholder.com/600x300)

## 🎯 Sobre o Projeto

A exposição excessiva à radiação solar é um grave problema de saúde pública. De acordo com a OMS, cerca de 60 mil pessoas morrem anualmente por doenças relacionadas a essa exposição. Em países tropicais como o Brasil, a incidência solar é elevada na maior parte do ano, aumentando os riscos.

O **UVision** nasce como uma tecnologia assistiva universal com o objetivo de promover a prevenção e a conscientização sobre os efeitos da radiação solar. A pulseira monitora a exposição e incentiva hábitos mais seguros, sendo especialmente útil para trabalhadores e praticantes de atividades ao ar livre.

Este projeto integra ciência, tecnologia e saúde preventiva para contribuir com a redução de casos de câncer de pele.

## 🚀 Funcionalidades Principais

* **Monitoramento em Tempo Real:** Utiliza o sensor ML8511 para detectar a intensidade dos raios UV (faixa de 280-390 nm).
* **Cálculo de Dose:** Converte os dados de irradiância em unidades de Dose Eritêmica Padrão (SED), uma métrica internacional para riscos à saúde.
* **Personalização:** O firmware permite ajustar as medições com base no tipo de pele, uso de protetor solar e áreas corporais expostas.
* **Classificação de Risco:** Categoriza o risco em quatro níveis: baixo, moderado, alto e muito alto.
* **Alertas Preventivos:** Emite alertas (sonoros ou luminosos) quando o tempo seguro de exposição é ultrapassado.
* **Conectividade:** Transmite os dados via Bluetooth para um aplicativo móvel.
* **Histórico de Exposição:** O aplicativo armazena o histórico diário e semanal, facilitando o acompanhamento dos hábitos.

## 🛠️ Hardware e Tecnologias

O protótipo foi desenvolvido utilizando os seguintes componentes principais:

* **Microcontrolador:** ESP32
* **Sensor UV:** Sensor Óptico ML8511
* **Comunicação:** Bluetooth (integrado ao ESP32)
* **Firmware:** C/C++ (Arduino)
* **App Móvel:** `[INSERIR TECNOLOGIA DO APP, EX: FLUTTER, KOTLIN, ETC.]`

## ⚙️ Como Funciona (Fluxo de Dados)

1.  O sensor **ML8511** capta a luz solar e a converte em sinais elétricos proporcionais à irradiância.
2.  O **ESP32** processa esses sinais.
3.  O firmware calcula a **Dose Eritêmica Padrão (SED)**, levando em conta as personalizações do usuário (tipo de pele, etc.).
4.  O sistema classifica o **Nível de Risco** (Baixo a Muito Alto).
5.  Se o limite de exposição segura for atingido, a pulseira emite um **alerta**.
6.  Paralelamente, os dados são enviados via **Bluetooth** para o aplicativo, onde o histórico é armazenado.

## 📦 Como Usar o Projeto

`[ESTA SEÇÃO É UM PLACEHOLDER - Você deve adicionar as instruções de como compilar e rodar o seu código. Por exemplo:]`

**Pré-requisitos:**

* Arduino IDE ou PlatformIO
* Biblioteca `[Nome da Biblioteca]`
* O aplicativo móvel `[Nome do App]`

**Passos para o Firmware:**

1.  Clone este repositório:
    ```bash
    git clone [https://github.com/edwfreitas/UVision_Inovatech.git](https://github.com/edwfreitas/UVision_Inovatech.git)
    cd UVision_Inovatech
    ```
2.  Abra o arquivo `.ino` na Arduino IDE.
3.  Configure a placa para "ESP32 Dev Module" (ou similar).
4.  Compile e envie o código para a placa.

**Passos para o Aplicativo:**

* O repositório do aplicativo pode ser encontrado aqui: `[LINK PARA O REPOSITÓRIO DO APP]`
* `[Instruções de build do app]`

## 📈 Resultados e Limitações

Os testes indicaram que a pulseira é eficiente no registro e interpretação das variações de radiação solar. Os alertas em tempo real mostraram-se eficazes na conscientização do usuário.

Uma limitação observada é a necessidade de calibração precisa do sensor, pois diferentes ângulos e condições climáticas podem influenciar as medições. Apesar disso, o desempenho geral foi considerado satisfatório.

## 🎓 Autores e Contexto do Projeto

Este projeto foi desenvolvido como parte do curso de **Sistema de Informação (4º Período)** do `[NOME DA INSTITUIÇÃO AQUI]`.

**Orientador(es):**
* `[NOME DO ORIENTADOR(A)]`

**Equipe (Autores):**
* Alexandro Simas
* Arleson Marinho
* Caio Luan
* Camilly Matelins
* Charles Gabriel
* Christian Marques
* Danilo Clever
* **Eduardo Freitas (edwfreitas)**
* Erich Mark
* Gabriela Marques
* George Ruso
* Giovanna Isabelle
* Guilherme John
* Hilary Larissa
* Isaac Lira
* Jamilly Carvalho
* Josias Tomáz
* Lara Jessica
* Lucas Silva
* Luiz Gabriel
* Mateus Fidelis
* Matheus Araújo
* Robison Nascimento
* Thaianny Cristine
* Yamilla Nicásio
* Yuri Girão

## 📜 Licença

`[ADICIONAR LICENÇA AQUI, EX: MIT]`

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
