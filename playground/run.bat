echo F|xcopy /y /s /f /q "target/HyperBridge.jar" "playground/HyperBridge.jar"
for /f "tokens=5" %%a in ('netstat -aon ^| find ":80" ^| find "LISTENING"') do taskkill /f /pid %%a
for /f "tokens=5" %%a in ('netstat -aon ^| find ":443" ^| find "LISTENING"') do taskkill /f /pid %%a
cd playground
java -Xmx1g -Xms1m -jar HyperBridge.jar eclipse