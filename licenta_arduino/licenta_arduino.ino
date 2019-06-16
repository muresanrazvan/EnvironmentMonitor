#include <SimpleDHT.h>
#include<Wire.h>
#include<Adafruit_SGP30.h>

// Air quality sensor, using I2C Bus
Adafruit_SGP30 sgp;
// MAX44009 light sensor, I2C bus address is 0x4A(74)
#define Addr 0x4A 
// DHT22, VCC: 5V, GND: GND, DATA: 4
int pinDHT22 = 4;
// Sound sensor, VCC: 3.3V, GND: GND, DATA: A3
int pinSound = A3;
// Humidity Sensor
SimpleDHT22 dht22;

float sumSound=0;
float sumTVOC=0;
float sumECO2=0;
float sumLight=0;
float sumTemperature=0;
float sumHumidity=0;
int loopCounter=0;
int sgpCounter=0;

int loopIterations=30;

void setup() {
  delay(3000);
  if (! sgp.begin()){
    Serial.println("SGP Sensor not found");
    while (1);
  }
  // Set the analog sound detector pin to input
  pinMode(pinSound,INPUT);
    // Initialize I2C communication as MASTER
  Wire.begin();
  // Initialize serial communication, set baud rate = 115200
  Serial.begin(115200);
  // Start I2C Transmission
  Wire.beginTransmission(Addr);
  // Select configuration register
  Wire.write(0x02);
  // Manual mode, Integration time = 800 ms
  Wire.write(0x40);
  // Stop I2C transmission
  Wire.endTransmission();
  delay(3000);
}

float getLight(){
  unsigned int data[2];
  // Start I2C Transmission
  Wire.beginTransmission(Addr);
  // Select data register
  Wire.write(0x03);
  // Stop I2C transmission
  Wire.endTransmission();
  // Request 2 bytes of data
  Wire.requestFrom(Addr, 2);
  // Read 2 bytes of data
  if (Wire.available() == 2){
    data[0] = Wire.read(); // luminance high byte register
    data[1] = Wire.read(); // luminance low byte register
  }
  // Convert the data to lux
  int exponent = (data[0] & 0xF0) >> 4;
  int mantissa = ((data[0] & 0x0F) << 4) | (data[1] & 0x0F);
  float luminance = pow(2, exponent) * mantissa * 0.045;
  return luminance;
}

double getSoundLevel()
{
  float dBAnalogQuiet = 10; // calibrated value from analog input (48 dB equivalent)
  float dBAnalogMedium = 11;
  float dBAnalogLoud = 13;
  float decibelsValueQuiet = 49;
  float decibelsValueMedium = 65;
  float decibelsValueLoud = 70;

  int value=0;
  value = analogRead(pinSound);

  if (value < 10){
    decibelsValueQuiet += 30 * log10(value/dBAnalogQuiet);
    return decibelsValueQuiet;
  }
  else if ((value > 10) && ( value <= 19) ){
    decibelsValueMedium += 20*log10(value/dBAnalogMedium);
    return decibelsValueMedium;
  }
  else if(value > 19){
    decibelsValueLoud += 14*log10(value/dBAnalogLoud);
    return decibelsValueLoud;
  }
  return 0;
}

uint32_t getAbsoluteHumidity(float temperature, float humidity) {
    // approximation formula from Sensirion SGP30 Driver Integration chapter 3.15
    const float absoluteHumidity = 216.7f * ((humidity / 100.0f) * 6.112f *exp((17.62f * temperature) / (243.12f + temperature)) / 
    (273.15f + temperature)); // [g/m^3]
    const uint32_t absoluteHumidityScaled = static_cast<uint32_t>(1000.0f * absoluteHumidity); // [mg/m^3]
    return absoluteHumidityScaled;
}

void loop() {
  if (! sgp.IAQmeasure()) {
    Serial.println(" Air quality measurement failed");
    return;
  }
  sumTVOC+=sgp.TVOC;
  sumECO2+=sgp.eCO2;

  sgpCounter++;
  if (sgpCounter == 30) {
    sgpCounter = 0;

    uint16_t TVOC_base, eCO2_base;
    if (! sgp.getIAQBaseline(&eCO2_base, &TVOC_base)) {
      Serial.println("Failed to get baseline readings");
      return;
    }
  }
  float temperature = 0;
  float humidity = 0;
  int err = SimpleDHTErrSuccess;
  if ((err = dht22.read2(pinDHT22, &temperature, &humidity, NULL)) != SimpleDHTErrSuccess) {
    Serial.print("Read DHT22 failed, err="); Serial.println(err);delay(2000);
    return;
  }
  sumSound+=getSoundLevel();
  sumLight+=getLight();
  sumTemperature+=(float)temperature;
  sumHumidity+=(float)humidity;
  loopCounter++;
  delay(1000);
  if(loopCounter>=loopIterations){
    Serial.println("=================================");
    loopCounter=0;
  
    Serial.print(sumTemperature/loopIterations); Serial.println(" *C, ");
    Serial.print(sumHumidity/loopIterations); Serial.println(" RH%");
    Serial.print(sumLight/loopIterations); Serial.println(" Lux");
    Serial.print(sumSound/loopIterations); Serial.println(" dB");
    Serial.print(sumTVOC/loopIterations); Serial.println(" ppb");
    Serial.print(sumECO2/loopIterations); Serial.println(" ppm");

    sgp.setHumidity(getAbsoluteHumidity(sumTemperature/loopIterations, sumHumidity/loopIterations));
    sumSound=0;
    sumTVOC=0;
    sumECO2=0;
    sumLight=0;
    sumTemperature=0;
    sumHumidity=0;
    // DHT22 sampling rate is 0.5HZ.
  }
  
}
