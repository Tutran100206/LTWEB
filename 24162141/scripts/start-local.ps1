param(
    [string]$TomcatHome = 'C:\Users\Tu\Downloads\apache-tomcat-9.0.121',
    [string]$JavaHome = 'C:\Program Files\Java\jdk-22',
    [int]$Port = 8086,
    [string]$ConfigFile = ''
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
if (-not $ConfigFile) { $ConfigFile = Join-Path $projectRoot 'config\application.local.properties' }
if (-not (Test-Path -LiteralPath $ConfigFile)) { throw "Missing configuration: $ConfigFile. See README.md." }
$runtimePath = Join-Path $projectRoot '.runtime\tomcat'
if (Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue) { throw "Port $Port is already in use." }
foreach ($folder in @('conf','logs','temp','webapps','work')) { New-Item -ItemType Directory -Force (Join-Path $runtimePath $folder) | Out-Null }
Copy-Item -Path (Join-Path $TomcatHome 'conf\*') -Destination (Join-Path $runtimePath 'conf') -Force
$server = @"
<?xml version="1.0" encoding="UTF-8"?>
<Server port="-1"><Service name="Catalina"><Connector port="$Port" protocol="HTTP/1.1" connectionTimeout="20000" URIEncoding="UTF-8"/><Engine name="Catalina" defaultHost="localhost"><Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="false"/></Engine></Service></Server>
"@
[IO.File]::WriteAllText((Join-Path $runtimePath 'conf\server.xml'), $server)
# Use an external context pointing to the current WAR; no copy of existing user apps.
$contextPath = Join-Path $runtimePath 'conf\Catalina\localhost'
New-Item -ItemType Directory -Force $contextPath | Out-Null
$warPath = Join-Path $projectRoot 'target\de06_24162141.war'
if (-not (Test-Path -LiteralPath $warPath)) { throw 'Run mvn clean package first.' }
[IO.File]::WriteAllText((Join-Path $contextPath 'de06_24162141.xml'), "<Context docBase=`"$warPath`" />")
$javaPath = Join-Path $JavaHome 'bin\java.exe'
$argsList = @(
    "-Dcatalina.home=`"$TomcatHome`"", "-Dcatalina.base=`"$runtimePath`"",
    "-Djava.io.tmpdir=`"$(Join-Path $runtimePath 'temp')`"", '-Dfile.encoding=UTF-8',
    "-Dapp.config=`"$ConfigFile`"", "-Djava.library.path=`"$(Join-Path $projectRoot '.runtime\native')`"",
    '-cp', "`"$(Join-Path $TomcatHome 'bin\bootstrap.jar');$(Join-Path $TomcatHome 'bin\tomcat-juli.jar')`"",
    'org.apache.catalina.startup.Bootstrap', 'start'
)
$process = Start-Process -FilePath $javaPath -ArgumentList $argsList -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $runtimePath 'logs\stdout.log') -RedirectStandardError (Join-Path $runtimePath 'logs\stderr.log')
$process.Id | Set-Content (Join-Path $runtimePath 'tomcat.pid')
Write-Output "Tomcat PID $($process.Id): http://localhost:$Port/de06_24162141/"
