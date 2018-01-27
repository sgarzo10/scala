#include "FastLED.h"  //Libreria contenente istruzioni per striscia LED WS2811

#define NUM_LEDS 100   //n. led nella striscia
#define DATA_PIN 9    //uscita per collegamento DAT striscia led
#define PIR_BASSO 2   //PIR Piano Terra
int sign_PIR_BASSO=0;
#define PIR_ALTO 4    //PIR Primo Piano
int sign_PIR_ALTO=0;
boolean mov_salita=false;
boolean mov_discesa=false;
int FOTO_PIN=A0;         //fotoresistenza per funzionamento ciclo automatico al buio
int sign_LUCE=0;
boolean presenzaLUCE =true;
int sogliaBUIO=380;
#define PULS_PIN 8    //pulsante per cicli random di luci 
int sign_PULS = 0;
boolean scalaAccesa=false;

// Definisco n. led nella striscia
CRGB leds[NUM_LEDS];

void setup()
{ 
  //Imposto PIN FOTORESISTENZA  
  pinMode(FOTO_PIN, INPUT);
  //Inizializzo Seriale 
  Serial.begin (9600);
  Serial.println ("SCALA START");
  //Inizializzo Array di LED
  FastLED.addLeds<WS2811, DATA_PIN, BRG>(leds, NUM_LEDS);
}

void cambia_led(boolean on, int posizione)
{
    
  if (on)
    leds[posizione] = CRGB::White;
  else
    leds[posizione] = CRGB::Black;
  FastLED.show();
}
  
void cambia_striscia(boolean salita, boolean on, int tempo)
{
  int i,num;
  if (salita)
  {
    i=0;
    num=NUM_LEDS;
    for (i;i<num;i++)
     {
      cambia_led(on,i);
      delay(tempo);
     }
  }
  else
  {
    i=NUM_LEDS-1;
    num=0;
    for(i;i>=0;i--)
    {
      cambia_led(on,i);
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
    if (sign_PIR_BASSO==1 and sign_PIR_ALTO==0)
    {
      //STO SALENDO-ACCENDO LUCI IN SALITA
      if (mov_salita==false and mov_discesa==false)
      {    
        cambia_striscia(true, true, 100);
        mov_salita=true;
      }
      //SONO SCESO-SPENGO LUCI IN DISCESA
      if (mov_salita==false and mov_discesa==true)
      {    
        cambia_striscia(false, false, 100);
        mov_discesa=false;
      }
    }
    //RILEVO PIR BASSO E NON PIR ALTO  
    if (sign_PIR_BASSO==0 and sign_PIR_ALTO==1)
    {
      // SONO SALITO-SPENGO LUCI IN SALITA
      if (mov_salita==true and mov_discesa==false )
      {
        cambia_striscia(true, false, 100);
        mov_salita=false;
      }  
      // STO SCENDENDO-ACCENDO LUCI IN DISCESA
      if (mov_salita==false and mov_discesa==false )
      {
        cambia_striscia(false, true, 100);
        mov_discesa=true;
      }        
    }
  }
  else
  {
    Serial.println("LUCE RILEVATA SCALA OFF!!");
  }
}

boolean letturaLUCE()
{
  sign_LUCE=analogRead(FOTO_PIN);
  Serial.print("Presenza LUCE: "); // x leggere la fotoresistenza
  Serial.println(sign_LUCE);
  if (sign_LUCE>=sogliaBUIO)
    return false;
  else
    return true;
}

int READ_PIR(int pinPir){return digitalRead(pinPir);}
  

void read_BUTTON()
{
  sign_PULS=digitalRead(PULS_PIN);
  Serial.print("BUTTON: ");
  Serial.println(sign_PULS);
  if (sign_PULS==1 and scalaAccesa==true)
  {
    scalaAccesa=false;
    cambia_striscia(true, false, 0);
  }
  else if (sign_PULS==1 and scalaAccesa==false)
  {
    scalaAccesa=true;
    cambia_striscia(true, true, 0);
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
  read_BUTTON();
  presenzaLUCE=letturaLUCE();
  //statoSCALA();
  letturaPir();
  
  delay(400);


  /*  
  cambia_striscia(true, true, 250);
  cambia_striscia(true, false, 100);
  delay(500);
  cambia_striscia(false,true, 250);
  cambia_striscia(false, false, 100);
  */
}


