/*
 * #################################
 *         SKETCH SCALA v1.0
 * #################################
 *
*/
#include "FastLED.h"
#include <SoftwareSerial.h>

#define NUM_LEDS 40  //n. led nella striscia CRI = 100
#define DATA_PIN 9   //uscita per collegamento striscia led
#define PIR_BASSO 2  //PIR Piano Terra
#define PIR_ALTO 4   //PIR Primo Piano
#define PULS_PIN 8   //pulsante per accensione manuale
#define FOTO_PIN A0  //Fotoresistenza

//Struct per il colore e array di colori
typedef struct {
  String nome;
  uint32_t valore;
  } colore;
colore colori[10];

//Oggetto COM Bluettoth e Variabile buffer
SoftwareSerial bluetooth(6, 7); //BLUETOOTH: PIN TXD 6, PIN RXD 7
String BLUETOOTH_BUFFER = "";

//Variabili per funzionamento scala e pulsante
boolean sign_PIR_BASSO=false;
boolean sign_PIR_ALTO=false;
boolean sign_PULS=false;
boolean luce = false;
boolean mov_salita=false;
boolean mov_discesa=false;
uint16_t tempi[6] = {100, 100, 100, 100, 0, 0}; //salita ON, salita OFF, discesa ON, discesa OFF, pulsante ON, pulsante OFF
uint32_t colori_movimento[6] = {0xFFFFFF, 0x000000, 0xFFFFFF, 0x000000, 0xFFFFFF, 0x000000};
uint8_t livelli[6] = {140, 140, 140, 140, 140, 140};

//Variabili per gestione Fotoresistenza
boolean usaFOTO = true;
boolean presenzaLUCE =true;
uint16_t sign_LUCE=0;
uint16_t sogliaBUIO=380;

// Definisco n. led nella striscia
CRGB leds[NUM_LEDS];

void setup()
{  
  pinMode(FOTO_PIN, INPUT);   //Imposto PIN FOTORESISTENZA  
  Serial.begin (9600);   //Inizializzo Seriale
  Serial.println ("SCALA START");
  Serial.println("------------------------------------------");
  FastLED.addLeds<WS2811, DATA_PIN, BRG>(leds, NUM_LEDS);   //Inizializzo Array di LED
  bluetooth.begin(9600);  //Inizializzo COM Bluetooth
  colori[0] = {"BLK", 0x000000}; //BLACK
  colori[1] = {"WHT", 0xFFFFFF}; //WHITE
  colori[2] = {"RED", 0xFF0000}; //RED
  colori[3] = {"GRN", 0x00FF00}; //GREEN
  colori[4] = {"BLU", 0x0000FF}; //BLUE
  colori[5] = {"YEL", 0xFFFF00}; //YELLOW
  colori[6] = {"PUR", 0xFF00FF}; //PURPLE
  colori[7] = {"AZU", 0x00FFFF}; //AZURE
}

void cambia_led(int16_t posizione, uint16_t livello, uint32_t color)
{
  leds[posizione] = color;
  leds[posizione].fadeLightBy(livello);
  FastLED.show();
}
  
void cambia_striscia(int16_t inizio, int16_t fine, uint16_t tempo, uint16_t livello, uint32_t color)
{
  if (inizio < fine)
  {
    for (inizio;inizio<fine;inizio++)
     {
      cambia_led(inizio,livello,color);
      delay(tempo);
     }
  }
  else
  {
    for(inizio = inizio-1;inizio>=fine;inizio--)
    {
      cambia_led(inizio,livello,color);
      delay(tempo);
    }
  }
}

void letturaPir()
{
  sign_PIR_ALTO=digitalRead(PIR_ALTO);
  sign_PIR_BASSO=digitalRead(PIR_BASSO);
  Serial.print("PIR BASSO:");
  Serial.println(sign_PIR_BASSO);
  Serial.print("PIR ALTO:");
  Serial.println(sign_PIR_ALTO);
  if (presenzaLUCE==false)
  {

    //RILEVO PIR BASSO E NON PIR ALTO
    if (sign_PIR_BASSO==true and sign_PIR_ALTO==false)
    {
      //STO SALENDO-ACCENDO LUCI IN SALITA
      if (mov_salita==false and mov_discesa==false)
      {    
        cambia_striscia(0, NUM_LEDS, tempi[0], livelli[0], colori_movimento[0]);
        mov_salita=true;
        luce = true;
      }
      //SONO SCESO-SPENGO LUCI IN DISCESA
      if (mov_salita==false and mov_discesa==true)
      {  
        cambia_striscia(NUM_LEDS, 0, tempi[3], livelli[3], colori_movimento[3]);
        mov_discesa=false;
        luce = false;
      }
    }
    //RILEVO PIR BASSO E NON PIR ALTO  
    else if (sign_PIR_BASSO==false and sign_PIR_ALTO==true)
    {
      // STO SCENDENDO-ACCENDO LUCI IN DISCESA
      if (mov_salita==false and mov_discesa==false )
      {
        cambia_striscia(NUM_LEDS, 0, tempi[2], livelli[2], colori_movimento[2]);
        mov_discesa=true;
        luce = true;
      }     
      // SONO SALITO-SPENGO LUCI IN SALITA
      if (mov_salita==true and mov_discesa==false )
      {
        cambia_striscia(0, NUM_LEDS, tempi[1], livelli[1], colori_movimento[1]);
        mov_salita=false;
        luce = false;
      }  
    }
    else
      Serial.println("LUCE RILEVATA SCALA OFF!!");
  }
}

boolean letturaLUCE()
{
  boolean presenza = false;
  if (usaFOTO == true)
  {
    sign_LUCE=analogRead(FOTO_PIN);
    Serial.print("Presenza LUCE: ");
    Serial.println(sign_LUCE);
    if (not sign_LUCE>=sogliaBUIO)
      presenza = true;
  }
  return presenza;    
} 

void read_BUTTON()
{
  sign_PULS=digitalRead(PULS_PIN);
  Serial.print("BUTTON: ");
  Serial.println(sign_PULS);
  if (sign_PULS==true and luce==true)
  {
    luce=false;
    cambia_striscia(0, NUM_LEDS, tempi[5], livelli[5], colori_movimento[5]);
  }
  else if (sign_PULS==true and luce==false)
  {
    luce=true;
    cambia_striscia(0, NUM_LEDS, tempi[4], livelli[4], colori_movimento[4]);
  }
}

void statoSCALA()
{
  Serial.print("STATO SCALA:");
  if (presenzaLUCE==true)
    Serial.println("ON");
  else
    Serial.println("OFF");
}

void loop()
{
  
  //Lettura Pulsante Manuale
  read_BUTTON();
  //Lettura abilitazione Fotoresistenza
  presenzaLUCE=letturaLUCE();
  //statoSCALA();
  letturaPir();
  /*
   for (int x=64;x<200;x=x+10)
      cambia_striscia(0, NUM_LEDS,0, x, GREEN);
   cambia_striscia(0, NUM_LEDS,0, 100, BLACK);
  */ 
  /*//Lettura Bluetooth
  BLUETOOTH_READ();
  if (BLUETOOTH_BUFFER != "")
    BLUETOOTH_COMMAND();*/
  delay(200);
}

void BLUETOOTH_READ(){
  BLUETOOTH_BUFFER = "";
  while (bluetooth.available())
    BLUETOOTH_BUFFER += (char)bluetooth.read();
  if (!bluetooth.available() && BLUETOOTH_BUFFER != ""){
    Serial.print("RX: ");
    Serial.println(BLUETOOTH_BUFFER);
    bluetooth.println("chk_"+BLUETOOTH_BUFFER);
  }
}

void BLUETOOTH_COMMAND()
{
  if (BLUETOOTH_BUFFER.substring(0, 4) == "set ")
    SET_TEMPO_COLORE(BLUETOOTH_BUFFER.substring(4, BLUETOOTH_BUFFER.length()));
  if (BLUETOOTH_BUFFER.substring(0, 4) == "foto")
    SET_FOTO(BLUETOOTH_BUFFER.substring(4, BLUETOOTH_BUFFER.length()));
  if (BLUETOOTH_BUFFER.substring(0, 4) == "btn ")
    colori_movimento[4]=getColor(BLUETOOTH_BUFFER.substring(4, BLUETOOTH_BUFFER.length()));
  BLUETOOTH_BUFFER = "";
}

void SET_TEMPO_COLORE(String s)
{
  String direzione = s.substring(4,s.indexOf("COL:"));
  String colore = s.substring(s.indexOf("COL:")+4,s.indexOf("TEMPO:"));
  String tempo = s.substring(s.indexOf("TEMPO:")+6,s.length());
  if (direzione == "SU" && colore != "BLK")
  {
    tempi[0] = tempo.toInt();
    colori_movimento[0] = getColor(colore);
  }
  if (direzione == "GIU" && colore != "BLK")
  {
    tempi[2] = tempo.toInt();
    colori_movimento[2] = getColor(colore);
  }
  if (direzione == "SU" && colore == "BLK")
    tempi[1] = tempo.toInt();
  if (direzione == "GIU" && colore == "BLK")
    tempi[3] = tempo.toInt();
}

void SET_FOTO(String s)
{
  if (s == "ON")
    usaFOTO = true;
  if (s == "OFF")
    usaFOTO = false;
}

uint32_t getColor(String nome)
{
  uint32_t valore=0xFFFFFF;
  boolean trovato = false;
  for(uint8_t i=0;i<8 and trovato == false;i++)
  {
    if (colori[i].nome == nome)
    {
      valore = colori[i].valore;
      trovato = true;
    }
  }
  return valore;
}
