#!/usr/bin/python

import serial, string, datetime,time, re
import pyrebase

config = {
  "apiKey": "AIzaSyCSdmaI7jErihMER3VJtv4WMQ44mP0nEgE",
  "authDomain": "licenta-499a4.firebaseapp.com",
  "databaseURL": "https://licenta-499a4.firebaseio.com/",
  "storageBucket": "projectId.appspot.com"
}

firebase = pyrebase.initialize_app(config)
db = firebase.database()

data = {
}


output = " "
ser = serial.Serial('/dev/ttyACM0', 9600, 8, 'N', 1, timeout=1)

count=0
while True:
  print "----"
  if count==0:
      file=open("output.txt","a")
  while output != "":
    output = ser.readline()
    if "=" in output:
        file.write(str(datetime.datetime.now())+'\n')
        continue
    if "*C" in output:
        tmp=re.findall("\d+\.\d+", output)
        data["temperature"]=tmp[0]
        print ("temp",tmp[0])
        file.write(tmp[0]+'\n')
        continue
    if "%" in output:
        hmd=re.findall("\d+\.\d+", output)
        print ("hum",hmd[0])
        data["humidity"]=hmd[0]
        file.write(hmd[0]+'\n')
        continue
    if "Lux" in output:
        lux=re.findall("\d+\.\d+", output)
        print ("lux",lux[0])
        data["light"]=lux[0]
        file.write(lux[0]+'\n')
        data["timestamp"]=int(round(time.time() * 1000))
        db.child("users").child(str(datetime.datetime.now()).replace(":","-").replace(" ","-").replace(".","-")).set(data)
        continue
    count+=1
  if count>=50:
      file.close()
      count=0
  output = " "

#for i in range(0,256):
#  print unichr(i)
#  ser.write(chr(i))
#  time.sleep(1)