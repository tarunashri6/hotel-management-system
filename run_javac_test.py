import os
import subprocess
import glob

src_dir = r"c:\Users\tarun\OneDrive\Documents\Osdlproject\hotel-management-system\src\main\java"
java_files = glob.glob(src_dir + "/**/*.java", recursive=True)

# JavaFX jar locations (assuming standard maven local repo or downloaded, this might fail if not in path)
# But let's just use maven to get the classpath.
try:
    # mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
    subprocess.run(['mvn.cmd', 'dependency:build-classpath', '-q', '-Dmdep.outputFile=cp.txt'], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    with open('cp.txt', 'r') as f:
        cp = f.read().strip()
    
    cmd = ['javac', '-cp', cp] + java_files
    res = subprocess.run(cmd, capture_output=True, text=True)
    with open('javac_err.txt', 'w') as f:
        f.write(res.stdout + "\n" + res.stderr)
except Exception as e:
    with open('javac_err.txt', 'w') as f:
        f.write(str(e))
