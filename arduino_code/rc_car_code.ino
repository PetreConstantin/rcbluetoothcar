#include<SoftwareSerial.h> //necessary library for bluetooth TX and RX pins
SoftwareSerial hcSerial(2,3); //rx,tx

char command; // comand received from bluetooth module

void setup() {

  // bluetooth pins



  //pins for the forward/reverse DC Motor
  pinMode(13, OUTPUT); //forward
  pinMode(12, OUTPUT); //reverse


  // pins for the left/right DC Motor
  pinMode(11, OUTPUT); //left
  pinMode(10, OUTPUT); //right


  //pins for LEDS

  pinMode(9, OUTPUT); // FRONT LEDS
  pinMode(8, OUTPUT); // BACK LEDS


  delay(500);
 


  Serial.begin(9600);
  hcSerial.begin(9600);



}

void loop() {

  if (hcSerial.available())
  {
    command = hcSerial.read();
    hcSerial.write(command);
  }

  switch (command)
  {
    case 'F':
      {
        digitalWrite(13, HIGH);
        digitalWrite(12,LOW);
        delay(90);
        command = 's';
        break;
      }
    case 'B':
      {
        digitalWrite(12, HIGH);
        digitalWrite(13,LOW);
        delay(90);
        command = 's';
        break;
      }
    case 'R':
      {
        digitalWrite(10, HIGH);
        digitalWrite(11,LOW);
        delay(25);
        command = 's';
        break;
      }
    case 'L':
      {
        digitalWrite(11, HIGH);
        digitalWrite(10,LOW);
        delay(25);
        command = 's';
        break;
      }
    case 'u':
      {
        digitalWrite(9, HIGH); // front leds on
        break;
      }
    case 'i':
      {
        digitalWrite(9, LOW); //front leds off
        break;
      }
    case 'j':
      {
        digitalWrite(8, HIGH); //back leds on
        break;
      }
    case 'k':
      {
        digitalWrite(8, LOW); //back leds off
        break;
      }
      case 's':
      {
         digitalWrite(10,LOW); 
         digitalWrite(11,LOW); 
         digitalWrite(12,LOW); 
         digitalWrite(13,LOW); 
        break;
      }
      delay(100);
      

  }





}
