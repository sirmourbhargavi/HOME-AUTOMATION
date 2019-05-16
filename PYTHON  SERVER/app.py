from flask import Flask, request
import serial
import sys
import time
import apiai
import firebase as fb
import json
import requests


app = Flask(__name__)

ser = serial.Serial("COM3", 9600)

time.sleep(3)

application1 =  False

application2 =  False

@app.route('/app1/<status>', methods=["GET"])
def app1(status):
    if status == "true":
        status = True
    else:
        status = False

    try:

        if status:
            
            print("Status came True")
            p_v = fb.get_all()
            if not p_v["light1"] == status:
                ser.write("B".encode())
                fb.update("light1", True)

            return "ON"

        else:
            
            print("Status came False")
            p_v = fb.get_all()
            if not p_v["light1"] == status:
                ser.write("A".encode())
                fb.update("light1", False)

            return "OFF"

    except serial.SerialException as e:
        #There is no new data from serial port
        print(str(e))
        sys.exit(1)
    except TypeError as e:
        print(str(e))
        ser.close()
        sys.exit(1)


@app.route('/app2/<status>', methods=["GET"])
def app2(status):
    if status == "true":
        status = True
    else:
        status = False

    try:

        if status:
            
            p_v = fb.get_all()
            if not p_v["light2"] == status:
                ser.write("D".encode())
                fb.update("light2", True)

            return "ON"

        else:
            
            p_v = fb.get_all()
            if not p_v["light2"] == status:
                ser.write("C".encode())
                fb.update("light2", False)

            return "OFF"


    except serial.SerialException as e:
        #There is no new data from serial port
        print(str(e))
        sys.exit(1)
    except TypeError as e:
        print(str(e))
        ser.close()
        sys.exit(1)

@app.route('/app3/<text>', methods=["GET"])
def app3(text):
    print(text)
    ai = apiai.ApiAI("1a07551ce7a54835b19d319a2355d9a2")
    request = ai.text_request()
    request.query = text
    request.session = "laihfiaiwi"
    request.lang = 'en'
    response = request.getresponse()
    response = response.read()
    response = json.loads(response)
    print(response)

    params = response['result']['parameters']
    entity = params['lights']
    op = params['operation']
    if entity == "light 1":

            try:

                if op == "on":
                    status = True
                    print("Status came True")
                    p_v = fb.get_all()
                    if not p_v["light1"] == status:
                        ser.write("B".encode())
                        fb.update("light1", True)

                    return "ON"

                elif op == "off":
                    status = False
                    print("Status came False")
                    p_v = fb.get_all()
                    if not p_v["light1"] == status:
                        ser.write("A".encode())
                        fb.update("light1", False)

                    return "OFF"

            except serial.SerialException as e:
                #There is no new data from serial port
                print(str(e))
                sys.exit(1)
            except TypeError as e:
                print(str(e))
                ser.close()
                sys.exit(1)

    elif entity == "light 2":

            try:

                if op == "on":
                    status = True
                    p_v = fb.get_all()
                    if not p_v["light2"] == status:
                        ser.write("D".encode())
                        fb.update("light2", True)

                    return "ON"

                elif op == "off":
                    status = False
                    p_v = fb.get_all()
                    if not p_v["light2"] == status:
                        ser.write("C".encode())
                        fb.update("light2", False)

                    return "OFF"


            except serial.SerialException as e:
                #There is no new data from serial port
                print(str(e))
                sys.exit(1)
            except TypeError as e:
                print(str(e))
                ser.close()
                sys.exit(1)


if __name__ == "__main__":

    app.run(port=8000)

    

    
        

        
