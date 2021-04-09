#!/usr/bin/python
import subprocess
import time


def ssj():
    times = 21
    totalUsage = []
    for i in range(times):
        _cpu = []
        _mem = []
        _runTime = []
        start = time.time()
        for j in range(10):
            ps_pid = subprocess.Popen(["java", "-jar", "umontreal-simul-ssj-bd120eb-3.3.1.jar", str(25000*i)])
            itTime = time.time()
            while ps_pid.poll() is None:
                time.sleep(0.2)
                usage = str(subprocess.run(["ps", "-o", "pid,%cpu,%mem", "-fp", str(ps_pid.pid)], capture_output=True)).split("\\n")[1]
                cpu = usage.split(" ")[2]
                if cpu != "":
                    cpu_u = float(cpu) / 8
                    mem_u = str(usage).split(" ")[4]
                    _cpu.append(cpu_u)
                    _mem.append(mem_u)
            while ps_pid.poll() is None:
                time.sleep(0.01)
            _runTime.append(time.time() - itTime)
        end = time.time()
        totalUsage.append("Customers: " + str(i * 25000) + " : " + str((end - start) / 10))
        totalUsage.append(_cpu)
        totalUsage.append(_mem)
        totalUsage.append(_runTime)
        print("SSJ: " + str((end - start) / 10) + ";" + str(i * 25000))
    with open('SSJ.txt', 'w') as f:
        for item in totalUsage:
            f.write("%s\n" % item)


def simPy():
    times = 21
    totalUsage = []
    for i in range(times):
        _cpu = []
        _mem = []
        _runTime = []
        start = time.time()
        for j in range(10):
            ps_pid = subprocess.Popen(["python", "StreetFood.py" , str(25000*i)])
            itTime = time.time()
            while ps_pid.poll() is None:
                time.sleep(0.2)
                usage = str(subprocess.run(["ps", "-o", "pid,%cpu,%mem", "-fp", str(ps_pid.pid)], capture_output=True)).split("\\n")[1]
                cpu = usage.split(" ")[2]
                if cpu != "":
                    cpu_u = float(cpu)/8
                    mem_u = str(usage).split(" ")[4]
                    _cpu.append(cpu_u)
                    _mem.append(mem_u)
            while ps_pid.poll() is None:
                time.sleep(0.01)
            _runTime.append(time.time() - itTime)
        end = time.time()
        totalUsage.append("Customers: " + str(i * 25000) +" : "+ str((end - start) / 10))
        totalUsage.append(_cpu)
        totalUsage.append(_mem)
        totalUsage.append(_runTime)
        print("SimPy: " + str((end - start) / 10) + ";" + str(i * 25000))
    with open('SimPyResults.txt', 'w') as f:
        for item in totalUsage:
            f.write("%s\n" % item)


def jaamSim():
    times = 21
    totalUsage = []
    for i in range(times):
        _cpu = []
        _mem = []
        _runTime = []
        start = time.time()
        for j in range(10):
            ps_pid = subprocess.Popen(["java", "-jar" ,"JaamSim2021-01.jar" , str(i)+"test.cfg" ,  "-m" , "-h"])
            itTime = time.time()
            while ps_pid.poll() is None:
                time.sleep(0.2)
                usage = str(subprocess.run(["ps", "-o", "pid,%cpu,%mem", "-fp", str(ps_pid.pid)], capture_output=True)).split("\\n")[1]
                cpu = usage.split(" ")[2]
                if cpu != "":
                    cpu_u = float(cpu)/8
                    mem_u = str(usage).split(" ")[4]
                    _cpu.append(str(cpu_u))
                    _mem.append(mem_u)
            while ps_pid.poll() is None:
                time.sleep(0.01)
            _runTime.append(time.time() - itTime)
        end = time.time()
        totalUsage.append("Customers: " + str(i * 25000) +" : "+ str((end - start) / 10))
        totalUsage.append(_cpu)
        totalUsage.append(_mem)
        totalUsage.append(_runTime)
        print("JaamSim: " + str((end - start) / 10) + ";" + str(i * 25000))
    with open('JaamSimResults.txt', 'w') as f:
        for item in totalUsage:
            f.write("%s\n" % item)


def javaSim():
    times = 21
    totalUsage = []
    for i in range(times):
        _cpu = []
        _mem = []
        _runTime = []
        start = time.time()
        for j in range(10):
            ps_pid = subprocess.Popen(["java", "-jar", "StreetFoodModel.jar", str(1+(25000*i))])
            itTime = time.time()
            while ps_pid.poll() is None:
                time.sleep(0.2)
                usage = str(subprocess.run(["ps", "-o", "pid,%cpu,%mem", "-fp", str(ps_pid.pid)], capture_output=True)).split("\\n")[1]
                cpu = usage.split(" ")[2]
                if cpu != "":
                    cpu_u = float(cpu)/8
                    mem_u = str(usage).split(" ")[4]
                    _cpu.append(cpu_u)
                    _mem.append(mem_u)
            while ps_pid.poll() is None:
                time.sleep(0.01)
            _runTime.append(time.time() - itTime)
        end = time.time()
        totalUsage.append("Customers: " + str(i * 25000) +" : "+ str((end - start) / 10))
        totalUsage.append(_cpu)
        totalUsage.append(_mem)
        totalUsage.append(_runTime)
        print("JavaSim: " + str((end - start) / 10) + ";" + str(i * 25000))
    with open('JavaSimResults.txt', 'w') as f:
        for item in totalUsage:
            f.write("%s\n" % item)


def simJulia():
    times = 21
    totalUsage = []
    for i in range(times):
        _cpu = []
        _mem = []
        _runTime = []
        start = time.time()
        for j in range(5):
            ps_pid = subprocess.Popen(["julia", "StreetFood.jl", str(25000*i)])
            itTime = time.time()
            while ps_pid.poll() is None:
                time.sleep(0.2)
                usage = str(subprocess.run(["ps", "-o", "pid,%cpu,%mem", "-fp", str(ps_pid.pid)], capture_output=True)).split("\\n")[1]
                cpu = usage.split(" ")[2]
                if cpu != "":
                    cpu_u = float(cpu)/8
                    mem_u = str(usage).split(" ")[4]
                    _cpu.append(cpu_u)
                    _mem.append(mem_u)
            while ps_pid.poll() is None:
                time.sleep(0.01)
            _runTime.append(time.time() - itTime)
        end = time.time()
        totalUsage.append("Customers: " + str(i * 25000) + " : " + str((end - start) / 5))
        totalUsage.append(_cpu)
        totalUsage.append(_mem)
        totalUsage.append(_runTime)
        print("SimJulia: " + str((end - start) / 10) + ";" + str(i * 25000))
        with open('SimJuliaResults.txt', 'w') as f:
            for item in totalUsage:
                f.write("%s\n" % item)

#ssj()

jaamSim()

#javaSim()

#simPy()

#simJulia()
