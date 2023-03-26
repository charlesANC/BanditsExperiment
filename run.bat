for /l %%x in (1, 1, %8) do (
echo %%x
"c:\Program Files\Java\jdk-11.0.2\bin\java.exe" -jar RunBandits.jar %1 %2 %3 %4 %5 %6 %7
)