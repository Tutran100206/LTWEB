$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$runtimePath = Join-Path $projectRoot '.runtime\tomcat'
$pidFile = Join-Path $runtimePath 'tomcat.pid'
if (-not (Test-Path -LiteralPath $pidFile)) { Write-Output 'No local Tomcat PID file.'; exit }
$tomcatPid = [int](Get-Content -LiteralPath $pidFile)
$process = Get-CimInstance Win32_Process -Filter "ProcessId=$tomcatPid"
if ($process -and $process.Name -eq 'java.exe' -and $process.CommandLine.Contains("-Dcatalina.base=`"$runtimePath`"")) {
    Stop-Process -Id $tomcatPid
    Write-Output "Stopped project Tomcat PID $tomcatPid."
} elseif ($process) { throw 'PID belongs to a different process; refusing to stop it.' }
Remove-Item -LiteralPath $pidFile
