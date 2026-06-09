import subprocess
try:
    result = subprocess.run(['mvn.cmd', 'clean', 'compile'], capture_output=True, text=True)
    with open('maven_out.txt', 'w') as f:
        f.write(result.stdout + '\n' + result.stderr)
except Exception as e:
    print(str(e))
