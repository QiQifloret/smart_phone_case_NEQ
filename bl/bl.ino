#include <SoftwareSerial.h>
SoftwareSerial mySerial(10, 11); // RX, TX

#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
  #include <avr/power.h>
#endif

#define PIN 6

Adafruit_NeoPixel strip = Adafruit_NeoPixel(2, PIN, NEO_GRB + NEO_KHZ800);

#include <Servo.h>
int servoPin = 5; 
Servo servo;   
int servoAngle = 0; 
unsigned long time;
int sum = 0;

char data = 0;            //Variable for storing received data
void setup()
{
    Serial.begin(9600);   //Sets the baud for serial data transmission 
     while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }


  Serial.println("Serial");

  // set the data rate for the SoftwareSerial port
  mySerial.begin(4800);
  mySerial.println("MySerial");                              
  pinMode(LED_BUILTIN, OUTPUT);  //Sets digital pin 13 as output pin
  
   // This is for Trinket 5V 16MHz, you can remove these three lines if you are not using a Trinket
  #if defined (__AVR_ATtiny85__)
    if (F_CPU == 16000000) clock_prescale_set(clock_div_1);
  #endif
  // End of trinket special code
  
 servo.attach(servoPin);

  strip.begin();
  strip.show(); // Initialize all pixels to 'off'
    
}
void loop()
{
   if(Serial.available() > 0)      // Send data only when you receive data:
   {
      data = Serial.read();        //Read the incoming data & store into data
      Serial.print(data);          //Print Value inside data in Serial monitor
      Serial.print("\n");
      
      mySerial.write(data);        
      if(data == '0')   {           // off
         strip.setBrightness(0);
         strip.show();
         delay(100);
        }
      if(data == '1') {        //  on
         strip.setBrightness(200);
         strip.show();
         delay(100);
      }
      if(data == '2'){
         
         strip.setPixelColor(0, strip.Color(255, 0, 0));
         strip.setPixelColor(1, strip.Color(255, 0, 0));
         strip.show();
         servo.write(100);
         delay(100);      
      }
      if(data == '3'){
         strip.setPixelColor(0, strip.Color(255, 255, 0));
         strip.setPixelColor(1, strip.Color(255, 255, 0));
         strip.show();
         servo.write(60);
         delay(100);      
      }
      if(data == '4'){
         strip.setPixelColor(0, strip.Color(0, 255, 0));
         strip.setPixelColor(1, strip.Color(0, 255, 0));
         strip.show();
         servo.write(20);
         delay(100);      
      }
   }
   if (mySerial.available()) {
    Serial.write(mySerial.read());
  }
}
