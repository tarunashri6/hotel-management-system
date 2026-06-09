import subprocess

try:
    proc = subprocess.Popen(['mvn.cmd', '-X', 'compile'], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    out, _ = proc.communicate(timeout=15)
    with open('maven_err_capture.txt', 'w') as f:
        f.write(out)
except subprocess.TimeoutExpired as e:
    proc.kill()
    out, _ = proc.communicate()
    with open('maven_err_capture.txt', 'w') as f:
        f.write("TIMEOUT\n" + out.decode('utf-8', errors='ignore'))
except Exception as e:
    with open('maven_err_capture.txt', 'w') as f:
        f.write(str(e))
