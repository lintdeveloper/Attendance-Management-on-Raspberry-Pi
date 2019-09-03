import tkinter as tk

class initPage:
    def __int__(self, master):
        self.master = master
        self.firstFrame = tk.Frame(
            root, width=600, height=50, bg='#8B008B')
        self.firstFrame.pack(fill='x')
        self.firstFrame.tkraise()
        self.headerLabel = tk.Label(self.firstFrame, fg='#eaeaea', bg='#8B008B',
                                    text='FUTMINNA | Attendance Management System', font=('Calibri', 15))
        self.headerLabel.pack()

        self.topFrame = tk.Frame(
            root, background='#630063', width=600, height=100)
        self.topFrame.pack(fill='x')

        self.informationLabel = tk.Label(self.topFrame, fg='#ffffff', bg='#630063', text="Welcome, Please Choose Mode:",
                                         font=('Arial', 40))
        self.informationLabel.pack(pady=50, padx=10)
        self.startAttendanceSystemButton = tk.Button(self.topFrame, text="Attendance System", width="15",
                                                     font=('Arial', 50), bg='yellow',
                                                     padx=20, command=self.homePage)
        self.startAttendanceSystemButton.pack(pady=10)
        self.sendAttendanceSystemLog = tk.Button(self.topFrame, text="Attendance Log", width="15", font=('Arial', 50),
                                                 bg='#8d6e63', padx=20, command=self.attendanceLogPage)
        self.sendAttendanceSystemLog.pack(pady=10)
        self.exitFrame = tk.Frame(root)
        self.exitButton = tk.Button(self.exitFrame, text="Power Off", fg="#ffffff", bg="#B71C1C",
                                    command=self.exitThenShutDown,
                                    font=('Arial', 40))
        self.exitButton.pack(side='left')
        self.exitFrame.pack(side='bottom')

root = tk.Tk();