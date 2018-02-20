/*
 * #################################
 *         SKETCH SCALA v1.0
 * #################################
 *
*/
#include "FastLED.h"
#include <SoftwareSerial.h>

#define NUM_LEDS 7  //n. led nella striscia CRI = 7
#define NUM_COL 8    //n. colori 
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
colore lista_colori[NUM_COL];

//Oggetto COM Bluettoth e Variabile buffer
SoftwareSerial bluetooth(6, 7); //BLUETOOTH: PIN TXD 6, PIN RXD 7
String BLUETOOTH_BUFFER = "";

//Variabili per funzionamento scala e pulsante
uint8_t flag_print= 1; //|0-No Print |1-Serial.Print |2-Bluetooth Print
boolean sign_PIR_BASSO=false;
boolean sign_PIR_ALTO=false;
boolean sign_PULS=false;
boolean luce = false;
boolean mov_salita=false;
boolean mov_discesa=false;
/*
------------------------------------------------------
||  POS:  ||             DESCRIZIONE:               ||
------------------------------------------------------
||   0    ||  tempo/colore/luminosità salita ON     ||
||   1    ||  tempo/colore/luminosità salita OFF    ||
||   2    ||  tempo/colore/luminosità discesa ON    ||
||   3    ||  tempo/colore/luminosità discesa OFF   ||
||   4    ||  tempo/colore/luminosità pulsante ON   ||
||   5    ||  tempo/colore/luminosità pulsante OFF  ||
------------------------------------------------------
*/
uint16_t tempi[6] = {100, 100, 100, 100, 0, 0};
uint32_t colori[6] = {0xFFFFFF, 0x000000, 0xFFFFFF, 0x000000, 0xFFFFFF, 0x000000};
uint8_t livelli[6] = {145, 145, 145, 145, 145, 145};


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
  myprintln ("---------------------- SCALA START ----------------------------");
  FastLED.addLeds<WS2811, DATA_PIN, BRG>(leds, NUM_LEDS);   //Inizializzo Array di LED
  bluetooth.begin(9600);  //Inizializzo COM Bluetooth
  lista_colori[0] = {"BLK", 0x000000}; //BLACK
  lista_colori[1] = {"WHT", 0xFFFFFF}; //WHITE
  lista_colori[2] = {"RED", 0xFF0000}; //RED
  lista_colori[3] = {"GRN", 0x00FF00}; //GREEN
  lista_colori[4] = {"BLU", 0x0000FF}; //BLUE
  lista_colori[5] = {"YEL", 0xFFFF00}; //YELLOW
  lista_colori[6] = {"PUR", 0xFF00FF}; //PURPLE
  lista_colori[7] = {"AZU", 0x00FFFF}; //AZURE
}

void myprint(String mex)
{
  if (flag_print==1)
    Serial.print(mex);
  if (flag_print==2)
    bluetooth.print(mex);
}

void myprintln(String mex)
{
  if (flag_print==1)
    Serial.println(mex);
  if (flag_print==2)
    bluetooth.println(mex);    
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
  myprint("PIR BASSO:");
  myprintln((String)sign_PIR_BASSO);
  myprint("PIR ALTO:");
  myprintln((String)sign_PIR_ALTO);
  if (presenzaLUCE==false)
  {
    //RILEVO PIR BASSO E NON PIR ALTO
    if (sign_PIR_BASSO==true and sign_PIR_ALTO==false)
    {
      //STO SALENDO-ACCENDO LUCI IN SALITA
      if (mov_salita==false and mov_discesa==false)
      {    
        cambia_striscia(0, NUM_LEDS, tempi[0], livelli[0], colori[0]);
        mov_salita=true;
        luce = true;
      }
      //SONO SCESO-SPENGO LUCI IN DISCESA
      if (mov_salita==false and mov_discesa==true)
      {  
        cambia_striscia(NUM_LEDS, 0, tempi[3], livelli[3], colori[3]);
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
        cambia_striscia(NUM_LEDS, 0, tempi[2], livelli[2], colori[2]);
        mov_discesa=true;
        luce = true;
      }     
      // SONO SALITO-SPENGO LUCI IN SALITA
      if (mov_salita==true and mov_discesa==false )
      {
        cambia_striscia(0, NUM_LEDS, tempi[1], livelli[1], colori[1]);
        mov_salita=false;
        luce = false;
      }  
    }
  }
  else
    myprintln("LUCE RILEVATA SCALA OFF!!");
}

boolean letturaLUCE()
{
  boolean presenza = false;
  if (usaFOTO == true)
  {
    sign_LUCE=analogRead(FOTO_PIN);
    myprintln("Presenza LUCE: " + (String)sign_LUCE);
    if (not (sign_LUCE>=sogliaBUIO))
      presenza = true;
  }
  return presenza;    
} 

void read_BUTTON()
{
  sign_PULS=digitalRead(PULS_PIN);
  myprint("BUTTON: ");
  myprintln((String)sign_PULS);
  if (sign_PULS==true and luce==true)
  {
    luce=false;
    cambia_striscia(0, NUM_LEDS, tempi[5], livelli[5], colori[5]);
  }
  else if (sign_PULS==true and luce==false)
  {
    luce=true;
    cambia_striscia(0, NUM_LEDS, tempi[4], livelli[4], colori[4]);
  }
}

void statoSCALA()
{
  myprint("STATO SCALA:");
  if (presenzaLUCE==true)
    myprintln("ON");
  else
    myprintln("OFF");
}

void loop()
{
  //Lettura Pulsante Manuale
  read_BUTTON();
  //Lettura abilitazione Fotoresistenza
  presenzaLUCE=letturaLUCE();
  //statoSCALA();
  letturaPir();
  //Lettura Bluetooth*/
  BLUETOOTH_READ();
  if (BLUETOOTH_BUFFER != "")
    BLUETOOTH_COMMAND();
  delay(200);
}

void BLUETOOTH_READ(){
  BLUETOOTH_BUFFER = "";
  while (bluetooth.available())
  {
    BLUETOOTH_BUFFER += (char)bluetooth.read();
  }    
  if (!bluetooth.available() && BLUETOOTH_BUFFER != ""){
    myprintln("RX: " + BLUETOOTH_BUFFER);
    bluetooth.println("chk_"+BLUETOOTH_BUFFER);
  }
}

void BLUETOOTH_COMMAND()
{
  if (BLUETOOTH_BUFFER.substring(0, 4) == "setT")
    SET_TEMPI(BLUETOOTH_BUFFER.substring(4, BLUETOOTH_BUFFER.length()));
  if (BLUETOOTH_BUFFER.substring(0, 4) == "getT")
    GET_TEMPI(BLUETOOTH_BUFFER.substring(4, BLUETOOTH_BUFFER.length()));
  if (BLUETOOTH_BUFFER.substring(0, 4) == "setC")
    SET_COLLUM(BLUETOOTH_BUFFER.substring(4, BLUETOOTH_BUFFER.length()));
  if (BLUETOOTH_BUFFER.substring(0, 4) == "getC")
    bluetooth.println("COL:"+getNome(colori[0])+"LUM:"+livelli[0]);
  if (BLUETOOTH_BUFFER.substring(0, 4) == "setF")
    SET_FOTO(BLUETOOTH_BUFFER.substring(4, BLUETOOTH_BUFFER.length()));
  if (BLUETOOTH_BUFFER.substring(0, 4) == "getF")
    bluetooth.println(usaFOTO);
  if (BLUETOOTH_BUFFER.substring(0, 4) == "com1")
    COMBO_1();
  if (BLUETOOTH_BUFFER.substring(0, 4) == "com2")
    COMBO_2();
  BLUETOOTH_BUFFER = "";
}

void GET_TEMPI(String s)
{
  String direzione = s.substring(0,s.length());
  if (direzione == "SU")
    bluetooth.println("tONs"+String(tempi[0])+"tOFFs"+String(tempi[1]));
  if (direzione == "GIU")
    bluetooth.println("tONd"+String(tempi[2])+"tOFFd"+String(tempi[3]));
}

void SET_TEMPI(String s)
{
  String direzione = s.substring(0,s.indexOf("ON:"));
  myprintln(direzione);
  String tON = s.substring(s.indexOf("ON:")+3,s.indexOf("OFF:"));
  String tOFF = s.substring(s.indexOf("OFF:")+4,s.length());
  if (direzione == "SU" )
  {
    tempi[0] = tON.toInt();
    tempi[1] = tOFF.toInt();
    myprint("Tempo Salita ON:");
    myprintln((String)tempi[0]);
    myprint("Tempo Salita OFF:");
    myprintln((String)tempi[1]);
  }
  if (direzione == "GIU" )
  {
    tempi[2] = tON.toInt();
    tempi[3] = tOFF.toInt();
    myprint("Tempo Discesa ON:");
    myprintln((String)tempi[2]);
    myprint("Tempo Discesa OFF:");
    myprintln((String)tempi[3]);
  }
}

void SET_COLLUM(String s)
{
  String colore = s.substring(0,s.indexOf("LUM:"));
  String livello = s.substring(s.indexOf("LUM:")+4,s.length());
  colori[0]=getColor(colore);
  colori[2]=getColor(colore);
  colori[4]=getColor(colore);
  for(int i=0;i<6;i++)
    livelli[i]=livello.toInt();
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
  for(uint8_t i=0;i<NUM_COL and trovato == false;i++)
  {
    if (lista_colori[i].nome == nome)
    {
      valore = lista_colori[i].valore;
      trovato = true;
    }
  }
  return valore;
}

String getNome(uint32_t valore)
{
  String nome = "WHT";
  boolean trovato = false;
  for(uint8_t i=0;i<NUM_COL and trovato == false;i++)
  {
    if (lista_colori[i].valore == valore)
    {
      nome = lista_colori[i].nome;
      trovato = true;
    }
  }
  return nome;
}

void COMBO_3()
{
  cambia_striscia(0, NUM_LEDS, 0, 100, lista_colori[0].valore);
  uint32_t colore = 0xFFFFFF;
  uint16_t decremento = colore / NUM_LEDS;
  for(int16_t i=0; i<NUM_LEDS;i++)
  {
    cambia_led(i, 140, colore);
    delay(120);
    colore = colore - decremento;
  }
  delay(500);
  cambia_striscia(0, NUM_LEDS, 0, 100, lista_colori[0].valore);
}

void COMBO_1()
{
  cambia_striscia(0, NUM_LEDS, 0, 100, lista_colori[0].valore);
  uint32_t colore = 0xFFFFFF;
  uint16_t decremento = colore / NUM_LEDS;
  for(int16_t i=0; i<NUM_LEDS;i++)
  {
    if (i==0)
      colore=0xFFFFFF;
    if (i==1)
      colore=0xFFFF00; //GIALLO
    if (i==2)
      colore=0xFF0000; //ROSSO
    if (i==3)
      colore=0xFF00FF; //FUCSIA 
    if (i==4)
      colore=0x0000FF; //BLU
    if (i==5)
      colore=0x00FFFF; //AZZURRO
    if (i==6)
      colore=0x00FF00; //VERDE
          
    cambia_led(i, 140, colore);
    delay(120);
    //colore = colore - decremento;
  }
  delay(500);
  cambia_striscia(0, NUM_LEDS, 0, 100, lista_colori[0].valore);
}


void COMBO_2()
{
  myprintln("INIZIO COMBO 2----------------------");
  for(int16_t i=0; i==2;i++)
  {
    
    cambia_led(i, 140, 0XFFFF00);
    cambia_led(NUM_LEDS-i, 140, 0X00FFFF);
    delay(1000);
  }
  cambia_led(3, 140, 0x00FF00);
  delay(1000);
  myprintln("META' COMBO 2----------------------");
  for(int16_t i=0; i==2;i++)
  {
    i=i+1;
    cambia_led(3-i, 140, 0XFFFF00);
    cambia_led(3+i, 140, 0X00FFFF);
    delay(1000);
    i=i-1;
  }  
  cambia_striscia(0, NUM_LEDS, 0, 100, lista_colori[0].valore);
  myprintln("FINE COMBO 2----------------------");
}

void COMBO_2()
{
  myprintln("INIZIO COMBO 2----------------------");
  for(int16_t x=0; i==2;i++)
  {
    
    cambia_led(i, 140, 0XFFFF00);
    cambia_led(NUM_LEDS-i, 140, 0X00FFFF);
    delay(1000);
  }
  cambia_led(3, 140, 0x00FF00);
  delay(1000);
  myprintln("META' COMBO 2----------------------");
  for(int16_t y=0; i==2;i++)
  {
    y=y+1;
    cambia_led(y-i, 140, 0XFFFF00);
    cambia_led(y+i, 140, 0X00FFFF);
    delay(1000);
    y=y-1;
  }  
  cambia_striscia(0, NUM_LEDS, 0, 100, lista_colori[0].valore);
  myprintln("FINE COMBO 2----------------------");
}
