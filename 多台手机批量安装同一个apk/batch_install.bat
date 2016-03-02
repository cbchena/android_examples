@echo off
for /f "skip=1 tokens=1 delims=	" %%i in ('adb devices') do adb -s %%i install D:\IdeaProjects_androidCtrl\myGIFImage\out\production\myGIFImage\myGIFImage.apk
pause