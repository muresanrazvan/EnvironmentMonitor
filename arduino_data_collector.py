#!/usr/bin/python

import serial, string, datetime,time, re
import pyrebase
import urllib2

config = {
  "apiKey": "AIzaSyCSdmaI7jErihMER3VJtv4WMQ44mP0nEgE",
  "authDomain": "licenta-499a4.firebaseapp.com",
  "databaseURL": "https://licenta-499a4.firebaseio.com/",
  "storageBucket": "projectId.appspot.com"
}

firebase = pyrebase.initialize_app(config)
db = firebase.database()


waiting_data=[]

def connection_check():
    try:
        urllib2.urlopen("https://www.google.com/")
        return True
    except:
        return False

def send_to_firebase(data,last_datetime):
    if connection_check():
        if len(waiting_data)>0:
            while(len(waiting_data)>0):
                tmp_data=waiting_data.pop(0)
                db.child("params")
				.child(tmp_data["last_datetime"]
				.replace(":","-")
				.replace(" ","-")
				.replace(".","-"))
				.set(tmp_data["data"])
                print("uploading waiting data" + str(tmp_data["data"]))
        db.child("params")
		.child(last_datetime.replace(":","-").replace(" ","-").replace(".","-"))
		.set(data)
    else:
        print("failed to upload")
        waiting_data.append({"data":data,"last_datetime":last_datetime})

output = " "
ser = serial.Serial('/dev/ttyACM0', 9600, 8, 'N', 1, timeout=1)

count=0
while True:
  data={}
  print("----")
  last_datetime=str(datetime.datetime.now())
  timestamp=int(round(time.time() * 1000)) 
  if count==0:
      file=open("output.txt","a")
  while output != "":
    output = ser.readline()
    if "=" in output:
        file.write('\n')
        file.write(last_datetime+'\n')
        file.write("timestamp="+str(timestamp)+'\n')
    if "*C" in output:
        tmp=re.findall("\d+\.\d+", output)
        data["temperature"]=tmp[0]
        print ("temp",tmp[0])
        file.write("tmp="+tmp[0]+'\n')
    if "%" in output:
        hmd=re.findall("\d+\.\d+", output)
        print ("hum",hmd[0])
        data["humidity"]=hmd[0]
        file.write("hmd="+hmd[0]+'\n')
    if "Lux" in output:
        lux=re.findall("\d+\.\d+", output)
        print ("lux",lux[0])
        data["light"]=lux[0]
        file.write("lux="+lux[0]+'\n')
    if "dB" in output:
        dBs=re.findall("\d+\.\d+", output)
        print ("dBs",dBs[0])
        data["noise"]=dBs[0]
        file.write("dbs="+dBs[0]+'\n')
    if "ppb" in output:
        tvoc=re.findall("\d+\.\d+", output)
        print ("ppb",tvoc[0])
        data["TVOC"]=tvoc[0]
        file.write("tvoc="+tvoc[0]+'\n')
    if "ppm" in output:
        eco2=re.findall("\d+\.\d+", output)
        print ("ppm",eco2[0])
        data["eCO2"]=eco2[0]
        file.write("eco2="+eco2[0]+'\n')
        data["timestamp"]=timestamp
        send_to_firebase(data,last_datetime)
        continue
  count+=1
  if count>=50:
      print("flushed to file")
      file.close()
      count=0
  output = " "

#for i in range(0,256):
#  print unichr(i)
#  ser.write(chr(i))
#  time.sleep(1)
