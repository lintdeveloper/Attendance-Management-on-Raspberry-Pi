import tkinter as tk
import socket
import time
import threading
import queue
import json
import sys
import serial
import select
import ast
import sqlite3
import os
import hashlib
from pyfingerprint.pyfingerprint import PyFingerprint
import RPi.GPIO as GPIO

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.setup(21, GPIO.OUT, initial=GPIO.LOW);
GPIO.setup(20, GPIO.OUT, initial=GPIO.LOW);
GPIO.setup(16, GPIO.OUT, initial=GPIO.LOW);

enroll_page_thread = 1
ats_connection_thread = 1
at_record_thread = 1
at_log_thread = 1
connection_exists = False
current_cd_time = None;


class initPage:
    def __init__(self, master):
        self.master = master
        self.firstFrame = tk.Frame(root, width=600, height=50, bg='#8B008B')
        self.firstFrame.pack(fill='x')
        self.firstFrame.tkraise()
        self.headerLabel = tk.Label(self.firstFrame, fg='#eaeaea', bg='#8B008B',
                                    text='FUTMINNA | Attendance Management System', font=('Calibri', 15))
        self.headerLabel.pack()

        self.topFrame = tk.Frame(root, background='#630063', width=600, height=100)
        self.topFrame.pack(fill='x')

        self.informationLabel = tk.Label(self.topFrame, fg='#ffffff', bg='#630063', text="Welcome, Please Choose Mode:",
                                         font=('Arial', 40))
        self.informationLabel.pack(pady=50, padx=10)

        # Screen is taken to the homepage and a threads starts running
        self.startAttendanceSystemButton = tk.Button(self.topFrame, text="Attendance System", width="15",
                                                     font=('Arial', 50), bg='yellow',
                                                     padx=20, command=self.homePage)
        self.startAttendanceSystemButton.pack(pady=10)

        # Sends attendance system log
        self.sendAttendanceSystemLog = tk.Button(self.topFrame, text="Attendance Log", width="15", font=('Arial', 50),
                                                 bg='#8d6e63', padx=20, command=self.attendanceLogPage)
        self.sendAttendanceSystemLog.pack(pady=10)
        self.exitFrame = tk.Frame(root)

        # End running program then Shuts down the raspberry Pi
        self.exitButton = tk.Button(self.exitFrame, text="Power Off", fg="#ffffff", bg="#B71C1C",
                                    command=self.exitThenShutDown,
                                    font=('Arial', 40))
        self.exitButton.pack(side='left')
        self.exitFrame.pack(side='bottom')

#### End running programs and then use the os module to shutdown
    def exitThenShutDown(self):
        endRunningProgNdVariables()
        os.system("sudo shutdown -h now")

    def homePage(self):
        global client
        self.firstFrame.destroy()
        self.topFrame.destroy()
        self.exitFrame.destroy()
        client = ThreadedClient(root)

    def attendanceLogPage(self):
        global client
        self.firstFrame.destroy()
        self.topFrame.destroy()
        self.exitFrame.destroy()
        client = ATLogGui(root)
class ATLogGui:
    def __init__(self, root):
        self.deleteTries = 0;
        self.firstFrame = tk.Frame(root, width=600, height=50, bg='#8B008B')
        self.firstFrame.pack(fill='x')
        self.headerLabel = tk.Label(self.firstFrame, fg='#ffffff', bg='#8B008B',
                                    text='FUTMINNA | Attendance Management System', font=('Calibri', 15))
        self.headerLabel.pack()
        self.noData = True;
        self.secondFrame = tk.Frame(root, bg="#ffffff")
        self.secondFrame.grid_rowconfigure(0, weight=1)
        self.secondFrame.grid_columnconfigure(0, weight=1)
        self.secondFrame.tkraise()
        self.secondFrame.pack(side="top", fill='x', ipady="50", expand=True)
        self.log = self.loadAttendanceLog()
        self.mainLabel = tk.Label(self.secondFrame, bg="#ffffff", text=self.log, font=('Calibri', 30), wraplength=800,
                                  padx="10", pady="50")
        self.mainLabel.pack()

        self.thirdFrame = tk.Frame(root, bg="#ffffff", )
        self.thirdFrame.pack(side="bottom", fill="both", expand=True)
        self.saveButton = tk.Button(self.secondFrame, text="Send", bg="#2e7d32", fg="#ffffff", font=('Arial', 50),
                                    width="10", padx=5, pady=10, command=self.send)
        self.deleteButton = tk.Button(self.secondFrame, text="Delete", bg="#b71c1c", fg="#ffffff", font=('Arial', 50),
                                      width="10", padx=5, pady=10, command=self.delete)

        self.saveButton.pack(side="left")
        self.deleteButton.pack(side="right")
        # self.confirm(self.noData)
        self.backButton = tk.Button(self.thirdFrame, text="< Back", bg="#8B008B", fg="#ffffff", pady=10,
                                    font=('Arial', 50), width="15",
                                    command=self.returnToInitPage).pack(fill="x")

    def returnToInitPage(self):
        self.firstFrame.destroy()
        self.secondFrame.destroy()
        self.thirdFrame.destroy()
        global client
        self.deleteTries = 0;
        client = initPage(root);

    def confirm(self, cnd):
        """
        if cnd is False:
            self.saveButton.pack(side="left")
            self.deleteButton.pack(side="right")
        """

    def loadAttendanceLog(self):
        text = "No Record Found";
        conn = sqlite3.connect("attendance.db")
        select_sql = "SELECT DISTINCT course_id, week_number FROM LECTURE_ATTENDANCE";
        val = conn.execute(select_sql)
        conn.commit()
        for cw in val.fetchall():
            text += str(cw[0]) + " W" + str(cw[1]) + ", ";
        text = text[15:-2];
        if len(text) < 1:
            text = "No Record Found";
            self.noData = True
        else:
            self.noData = False

        text = limitateText(text)
        return text;

    def send(self):
        if not self.noData:
            if checkConnection():
                self.mainLabel.config(fg="green", text="Sending...\n")
                if sendAttendanceLog():
                    self.mainLabel.config(text="Attendance Log Sent..\n")
                else:
                    self.mainLabel.config(text="Error Sending Attendance Log, \nClient Response Error!")
            else:
                self.mainLabel.config(text="Error Sending Attendance Log,\nNot Connected to Client!", fg="red",
                                      wraplength=800)
            self.mainLabel.update()
            time.sleep(1)
            self.mainLabel.config(fg="black", text=self.log)
        else:
            self.mainLabel.config(fg="red", text="Sorry no data to send\n")
            self.mainLabel.update()
            time.sleep(1)
            self.mainLabel.config(fg="black", text="No Record Found\n")

    def delete(self):
        if not self.noData:
            self.deleteTries += 1;
            if self.deleteTries == 2:
                deleteSentData()
                self.deleteTries = 0
                self.mainLabel.config(fg="red", text="Attendance Log deleted\n")

            else:
                self.mainLabel.config(fg="red",
                                      text="Are you sure you want to\n delete attendance log?, if yes press delete again")
            self.mainLabel.update()
            time.sleep(1)
            self.mainLabel.config(fg="black", text=self.loadAttendanceLog())
        else:
            self.mainLabel.config(fg="red", text="Sorry No data to delete\n")
            self.mainLabel.update()
            time.sleep(1)
            self.mainLabel.config(fg="black", text="No Record Found\n")

        # SELECT FROM DATA AND GET THE LIST OF ATTENDANCE COURSE AND THEIR WEEEK NUMBERS
class ATSystemGui:
    def __init__(self, master, queue):
        self.master = master
        self.queue = queue
        self.firstFrame = tk.Frame(root, width=600, height=50, bg='#8B008B')
        self.firstFrame.pack(fill='x')
        self.firstFrame.tkraise()
        self.headerLabel = tk.Label(self.firstFrame, fg='#eaeaea', bg='#8B008B',
                                    text='FUTMINNA | Attendance Management System', font=('Calibri', 15))
        self.headerLabel.pack()

        self.topFrame = tk.Frame(root, background='#630063', width=600, height=100)
        self.topFrame.pack(fill='x')

        self.serverConnectionLabel = tk.Label(self.topFrame, fg='#ffffff', bg='#630063', text="Waiting for Client..",
                                              font=('Arial', 40))
        self.serverConnectionLabel.pack(pady=50, padx=10)
        self.startAttendanceSystemButton = tk.Button(self.topFrame, text="Start Attendance", width="15",
                                                     font=('Arial', 50), bg='#32CD32',
                                                     padx=20, command=self.attendancePage)
        self.startAttendanceSystemButton.pack(pady=10)
        self.sendAttendanceSystemLog = tk.Button(self.topFrame, text="Enroll Fingerprint", width="15",
                                                 font=('Arial', 50),
                                                 bg='#6495ED', padx=20, command=self.fingerprintRecording)
        self.sendAttendanceSystemLog.pack(pady=10)
        self.thirdFrame = tk.Frame(root)
        self.backButton = tk.Button(self.thirdFrame, text="< Back", fg="#ffffff", bg="#8B008B",
                                    command=self.moveBackToInitPage,
                                    font=('Arial', 40))
        self.backButton.pack(side='left')
        self.thirdFrame.pack(side='bottom')
        """
        self.queue = queue
        self.firstFrame = tk.Frame(root, width=600, height=10, bg='#8B008B')
        self.firstFrame.pack(fill='x')
        self.firstFrame.tkraise()
        self.headerLabel = tk.Label(self.firstFrame, fg='#eaeaea', bg='#8B008B',
                                    text='FUTMINNA | Attendance Management System', font=('Calibri', 15))
        self.headerLabel.pack()

        self.topFrame = tk.Frame(root, background='#630063', width=600, height=100)
        self.topFrame.pack(fill='x')

        self.serverConnectionLabel = tk.Label(self.topFrame, fg='#ffffff', bg='#630063', text="Waiting for Client..",
                                              font=('Arial', 40))
        self.serverConnectionLabel.pack(pady=30, padx=10)
        self.startAttendanceButton = tk.Button(self.topFrame, text="Start Attendance", font=('Arial', 50), bg='#32CD32',
                                               padx=10, command=self.attendancePage)
        self.enrollFingerprintButton = tk.Button(self.topFrame, text="Enroll Fingerprint", font=('Arial', 50),
                                                 bg='#6495ED', padx=10, command=self.fingerprintRecording)
        self.startAttendanceButton.pack(pady=30, padx=10)
        self.enrollFingerprintButton.pack(pady=30, padx=10)
        self.thirdFrame = tk.Frame(root)
        self.thirdFrame.pack(side='bottom')
        self.backButton = tk.Button(self.thirdFrame, text="< Back", fg="#ffffff", bg="#8B008B", command=self.moveBackToInitPage,
                                    font=('Arial', 40))
        self.backButton.pack(side='left')
        """
        #

    def moveBackToInitPage(self):
        print("back pressed")
        self.endConnectionSearchThread()
        self.firstFrame.destroy()
        self.topFrame.destroy()
        self.thirdFrame.destroy()
        global client
        client = initPage(root)

    def endConnectionSearchThread(self):
        connection_thread = 0

    def attendancePage(self):
        global client
        self.firstFrame.destroy()
        self.thirdFrame.destroy()
        client = ThreadedClientAtt(root, self.topFrame)

    def fingerprintRecording(self):
        global client
        self.firstFrame.destroy()
        self.thirdFrame.destroy()
        client = ThreadedClientFingerprintRecord(root, self.topFrame)

    def processIncoming(self):
        """Handle all messages currently in the queue, if any."""
        while self.queue.qsize():
            try:
                msg = self.queue.get(0)
                self.serverConnectionLabel.config(text=msg)
                # if msg == "Connection Sucessful":
                #    self.startAttendanceButton.pack(pady=30, padx=10)
                #    self.enrollFingerprintButton.pack(pady=30, padx=10)

            except:
                pass
class ATRecordGui:
    def __init__(self, master, queue, tQueue, lQueue, endCommand, topFrame):
        self.tQueue = tQueue
        self.lQueue = lQueue
        self.queue = queue
        self.topFrame = topFrame
        self.topFrame.destroy()
        self.firstFrame = tk.Frame(root, width=600, height=50, bg='#8B008B')
        self.firstFrame.pack(fill='x')
        self.headerLabel = tk.Label(self.firstFrame, fg='#eaeaea', bg='#8B008B',
                                    text='FUTMINNA | Attendance Management System', font=('Calibri', 15))
        self.headerLabel.pack()
        self.secondFrame = tk.Frame(root)
        self.courseCodeLabel = tk.Label(self.secondFrame, text="Lecture Course Code | week No | Title",
                                        font=('Calibri', 20))
        self.courseCodeLabel.pack(pady=10)

        self.countdownTimer = tk.Label(self.secondFrame, text="00:00:00", font=('Calibri', 30), fg='red')
        self.countdownTimer.pack(pady=10)

        self.fingerprintLabel = tk.Label(self.secondFrame, text="Waiting for Fingerprint Data", wraplength=800,
                                         fg='#630063', font=('Arial', 45))
        self.fingerprintLabel.pack(pady=10)

        # New code : adding fingerprint image.
        self.photo = tk.PhotoImage(file='fingerprint.png')
        self.photo = self.photo.subsample(6, 6)
        self.imageLabel = tk.Label(self.secondFrame, image=self.photo)
        self.imageLabel.pack(pady=20)
        # End of new Code

        self.secondFrame.pack(side="top", fill="both", expand=True)
        self.secondFrame.grid_rowconfigure(0, weight=1)
        self.secondFrame.grid_columnconfigure(0, weight=1)
        self.secondFrame.tkraise()

        self.thirdFrame = tk.Frame(root)
        self.thirdFrame.pack(side='bottom')
        self.backButton = tk.Button(self.thirdFrame, text="< Back", bg="#8B008B", fg="#ffffff",
                                    command=self.returnToAtsPage,
                                    font=('Arial', 40))
        self.backButton.pack(side='left')
        #

    def returnToAtsPage(self):
        print("back pressed")
        self.endApplication()
        self.firstFrame.destroy()
        self.topFrame.destroy()
        self.secondFrame.destroy()
        self.thirdFrame.destroy()
        global client
        client = ThreadedClient(root)

    def endApplication(self):
        global at_record_thread
        at_record_thread = 0;

    def exitThenShutDown(self):
        os.system("sudo shutdown -h now")

    def processCounter(self):
        while self.tQueue.qsize():
            try:
                counter = self.tQueue.get(0)
                self.countdownTimer.config(text=counter)
                # self.oneLabel.config(text=msg)

            except:
                pass

    def processIncoming(self):
        """Handle all messages currently in the queue, if any."""
        while self.queue.qsize():
            try:
                msg = self.queue.get(0)
                self.fingerprintLabel.config(text=msg)
                title = self.lQueue.get(0)
                self.courseCodeLabel.config(text=title)
                # self.oneLabel.config(text=msg)

            except:
                # just on general principles, although we don't
                # expect this branch to be taken in this case
                pass
class FingerprintRecordGui:
    def __init__(self, master, queue, topFrame):
        self.queue = queue
        self.topFrame = topFrame
        self.topFrame.destroy()
        self.firstFrame = tk.Frame(root, width=600, height=50, bg='#8B008B')
        self.firstFrame.pack(fill='x')
        self.firstFrame.tkraise()
        self.headerLabel = tk.Label(self.firstFrame, fg='#eaeaea', bg='#8B008B',
                                    text='FUTMINNA | Attendance Management System', font=('Calibri', 15))

        self.headerLabel.pack()
        self.secondFrame = tk.Frame(root)
        self.secondFrame.pack(side="top", fill="both", expand=True)
        self.secondFrame.grid_rowconfigure(0, weight=1)
        self.secondFrame.grid_columnconfigure(0, weight=1)
        self.secondFrame.tkraise()
        if checkConnection():
            self.conLabel = tk.Label(self.secondFrame, fg="#8B008B", text="Record Fingerprint for User",
                                     font=('Arial', 20))
            self.conLabel.pack(pady=10)
            self.fingerprintLabel = tk.Label(self.secondFrame, text="Waiting for Fingerprint", wraplength=800,
                                             font=('Arial', 40))
            self.fingerprintLabel.pack(pady=10)

            # New code : adding fingerprint image.
            self.photo = tk.PhotoImage(file='fingerprint.png')
            self.photo = self.photo.subsample(6, 6)
            self.imageLabel = tk.Label(self.secondFrame, image=self.photo)
            self.imageLabel.pack(pady=20)
            # End of new Code
        else:
            self.conLabel = tk.Label(self.secondFrame, fg='red', text="Not Connected!. Press Back",
                                     font=('Arial', 40))
            self.conLabel.pack(pady=40)

        self.thirdFrame = tk.Frame(root)
        self.thirdFrame.pack(side='bottom')
        self.backButton = tk.Button(self.thirdFrame, text="< Back", bg="#8B008B", fg="#ffffff",
                                    command=self.returnToAtsPage, font=('Arial', 40))
        self.backButton.pack(side='left')
        #

    def returnToAtsPage(self):
        print("back pressed")
        self.endApplication()
        self.firstFrame.destroy()
        self.topFrame.destroy()
        self.secondFrame.destroy()
        self.thirdFrame.destroy()
        global client
        client = ThreadedClient(root)

    def endApplication(self):
        global enroll_page_thread
        enroll_page_thread = 0;

    def processIncoming(self):
        """Handle all messages currently in the queue, if any."""
        while self.queue.qsize():
            try:
                msg = self.queue.get(0)
                # Check contents of message and do whatever is needed. As a
                # simple test, print it (in real life, you would
                # suitably update the GUI's display in a richer fashion).
                print(msg, "\n")
                self.fingerprintLabel.config(text=msg)
                # self.oneLabel.config(text=msg)

            except:  # queue.Empty:
                # just on general principles, although we don't
                # expect this branch to be taken in this case
                pass


class ThreadedClient:
    def __init__(self, master):
        self.master = master

        # Create the queue
        self.queue = queue.Queue()

        # Set up the GUI part
        self.gui = ATSystemGui(master, self.queue)

        self.thread1 = threading.Thread(target=self.workerThread1)
        self.thread1.start()
        self.periodicCall()

    def periodicCall(self):
        """
        Check every 200 ms if there is something new in the queue.
        """
        self.gui.processIncoming()
        self.master.after(200, self.periodicCall)

    def workerThread1(self):
        global connection_exists
        self.deleteFingerprints()
        if connection_exists:
            self.queue.put("Connection Sucessful")
        else:
            while not connection_exists:
                s.listen()
                try:
                    global con, addr
                    con, addr = s.accept()
                    self.queue.put("Connection Sucessful")
                    connection_exists = True
                    break;
                except Exception as e:
                    pass

    def deleteFingerprints(self):
        try:
            f = PyFingerprint('/dev/ttyUSB1', 57600, 0xFFFFFFFF, 0x00000000)
            for x in range(f.getTemplateCount()):
                # f.deleteTemplate(int(x-1))
                if (f.deleteTemplate(x) == True):
                    print('Template deleted!')
        except Exception as e:
            print('Operation failed!')
            print('Exception message: ' + str(e))
            pass
class ThreadedClientAtt:

    def __init__(self, master, oldFrame):
        global at_record_thread
        self.master = master
        self.queue = queue.Queue()

        # countdownQueue
        self.tQueue = queue.Queue()

        # lecture title Queue
        self.lQueue = queue.Queue()

        # Set up the GUI part
        self.gui = ATRecordGui(master, self.queue, self.tQueue, self.lQueue, self.endApplication, oldFrame)

        at_record_thread = 1
        self.thread2 = threading.Thread(target=self.workerThreadReceiveFingerprint, daemon=True)
        self.thread2.start()
        self.thread2.join()
        ##
        self.thread1 = threading.Thread(target=self.workerThread1, daemon=True)
        self.thread1.start()

        # Start the periodic call in the GUI to check if the queue contains
        # anything
        self.pdc()
        self.periodicCall()

    def pdc(self):
        self.gui.processCounter()
        self.master.after(200, self.pdc)

    def periodicCall(self):
        global at_record_thread
        self.gui.processIncoming()
        if not at_record_thread:
            self.queue = None;
            self.tQueue = None;
            self.lQueue = None;

        self.master.after(200, self.periodicCall)

    def workerThread1(self):
        global at_record_thread
        while at_record_thread:
            searchFingerprint(self.queue)
            time.sleep(3);

    def workerThreadReceiveFingerprint(self):
        global at_record_thread
        total = b''
        try:
            socket_list = [sys.stdin, con]
            read_sockets, write_sockets, error_sockets = select.select(socket_list, [], [])
            for sock in read_sockets:
                if sock == con:
                    while True:
                        data = con.recv(20002)
                        total += data;
                        if "lecture_details" in data.decode("UTF-8") and "]}" in data.decode("UTF-8"):
                            break;
                with open("newfinger.json", 'wb') as f:
                    newdata = total.replace(b"\0", b"");
                    f.write(newdata);
                    f.close()
        except Exception as e:
            pass

        with open('newfinger.json', 'r') as f:
            for line in f:
                if line.strip():
                    file = json.loads(line)

        courseTitle = file["lecture_details"][0]
        weekNo = file["lecture_details"][1]
        courseCode = file["lecture_details"][2]
        secs = int(3600 * file["lecture_details"][3])
        ltitle = courseCode + " | Week No: " + weekNo + " " + courseTitle
        self.lQueue.put(ltitle)
        for jsn in file["fingerprints"]:
            self.LoadFingerprintIntoModule(jsn[1])
            #print(jsn[1])
        self.thread3 = threading.Thread(target=self.countTimer, args=(secs, self.tQueue,), daemon=True)
        self.thread3.start()

    def LoadFingerprintIntoModule(self, inputData):
        try:
            f = PyFingerprint('/dev/ttyUSB1', 57600, 0xFFFFFFFF, 0x00000000)
            if (inputData != "null"):
                inputData = ast.literal_eval(inputData)
                f.uploadCharacteristics(0x01, inputData)
                f.storeTemplate()
            if (f.verifyPassword() == False):
                raise ValueError('The given fingerprint sensor password is wrong!')
        except Exception as e:
            pass

    def deleteFingerprints(self, deletePosition):
        try:
            f = PyFingerprint('/dev/ttyUSB1', 57600, 0xFFFFFFFF, 0x00000000)
            for x in range(f.getTemplateCount()):
                # f.deleteTemplate(int(x-1))
                if (f.deleteTemplate(x) == True):
                    print('Template deleted!')
        except Exception as e:
            print('Operation failed!')
            print('Exception message: ' + str(e))
            pass

    def countTimer(self, secs, qu):
        countdown(secs, qu)
class ThreadedClientFingerprintRecord:
    def __init__(self, master, oldFrame):
        global enroll_page_thread;
        self.master = master

        # Create the queue
        self.queue = queue.Queue()
        self.gui = FingerprintRecordGui(master, self.queue, oldFrame)
        enroll_page_thread = 1;
        # self.running = 1
        self.thread1 = threading.Thread(target=self.workerThread1, daemon=True)
        self.thread1.start()

        self.periodicCall()

    def periodicCall(self):
        global enroll_page_thread
        self.gui.processIncoming()
        if not enroll_page_thread:
            self.queue = None
            # import sys
            # sys.exit(1)
        self.master.after(200, self.periodicCall)

    def workerThread1(self):
        global enroll_page_thread, connection_exists
        while enroll_page_thread and connection_exists:
            enrollFingerprintAndSend(self.queue)
            time.sleep(1);
        print("Thread Stopped")


def sendAttendanceLog():
    queryLogs = generateQueriesFromLog()
    try:
        for sql in queryLogs:
            time.sleep(0.200)
            con.send(bytes(sql + "\n", "UTF-8"))
            updateAttendanceRecord(sql[114:130]);
        return True
    except Exception as e:
        print(e)
        return False
# ATTENDANCE COUNTDOWN FUNCTION
def countdown(t, timeQueue):
    global at_record_thread, current_cd_time
    if current_cd_time is not None:
        t = current_cd_time

    while t:
        hours = 00;
        mins, secs = divmod(t, 60)
        if mins >= 60:
            hours, extramins = divmod(mins, 60)
            mins = extramins
        timeformat = '{:02d}:{:02d}:{:02d}'.format(hours, mins, secs)
        timeQueue.put(timeformat)
        time.sleep(1)
        t -= 1;
        current_cd_time = t;
    os.system("sudo shutdown -h now")
# Fingerprint Functions below :
def endRunningProgNdVariables():
    global enroll_page_thread, ats_connection_thread, at_record_thread, at_log_thread, connection_exists, current_cd_time
    enroll_page_thread = 0
    ats_connection_thread = 0
    at_record_thread = 0
    at_log_thread = 0
    connection_exists = False
    current_cd_time = None;
    try:
        con.close()
    except:
        pass
def searchFingerprint(queue):
    queryRFID(queue)
    tries = 0;
    while tries < 3:
        tries += 1;
        try:
            f = PyFingerprint('/dev/ttyUSB1', 57600, 0xFFFFFFFF, 0x00000000)

            if (f.verifyPassword() == False):
                raise ValueError('The given fingerprint sensor password is wrong!')

        except Exception as e:
            # print('The fingerprint sensor could not be initialized!')
            # print('Exception message: ' + str(e))
            os.system("sudo shutdown -r now")
            # exit(1)

        ## Gets some sensor information
        # print('Currently used templates: ' + str(f.getTemplateCount()) +'/'+ str(f.getStorageCapacity()))

        ## Tries to search the finger and calculate hash
        try:
            print('Waiting for finger...')
            queue.put('Waiting for finger...')

            ## Wait that finger is read
            while (f.readImage() == False):
                pass

            ## Converts read image to characteristics and stores it in charbuffer 1
            f.convertImage(0x01)

            ## Searchs template
            result = f.searchTemplate()

            positionNumber = result[0]
            accuracyScore = result[1]

            if (positionNumber == -1):
                print('No match found!')
                queue.put('Sorry No match found')
                beepReject()
                # exit(0)
            else:
                # cross-check the hash of
                f.loadTemplate(positionNumber, 0x01)
                ## Downloads the characteristics of template loaded in charbuffer 1
                characterics = str(f.downloadCharacteristics(0x01)).encode('utf-8')
                hash = hashlib.sha256(characterics).hexdigest();

                find_Student_And_Record_Attendance(hash, queue);
                # queue.put('Attendance Taken...')
                # print('Found template at position #' + str(positionNumber))
                # print('The accuracy score is: ' + str(accuracyScore))
                tries = 4;



        except Exception as e:
            pass
            # print('Operation failed!')
            # print('Exception message-testing: ' + str(e))
            # exit(1)
        time.sleep(2);

    # time.sleep(2);
def enrollFingerprintAndSend(queue):
    try:
        f = PyFingerprint('/dev/ttyUSB1', 57600, 0xFFFFFFFF, 0x00000000)

        if (f.verifyPassword() == False):
            raise ValueError('The given fingerprint sensor password is wrong!')

    except Exception as e:
        print('The fingerprint sensor could not be initialized!')
        print('Exception message: ' + str(e))
        exit(1)

    ## Gets some sensor information
    print('Currently used templates: ' + str(f.getTemplateCount()) + '/' + str(f.getStorageCapacity()))

    ## Tries to enroll new finger
    try:
        print('Waiting for finger...')
        queue.put('Waiting for finger...')

        ## Wait that finger is read
        while (f.readImage() == False):
            pass

        ## Converts read image to characteristics and stores it in charbuffer 1
        f.convertImage(0x01)

        ## Checks if finger is already enrolled
        result = f.searchTemplate()
        positionNumber = result[0]

        if (positionNumber >= 0):
            print('Template already exists at position #' + str(positionNumber))
            exit(0)

        print('Remove finger...')
        queue.put('Remove finger....')
        time.sleep(2)

        print('Waiting for same finger again...')
        queue.put('Waiting for same finger again...')

        ## Wait that finger is read again
        while (f.readImage() == False):
            pass

        ## Converts read image to characteristics and stores it in charbuffer 2
        f.convertImage(0x02)

        ## Compares the charbuffers
        if (f.compareCharacteristics() == 0):
            queue.put('Fingers do not match')
            #raise Exception('Fingers do not match')
        else:
            f.createTemplate()
            fingerData = f.downloadCharacteristics()
            #I NEED TO LOAD THE FINGERPRINT AND SEND IT THROUGHT THE STREAM
            con.send(bytes(str(fingerData) + "\n", "UTF-8"))
            queue.put('FingerPrint Data Sent Successfully')
            beep()
            ledGreen()
        time.sleep(1)

    except Exception as e:
        print(e)
def find_Student_And_Record_Attendance(hash, queue):
    with open('newfinger.json', 'r') as f:
        for line in f:
            if line.strip():
                file = json.loads(line)

    for f in file["fingerprints"]:
        if (f[2] == hash):
            load_Data_Into_Database_And_Send(f[0], file["lecture_details"][2], file["lecture_details"][1], queue);
            break;
        else:
            print("hash not found")
def load_Data_Into_Database_And_Send(userId, courseId, weekNumber, queue):
    attendance_id = 'W' + weekNumber + '' + userId + '' + courseId;
    sql = "RTATR---" + "INSERT INTO LECTURE_ATTENDANCE (attendance_id, course_id, student_id, week_number, entry_time) VALUES ('" + attendance_id + "','" + courseId + "', '" + userId + "', '" + weekNumber + "', datetime())";
    # print(sql)
    conn = sqlite3.connect('attendance.db');
    c = conn.cursor();
    if attendanceAlreadyTaken(c, attendance_id) == False:
        with conn:
            c.execute(
                "INSERT INTO LECTURE_ATTENDANCE  VALUES ('" + attendance_id + "','" + courseId + "', '" + userId + "', '" + weekNumber + "', datetime(), 'Not-Sent')");
            try:
                con.send(bytes(sql + "\n", "UTF-8"))
                c.execute("UPDATE LECTURE_ATTENDANCE SET status='Sent' WHERE attendance_id = '" + attendance_id + "'")
            except Exception:
                pass

            queue.put('Attendance Taken\nRemove Finger')
            beep()
            ledGreen()
            print('Attendance Taken! Remove Finger')
        conn.close()
    else:
        queue.put('Attendance Already Taken! . Remove Finger')
        beepReject()
        ledYellow()
def attendanceAlreadyTaken(c, attendance_id):
    taken = False;
    val = c.execute("SELECT * FROM LECTURE_ATTENDANCE WHERE attendance_id='" + attendance_id + "'")
    if len(val.fetchall()):
        taken = True
    else:
        taken = False

    return taken
def beepReject():
    GPIO.output(16, GPIO.HIGH)
    time.sleep(0.400)
    GPIO.output(16, GPIO.LOW)
def beep():
    GPIO.output(16, GPIO.HIGH)
    time.sleep(0.300)
    GPIO.output(16, GPIO.LOW)
    time.sleep(0.200)
    GPIO.output(16, GPIO.HIGH)
    time.sleep(0.300)
    GPIO.output(16, GPIO.LOW)
def ledYellow():
    GPIO.output(20, GPIO.HIGH)
    time.sleep(0.400)
    GPIO.output(20, GPIO.LOW)
def ledGreen():
    GPIO.output(21, GPIO.HIGH)
    time.sleep(0.400)
    GPIO.output(21, GPIO.LOW)
def deleteSentData():
    delcon = sqlite3.connect("attendance.db");
    dlt = delcon.cursor();
    dlt.execute("DELETE FROM LECTURE_ATTENDANCE WHERE status='Sent'")
    delcon.commit()
    delcon.close()
def queryRFID(queue):
    queue.put("Place RFID close to Reciever")
    try:
        ser = serial.Serial('/dev/ttyUSB0', 115200, timeout=3)
        while True:
            # queue.put("Place RFID close to Reciever")
            tag = ser.read(35).decode()
            print(tag)
            if tag != 0:
                queue.put("Now Place Fingerpint..")
            break;
        queue.put("Now Place Fingerprint")
    except Exception as e:
        pass
def limitateText(str):
    if len(str) > 42:
        str += "...and more"
    elif len(str) < 33:
        str += "\n"

    return str
def checkConnection():
    global connection_exists
    try:
        con.send(bytes())
        connection_exists = True
        return True
    except Exception as e:
        connection_exists = False
        return False;
def generateQueriesFromLog():
    sqlconn = sqlite3.connect("attendance.db")
    val = sqlconn.execute("SELECT * FROM LECTURE_ATTENDANCE WHERE status='Not-Sent'")
    sqlconn.commit()
    return generateSql(val.fetchall())
def generateSql(lst):
    sql = []
    for data in lst:
        text = "RECVATR---INSERT INTO LECTURE_ATTENDANCE (attendance_id, course_id, student_id, week_number, entry_time) VALUES ('" + str(
            data[0]) + "','" + str(data[1]) + "', '" + str(data[2]) + "', '" + str(data[3]) + "','" + str(
            data[4]) + "')";
        sql.append(text)
    return sql
def connectToClient():
    global connection_exists
    while not connection_exists:
        s.listen()
        try:
            global con, addr
            con, addr = s.accept()
            connection_exists = True
        except Exception as e:
            pass
def updateAttendanceRecord(atid):
    sqlconn = sqlite3.connect("attendance.db")
    sqlconn.execute("UPDATE LECTURE_ATTENDANCE SET status='Sent' WHERE attendance_id = '" + atid + "'")
    sqlconn.commit()


s = socket.socket()
wi_fi_host = "192.168.4.1"  # wifi-ip-address-of-raspberry-pi
port = 6789
s.bind((wi_fi_host, port))
root = tk.Tk()
#root.geometry("800x600")
# root.attributes('-fullscreen', True);
root.configure(background='#630063');
client = initPage(root)
connectionthread = threading.Thread(target=connectToClient)
connectionthread.start()
root.mainloop()
