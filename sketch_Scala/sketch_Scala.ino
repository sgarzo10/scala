/*
 * #################################
 *         SKETCH SCALA v1.0
 * #################################
 *
*/
#include "FastLED.h"  //Libreria contenente istruzioni per striscia LED WS2811
#include <SoftwareSerial.h>

#define NUM_LEDS 40   //n. led nella striscia CRI = 100
#define DATA_PIN 9    //uscita per collegamento striscia led
#define PIR_BASSO 2   //PIR Piano Terra
#define PIR_ALTO 4    //PIR Primo Piano
#define PULS_PIN 8    //pulsante per accensione manuale
#define FOTO_PIN A0  //Fotoresistenza
#define BLACK 0x000000
#define WHITE 0xFFFFFF

//Oggetto COM Bluettoth e Variabile buffer
SoftwareSerial bluetooth(6, 7); //BLUETOOTH: PIN TXD 6, PIN RXD 7
String BLUETOOTH_BUFFER = "";

//Variabili per funzionamento scala
boolean sign_PIR_BASSO=false;
boolean sign_PIR_ALTO=false;
boolean mov_salita=false;
boolean mov_discesa=false;
uint16_t tempo_salita_ON = 100;
uint16_t tempo_salita_OFF = 100;
uint16_t tempo_discesa_ON = 100;
uint16_t tempo_discesa_OFF = 100;

//Variabili per gestione Fotoresistenza
boolean usaFOTO = true;
boolean presenzaLUCE =true;
uint16_t sign_LUCE=0;
uint16_t sogliaBUIO=380;

//Varaibili per gestione Pulsante Manuale
boolean sign_PULS=false;
boolean scalaAccesa=false;

// Definisco n. led nella striscia
CRGB leds[NUM_LEDS];

void setup()
{  
  pinMode(FOTO_PIN, INPUT);   //Imposto PIN FOTORESISTENZA  
  Serial.begin (9600);   //Inizializzo Seriale
  Serial.println ("SCALA START");
  FastLED.addLeds<WS2811, DATA_PIN, BRG>(leds, NUM_LEDS);   //Inizializzo Array di LED
  bluetooth.begin(9600);  //Inizializzo COM Bluetooth
}

void cambia_led(uint8_t posizione, uint16_t livello, uint32_t color)
{
  leds[posizione] = color;
  leds[posizione].fadeLightBy(livello);
  FastLED.show();
}
  
void cambia_striscia(uint16_t inizio, uint16_t fine, uint16_t tempo, uint16_t livello, uint32_t color)
{
  if (inizio < fine)
  {
    for (inizio;inizio<fine;inizio++)
     {
      cambia_led(inizio,livello,color);
      delay(tempo);
     }
  }
  if (inizio > fine)
  {
    for(inizio-1;inizio>=fine;inizio--)
    {
      cambia_led(inizio,livello,color);
      delay(tempo);
    }
  }
}

void letturaPir()
{
  sign_PIR_BASSO=digitalRead(PIR_BASSO);
  sign_PIR_ALTO=digitalRead(PIR_ALTO);
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
        cambia_striscia(0, NUM_LEDS, tempo_salita_ON, 100, WHITE);
        mov_salita=true;
      }
      //SONO SCESO-SPENGO LUCI IN DISCESA
      if (mov_salita==false and mov_discesa==true)
      {    
        cambia_striscia(NUM_LEDS, 0, tempo_discesa_OFF, 100, BLACK);
        mov_discesa=false;
      }
    }
    //RILEVO PIR BASSO E NON PIR ALTO  
    if (sign_PIR_BASSO==false and sign_PIR_ALTO==true)
    {
      // SONO SALITO-SPENGO LUCI IN SALITA
      if (mov_salita==true and mov_discesa==false )
      {
        cambia_striscia(0, NUM_LEDS, tempo_salita_OFF, 100, BLACK);
        mov_salita=false;
      }  
      // STO SCENDENDO-ACCENDO LUCI IN DISCESA
      if (mov_salita==false and mov_discesa==false )
      {
        cambia_striscia(NUM_LEDS, 0, tempo_discesa_ON, 100, WHITE);
        mov_discesa=true;
      }        
    }
  }
  else
    Serial.println("LUCE RILEVATA SCALA OFF!!");
}

boolean letturaLUCE()
{
  sign_LUCE=analogRead(FOTO_PIN);
  Serial.print("Presenza LUCE: ");
  Serial.println(sign_LUCE);
  if (sign_LUCE>=sogliaBUIO)
    return false;
  else
    return true;
} 

void read_BUTTON()
{
  sign_PULS=digitalRead(PULS_PIN);
  Serial.print("BUTTON: ");
  Serial.println(sign_PULS);
  if (sign_PULS==true and scalaAccesa==true)
  {
    scalaAccesa=false;
    cambia_striscia(0, NUM_LEDS, 0, 100, BLACK);
  }
  else if (sign_PULS==true and scalaAccesa==false)
  {
    scalaAccesa=true;
    cambia_striscia(0, NUM_LEDS, 0, 100, WHITE);
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
  
  /* //Lettura Pulsante Manuale
  read_BUTTON();
  //Lettura abilitazione Fotoresistenza
  presenzaLUCE=letturaLUCE();
  statoSCALA();
  letturaPir();*/
  /*//Lettura Bluetooth
  BLUETOOTH_READ();
  if (BLUETOOTH_BUFFER != "")
    BLUETOOTH_COMMAND();*/
  cambia_led(true, 1, 140, WHITE);
  delay(2000);
  cambia_led(true, 1, 140, BLACK);
  delay(2000);
  cambia_striscia(0, 20, tempo_salita_ON, 140, WHITE);
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
    SET_TEMPO(BLUETOOTH_BUFFER.substring(4, BLUETOOTH_BUFFER.length()));
  if (BLUETOOTH_BUFFER.substring(0, 4) == "foto")
    SET_FOTO(BLUETOOTH_BUFFER.substring(4, BLUETOOTH_BUFFER.length())); 
  BLUETOOTH_BUFFER = "";
}

void SET_TEMPO(String s)
{
  String direzione = s.substring(4,s.indexOf("LUCE:"));
  String luce = s.substring(s.indexOf("LUCE:")+5,s.indexOf("TEMPO:"));
  String tempo = s.substring(s.indexOf("TEMPO:")+6,s.length());
  if (direzione == "SU" && luce == "ON")
    tempo_salita_ON = tempo.toInt();
  if (direzione == "GIU" && luce == "ON")
    tempo_discesa_ON = tempo.toInt();
  if (direzione == "SU" && luce == "OFF")
    tempo_salita_OFF = tempo.toInt();
  if (direzione == "GIU" && luce == "OFF")
    tempo_discesa_OFF = tempo.toInt();
}

void SET_FOTO(String s)
{
  if (s == "ON")
    usaFOTO = true;
  if (s == "OFF")
    usaFOTO = false;
}
