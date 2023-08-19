for /l %%x in (1, 1, %8) do (
echo %%x
echo off
java -jar RunBandits.jar %1 %2 %3 %4 %5 %6 %7
echo on
)