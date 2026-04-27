@echo off

REM Start backend service
echo Starting backend service...
start "Backend Server" cmd /c "cd backend && mvn spring-boot:run"

REM Wait for backend service to start
echo Waiting for backend service to start...
timeout /t 3 /nobreak >nul

REM Start frontend service
echo Starting frontend service...
start "Frontend Server" cmd /c "cd frontend && npm run dev"

REM Wait for frontend service to start
echo Waiting for frontend service to start...
timeout /t 3 /nobreak >nul

REM Open frontend page in Chrome
echo Opening frontend page...
start chrome http://127.0.0.1:5173

echo Start completed!
