#include <SoftwareSerial.h>
#define RxD 2
#define TxD 3
#define LED 12
#define bee 6
SoftwareSerial blueToothSerial(RxD,TxD); 

int val=0;
int count=0;
int output;
boolean OnCar=false;
boolean NowState=true;

void setup()  
{
  pinMode(LED, OUTPUT);   
  pinMode(11, OUTPUT);
  pinMode(6, OUTPUT);
  Serial.begin(9600);  
  digitalWrite(11,HIGH);
  blueToothSerial.begin(9600);
}

void loop()
{
  output=digitalRead(7);
  val=analogRead(A0);
  if(output==1)
  {
        	 
           
           Serial.println(output);
              if(val>30)
              {
               count=0;
              // Serial.println(val); 
              }
              else
              {
               count++;
              }
         
        
           
          if(count>10)
          {
          	  OnCar=false;
          }
          else
          {
              OnCar=true;
          }
          
          if(NowState!=OnCar)
          {
            if(OnCar)
            {
              digitalWrite(LED, LOW);  
              blueToothSerial.write(3);
            }
             else
             {
              digitalWrite(LED, HIGH); 
               blueToothSerial.write(2);
              }
          
            
            NowState=OnCar;
          }
          delay(200);
          // blueToothSerial.write(2);
  }
  else
  {
    if(val>500)
    digitalWrite(bee, HIGH);
    else
     digitalWrite(bee, LOW);  
  }
  
}


